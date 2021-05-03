package func;

import ec.*;
import ec.app.ant.*;
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
            double result = 3;
//        double result;
//        DoubleData rd = ((DoubleData)(input));
//
//        children[0].eval(state,thread,input,stack,individual,problem);
//        result = rd.x;
//
//        children[1].eval(state,thread,input,stack,individual,problem);
//        rd.x = result + rd.x;
        }
}




