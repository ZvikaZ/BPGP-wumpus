package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;

public class WaitFor extends GPNode { // implements EvalPrint {
    public String toString() { return "WaitFor"; }

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




