package func;

import ec.gp.*;

public class StringData extends GPData {
    public String str;    // return value

    public void copyTo(final GPData gpd) {   // copy my stuff to another DoubleData
        ((StringData)gpd).str = str;
    }
}

