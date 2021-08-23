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


public class BpgpProblem extends GPProblem implements SimpleProblemForm {
    static final String bpRunLog = "bpRun.log";
    static final boolean debug = false;
    static final int numOfBoards = 6;       //TODO increase

    protected double bpRun(String generatedCode, int boardNum) {
        generatedCode = "bp.log.setLevel(\"Warn\");\n" + generatedCode;   //TODO return
        String tempFile = BpgpUtils.writeToTempFile(generatedCode);
        CobpRunner runner = new CobpRunner("wumpus/board"+boardNum+".js", "wumpus/dal.js", "wumpus/bl.js", tempFile);
        return getRunFitness(runner.runResult, runner.numOfEvents, generatedCode);
    }

    // return a Koza fitness: 0 is best, infinity is worst
    // Wumpus score: -inf is worse, 1000 is best
    private double getRunFitness(BEvent runResult, int numOfEvents, String generatedCode) {
        if (runResult != null && runResult.getName().equals("Game over")) {
            double score = (double) ((NativeObject) runResult.getData()).get("score");
            int numOfBts = BpgpUtils.countInString(generatedCode, "bthread");
            double result = (1000 - score) + (numOfBts * 4) + (numOfEvents / 10);
            System.out.println("getRunFitness. score: " + score + ", numOfBts: " + numOfBts + ", numOfEvents: " + numOfEvents + ". result: " + result);
//            if (score < -300) { //TODO
//                System.out.println("negative score!");
//                System.exit(1);
//            }
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
        var p = state.population.subpops.get(0);
        if (p.initialSize != p.individuals.size()) {
            System.out.println("ZZZ someone got lost!!!");
            System.exit(1);
        }

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
