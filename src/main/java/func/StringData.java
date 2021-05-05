package func;

import ec.gp.*;

public class StringData extends GPData {
    // return value
    public String str;

    // used for cons
    private String seperator;

    public String getSeperator() {
        if (seperator == null)
            return "";
        else
            return seperator;
    }

    // if needed, we'll implement it as a real stack; currently it's not needed...
    private String origSeperator;
    void pushSeperator(String seperator) {
        if (origSeperator != null)
            throw new RuntimeException();

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

