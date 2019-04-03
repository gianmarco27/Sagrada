package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Dice.Dice;
import ingsw.Server.Dice.DiceColor;
import ingsw.Server.Grid.GridCell;
import ingsw.Server.Grid.GridCellColor;
import ingsw.Server.Grid.GridCellShade;
import ingsw.Server.Grid.NoSuchConstrainException;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for Rendering the Single Dicecell being it containing constraints (of shade or color) or containing a real dice.
 */
public class DiceView  extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    private static final int SIZE = 80;

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("DiceExemple");
        Label lbl = new Label("This is a dice");
        List<StackPane> lr = new ArrayList<>();
        lr.add(renderDice(new Dice(DiceColor.RED), SIZE));
        lr.add(renderDice(new Dice(DiceColor.BLUE), SIZE));

        HBox hb = new HBox(10);
        hb.getChildren().addAll(lbl, lr.get(0), lr.get(1));
        Scene scene = new Scene(hb);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public StackPane renderDice(Dice toRender, int size) {
        StackPane sp = new StackPane();
        Pane p = new Pane();
        p.setPrefSize(size, size);
        p.setMaxSize(size, size);
        p.setMinSize(size, size);
        Text text = createText(toRender.getValue(), size - 8);
        p.setBackground(new Background(new BackgroundFill(toRender.getColor().getDisplayColor(), CornerRadii.EMPTY, Insets.EMPTY)));
        sp.getChildren().addAll(p, text);
        return sp;
    }

    public StackPane renderCell(GridCell toRender, int size) throws NoSuchConstrainException {
        StackPane sp = new StackPane();
        Pane p = new Pane();
        p.setPrefSize(size, size);
        p.setMaxSize(size, size);
        p.setMinSize(size, size);
        Text text;
        if (toRender.hasColorConstrain()) {
            GridCellColor coloredCell =  (GridCellColor) toRender;
            p.setBackground(new Background(new BackgroundFill(coloredCell.getColorConstrain().getDisplayColor(), CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
            p.setBackground(new Background(new BackgroundFill(Color.GHOSTWHITE, CornerRadii.EMPTY, Insets.EMPTY)));
            }
        if (toRender.hasShadeConstrain()) {
            GridCellShade valueCell = (GridCellShade) toRender;
            text = createText(valueCell.getShadeConstrain(), size - 8);
        } else {
            text = createText(0, size - 8);
        }

        sp.getChildren().addAll(p, text);
        return sp;
    }

    public StackPane renderColor(DiceColor toRender, int size) throws NoSuchConstrainException {
        GridCell cellToRender = new GridCellColor(toRender);
        return renderCell(cellToRender, size);
    }

    public Text createText(Integer value, int fontSize) {
        Text text = new Text((value != 0) ? Integer.toString(value) : "");
        text.setFill(Color.WHITE);

        text.setStyle("-fx-font: " + fontSize + " arial;" +
                "-fx-stroke: grey;\n" +
               "-fx-stroke-width: " +
                ((fontSize > 50) ? "1.5px;" :"0px;"));
        return text;
    }
}