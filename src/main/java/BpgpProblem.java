import BP.CobpRunner;
import ec.Subpopulation;
import func.StringData;

import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;

import il.ac.bgu.cs.bp.bpjs.model.BEvent;

import org.mozilla.javascript.NativeObject;

import java.io.File;
import java.io.IOException;


public class BpgpProblem extends GPProblem implements SimpleProblemForm {
    static final boolean debug = false;
    static final int numOfBoards = 70;      //TODO use split point
    static final int slurmRerunTries = 5000;

    // used for slurm call when splitting evaluations
    // first arg is file.js ; second arg is boardNum
    // it prints the calculated fitness
    public static void main(String[] args) throws IOException {
        BpgpProblem problem = new BpgpProblem();
        System.out.println(problem.bpRun(new File(args[0]), Integer.parseInt(args[1])));
        // without this 'mvn exec' takes ages to finish, and issues warnings
        System.exit(0);
    }

    protected double bpRun(File indCodeFile, int boardNum) {
        CobpRunner runner = new CobpRunner("wumpus/boards/board"+boardNum+".js", "wumpus/dal.js", "wumpus/bl.js", indCodeFile.getName());

//        File dest = new File(indCodeFile+".done");
//        var success = indCodeFile.renameTo(dest);

        return getRunFitness(runner.listener.runResult, runner.listener.numOfEvents);
    }

    // return a Koza fitness: 0 is best, infinity is worst
    // Wumpus score: -inf is worse, 1000 is best
    private double getRunFitness(BEvent runResult, int numOfEvents) {
        if (runResult != null && runResult.getName().equals("Game over")) {
            double score = (double) ((NativeObject) runResult.getData()).get("score");
            double numOfVisitedCells = (double) ((NativeObject) runResult.getData()).get("numOfVisitedCells");
            double boardSize = (double) ((NativeObject) runResult.getData()).get("boardSize");
//            int numOfBts = BpgpUtils.countInString(generatedCode, "bthread");

//            double scoreNormalized = BpgpUtils.sigmoid((1000 - score) / 200.0);
            double scoreNormalized = (1000 - score) / 2000.0;
//            double pressuredScore = BpgpUtils.pressure(scoreNormalized);
//            double numOfBtsNormalized = BpgpUtils.sigmoid(numOfBts / 3.0);
//            double numOfEventsNormalized = BpgpUtils.sigmoid(numOfEvents / 20.0);
            double boardMissingCoverageNormalized = 1 - (numOfVisitedCells / boardSize);

            double result = score > 0 ?
                    scoreNormalized :
                    0.8 * scoreNormalized  + 0.2 * boardMissingCoverageNormalized;
            System.out.println("getRunFitness. score: " + score +
//                    ", numOfBts: " + numOfBts +
                    ", numOfEvents: " + numOfEvents +
                    ", numOfVisitedCells: " + numOfVisitedCells +
                    ", scoreNormalized: " + scoreNormalized +
//                    ", pressuredScore: " + pressuredScore +
//                    ", numOfBtsNormalized: " + numOfBtsNormalized + ", numOfEventsNormalized: " + numOfEventsNormalized +
                    ", boardMissingCoverageNormalized: " + boardMissingCoverageNormalized +
                    ". result: " + result);

            return result;
        } else { //TODO
            System.out.println("getRunFitness. not finished! numOfEvents: " + numOfEvents + ". result: " + (2000 - numOfEvents));
            System.exit(1);
            return 2000 - numOfEvents;
        }
    }

    public void evaluate(final EvolutionState state,
                         final Individual ind,
                         final int subpopulation,
                         final int threadnum)
    {
        if (ind.evaluated) return;

		//TODO remove this...
//        var p = state.population.subpops.get(0);
//        if (p.individuals.size() != p.initialSize * BpgpUtils.getEcjIntParam(state, "eval.num-tests")) {
//            System.out.println("ZZZ someone got lost!!!");
//            System.exit(1);
//        }

        StringData input = (StringData)(this.input);

        if (!(ind instanceof GPIndividual))
            state.output.fatal("Whoa!  It's not a GPIndividual!!!",null);

        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        var indCode = input.str;
        System.out.println("==============");
        System.out.println("Generation: " + state.generation);
        System.out.println("---------\n" + indCode + "---------");

//        String niceTree = treeToString(((GPIndividual) ind).trees[0], state);
//        System.out.println("grammar: " + niceTree + "---------");

        indCode = "bp.log.setLevel(\"Warn\");\n" + indCode;
        File indCodeFile = BpgpUtils.writeToTempFile(indCode);

        BpgpIndInfo indInfo = new BpgpIndInfo((GPIndividual)ind, indCode);
        BpgpSpecies species = (BpgpSpecies) state.population.subpops.get(subpopulation).species;
        species.indInfos.add(indInfo);

        double totalRunResults = runAndCalcFitnesses(indCodeFile);

        if (debug)
            System.out.println("totalRunResults: " + totalRunResults);
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, totalRunResults / numOfBoards);
        f.printFitnessForHumans(state, 0);
        ind.evaluated = true;
    }

    private double runAndCalcFitnesses(File indCodeFile) {
        double totalRunResults = 0;

        if (Slurm.isSlurmInstalled()) {
            Slurm[] slurms = new Slurm[numOfBoards];
            for (int i = 1; i <= numOfBoards; i++) {
                slurms[i-1] = new Slurm(String.format(
                        "mvn exec:java@bpRun -Dexec.args=\'%s %d\'",
                        indCodeFile, i));
            }
            for (int k = 0; k <= slurmRerunTries - 1; k++) {
                //TODO break if all OK
                for (int i = 0; i <= numOfBoards - 1; i++) {
                    if (slurms[i].getStatus() == Slurm.Status.FAILED)
                        slurms[i].runJob();
                }
            }
            for (int i = 0; i <= numOfBoards - 1; i++) {
                if (slurms[i].getStatus() == Slurm.Status.FAILED) {
                    System.out.println("ERROR: job failed, exiting: " + slurms[i]);
                    throw new RuntimeException();
                }
            }
            for (int i = 0; i <= numOfBoards - 1; i++) {
                // System.out.println("waitFinished. i: " + i + ", job: " + slurms[i].jobId);
                slurms[i].waitFinished();
            }
            for (int i = 0; i <= numOfBoards - 1; i++) {
                if (slurms[i].getStatus() != Slurm.Status.COMPLETED) {
                    System.out.println("ERROR: job not completed, exiting: " + slurms[i]);
                    throw new RuntimeException();
                }
                String[] output = slurms[i].getOutput().split("\n");
                new File(slurms[i].getOutputFileName()).delete();
                double runResult = Double.parseDouble(output[output.length-1]);
                totalRunResults += runResult;
            }
        } else {
            // no Slurm
            for (int i = 1; i <= numOfBoards; i++) {
                double runResult = bpRun(indCodeFile, i);
                if (debug)
                    System.out.println("runResult:" + runResult);
                totalRunResults += runResult;
            }
        }

        indCodeFile.delete();
        return totalRunResults;
    }


//    private String treeToString(GPTree tree, EvolutionState state) {
//        StringWriter out    = new StringWriter();
//        PrintWriter writer = new PrintWriter(out);
//
//        tree.printTree(state, writer);
//        writer.flush();
//        return out.toString();
//    }

    public void describe(
        final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum,
        final int log)
    {
        StringData input = (StringData)(this.input);
        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        state.output.println("\n\nBest Individual's code\n======================", log);
        state.output.println(((StringData) input).str, log);
    }


}
