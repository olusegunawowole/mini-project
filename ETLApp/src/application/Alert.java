package application;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Alert extends VBox {
	private StackPane[] buttons;
	private Label label;

	public Alert() {
		ProgressIndicator PI = new ProgressIndicator();
		label = new Label("Wait...");
		label.setStyle("-fx-text-fill: white");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
		label.setMinHeight(70);
		label.setAlignment(Pos.CENTER);
		setSpacing(5);
		setAlignment(Pos.CENTER);
		setPrefSize(300, 85);
		setMaxSize(300, 135);
		setPadding(new Insets(10, 10, 10, 10));
		setStyle(
				"-fx-background-color: #26619C; -fx-border-width: 0.5px; -fx-border-style: solid; -fx-border-color: #FFFFFF");
		getChildren().addAll(PI, label);
	}

	public Alert(String message, String... buttonName) {
		label = new Label(message);
		label.setWrapText(true);
		label.setStyle("-fx-text-fill: #FFFFFF");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		label.setMinHeight(70);
		label.setAlignment(Pos.TOP_LEFT);

		HBox hb = new HBox(10);

		buttons = new StackPane[buttonName.length];
		for (int i = 0; i < buttonName.length; i++) {
			Label name = new Label(buttonName[i]);
			name.setStyle("-fx-text-fill: #FFFFFF");
			name.setFont(Font.font("Arial", FontWeight.NORMAL, 15));

			buttons[i] = new StackPane(name);
			buttons[i].setStyle(
					"-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: #FFFFFF");
			buttons[i].setMinHeight(25);
			buttons[i].setMinWidth(100);
			int index = i;
			buttons[i].setOnMouseEntered(e -> {
				buttons[index].setStyle(
						"-fx-background-color: #FFFFFF; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: #FFFFFF");
				name.setStyle("-fx-text-fill: #26619C");
			});
			buttons[i].setOnMouseExited(e -> {
				buttons[index].setStyle(
						"-fx-background-color: transparent; -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: #FFFFFF;");
				name.setStyle("-fx-text-fill: white");
			});
			buttons[i].setCursor(Cursor.HAND);
			hb.getChildren().add(buttons[i]);
		}
		hb.setAlignment(Pos.CENTER);

		setSpacing(25);
		setMaxSize(300, 135);
		setPadding(new Insets(10, 10, 10, 10));
		setStyle("-fx-background-color: #26619C; -fx-border-width: 2px; -fx-border-style: solid; -fx-border-color: #FFFFFF;");
		getChildren().addAll(label, hb);

	}

	public void setButtonOnAction(EventHandler<MouseEvent> evt, int buttonNumber) {
		buttons[buttonNumber].setOnMouseClicked(evt);
	}

	public StackPane getButton(int buttonNumber) {
		return buttons[buttonNumber];
	}
}
