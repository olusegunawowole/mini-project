package application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;

public class Main extends Application {
	private VBox homePane;
	private VBox gamePane;
	private StackPane container;
	private StackPane infoPane;
	private Label infoLabel;
	private final int GRID_SIZE = 5;
	public final static String PROJECT_FILE_PATH = "C:\\Users\\peace\\eclipse-workspace\\";
	private ArrayList<Cell> cells;
	private ImageList[] imageCategoryList = new ImageList[4];
	private String[] imageUrls = new String[8];
	private SimpleTimer clockTimer;
	private int clickCount;
	private int matchCount;
	private int previousSelectionId = -1;
	private int categoryIndex;;
	private boolean clickMaxed;
	private boolean gameInProgress;
	private File dataFile = new File(PROJECT_FILE_PATH + "memory\\resource\\data.xml");

	@Override
	public void start(Stage primaryStage) {
		StackPane root = new StackPane(container);
		root.setPadding(new Insets(5));
		root.setStyle("-fx-background-color: black");
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.sizeToScene();
		primaryStage.show();
		String path = "file:///" + PROJECT_FILE_PATH + "memory\\resource\\0.png";
		Image icon = new Image(path);
		primaryStage.getIcons().add(icon);
		primaryStage.setTitle("Memory Game");
		primaryStage.setOnCloseRequest(e -> saveGameData());
	}

	public void init() {
		homePaneSetup();
		gamePaneSetup();
		readGameData();
	}

	private void gamePaneSetup() {
		infoLabel = new Label("Image Collections");
		infoLabel.setStyle("-fx-text-fill: black");
		infoLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

		infoPane = new StackPane(infoLabel);
		infoPane.setStyle("-fx-background-color: lightblue; -fx-border-width: 2 2 0 2; -fx-border-color: gray");
		infoPane.setPadding(new Insets(10));

		cells = new ArrayList<>();
		GridPane gridPane = new GridPane();
		gridPane.setStyle("-fx-background-color: lightblue; -fx-border-width: 1px; -fx-border-color: gray");
		String path = "file:///" + PROJECT_FILE_PATH + "memory\\resource\\";
		int index = 0;
		for (int col = 0; col < GRID_SIZE; col++) {
			for (int row = 0; row < GRID_SIZE; row++) {
				if (col == GRID_SIZE - 1 && row == GRID_SIZE - 1) {// Last cell
					String imgUrl = path + "home.png";
					Cell cell = new Cell(imgUrl);
					gridPane.add(cell, col, row);
					cells.add(cell);
					cell.setOnMouseClicked(e -> {
						flipPane(homePane);
						if(gameInProgress)
							clockTimer.stop();
					});
				} else {
					String imgUrl = path + "0.png";
					Cell cell = new Cell(imgUrl);
					gridPane.add(cell, col, row);
					cells.add(cell);
					int selectedIndex = index;
					cell.setOnMouseClicked(e -> {
						makeSelection(selectedIndex);
					});
				}
				index++;
			}
		}
		clockTimer = new SimpleTimer();
		gamePane = new VBox(clockTimer, gridPane);
		gamePane.setMaxWidth(712);
		gamePane.setMaxHeight(757);
	}

	private void homePaneSetup() {
		Label header = new Label("Memory Game");
		header.setStyle("-fx-text-fill: black");
		header.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 75));

		Label collectionHeader = new Label("Image Collections");
		collectionHeader.setStyle("-fx-text-fill: black");
		collectionHeader.setFont(Font.font("Arial", FontWeight.NORMAL, 25));

		HBox topPane = new HBox(10);
		topPane.setAlignment(Pos.CENTER);
		imageCategoryList[0] = new ImageList(ImageCategory.DOG);
		imageCategoryList[1] = new ImageList(ImageCategory.HOME);
		imageCategoryList[2] = new ImageList(ImageCategory.FRUIT);
		imageCategoryList[3] = new ImageList(ImageCategory.FISH);

		for (int index = 0; index < imageCategoryList.length; index++) {
			ImageList list = imageCategoryList[index];
			topPane.getChildren().add(list.getThumbnail());
			int selectedIndex = index;
			list.setOnAction(e -> {
				selectImageCategory(selectedIndex);
			});
		}
		selectImageCategory(0);
		Button newGameBtn = createButton("New Game");
		newGameBtn.setOnAction(e -> {
			createNewGame();
			clockTimer.start();
			flipPane(gamePane);
		});

		Button revealBtn = createButton("Reveal");
		revealBtn.setOnAction(e -> {
			clockTimer.stop();
			gameInProgress = false;
			for (Cell cell : cells) {
				if (cell.getGroupId() < 0)
					continue;
				cell.flip();
				flipPane(gamePane);
			}
		});
		Button backBtn = createButton("Back to Game");
		backBtn.setOnAction(e -> {
			flipPane(gamePane);
			if(gameInProgress)
				clockTimer.start();
		});

		HBox btnPane = new HBox(10, newGameBtn, revealBtn, backBtn);
		btnPane.setPadding(new Insets(15, 0, 0, 0));
		btnPane.setAlignment(Pos.CENTER);

		homePane = new VBox(10);
		homePane.setAlignment(Pos.CENTER);
		homePane.setStyle("-fx-background-color: lightblue; -fx-border-width: 1px; -fx-border-color: gray");
		homePane.setMaxSize(712, 757);
		homePane.setMinSize(712, 757);
		homePane.getChildren().addAll(header, collectionHeader, topPane, btnPane);
		container = new StackPane(homePane);
	}

	private void flipPane(Pane pane) {
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				container.getChildren().clear();
				container.getChildren().add(pane);
				RotateTransition rotator = new RotateTransition(Duration.millis(100), container);
				rotator.setAxis(Rotate.Y_AXIS);
				rotator.setFromAngle(90);
				rotator.setToAngle(0);
				rotator.setInterpolator(Interpolator.EASE_IN);
				rotator.play();
			}
		};

		RotateTransition rotator = new RotateTransition(Duration.millis(100), container);
		rotator.setAxis(Rotate.Y_AXIS);
		rotator.setFromAngle(0);
		rotator.setToAngle(90);
		rotator.setInterpolator(Interpolator.EASE_IN);
		rotator.setOnFinished(onFinished);
		rotator.play();
	}

	private void makeSelection(int index) {
		if (!gameInProgress) {
			infoLabel.setText("No game in progress.");
			gamePane.getChildren().set(0, infoPane);
			return;
		}

		if (clickMaxed)// User has clicked 3 times without making a complete match.
			return;
		Cell cell = cells.get(index);
		if (cell.isComplete() || cell.isOpen())
			return; // Image already flipped
		clickCount++;
		cells.get(index).flip();
		if (previousSelectionId == -1 || previousSelectionId == cell.getGroupId()) {
			matchCount++;
			previousSelectionId = cell.getGroupId();
		} else {
			matchCount = 0;
		}

		if (matchCount == 3) {
			lockCells(cell.getGroupId());
			clickCount = 0;
			previousSelectionId = -1;
			matchCount = 0;
			gameOver();// Check if game is over
		}

		if (clickCount == 3) {
			reset();
		}
	}
	
	private void reset() {
		clickMaxed = true;
		freezeCell();
		infoLabel.setText("Clicked count maxed. There will be 5-second delay.");
		gamePane.getChildren().set(0, infoPane);
		new Thread() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Platform.runLater(() -> {
					for (Cell cell : cells) {
						if (cell.isOpen() && !cell.isComplete()) {
							cell.reset();
							clickCount = 0;
							previousSelectionId = -1;
							matchCount = 0;
							clickMaxed = false;
							unFreezeCell();
							gamePane.getChildren().set(0, clockTimer);
						}
					}

				});
			}

		}.start();
	}

	private void lockCells(int id) {
		for (Cell cell : cells) {
			if (cell.getGroupId() == id) {
				cell.setComplete(true);
			}
		}
	}

	// This method disables mouseOver effects.
	private void freezeCell() {
		for (Cell cell : cells) {
			if (cell.getGroupId() < 0)
				continue;
			cell.setFrozen(true);
		}
	}

	// This method enables mouseOver effects.
	private void unFreezeCell() {
		for (Cell cell : cells) {
			if (cell.getGroupId() < 0)
				continue;
			cell.setFrozen(false);
		}
	}

	private void selectImageCategory(int selectedIndex) {
		for (int index = 0; index < imageCategoryList.length; index++) {
			if (index == selectedIndex) {
				imageCategoryList[index].select();
			} else {
				imageCategoryList[index].deselect();
			}
		}
		imageUrls = imageCategoryList[selectedIndex].getUrls();
		categoryIndex = selectedIndex;
	}

	private void createNewGame() {
		ArrayList<Integer> randomNumbers = new ArrayList<>();
		for (int index = 0; index < 8; index++) {
			randomNumbers.add(index);
			randomNumbers.add(index);
			randomNumbers.add(index);
		}
		// Shuffle randNumbers
		for (int index = randomNumbers.size() - 1; index > 0; index--) {
			int randIndex = (int) (Math.random() * (index + 1));
			int temp = randomNumbers.get(randIndex);
			randomNumbers.set(randIndex, randomNumbers.get(index));
			randomNumbers.set(index, temp);
		}

		for (int index = 0; index < cells.size() - 1; index++) {
			Cell cell = cells.get(index);
			cell.reset();
			int id = randomNumbers.get(index);
			cell.setImageUrl(imageUrls[id]);
			cell.setGroupId(id);
		}
		clockTimer.reset();
		gamePane.getChildren().set(0, clockTimer);
		gameInProgress = true;
	}

	// check if game over
	private void gameOver() {
		boolean isGameOver = true;
		for (Cell cell : cells) {
			if (cell.getGroupId() < 0)
				continue;
			if (!cell.isComplete()) { // Some images are yet to be flipped
				isGameOver = false;
				break;
			}
		}
		if (isGameOver) {
			if (clockTimer.isBestTime()) {
				clockTimer.setBestTime();
			}
			String info = "Game over: Game completed in " + clockTimer.toString() + ".";
			infoLabel.setText(info);
			gamePane.getChildren().set(0, infoPane);
			gameInProgress = false;
		}

	}

	private void readGameData() {
		try {
			if (dataFile == null || !dataFile.exists()) {
				throw new FileNotFoundException("Data file not found.");
			}
			Document xmldoc;
			DocumentBuilder docReader = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			xmldoc = docReader.parse(dataFile);
			Element rootElement = xmldoc.getDocumentElement();
			if (!rootElement.getNodeName().equals("memory"))
				throw new Exception("Data file is invalid.");
			String version = rootElement.getAttribute("version");
			double versionNumber = Double.parseDouble(version);
			if (versionNumber > 1.0)
				throw new Exception("Data file requires a newer version of memory.");

			NodeList nodes = rootElement.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				if (nodes.item(i) instanceof Element) {
					Element element = (Element) nodes.item(i);
					if (element.getTagName().equals("click-count")) {
						clickCount = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("previous-selected-id")) {
						previousSelectionId = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("match-count")) {
						matchCount = Integer.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("click-maxed")) {
						clickMaxed = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("game-in-progress")) {
						gameInProgress = Boolean.valueOf(element.getAttribute("value"));
					} else if (element.getTagName().equals("category-index")) {
						categoryIndex = Integer.valueOf(element.getAttribute("value"));
						selectImageCategory(categoryIndex);
					} else if (element.getTagName().equals("timer-best-time")) {
						clockTimer.setBestTime(Long.valueOf(element.getAttribute("value")));
					} else if (element.getTagName().equals("timer-time")) {
						clockTimer.setTime(Long.valueOf(element.getAttribute("value")));
					} else if (element.getTagName().equals("grid-cell")) {
						NodeList cellNodeList = element.getChildNodes();
						for (int j = 0; j < cellNodeList.getLength(); j++) {
							if (cellNodeList.item(j) instanceof Element) {
								Element cellElement = (Element) cellNodeList.item(j);
								int cellIndex = Integer.valueOf(cellElement.getAttribute("index"));
								Cell cell = cells.get(cellIndex);
								cell.setGroupId(Integer.valueOf(cellElement.getAttribute("group-id")));
								cell.setOpen(Boolean.valueOf(cellElement.getAttribute("open")));
								cell.setComplete(Boolean.valueOf(cellElement.getAttribute("complete")));
								cell.setFrozen(Boolean.valueOf(cellElement.getAttribute("frozen")));
								if (cell.getGroupId() > -1)
									cell.setImageUrl(imageUrls[cell.getGroupId()]);
								if (cell.isOpen() || cell.isComplete()) {
									cell.flip();
								}
								if(clickCount == 3) {
									reset();
								}
							}
						}
					}

				}

			}

		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
			createNewGame();
		}

	}

	private void saveGameData() {
		try {
			PrintWriter out = new PrintWriter(new FileWriter(dataFile));
			out.println("<?xml version=\"1.0\"?>");
			out.println("<memory version=\"1.0\">");
			out.println("  <click-count value='" + clickCount + "' />");
			out.println("  <previous-selected-id value='" + previousSelectionId + "' />");
			out.println("  <match-count value='" + matchCount + "' />");
			out.println("  <click-maxed value='" + clickMaxed + "' />");
			out.println("  <game-in-progress value='" + gameInProgress + "' />");
			out.println("  <category-index value='" + categoryIndex + "' />");
			out.println("  <timer-best-time value='" + clockTimer.getBestTime() + "' />");
			out.println("  <timer-time value='" + clockTimer.getTime() + "' />");
			out.println("  <grid-cell>");
			for (int index = 0; index < cells.size(); index++) {
				Cell cell = cells.get(index);
				out.print("    <cell index='" + index + "' ");
				out.print("group-id='" + cell.getGroupId() + "' ");
				out.print("open='" + cell.isOpen() + "' ");
				out.print("complete='" + cell.isComplete() + "' ");
				out.print("frozen='" + cell.isFrozen() + "' />");
				out.println();
			}
			out.println("  </grid-cell>");
			out.println("</memory>");
			out.close();

		} catch (Exception e) {

		}

	}

	private Button createButton(String text) {
		Button btn = new Button(text);
		btn.setPrefWidth(150);
		String style = "-fx-background-color: transparent; -fx-color: rgb(255, 255, 255); -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: black; -fx-cursor: hand";
		btn.setStyle(style);
		btn.setOnMouseEntered(e -> btn.setStyle(
				"-fx-background-color: black; -fx-color: rgb(0, 0, 0); -fx-border-width: 1px; -fx-border-style: solid; -fx-border-color: black; -fx-cursor: hand"));
		btn.setOnMouseExited(e -> btn.setStyle(style));
		btn.setOnMousePressed(e -> {
			btn.setOpacity(0.6);
		});
		btn.setOnMouseReleased(e -> {
			btn.setOpacity(1.0);
		});
		return btn;
	}

	public static void main(String[] args) {
		launch(args);
	}
}
