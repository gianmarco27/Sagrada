package ingsw.Server.Objective;

import ingsw.Server.Dice.DiceColor;

public class ShadesOfPurple extends PrivateObjective {

    public ShadesOfPurple() {
        this.description = "Sum of Values on Purple Dice.";
        this.objValue = 1;
        this.cardName = "Shades Of Purple";
        this.cardId = 15;
        this.privateColor = DiceColor.PURPLE;
    }
}
