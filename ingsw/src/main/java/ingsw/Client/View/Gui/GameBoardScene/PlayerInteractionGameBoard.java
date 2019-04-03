package ingsw.Client.View.Gui.GameBoardScene;

import ingsw.Server.Actions.DiceActionParameter;
import ingsw.Server.Actions.ToolActionParameter;
import ingsw.Client.View.ToolParameterParserGUI;
import ingsw.Server.Controller.Controller;
import ingsw.Server.Dice.Dice;
import ingsw.Server.Utility.Coord;
import ingsw.Server.Utility.GameException;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.effect.Glow;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * This Class is responsible of setting the main eventHandlers in-game that control how the user can interact with the game,
 * currently in each of its methods it controls: wished Value setting, selecting a gridCell, a Die from RT or Dicepool,
 * a tool to use and clicking on the confirm and end turn buttons, binding their actions to the controller
 */
public class PlayerInteractionGameBoard {
    private static final Logger log = Logger.getLogger( PlayerInteractionGameBoard.class.getName() );
    private static Controller controller;

    private static final String CLICKED_KEY = "clicked";
    private static final String SHADOW_EFFECT = "-fx-effect:dropshadow(three-pass-box,";
    private static final String COLOR_HIGHLIGHTED = "coral";
    private static final String COLOR_HOVERED = "rgba(0, 174, 255, 1)";
    private static Label wishedValueLabel;
    private static OwnGridView ownGrid;

    static Boolean selectedTool = false;
    static Boolean selectedDiceTracker = false;
    private static boolean selectedDicePool = false;
    private static boolean selectedPayPrice = false;
    private static boolean waitingCallback = false;

    /**
     * Set Grid dice interactive effects (only own grid)
     */
    private static void setClickableButton(Button button) {
        Glow glow = new Glow();
        button.setOnMousePressed(event -> {
            glow.setLevel(1);
            button.setEffect(glow);
        });
        button.setOnMouseReleased(event -> {
            glow.setLevel(0);
            button.setEffect(glow);
        });

    }

    /**
     * Given a Node element rapresenting a Die in a Grid this method sets its proper behaviour and visual effect
     * @param toSet
     * @param xy coordinates of the die in the grid
     */
    public static void setGridDiceHandler(Node toSet, Coord xy) {
        toSet.setUserData(xy);
        toSet.getProperties().put(CLICKED_KEY, false);

        toSet.setOnMouseClicked(e -> {
            Coord coord = (Coord) toSet.getUserData();
            // On click revert the "Clicked" status
            toSet.getProperties().compute(CLICKED_KEY, (k, v) -> !(Boolean) v);
            toSet.setStyle(SHADOW_EFFECT +
                    (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? COLOR_HIGHLIGHTED : COLOR_HOVERED) +
                    ", 4, 5, 0, 0);");
            if ((Boolean) toSet.getProperties().get(CLICKED_KEY)) {
                ToolParameterParserGUI.getInstance().addCoord(coord);
            } else {
                ToolParameterParserGUI.getInstance().removeCoord(coord);
            }
        });

        setHoverEffectDie(toSet);
    }

    public static void resetSelectedDice() {
        selectedDicePool = false;
        selectedPayPrice = false;
        selectedDiceTracker = false;
        selectedTool = false;
        wishedValueLabel.setText("");
    }

    /**
     * Given a Node element rapresenting a Die in the DicePool this method sets its proper behaviour and visual effect
     * @param toSet
     * @param idx index of the die in the DicePool
     * @param die
     */
    public static void setPoolDiceHandler(Node toSet, int idx, Dice die) {

        toSet.setUserData(idx);
        toSet.getProperties().put(CLICKED_KEY, false);
        toSet.getProperties().put("dicePoolPay", false);
        toSet.getProperties().put("dicePoolTarget", false);
        toSet.getProperties().put("die", die); // Storing in the javafx object the information regarding the die
        toSet.setOnMouseClicked(e -> {
            ToolParameterParserGUI tppg = ToolParameterParserGUI.getInstance();
            Boolean isDicePoolTarget = (Boolean) toSet.getProperties().get("dicePoolTarget");
            Boolean isPayTarget = (Boolean) toSet.getProperties().get("dicePoolPay");
            if (e.getButton() == MouseButton.PRIMARY) {
                // Behaviour with the left click
                if (!selectedDicePool || isDicePoolTarget) {
                    if (!selectedDicePool) {
                        selectedDicePool = true;
                        int dicePoolIdx = (Integer) toSet.getUserData();
                        tppg.setDicePool(dicePoolIdx);
                        ownGrid.suggestionRender((Dice) toSet.getProperties().get("die"));
                    } else if (isDicePoolTarget) {
                        selectedDicePool = false;
                        tppg.setDicePool(-1);
                        ownGrid.resetSuggestion();
                    }
                    toSet.getProperties().compute("dicePoolTarget", (k, v) -> !(Boolean) v);
                }
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // Behaviour with the right click
                if (!selectedPayPrice || isPayTarget) {
                    if (!selectedPayPrice) {
                        selectedPayPrice = true;
                        int dicePayIdx = (Integer) toSet.getUserData();
                        tppg.setPayDicePool(dicePayIdx);
                    } else if (isPayTarget) {
                        selectedPayPrice = false;
                        tppg.setPayDicePool(-1);
                    }
                    toSet.getProperties().compute("dicePoolPay", (k, v) -> !(Boolean) v);
                    }
                }
            isDicePoolTarget = (Boolean) toSet.getProperties().get("dicePoolTarget");
            isPayTarget = (Boolean) toSet.getProperties().get("dicePoolPay");
            toSet.getProperties().put(CLICKED_KEY, (isDicePoolTarget || isPayTarget));
            // On click revert the "Clicked" status
            toSet.setStyle(SHADOW_EFFECT +
                    ((isDicePoolTarget || isPayTarget) ? COLOR_HIGHLIGHTED : COLOR_HOVERED) +
                    ", 4, 5, 0, 0);");
        });

        setHoverEffectDie(toSet);
    }

    public static void setGrid(OwnGridView ownGrid) {
        PlayerInteractionGameBoard.ownGrid = ownGrid;
    }

    /**
     * Given a Node element rapresenting a Tool this method sets its proper behaviour and visual effect
     * @param toSet
     * @param toolNumber the unique Id of the Tool
     */
    public static void setToolHandler(Node toSet, int toolNumber) {
        toSet.setUserData(toolNumber);
        toSet.getProperties().put(CLICKED_KEY, false);
        toSet.setStyle("-fx-background-color: rgba(240, 230, 140, 1);");

        toSet.setOnMouseClicked(e -> {
            if (!selectedTool || ((Boolean) toSet.getProperties().get(CLICKED_KEY))) {
                selectedTool = !selectedTool;
                Integer toolN = (Integer) toSet.getUserData();
                // On click revert the "Clicked" status
                toSet.getProperties().compute(CLICKED_KEY, (k, v) -> !(Boolean) v);
                toSet.setStyle("-fx-background-color:" +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "orange" : "rgba(240, 230, 140, 1)") +
                        ";");
                if ((Boolean) toSet.getProperties().get(CLICKED_KEY)) {
                    ToolParameterParserGUI.getInstance().setPickedTool(toolN);
                } else {
                    ToolParameterParserGUI.getInstance().setPickedTool(-1);
                }
            }
        });

        toSet.setOnMouseEntered(e -> {
            toSet.setStyle("-fx-background-color:" +
                    (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "orange" : "rgba(135, 206, 250, 1)") +
                    ";");
            // Checks if the CLICKED_KEY property is true and changes how the highlight colors and size are displayed
        });

        toSet.setOnMouseExited(e ->
                toSet.setStyle("-fx-background-color:" +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? "orange" : "rgba(240, 230, 140, 1)") +
                        ";"));
        // Necessary to prevent that de-hovering alters how CLICKED_KEY is displayed
    }

    public static void setWishedValueHandler(Button wishedValue, Label wishedValueView) {

        wishedValueLabel = wishedValueView;

        wishedValue.setOnMouseClicked(e -> {
            int value;
            value = WishedValueView.display();
            wishedValueView.setText("");
            if (value != -1) {
                ToolParameterParserGUI.getInstance().setWishValue(value);
                wishedValueView.setText(Integer.toString(value));
            }
        });
    }

    /**
     * Given a Node element rapresenting a Die in the RoundTracker this method sets its proper behaviour and visual effect
     * @param toSet
     * @param xy coordinates of the Die in the RoundTracker
     */
    public static void setRoundTrackerHandler(Node toSet, Coord xy) {
        toSet.setUserData(xy);
        toSet.getProperties().put(CLICKED_KEY, false);

        toSet.setOnMouseClicked(e -> {
            if (!selectedDiceTracker || ((Boolean) toSet.getProperties().get(CLICKED_KEY))) {
                selectedDiceTracker = !selectedDiceTracker;
                Coord coord = (Coord) toSet.getUserData();
                // On click revert the CLICKED_KEY status
                toSet.getProperties().compute(CLICKED_KEY, (k, v) -> !(Boolean) v);
                toSet.setStyle(SHADOW_EFFECT +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? COLOR_HIGHLIGHTED : COLOR_HOVERED) +
                        ", 4, 5, 0, 0);\n");
                if ((Boolean) toSet.getProperties().get(CLICKED_KEY)) {
                    ToolParameterParserGUI.getInstance().setRoundTracker(coord);
                } else {
                    ToolParameterParserGUI.getInstance().setRoundTracker(null);
                }
            }
        });

        setHoverEffectDie(toSet);
    }

    public static void setEndTurnButtonHandler(Button endTurnButton) {

        setClickableButton(endTurnButton);

        endTurnButton.setOnMouseClicked(e -> {
            try {
                controller.sendTurnBeginChoice(0);
            } catch (GameException ge) {
                GameExceptionPopUp.show(ge);
            } catch (IOException e1) {
                log.warning(Arrays.toString(e1.getStackTrace()));
            }
        });
    }

    /**
     * Utility class to use for a consistent hover effect on the dice
     */
    public static void setHoverEffectDie(Node toSet) {
        toSet.setOnMouseEntered(e -> {
            toSet.setStyle(SHADOW_EFFECT +
                    (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? COLOR_HIGHLIGHTED : COLOR_HOVERED) +
                    ", 4, 5, 0, 0);\n");
            // Checks if the CLICKED_KEY property is true and changes how the highlight colors and size are displayed
        });

        toSet.setOnMouseExited(e ->
                toSet.setStyle(SHADOW_EFFECT +
                        (((Boolean) toSet.getProperties().get(CLICKED_KEY)) ? COLOR_HIGHLIGHTED + ", 4" : COLOR_HOVERED + ", 0") +
                        ", 5, 0, 0);\n"));
        // Necessary to prevent that de-hovering alters how CLICKED_KEY is displayed
    }

    public static void setController(Controller c) {
        controller = c;
    }

    public static void setConfirmButtonHandler(Button confirmButton) {

        setClickableButton(confirmButton);

        confirmButton.setOnMouseClicked(e -> {
            // We disable the interaction with the button during the execution of the Event
            confirmButton.disarm();

            ToolActionParameter tap = ToolParameterParserGUI.getInstance().createCommand();
            ToolParameterParserGUI.getInstance().reset();
            try {
                log.info("Beginning current state : " + controller.getGameBoard().getCurrentState());
                log.info("INIT Waiting for callback " + waitingCallback);
                if(waitingCallback) {
                    // Must use tap to complete the callback
                    try {
                        tap.setCallBack(true);
                        controller.useTap(tap);
                        log.info("Used TAP for CALLBACK current state : " + controller.getGameBoard().getCurrentState());
                    } catch (GameException ge) {
                        GameExceptionPopUp.show(ge);
                        log.info("Exception using callback thrown state : " + controller.getGameBoard().getCurrentState());
                        return;
                    }
                    waitingCallback = false;
                    log.info("setted false Waiting for callback " + waitingCallback);
                    controller.back();
                    log.info("Played callback normally, can go back to main menu : " + controller.getGameBoard().getCurrentState());
                    return;
                }


                if (tap.getPickedTool() != -1) {
                    // Using tool
                    try {
                        controller.sendTurnBeginChoice(2);
                        log.info("Switched to TOOLACTION current state : " + controller.getGameBoard().getCurrentState());
                    } catch (GameException ge) {
                        GameExceptionPopUp.show(ge);
                        log.info("Exception thrown (returning) current state : " + controller.getGameBoard().getCurrentState());
                        return;
                    }
                    try {
                        controller.useTap(tap);
                        log.info("Used TAP current state : " + controller.getGameBoard().getCurrentState());
                    } catch (GameException ge){
                        if(ge.isCallBack()){
                            waitingCallback = true;
                            log.info("Setting Waiting for callback "+ waitingCallback);
                            GameExceptionPopUp.show(ge);
                        } else {
                            GameExceptionPopUp.show(ge);
                            log.info("Exception thrown state : " + controller.getGameBoard().getCurrentState());
                            controller.back();
                            log.info("After back current state : "+controller.getGameBoard().getCurrentState());
                        }
                    }
                } else if (!tap.getCoords().isEmpty()) {
                    try {
                        controller.sendTurnBeginChoice(1);
                        log.info ("Switched to DICEACTION current state : " + controller.getGameBoard().getCurrentState());

                    } catch (GameException ge) {
                        GameExceptionPopUp.show(ge);
                        log.info ("Exception thrown (returning) current state : " + controller.getGameBoard().getCurrentState());
                        return;
                    }
                    DiceActionParameter dap = new DiceActionParameter();
                    dap.choseTarget(tap.getCoords().get(0).x,tap.getCoords().get(0).y);
                    dap.chooseDie(tap.getDicePoolTarget());
                    try {
                        boolean res = controller.chooseAndPlaceDie(dap);
                        log.info ("Used dice current state : " + controller.getGameBoard().getCurrentState());
                        if (!res) {
                            throw new GameException("Error placing dice");
                        }
                    } catch (GameException ge) {
                        GameExceptionPopUp.show(ge);
                        log.info("Exception thrown state : " + controller.getGameBoard().getCurrentState());
                        controller.back();
                        log.info("After back current state : " + controller.getGameBoard().getCurrentState());
                    }
                }
            } catch (IOException re) {
                log.warning(Arrays.toString(re.getStackTrace()));
            } catch (GameException ge) {
                log.info("General catch, why are we here????");
                GameExceptionPopUp.show(ge);
            }
        // When the event has finished we re-enable the button
        confirmButton.arm();
        });
    }
}
