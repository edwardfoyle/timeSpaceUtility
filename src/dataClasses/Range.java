package dataClasses;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Edward on 5/21/2015. This class represents a single continuous
 * value range. It provides methods such as contains, eclude and union of ranges
 * Disjoint ranges are represented as an ArrayList of Ranges Magnitude is a
 * bound property to the start and end values
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class Range implements Comparable<Range> {

    private DoubleProperty start = new SimpleDoubleProperty();
    private DoubleProperty end = new SimpleDoubleProperty();
    private DoubleBinding magnitude = new DoubleBinding() {
        {
            super.bind(start, end);
        }

        @Override
        protected double computeValue() {
            return end.get() - start.get();
        }
    };

    /**
     * no arg constructor sets beginning and end of range to NaN
     */
    public Range() {
        start.setValue(Double.NaN);
        end.setValue(Double.NaN);
    }

    /**
     * initializes the start and end of this range
     *
     * @param start the starting value
     * @param end the ending value
     */
    public Range(double start, double end) {
        if (start <= end) {
            this.start.setValue(start);
            this.end.setValue(end);
        } else {
            throw new RuntimeException(
                    "starting value must be less than or equal to ending value");
        }
    }

    public double getStart() {
        return start.get();
    }

    public DoubleProperty startProperty() {
        return start;
    }

    public double getEnd() {
        return end.get();
    }

    public DoubleProperty endProperty() {
        return end;
    }

    public Number getMagnitude() {
        return magnitude.get();
    }

    public DoubleBinding magnitudeProperty() {
        return magnitude;
    }

    public void setStart(double start) {
        this.start.set(start);
    }

    public void setEnd(double end) {
        this.end.set(end);
    }

    /**
     * checks if the test value is within this range
     * @param testVal the value to be tested
     * @return a boolean of whether or not the value is in this range
     */
    public boolean contains(double testVal) {
        return start.get() <= testVal && testVal <= end.get();
    }

    /**
     * checks if the test value is included in ANY of the inputted ranges
     * @param ranges a disjoint range (List of Ranges)
     * @param test a test value
     * @return whether or not the value is contained in the disjoint ranges
     */
    public static boolean contains(List<Range> ranges, double test) {
        for (int i = 0; i < ranges.size(); i++) {
            if (ranges.get(i).contains(test)) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks if this range fully contains another range
     * @param subRange the test range
     * @return a boolean of whether or not this range fully covers the subRange
     */
    public boolean contains(Range subRange) {
        return start.get() <= subRange.getStart() &&
               subRange.getEnd() <= end.get();
    }

    /**
     * checks if this Range partially covers another range
     * @param otherRange the test Range
     * @return a boolean of whether or not these Ranges partially cover each
     * other
     */
    public boolean intersects(Range otherRange) {
        return this.contains(otherRange.getStart()) ||
               this.contains(otherRange.getEnd());
    }

    /**
     * computes the intersection of this Range with another. If no
     * intersection exists, it returns an empty range
     * @param other the other Range
     * @return the intersection of this range and the other as a new Range
     */
    public Range intersection(Range other) {
        if (this.intersects(other)) {
            return new Range(
                    this.start.get() <= other.getStart() ? other.getStart() :
                            this.start.get(),
                    this.end.get() <= other.getEnd() ? this.end.get() :
                            other.getEnd());
        } else {
            return new Range();
        }
    }

    /**
     * excludes the input range from this range.
     * @param range the range to be excluded
     * @return an ArrayList of the ranges that cover all values included in
     * this range and not in the other range. The ArrayList will only ever be
     * 0, 1, or 2 elements in size
     */
    public ArrayList<Range> exclude(Range range) {
        ArrayList<Range> result = new ArrayList<>();
        if (this.contains(range)) {
            result.add(new Range(start.get(), range.getStart()));
            result.add(new Range(range.getEnd(), end.get()));
        } else if (range.contains(this)) {
            return result;
        } else {
            double newStart =
                    start.get() < range.getEnd() && range.getEnd() < end.get() ?
                            range.getEnd() : start.get();
            double newEnd = start.get() < range.getStart() &&
                            range.getStart() < end.get() ? range.getStart() :
                    end.get();
            result.add(new Range(newStart, newEnd));
        }
        return result;
    }

    /**
     * An extremely inefficient algorithm for excluding multiple ranges from
     * this range and returning the minimum number of disjoint
     * ranges to span the solution set.
     * @param ranges a list of ranges to be excluded from this one
     * @return an ArrayList of ranges spanning values in this Range and not
     * in any of the input ranges
     */
    public ArrayList<Range> excludeAll(List<Range> ranges) {
        ArrayList<Range> result = new ArrayList<>(Collections.singletonList(this));
        ArrayList<Range> temps = new ArrayList<>();
        for (Range range : ranges) {
            temps.clear();
            temps.addAll(result);
            result.clear();
            for (Range temp : temps) {
                result.addAll(temp.exclude(range));
            }
        }
        result = union(result);
        return result;
    }

    /**
     * Another extremely inefficient algorithm for excluding one list of ranges
     * from another list of Ranges.
     *
     * @param ranges    the original ranges
     * @param excluding the ranges to be excluded
     * @return a new array list of ranges
     */
    public static ArrayList<Range> exclude(List<Range> ranges, List<Range> excluding) {
        ArrayList<Range> result = new ArrayList<>();
        for (Range range : ranges) {
            result.addAll(range.excludeAll(excluding));
        }
        result = union(result);
        return result;
    }

    /**
     * exclude this range from all ranges in the input
     *
     * @param ranges the ranges that will have this range removed from them
     * @return the new list of ranges that do not contain this range
     */
    public ArrayList<Range> excludeFromAll(List<Range> ranges) {
        ArrayList<Range> result = new ArrayList<>();
        for (Range range : ranges) {
            result.addAll(range.exclude(this));
        }
        return result;
    }

    public double magnitude() {
        return magnitude.get();
    }

    /**
     * a method to convert this range into 2 parallel lines to prepare the
     * band to be plotted
     * @param slope the slope of the lines
     * @return the 2 parallel lines as a Band object
     */
    public Band toBand(double slope) {
        BandLine front = new BandLine(new Point2D(start.get(), 0), slope);
        BandLine back = new BandLine(new Point2D(end.get(), 0), slope);
        return new Band(front, back);
    }

    /**
     * output a new range that is this range shifted by delta
     * @param delta the amount of shift
     * @return a new, shifted Range
     */
    public Range shift(double delta) {
        return new Range(start.get() + delta, end.get() + delta);
    }

    /**
     * shift a list of ranges by the same delta
     * @param ranges the list of ranges to be shifted
     * @param delta the amount of shift
     * @return a new, shifted ArrayList of ranges
     */
    public static ArrayList<Range> shift(List<Range> ranges, double delta) {
        ArrayList<Range> result = new ArrayList<>();
        for (Range range : ranges) {
            result.add(range.shift(delta));
        }
        return result;
    }

    /**
     * computes the minimum number of ranges required to span a list of ranges
     * @param ranges the inputted ranges
     * @return the minimum spanning set
     */
    public static ArrayList<Range> union(List<Range> ranges) {
        Collections.sort(ranges);
        ArrayList<Range> result = new ArrayList<Range>();
        int idx = 0;
        for (Range range : ranges) {
            if (result.isEmpty()) { //if first time through
                result.add(range);
            } else if (!result.get(idx).contains(range) &&
                       result.get(idx).intersects(range)) {
                result.set(idx, result.get(idx).union(range));
            } else if (!result.get(idx).intersects(range)) {
                result.add(range);
                idx++;
            }
        }
        return result;
    }

    /**
     * compute the union of 2 ranges. The ranges must intersect
     * @param r1 the first range
     * @param r2 the second range
     * @return the union of the ranges
     */
    private static Range union(Range r1, Range r2) {
        return new Range(
                Collections.min(Arrays.asList(r1.getStart(), r2.getStart())),
                Collections.max(Arrays.asList(r1.getEnd(), r2.getEnd())));
    }

    /**
     * compute the union of this range with another. The ranges must intersect
     * @param r1 the other range
     * @return the union of the ranges
     */
    private Range union(Range r1) {
        return new Range(
                Collections.min(Arrays.asList(this.start.get(), r1.getStart())),
                Collections.max(Arrays.asList(this.end.get(), r1.getEnd())));
    }

    /**
     * alows ranges to be sorted by their starting values
     * @param other another range to compare this one to
     * @return an ordering integer
     */
    @Override
    public int compareTo(Range other) {
        return ((Double) this.start.get()).compareTo(other.getStart());
    }

    @Override
    public String toString() {
        return "[" + start.get() + ", " + end.get() + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other == null || !(other instanceof Range)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        Range that = (Range) other;
        return this.start.get() == that.getStart() && this.end.get() == that.getEnd();
    }
}
