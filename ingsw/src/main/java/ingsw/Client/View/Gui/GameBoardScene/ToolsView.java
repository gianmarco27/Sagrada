package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Tools.EglomiseBrush;
import ingsw.Server.Tools.Tool;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javafx.geometry.Pos.CENTER;

/**
 * This Class makes use of the Modular Pane that ToolView returns to fill the Tools Section
 */
public class ToolsView extends Application{

    private static Controller controller;
    Pane tPane;
    Parent root;

    public ToolsView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public ToolsView(Parent root) {
         tPane = ((Pane) root.lookup("#ToolSection"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setController(Controller c) {
        ToolsView.controller = c;
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<Tool> toolsList = new ArrayList<>();
        toolsList.add(new EglomiseBrush());
        toolsList.add(new EglomiseBrush());
        toolsList.add(new EglomiseBrush());
        toolsList.add(new EglomiseBrush());
        toolsList.add(new EglomiseBrush());
        primaryStage.setTitle("ToolsExemple");
        tPane = ((Pane) root.lookup("#ToolSection"));
        renderTools(toolsList);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void renderTools(List<Tool> tools) throws Exception {
        int size = tools.size();
        Pane renderedTool;
        HBox toolsSlot = new HBox(5);
        toolsSlot.setAlignment(CENTER);
        toolsSlot.setMinWidth(tPane.getPrefWidth());
        toolsSlot.setMinHeight(tPane.getPrefHeight());
        VBox splitSlot;
        tPane.getChildren().add(toolsSlot);
        for (int idx = 0; idx < size; idx++) {
            renderedTool = new ToolView().renderTool(tools.get(idx));
            splitSlot = new VBox(10);
            splitSlot.setAlignment(CENTER);
            toolsSlot.getChildren().add(splitSlot);
            splitSlot.getChildren().add(renderedTool);
            PlayerInteractionGameBoard.setToolHandler(renderedTool, tools.get(idx).getToolNumber());
        }
        if (size > 3) {
            // if we have more than 3 tools we resize them accordingly to the number of tools to render
            toolsSlot.setScaleX(0.75);
            toolsSlot.setScaleY(0.9);
            toolsSlot.setMaxWidth(tPane.getWidth());
        }

    }

    public void updateTools(List<Tool> tools) {
        tPane.getChildren().clear();
        try {
            renderTools(tools);
        } catch (Exception e) {
            System.out.println("Error updating tools");
        }
    }

}

