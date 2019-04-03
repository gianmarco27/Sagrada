package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Grid.Grid;
import ingsw.Server.Grid.GridCell;
import ingsw.Server.Grid.NoSuchConstrainException;
import ingsw.Server.Utility.Coord;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * This class is responsible for rendering the Grid of any player and its content
 */
public class GridView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("GridExemple");
        Pane root = FXMLLoader.load(getClass().getResource("/View/Fxml/gridLayout.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane  renderGrid(Grid toRender, int size, boolean interactive) throws IOException, NoSuchConstrainException {

        Pane rootParent = FXMLLoader.load(getClass().getResource("/View/Fxml/gridLayout.fxml"));
        GridPane gridPane = (GridPane) rootParent.getChildren().get(1);
        Label txt = (Label) rootParent.lookup("#gridName");
        gridPane.setStyle("-fx-background-color: black;");
        txt.setText(toRender.getCardName());
        txt = (Label) rootParent.lookup("#favorPoints");
        txt.setText(Integer.toString(toRender.getFavorPoints()));
        for (int idx_x = 0; idx_x < Grid.COLUMNS; idx_x++) {
            for (int idx_y = 0; idx_y < Grid.ROWS; idx_y++) {
                Dice dice = toRender.getDie(idx_x, idx_y);
                Coord xy = new Coord(idx_x, idx_y);
                StackPane sp;
                if (dice != null) {
                    sp = (new DiceView()).renderDice(dice, size);
                    gridPane.add((new DiceView()).renderDice(dice, size), idx_x, idx_y);
                } else {
                    GridCell gc = toRender.getCell(idx_x, idx_y);
                    sp = (new DiceView()).renderCell(gc, size);

                }
                if (interactive) {
                    PlayerInteractionGameBoard.setGridDiceHandler(sp, xy);
                }

                gridPane.add(sp, idx_x, idx_y);
            }
        }
        return rootParent;
    }

}
