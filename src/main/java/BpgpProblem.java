import ec.EvolutionState;
import ec.Individual;
import ec.gp.GPIndividual;
import ec.gp.GPProblem;
import ec.gp.GPTree;
import ec.gp.ge.GEIndividual;
import ec.gp.ge.GESpecies;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleProblemForm;
import func.StringData;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.StringBProgram;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.PrioritizedBSyncEventSelectionStrategy;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;


public class BpgpProblem extends GPProblem implements SimpleProblemForm {
    static final String bpRunLog = "bpRun.log";
    static final int numOfRandomRuns = 40;
    static final boolean debug = false;
    enum Rival {
        RANDOM,
        SELF,
        OTHERS
    }
    static Rival rival = Rival.OTHERS;

    private int bpRun(String generatedCode) {
        // This will load the program file from <Project>/src/main/resources/
        // TODO take file name from user (param file, or cli flag)
        String code = resourceToString("FourInARow.js");

        // TODO redirect these to some file, waiting for https://github.com/bThink-BGU/BPjs/issues/163
        code = "bp.log.setLevel(\"Warn\");\n" + code;

        code += "\n\n" + generatedCode;

        final BProgram bprog = new StringBProgram(code);
        
        var prio = new PrioritizedBSyncEventSelectionStrategy();
        prio.setDefaultPriority(0);
        bprog.setEventSelectionStrategy(prio);

        BProgramRunner rnr = new BProgramRunner(bprog);
        BpgpListener listener = null;

        try {
            // TODO keep log from previous runs (?)
            PrintStream ps = new PrintStream(bpRunLog);
            listener = rnr.addListener( new BpgpListener(ps) );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // go!
        rnr.run();

        return getRunFitness(listener.runResult);
    }

    // Koza fitness: 0 is best, infinity is worst
    // TODO be flexible - currently it wants Yellow to win
    private int getRunFitness(BEvent runResult) {
        if (debug)
            System.out.println("result event: " + runResult);
        if (runResult.name.contains("Win") && runResult.name.contains("Yellow"))
            return 0;
        else if (runResult.name.contains("Win") && runResult.name.contains("Red"))
            return 2;
        if (runResult.name.contains("Draw"))
            return 1;
        else
            throw new RuntimeException();
    }

    private String resourceToString(String resourceName) {
        URL url = this.getClass().getResource(resourceName);
        String result = null;
        try {
            result = IOUtils.toString(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
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

        // make sure that we start empty
        input.str = null;

        //TODO move this to better place?
        input.playerColor = "Yellow";

        if (!(ind instanceof GPIndividual))
            state.output.fatal("Whoa!  It's not a GPIndividual!!!",null);

        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        var indCode = input.str;
        System.out.println("==============");
        System.out.println("Generation: " + state.generation);
        System.out.println("---------\n" + indCode + "---------");

        String niceTree = treeToString(((GPIndividual) ind).trees[0], state);
        System.out.println("grammar: " + niceTree + "---------");

        int totalRunResults = 0;
        for (int i=0; i < numOfRandomRuns; i++) {
            switch (rival) {
                case RANDOM:
                    input.str = "";
                    break;
                case SELF:
                    input.playerColor = swapColor(input.playerColor);
                    input.str = null;
                    ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
                    input.playerColor = swapColor(input.playerColor);
                    break;
                case OTHERS:
                    var inds = state.population.subpops.get(subpopulation).individuals;
                    GEIndividual rivalInd = (GEIndividual)inds.get(state.random[threadnum].nextInt(inds.size()));
                    input.playerColor = swapColor(input.playerColor);
                    input.str = null;
                    GESpecies species = (GESpecies)(rivalInd.species);
                    GPIndividual gpRivalInd = species.map(state, rivalInd, threadnum, null);
                    gpRivalInd.trees[0].child.eval(state, threadnum, input, stack, gpRivalInd, this);
                    input.playerColor = swapColor(input.playerColor);
                    break;
            }
            if (input.str == null)
                throw new RuntimeException();
            if (debug)
                System.out.println("---------PLAYING AGAINST\n" + input.str + "---------");
            int runResult = bpRun(indCode + "\n" + input.str);
            if (debug)
                System.out.println("runResult:" + runResult);
            totalRunResults += runResult;
        }

        if (debug)
            System.out.println("totalRunResults: " + totalRunResults);
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, totalRunResults);
        f.printFitnessForHumans(state, 0);
        ind.evaluated = true;
    }

    private String swapColor(String playerColor) {
        if (playerColor.equals("Yellow"))
            return "Red";
        else if (playerColor.equals("Red"))
            return "Yellow";
        else throw new RuntimeException();
    }

    private String treeToString(GPTree tree, EvolutionState state) {
        StringWriter out    = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        tree.printTree(state, writer);
        writer.flush();
        return out.toString();
    }

    public void describe(
        final EvolutionState state,
        final Individual ind,
        final int subpopulation,
        final int threadnum,
        final int log)
    {
        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        state.output.println("\n\nBest Individual's code\n======================", log);
        state.output.println(((StringData) input).str, log);
    }


}
