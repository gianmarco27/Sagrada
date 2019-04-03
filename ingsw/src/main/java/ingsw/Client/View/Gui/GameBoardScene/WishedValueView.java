package ingsw.Client.View.Gui.GameBoardScene;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * This class generates a small window containing a field to insert the Value you want to change a certain Die into
 */
public class WishedValueView {

    static int answer;

    public static int display() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Wished value");
        window.setMinWidth(300);
        window.setMinHeight(300);
        window.getIcons().add(new Image(WishedValueView.class.getClassLoader().getResourceAsStream("View/Img/icon.png")));

        Label label = new Label("Insert the desired int value");

        TextField userInput = new TextField();
        userInput.setMaxWidth(150);
        Button confirmButton = new Button("CONFIRM");
        confirmButton.setStyle("-fx-background-color: rgb(39,174,96); -fx-text-fill: white");
        Button cancelButton = new Button("CANCEL");
        cancelButton.setStyle("-fx-background-color: rgb(192,57,43); -fx-text-fill: white");
        userInput.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                confirmButton.fire();
            }
        });
        confirmButton.setOnAction(e -> {
            try {
                answer = Integer.parseInt(userInput.getText());
                window.close();
            } catch (NumberFormatException ex) {
                userInput.setText("");
                label.setText("Insert the desired INT value");
            }
        });

        cancelButton.setOnAction(e -> {
            answer = -1;
            window.close();
        });

        VBox layout = new VBox(15);

        //Add buttons
        layout.getChildren().addAll(label, userInput, confirmButton, cancelButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();

        //Make sure to return answer
        return answer;
    }
}

