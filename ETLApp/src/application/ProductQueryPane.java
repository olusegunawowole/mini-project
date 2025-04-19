package application;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.Category;
import model.Product;
import service.OracleDB;

public class ProductQueryPane extends HBox {
	// Panes
	private VBox resultPane; // Results pane - for displaying results
	private VBox queryPane; // Query pane - for query options
	private Accordion accordion; // For filter options
	private TitledPane brandPane; // For list of brands
	private StackPane brandPlaceholderPane;
	private StackPane tablePane;
	private TableView<Product> productTableView;
	private Label resultLabel;

	private ListView<String> brandView;

	private CustomField categoryField;
	private CustomField minPriceField;
	private CustomField maxPriceField;
	private FlatButton searchButton;
	private FlatButton resetButton;
	private ModalDialog dialog;
	private OracleDB db;
	
	private Category[] categories;

	public ProductQueryPane(OracleDB db) {
		this.db = db;
		dialog = new ModalDialog();
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
		resultLabel = new Label();
		resultLabel.setStyle("-fx-text-fill: #FFFFFF");
		resultLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
		StackPane labelPane = new StackPane(resultLabel);
		labelPane.setPadding(new Insets(5));

		// Configure product tableView for displaying results
		productTableView = new TableView<>();

		TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		productTableView.getColumns().add(nameColumn);

		TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
		priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
		productTableView.getColumns().add(priceColumn);

		TableColumn<Product, String> brandColumn = new TableColumn<>("Brand");
		brandColumn.setCellValueFactory(new PropertyValueFactory<>("manufacturer"));
		productTableView.getColumns().add(brandColumn);

		productTableView.setStyle("-fx-background-color: white; -fx-border-width: 0 1 0 1; -fx-border-color: #E3E3E3");
		productTableView.setPlaceholder(new Label("No Content"));

		// productTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		tablePane = new StackPane(productTableView);
		VBox.setVgrow(tablePane, Priority.ALWAYS);

		nameColumn.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.50));
		priceColumn.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.20));
		brandColumn.prefWidthProperty().bind(productTableView.widthProperty().multiply(0.30));

		productTableView.setRowFactory(tv -> {
			TableRow<Product> row = new TableRow<>();
			row.setOnMouseClicked(event -> {
				if (event.getClickCount() == 2 && (!row.isEmpty())) {
					Product rowData = row.getItem();
					displayProduct(rowData);
				}
			});
			return row;
		});

		resultPane = new VBox(labelPane, tablePane);
		resultPane.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		resultPane.setPadding(new Insets(5));
		resultPane.setPadding(new Insets(5));
	}

	private void configureQueryPane() {
		categories = db.getCategories();

		categoryField = new CustomField("Category", null, true);
		categoryField.setEdit(true);
		categoryField.setFontColor("#FFFFFF");

		for (Category category : categories) {
			categoryField.getComboBox().getItems().add(category.getName());
		}

		minPriceField = new CustomField("Minimum Price", null);
		minPriceField.setPromptText("$");
		minPriceField.setEdit(true);
		minPriceField.setFontColor("#FFFFFF");

		maxPriceField = new CustomField("Maximum Price", null);
		maxPriceField.setPromptText("$");
		maxPriceField.setEdit(true);
		maxPriceField.setFontColor("#FFFFFF");

		brandView = new ListView<>();
		brandView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		brandView.setStyle("-fx-border-color: transparent; -fx-border-width: 2 0 0 0");
		brandView.setPrefHeight(200);

		searchButton = new FlatButton("Search");
		searchButton.setMinWidth(150);
		searchButton.setHoverStyle("#D3D3D3", "#000000", "#D3D3D3");
		searchButton.setDisable(true);

		resetButton = new FlatButton("Reset");
		resetButton.setMinWidth(150);
		resetButton.setHoverStyle("#D3D3D3", "#000000", "#D3D3D3");

		HBox buttonBox = new HBox(5, searchButton, resetButton);
		buttonBox.setPadding(new Insets(5, 0, 0, 0));
		buttonBox.setAlignment(Pos.CENTER);

		brandPlaceholderPane = new StackPane(new Label("No brand to display"));
		brandPlaceholderPane
				.setStyle("-fx-border-color: #0096C9; -fx-border-width: 2 0 0 0; -fx-background-color: #FFFFFF");
		brandPlaceholderPane.setPrefHeight(200);

		brandPane = new TitledPane("Brands", brandPlaceholderPane);
		accordion = new Accordion(brandPane);

		queryPane = new VBox(categoryField, minPriceField, maxPriceField,  accordion, buttonBox);
		queryPane.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		queryPane.setPadding(new Insets(5));
		queryPane.setMinWidth(315);

		minPriceField.getTextField().textProperty().addListener(e -> {
			validPriceRangeInput(minPriceField);
			enableSubmission();
		});

		maxPriceField.getTextField().textProperty().addListener(e -> {
			validPriceRangeInput(maxPriceField);
			enableSubmission();
		});

		categoryField.getComboBox().getSelectionModel().selectedIndexProperty().addListener(e -> {
			int selectedIndex = categoryField.getComboBox().getSelectionModel().getSelectedIndex();
			if (selectedIndex < 0) {
				return;
			}
			String[] brands = db.getBrands(categories[selectedIndex].getId());
			brandView.getItems().setAll(brands);
			if (brandView.getItems().isEmpty()) {
				brandPane.setContent(brandPlaceholderPane);
			} else {
				brandPane.setContent(brandView);
			}
			enableSubmission();
		});

		brandView.getSelectionModel().selectedIndexProperty().addListener(e -> {
			int count = brandView.getSelectionModel().getSelectedIndices().size();
			String title = count == 0 ? "Brands" : "Brands (" + count + ")";
			brandPane.setText(title);
		});

		searchButton.setOnMouseClicked(e -> executeQuery());
		resetButton.setOnMouseClicked(e -> reset());
	}

	private void displayProduct(Product product) {
		VBox thumbnail = createProductThumbnail(product);
		FlatButton cancelButton = new FlatButton("Cancel");
		cancelButton.setOnMouseClicked(e -> dialog.close());
		cancelButton.setHoverStyle("#D3D3D3", "#000000", "#D3D3D3");

		VBox root = new VBox(30, thumbnail, cancelButton);
		root.setMaxSize(320, 500);
		root.setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2px; -fx-background-color: #0096C9");
		root.setAlignment(Pos.CENTER);

		tablePane.getChildren().setAll(productTableView, dialog);
		dialog.open(root);

	}

	public VBox createProductThumbnail(Product product) {
		Label nameLabel = new Label(product.getName());
		nameLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		nameLabel.setWrapText(true);

		Label descLabel = new Label(product.getDescription());
		descLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 15));
		descLabel.setWrapText(true);

		Label priceLabel = new Label(parseDouble(product.getPrice() + ""));
		priceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

		Image img = new Image(product.getImage());
		// Math.min(maxWidth / imageWidth, maxHeight / imageHeight)
		double ratio = Math.min(290 / img.getWidth(), 250 / img.getHeight());
		double w = img.getWidth() * ratio;
		double h = img.getHeight() * ratio;
		ImageView iv = new ImageView(img);
		iv.setFitHeight(h);
		iv.setFitWidth(w);
		StackPane imageRoot = new StackPane(iv);

		VBox.setVgrow(imageRoot, Priority.ALWAYS);
		VBox root = new VBox(10, imageRoot, nameLabel, descLabel, priceLabel);
		root.setMaxSize(300, 450);
		root.setPadding(new Insets(5));
		root.setStyle("-fx-background-color: #FFFFFF; -fx-border-width: 1px; -fx-border-color: lightgray");

		return root;
	}

	private void executeQuery() {
		queryPane.setDisable(true);
		searchButton.setName("Loading...");
		dialog.close();
		new Thread() {
			public void run() {
				String[] brands = brandView.getSelectionModel().getSelectedItems()
						.toArray(new String[brandView.getSelectionModel().getSelectedItems().size()]);
				Product[] products = db.getProducts(categoryField.getValue(), minPriceField.getValue(),
						maxPriceField.getValue(), brands);
				String text = products.length > 1 ? String.format("%,d products found", products.length)
						: String.format("%,d product found", products.length);
				Platform.runLater(() -> {
					productTableView.getItems().setAll(FXCollections.observableArrayList(products));
					resultLabel.setText(text);
					searchButton.setName("Search");
					queryPane.setDisable(false);
				});

			};
		}.start();

	}

	private void enableSubmission() {
		boolean disabled = false;
		String category = categoryField.getValue();
		if (category == null || category.isEmpty() || minPriceField.isError() || maxPriceField.isError()) {
			disabled = true;
		}
		searchButton.setDisable(disabled);
	}

	public void reset() {
		categoryField.clear();
		categories = db.getCategories();
		for (Category category : categories) {
			categoryField.getComboBox().getItems().add(category.getName());
		}
		minPriceField.clear();
		maxPriceField.clear();
		brandView.getItems().clear();
		productTableView.getItems().clear();
		resultLabel.setText(null);
		dialog.close();
		brandPane.setContent(brandPlaceholderPane);
	}

	private void validPriceRangeInput(CustomField field) {
		// Clear existing error messages if any
		field.setError(false);
		String reg = "^\\d*\\.?\\d*$";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(field.getValue());
		if (!m.find()) {
			field.setError(true, "Price contains invalid character");
		}
	}

	private String parseDouble(String value) {
		if (value == null)
			return value;
		Double numParsed = Double.parseDouble(value);
		return String.format("$%,.2f", numParsed);
	}
}
