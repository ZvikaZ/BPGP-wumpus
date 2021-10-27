package BP;

import il.ac.bgu.cs.bp.bpjs.context.ContextBProgram;
import il.ac.bgu.cs.bp.bpjs.execution.BProgramRunner;
import il.ac.bgu.cs.bp.bpjs.model.BEvent;
import il.ac.bgu.cs.bp.bpjs.model.BProgram;
import il.ac.bgu.cs.bp.bpjs.model.eventselection.PrioritizedBSyncEventSelectionStrategy;
import org.mozilla.javascript.NativeObject;

import javax.swing.plaf.basic.BasicEditorPaneUI;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;

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
//    private static final Level logLevel = Level.ALL;

    public BpgpListener listener;


    static long seed = System.currentTimeMillis();

    public CobpRunner(String... resourceNames) {

        BProgram bprog = new ContextBProgram(resourceNames);
        final BProgramRunner rnr = new BProgramRunner(bprog);
//        rnr.addListener(new PrintCOBProgramRunnerListener(logLevel, new PrintBProgramRunnerListener()));

        listener = null;

        try {
            File tempFile = File.createTempFile("bpRun-", ".log");
            tempFile.deleteOnExit();
            PrintStream ps = new PrintStream(tempFile);
            listener = rnr.addListener( new BpgpListener(ps) );
        } catch (IOException e) {
            e.printStackTrace();
        }


        var prio = new PrioritizedBSyncEventSelectionStrategy(seed);
        prio.setDefaultPriority(0);
        bprog.setEventSelectionStrategy(prio);
//      bprog.setWaitForExternalEvents(true);
        rnr.run();
    }
}


