package application;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Service;
import model.Store;
import service.OracleDB;

public class StoreQueryPane extends HBox {
	private VBox resultPane; // Results pane - for displaying results
	private VBox queryPane; // Query pane - for query options
	private TableView<Store> storeTableView;
	private TextArea textArea;
	private Label resultLabel;

	private CustomField stateField;
	private CustomField cityField;
	private CustomField typeField;
	private CustomField hoursField;
	private FlatButton searchButton;
	private FlatButton resetButton;
	private OracleDB db;

	public StoreQueryPane(OracleDB db) {
		this.db = db;
		// Configure panes
		configureQueryPane();
		configureResultPane();

		setSpacing(5);
		setPadding(new Insets(5));
		HBox.setHgrow(resultPane, Priority.ALWAYS);
		setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		getChildren().addAll(queryPane, resultPane);
	}

	private void configureResultPane() {
		resultLabel = new Label("");
		resultLabel.setStyle("-fx-text-fill: #FFFFFF");
		resultLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
		StackPane labelPane = new StackPane(resultLabel);
		labelPane.setPadding(new Insets(5));

		storeTableView = new TableView<>();

		TableColumn<Store, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		storeTableView.getColumns().add(nameColumn);

		TableColumn<Store, String> addressColumn = new TableColumn<>("Address");
		addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
		storeTableView.getColumns().add(addressColumn);

		TableColumn<Store, String> address2Column = new TableColumn<>("Address2");
		address2Column.setCellValueFactory(new PropertyValueFactory<>("address2"));
		storeTableView.getColumns().add(address2Column);

		TableColumn<Store, String> cityColumn = new TableColumn<>("City");
		cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
		storeTableView.getColumns().add(cityColumn);

		TableColumn<Store, String> stateColumn = new TableColumn<>("State");
		stateColumn.setCellValueFactory(new PropertyValueFactory<>("state"));
		storeTableView.getColumns().add(stateColumn);

		TableColumn<Store, String> zipcColumn = new TableColumn<>("Zip");
		zipcColumn.setCellValueFactory(new PropertyValueFactory<>("zip"));
		storeTableView.getColumns().add(zipcColumn);

		nameColumn.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.30));
		addressColumn.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.25));
		address2Column.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.10));
		cityColumn.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.15));
		stateColumn.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.05));
		zipcColumn.prefWidthProperty().bind(storeTableView.widthProperty().multiply(0.15));

		storeTableView.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 1; -fx-border-color: #E3E3E3");
		storeTableView.setPlaceholder(new Label("No Content"));

		hoursField = new CustomField("Hours", null);
		hoursField.setFontColor("#FFFFFF");

		textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setEditable(false);
		textArea.setPrefHeight(50);

		Label label = new Label("Services");
		label.setStyle("-fx-text-fill: #FFFFFF");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 12));

		VBox vBox = new VBox(hoursField, label, textArea);
		vBox.setPadding(new Insets(5, 0, 0, 0));

		VBox.setVgrow(storeTableView, Priority.ALWAYS);
		resultPane = new VBox(labelPane, storeTableView, vBox);
		resultPane.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		resultPane.setPadding(new Insets(5));

		storeTableView.getSelectionModel().selectedItemProperty().addListener(e -> {
			Store store = storeTableView.getSelectionModel().getSelectedItem();
			if (store == null) {
				return;
			}
			hoursField.setValue(store.getHours());
			StringBuilder sb = new StringBuilder();
			for (Service service : store.getServices()) {
				sb.append(service.getName()).append("; ");
			}
			if (sb.length() > 1) {
				sb.replace(sb.length() - 2, sb.length(), ".");
				textArea.setText(sb.toString());
			} else {
				textArea.clear();
			}

		});

	}

	private void configureQueryPane() {
		stateField = new CustomField("State", null, true, getStates());
		stateField.setEdit(true);
		stateField.setFontColor("#FFFFFF");
		stateField.setPromptText("Select State");

		cityField = new CustomField("City", null, true);
		cityField.setEdit(true);
		cityField.setFontColor("#FFFFFF");
		cityField.setPromptText("Select City");

		typeField = new CustomField("Store Type", "All", true, db.getStoreType());
		typeField.setEdit(true);
		typeField.setFontColor("#FFFFFF");
		typeField.setPromptText("Select Store Type");

		searchButton = new FlatButton("Search");
		searchButton.setMinWidth(150);
		searchButton.setHoverStyle("#D3D3D3", "#000000", "#D3D3D3");

		resetButton = new FlatButton("Reset");
		resetButton.setMinWidth(150);
		resetButton.setHoverStyle("#D3D3D3", "#000000", "#D3D3D3");

		HBox buttonBox = new HBox(5, searchButton, resetButton);
		buttonBox.setPadding(new Insets(5, 0, 0, 0));
		buttonBox.setAlignment(Pos.CENTER);

		queryPane = new VBox(stateField, cityField, typeField, buttonBox);
		queryPane.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		queryPane.setPadding(new Insets(5));
		queryPane.setMinWidth(315);

		stateField.getComboBox().getSelectionModel().selectedIndexProperty().addListener(e -> {
			String selectedItem = stateField.getValue();
			if (selectedItem == null) {
				return;
			}
			selectedItem = selectedItem.substring(0, selectedItem.indexOf("-")).trim();
			String[] cities = db.getCities(selectedItem);
			cityField.clear();
			cityField.getComboBox().getItems().setAll(FXCollections.observableArrayList(cities));
		});

		searchButton.setOnMouseClicked(e -> executeQuery());
		resetButton.setOnMouseClicked(e -> reset());
	}

	private void executeQuery() {
		queryPane.setDisable(true);
		searchButton.setName("Loading...");

		new Thread() {
			public void run() {
				String selectedState = null;
				if (stateField.getValue() != null) {
					selectedState = stateField.getValue().substring(0, stateField.getValue().indexOf("-")).trim();
				}
				Store[] stores = db.getStores(selectedState, cityField.getValue(), typeField.getValue());
				String text = stores.length > 1 ? String.format("%d stores found", stores.length)
						: String.format("%d store found", stores.length);
				Platform.runLater(() -> {
					searchButton.setName("Search");
					queryPane.setDisable(false);
					storeTableView.getItems().setAll(FXCollections.observableArrayList(stores));
					resultLabel.setText(text);
					hoursField.clear();
					textArea.clear();
				});

			};
		}.start();

	}

	public void reset() {
		stateField.clear();
		cityField.clear();
		typeField.getComboBox().getItems().setAll(db.getStoreType());
		typeField.getComboBox().getSelectionModel().select(0);
		hoursField.clear();
		textArea.clear();
		storeTableView.getItems().clear();
		resultLabel.setText(null);
	}

	// Read US states from file
	private String[] getStates() {
		ArrayList<String> arrayList = new ArrayList<>();
		try {
			Path filePath = Paths.get(System.getProperty("user.dir"), "src", "resource", "us_states.txt");
			BufferedReader in = new BufferedReader(new FileReader(filePath.toFile()));
			String str = in.readLine();
			while (str != null) {
				arrayList.add(str);
				str = in.readLine();
			}
			in.close();
			return arrayList.toArray(new String[arrayList.size()]);

		} catch (Exception e) {
			e.printStackTrace();
			return new String[0];
		}
	}
}
