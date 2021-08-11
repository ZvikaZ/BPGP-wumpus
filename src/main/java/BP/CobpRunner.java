package BP;

import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.execution.listeners.PrintBProgramRunnerListener;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.PrioritizedBSyncEventSelectionStrategy;

import java.net.URISyntaxException;

import static il.ac.bgu.cs.bp.bpjs.context.PrintCOBProgramRunnerListener.Level;

public class CobpRunner {
    /**
     * Choose the desired COBP program...
     */

    /**
     * internal context events are: "CTX.Changed", "_____CTX_LOCK_____", "_____CTX_RELEASE_____"
     * You can filter these event from printing on console using the Level:
     * Level.ALL : print all
     * Level.NONE : print none
     * Level.CtxChanged: print only CTX.Changed events (i.e., filter the transaction lock/release events)
     */
//    private static final Level logLevel = Level.CtxChanged;
    private static final Level logLevel = Level.ALL;

    public CobpRunner(String... resourceNames) {
        BProgram bprog = new ContextBProgram(resourceNames); //"wumpus/dal.js", "wumpus/bl.js");
        final BProgramRunner rnr = new BProgramRunner(bprog);
        rnr.addListener(new PrintCOBProgramRunnerListener(logLevel, new PrintBProgramRunnerListener()));

        bprog.setEventSelectionStrategy(new PrioritizedBSyncEventSelectionStrategy());
//      bprog.setWaitForExternalEvents(true);
        rnr.run();
    }

}


