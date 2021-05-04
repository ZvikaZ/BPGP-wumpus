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
            StringData rd = (StringData)input;
            //TODO parameters for 'waitFor'
            rd.str = "bp.sync({ waitFor:[ StaticEvents.RedWin, StaticEvents.YellowWin, StaticEvents.Draw ] });\n";
        }
}




