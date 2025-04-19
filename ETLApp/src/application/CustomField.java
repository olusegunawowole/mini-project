package application;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Callback;
import javafx.util.StringConverter;

public class CustomField extends VBox {

	private FilterComboBox comboBox;
	private TextField textField;
	private TextField backgroundField;
	private BooleanProperty edit;
	private Label errLabel;
	private boolean dropDown;
	private boolean error;
	private String errorLabelColor = "#ffefef";
	private String value;
	private String title;
	private Label label;
	private boolean isDate;
	private boolean telephoneFormat;
	private boolean autoFormat;
	private DatePicker datePicker;
	private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private String dateFormat = "MM-DD-YYYY";
	private static final String NIGERIA = "+234";
	private static final String UK = "+44";
	private BooleanProperty errorProperty;

	public CustomField() {
		errorProperty = new SimpleBooleanProperty(false);
		label = new Label(title);
		label.setStyle("-fx-text-fill: black");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

		errLabel = new Label();
		errLabel.setStyle("-fx-text-fill: " + errorLabelColor);
		errLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 9));

		String style = "-fx-border-width: 1px;-fx-border-style: solid inside; -fx-border-color: lightgray; -fx-background-color: white";

		textField = new TextField();
		textField.setText(value);
		textField.setPrefWidth(200);
		textField.setPrefHeight(27);
		textField.setStyle(style);
		textField.setEditable(true);
		textField.setPromptText(title);

		backgroundField = new TextField();
		backgroundField.setPrefWidth(200);
		backgroundField.setPrefHeight(27);
		backgroundField.setStyle(style);
		backgroundField.setEditable(false);
		backgroundField.setPromptText(title);

		telephoneFormat = true;

		edit = new SimpleBooleanProperty();
		edit.addListener((obj, oldVal, newVal) -> {
			formatTextAsTelNumber();
		});

		VBox.setMargin(label, new Insets(0, 0, 2, 2));
		getChildren().addAll(label, backgroundField, errLabel);
		widthProperty().addListener(e -> {
			double width = getWidth();
			textField.setPrefWidth(width);
			backgroundField.setPrefWidth(width);
		});
		textField.setOnKeyReleased(e -> {
			if (e.getCode().isArrowKey())
				return;
			if (telephoneFormat && autoFormat) {
				String text = textField.getText();
				textField.setText(autoFormat(text));
				textField.positionCaret(textField.getText().length());
			}
		});
	}

	public CustomField(String title) {
		errorProperty = new SimpleBooleanProperty(false);
		this.title = title;
		isDate = true;
		label = new Label(title);
		label.setStyle("-fx-text-fill: black");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

		errLabel = new Label();
		errLabel.setStyle("-fx-text-fill: " + errorLabelColor);
		errLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 9));

		StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
			@Override
			public LocalDate fromString(String str) {
				if (str == null || str.isEmpty()) {
					return null;
				}
				return LocalDate.parse(str, formatter);
			}

			@Override
			public String toString(LocalDate date) {
				if (date == null)
					return "";
				return formatter.format(date);
			}

		};

		String style = "-fx-border-width: 1px;-fx-border-style: solid inside; -fx-border-color: lightgray; -fx-background-color: white";

		datePicker = new DatePicker();
		datePicker.setPrefWidth(200);
		datePicker.setPrefHeight(27);
		datePicker.setStyle(style);
		datePicker.setConverter(converter);
		datePicker.setPromptText(dateFormat);
		datePicker.setEditable(false);

		textField = new TextField();
		textField.setPrefWidth(200);
		textField.setPrefHeight(27);
		textField.setStyle(style);
		textField.setPromptText(dateFormat);
		textField.setEditable(false);

		edit = new SimpleBooleanProperty();
		edit.addListener((obj, oldVal, newVal) -> {
			if (newVal) {
				getChildren().set(1, datePicker);
			} else {
				LocalDate ld = (LocalDate) datePicker.getValue();
				if (ld != null)
					textField.setText(formatter.format(ld));
				getChildren().set(1, textField);
			}

		});

		VBox.setMargin(label, new Insets(0, 0, 2, 2));
		getChildren().addAll(label, textField, errLabel);
		widthProperty().addListener(e -> {
			double width = getWidth();
			textField.setPrefWidth(width);
			if (comboBox != null)
				comboBox.setPrefWidth(width);
			if (datePicker != null)
				datePicker.setPrefWidth(width);
		});
	}

	public CustomField(String title, String value) {
		this(title, value, false);
	}

	public CustomField(String title, String value, boolean dropDown, String... options) {
		errorProperty = new SimpleBooleanProperty(false);
		label = new Label(title);
		label.setStyle("-fx-text-fill: black");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

		errLabel = new Label();
		errLabel.setStyle("-fx-text-fill: " + errorLabelColor);
		errLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 9));

		String style = "-fx-border-width: 1px;-fx-border-style: solid inside; -fx-border-color: lightgray; -fx-background-color: white";

		textField = new TextField();
		textField.setText(value);
		textField.setPrefWidth(200);
		textField.setPrefHeight(27);
		textField.setStyle(style);
		textField.setEditable(false);
		textField.setPromptText(title);

		this.value = value;
		this.dropDown = dropDown;
		this.title = title;

		if (dropDown) {
			comboBox = new FilterComboBox(options);// new ComboBox<String>(FXCollections.observableArrayList(options));
			comboBox.getSelectionModel().select(value);
			comboBox.setStyle(style);
			comboBox.setPrefWidth(200);
			comboBox.setPromptText(title);
			//textField.setMaxWidth(200);
		}
		edit = new SimpleBooleanProperty();
		edit.addListener((obj, oldVal, newVal) -> {
			if (dropDown) {
				if (newVal) {
					getChildren().set(1, comboBox);
				} else {
					getChildren().set(1, textField);
					String selectedVal = comboBox.getSelectionModel().getSelectedItem();
					if (selectedVal == null || selectedVal.equalsIgnoreCase(this.value))
						return;
					this.value = selectedVal;
					textField.setText(this.value);
				}
			} else {
				textField.setEditable(newVal);
			}
		});

		VBox.setMargin(label, new Insets(0, 0, 2, 2));
		getChildren().addAll(label, textField, errLabel);
		widthProperty().addListener(e -> {
			double width = getWidth();
			textField.setPrefWidth(width);
			if (comboBox != null)
				comboBox.setPrefWidth(width);
			if (datePicker != null)
				datePicker.setPrefWidth(width);
		});
	}

	public void setEdit(boolean value) {
		edit.set(value);
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}

	public String getDateFormat() {
		return dateFormat;
	}

	public void setCustomMaxWidth(double width) {
		widthProperty().addListener(e -> {
			setCustomWidth(width);
		});
	}

	public void setCustomWidth(double width) {
		textField.setPrefWidth(width);
		textField.setMinWidth(width);
		if (dropDown) {
			comboBox.setPrefWidth(width);
			comboBox.setMinWidth(width);
		}
		if (isDate) {
			datePicker.setPrefWidth(width);
			datePicker.setMinWidth(width);
		}
	}

	public void setValue(LocalDate localDate) {
		if (isDate) {
			datePicker.setValue(localDate);
			textField.setText(formatter.format(localDate));
		}
	}

	public void setValue(String value, String country) {
		if (telephoneFormat) {
			textField.setText(value);
			formatTextAsTelNumber();
		} else {
			setValue(value);
		}
	}

	public void setValue(String value) {
		textField.setText(value);
		if (telephoneFormat)
			formatTextAsTelNumber();
		if (dropDown) {
			comboBox.getSelectionModel().select(value);
		}
		if (isDate) {
			datePicker.setValue(LocalDate.parse(value, formatter));
			textField.setText(formatter.format(LocalDate.parse(value, formatter)));
		}
	}

	public LocalDate getValueAsDate() {
		return (datePicker.getValue());
	}

	public String getValue() {
		if (telephoneFormat) {
			boolean isNull = textField.getText() == null;
			if (isNull)
				return textField.getText();
			return textField.getText().replaceAll("[\\s\\(\\)-]", "").trim();
		}
		if (isDate) {
			LocalDate date = (LocalDate) (datePicker.getValue());
			if (date == null)
				return "";
			else
				return formatter.format(date);
		}
		if (!dropDown) {
			boolean isNull = textField.getText() == null;
			if (isNull)
				return textField.getText();
			return textField.getText().trim();
		}
		return comboBox.getSelectionModel().getSelectedItem();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
		label.setText(title);
	}

	public String getErrorMessage() {
		return errLabel.getText();
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setError(boolean err, String errMsg) {
		errorProperty.set(err);
		this.error = err;
		if (isDate) {
			if (err) {
				datePicker.getEditor().setStyle("-fx-background-color: #FADADD");
				datePicker.setStyle("-fx-border-color:red");
				errLabel.setText(errMsg);
			} else {
				datePicker.getEditor().setStyle("-fx-background-color: white");
				datePicker.setStyle("-fx-border-color: lightgray");
				errLabel.setText("");
			}
			return;
		}
		if (err) {
			textField.setStyle("-fx-border-color:red; -fx-background-color: #FADADD");
			errLabel.setText(errMsg);
		} else {
			textField.setStyle("-fx-border-color:lightgray; -fx-background-color: white");
			errLabel.setText("");
		}
		if (dropDown) {
			if (err) {
				comboBox.setStyle("-fx-border-color:red; -fx-background-color: #FADADD");
				errLabel.setText(errMsg);
			} else {
				comboBox.setStyle("-fx-border-color:lightgray; -fx-background-color: white");
				errLabel.setText("");
			}
		}
	}

	private void formatTextAsTelNumber() {
		if (edit.get()) {
			getChildren().set(1, textField);
		} else {
			setError(false);
			getChildren().set(1, backgroundField);
			String text = textField.getText();
			backgroundField.setText(autoFormat(text));
		}
	}

	private String autoFormat(String text) {
		// String str = text.replaceAll("[\\s\\(\\)-]", "").trim();
		if (text == null || text.isEmpty() || !Character.isDigit(text.charAt(text.length() - 1))) {
			return text;
		}
		String str = text.replaceAll("[\\s\\(\\)-]", "").trim();
		String regex = "^\\+?[0-9]*$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(str);
		char ch = text.charAt(0);
		if (matcher.find()) {
			switch (ch) {
			case '+':
				if (str.length() > 2 && str.length() < 6 && str.charAt(1) == '1') {
					return str.substring(0, 2) + " " + str.substring(2);
				} else if (str.length() >= 6 && str.length() < 9 && str.charAt(1) == '1') {
					return str.substring(0, 2) + " " + str.substring(2, 5) + " " + str.substring(5);
				} else if (str.length() >= 9 && str.length() < 13 && str.charAt(1) == '1') {
					return str.substring(0, 2) + " " + str.substring(2, 5) + " " + str.substring(5, 8) + " "
							+ str.substring(8);
				}
				// Nigeria +234 805 123 4567
				else if (str.length() > 4 && str.length() < 8 && str.substring(0, NIGERIA.length()).equals(NIGERIA)) {
					return str.substring(0, 4) + " " + str.substring(4);
				}

				else if (str.length() >= 8 && str.length() < 11 && str.substring(0, NIGERIA.length()).equals(NIGERIA)) {
					return str.substring(0, 4) + " " + str.substring(4, 7) + " " + str.substring(7);
				} else if (str.length() >= 11 && str.length() < 15
						&& str.substring(0, NIGERIA.length()).equals(NIGERIA)) {
					return str.substring(0, 4) + " " + str.substring(4, 7) + " " + str.substring(7, 10) + " "
							+ str.substring(10);
				}
				// UK
				else if (str.length() > 3 && str.length() < 8 && str.substring(0, UK.length()).equals(UK)) {
					return str.substring(0, 3) + " " + str.substring(3);
				}

				else if (str.length() >= 8 && str.length() < 14 && str.substring(0, UK.length()).equals(UK)) {
					return str.substring(0, 3) + " " + str.substring(3, 7) + " " + str.substring(7);
				}
				break;

			default:
				// With 1
				if (str.length() > 1 && str.length() < 6 && str.charAt(0) == '1') {
					return str.substring(0, 1) + " " + str.substring(1);
				} else if (str.length() >= 5 && str.length() < 8 && str.charAt(0) == '1') {
					return str.substring(0, 1) + " " + str.substring(1, 4) + " " + str.substring(4);
				} else if (str.length() >= 8 && str.length() < 12 && str.charAt(0) == '1') {
					return str.substring(0, 1) + " " + str.substring(1, 4) + " " + str.substring(4, 7) + " "
							+ str.substring(7);
				} else if (str.length() > 3 && str.length() < 8 && str.charAt(0) != '0')
					return str.substring(0, 3) + "-" + str.substring(3);
				else if (str.length() >= 8 && str.length() < 11 && str.charAt(0) != '0') {
					return "(" + str.substring(0, 3) + ") " + str.substring(3, 6) + "-" + str.substring(6);
				} else if (str.length() == 11 && str.charAt(0) == '0') {
					// (0803) 231-1234
					return "(" + str.substring(0, 4) + ") " + str.substring(4, 7) + "-" + str.substring(7);
				}

				else {
					return str;
				}
			}
		}
		return str;
	}

	public void clear() {
		if (textField != null)
			textField.clear();
		if (backgroundField != null)
			backgroundField.clear();
		if (dropDown)
			comboBox.getSelectionModel().clearSelection();
		if (datePicker != null)
			datePicker.setValue(null);
		value = "";
	}
	
	// Source: https://stackoverflow.com/questions/46351620/limit-date-range-to-datepicker-javafx-8
	public void restrictValue(LocalDate minDate, LocalDate maxDate) {
		if(datePicker == null) {
			return;
		}
	    final Callback<DatePicker, DateCell> dayCellFactory = new Callback<DatePicker, DateCell>() {
	        @Override
	        public DateCell call(final DatePicker datePicker) {
	            return new DateCell() {
	                @Override
	                public void updateItem(LocalDate item, boolean empty) {
	                    super.updateItem(item, empty);
	                     if (item.isBefore(minDate)) {
	                        setDisable(true);
	                        setStyle("-fx-background-color: #ffc0cb;");
	                    }else if (item.isAfter(maxDate)) {
	                        setDisable(true);
	                        setStyle("-fx-background-color: #ffc0cb;");
	                    }
	                }
	            };
	        }
	    };
	    datePicker.setDayCellFactory(dayCellFactory);
	}

//	public void setDatePattern() {
//		datePicker.setConverter(new StringConverter<LocalDate>() {
//			 String pattern = "yyyy-MM-dd";
//			 DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
//
//			 {
//			     datePicker.setPromptText(pattern.toLowerCase());
//			 }
//
//			 @Override public String toString(LocalDate date) {
//			     if (date != null) {
//			         return dateFormatter.format(date);
//			     } else {
//			         return "";
//			     }
//			 }
//
//			 @Override public LocalDate fromString(String string) {
//			     if (string != null && !string.isEmpty()) {
//			         return LocalDate.parse(string, dateFormatter);
//			     } else {
//			         return null;
//			     }
//			 }
//			});
//	}
	
	public ComboBox<String> getComboBox() {
		return comboBox;
	}

	public boolean hasOption() {
		return dropDown;
	}

	public TextField getTextField() {
		return textField;
	}

	public void setPromptText(String text) {
		if (textField != null)
			textField.setPromptText(text);
		if (comboBox != null)
			comboBox.setPromptText(text);
		if (datePicker != null)
			datePicker.setPromptText(text);
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		setError(error, "");
	}

	public boolean isEditable() {
		return edit.get();
	}

	public boolean isTelephoneFormat() {
		return telephoneFormat;
	}

	public void setTelephoneFormat(boolean telephoneFormat) {
		this.telephoneFormat = telephoneFormat;
	}

	public boolean isAutoFormat() {
		return autoFormat;
	}

	public void setAutoFormat(boolean autoFormat) {
		this.autoFormat = autoFormat;
	}

	public void setValueAndSelect(String value) {
		if (textField == null)
			return;
		textField.setText(value);
		textField.requestFocus();
		textField.selectAll();
	}

	public BooleanProperty errorProperty() {
		return errorProperty;
	}

	public void setFontColor(String color) {
		label.setStyle("-fx-text-fill: " + color);

	}
}
