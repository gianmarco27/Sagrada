package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Objective.*;
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
 * This Class takes care of spawning the different objectives in the Objectives section in the main window
 */
public class ObjectivesView extends Application{

    private static Controller controller;
    Pane objPane;
    Pane opPane;
    Parent root;

    public ObjectivesView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public ObjectivesView(Parent root) {
        objPane = ((Pane) root.lookup("#Objectives"));
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setController(Controller c) {
        ObjectivesView.controller = c;
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        List<PublicObjective> pubObjList = new ArrayList<>();
        ArrayList<PrivateObjective> privateObjective = new ArrayList<>();
        privateObjective.add(new ShadesOfYellow());
        privateObjective.add(new ShadesOfPurple());
        pubObjList.add(new ColorVariety());
        pubObjList.add(new ColorVariety());
        primaryStage.setTitle("ObjectivesExemple");
        root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        objPane = ((Pane) root.lookup("#Objectives"));
        renderAllObj(pubObjList, privateObjective);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void renderAllObj(List<PublicObjective> publicObjectives, List<PrivateObjective> privateObjectives) throws Exception {
        Pane renderedObj;
        HBox currentSlot = new HBox(5);
        List<Objective> objectives = new ArrayList<>();
        objectives.addAll(publicObjectives);
        objectives.addAll(privateObjectives);
        currentSlot.setAlignment(CENTER);
        currentSlot.setMinWidth(objPane.getPrefWidth());
        currentSlot.setMinHeight(objPane.getPrefHeight());
        VBox splitSlot;
        objPane.getChildren().add(currentSlot);
        for (Objective objective : objectives) {
            renderedObj = new ObjectiveView().renderObjective(objective);
            splitSlot = new VBox(10);
            splitSlot.setAlignment(CENTER);
            currentSlot.getChildren().add(splitSlot);
            splitSlot.getChildren().add(renderedObj);
        }
    }

    public void updateObj(List<PublicObjective> publicObjectives, List<PrivateObjective> privateObjectives) {
        objPane.getChildren().clear();
        try {
            renderAllObj(publicObjectives, privateObjectives);
        } catch (Exception e) {
            System.out.println("Error while updating Objectives");
        }
    }
}
