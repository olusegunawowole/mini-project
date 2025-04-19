package application;

import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class FlatButton extends StackPane {// #6495ed
	private Label nameLabel;
	private String bgColor = "#ffffff";
	private String fontColor = "#333333";
	private String borderColor = "#d3d3d3";
	private String bgHoverColor = "#6495ed";
	private String fontHoverColor = "#ffffff";
	private String borderHoverColor = "#d3d3d3";
	private int red = 255;
	private int green = 255;
	private int blue = 255;

	public FlatButton() {
		setMaxSize(125.0, 34.0);
		nameLabel = new Label();
		nameLabel.setStyle("-fx-text-fill: " + fontColor);
		nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
		getChildren().add(nameLabel);
	}

	public FlatButton(String name) {
		setMinSize(125.0, 34.0);
		setMaxSize(125.0, 34.0);
		nameLabel = new Label(name);
		nameLabel.setStyle("-fx-text-fill: " + fontColor);
		nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
		setStyle(bgColor, fontColor, borderColor);
		getChildren().add(nameLabel);
		setCursor(Cursor.HAND);
		setOnMouseEntered(e -> {
			setHoverStyle();
		});
		setOnMouseExited(e -> {
			if (!isDisable())
				setStyle(bgColor, fontColor, borderColor);
		});

		setOnMousePressed(e -> {
			setStyle("-fx-background-color: " + bgHoverColor + " ; -fx-border-width: 1px; -fx-border-color: "
					+ borderHoverColor + "; -fx-opacity: 0.5;");
			nameLabel.setStyle("-fx-text-fill: " + fontHoverColor);
		});
		setOnMouseReleased(e -> {
			setHoverStyle();
		});
		disableProperty().addListener((obj, oldVal, newVal) -> {
			if (newVal) {
				setStyle("-fx-background-color: " + bgColor + "; -fx-border-width: 1px; -fx-border-color: rgb(" + red
						+ ", " + green + ", " + blue + ", 0.4);-fx-opacity: 0.8");
				nameLabel.setStyle("-fx-text-fill: " + fontColor);			
			} else {
				setStyle(bgColor, fontColor, borderColor);
			}
		});

	}

	public void setName(String name) {
		nameLabel.setText(name);
	}

	public void setTooltip(String text) {
		Tooltip tp = new Tooltip(text);
		tp.setStyle("-fx-background-color: white; -fx-border-width:1px; -fx-border-color: black; -fx-text-fill: black");
		tp.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
		Tooltip.install(this, tp);
	}

	public void setStyle(String bgColor, String fontColor, String borderColor) {
		nameLabel.setStyle("-fx-text-fill: " + fontColor);
		setStyle("-fx-background-color: " + bgColor + "; -fx-border-width: 1px; -fx-border-color: " + borderColor);
		this.bgColor = bgColor;
		this.fontColor = fontColor;
		this.borderColor = borderColor;
	}

	public void setHoverStyle(String bgHoverColor, String fontHoverColor, String borderHoverColor) {
		this.bgHoverColor = bgHoverColor;
		this.fontHoverColor = fontHoverColor;
		this.borderHoverColor = borderHoverColor;
	}

	private void setHoverStyle() {
		nameLabel.setStyle("-fx-text-fill: " + fontHoverColor);
		setStyle("-fx-background-color: " + bgHoverColor + "; -fx-border-width: 1px; -fx-border-color: "
				+ borderHoverColor);
	}

	public void setDisableColor(int red, int green, int blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}
}