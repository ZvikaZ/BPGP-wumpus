package func;

import ec.gp.GPData;

public class StringData extends GPData {
    // return value
    public String str;

    // current player's color
    public String playerColor;


    public void copyTo(final GPData gpd) {
        // copy my stuff to another DoubleData
        ((StringData)gpd).str = str;
        ((StringData)gpd).playerColor = playerColor;
    }

}

