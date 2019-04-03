package ingsw.Server.Actions;

import ingsw.Server.Utility.Coord;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Class that handle all the Parameters related to tools usage. CallBack mean a tool action is divided in multiple parts
 */
public class ToolActionParameter implements Serializable {
    int pickedTool;
    List<Coord> coords;
    boolean workOnDicePool;
    int dicePoolTarget;
    int payDicePool;
    boolean workOnRoundTracker;
    int roundTrackerTargetRound;
    int roundTrackerTargetOption;
    boolean callBack;
    int valueChangeTo; // Tools 1, 6, 11

    /**
     * Default constructor that initialize all the attributes
     */
    public ToolActionParameter() {
        this.coords = new CopyOnWriteArrayList<>();
        pickedTool = -1;
        dicePoolTarget = -1;
        payDicePool = -1;
        roundTrackerTargetRound = -1;
        roundTrackerTargetOption = -1;
        valueChangeTo = -1;
    }

    @Override
    public String toString() {
        StringBuilder rep = new StringBuilder();
        rep.append("Tool Action Parameter\n");
        rep.append("Tool Pick " + pickedTool + "\n");
        rep.append("COORDS :");
        for (Coord c : coords) {
            rep.append(c);
            rep.append("\n");
        }
        rep.append("DP " + workOnDicePool + " " + dicePoolTarget + "\n");
        rep.append("RT " + workOnRoundTracker + " " + roundTrackerTargetRound + roundTrackerTargetOption + "\n");
        rep.append("CALLBACK " + callBack + "\n");
        rep.append("VALCHANGE " + valueChangeTo + "\n");
        rep.append("PAYDICE " + payDicePool + "\n");
        return rep.toString();
    }

    /**
     * Generic Getter and Setter
     */

    public int getPickedTool() {
        return pickedTool;
    }

    public void setPickedTool(int pickedTool) {
        this.pickedTool = pickedTool;
    }

    public int getPayDicePool() {
        return payDicePool;
    }

    public void setPayDicePool(int payDicePool) {
        this.payDicePool = payDicePool;
    }

    public List<Coord> getCoords() {
        return coords;
    }

    public void addCoords(int x, int y) {
        this.coords.add(new Coord(x, y));
    }

    public boolean isWorkOnDicePool() {
        return workOnDicePool;
    }

    public void setWorkOnDicePool(boolean workOnDicePool) {
        this.workOnDicePool = workOnDicePool;
    }

    public int getDicePoolTarget() {
        return dicePoolTarget;
    }

    public void setDicePoolTarget(int dicePoolTarget) {
        this.dicePoolTarget = dicePoolTarget;
    }

    public boolean isWorkOnRoundTracker() {
        return workOnRoundTracker;
    }

    public void setWorkOnRoundTracker(boolean workOnRoundTracker) {
        this.workOnRoundTracker = workOnRoundTracker;
    }

    public int getRoundTrackerTargetRound() {
        return roundTrackerTargetRound;
    }

    public int getRoundTrackerTargetOption() {
        return roundTrackerTargetOption;
    }

    public void setRoundTrackerTargetRound(int roundTrackerTarget) {
        this.roundTrackerTargetRound = roundTrackerTarget;
    }

    public void setRoundTrackerTargetOption(int roundTrackerTarget) {
        this.roundTrackerTargetOption = roundTrackerTarget;
    }

    public boolean isCallBack() {
        return callBack;
    }

    public void setCallBack(boolean callBack) {
        this.callBack = callBack;
    }

    public int getValueChangeTo() {
        return valueChangeTo;
    }

    public void setValueChangeTo(int valueChangeTo) {
        this.valueChangeTo = valueChangeTo;
    }

    public void resetCoordinates() {
        this.coords = new CopyOnWriteArrayList<>();
    }
}
