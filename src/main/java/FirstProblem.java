
/*
  Copyright 2006 by Sean Luke
  Licensed under the Academic Free License version 3.0
  See the file "LICENSE" for more information
*/


import ec.*;
import ec.simple.*;
import ec.vector.*;

import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.ResourceBProgram;


public class FirstProblem extends Problem implements SimpleProblemForm {

    private int bp_run() {
        // This will load the program file from <Project>/src/main/resources/
        final BProgram bprog = new ResourceBProgram("FourInARow.js");

        BProgramRunner rnr = new BProgramRunner(bprog);

        // Print program events to the console
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

        if (!(ind instanceof BitVectorIndividual))
            state.output.fatal("Whoa!  It's not a BitVectorIndividual!!!",null);

        bp_run();   // currently ignoring the returned value

        int sum=0;
        BitVectorIndividual ind2 = (BitVectorIndividual)ind;

        for(int x=0; x<ind2.genome.length; x++)
            sum += (ind2.genome[x] ? 1 : 0);

        if (!(ind2.fitness instanceof SimpleFitness))
            state.output.fatal("Whoa!  It's not a SimpleFitness!!!",null);
        ((SimpleFitness)ind2.fitness).setFitness(state,
                /// ...the fitness...
                sum/(double)ind2.genome.length,
                ///... is the individual ideal?  Indicate here...
                sum == ind2.genome.length);
        ind2.evaluated = true;
    }
}
