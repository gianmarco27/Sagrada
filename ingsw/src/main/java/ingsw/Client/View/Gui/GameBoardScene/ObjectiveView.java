package ingsw.Client.View.Gui.GameBoardScene;

        import ingsw.Server.Objective.*;
        import javafx.application.Application;
        import javafx.fxml.FXMLLoader;
        import javafx.scene.Scene;
        import javafx.scene.control.Label;
        import javafx.scene.layout.HBox;
        import javafx.scene.layout.Pane;
        import javafx.stage.Stage;

        import java.io.IOException;


/**
 * This class takes care of rendering a Pane containing a single objective wich is used modularly in the objectivesView and in the GridPicking scene
 */
public class ObjectiveView extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    /*
     * Start-method used for displaying the view as a standalone window for debugging feature
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ObjectiveExemple");
        Label lbl = new Label("This is a Objective");

        HBox hb = new HBox(10);

        Pane priob = renderObjective( new ColorVariety() );
        Pane pubob = renderObjective( new ColorVariety() );

        hb.getChildren().addAll(lbl, priob, pubob);
        Scene scene = new Scene(hb);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public Pane renderObjective(Objective toRender) throws IOException {

        Pane rootParent = FXMLLoader.load(getClass().getResource("/View/Fxml/objectiveLayout.fxml"));

        Label name = (Label) rootParent.lookup("#title");
        Label desc = (Label) rootParent.lookup("#description");
        Label cost = (Label) rootParent.lookup("#cost");

        rootParent.setStyle("-fx-background-color: rgba(240, 230, 140, 1)");
        name.setText(toRender.getCardName());
        desc.setText(toRender.getDescription());
        cost.setText("Val: " + Integer.toString(toRender.getObjValue()));

        return rootParent;
    }
}