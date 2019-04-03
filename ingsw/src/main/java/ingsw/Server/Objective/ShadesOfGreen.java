package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;

public class ShadesOfGreen extends PrivateObjective {

    public ShadesOfGreen() {
        this.description = "Sum of Values on Green Dice.";
        this.objValue = 1;
        this.cardName = "Shades Of Green";
        this.cardId = 13;
        this.privateColor = DiceColor.GREEN;
    }
}
