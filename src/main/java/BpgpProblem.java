import BP.CobpRunner;
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


public class BpgpProblem extends GPProblem implements SimpleProblemForm {
    static final boolean debug = false;
    static final int numOfBoards = 70;      //TODO use split point

    protected double bpRun(String generatedCode, int boardNum) {
        generatedCode = "bp.log.setLevel(\"Warn\");\n" + generatedCode;   //TODO return
        File tempFile = BpgpUtils.writeToTempFile(generatedCode);
        CobpRunner runner = new CobpRunner("wumpus/boards/board"+boardNum+".js", "wumpus/dal.js", "wumpus/bl.js", tempFile.getName());

        File dest = new File(tempFile+".done");
        var success = tempFile.renameTo(dest);


        return getRunFitness(runner.listener.runResult, runner.listener.numOfEvents, generatedCode);
    }

    // return a Koza fitness: 0 is best, infinity is worst
    // Wumpus score: -inf is worse, 1000 is best
    private double getRunFitness(BEvent runResult, int numOfEvents, String generatedCode) {
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

        double totalRunResults = 0;

        for (int i=1; i <= numOfBoards; i++) {
            double runResult = bpRun(indCode, i);
            if (debug)
                System.out.println("runResult:" + runResult);
            totalRunResults += runResult;
        }

        if (debug)
            System.out.println("totalRunResults: " + totalRunResults);
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, totalRunResults / numOfBoards);
        f.printFitnessForHumans(state, 0);
        ind.evaluated = true;
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
