package controls;

import dataClasses.Corridor;
import dataClasses.Intersection;
import javafx.scene.chart.NumberAxis;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Edward on 5/8/2015.
 * A few global helper methods
 * @author Edward Foyle
 * @version 1.0
 */
public abstract class Process {

    /**
     * generates a default corridor (mainly for debugging)
     * @return the default corridor
     */
    public static Corridor genCorridor() {
        Intersection int1 = new Intersection(100, 50, 60, 0, 0, 0);
        int1.setRef(true);
        Intersection int2 = new Intersection(100, 60, 40, 0, 0, 4200);
        Intersection int3 = new Intersection(100, 60, 40, 0, 0, 5600);
        Intersection int4 = new Intersection(100, 50, 60, 10, 30, 7000);
        ArrayList<Intersection> intersections = new ArrayList<>(Arrays.asList(int1, int2, int3, int4));
        Corridor corr = new Corridor(intersections);
        corr.setSpeed(GlobalVariables.speedLim.get());
        return corr;
    }

    /**
     * calculate appropriate x axis bounds based on the global corridor
     * @return a number axis with set bounds
     */
    public static NumberAxis calcAxisX() {
        double minX = 0;
        double maxX = Collections.max(GlobalVariables.systemCorridor.getDistances())
                      / GlobalVariables.systemCorridor.getSpeed()
                      + GlobalVariables.cycleLen.get();
        maxX = 3 * GlobalVariables.cycleLen.get() > maxX ? 3 * GlobalVariables.cycleLen.get() : maxX;
        maxX = Math.round(maxX / 10) * 10;
        double scaleX = 0.1 * (maxX - minX);
        scaleX = Math.round(scaleX / 10) * 10;
        NumberAxis axis = new NumberAxis("Time (sec)", minX, maxX, scaleX);
        axis.setTickLabelFont(Font.font(18));
        return axis;
    }

    /**
     * calculates appropriate y axis bounds based on the global corridor
     * @return
     */
    public static NumberAxis calcAxisY() {
        double minY = Math.round(
                Collections.min(GlobalVariables.systemCorridor.getDistances()) / 100) * 100 - 100;
        double maxY = Math.round(
                Collections.max(GlobalVariables.systemCorridor.getDistances()) / 100) * 100 + 100;
        double scaleY = Math.round(0.1 * (maxY - minY) / 100) * 100;
        NumberAxis axis =  new NumberAxis("Distance (ft)", minY, maxY, scaleY);
        axis.setTickLabelFont(Font.font(16));
        axis.setMinorTickCount(4);
        return axis;
    }

}
