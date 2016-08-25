package dataClasses;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Created by Edward on 6/17/2015.
 * This enum is for selecting movement in the ring barrier UI. Each Movement
 * is composed of two other enums, Direction and Turning
 */
public enum Movement {
    OB_LEFT(Direction.OUT, Turning.LEFT),
    OB_THRU(Direction.OUT, Turning.THRU),
    IB_LEFT(Direction.IN, Turning.LEFT),
    IB_THRU(Direction.IN, Turning.THRU);

    private ObjectProperty<Direction> direction = new SimpleObjectProperty<>();
    private ObjectProperty<Turning> turning = new SimpleObjectProperty<>();

    Movement(Direction direction, Turning turning) {
        this.direction.set(direction);
        this.turning.set(turning);
    }

    public Direction getDirection() {return direction.get(); }
    public Turning getTurning() {return turning.get(); }
    public ObjectProperty<Direction> directionProperty() { return direction; }
    public ObjectProperty<Turning> turningProperty() { return turning; }

    public String toString() {
        switch (this) {
            case OB_LEFT:
                return "Outbound Left";
            case OB_THRU:
                return "Outbound Through";
            case IB_LEFT:
                return "Inbound Left";
            case IB_THRU:
                return "Inbound Through";
            default:
                return "";
        }
    }

    public enum Direction {
        IN, OUT
    }

    public enum Turning {
        LEFT, THRU
    }
}
