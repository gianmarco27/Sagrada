package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Utility.GameException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;

import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static javafx.geometry.Pos.CENTER;


/**
 * This Class is used to spawn a to inform the player of what game exception happened (displaying its message), usually used when a game rule is not respected
 */
public class GameExceptionPopUp extends Application {

    public static void show(GameException e) {
        final Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.getIcons().add(new Image(GameExceptionPopUp.class.getClassLoader().getResourceAsStream("View/Img/icon.png")));

        VBox mainHolder = new VBox(50);
        Button ok = new Button("OK");
        StackPane dialogSp = new StackPane();
        Label message = new Label(e.getMessage());

        message.setStyle("-fx-font-size:18;");
        dialogSp.getChildren().add(message);
        ok.setOnMouseClicked(event -> dialog.close());
        mainHolder.getChildren().addAll(dialogSp, ok);
        message.setWrapText(true);
        message.setTextAlignment(TextAlignment.CENTER);
        dialogSp.setAlignment(CENTER);
        mainHolder.setAlignment(CENTER);

        Scene dialogScene = new Scene(mainHolder, 350, 150);

        dialog.setScene(dialogScene);
        dialog.setTitle("Warning");
        dialog.showAndWait();
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        show(new GameException("trentatre trentini trottarono giù da trento tutti e trentatre trotterellando"));
    }

}
