package dataClasses;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * Created by Edward on 5/20/2015. Represents a signal cycle. Has offset
 * (reference point is start of green), redTime, greenTime and cycle length
 * properties (cycle length is a bound property dependent on the red and green
 * time)
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class Cycle {

    private DoubleProperty offset = new SimpleDoubleProperty(0);
    private DoubleProperty redTime = new SimpleDoubleProperty(0);
    private DoubleProperty greenTime = new SimpleDoubleProperty(0);
    private DoubleBinding cycleLen = new DoubleBinding() {
        {
            super.bind(redTime, greenTime);
        }

        @Override
        protected double computeValue() {
            return redTime.get() + greenTime.get();
        }
    };

    /**
     * copies another cycle
     *
     * @param other the cycle to be copied
     */
    public Cycle(Cycle other) {
        this.offset.set(other.getOffset());
        this.redTime.set(other.getRedTime());
        this.greenTime.set(other.getGreenTime());
        //this.cycleLen = other.getCycleLen();
    }

    /**
     * creates a cycle from the inputted offset, redTime and greenTime
     *
     * @param offset the time offset of the cycle (if greater than the cycle
     *               length, it will be modded with the cycle length
     * @param redTime the red time
     * @param greenTime the green time
     */
    public Cycle(double offset, double redTime, double greenTime) {
        this.redTime.set(redTime);
        this.greenTime.set(greenTime);
        //cycleLen = redTime + greenTime;
        this.offset.setValue((offset < 0) ?
                (cycleLen.get() - (Math.abs(offset) % cycleLen.get())) % cycleLen.get()
                : (offset % cycleLen.get()));
    }

    /**
     * default no-arg constructor
     */
    public Cycle() {}

    public DoubleProperty offsetProperty() {
        return offset;
    }

    public double getOffset() {
        return offset.get();
    }

    public double getRedTime() {
        return redTime.get();
    }

    public double getGreenTime() {
        return greenTime.get();
    }

    public double getCycleLen() {
        return cycleLen.get();
    }

    public DoubleProperty redTimeProperty() {
        return redTime;
    }

    public DoubleProperty greenTimeProperty() {
        return greenTime;
    }

    public DoubleBinding cycleLenProperty() {
        return cycleLen;
    }

    /**
     * creates a new Cycle that is this cycle shifted by delta
     * @param delta the shift to perform on this cycle
     * @return a new cycle shifted by delta from this cycle
     */
    public Cycle shift(double delta) {
        double newOffset = offset.getValue() + delta;
        newOffset = (newOffset < 0) ? (cycleLen.get() - (Math.abs(newOffset) % cycleLen.get())) % cycleLen.get()
                : (newOffset % cycleLen.get());
        offset.setValue(newOffset);
        return new Cycle(this);
    }

    /**
     * converts the red time to range closest to the offset value
     * @return the red time as a single range
     */
    public Range getRedAsRange() {
        return new Range(offset.get() - redTime.get(), offset.get());
    }

    /**
     * converts the green time to a range closest to the offset value
     * @return the green time as a single range
     */
    public Range getGreenAsRange() {
        return new Range(offset.getValue(), offset.get() + greenTime.get());
    }

    /**
     * equals method
     * @param other another object
     * @return if these two objects are equal
     */
    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Cycle)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        Cycle that = (Cycle) other;
        return this.offset.getValue() == that.getOffset() && this.redTime.get() == that.getRedTime()
               && this.greenTime.get() == that.getGreenTime();
    }

    /**
     * @return the toString() method of the red and green ranges
     */
    public String toString() {
        return getRedAsRange().toString() + getGreenAsRange().toString();
    }
}
