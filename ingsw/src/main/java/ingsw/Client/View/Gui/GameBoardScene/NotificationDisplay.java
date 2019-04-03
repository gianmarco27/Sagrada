package ingsw.Client.View.Gui.GameBoardScene;

import java.util.Timer;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.util.TimerTask;

/**
 * This class renders a Pane containing small notifications during game like "player disconnected" in the current game window
 */
public class NotificationDisplay {

    static Pane notification;
    static Timer timer = new Timer();
    static TimerTask timerTask;
    static Boolean running = false;
    public static final int DELAY = 5000;

    public static void notificationSetRoot(Parent root) {
        notification = new Pane();
        notification.setMinSize(0, 0);
        notification.setMaxSize(0, 0);
        ((Pane) root).getChildren().add(notification);
        notification.setLayoutX(5);
        notification.setLayoutY(645);
        notification.toFront();
    }

    public static void notification(String toPrint) {
        //clean queue from previous, not terminated timer tasks
        if (running) {
            timerTask.cancel();
            timer.purge();
        }
        Platform.runLater(() -> {
            Label text = new Label(toPrint);

            if (running) {
                notification.getChildren().clear();
            }

            text.setMinSize(250, 70);
            text.setMaxSize(250, 70);
            text.setStyle("-fx-font-size: 16;\n" +
                    "-fx-background-radius: 5;\n" +
                    "-fx-border-radius: 5;\n" +
                    "-fx-border-width: 1.5;\n" +
                    "-fx-border-color:#171b2a;\n" +
                    "-fx-background-color: #f9ffbf");
            text.setAlignment(Pos.CENTER);

            notification.autosize();
            notification.getChildren().add(text);
        });

        timerTask = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    notification.getChildren().clear();
                    running = false;
                });
            }
        };
        timer.schedule(timerTask, DELAY);
        running = true;
    }
}
