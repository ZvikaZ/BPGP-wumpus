package func;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.*;
import ec.util.Code;
import ec.util.DecodeReturn;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class QueryERC extends ERC {
    public int value;

    // TODO read from user-supplied file?
    static public String[] queries = {
            "Cell.NearWithoutKnownDanger_NoGold",
            "Cell.NearUnvisitedNoDanger_NoGold",
            "Cell.NearVisited_NoGold",
            "Cell.UnVisitedSafeToVisit",
            "Cell.UnVisitedPossibleDangerRoute"
    };

    public String name() {
        return "QueryERC";
    }

    public String toStringForHumans() {
        return queries[value];
    }

    public String encode() {
        return Code.encode(value);
    }

    public boolean decode(DecodeReturn dret) {
        int pos = dret.pos;
        String data = dret.data;
        Code.decode(dret);
        if (dret.type != DecodeReturn.T_DOUBLE) {
            // uh oh! Restore and signal error.
            dret.data = data;
            dret.pos = pos;
            return false;
        }
        value = (int)dret.l;
        return true;
    }

    public boolean nodeEquals(GPNode node) {
        return (node.getClass() == this.getClass() && ((QueryERC)node).value == value);
    }

    public void readNode(EvolutionState state, DataInput dataInput) throws IOException {
        value = dataInput.readInt();
    }

    public void writeNode(EvolutionState state, DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(value);
    }

    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextInt(queries.length);
    }

    public void mutateNode(EvolutionState state, int thread) {
        throw new RuntimeException();
    }

    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack,
                     GPIndividual individual, Problem Problem) {
        StringData rd = ((StringData)(input));
        rd.str = toStringForHumans();
    }
}

