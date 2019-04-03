package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Grid.NoSuchConstrainException;
import ingsw.Server.Tools.FluxRemover;
import ingsw.Server.Tools.Tool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;


/**
 * This Class is responsible for spawning  modular single Panes containing Each a given Tool to be used by ToolsView
 */
public class ToolView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("DiceExemple");
        Label lbl = new Label("This is a Tool");

        HBox hb = new HBox(10);

        Pane np = renderTool((new FluxRemover()));

        hb.getChildren().addAll(lbl, np);
        Scene scene = new Scene(hb);


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane renderTool(Tool toRender) throws IOException, NoSuchConstrainException {

        Pane rootParent = FXMLLoader.load(getClass().getResource("/View/Fxml/toolLayout.fxml"));

        Label name = (Label) rootParent.lookup("#title");
        Label number = (Label) rootParent.lookup("#number");
        Label desc = (Label) rootParent.lookup("#description");
        Label cost = (Label) rootParent.lookup("#cost");
        Pane colorPrice = (Pane) rootParent.lookup("#colorPrice");

        name.setText(toRender.getToolName());
        number.setText("# " + Integer.toString(toRender.getToolNumber()));
        desc.setText(toRender.getDescription());
        cost.setText("C: " + Integer.toString(toRender.getCost()));
        colorPrice.getChildren().addAll(new DiceView().renderColor(toRender.getColorCost(), 20));
        return rootParent;
    }
}

