package ingsw.Client.View.Gui;

import ingsw.Client.View.Gui.GameBoardScene.NotificationDisplay;
import ingsw.Server.Player;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This Class is responsible of rendering the Lobby scene where Players can wait for the match to begin
 */
public class LobbyView extends Application {

    private Pane lobby;

    public LobbyView() throws IOException {
        this.lobby = FXMLLoader.load(getClass().getResource("/View/Fxml/lobbyViewLayout.fxml"));
    }

    @Override
    public void start(Stage primaryStage) {
    primaryStage.setTitle("Player Lobby");

    List<Player> testList = new ArrayList<>();
    testList.add(new Player("Federico"));
    testList.add(new Player("Giosch"));
    testList.add(new Player("Poggi"));
    testList.add(new Player("Paolo"));

    primaryStage.setScene(lobbyViewRender(testList));
    primaryStage.show();
}

    public Scene lobbyViewRender(List<Player> players) {
        VBox playersBox = (VBox) lobby.lookup("#playersBox");
        NotificationDisplay.notificationSetRoot(lobby);

        playersBox.setAlignment(Pos.TOP_CENTER);
        for (Player p : players) {
            Label playerName = new Label(p.getName().toUpperCase());
            playerName.setStyle("-fx-font-weight: bold; -fx-text-fill: green; -fx-font-size: 25;");
            playersBox.getChildren().addAll(playerName);
        }
        return new Scene(lobby, 1280, 720);
    }
}