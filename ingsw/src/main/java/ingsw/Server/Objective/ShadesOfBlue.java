package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;

public class ShadesOfBlue extends PrivateObjective{

    public ShadesOfBlue() {
        this.description = "Sum of Values on Blue Dice.";
        this.objValue = 1;
        this.cardName = "Shades Of Blue";
        this.cardId = 14;
        this.privateColor = DiceColor.BLUE;
    }
}
