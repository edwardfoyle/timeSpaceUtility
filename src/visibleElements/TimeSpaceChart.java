package visibleElements;

import controls.GlobalVariables;
import dataClasses.Corridor;
import dataClasses.Intersection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;

import java.util.Set;

/**
 * Created by Edward on 5/29/2015. This is where all of the chart stuff
 * happens. The chart must keep track of the mouse x value to properly
 * convert mouse dragged coordinates into chart coordinates.
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class TimeSpaceChart extends LineChart<Number, Number> {

    private double mousex;
    private Corridor corridor = GlobalVariables.systemCorridor;

    /**
     * creates the chart and initializes mouse listeners that will change the
     * intersection offsets accordingly. Also listens for chart resize and
     * rescales sliders. Also listens for reCalcChart and reCalcCorridor events
     * @param xAxis the x axis
     * @param yAxis the y axis
     */
    public TimeSpaceChart(NumberAxis xAxis, NumberAxis yAxis) {
        super(xAxis, yAxis);

        getStylesheets().add(this.getClass().getResource
                ("/visibleElements/markupFiles/plot-style.css")
                .toExternalForm());
        populateChart();

        setLegendVisible(false);
        setAnimated(false);
        setPrefSize(1100, 800);
        setPickOnBounds(false);

        //mouse listening stuff
        setOnMousePressed(e -> {
            Point2D scenePos = new Point2D(e.getSceneX(), e.getSceneY());
            mousex = (double) xAxis.getValueForDisplay(xAxis.sceneToLocal(scenePos).getX());
            for (Intersection curr : corridor.getIntersections()) {
                curr.isSelectedProperty().set(false);
            }
        });

        setOnMouseReleased(e -> {
            setCursor(Cursor.DEFAULT);
        });

        setOnMouseDragged(e -> {
            Point2D scenePos = new Point2D(e.getSceneX(), e.getSceneY());
            double x = (double) xAxis.getValueForDisplay(xAxis.sceneToLocal(scenePos).getX());
            for (Intersection curr : corridor.getIntersections()) {
                if (curr.isSelectedProperty().get()) {
                    setCursor(Cursor.CLOSED_HAND);
                    double cycleLen = GlobalVariables.cycleLen.get();
                    double delta = x - mousex;
                    Data<Double, Double> slider = curr.getSlider();
                    double newVal = slider.getXValue() + delta;
                    newVal = (newVal < 0) ? (cycleLen - (Math.abs(newVal) % cycleLen)) % cycleLen
                            : (newVal % cycleLen);
                    mousex = x;
                    curr.getCycleOut().shift(delta);
                    curr.getCycleIn().shift(delta);
                    updateChart();
                }
            }
        });

        //rescaling sliders
        xAxis.scaleProperty().addListener(l -> {
            for (Data slider : corridor.getSliders()) {
                slider.getNode().setScaleX(xAxis.getScale());
            }
        });
//        yAxis.scaleProperty().addListener(l -> {
//            for (Data slider : corridor.getSliders()) {
//                slider.getNode().setScaleY(yAxis.getScale());
//            }
//        });

        GlobalVariables.recalcChart.addListener(observable -> {
            updateChart();
        });

        GlobalVariables.recalcCorridor.addListener(observable -> {
            updateSliders();
        });
    }

    /**
     * colors inbound and outbound bands different colors
     */
    private void colorStuff() {
        int nSeries = 0;
        for (Series<Number, Number> curr : getData()) {
            Set<Node> lineNodes = lookupAll(".series" + nSeries);
            try {
                if (curr.getName().equals("IBD")) {
                    for (Node line : lineNodes) {
                        line.setStyle("-fx-stroke: blue");
                    }
                } else if (curr.getName().equals("OBD")) {
                    for (Node line : lineNodes) {
                        line.setStyle("-fx-stroke: orange");
                    }
                } else if (curr.getName().equals("label")) {
                    for (Node label : lineNodes) {
                        label.setStyle("-fx-stroke: transparent");
                    }
                }
            } catch (NullPointerException e) {
                System.out.println(
                        "colors not set for series: " + curr.getName() + ", " + nSeries);
            }
            nSeries++;
        }
    }

    /**
     * initially puts bands and sliders in the chart
     */
    private void populateChart() {
        //TODO make chart update in real-time
        ObservableList<Series<Number, Number>> bands = corridor.getBands();
        for (Series<Number, Number> curr : bands) {
            getData().add(curr);
            colorStuff();
        }

        updateSliders();
    }

    /**
     * removes bands (and not sliders) from the chart to allow a redraw to
     * take place
     */
    private void removeBands() {
        ObservableList<Series<Number, Number>> copy = FXCollections.observableArrayList(getData());
        for (Series<Number, Number> series : copy) {
            if (series.getName().equals("IBD") ||
                series.getName().equals("OBD") ||
                series.getName().equals("label")) {
                getData().remove(series);
            }
        }
    }

    /**
     * removes sliders (and not bands) to allow a redraw to take place
     */
    private void removeSliders() {
        ObservableList<Series<Number, Number>> copy = FXCollections.observableArrayList(getData());
        for (Series<Number, Number> series : copy) {
            if (series.getName().equals("sliders")) {
                getData().remove(series);
            }
        }
    }

    /**
     * moves rearranges render order to put sliders on top
     */
    private void moveSlidersToFont() {
        for (Series<Number, Number> series : getData()) {
            if (series.getName().equals("sliders")) {
                for (Data data : series.getData()) {
                    data.getNode().toFront();
                }
                break;
            }
        }
    }

    /**
     * calls corridor updateBands() method and redraws bands
     */
    private void updateChart() {
        corridor.updateBands();
        removeBands();
        for (Series<Number, Number> newBand : corridor.getBands()) {
            getData().add(newBand);
            colorStuff();
        }
        moveSlidersToFont();
    }

    /**
     * removes and re-adds new sliders. This is only necessary if a change is
     * made to the corridor after the chart is created (an intersection is
     * added or deleted)
     */
    private void updateSliders() {
        removeSliders();
        ObservableList<Data> sliderData = corridor.getSliders();
        for (Data slider : sliderData) {
            slider.getNode().setScaleX(getXAxis().getScaleX());
            //slider.getNode().setScaleY(getYAxis().getScaleY());
        }
        Series<Number, Number> sliders = new Series(sliderData);
        sliders.setName("sliders");
        getData().add(sliders);
        moveSlidersToFont();
    }
}
