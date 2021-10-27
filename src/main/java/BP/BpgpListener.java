package BP;

import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;

import java.io.PrintStream;

public class BpgpListener extends PrintBProgramRunnerListener {
    public BEvent runResult;

    public BpgpListener( PrintStream aStream ){
        super(aStream);
    }

    public int numOfEvents = 0;

    public void eventSelected(BProgram bp, BEvent theEvent) {
        numOfEvents++;
//        System.out.println("eventSelected: " + numOfEvents + ". event: " + theEvent);
        super.eventSelected(bp, theEvent);
        if (theEvent.name.contains("Game over"))
            runResult = theEvent;
    }

}
