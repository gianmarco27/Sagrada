package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;

public class ShadesOfRed extends PrivateObjective {

    public ShadesOfRed() {
        this.description = "Sum of Values on Red Dice.";
        this.objValue = 1;
        this.cardName = "Shades Of Red";
        this.cardId = 11;
        this.privateColor = DiceColor.RED;
    }
}
