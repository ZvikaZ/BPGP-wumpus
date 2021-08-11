package func;

import ec.*;
import ec.gp.*;

public class RequestPlan extends GPNode {
    public String toString() { return "RequestPlan"; }

    public int expectedChildren() { return 2; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            String result = "\t\tsync({request: Event(\"Plan\", {plan: ";

            StringData rd = ((StringData)(input));

            children[0].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += "}), waitFor: AnyPlan}, ";

            children[1].eval(state,thread,input,stack,individual,problem);
            result += rd.str;

            result += ")\n";

            rd.str = result;
        }
}




