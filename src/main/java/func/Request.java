package func;

import ec.*;
import ec.gp.*;
import ec.util.*;

public class Request extends GPNode { // implements EvalPrint {
    public String toString() { return "Request"; }

    public void eval(final EvolutionState state,
                     final int thread,
                     final GPData input,
                     final ADFStack stack,
                     final GPIndividual individual,
                     final Problem problem)
        {
            StringData rd = (StringData)input;
            //TODO parameters for 'request'
            rd.str = "bp.sync({ request:[ StaticEvents.Draw ] });\n";
        }
}




