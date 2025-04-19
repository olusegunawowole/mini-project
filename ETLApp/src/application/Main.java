package application;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Category;
import model.Product;
import model.Store;
import service.ETL;
import service.OracleDB;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class Main extends Application {
	enum DataType {
		CATEGORY, PRODUCT, STORE
	};

	private BorderPane root;
	private StackPane centerPane;
	private Stage mainStage;
	private File selectedFile;
	private MenuBar menuBar;

	private ProductQueryPane productQueryPane;
	private StoreQueryPane storeQueryPane;
	private ModalDialog modalDialog;
	private OracleDB db;

	@Override
	public void start(Stage primaryStage) {
		try {
			db = new OracleDB();
			productQueryPane = new ProductQueryPane(db);
			storeQueryPane = new StoreQueryPane(db);
			menuBar = createMenuBar();
			modalDialog = new ModalDialog();
			centerPane = new StackPane(productQueryPane, modalDialog);

			root = new BorderPane();
			root.setStyle("-fx-background-color: white; -fx-border-width: 2px; -fx-border-color: #0096C9");
			root.setTop(menuBar);
			root.setCenter(centerPane);

			Scene scene = new Scene(root, 1200, 800);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			mainStage = primaryStage;
			primaryStage.setTitle("ETL App");
			primaryStage.setScene(scene);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates and returns a menu bar that contains controls of the application
	 */
	private MenuBar createMenuBar() {
		Menu menu = new Menu("Menu");

		MenuItem findProduct = new MenuItem(String.format("%-20s", "Find Product"));
		findProduct.setOnAction(e -> {
			centerPane.getChildren().setAll(productQueryPane, modalDialog);
		});
		MenuItem findStore = new MenuItem("Find Store");
		findStore.setOnAction(e -> {
			centerPane.getChildren().setAll(storeQueryPane, modalDialog);
		});
		menu.getItems().addAll(findProduct, findStore);
		menu.getItems().add(new SeparatorMenuItem());

		MenuItem importCategory = new MenuItem("Import Category Data");
		importCategory.setOnAction(e -> importData(DataType.CATEGORY));

		MenuItem importProduct = new MenuItem("Import Product Data");
		importProduct.setOnAction(e -> importData(DataType.PRODUCT));

		MenuItem importStore = new MenuItem("Import Store Data");
		importStore.setOnAction(e -> importData(DataType.STORE));

		menu.getItems().addAll(importCategory, importProduct, importStore);
		menu.getItems().add(new SeparatorMenuItem());
		MenuItem quit = new MenuItem("Quit");
		quit.setOnAction(e -> System.exit(0));
		menu.getItems().addAll(quit);

		MenuBar menuBar = new MenuBar(menu);

		return menuBar;
	}

	private void importData(DataType dataType) {
		FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("JSON Files", "*.json");
		FileChooser fileDialog = new FileChooser();
		fileDialog.getExtensionFilters().add(fileFilter);

		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
		fileDialog.setInitialFileName("Summary_" + LocalDateTime.now().format(df) + ".json");

		if (selectedFile == null) {
			fileDialog.setInitialDirectory(new File(System.getProperty("user.home")));
		} else {
			fileDialog.setInitialDirectory(selectedFile.getParentFile());
		}

		fileDialog.setTitle("Select File");
		File selectedFile = fileDialog.showOpenDialog(mainStage);
		if (selectedFile == null)
			return; // User did not select a file.
		ProgressIndicator indicator = new ProgressIndicator();
		modalDialog.open(indicator);
		menuBar.setDisable(true);
		new Thread() {
			int size;
			int loaded;

			public void run() {
				try {
					ETL etl = new ETL();
					if (dataType == DataType.CATEGORY) {
						Set<Category> categories = etl.extractCategories(selectedFile);
						size = categories.size();
						loaded = etl.loadCategories(categories, indicator);
					} else if (dataType == DataType.PRODUCT) {
						Set<Product> products = etl.extractProducts(selectedFile);
						size = products.size();
						loaded = etl.loadProducts(products, indicator);
					} else if (dataType == DataType.STORE) {
						Set<Store> stores = etl.extractStores(selectedFile);
						size = stores.size();
						loaded = etl.loadStores(stores, indicator);
					}
					Platform.runLater(() -> {
						String str = String.format("Loading completed...\n%,d of %,d records saved.", loaded, size);
						Alert alert = new Alert(str, "Cancel");
						modalDialog.open(alert);
						alert.setButtonOnAction(e -> {
							menuBar.setDisable(false);
							modalDialog.close();
						}, 0);
						productQueryPane.reset();
						storeQueryPane.reset();
					});
				} catch (Exception e) {
					Platform.runLater(() -> openErrorAlert(e.getMessage()));
					e.printStackTrace();
				}
			};
		}.start();
	}

	private void openErrorAlert(String message) {
		menuBar.setDisable(true);
		StringBuilder sb = new StringBuilder("Encountered error when tried to load data: ");
		sb.append(message);
		Alert alert = new Alert(sb.toString(), "Cancel");
		alert.setButtonOnAction(e -> {
			menuBar.setDisable(false);
			modalDialog.close();
		}, 0);
		modalDialog.open(alert);

	}

	public static void main(String[] args) {
		launch(args);
	}
}
