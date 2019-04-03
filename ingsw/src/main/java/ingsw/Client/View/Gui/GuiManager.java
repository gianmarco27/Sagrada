package ingsw.Client.View.Gui;

import ingsw.Client.View.GuiViewWrapper;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

/**
 * This class manages the switch between every scene in the GUI depending on the Current stage of the game
 */
public class GuiManager extends Application {

    private static final Logger log = Logger.getLogger( GuiManager.class.getName() );

    static Stage mainStage;
    private static boolean launched = false;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;
        PlayerRegisterView loginWindow = new PlayerRegisterView();
        primaryStage.setTitle("SAGRADA");
        primaryStage.setScene(loginWindow.RegisterViewRender());
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(new Image(GuiViewWrapper.class.getClassLoader().getResourceAsStream("View/Img/icon.png")));
        primaryStage.show();
    }

    public static void SceneSwitcher (Scene toSwitch) {
        Platform.runLater(() -> mainStage.setScene(toSwitch));
    }
    public static void close(){ Platform.runLater(() -> mainStage.hide()); }
    public void launchApplication() throws Exception {
        if(!launched) {
            launched = true;
            launch();
        } else {
            Platform.runLater( () -> {
                try {
                    mainStage.setScene(new PlayerRegisterView().RegisterViewRender());
                } catch (Exception e) {
                    log.warning(Arrays.toString(e.getStackTrace()));
                }
                mainStage.show();
            });
        }
    }
}
