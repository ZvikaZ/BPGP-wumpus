import ec.EvolutionState;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class BpgpEvolve {

    public static final File OUTPUT_DIR = new File("output");

    public static void main(String[] args) throws IOException {
        FileUtils.deleteDirectory(OUTPUT_DIR);
        OUTPUT_DIR.mkdir();
        String[] evolveArgs = {"-file", BpgpUtils.getResourceFileName("single_machine.params")};
        evolve(evolveArgs);
        if (!BpgpUtils.isSlurm()) {
            plotGraph();
        }

    }

    // we need to 'conda activate bpgp' for this to work
    private static void plotGraph() {
        System.out.println("Preparing graph...");
        String[] cmd = {
              "python",
              "src/main/python/analyze.py",
              ".",
              "-s"
        };
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // based on ec.Evolve - has 'simple main' in comments:
    private static void evolve(String[] args) {
        EvolutionState state = ec.Evolve.possiblyRestoreFromCheckpoint(args);
        if (state != null)  // loaded from checkpoint
            state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
        else {
            state = ec.Evolve.initialize(ec.Evolve.loadParameterDatabase(args), 0);
            state.run(EvolutionState.C_STARTED_FRESH);
        }
        ec.Evolve.cleanup(state);
    }
}