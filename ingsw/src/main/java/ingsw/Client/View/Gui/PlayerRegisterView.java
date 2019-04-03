package ingsw.Client.View.Gui;

import ingsw.Client.View.GameConnection;
import ingsw.Client.View.Gui.GameBoardScene.GameExceptionPopUp;
import ingsw.Client.View.Gui.GameBoardScene.NotificationDisplay;
import ingsw.Server.Utility.GameException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This Class is the First gameWindow spawned in the GUI and allows the player to pick his nickname for thr match to begin or to reconnect to a running match
 */
public class PlayerRegisterView extends Application {

    private static final Logger log = Logger.getLogger( PlayerRegisterView.class.getName() );

    private static GameConnection gc;
    public static void setGc(GameConnection gc) {
        PlayerRegisterView.gc = gc;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("RegisterPlayer");
        primaryStage.setScene(RegisterViewRender());
        primaryStage.show();
    }

    public Scene RegisterViewRender() throws Exception {
        Pane root = FXMLLoader.load(getClass().getResource("/View/Fxml/playerRegisterLayout.fxml"));
        TextField userInput = (TextField) root.lookup("#namePicked");
        Button confirm = (Button) root.lookup("#confirmButton");
        Label errorMessage = (Label) root.lookup("#errorBox");
        NotificationDisplay.notificationSetRoot(root);
        errorMessage.setText(""); // At the start we don't want to display any error
        confirm.setOnMouseClicked(e -> registerPlayer(userInput));
        userInput.setUserData(errorMessage); // Store in the user input a reference to where to display a potential error
        userInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                registerPlayer(userInput);
            }
        });
        return new Scene(root, 1280, 720);
    }

    public void registerPlayer(TextField userInput) {
        if (userInput.getText().length() > 0 && userInput.getText().length() < 15) {
            try {
                gc.connect(userInput.getText());
            } catch (GameException e) {
                GameExceptionPopUp.show(e);
            } catch (NotBoundException | ClassNotFoundException e) {
                log.warning(Arrays.toString(e.getStackTrace()));
            } catch (IOException e){
                GameExceptionPopUp.show(new GameException("Could not connect to the server."));
            }
        } else {
            Label errorMessage = (Label) userInput.getUserData();
            userInput.setText("");
            errorMessage.setText("Error with the provided username");
            return;
        }
        // Registration logic
        return;
    }
}
