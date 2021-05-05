

import org.apache.commons.io.IOUtils;

import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.StringBProgram;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import func.StringData;


public class FirstProblem extends GPProblem implements SimpleProblemForm {
    private int bp_run(String generatedCode) {
        // This will load the program file from <Project>/src/main/resources/
        // TODO take file name from user (param file, or cli flag)
        String code = resourceToString("FourInARow.js");
        code += "\n\n" + generatedCode;
        final BProgram bprog = new StringBProgram(code);

        BProgramRunner rnr = new BProgramRunner(bprog);

        // Print program events to the console
        // TODO don't print!
        rnr.addListener( new PrintBProgramRunnerListener() );

        // go!
        rnr.run();

        // just a place holder, we should return something smarter...
        return 1;
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
        System.out.println("problem evaluated: " + input.str + "\n.");

        String niceTree = treeToString(((GPIndividual) ind).trees[0], state);
        System.out.println("treeToString result: " + niceTree);

        int run_result = bp_run(input.str);   // TODO currently it's garbage value

        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, run_result);
        ind.evaluated = true;
        //TODO the previous problem used 'ind2'. does it matter?
    }

    private String treeToString(GPTree tree, EvolutionState state) {
        StringWriter out    = new StringWriter();
        PrintWriter writer = new PrintWriter(out);

        tree.printTree(state, writer);
        writer.flush();
        return out.toString();
    }
}
