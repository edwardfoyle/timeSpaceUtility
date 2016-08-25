package dataClasses;

/**
 * Created by Edward on 6/17/2015. An enum that represents the 4 possible
 * reference points
 */
public enum RefPoint {
    BEGIN_1ST_GREEN, BEGIN_2ND_GREEN, END_1ST_GREEN, END_2ND_GREEN;

    public String toString() {
        switch (this) {
            case BEGIN_1ST_GREEN:
                return "Begin 1st Green";
            case BEGIN_2ND_GREEN:
                return "Begin 2nd Green";
            case END_1ST_GREEN:
                return "End 1st Green";
            case END_2ND_GREEN:
                return "End 2nd Green";
            default:
                return "";
        }
    }
}
