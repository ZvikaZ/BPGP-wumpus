
/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


import ec.*;
import ec.gp.*;
import ec.gp.koza.*;
import ec.simple.*;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;

import java.io.PrintWriter;
import java.io.StringWriter;

import func.StringData;


public class FirstProblem extends GPProblem implements SimpleProblemForm {
    private int bp_run() {
        // This will load the program file from <Project>/src/main/resources/
        final BProgram bprog = new ResourceBProgram("FourInARow.js");

        BProgramRunner rnr = new BProgramRunner(bprog);

        // Print program events to the console
        // TODO don't print!
        rnr.addListener( new PrintBProgramRunnerListener() );

        // go!
        rnr.run();

        // just a place holder, we should return something smarter...
        return 1;
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

        String code = treeToString(((GPIndividual) ind).trees[0], state);
        System.out.println("treeToString result: " + code);

        int run_result = bp_run();   // TODO currently it's garbage value

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
