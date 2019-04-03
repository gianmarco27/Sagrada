package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Player;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Map;

import static javafx.geometry.Pos.CENTER;

/**
 * This Class is used to render Single and MultiPlayer end-game Scoreboards
 */
public class FinalResultsView {

    static VBox scoreHolder = new VBox(25);

    public static void MultiScoreboardRender(Map<Player, Integer> playerScoreBoard) {
        Label title = new Label("Final Score");
        title.setAlignment(CENTER);
        scoreHolder.setAlignment(CENTER);
        title.setStyle("-fx-font-size: 42px");
        scoreHolder.getChildren().add(title);
        stageRender(playerScoreBoard, scoreHolder);
    }

    public static void SingleScoreboardRender(Map<Player, Integer> playerScoreBoard, Integer scoreBoardScore) {
        Player p = playerScoreBoard.keySet().stream().findFirst().orElse(null);
        if (p != null) {
            Integer playerScore = playerScoreBoard.get(p);
            Label title = new Label((playerScore >= scoreBoardScore) ? "YOU WON!" : "YOU LOST!");
            Label roundTrackerScore = new Label("ROUNDTRACKER | Total Score : " + scoreBoardScore);
            title.setAlignment(CENTER);
            scoreHolder.setAlignment(CENTER);
            title.setStyle("-fx-font-size: 42px");
            scoreHolder.getChildren().add(title);
            scoreHolder.getChildren().add(roundTrackerScore);
        }
        stageRender(playerScoreBoard, scoreHolder);
    }

    private static void stageRender(Map<Player, Integer> playerScoreBoard, VBox scoreHolder) {
        Stage scoreWindow = new Stage();
        scoreWindow.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        scoreWindow.getIcons().add(new Image(FinalResultsView.class.getClassLoader().getResourceAsStream("View/Img/icon.png")));
        int position = 0;
        for (Player p : playerScoreBoard.keySet()) {
            if(p.isConnected()) {
                Label playerName = new Label("N° " + (++position) + " " + p.getName() + " Score: " + playerScoreBoard.get(p));
                playerName.setStyle("-fx-font-size: 22px");
                playerName.setAlignment(CENTER);
                scoreHolder.getChildren().add(playerName);
            }
        }
        for (Player p : playerScoreBoard.keySet()) {
            if(!p.isConnected()) {
                Label playerName = new Label("N° " + (++position) + " " + p.getName() + " Score: " + playerScoreBoard.get(p)+" - disconnected");
                playerName.setStyle("-fx-font-size: 22px");
                playerName.setAlignment(CENTER);
                scoreHolder.getChildren().add(playerName);
            }
        }
        scoreWindow.initModality(Modality.APPLICATION_MODAL);
        Scene toSet = new Scene(scoreHolder, 400, 400);
        scoreWindow.setScene(toSet);
        scoreWindow.showAndWait();
    }
}