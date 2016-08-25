package visibleElements;

import controls.GlobalVariables;
import dataClasses.Example;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * Created by Edward on 6/17/2015. Manages the left hand controls.
 * @version 2.0
 * @author Edward Foyle
 */
public class UserInput extends VBox {

    AnchorPane global;
    VBox intersections = new VBox();

    /**
     * initializes the global inputs area and listens for changes to the
     * number of intersections and will load the corresponding number of
     * intersection input fields
     */
    public UserInput() {
        super();
        try {
            global = FXMLLoader
                    .load(this.getClass().getResource("markupFiles/GlobalInputs.fxml"));
        } catch (IOException e) {
            global = new AnchorPane();
            System.out.println("couldn't load global inputs fxml file");
        }
        getChildren().add(global);
        GlobalVariables.numIntersections.addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.equals(Double.NaN) && !newValue.equals(0)) {
                setNumIntersectionEntries(GlobalVariables.numIntersections.get());
            }
        });
        GlobalVariables.loadIntersections.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                intersections.getChildren().clear();
            }
        });
        GlobalVariables.example.addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(Example.MANUAL)) {
                intersections.getChildren().clear();
            }
        });
        getChildren().add(intersections);
    }

    /**
     * method that sets the correct number of inputs
     * @param size
     */
    private void setNumIntersectionEntries(int size) {
        if (intersections.getChildren().size() > size) {
            intersections.getChildren().remove(size, intersections.getChildren().size());
        } else {
            GlobalVariables.jankIntersectionCounter = intersections.getChildren().size();
            for (int i = intersections.getChildren().size(); i < size; i++) {
                try {
                    intersections.getChildren().add(FXMLLoader.load(this.getClass()
                            .getResource("markupFiles/RingBarrierEntry.fxml")));
                } catch (IOException e) {
                    System.out.println("couldn't load ring barrier inputs fxml file");
                    e.printStackTrace();
                }
                GlobalVariables.jankIntersectionCounter++;
            }
        }
    }
}
