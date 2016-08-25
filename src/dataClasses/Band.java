package dataClasses;

import javafx.scene.chart.XYChart.Series;

import java.util.ArrayList;

/**
 * A class that contains a front and back BandLine
 * Created by Edward on 5/21/2015.
 */
public class Band {

    private BandLine front = new BandLine();
    private BandLine back = new BandLine();

    /**
     * constructor
     * @param front the front of the band
     * @param back the back of the band
     */
    public Band(BandLine front, BandLine back) {
        this.front = front;
        this.back = back;
    }

    /**
     * converts the band to a series that can be plotted on an XYChart
     * @param highY the highest y value to be plotted
     * @param lowY the lowest y value to be plotted
     * @return an ArrayList of series' representing the band
     */
    public ArrayList<Series<Double, Double>> toSeries(double highY, double lowY) {
        ArrayList<Series<Double, Double>> result = new ArrayList<>();
        result.add(front.toSeries(highY, lowY));
        result.add(back.toSeries(highY, lowY));
        return result;
    }

    /**
     * calculates the width of the band
     * @return the bandwidth
     */
    public double bandWidth() {
        return front.getPoint().distance(back.getPoint());
    }
}
