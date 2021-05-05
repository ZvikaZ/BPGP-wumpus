package func;

import ec.gp.*;
import ec.util.*;

import ec.EvolutionState;
import ec.Problem;
import ec.gp.ADFStack;
import ec.gp.GPData;
import ec.gp.GPIndividual;
import ec.gp.GPNode;


import java.io.*;

public class ColERC extends ERC {
    public long value;
    static public long MAX = 7;     //TODO have it more generic?

    public String toString() {
        //TODO it seems I must return "ERC", and use that in the grammar
        //but if I want two differnt ERCs? seems like a bug...
        return "ERC";
    }

    public String toStringForHumans() {
        return "" + value;
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
        value = dret.l;
        return true;
    }

    public boolean nodeEquals(GPNode node) {
        return (node.getClass() == this.getClass() && ((ColERC)node).value == value);
    }

    public void readNode(EvolutionState state, DataInput dataInput) throws IOException {
        value = dataInput.readLong();
    }

    public void writeNode(EvolutionState state, DataOutput dataOutput) throws IOException {
        dataOutput.writeDouble(value);
    }

    public void resetNode(EvolutionState state, int thread) {
        value = state.random[thread].nextLong(this.MAX);
    }

    public void mutateNode(EvolutionState state, int thread) {
//        double v;
//        do v = value + state.random[thread].nextGaussian() * 0.01;
//        while( v < 0.0 || v >= 1.0 );
//        value = v;
        //TODO
    }
    public void eval(EvolutionState state, int thread, GPData input, ADFStack stack,
                     GPIndividual individual, Problem Problem) {
        StringData rd = ((StringData)(input));
        rd.str = "" + value;      //TODO?
    }
}

