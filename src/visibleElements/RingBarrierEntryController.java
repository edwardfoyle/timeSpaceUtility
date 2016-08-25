package visibleElements;

import com.sun.javafx.scene.control.skin.BehaviorSkinBase;
import controls.GlobalVariables;
import controls.IntersectionInputController;
import controls.Validation;
import dataClasses.Example;
import dataClasses.Intersection;
import dataClasses.Movement;
import javafx.animation.FadeTransition;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Edward on 6/16/2015. Controller for ring-barrier intersection
 * entry method. Provides methods for converting this input scheme into the
 * inputs that the Corridor and Intersection constructors use as well as
 * firing the properties that activate Corridor and chart updates.
 *
 * @author Edward Foyle
 * @version 2.0
 */
public class RingBarrierEntryController implements Initializable {

    @FXML private ComboBox comboBox1;
    @FXML private ComboBox comboBox2;
    @FXML private ComboBox comboBox3;
    @FXML private ComboBox comboBox4;
    @FXML private TextField nameLabel;
    @FXML private NumberField duration1text;
    @FXML private NumberField duration2text;
    @FXML private NumberField duration3text;
    @FXML private NumberField duration4text;
    @FXML private NumberField offsetText;
    @FXML private NumberField distField;
    @FXML private Label error;
    @FXML private Label confirm;
    @FXML private ImageView TLimgView;
    @FXML private ImageView TRimgView;
    @FXML private ImageView BLimgView;
    @FXML private ImageView BRimgView;
    @FXML private StackPane TLpane;
    @FXML private StackPane TRpane;
    @FXML private StackPane BLpane;
    @FXML private StackPane BRpane;
    @FXML private Rectangle TLrect;
    @FXML private Rectangle TRrect;
    @FXML private Rectangle BLrect;
    @FXML private Rectangle BRrect;

    private int index;
    private DoubleProperty ibdGreenTime = new SimpleDoubleProperty();
    private DoubleProperty obdGreenTime = new SimpleDoubleProperty();
    private DoubleProperty ibdOffset = new SimpleDoubleProperty();
    private DoubleProperty obdOffset = new SimpleDoubleProperty();
    private BooleanBinding ibdFirst = new BooleanBinding() {
        {
            super.bind(ibdGreenTime, obdGreenTime, ibdOffset, obdOffset);
        }
        @Override
        protected boolean computeValue() {
            boolean result;
            if (ibdOffset.get() < obdOffset.get()) {
                return true;
            } else if (ibdOffset.get() > obdOffset.get()) {
                return false;
            } else if (ibdGreenTime.get() < obdGreenTime.get()) {
                return true;
            } else return false;
        }
    };

    private DoubleBinding topDivider;
    private DoubleBinding botDivider;

    private double delta;

    private IntersectionInputController controller = new
            IntersectionInputController();

    @Override
    public void initialize(URL fxmlLocation, ResourceBundle resources) {
        index = GlobalVariables.jankIntersectionCounter;
        comboBox1.getItems().setAll(Movement.values());
        comboBox1.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                duration1text.requestFocus();
            }
        });
        comboBox2.getItems().setAll(Movement.values());
        comboBox2.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                duration2text.requestFocus();
            }
        });
        comboBox3.getItems().setAll(Movement.values());
        comboBox3.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                duration3text.requestFocus();
            }
        });
        comboBox4.getItems().setAll(Movement.values());
        comboBox4.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                duration4text.requestFocus();
            }
        });
        nameLabel.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                comboBox1.requestFocus();
            }
        });
        TLrect.widthProperty().bind(TLpane.widthProperty());
        TLrect.heightProperty().bind(TLpane.heightProperty());
        TRrect.widthProperty().bind(TRpane.widthProperty());
        TRrect.heightProperty().bind(TRpane.heightProperty());
        BLrect.widthProperty().bind(BLpane.widthProperty());
        BLrect.heightProperty().bind(BLpane.heightProperty());
        BRrect.widthProperty().bind(BRpane.widthProperty());
        BRrect.heightProperty().bind(BRpane.heightProperty());

        controller.movement1Property().bind(comboBox1.focusedProperty());
        controller.movement2Property().bind(comboBox2.focusedProperty());
        controller.movement3Property().bind(comboBox3.focusedProperty());
        controller.movement4Property().bind(comboBox4.focusedProperty());
        controller.duration1Property().bind(duration1text.enteredValueProperty());
        controller.duration2Property().bind(duration2text.enteredValueProperty());
        controller.duration3Property().bind(duration3text.enteredValueProperty());
        controller.duration4Property().bind(duration4text.enteredValueProperty());
        controller.distProperty().bind(distField.enteredValueProperty());

        topDivider = new DoubleBinding() {
            {
                super.bind(duration1text.enteredValueProperty(), duration2text
                        .enteredValueProperty());
            }
            @Override
            protected double computeValue() {
                return duration1text.enteredValueProperty().get() /
                       (duration1text.enteredValueProperty().get() +
                        duration2text.enteredValueProperty().get());
            }
        };
        botDivider = new DoubleBinding() {
            {
                super.bind(duration3text.enteredValueProperty(), duration4text
                        .enteredValueProperty());
            }
            @Override
            protected double computeValue() {
                return duration3text.enteredValueProperty().get() /
                       (duration3text.enteredValueProperty().get() +
                        duration4text.enteredValueProperty().get());
            }
        };

        topDivider.addListener(observable -> ringBarrierResize());
        botDivider.addListener(observable -> ringBarrierResize());

        confirm.setVisible(false);

        BooleanBinding submittable = new BooleanBinding() {
            {
                super.bind(duration1text.textProperty(),
                        duration2text.textProperty(),
                        duration3text.textProperty(),
                        duration4text.textProperty(),
                        distField.textProperty(),
                        comboBox1.valueProperty(),
                        comboBox2.valueProperty(),
                        comboBox3.valueProperty(),
                        comboBox4.valueProperty(),
                        offsetText.textProperty());
            }

            @Override
            protected boolean computeValue() {
                return !Validation.validateDouble(duration1text.getText(),
                        duration2text.getText(),
                        duration3text.getText(),
                        duration4text.getText(),
                        distField.getText(),
                        offsetText.getText()) ||
                       !Validation.validateMovement(
                               comboBox1.getValue(),
                               comboBox2.getValue(),
                               comboBox3.getValue(),
                               comboBox4.getValue()) ||
                       !(duration1text.enteredValueProperty().get() +
                        duration2text.enteredValueProperty().get() <=
                        GlobalVariables.cycleLen.get() &&
                        duration3text.enteredValueProperty().get() +
                        duration4text.enteredValueProperty().get() <=
                        GlobalVariables.cycleLen.get());
            }
        };
        controller.addPropertyChangeListener(evt -> {
            if (!submittable.get()) {
                submitForm();
            }
        });
        offsetText.setOnKeyPressed(event -> {
            if ((event.getCode().equals(KeyCode.ENTER) || event.getCode().equals(KeyCode.TAB))) {
                offsetText.enteredValueProperty().set(offsetText.getIntermediateValue());
                if (!submittable.get()) {
                    submitForm();
                }
                distField.requestFocus();
            }
        });
        distField.setOnKeyPressed(event -> {
            if ((event.getCode().equals(KeyCode.ENTER)) || event.getCode().equals(KeyCode.TAB))
            distField.enteredValueProperty().set(distField.getIntermediateValue());
            if (!submittable.get()) {
                submitForm();
            }
            if (event.getCode().equals(KeyCode.ENTER)) {
                ((BehaviorSkinBase) distField.getSkin()).getBehavior().traverseNext();
            }
        });
        error.visibleProperty().bind(submittable);

        TLimgView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                super.bind(comboBox1.valueProperty());
            }

            @Override
            protected Image computeValue() {
                return chooseImg((Movement) comboBox1.getValue());
            }
        });
        TRimgView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                super.bind(comboBox2.valueProperty());
            }

            @Override
            protected Image computeValue() {
                return chooseImg((Movement) comboBox2.getValue());
            }
        });
        BLimgView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                super.bind(comboBox3.valueProperty());
            }

            @Override
            protected Image computeValue() {
                return chooseImg((Movement) comboBox3.getValue());
            }
        });
        BRimgView.imageProperty().bind(new ObjectBinding<Image>() {
            {
                super.bind(comboBox4.valueProperty());
            }

            @Override
            protected Image computeValue() {
                return chooseImg((Movement) comboBox4.getValue());
            }
        });

        GlobalVariables.recalcIntersections.addListener(observable -> {
            if (!submittable.get()) submitForm();
        });

        if (GlobalVariables.loadIntersections.get()) {
            pickExample(GlobalVariables.example.get());
            if (index == GlobalVariables.numIntersections.get() - 1) {
                GlobalVariables.loadIntersections.set(false);
            }
        }
    }

    /**
     * method called when the form is submittable (all fields have passed
     * validation) Sets the intersection at the corresponding index and
     * activates all appropriate update toggles
     */
    public void submitForm() {
        assignOffsetsAndDurations();
        delta = moveOffset();
        GlobalVariables.systemCorridor.setIntersection(index, new Intersection(
                GlobalVariables.cycleLen.get(),
                GlobalVariables.cycleLen.get() - ibdGreenTime.get(),
                GlobalVariables.cycleLen.get() - obdGreenTime.get(),
                ibdOffset.get() + delta,
                obdOffset.get() + delta,
                distField.getEnteredValue()));
        String text = nameLabel.getText();
        GlobalVariables.systemCorridor.getIntersections().get(index)
                .crossStNameProperty().bind(nameLabel.textProperty());

        bindOffset();

        nameLabel.setText(text);
        GlobalVariables.changeToggle.set(GlobalVariables.formFilled.get() &&
                                         !GlobalVariables.changeToggle.get());
        GlobalVariables.recalcChart.set(!GlobalVariables.recalcChart.get());
        if (index == 0) {
            GlobalVariables.systemCorridor.getIntersections().get(index).setRef(true);
        }
        confirm.setVisible(true);
        FadeTransition transition = new FadeTransition(Duration.seconds(2.5), confirm);
        transition.setFromValue(1.0);
        transition.setToValue(0.0);
        transition.play();
    }

    /**
     * calculates an offset shift based on the signal timing and the global
     * reference point
     * @return a delta value for how much the inputted offset needs to be
     * shifted to be inputted into the Intersection constructor
     */
    private double moveOffset() {
        double delta = 0;
        switch (GlobalVariables.refPoint.get()) {
            case END_1ST_GREEN:
                delta = ibdFirst.get() ?
                        (offsetText.getEnteredValue() - ibdGreenTime.get()) - ibdOffset.get()
                        : (offsetText.getEnteredValue() - obdGreenTime.get()) - obdOffset.get();
                break;
            case END_2ND_GREEN:
                delta = ibdFirst.get() ?
                        (offsetText.getEnteredValue() - obdGreenTime.get()) - obdOffset.get()
                        : (offsetText.getEnteredValue() - ibdGreenTime.get()) - ibdOffset.get();
                break;
            case BEGIN_1ST_GREEN:
                delta = ibdFirst.get() ?
                        offsetText.getEnteredValue() - ibdOffset.get()
                        : offsetText.getEnteredValue() - obdOffset.get();
                break;
            case BEGIN_2ND_GREEN:
                delta = ibdFirst.get() ?
                        offsetText.getEnteredValue() - obdOffset.get()
                        : offsetText.getEnteredValue() - ibdOffset.get();
                break;
        }
        return delta;
    }

    /**
     * unbinds and rebinds the inbound and outbound green time and offset
     * properties based on which movements are selected in the ring-barrier
     * entry
     */
    private void assignOffsetsAndDurations() {
        if (comboBox1.getValue().equals(Movement.IB_THRU)) {
            ibdGreenTime.unbind();
            ibdOffset.unbind();
            ibdGreenTime.bind(duration1text.enteredValueProperty());
            ibdOffset.set(0);
        } else if (comboBox1.getValue().equals(Movement.OB_THRU)) {
            obdGreenTime.unbind();
            obdOffset.unbind();
            obdGreenTime.bind(duration1text.enteredValueProperty());
            obdOffset.set(0);
        }
        if (comboBox2.getValue().equals(Movement.IB_THRU)) {
            ibdGreenTime.unbind();
            ibdOffset.unbind();
            ibdGreenTime.bind(duration2text.enteredValueProperty());
            ibdOffset.bind(duration1text.enteredValueProperty());
        } else if (comboBox2.getValue().equals(Movement.OB_THRU)) {
            obdGreenTime.unbind();
            obdOffset.unbind();
            obdGreenTime.bind(duration2text.enteredValueProperty());
            obdOffset.bind(duration1text.enteredValueProperty());
        }
        if (comboBox3.getValue().equals(Movement.IB_THRU)) {
            ibdGreenTime.unbind();
            ibdOffset.unbind();
            ibdGreenTime.bind(duration3text.enteredValueProperty());
            ibdOffset.set(0);
        } else if (comboBox3.getValue().equals(Movement.OB_THRU)) {
            obdGreenTime.unbind();
            obdOffset.unbind();
            obdGreenTime.bind(duration3text.enteredValueProperty());
            obdOffset.set(0);
        }
        if (comboBox4.getValue().equals(Movement.IB_THRU)) {
            ibdGreenTime.unbind();
            ibdOffset.unbind();
            ibdGreenTime.bind(duration4text.enteredValueProperty());
            ibdOffset.bind(duration3text.enteredValueProperty());
        } else if (comboBox4.getValue().equals(Movement.OB_THRU)) {
            obdGreenTime.unbind();
            obdOffset.unbind();
            obdGreenTime.bind(duration4text.enteredValueProperty());
            obdOffset.bind(duration3text.enteredValueProperty());
        }
    }

    /**
     * picks the image for the image view that corresponds with the movement
     * selected
     * @param movement the movement that the image needs to correspond to
     * @return the corresponding image. The references account for this
     * project being placed in a jar file and will still work
     */
    private Image chooseImg(Movement movement) {

        File file;
        if (movement == null) {
            return new Image(getClass().getResource("/assets/blank.png").toExternalForm());
        }
        try {
            switch (movement) {
                case IB_THRU:
                    return new Image(getClass().getResource("/assets/inbound_arrow.png")
                            .toExternalForm());
                case IB_LEFT:
                    return new Image(getClass().getResource("/assets/inbound_left_arrow.png")
                            .toExternalForm());
                case OB_THRU:
                    return new Image(getClass().getResource("/assets/outbound_arrow.png")
                            .toExternalForm());
                case OB_LEFT:
                    return new Image(getClass().getResource("/assets/outbound_left_arrow.png")
                            .toExternalForm());
                default:
                    return new Image(getClass().getResource("/assets/blank.png")
                            .toExternalForm());
            }
        } catch (NullPointerException e) {
            System.out.println("image address is null");
            return null;
        } catch (IllegalArgumentException e) {
            System.out.println("image url is invalid");
            return null;
        }
    }

    /**
     * binds the offset value to the intersection offset so that this value
     * will change when the sliders are moved
     */
    private void bindOffset() {
        switch (GlobalVariables.refPoint.get()) {
            case BEGIN_1ST_GREEN:
                if (ibdFirst.get()) {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleIn()
                        .offsetProperty().addListener(observable -> {
                            offsetText.enteredValueProperty().set(
                                    Math.round(GlobalVariables.systemCorridor.getIntersections()
                                            .get(index).getCycleIn().getOffset()));
                        });
                } else {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleOut()
                        .offsetProperty().addListener(observable -> {
                        offsetText.enteredValueProperty().set(
                                Math.round(GlobalVariables.systemCorridor.getIntersections()
                                        .get(index).getCycleOut().getOffset()));
                    });
                }
                break;
            case BEGIN_2ND_GREEN:
                if (ibdFirst.get()) {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleOut()
                        .offsetProperty().addListener(observable -> {
                        offsetText.enteredValueProperty().set(
                                Math.round(GlobalVariables.systemCorridor.getIntersections()
                                        .get(index).getCycleOut().getOffset()));
                    });
                } else {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleIn()
                        .offsetProperty().addListener(observable -> {
                            offsetText.enteredValueProperty().set(
                                    Math.round(GlobalVariables.systemCorridor.getIntersections()
                                            .get(index).getCycleOut().getOffset()));
                        });
                }
                break;
            case END_1ST_GREEN:
                if (ibdFirst.get()) {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleIn()
                        .offsetProperty().addListener(observable -> {
                            offsetText.enteredValueProperty().set(
                                    Math.round(GlobalVariables.systemCorridor.getIntersections()
                                                       .get(index).getCycleIn().getOffset() +
                                               ibdGreenTime.get()));
                        });
                } else {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleOut()
                        .offsetProperty().addListener(observable -> {
                        offsetText.enteredValueProperty().set(
                                Math.round(GlobalVariables.systemCorridor.getIntersections()
                                                   .get(index).getCycleOut().getOffset() +
                                           obdGreenTime.get()));
                    });
                }
                break;
            case END_2ND_GREEN:
                if (ibdFirst.get()) {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleOut()
                        .offsetProperty().addListener(observable -> {
                        offsetText.enteredValueProperty().set(
                                Math.round(GlobalVariables.systemCorridor.getIntersections()
                                                   .get(index).getCycleOut().getOffset() +
                                           obdGreenTime.get()));
                    });
                } else {
                    GlobalVariables.systemCorridor.getIntersections().get(index).getCycleIn()
                        .offsetProperty().addListener(observable -> {
                            offsetText.enteredValueProperty().set(
                                    Math.round(GlobalVariables.systemCorridor.getIntersections()
                                        .get(index).getCycleIn().getOffset() + ibdGreenTime.get()));
                        });
                }
        }
    }

    /**
     * compares the proportions of each movement and determines the size of
     * the boxes in the ring barrier diagram. The bounding rectangle and
     * ImageView are both bound to the pane properties
     */
    private void ringBarrierResize() {
        if (topDivider.get() < botDivider.get()) {
            TLpane.setPrefWidth(100);
            TRpane.setPrefWidth(150);
            BLpane.setPrefWidth(150);
            BRpane.setPrefWidth(100);
        } else if (topDivider.get() > botDivider.get()) {
            TLpane.setPrefWidth(150);
            TRpane.setPrefWidth(100);
            BLpane.setPrefWidth(100);
            BRpane.setPrefWidth(150);
        } else {
            TLpane.setPrefWidth(125);
            TRpane.setPrefWidth(125);
            BLpane.setPrefWidth(125);
            BRpane.setPrefWidth(125);
        }
    }

    private void pickExample(Example example) {
        switch (example) {
            case DDI:
                setExampleDDI();
                break;
            case TRADITIONAL:
                setExampleTraditional();
        }
        submitForm();
    }
    private void setExampleDDI() {
        switch (index) {
            case 0:
                nameLabel.setText("Fast Ln.");
                comboBox1.setValue(Movement.IB_THRU);
                duration1text.setText("50");
                duration1text.enteredValueProperty().set(50);
                comboBox2.setValue(Movement.OB_LEFT);
                duration2text.setText("20");
                duration2text.enteredValueProperty().set(20);
                comboBox3.setValue(Movement.OB_THRU);
                duration3text.setText("60");
                duration3text.enteredValueProperty().set(60);
                comboBox4.setValue(Movement.IB_LEFT);
                duration4text.setText("10");
                duration4text.enteredValueProperty().set(10);
                offsetText.setText("0");
                offsetText.enteredValueProperty().set(0);
                distField.setText("0");
                distField.enteredValueProperty().set(0);
                return;
            case 1:
                nameLabel.setText("DDI Crossover 1");
                comboBox1.setValue(Movement.OB_THRU);
                duration1text.setText("50");
                duration1text.enteredValueProperty().set(50);
                comboBox2.setValue(Movement.IB_LEFT);
                duration2text.setText("50");
                duration2text.enteredValueProperty().set(50);
                comboBox3.setValue(Movement.OB_LEFT);
                duration3text.setText("50");
                duration3text.enteredValueProperty().set(50);
                comboBox4.setValue(Movement.IB_THRU);
                duration4text.setText("50");
                duration4text.enteredValueProperty().set(50);
                offsetText.setText("10");
                offsetText.enteredValueProperty().set(10);
                distField.setText("800");
                distField.enteredValueProperty().set(800);
                return;
            case 2:
                nameLabel.setText("DDI Crossover 2");
                comboBox1.setValue(Movement.OB_LEFT);
                duration1text.setText("60");
                duration1text.enteredValueProperty().set(60);
                comboBox2.setValue(Movement.IB_THRU);
                duration2text.setText("40");
                duration2text.enteredValueProperty().set(40);
                comboBox3.setValue(Movement.OB_THRU);
                duration3text.setText("60");
                duration3text.enteredValueProperty().set(60);
                comboBox4.setValue(Movement.IB_LEFT);
                duration4text.setText("40");
                duration4text.enteredValueProperty().set(40);
                offsetText.setText("0");
                offsetText.enteredValueProperty().set(0);
                distField.setText("1200");
                distField.enteredValueProperty().set(1200);
                return;
            case 3:
                nameLabel.setText("Disc Dr.");
                comboBox1.setValue(Movement.OB_THRU);
                duration1text.setText("50");
                duration1text.enteredValueProperty().set(50);
                comboBox2.setValue(Movement.IB_LEFT);
                duration2text.setText("20");
                duration2text.enteredValueProperty().set(20);
                comboBox3.setValue(Movement.OB_LEFT);
                duration3text.setText("10");
                duration3text.enteredValueProperty().set(10);
                comboBox4.setValue(Movement.IB_THRU);
                duration4text.setText("60");
                duration4text.enteredValueProperty().set(60);
                offsetText.setText("15");
                offsetText.enteredValueProperty().set(15);
                distField.setText("1500");
                distField.enteredValueProperty().set(1500);
        }
    }
    private void setExampleTraditional() {
        switch (index) {
            case 0:
                nameLabel.setText("Sesame St.");
                comboBox1.setValue(Movement.IB_THRU);
                duration1text.setText("50");
                duration1text.enteredValueProperty().set(50);
                comboBox2.setValue(Movement.OB_LEFT);
                duration2text.setText("20");
                duration2text.enteredValueProperty().set(20);
                comboBox3.setValue(Movement.OB_THRU);
                duration3text.setText("60");
                duration3text.enteredValueProperty().set(60);
                comboBox4.setValue(Movement.IB_LEFT);
                duration4text.setText("10");
                duration4text.enteredValueProperty().set(10);
                offsetText.setText("0");
                offsetText.enteredValueProperty().set(0);
                distField.setText("0");
                distField.enteredValueProperty().set(0);
                return;
            case 1:
                nameLabel.setText("Warp Dr.");
                comboBox1.setValue(Movement.OB_THRU);
                duration1text.setText("60");
                duration1text.enteredValueProperty().set(60);
                comboBox2.setValue(Movement.IB_LEFT);
                duration2text.setText("10");
                duration2text.enteredValueProperty().set(10);
                comboBox3.setValue(Movement.IB_THRU);
                duration3text.setText("60");
                duration3text.enteredValueProperty().set(60);
                comboBox4.setValue(Movement.OB_LEFT);
                duration4text.setText("10");
                duration4text.enteredValueProperty().set(10);
                offsetText.setText("10");
                offsetText.enteredValueProperty().set(10);
                distField.setText("800");
                distField.enteredValueProperty().set(800);
                return;
            case 2:
                nameLabel.setText("Divorce Ct.");
                comboBox1.setValue(Movement.OB_LEFT);
                duration1text.setText("20");
                duration1text.enteredValueProperty().set(20);
                comboBox2.setValue(Movement.IB_THRU);
                duration2text.setText("40");
                duration2text.enteredValueProperty().set(40);
                comboBox3.setValue(Movement.OB_THRU);
                duration3text.setText("60");
                duration3text.enteredValueProperty().set(60);
                comboBox4.setValue(Movement.IB_LEFT);
                duration4text.setText("0");
                duration4text.enteredValueProperty().set(0);
                offsetText.setText("0");
                offsetText.enteredValueProperty().set(0);
                distField.setText("1500");
                distField.enteredValueProperty().set(1500);
                return;
            case 3:
                nameLabel.setText("Memory Ln.");
                comboBox1.setValue(Movement.OB_THRU);
                duration1text.setText("50");
                duration1text.enteredValueProperty().set(50);
                comboBox2.setValue(Movement.IB_LEFT);
                duration2text.setText("20");
                duration2text.enteredValueProperty().set(20);
                comboBox3.setValue(Movement.OB_LEFT);
                duration3text.setText("10");
                duration3text.enteredValueProperty().set(10);
                comboBox4.setValue(Movement.IB_THRU);
                duration4text.setText("60");
                duration4text.enteredValueProperty().set(60);
                offsetText.setText("15");
                offsetText.enteredValueProperty().set(15);
                distField.setText("1800");
                distField.enteredValueProperty().set(1800);
                return;
            case 4:
                nameLabel.setText("Rocky Rd.");
                comboBox1.setValue(Movement.OB_LEFT);
                duration1text.setText("10");
                duration1text.enteredValueProperty().set(10);
                comboBox2.setValue(Movement.IB_THRU);
                duration2text.setText("50");
                duration2text.enteredValueProperty().set(50);
                comboBox3.setValue(Movement.IB_LEFT);
                duration3text.setText("10");
                duration3text.enteredValueProperty().set(10);
                comboBox4.setValue(Movement.OB_THRU);
                duration4text.setText("50");
                duration4text.enteredValueProperty().set(50);
                offsetText.setText("20");
                offsetText.enteredValueProperty().set(20);
                distField.setText("2200");
                distField.enteredValueProperty().set(2200);
        }
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}