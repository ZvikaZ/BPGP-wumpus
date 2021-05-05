package func;

import ec.gp.*;

import java.util.Stack;

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

    Stack<String> origSeperator = new Stack<String>();

    void pushSeperator(String seperator) {
        this.origSeperator.push(this.seperator);
        this.seperator = seperator;
    }

    void popSeparator() {
        seperator = origSeperator.pop();
    }

    public void copyTo(final GPData gpd) {
        // copy my stuff to another DoubleData
        ((StringData)gpd).str = str;
        ((StringData)gpd).seperator = seperator;
        ((StringData)gpd).origSeperator = origSeperator;
    }
}

