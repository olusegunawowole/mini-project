package application;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;

public class ProgressIndicator extends StackPane {
	private Arc innerArc;
	private Arc outerArc;
	private Label label;

	public ProgressIndicator() {
		label = new Label("0%");
		double radius = 100;

		innerArc = new Arc();
		innerArc.setCenterX(0.0f);
		innerArc.setCenterY(0.0f);
		innerArc.setRadiusX(radius + 0.5);
		innerArc.setRadiusY(radius + 0.5);
		innerArc.setStartAngle(90.0f);
		innerArc.setLength(360.0f);
		innerArc.setFill(Color.WHITE);
		innerArc.setStroke(Color.LIGHTGRAY);
		innerArc.setStrokeWidth(10);

		outerArc = new Arc();
		outerArc.setCenterX(0.0);
		outerArc.setCenterY(0.0);
		outerArc.setRadiusX(radius);
		outerArc.setRadiusY(radius);
		outerArc.setStartAngle(90.0f);
		outerArc.setLength(0);
		outerArc.setType(ArcType.OPEN);
		outerArc.setFill(null);
		outerArc.setStroke(Color.CORAL);
		outerArc.setStrokeWidth(10);

		Group group = new Group(innerArc, outerArc);
		getChildren().addAll(group, label);
	}

	public void update(double maxValue, double currentValue) {
		if (currentValue > maxValue) {
			currentValue = maxValue;
		}
		double percentageCompleted = currentValue / maxValue * 100;
		String text = String.format("%1.0f%s", percentageCompleted, "%");
		label.setText(text);
		double degree = -currentValue / maxValue * 360;
		outerArc.setLength(degree);

	}

}
