// run with -file src/main/resources/first.params

import ec.*;

public class Evolve {
    // based on ec.Evolve - has 'simple main' in comments:
    public static void main(String[] args) {
        EvolutionState state = ec.Evolve.possiblyRestoreFromCheckpoint(args);
        if (state != null)  // loaded from checkpoint
            state.run(EvolutionState.C_STARTED_FROM_CHECKPOINT);
        else {
            state = ec.Evolve.initialize(ec.Evolve.loadParameterDatabase(args), 0);
            state.run(EvolutionState.C_STARTED_FRESH);
        }
        ec.Evolve.cleanup(state);
        System.exit(0);
    }
}