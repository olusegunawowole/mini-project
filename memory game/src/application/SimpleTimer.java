package application;

import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SimpleTimer extends HBox {
	private LongProperty bestTime; // measured in seconds
	private LongProperty time; // current time in seconds
	private AnimationTimer timer;// for animation
	private Label bestTimeLabel;
	private Label bestTimeTitleLabel;
	private Label timeLabel;
	private Label timeTitleLable;

	public SimpleTimer() {
		bestTimeTitleLabel = createLabel("Best Time: ");
		bestTimeLabel = createLabel("99:59:59");
		timeTitleLable = createLabel("Current Time: ");
		timeLabel = createLabel("00:00:00");
		setAlignment(Pos.TOP_CENTER);
		setSpacing(25);
		getChildren().addAll(bestTimeTitleLabel, bestTimeLabel, timeTitleLable, timeLabel);
		setStyle("-fx-background-color: lightblue; -fx-border-width: 2 2 0 2; -fx-border-color: gray");
		setPadding(new Insets(10));
		time = new SimpleLongProperty();
		time.addListener(e -> {
			long totalTime = time.get();// in seconds
			String hour = totalTime / 3600 < 10 ? "0" + totalTime / 3600 : totalTime / 3600 + "";
			String min = (totalTime / 60) % 60 < 10 ? "0" + (totalTime / 60) % 60 : (totalTime / 60) % 60 + "";
			String sec = totalTime % 60 < 10 ? "0" + totalTime % 60 : totalTime % 60 + "";
			String timeStr = hour + ":" + min + ":" + sec;
			timeLabel.setText(timeStr);
			if (bestTime.get() < time.get()) {
				timeLabel.setStyle("-fx-text-fill: red");
				timeTitleLable.setStyle("-fx-text-fill: red");

			} else {
				timeLabel.setStyle("-fx-text-fill: black");
				timeTitleLable.setStyle("-fx-text-fill: black");
			}

		});

		bestTime = new SimpleLongProperty();
		bestTime.addListener(e -> {
			long totalTime = bestTime.get();// in seconds
			String hour = totalTime / 3600 < 10 ? "0" + totalTime / 3600 : totalTime / 3600 + "";
			String min = (totalTime / 60) % 60 < 10 ? "0" + (totalTime / 60) % 60 : (totalTime / 60) % 60 + "";
			String sec = totalTime % 60 < 10 ? "0" + totalTime % 60 : totalTime % 60 + "";
			String timeStr = hour + ":" + min + ":" + sec;
			bestTimeLabel.setText(timeStr);
		});
		bestTime.set(359999);
		timer = new AnimationTimer() {
			int frame;

			@Override
			public void handle(long now) {
				if (frame % 60 == 0) {// changes every second
					time.set(time.get() + 1);
				}
				frame++;
			}
		};
	}

	public void start() {
		stop();
		timer.start();
	}

	public void stop() {
		timer.stop();
	}

	public void reset() {
		time.set(0);

	}

	public void resetAll() {
		bestTime.set(0);
		;
		time.set(0);
	}

	// time in seconds
	public void setBestTime(long time) {
		if (time < 0)
			return;
		bestTime.set(time);
	}
	
	public void setBestTime() {
		bestTime.set(time.get());
	}

	public long getBestTime() {
		return bestTime.get();
	}

	// time in seconds
	public void setTime(long time) {
		if (time < 0)
			return;
		this.time.set(time);
	}

	// time in seconds
	public long getTime() {
		return time.get();
	}

	public boolean isBestTime() {
		return time.get() < bestTime.get();
	}
	
	

	@Override
	public String toString() {
		long totalTime = time.get();// in seconds
		String hour = totalTime / 3600 < 10 ? "0" + totalTime / 3600 : totalTime / 3600 + "";
		String min = (totalTime / 60) % 60 < 10 ? "0" + (totalTime / 60) % 60 : (totalTime / 60) % 60 + "";
		String sec = totalTime % 60 < 10 ? "0" + totalTime % 60 : totalTime % 60 + "";
		String str = hour + ":" + min + ":" + sec;
		return str;
	}

	// Utility method
	private Label createLabel(String text) {
		Label label = new Label(text);
		label.setStyle("-fx-text-fill: black");
		label.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
		return label;
	}
}
