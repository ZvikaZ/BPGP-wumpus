package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class RegisterBThread extends GPNode { // implements EvalPrint {
    public String toString() { return "RegisterBThread"; }

    public int expectedChildren() { return 1; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            //TODO have different names?
            String result = "bp.registerBThread(\"GeneratedBT\" , function() { \n" +
                    "\twhile(true) {\n";

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += "\t}\n" +
                    "});\n";

            rd.str = result;
        }
}




