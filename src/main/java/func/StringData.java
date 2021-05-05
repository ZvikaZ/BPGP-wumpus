package func;

import ec.gp.*;

public class StringData extends GPData {
    // return value
    public String str;

    // used for cons
    public String seperator;

    // if needed, we'll implement it as a realstack; currently it's not needed...
    private String origSeperator = null;
    void pushSeperator(String seperator) {
        assert origSeperator == null;
        this.origSeperator = this.seperator;
        this.seperator = seperator;
    }

    void popSeparator() {
        seperator = origSeperator;
    }

    public void copyTo(final GPData gpd) {
        // copy my stuff to another DoubleData
        ((StringData)gpd).str = str;
        ((StringData)gpd).seperator = seperator;
        ((StringData)gpd).origSeperator = origSeperator;
    }
}

