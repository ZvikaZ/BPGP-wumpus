package func;

import ec.gp.GPData;

public class StringData extends GPData {
    // return value
    public String str;


    public void copyTo(final GPData gpd) {
        // copy my stuff to another DoubleData
        ((StringData)gpd).str = str;
    }

}

