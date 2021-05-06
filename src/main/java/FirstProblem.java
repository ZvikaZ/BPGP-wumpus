import ec.*;
import ec.gp.*;
import ec.simple.*;
import ec.gp.koza.KozaFitness;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.StringBProgram;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;

import java.io.*;
import java.net.URL;
import org.apache.commons.io.IOUtils;

import func.StringData;

public class FirstProblem extends GPProblem implements SimpleProblemForm {
    static final String bpRunLog = "bpRun.log";
    static final int numOfRandomRuns = 6;       //TODO increase
    static final boolean debug = false;

    private int bpRun(String generatedCode) {
        // This will load the program file from <Project>/src/main/resources/
        // TODO take file name from user (param file, or cli flag)
        String code = resourceToString("FourInARow.js");

        // TODO redirect these to some file, waiting for https://github.com/bThink-BGU/BPjs/issues/163
        code = "bp.log.setLevel(\"Warn\");\n" + code;

        code += "\n\n" + generatedCode;

        final BProgram bprog = new StringBProgram(code);

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

    //TODO be flexible - currently it wants Yellow to win
    // Koza fitness: 0 is best, infinity is worst
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

        StringData input = (StringData)(this.input);

        if (!(ind instanceof GPIndividual))
            state.output.fatal("Whoa!  It's not a GPIndividual!!!",null);

        ((GPIndividual)ind).trees[0].child.eval(state, threadnum, input, stack, (GPIndividual)ind, this);
        System.out.println("==============");
        System.out.println("Generation: " + state.generation);
        System.out.println("---------\n" + input.str + "---------");

        String niceTree = treeToString(((GPIndividual) ind).trees[0], state);
        System.out.println("grammar: " + niceTree + "---------");

        int totalRunResults = 0;
        for (int i=0; i < numOfRandomRuns; i++) {
            int runResult = bpRun(input.str);
            if (debug)
                System.out.println("runResult:" + runResult);
            totalRunResults += runResult;
        }

        System.out.println("totalRunResults: " + totalRunResults);
        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, totalRunResults);
        f.printFitnessForHumans(state, 0);
        ind.evaluated = true;
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
