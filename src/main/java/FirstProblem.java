
/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


import ec.*;
import ec.gp.*;
import ec.gp.ge.GEIndividual;
import ec.gp.koza.*;
import ec.simple.*;
import ec.vector.*;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;


public class FirstProblem extends GPProblem implements SimpleProblemForm {
    private int bp_run() {
        // This will load the program file from <Project>/src/main/resources/
        final BProgram bprog = new ResourceBProgram("FourInARow.js");

        BProgramRunner rnr = new BProgramRunner(bprog);

        // Print program events to the console
        // TODO don't print!
        rnr.addListener( new PrintBProgramRunnerListener() );

        // go!
//        rnr.run();

        // just a place holder, we should return something smarter...
        return 1;
    }

    public void evaluate(final EvolutionState state,
                         final Individual ind,
                         final int subpopulation,
                         final int threadnum)
    {
        if (ind.evaluated) return;

        if (!(ind instanceof GEIndividual))
            state.output.fatal("Whoa!  It's not a GEIndividual!!!",null);

        int run_result = bp_run();   // TODO currently it's garbage value

        KozaFitness f = ((KozaFitness)ind.fitness);
        f.setStandardizedFitness(state, run_result);
        //Z f.hits = sum;   //TODO what's this?
        ind.evaluated = true;
        //TODO the previous problem used 'ind2'. does it matter?
    }
}
