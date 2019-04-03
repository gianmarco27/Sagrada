package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Controller.Controller;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.NoSuchConstrainException;
import ingsw.Server.Utility.Coord;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * This Class is responsible of rendering Your own Grid in the relative section with suggestions on available placements when selecting a Die
 */
public class OwnGridView extends Application {

    private static Controller controller;
    Pane playerGrid;
    Pane toShadow;
    StackPane dice;
    Grid referenceGrid;
    Parent root;

    public OwnGridView() {
        try {
            root = FXMLLoader.load(getClass().getResource("/View/Fxml/gameBoardLayout.fxml"));
        } catch (IOException e) {
            System.out.println("error loading main frame");
        }
    }

    public OwnGridView(Parent root) {
        playerGrid = (Pane) root.lookup("#PlayerGrid");
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static void setController(Controller c) {
        OwnGridView.controller = c;
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GridOwnViewExemple");
        root = FXMLLoader.load(getClass().getResource("/View/Fxml/gridLayout.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void renderGrid(Grid toRender, int size) throws IOException, NoSuchConstrainException { // Abstract in OwnGridView
        this.referenceGrid = toRender;
        Pane grid = new GridView().renderGrid(toRender, size, true);
        playerGrid.getChildren().add(grid);
    }

    public void updateGrid(Grid toRender, int size) {
        this.referenceGrid = toRender;
        playerGrid.getChildren().clear();
        try {
            renderGrid(toRender, size);
        } catch (IOException | NoSuchConstrainException e) {
            System.out.println("Error updating Own Grid");
        }
    }

    public void suggestionRender(Dice selected) {
        GridPane grid = (GridPane) playerGrid.lookup("#ownGridPane");
        List<Coord> coords = referenceGrid.suggestPlacements(selected);
        for (int idx = 0; idx < grid.getChildren().size(); idx++) {
            if (coords.contains((Coord) grid.getChildren().get(idx).getUserData())) {
                dice = ((StackPane) grid.getChildren().get(idx));
                toShadow = ((Pane) dice.getChildren().get(0));
                toShadow.setStyle("-fx-effect: innershadow( gaussian, rgba( 0, 0, 0, 0.3), 15, 25, 0, 0 );\n");
            }
        }
    }

    public void resetSuggestion() {
        GridPane grid = (GridPane) playerGrid.lookup("#ownGridPane");
        for (int idx = 0; idx < grid.getChildren().size(); idx++) {
                dice = ((StackPane) grid.getChildren().get(idx));
                toShadow = ((Pane) dice.getChildren().get(0));
                toShadow.setStyle("-fx-effect: innershadow( gaussian, rgba( 0, 0, 0, 0.3), 0, 0, 0, 0 );\n");
        }
    }
}
