package dataClasses;

import javafx.geometry.Point2D;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.shape.Line;

/**
 * Created by Edward on 5/21/2015. A line that can be represented by point-slope
 * form The line can be converted to a series with the toSeries() method
 */
public class BandLine extends Line {

    private double slope;
    private Point2D point;

    public BandLine() {
        super();
    }

    /**
     * constructor that overrides superclass constructor. Initializes the point
     * and slope parameters
     *
     * @param startX the starting x value
     * @param startY the starting y value
     * @param endX   the ending x value
     * @param endY   the ending y value
     */
    public BandLine(double startX, double startY, double endX, double endY) {
        super(startX, startY, endX, endY);
        point = new Point2D(startX, startY);
        slope = (endY - startY) / (endX - startX);
    }

    /**
     * constructor that takes in point slope form initializes the start and end
     * x,y pairs to start at the given point and end at the extrapolated value
     * at y + 1
     *
     * @param point a point on the line
     * @param slope the slope of the line
     */
    public BandLine(Point2D point, double slope) {
        super();
        this.slope = slope;
        this.point = point;
        setStartX(point.getX());
        setStartY(point.getY());
        setEndX(extrapXVal(point.getY() + 1));
        setEndY(point.getY() + 1);
    }

    /**
     * same as the point slope constructor, but allows you to specify the ending
     * y coordinate. the constructor will extrapolate the corresponding x value
     *
     * @param point  a point on the line
     * @param slope  the slope of the line
     * @param otherY the ending y coordinate
     */
    public BandLine(Point2D point, double slope, double otherY) {
        super();
        this.slope = slope;
        this.point = point;
        setStartX(point.getX());
        setStartY(point.getY());
        setEndX(extrapXVal(otherY));
        setEndY(otherY);
    }

    /**
     * extrapolates an x value given a y value
     *
     * @param yVal the y value
     * @return the corresponding x value
     */
    public double extrapXVal(double yVal) {
        return (yVal - point.getY()) / slope + point.getX();
    }

    /**
     * extrapolates a y value given an x value
     *
     * @param xVal the x value
     * @return the corresponding y value
     */
    public double extrapYVal(double xVal) {
        return slope * (xVal - point.getX()) + point.getY();
    }

    /**
     * creates a series representation of the BandLine that can be plotted in
     * an XYChart
     * @param highY the highest y value
     * @param lowY the lowest y value
     * @return a new series of the BandLine that goes from the highest y to
     * the lowest y
     */
    //create a series representation of the line that extends from high y to low y
    public Series<Double, Double> toSeries(double highY, double lowY) {
        Data<Double, Double> dataStart = new Data<>(extrapXVal(highY), highY);
        Data<Double, Double> dataEnd = new Data<>(extrapXVal(lowY), lowY);
        Series<Double, Double> result = new Series<>();
        result.getData().addAll(dataStart, dataEnd);
        return result;
    }

    /**
     * gets the slope of the line
     * @return the slope of the line
     */
    public double getSlope() {
        return slope;
    }

    /**
     * gets the given point on the line
     * @return the initial point on the line
     */
    public Point2D getPoint() {
        return point;
    }
}
