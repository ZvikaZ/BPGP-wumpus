import ec.EvolutionState;
import ec.Statistics;
import ec.gp.ge.GEIndividual;
import ec.gp.koza.KozaFitness;
import ec.simple.SimpleBreeder;
import ec.util.IntBag;
import ec.util.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class BpgpStatistics extends Statistics {
    public static final String OUTPUT_DIR = "output";
    public int bpgpLog;

    final int SUBPOP = 0;
    private long startTime = System.currentTimeMillis();

    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        bpgpLog = openLogFile(state, "bpgp.stat", false);
    }

    private int openLogFile(EvolutionState state, String fileName, boolean compress) {
        Path filePath = Paths.get(OUTPUT_DIR).resolve(fileName);
        try {
            return state.output.addLog(filePath.toFile(), !compress, compress);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void postEvaluationStatistics(EvolutionState state) {
        super.postEvaluationStatistics(state);
        int log = openLogFile(state, String.format("generation_%d.stat", state.generation), false);
        var individuals = state.population.subpops.get(SUBPOP).individuals;
        double[] fitnesses = new double[individuals.size()];
        HashSet<Double> fitnessesSet = new HashSet<Double>();
        double sumFitnesses = 0;
        double bestFitness = Double.POSITIVE_INFINITY;

        long stopTime = System.currentTimeMillis();
        double elapsedSeconds = (stopTime - startTime) / 1000.0;
        startTime = stopTime;

        state.output.println(String.format("Generation %d has %d individuals\n", state.generation, individuals.size()), log);
		//TODO remove after fixing 'breedthreads = auto' issue
//        state.output.println(String.format("Generation %d has %d individuals, %d miscMaps\n",
//                state.generation, individuals.size(), ((ArrayList) ((BpgpSpecies)state.population.subpops.get(SUBPOP).species).miscMaps).size()),
//                log);
        for (int index = 0; index < individuals.size(); index++) {
            GEIndividual geInd = (GEIndividual)(individuals.get(index));
            geInd.printIndividualForHumans(state, log);
            if (state.generation > 0)
                printParents(state, index, log);
            state.output.println("-----------------", log);

            fitnesses[index] = ((KozaFitness) geInd.fitness).standardizedFitness();
            fitnessesSet.add(fitnesses[index]);
            sumFitnesses += fitnesses[index];
            if (fitnesses[index] < bestFitness)
                bestFitness = fitnesses[index];
        }
        Arrays.sort(fitnesses);
        var median = fitnesses[individuals.size() / 2];
        var mean = sumFitnesses / individuals.size();
        double unique_ratio = (double)fitnessesSet.size() / individuals.size();
        state.output.println(
                String.format("Generation %d, best: %f, mean: %f, median: %f, num_of_inds: %d, unique_values: %d, unique_ratio: %f, time(s): %f",
                        state.generation, bestFitness, mean, median, individuals.size(), fitnessesSet.size(),
                        unique_ratio, elapsedSeconds), bpgpLog);

    }

    private void printParents(EvolutionState state, int index, int log) {
        int numElites = ((SimpleBreeder)state.breeder).numElites(state, SUBPOP);
        if (index < numElites) {
            state.output.println("parent: elitism", log);
        } else {
            var maps = ((BpgpSpecies)state.population.subpops.get(SUBPOP).species).miscMaps;
            printParents(state, maps.get(index - numElites), log);
//            try {
//                printParents(state, maps.get(index - numElites), log);
//            } catch (IndexOutOfBoundsException e) {
//                //TODO why is this happening? it occurs only with threads
//                e.printStackTrace();
//                state.output.println("missing miscMaps, cannot print parent for " + index, log);
//                state.output.warning("missing miscMaps, cannot print parent for " + index);
//            }
        }
    }

    private void printParents(EvolutionState state, HashMap<String, Object> map, int log) {
        IntBag[] bag = (IntBag[])map.get("parents");
        if (bag.length == 2 && bag[0] != null && bag[1] != null) {
            if (bag[0].objs.length == 2) {
                if (bag[1].objs.length != 1)
                    throw new RuntimeException("BpgpStatistics.printParents: Unexpected length");
                if (bag[0].objs[1] != bag[1].objs[0])
                    throw new RuntimeException("BpgpStatistics.printParents: objs value mismatch");
                state.output.println("parents: " + bag[0].objs[0] + " , " + bag[0].objs[1], log);
            } else if (bag[0].objs.length == 1) {
                state.output.println(String.format("parent: %d (failed to use: %d)", bag[0].objs[0], bag[1].objs[0]), log);
            } else {
                throw new RuntimeException("BpgpStatistics.printParents: Unexpected length");
            }
        } else if (bag.length == 1 || (bag.length == 2 && bag[1] == null)) {
            if (bag[0].objs.length != 1)
                throw new RuntimeException("BpgpStatistics.printParents: Unexpected length");

            state.output.println("parent: " + bag[0].objs[0], log);
        } else {
            throw new RuntimeException("BpgpStatistics.printParents: Unexpected length");
        }
    }

}
