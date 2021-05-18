import ec.EvolutionState;
import ec.Statistics;
import ec.gp.ge.GEIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class BpgpStatistics extends Statistics {
    public int bpgpLog;

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        bpgpLog = openLogFile(state, "bpgp.stat", false);
    }

    private int openLogFile(EvolutionState state, String fileName, boolean compress) {
        File statisticsFile = new File(fileName);
        try {
            return state.output.addLog(statisticsFile, !compress, compress);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void postEvaluationStatistics(EvolutionState state) {
        super.postEvaluationStatistics(state);
        int log = openLogFile(state, String.format("generation_%d.stat", state.generation), false);
        var individuals = state.population.subpops.get(0).individuals;
        double[] fitnesses = new double[individuals.size()];
        double sumFitnesses = 0;
        double bestFitness = 0;

        state.output.println(String.format("Generation %d has %d individuals\n", state.generation, individuals.size()), log);
        for (int index = 0; index < individuals.size(); index++) {
            GEIndividual geInd = (GEIndividual)(individuals.get(index));
            geInd.printIndividualForHumans(state, log);
            state.output.println("-----------------", log);

            fitnesses[index] = ((KozaFitness) geInd.fitness).standardizedFitness();
            sumFitnesses += fitnesses[index];
            if (fitnesses[index] > bestFitness)
                bestFitness = fitnesses[index];
        }
        Arrays.sort(fitnesses);
        var median = fitnesses[individuals.size() / 2];
        var mean = sumFitnesses / individuals.size();
        state.output.println(
                String.format("Generation %d, best: %f, mean: %f, median: %f",
                        state.generation, bestFitness, mean, median), bpgpLog);
    }
}
