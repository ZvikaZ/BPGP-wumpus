import ec.EvolutionState;
import ec.Statistics;
import ec.gp.ge.GEIndividual;
import ec.gp.koza.KozaFitness;
import ec.util.IntBag;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

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

//    public void postBreedingStatistics(EvolutionState state) {
//        super.postBreedingStatistics(state);
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

        for (var map : ((BpgpSpecies)state.population.subpops.get(0).species).miscMaps) {
            printParents(state, map, log);
        }
    }

    private void printParents(EvolutionState state, HashMap<String, Object> map, int log) {
        IntBag[] bag = (IntBag[])map.get("parents");
        if (bag.length == 2) {
            if (bag[0].objs.length != 2)
                throw new RuntimeException();
            if (bag[1].objs.length != 1)
                throw new RuntimeException();
            if (bag[0].objs[1] != bag[1].objs[0])
                throw new RuntimeException();

            state.output.println("parents: " + bag[0].objs[0] + " , " + bag[0].objs[1], log);
        } else {
            if (bag[0].objs.length != 1)
                throw new RuntimeException();

            state.output.println("parent: " + bag[0].objs[0], log);
        }
    }

}
