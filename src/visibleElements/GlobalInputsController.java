package visibleElements;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import controls.GlobalVariables;
import controls.Validation;
import dataClasses.Example;
import dataClasses.RefPoint;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Edward on 6/17/2015. Controls the Global Inputs section of the
 * UI. Mainly this controller validates the inputs and binds the values to
 * their respective parameters in the GlobalVariables class. This class will
 * also trigger chart and corridor recalculation events depending on what
 * information is changed
 * @version 2.0
 * @author Edward Foyle
 */

public class GlobalInputsController implements Initializable {

    @FXML private NumberField numIntsField;
    @FXML private NumberField cycleLenField;
    @FXML private NumberField speedLimField;
    @FXML private NumberField startUpLossField;
    @FXML private NumberField extOfGreenField;
    @FXML private ComboBox refPoint;
    @FXML private Label error;
    @FXML private ComboBox examples;

    @Override
    public void initialize(URL fxmlLocation, ResourceBundle resources) {
        examples.getItems().setAll(Example.values());
        refPoint.getItems().setAll(RefPoint.values());
        refPoint.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                ((BehaviorSkinBase) refPoint.getSkin()).getBehavior().traverseNext();
            }
        });
        GlobalVariables.example.bind(examples.valueProperty());
        numIntsField.setForceInteger(true);
        GlobalVariables.numIntersections.bind(numIntsField.intermediateValueProperty());
        GlobalVariables.cycleLen.bind(cycleLenField.enteredValueProperty());
        GlobalVariables.systemCorridor.speedProperty().bind(speedLimField.enteredValueProperty());
        GlobalVariables.startUpLoss.bind(startUpLossField.enteredValueProperty());
        GlobalVariables.extOfGreen.bind(extOfGreenField.enteredValueProperty());
        GlobalVariables.refPoint.bind(refPoint.valueProperty());

        refPoint.valueProperty().addListener(observable -> {
            GlobalVariables.recalcIntersections.set(!GlobalVariables.recalcIntersections.get());
        });

        cycleLenField.enteredValueProperty().addListener(observable -> {
            GlobalVariables.recalcIntersections.set(!GlobalVariables.recalcIntersections.get());
        });

        speedLimField.enteredValueProperty().addListener(observable -> {
            GlobalVariables.recalcChart.set(!GlobalVariables.recalcChart.get());
        });

        startUpLossField.enteredValueProperty().addListener(observable -> {
            GlobalVariables.recalcChart.set(!GlobalVariables.recalcChart.get());
        });

        extOfGreenField.enteredValueProperty().addListener(observable -> {
            GlobalVariables.recalcChart.set(!GlobalVariables.recalcChart.get());
        });
        examples.valueProperty().addListener((observable, oldValue, newValue) -> {
            Example newVal = (Example) newValue;
            switch (newVal) {
                case DDI:
                    GlobalVariables.loadIntersections.set(true);
                    cycleLenField.enteredValueProperty().set(100);
                    speedLimField.enteredValueProperty().set(35);
                    startUpLossField.enteredValueProperty().set(2);
                    extOfGreenField.enteredValueProperty().set(1);
                    refPoint.setValue(RefPoint.BEGIN_1ST_GREEN);
                    numIntsField.enteredValueProperty().set(4);
                    return;
                case TRADITIONAL:
                    GlobalVariables.loadIntersections.set(true);
                    cycleLenField.enteredValueProperty().set(80);
                    speedLimField.enteredValueProperty().set(45);
                    startUpLossField.enteredValueProperty().set(2);
                    extOfGreenField.enteredValueProperty().set(1);
                    refPoint.setValue(RefPoint.BEGIN_1ST_GREEN);
                    numIntsField.enteredValueProperty().set(5);
                    return;
                case MANUAL:
                    cycleLenField.setText("");
                    speedLimField.setText("");
                    startUpLossField.setText("");
                    extOfGreenField.setText("");
                    refPoint.setValue(null);
                    numIntsField.setText("");
                    GlobalVariables.systemCorridor.getIntersections().clear();
            }
        });

        numIntsField.enteredValueProperty().addListener(observable -> {
            GlobalVariables.recalcIntersections.set(!GlobalVariables.recalcIntersections.get());
        });

        BooleanBinding submittable = new BooleanBinding() {
            {
                super.bind(numIntsField.textProperty(), cycleLenField.textProperty(),
                        speedLimField.textProperty(), startUpLossField.textProperty(),
                        extOfGreenField.textProperty(), refPoint.valueProperty());
            }
            @Override
            protected boolean computeValue() {
                return Validation.validateDouble(numIntsField.textProperty().get(),
                        cycleLenField.textProperty().get(),
                        speedLimField.textProperty().get(),
                        startUpLossField.textProperty().get(),
                        extOfGreenField.textProperty().get())
                       && refPoint.getValue() instanceof RefPoint;
            }
        };
        error.visibleProperty().bind(submittable.not());
    }
}
