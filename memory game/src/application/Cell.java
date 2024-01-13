package application;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

public class Cell extends StackPane {
	private boolean open; // Indicates whether the image is covered or not
	private boolean complete; // Indicates whether all the images in a group have been matched or not
	private boolean frozen; // Indicates whether the mouseOver effect is enabled or disabled.
	private Image coverImage; // Represents default image
	private Image image; // Image to display
	private int groupId = -1; // Represents related images
	private int index;
	private ImageView iv;

	public Cell(String coverImageUrl) {
		image = new Image(coverImageUrl, 100, 100, true, true);
		coverImage = new Image(coverImageUrl, 100, 100, true, true);
		iv = new ImageView();
		iv.setFitHeight(100);
		iv.setFitWidth(100);
		iv.setImage(coverImage);
		setStyle("-fx-border-width: 1px; -fx-border-color: gray");
		setCursor(Cursor.HAND);
		setPadding(new Insets(20));
		getChildren().add(iv);
		setOnMouseEntered(e -> {
			if (frozen || open || complete)
				return;
			setStyle("-fx-border-width: 1px; -fx-border-color: white");

		});
		setOnMouseExited(e -> {
			setStyle("-fx-border-width: 1px; -fx-border-color: gray");

		});

	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
		if (frozen)
			setStyle("-fx-border-width: 1px; -fx-border-color: gray");
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isComplete() {
		return complete;
	}

	public void setComplete(boolean complete) {
		this.complete = complete;
	}

	public void setImageUrl(String imgUrl) {
		image = new Image(imgUrl, 100, 100, true, true);
	}

	public void reset() {
		open = false;
		complete = false;
		frozen = false;
		flipImage();
		setCursor(Cursor.HAND);

	}

	public void flip() {
	//	if (complete || open || image == null)
		//	return;
		flipImage();
		open = true;
		setCursor(Cursor.DEFAULT);
		setStyle("-fx-border-width: 1px; -fx-border-color: gray");
	}

	private void flipImage() {
		EventHandler<ActionEvent> onFinished = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (!open)
					iv.setImage(coverImage);
				else
					iv.setImage(image);
				RotateTransition rotator = new RotateTransition(Duration.millis(100), iv);
				rotator.setAxis(Rotate.Y_AXIS);
				rotator.setFromAngle(90);
				rotator.setToAngle(0);
				rotator.setInterpolator(Interpolator.EASE_IN);
				rotator.play();
			}
		};

		RotateTransition rotator = new RotateTransition(Duration.millis(100), iv);
		rotator.setAxis(Rotate.Y_AXIS);
		rotator.setFromAngle(0);
		rotator.setToAngle(90);
		rotator.setInterpolator(Interpolator.EASE_IN);
		rotator.setOnFinished(onFinished);
		rotator.play();
	}
}
