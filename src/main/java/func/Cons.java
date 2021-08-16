package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class Cons extends GPNode { 
    public String toString() { return "Cons"; }

    public int expectedChildren() { return 2; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result = "";
            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            children[1].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            rd.str = result;
        }
}




