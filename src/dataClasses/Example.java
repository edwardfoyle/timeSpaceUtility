package dataClasses;

/**
 * Created by Edward on 7/22/2015. Options for example scenarios
 */
public enum Example {
    MANUAL, DDI, TRADITIONAL;

    public String toString() {
        switch (this) {
            case MANUAL:
                return "Manual";
            case DDI:
                return "DDI";
            case TRADITIONAL:
                return "Traditional";
            default:
                return "";
        }
    }
}
