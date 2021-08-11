package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class CtxBthread extends GPNode {
    public String toString() { return "CtxBthread"; }

    public int expectedChildren() { return 2; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            String query = rd.str;

            children[1].eval(state,thread,input,stack,individual,problem);
            String requestPlan = rd.str;

            rd.str =
                //TODO have different names?
                "ctx.bthread(\"GeneratedBT\", \"" + query + "\", function (entity) {\n" +
                "    while(true) {\n" +
                "        " + requestPlan + "\n" +
                "    }\n" +
                "})\n";

        }
}




