package dataClasses;

import controls.GlobalVariables;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.scene.Cursor;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

/**
 * Represents an intersection. Has a distance offset from 0, a series of inbound
 * signal times and outbound signal times, as well as a slider data point with
 * custom node. Listens for clicks on the node and changes the 'isSelected'
 * property to 'true' when dragging the slider
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class Intersection {

    private DoubleProperty dist = new SimpleDoubleProperty();

    private StringProperty crossStName = new SimpleStringProperty("");

    private ObjectProperty<Cycle> cycleIn = new SimpleObjectProperty<>(new
            Cycle());
    private ObjectProperty<Cycle> cycleOut = new SimpleObjectProperty<>(new
            Cycle());

    private Data<Double, Double> slider;

    private DoubleBinding colorStopIn = new DoubleBinding() {
        {
            super.bind(cycleIn);
        }

        @Override
        protected double computeValue() {
            return cycleIn.get().getRedTime() / cycleIn.get().getCycleLen();
        }
    };

    private ObjectBinding<Rectangle> rectIn = new ObjectBinding<Rectangle>() {
        {
            super.bind(cycleIn);
        }

        @Override
        protected Rectangle computeValue() {
            double colorStopIn = cycleIn.get().getRedTime() / cycleIn.get().getCycleLen();
            Rectangle rect = new Rectangle(10 * cycleIn.get().getCycleLen(),
                    GlobalVariables.DEFAULT_INTERSECTION_WIDTH / 2);
            Stop[] stopsIn = new Stop[]{new Stop(0, Color.RED),
                    new Stop(colorStopIn, Color.RED),
                    new Stop(colorStopIn, Color.GREEN),
                    new Stop(1, Color.GREEN)};
            LinearGradient gradientIn =
                    new LinearGradient(cycleIn.get().getOffset() - cycleIn.get().getRedTime(),
                            0, cycleIn.get().getOffset() + cycleIn.get().getGreenTime(), 0,
                            false, CycleMethod.REPEAT, stopsIn);
            rect.setFill(gradientIn);
            rect.setTranslateY(-1);
            return rect;
        }
    };
    private ObjectBinding<Rectangle> rectOut = new ObjectBinding<Rectangle>() {
        {
            super.bind(cycleOut);
        }

        @Override
        protected Rectangle computeValue() {
            double colorStopOut = cycleOut.get().getRedTime() / cycleOut.get().getCycleLen();
            Rectangle rect = new Rectangle(10 * cycleIn.get().getCycleLen(),
                    GlobalVariables.DEFAULT_INTERSECTION_WIDTH / 2);
            Stop[] stopsOut = new Stop[]{new Stop(0, Color.RED),
                    new Stop(colorStopOut, Color.RED),
                    new Stop(colorStopOut, Color.GREEN),
                    new Stop(1, Color.GREEN)};
            LinearGradient gradientOut =
                    new LinearGradient(cycleOut.get().getOffset() - cycleOut.get().getRedTime(),
                            0, cycleOut.get().getOffset() + cycleOut.get().getGreenTime(), 0,
                            false, CycleMethod.REPEAT, stopsOut);
            rect.setFill(gradientOut);
            rect.setTranslateY(GlobalVariables.DEFAULT_INTERSECTION_WIDTH / 2);
            return rect;
        }
    };

    private boolean isRef = false;
    private BooleanProperty isSelected = new SimpleBooleanProperty(false);
    //debugging
    private boolean constrainMove = false;

    /**
     * Intersection constructor. Takes in a cycle length, and inbound and
     * outbound red time, an inbound and outbound offset (ref point is
     * start of green, and a distance offset
     * @param cycleLen the cycle length in seconds
     * @param redTimeIn inbound red time in seconds
     * @param redTimeOut outbound red time in seconds
     * @param offsetIn inbound offset in seconds
     * @param offsetOut outbound offset in seconds
     * @param dist distance offset in feet
     */
    public Intersection(double cycleLen, double redTimeIn, double redTimeOut,
                        double offsetIn, double offsetOut, double dist) {
        this.dist.set(dist);
        cycleIn.set(new Cycle(offsetIn, redTimeIn, cycleLen - redTimeIn));
        cycleOut.set(new Cycle(offsetOut, redTimeOut, cycleLen - redTimeOut));
        initVisible();
    }

    /**
     * no arg constructor
     */
    public Intersection() {
    }

    /**
     * gets the slider bar associated with this intersection
     * @return the slider as a data point w/ custom node
     */
    public Data<Double, Double> getSlider() {
        return slider;
    }

    /**
     * @return the distance offset of this intersection
     */
    public double getDist() {
        return dist.get();
    }

    public DoubleProperty distProperty() {
        return dist;
    }

    /**
     * create the slider based on the inputted cycle information
     * also initializes the listeners that will registers clicks and drags on
     * the slider
     */
    private void initVisible() {
        slider = new Data<>(cycleIn.get().getCycleLen() / 2 - 4 * cycleIn.get().getCycleLen(),
                dist.get());


        Pane root = new Pane();

        root.setOnMouseExited(e -> root.setCursor(Cursor.DEFAULT));
        //root.setPickOnBounds(false);
        root.setPrefSize(cycleIn.get().getCycleLen(), GlobalVariables.DEFAULT_INTERSECTION_WIDTH);
        root.getChildren().addAll(rectIn.get(), rectOut.get());
        //root.setScaleY(.1);
        slider.setNode(root);
        cycleIn.get().offsetProperty()
                .addListener((observable, oldVal, newVal) -> {
                    double cycleLen = cycleIn.get().getCycleLen();
                    double delta = (Double) newVal - (Double) oldVal;
                    double next = slider.getXValue() + delta;
                    next = next < 0 ? (cycleLen - (Math.abs(next) % cycleLen)) % cycleLen
                            : (next % cycleLen);
                    slider.setXValue(next - 4 * GlobalVariables.cycleLen.get());
                });

        root.setOnMouseReleased(e -> {
            if (!isRef || !constrainMove) {
                isSelected.set(false);
                root.setCursor(Cursor.HAND);
            }
        });
        root.setOnMouseDragged(e -> {
            if (!isRef || !constrainMove) {
                isSelected.set(true);
                root.setCursor(Cursor.CLOSED_HAND);
            }
        });
        root.setOnMousePressed(e -> {
            if (!isRef || !constrainMove) {
                isSelected.set(true);
                root.setCursor(Cursor.CLOSED_HAND);
            }
        });
        root.setOnMouseEntered(e -> {
            if (!isRef || !constrainMove) {
                root.setCursor(Cursor.HAND);
            }
        });
    }

    public Cycle getCycleIn() {
        return cycleIn.get();
    }

    public Cycle getCycleOut() {
        return cycleOut.get();
    }

    public BooleanProperty isSelectedProperty() {
        return isSelected;
    }

    public ObjectProperty<Cycle> cycleInProperty() {
        return cycleIn;
    }

    public ObjectProperty<Cycle> cycleOutProperty() {
        return cycleOut;
    }

    public void setRef(boolean ref) {
        this.isRef = ref;
    }

    public void setCrossStName(String name) {
        this.crossStName.setValue(name);
    }

    public String getCrossStName() {
        return crossStName.getValue();
    }

    public StringProperty crossStNameProperty() {
        return crossStName;
    }

    public boolean equals(Object other) {
        if (other == null || !(other instanceof Intersection)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        Intersection that = (Intersection) other;
        return this.dist.get() == that.getDist() &&
               this.cycleIn.get().equals(that.getCycleIn()) &&
               this.cycleOut.get().equals(that.getCycleOut());
    }

    public boolean isEmpty() {
        return this.equals(new Intersection());
    }

    public String toString() {
        try {
            return "{IBD: " + cycleIn.get().toString() + " OBD: " +
                   cycleOut.get().toString() + "}";
        } catch (NullPointerException e) {
            return "null";
        }
    }

}
