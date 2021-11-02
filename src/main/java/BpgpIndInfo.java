import ec.gp.GPIndividual;
import ec.gp.ge.GEIndividual;

// it's not used by evaluator, but kept to have some more information
public class BpgpIndInfo {
    public GPIndividual ind;
    public String code;

    public BpgpIndInfo(GPIndividual ind, String code) {
        this.ind = ind;
        this.code = code;
    }
}
