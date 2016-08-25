package controls;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import visibleElements.ChartLabels;
import visibleElements.TimeSpaceChart;
import visibleElements.UserInput;

/**
 * Main method that initializes everything
 * @author Edward Foyle
 * @version 1.0
 */
public class Main extends Application {

    private final double[] windowSize = {1500, 800};

    /**
     * Initializes the GUI window
     * @param stage because it has to be there
     */
    @Override
    public void start(Stage stage) {
        ScrollPane controls = new ScrollPane(new UserInput());
        BorderPane root = new BorderPane();
        root.setLeft(controls);
        Scene scene = new Scene(root, windowSize[0], windowSize[1]);
        stage.setScene(scene);
        stage.setResizable(true);
        stage.setTitle("Time-Space Diagram Utility");
        stage.show();

        GlobalVariables.changeToggle.addListener(observable -> {
            //TODO make chart update in real-time as intersections are entered
            if (GlobalVariables.formFilled.get() || true) {
                GlobalVariables.chart = new TimeSpaceChart(Process.calcAxisX(), Process.calcAxisY());
                root.setCenter(GlobalVariables.chart);
            } else {
                root.getChildren().clear();
                root.setLeft(controls);
            }
            if (GlobalVariables.formFilled.get()) {
                GlobalVariables.labels = new ChartLabels();
                root.setRight(GlobalVariables.labels);
            }
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

}