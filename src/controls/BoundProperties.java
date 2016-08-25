package controls;

import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Edward on 7/15/2015. Allows multiple properties to be added to this on and a change
 * event to be fired when ANY of the properties change
 */
public class BoundProperties {
    private final PropertyChangeSupport changeHandler;
    private final Map<ObservableValue, String> propertyNameMap;
    private final ChangeListener changeListener;

    public BoundProperties(Object bean) {
        this.changeHandler = new PropertyChangeSupport(bean);
        this.propertyNameMap = new HashMap<>();
        this.changeListener = new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                String propertyName = BoundProperties.this.propertyNameMap.get(observable);
                BoundProperties.this.changeHandler.firePropertyChange
                        (propertyName, oldValue, newValue);
            }
        };
    }

    public void addPropertyChangeEventFor(Property... propertyArray) {
        for (int i = 0; i < propertyArray.length; i++) {
            Property property = propertyArray[i];
            if (!this.propertyNameMap.containsKey(property)) {
                this.propertyNameMap.put(property, property.getName());
                property.addListener(this.changeListener);
            }
        }
    }

    public void addChangeListener(PropertyChangeListener listener) {
        this.changeHandler.addPropertyChangeListener(listener);
    }

    public void removeChangeListener(PropertyChangeListener listener) {
        this.changeHandler.removePropertyChangeListener(listener);
    }
}
