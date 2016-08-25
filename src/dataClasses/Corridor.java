package dataClasses;

import controls.GlobalVariables;
import controls.Process;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


/**
 * Created by Edward on 5/7/2015. Represents a series of intersections and provides methods for
 * calculating the bands through the corridor
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class Corridor {

    private DoubleProperty speed = new SimpleDoubleProperty();
    private ObservableList<Intersection> intersections = FXCollections.observableArrayList();

    //represent inbound and outbound bands as ranges along the 0 intersection
    private ObservableList<Range> outboundBandRanges = FXCollections.observableArrayList();
    private ObservableList<Range> inboundBandRanges = FXCollections.observableArrayList();

    private ObservableList<Series<Number, Number>> bands = FXCollections.observableArrayList();

    //constructors

    /**
     * initialized an intersection from an already populated ArrayList of intersections
     *
     * @param intersections the ArrayList of Intersection objects
     */
    public Corridor(ArrayList<Intersection> intersections) {
        this.intersections = FXCollections.observableArrayList(intersections);
        if (this.intersections.size() != 0) {
            updateBands();
        }
        isFull();
    }

    /**
     * Initializes an empty corridor of a given size
     *
     * @param size the size of the corridor
     */
    public Corridor(int size) {
        intersections.clear();
        for (int i = 0; i < size; i++) {
            intersections.add(new Intersection());
        }
    }

    /**
     * no-arg constructor. Adds a listener to the global number of intersections and will update the
     * size of this corridor accordingly
     */
    public Corridor() {
        GlobalVariables.numIntersections.addListener((observable, oldValue, newValue) -> {
                if (newValue != null && !newValue.equals(Double.NaN) && !newValue.equals(0)) {
                    setNumIntersections(GlobalVariables.numIntersections.get());
                }
        });
    }

    /**
     * sets the number of intersections to a given size. if the current size is greater than the
     * inputted size, intersections from the end of the ArrayList will be removed. If the given size
     * is larger than the current size, empty intersections will be added
     *
     * @param size the new corridor size
     */
    public void setNumIntersections(int size) {
        if (intersections.size() > size) {
            intersections.remove(size, intersections.size());
        } else {
            int stop = size - intersections.size();
            for (int i = 0; i < stop; i++) {
                intersections.add(new Intersection());
            }
        }
    }

    //calculate bands

    /**
     * calls all methods involved in updating the bands
     */
    public void updateBands() {
        if (this.isFull()) {
            calcInboundBand();
            calcOutboudBand();
            calcVisible();
        }
    }

    //normalize cycles

    /**
     * normalized the outbound cycles. This means that it shifts the cycles over by the distance a
     * car would have traveled by the time it gets to this intersection from the first one.
     *
     * @return a new ArrayList of cycles
     */
    private ArrayList<Cycle> normalizeCyclesOut() {
        ArrayList<Cycle> result = new ArrayList<>();
        for (Intersection curr : intersections) {
            Cycle cycle = new Cycle(curr.getCycleOut());
            double dist = curr.getDist();
            double timeShift = dist / -speed.get();
            cycle.shift(timeShift);
            result.add(cycle);
        }
        return result;
    }

    /**
     * normalized the inbound cycles. (see outbound cycle description)
     *
     * @return a new ArrayList of cycles
     */
    private ArrayList<Cycle> normalizeCyclesIn() {
        ArrayList<Cycle> result = new ArrayList<>();
        for (Intersection curr : intersections) {
            if (!curr.equals(new Intersection())) {
                Cycle cycle = new Cycle(curr.getCycleIn());
                double dist = curr.getDist();
                double timeShift = dist / speed.get();
                cycle.shift(timeShift);
                result.add(cycle);
            }
        }
        return result;
    }

    /**
     * calculates the outbond bands and updates the OutboundBandRanges ArrayList. Bands are kept
     * track of as ranges along the dist = 0 axis.
     */
    public void calcOutboudBand() {
        ArrayList<Range> redRegions = new ArrayList<>();
        for (Cycle cycle : normalizeCyclesOut()) {
            Range redRange = cycle.getRedAsRange();
            redRange.setEnd(redRange.getEnd() + GlobalVariables.startUpLoss.get());
            redRange.setStart(redRange.getStart() + GlobalVariables.extOfGreen.get());
            redRegions.add(redRange);
            redRegions.add(redRange.shift(GlobalVariables.cycleLen.get()));
            redRegions.add(redRange.shift(-GlobalVariables.cycleLen.get()));
        }
        Range greenRange = intersections.get(0).getCycleOut().getGreenAsRange();
        greenRange.setStart(greenRange.getStart() + GlobalVariables.startUpLoss.get());
        greenRange.setEnd(greenRange.getEnd() + GlobalVariables.extOfGreen.get());
        outboundBandRanges.setAll(greenRange.excludeAll(redRegions));
    }

    /**
     * calculates the inbound bands and updates the InboundBandRanges ArrayList. Bands are kept
     * track of as ranges along the dist = 0 axis.
     */
    public void calcInboundBand() {
        ArrayList<Range> redRegions = new ArrayList<>();
        for (Cycle cycle : normalizeCyclesIn()) {
            Range redRange = cycle.getRedAsRange();
            redRange.setEnd(redRange.getEnd() + GlobalVariables.startUpLoss.get());
            redRange.setStart(redRange.getStart() + GlobalVariables.extOfGreen.get());
            redRegions.add(redRange);
            redRegions.add(redRange.shift(GlobalVariables.cycleLen.get()));
            redRegions.add(redRange.shift(-GlobalVariables.cycleLen.get()));
        }
        //System.out.println(redRegions);
        Range greenRange = intersections.get(0).getCycleIn().getGreenAsRange();
        greenRange.setStart(greenRange.getStart() + GlobalVariables.startUpLoss.get());
        greenRange.setEnd(greenRange.getEnd() + GlobalVariables.extOfGreen.get());
        inboundBandRanges.setAll(greenRange.excludeAll(redRegions));
        for (int i = 0; i < inboundBandRanges.size(); i++) {
            inboundBandRanges.set(i, inboundBandRanges.get(i).shift(GlobalVariables.cycleLen.get()));
        }
    }

    /**
     * converts the band ranges to an ObservableList of series that can be plotted
     */
    private void calcVisible() {
        bands.clear();
        for (Series curr : getOutboundBandAsSeries()) {
            bands.add(curr);
        }
        for (Series curr : getInboundBandAsSeries()) {
            bands.add(curr);
        }
    }

    //adders & setters

    /**
     * adds a single intersection to the backing intersection ArrayList
     *
     * @param newInt the new intersection
     */
    public void addIntersection(Intersection newInt) {
        intersections.add(newInt);
        updateBands();
    }

    /**
     * add a collection of intersections to the backing intersection ArrayList
     *
     * @param newInts the collection of intersections
     */
    public void addAllIntersections(Collection<Intersection> newInts) {
        intersections.addAll(newInts);
        updateBands();
    }

    /**
     * replace a current intersection with a new one
     *
     * @param index        the index to replace
     * @param intersection the intersection to be inserted
     */
    public void setIntersection(int index, Intersection intersection) {
        System.out.println("intersection " + index + " submitted");
        intersections.set(index, intersection);
        System.out.println(toString());
        updateBands();
    }

    /**
     * sets the speed of this corridor. Note that setting this will break the connection between the
     * speed of the corridor and the global speed setting
     *
     * @param speed
     */
    public void setSpeed(double speed) {
        this.speed.set(speed);
    }

    //getters
    public ObservableList<Series<Number, Number>> getBands() {
        return bands;
    }

    public double getSpeed() {
        return speed.get();
    }

    public DoubleProperty speedProperty() {
        return speed;
    }

    public int getNumIntersections() {
        return intersections.size();
    }

    /**
     * get the distance offsets of all of the intersections in this corridor.
     *
     * @return
     */
    public ArrayList<Double> getDistances() {
        return intersections.stream().map(Intersection::getDist)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }

    /**
     * get the slider bars for each intersection as a list of Data
     *
     * @return the sliders as an ObservableList of Data that can be plotted in an XYChart
     */
    public ObservableList<Data> getSliders() {
        return FXCollections.observableList(intersections.stream().filter
                (intersection -> !intersection.isEmpty()).map(Intersection::getSlider)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll));
    }

    public ObservableList<Intersection> getIntersections() {
        return intersections;
    }

    public ObservableList<Range> getOutboundBandRanges() {
        return outboundBandRanges;
    }

    public ObservableList<Range> getInboundBandRanges() {
        return inboundBandRanges;
    }

    /**
     * calculates the outbound band as a series. Also adds the band-width in as part of the list
     *
     * @return a list of all serises necessary to represent the band including showing the bandwidth
     * value
     */
    public ObservableList<Series> getOutboundBandAsSeries() {
        ObservableList<Series> result = FXCollections.observableArrayList();
        for (Range curr : outboundBandRanges) {
            ArrayList<Series<Double, Double>> serises = curr.toBand(speed.get())
                    .toSeries(Collections.max(getDistances()) + 100,
                            Collections.min(getDistances()) - 100);
            for (Series series : serises) {
                series.setName("OBD");
            }
            Data<Double, Double> rangeLabel = new Data<>();
            Label text = new Label("" + Math.round((double) curr.getMagnitude()) + "s");
            text.setFont(Font.font(16));
            rangeLabel.setNode(text);
            double center = curr.shift(Collections.min(getDistances()) / speed.get()).getStart()
                            + (double) curr.getMagnitude() / 2;
            double offset;
            try {
                offset = (((NumberAxis) GlobalVariables.chart.getYAxis()).getUpperBound()
                          - ((NumberAxis) GlobalVariables.chart.getYAxis()).getLowerBound()) / 20;
            } catch (NullPointerException e) {
                offset = (Process.calcAxisY().getUpperBound() - Process.calcAxisY().getLowerBound())
                         / 20;
                System.out.println("using default band label vertical offset");
            }
            rangeLabel.setXValue(center + offset / speed.get());
            rangeLabel.setYValue(Collections.min(getDistances()) + offset);
            Series<Double, Double> label = new Series<>(FXCollections
                    .observableList(Collections.singletonList(rangeLabel)));
            label.setName("label");
            label.getData().get(0).getNode().toFront();
            serises.add(label);
            result.addAll(serises);
        }
        return result;
    }

    /**
     * calculates the inbound band as a series. Also adds the band-width in as part of the list
     *
     * @return a list of all serises necessary to represent the band including showing the bandwidth
     * value
     */
    public ObservableList<Series> getInboundBandAsSeries() {
        ObservableList<Series> result = FXCollections.observableArrayList();
        for (Range curr : inboundBandRanges) {
            ArrayList<Series<Double, Double>> serises = curr.toBand(-speed.get())
                    .toSeries(Collections.max(getDistances()) + 100,
                            Collections.min(getDistances()) - 100);
            for (Series series : serises) {
                series.setName("IBD");
            }
            Data<Double, Double> rangeLabel = new Data<>();
            Label text = new Label("" + Math.round((double) curr.getMagnitude()) + "s");
            text.setFont(Font.font(16));
            rangeLabel.setNode(text);
            double center = curr.shift(-Collections.min(getDistances()) / speed.get()).getStart()
                            + (double) curr.getMagnitude() / 2;
            double offset;
            try {
                offset = (((NumberAxis) GlobalVariables.chart.getYAxis()).getUpperBound()
                          - ((NumberAxis) GlobalVariables.chart.getYAxis()).getLowerBound()) / 20;
            } catch (NullPointerException e) {
                offset = (Process.calcAxisY().getUpperBound() - Process.calcAxisY().getLowerBound())
                         / 20;
            }
            rangeLabel.setXValue(center - offset / speed.get());
            rangeLabel.setYValue(Collections.min(getDistances()) + offset);
            Series<Double, Double> label = new Series<>(FXCollections.observableList(
                    Collections.singletonList(rangeLabel)));
            label.setName("label");
            label.getData().get(0).getNode().toFront();
            serises.add(label);
            result.addAll(serises);
        }
        return result;
    }

    /**
     * checks to see if there are any empty intersections
     *
     * @return a boolean of whether or not the corridor contains any empty intersections
     */
    public boolean isFull() {
        boolean result = true;
        for (int i = 0; i < intersections.size() && result; i++) {
            result = !intersections.get(i).isEmpty();
        }
        GlobalVariables.formFilled.setValue(result);
        return result;
    }

    /**
     * @return the toString() method of each intersection in this corridor
     */
    public String toString() {
        return intersections.toString();
    }
}
