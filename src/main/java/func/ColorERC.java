package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ec.util.DecodeReturn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class ColorERC extends ERC {
    // true -> Yellow
    public Boolean value;

    public String name() {
        return "ColorERC";
    }

    public String toStringForHumans() {
        if (value)
            return "Yellow";
        else
            return "Red";
    }

    public String encode() {
        return Code.encode(value);
    }

    public boolean decode(DecodeReturn dret) {
        int pos = dret.pos;
        String data = dret.data;
        Code.decode(dret);
        if (dret.type != DecodeReturn.T_BOOLEAN) {
            // uh oh! Restore and signal error.
            dret.data = data;
            dret.pos = pos;
            return false;
        }
        value = (dret.l != 0);
        return true;
    }

    public boolean nodeEquals(GPNode node) {
        return (node.getClass() == this.getClass() && ((ColorERC)node).value == value);
    }

    public void readNode(EvolutionState state, DataInput dataInput) throws IOException {
        value = dataInput.readBoolean();
    }

    public void writeNode(EvolutionState state, DataOutput dataOutput) throws IOException {
        dataOutput.writeBoolean(value);
    }

    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextBoolean();
    }

    public void mutateNode(EvolutionState state, int thread) {
        value = !value;
    }

    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack,
                     GPIndividual individual, Problem Problem) {
        StringData rd = ((StringData)(input));
        rd.str = toStringForHumans();
    }
}

