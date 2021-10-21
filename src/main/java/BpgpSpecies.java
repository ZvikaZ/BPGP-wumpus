import ec.EvolutionState;
import ec.Subpopulation;
import ec.util.IntBag;
import ec.util.Parameter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BpgpSpecies extends ec.gp.ge.GESpecies {
    public List<HashMap<String, Object>> miscMaps;

    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        miscMaps = new ArrayList<>();
    }

    public void updateSubpopulation(EvolutionState state, Subpopulation subpop) {
        miscMaps = new ArrayList<>();
    }

    public HashMap<String, Object> buildMisc(EvolutionState state, int subpopIndex, int thread) {
        HashMap<String, Object> miscMap = new HashMap<String, Object>();
        IntBag[] bag = new IntBag[2];
        miscMap.put("parents", bag);
        miscMaps.add(miscMap);
        return miscMap;
    }
}
