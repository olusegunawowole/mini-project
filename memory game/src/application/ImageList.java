package application;

import java.util.Arrays;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ImageList {
	private final double SIZE = 100;
	private ImageView imageView1;
	private ImageView imageView2;
	private StackPane root;
	private Image[] images = new Image[8];
	private String[] imageUrls = new String[8];
	private int slideIndex, frame;
	private final String path = "file:///" + Main.PROJECT_FILE_PATH + "memory\\resource\\";
	private ImageCategory category;
	private AnimationTimer timer;

	public ImageList(ImageCategory category) {
		// Images are numbered from 1 to 32. There are 8 images in a category.
		// start variable will be the first number in each category - 1, 9, 17 and 25
		int start = category.ordinal() * 8 + 1;
		for (int index = 0; index < 8; index++) {
			String url = path + start + ".png";
			imageUrls[index] = url;
			images[index] = new Image(url, SIZE, SIZE, true, true);
			start++;
		}
		imageView1 = new ImageView(images[0]);
		imageView2 = new ImageView(images[1]);
		root = new StackPane(imageView1, imageView2);
		root.setStyle("-fx-border-width: 1px; -fx-border-color: black");
		root.setMaxSize(SIZE + 14, SIZE + 14);
		root.setPadding(new Insets(5));
		setSlideAnimation(false);
		root.setOnMouseEntered(e -> {
			timer.start();
		});
		root.setOnMouseExited(e -> {
			timer.stop();
			frame = 0;// reset to enable animation begin immediately on mouse entered
		});

		timer = new AnimationTimer() {	
			@Override
			public void handle(long now) {
				if (frame % 90 == 0) {
					setSlideAnimation(true);
				}
				frame++;
			}
		};
	}

	public void setImageCategory(ImageCategory category) {
		this.category = category;
	}

	public ImageCategory getImageCategory() {
		return category;
	}
	
	public String[] getUrls() {
		return Arrays.copyOf(imageUrls, imageUrls.length);
	}
	
	public StackPane getThumbnail() {
		return root;
	}

	private void setSlideAnimation(boolean play) {
		if (play) {
			imageView1.setImage(images[slideIndex % 8]);
			int nextSlideIndex = (slideIndex + 1) % 8;
			imageView2.setImage(images[nextSlideIndex]);
			slideIndex++;
			slideIndex = slideIndex > 7 ? 0 : slideIndex;
		}

		Rectangle clipRect = new Rectangle(SIZE, SIZE);
		clipRect.setTranslateX(0);
		imageView1.setClip(clipRect);
		imageView1.setTranslateX(0);

		Rectangle clipRect2 = new Rectangle(0, SIZE);
		clipRect2.translateXProperty().set(0);
		imageView2.setClip(clipRect2);
		imageView2.translateXProperty().set(SIZE);

		KeyValue kv1 = new KeyValue(clipRect.translateXProperty(), SIZE);
		KeyValue kv2 = new KeyValue(imageView1.translateXProperty(), -SIZE);
		KeyValue kv3 = new KeyValue(clipRect2.widthProperty(), SIZE);
		KeyValue kv4 = new KeyValue(imageView2.translateXProperty(), 0);
		KeyFrame kf = new KeyFrame(Duration.seconds(.25), kv1, kv2, kv3, kv4);
		Timeline slide = new Timeline(kf);
		if (play)
			slide.play();
	}

	public void select() {
		root.setStyle("-fx-border-width: 2px; -fx-border-color: yellow");
	}

	public void deselect() {
		root.setStyle("-fx-border-width: 1px; -fx-border-color: black");
	}
	
	public void setOnAction(EventHandler<MouseEvent> e) {
		root.setOnMouseClicked(e);
	}
}
