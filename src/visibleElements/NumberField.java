package visibleElements;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;


/**
 * Created by Edward on 5/27/2015. A child of TextFild that only allows
 * numbers to be inputted. It contains properties 'enteredValue' and
 * 'intermediateValue'. enteredValue attempts to judge when a value has been
 * "entered" by only reassigning it when the field is navigated away from. The
 * intermediateValue is bound directly to the textProperty.
 */
public class NumberField extends TextField {

    //only changes on field not focused
    private DoubleProperty enteredValue = new SimpleDoubleProperty();

    //bound directly to the text property
    private DoubleBinding intermediateValue = new DoubleBinding() {
        {
            super.bind(textProperty());
        }
        @Override
        protected double computeValue() {
            try {
                return Double.parseDouble(getText());
            } catch (NumberFormatException e) {
                return Double.NaN;
            }
        }
    };

    private boolean forceInteger = false;

    public NumberField() {
        this.addEventFilter(KeyEvent.KEY_TYPED, event -> {
            char[] arr = event.getCharacter().toCharArray();
            char ch = arr[event.getCharacter().toCharArray().length - 1];
            if (forceInteger && !(ch >= '0' && ch <= '9')) {
                event.consume();
            } else if (!forceInteger &&
                       !(ch >= '0' && ch <= '9' || ch == '.' || ch == '-')) {
                event.consume();
            }
        });
        focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (!newValue && !textProperty().get().trim().equals("")) {
                try {
                    enteredValue.set(intermediateValue.get());
                } catch (NumberFormatException | NullPointerException e) {
                    enteredValue.set(Double.NaN);
                }
            }
        }));
        //entered value is casted to int because this listener is only
        // invoked when the slider is used to change offset values and in
        // that case showing decimal places is undesirable.
        enteredValue.addListener(observable -> {
            if (enteredValue.get() != intermediateValue.get()) {
                textProperty().set("" + (int) enteredValue.get());
            }
        });
        //allows ENTER key to function similarly to TAB key (traverse Focus
        // and enter values)
        setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                ((BehaviorSkinBase) this.getSkin()).getBehavior()
                        .traverseNext();
            }
        });
    }

    public DoubleProperty enteredValueProperty() { return enteredValue; }

    public double getEnteredValue() {
        return enteredValue.get();
    }

    public double getIntermediateValue() {
        return intermediateValue.get();
    }

    public DoubleBinding intermediateValueProperty() {
        return intermediateValue;
    }

    public void setForceInteger(boolean force) {
        forceInteger = force;
    }
}
