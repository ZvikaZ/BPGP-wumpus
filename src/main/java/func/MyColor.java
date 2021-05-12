package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class MyColor extends GPNode {
    public String toString() { return "MyColor"; }

    public int expectedChildren() { return 0; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
    {

        StringData rd = ((StringData)(input));
        //TODO support playing the other side, and return "\"Red\""
        rd.str = "\"Yellow\"";
    }
}
