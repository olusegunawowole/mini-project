package application;

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class VirtualKeyboard extends Application {
	// Copied from caspian css file and modified it as needed
	private String defaultButtonStyle = " -fx-background-color: -fx-shadow-highlight-color, -fx-outer-border, -fx-inner-border, -fx-body-color;"
			+ "    -fx-background-insets: 0 0 -1 0, 0, 1, 2;" + "    -fx-background-radius: 0;"
			+ "    -fx-padding: 0.166667em 0.833333em 0.25em 0.833333em;" + "    -fx-text-fill: -fx-text-base-color;"
			+ "    -fx-alignment: CENTER;" + "    -fx-content-display: LEFT;";
	
	private ArrayList<Button> buttons;

	@Override
	public void start(Stage stage) throws Exception {
		VBox root = new VBox();
		root.setPadding(new Insets(5, 20, 10, 20));

		// Setting up the header and the textArea
		String text = "Type some text using your keyboard. The keys you press"
				+ " will be highlighted and the text will be displayed. ";
		Label headerLabel = new Label(text);
		headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		root.getChildren().add(headerLabel);

		text = "Note: Clicking the buttons with your mouse will not perform any action.";
		headerLabel = new Label(text);
		headerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 16));
		root.getChildren().add(headerLabel);

		// Create a textarea to display characters pressed by user.
		TextArea textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setFont(Font.font("Arial", FontWeight.NORMAL, 25));
		textArea.setPrefWidth(900);
		textArea.setOnKeyPressed(e -> selectButton(e));

		textArea.setOnKeyReleased(e -> {
			clearButtonSelection();
		});

		root.getChildren().add(textArea);
		VBox.setMargin(textArea, new Insets(0, 0, 20, 0));

		// An array containing the value on each key.
		String[] charaterSet = { "~", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "+", "Backspace", "Tab",
				"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\", "Caps", "A", "S", "D", "F", "G", "H",
				"J", "K", "L", ":", "\"", "Enter", "Shift", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "?", "^", " ",
				"<", " v ", ">" };

		// Set up the keys
		buttons = new ArrayList<>();
		HBox buttonPane = new HBox();
		for (int index = 0; index < charaterSet.length; index++) {
			String str = charaterSet[index];
			KeyButton button = new KeyButton(str);
			button.setFocusTraversable(false);
			button.setStyle(defaultButtonStyle);
			buttonPane.getChildren().add(button);
			buttons.add(button);

			if (str.equals(" ")) {
				HBox.setMargin(button, new Insets(0, 90, 0, 240));
			} else if (str.equals("^")) {
				HBox.setMargin(button, new Insets(0, 0, 0, 30));
			}

			// There are 14 keys on the first and second row while third and fourth row have
			// 13 and 12 respectively
			if (index == 13 || index == 27 || index == 40 || index == 52) {
				root.getChildren().add(buttonPane);
				buttonPane = new HBox();
			}
		}
		// Add the remaining buttons to the root.
		root.getChildren().add(buttonPane);

		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.setResizable(false);
		stage.setTitle("Typing Tutor");
		stage.show();
	}

	private void clearButtonSelection() {
		for (Button btn : buttons) {
			btn.setStyle(defaultButtonStyle);
		}
	}

	private void selectButton(KeyEvent e) {
		String str;
		switch (e.getCode()) {
		case CAPS:
			str = "Caps";
			break;
		case BACK_SPACE:
			str = "Backspace";
			break;
		case TAB:
			str = "Tab";
			break;
		case SHIFT:
			str = "Shift";
			break;
		case BACK_QUOTE:
			str = "~";
			break;
		case ENTER:
			str = "Enter";
			break;
		case EQUALS:
			str = "+";
			break;
		case SEMICOLON:
			str = ":";
			break;
		case QUOTE:
			str = "\"";
			break;
		case SLASH:
			str = "?";
			break;
		case UP:
			str = "^";
			break;
		case DOWN:
			str = " v ";
			break;
		case LEFT:
			str = "<";
			break;
		case RIGHT:
			str = ">";
			break;
		default:
			str = e.getText();
			break;
		}

		for (Button btn : buttons) {
			if (btn.getText().equalsIgnoreCase(str)) {
				btn.setStyle("-fx-color: lightgreen");
			}
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	// A nested class to configure button.
	private static class KeyButton extends Button {

		public KeyButton(String str) {
			super(str);
			setPrefHeight(60);
			switch (str) {
			case "Backspace":
			case "Enter":
			case "Shift":
				setPrefWidth(120);
				break;
			case "Tab":
			case "Caps":
				setPrefWidth(90);
				break;
			case " ":
				setPrefWidth(360);
				break;
			default:
				setPrefWidth(60);
				break;
			}
		}
	}
}
