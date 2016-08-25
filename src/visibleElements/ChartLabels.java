package visibleElements;

import controls.GlobalVariables;
import dataClasses.Intersection;
import javafx.beans.binding.DoubleBinding;
import javafx.geometry.Insets;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;

/**
 * Created by Edward on 6/10/2015. Creates cross-street labels that will
 * appear on the RHS of the time-space chart. The label text is bound to the
 * intersection cross street name property and the location of the text is
 * bound to the y position of the intersections
 *
 * @author Edward Foyle
 * @version 1.0
 */
public class ChartLabels extends AnchorPane {

    public ChartLabels() {
        super();
        setPrefHeight(800);
        setPrefWidth(20);
        setPadding(new Insets(0,10,0,0));
        NumberAxis yAxis = (NumberAxis) GlobalVariables.chart.getYAxis();
        for (Intersection curr : GlobalVariables.systemCorridor.getIntersections()) {
            DoubleBinding yVal = new DoubleBinding() {
                {
                    super.bind(curr.distProperty(), yAxis.scaleProperty());
                }
                @Override
                protected double computeValue() {
                    return yAxis.getDisplayPosition(curr.distProperty().get()) - 5;// + yShift;
                }
            };
            Label label = new Label();
            label.setFont(Font.font(18));
            label.setPrefHeight(40);
            label.translateYProperty().bind(yVal);
            label.textProperty().bind(curr.crossStNameProperty());
            getChildren().add(label);
        }
    }
}
