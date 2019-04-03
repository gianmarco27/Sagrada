package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;

public class ShadesOfYellow extends PrivateObjective {

    public ShadesOfYellow() {
        this.description = "Sum of Values on Yellow Dice.";
        this.objValue = 1;
        this.cardName = "Shades Of Yellow";
        this.cardId = 12;
        this.privateColor = DiceColor.YELLOW;
    }
}
