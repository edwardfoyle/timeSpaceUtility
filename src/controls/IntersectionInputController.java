package controls;

import dataClasses.Movement;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.beans.PropertyChangeListener;

/**
 * Created by Edward on 7/15/2015. Class for managing the inputs to the
 * intersection entry boxes
 */
public class IntersectionInputController {
    private final BoundProperties boundProperties = new BoundProperties(this);
    private final DoubleProperty duration1 = new SimpleDoubleProperty(this, "duration1");
    private final DoubleProperty duration2 = new SimpleDoubleProperty(this, "duration2");
    private final DoubleProperty duration3 = new SimpleDoubleProperty(this, "duration3");
    private final DoubleProperty duration4 = new SimpleDoubleProperty(this, "duration4");
    private final DoubleProperty dist = new SimpleDoubleProperty(this, "dist");

    private final ObjectProperty movement1 = new SimpleObjectProperty<Movement>(this, "movement1");
    private final ObjectProperty movement2 = new SimpleObjectProperty<Movement>(this, "movement2");
    private final  ObjectProperty movement3 = new SimpleObjectProperty<Movement>(this, "movement3");
    private final ObjectProperty movement4 = new SimpleObjectProperty<Movement>(this, "movement4");

    public IntersectionInputController() {
        boundProperties.addPropertyChangeEventFor(duration1, duration2, duration3, duration4, dist,
                movement1, movement2, movement3, movement4);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        boundProperties.addChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        boundProperties.addChangeListener(listener);
    }


    //getters and setters
    public BoundProperties getBoundProperties() {
        return boundProperties;
    }

    public double getDuration1() {
        return duration1.get();
    }

    public DoubleProperty duration1Property() {
        return duration1;
    }

    public double getDuration2() {
        return duration2.get();
    }

    public DoubleProperty duration2Property() {
        return duration2;
    }

    public double getDuration3() {
        return duration3.get();
    }

    public DoubleProperty duration3Property() {
        return duration3;
    }

    public double getDuration4() {
        return duration4.get();
    }

    public DoubleProperty duration4Property() {
        return duration4;
    }

    public double getDist() {
        return dist.get();
    }

    public DoubleProperty distProperty() {
        return dist;
    }

    public Object getMovement1() {
        return movement1.get();
    }

    public ObjectProperty movement1Property() {
        return movement1;
    }

    public Object getMovement2() {
        return movement2.get();
    }

    public ObjectProperty movement2Property() {
        return movement2;
    }

    public Object getMovement3() {
        return movement3.get();
    }

    public ObjectProperty movement3Property() {
        return movement3;
    }

    public Object getMovement4() {
        return movement4.get();
    }

    public ObjectProperty movement4Property() {
        return movement4;
    }

    public void setDuration1(double duration1) {
        this.duration1.set(duration1);
    }

    public void setDuration2(double duration2) {
        this.duration2.set(duration2);
    }

    public void setDuration3(double duration3) {
        this.duration3.set(duration3);
    }

    public void setDuration4(double duration4) {
        this.duration4.set(duration4);
    }

    public void setDist(double dist) {
        this.dist.set(dist);
    }

    public void setMovement1(Object movement1) {
        this.movement1.set(movement1);
    }

    public void setMovement2(Object movement2) {
        this.movement2.set(movement2);
    }

    public void setMovement3(Object movement3) {
        this.movement3.set(movement3);
    }

    public void setMovement4(Object movement4) {
        this.movement4.set(movement4);
    }
}
