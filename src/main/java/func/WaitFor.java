package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class WaitFor extends GPNode { // implements EvalPrint {
    public String toString() { return "WaitFor"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result = "\t\tbp.sync({ waitFor:[ ";

            StringData rd = ((StringData)(input));
            rd.pushSeperator(", ");

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += " ] });\n";

            rd.str = result;
            rd.popSeparator();

        }
}




