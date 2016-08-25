package controls;

import dataClasses.Corridor;
import dataClasses.Example;
import dataClasses.RefPoint;
import javafx.beans.property.*;
import visibleElements.ChartLabels;
import visibleElements.TimeSpaceChart;

/**
 * Created by Edward on 5/11/2015.
 * global variables
 * @author Edward Foyle
 * @version 1.0
 */
public class GlobalVariables {
    public static double DEFAULT_INTERSECTION_WIDTH = 22;
    public static DoubleProperty speedLim = new SimpleDoubleProperty();
    public static DoubleProperty cycleLen = new SimpleDoubleProperty();
    public static DoubleProperty startUpLoss = new SimpleDoubleProperty();
    public static DoubleProperty extOfGreen = new SimpleDoubleProperty();
    public static ObjectProperty<RefPoint> refPoint = new SimpleObjectProperty<>();
    public static IntegerProperty numIntersections = new SimpleIntegerProperty();

    public static ObjectProperty<Example> example = new SimpleObjectProperty<>();
    public static BooleanProperty loadIntersections = new SimpleBooleanProperty(false);

    public static int jankIntersectionCounter;

    public static Corridor systemCorridor = new Corridor();

    //change listeners
    public static BooleanProperty formFilled = new SimpleBooleanProperty(false);
    public static BooleanProperty redrawToggle = new SimpleBooleanProperty(false);
    public static BooleanProperty changeToggle = new SimpleBooleanProperty(false);

    //new change listener scheme
    public static BooleanProperty recalcChart = new SimpleBooleanProperty(false);
    public static BooleanProperty recalcIntersections = new SimpleBooleanProperty(false);
    public static BooleanProperty recalcCorridor = new SimpleBooleanProperty(false);

    //UI elements
    public static TimeSpaceChart chart;
    public static ChartLabels labels;

    //debugging
    public static boolean debugging = false;
    //public static Corridor corridor = Process.genCorridor();
}
