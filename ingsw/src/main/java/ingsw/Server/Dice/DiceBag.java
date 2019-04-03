package ingsw.Server.Dice;


import ingsw.Server.NoMoreBagElementException;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bag of Dice that handles the correct instantiation of the Dice available in Sagrada.
 */
public class DiceBag implements Serializable {

    private List<Dice> dicePool; // Discussion List vs CopyOnWriteArrayList
    private int diceLeft;

    /**
     * DiceBag constructor, spawns 18 dices of each color inside the bag with randomic values
     * then shuffles the extraction order
     */
    public DiceBag() {
        dicePool = new CopyOnWriteArrayList<Dice>();
        for (DiceColor c : DiceColor.values()) {
            for (int value = 0; value < 18; value++) {
                dicePool.add(new Dice(c));
            }
        }
        this.diceLeft = 90;
        this.shuffleBag(); // We rely on this behaviour to avoid non-deterministic approach
    }

    /**
     * @return a string containing all the dice remaining in the bag
     */
    @Override
    public String toString() {
        if (dicePool.size() == 0) {
            return "[ Empty DiceBag ] ";
        } else {
            StringBuilder rep = new StringBuilder();
            for (Dice d : dicePool) {
                rep.append(d);
                rep.append("-");
            }
            rep.deleteCharAt(rep.length() - 1);
            return rep.toString();
        }
    }

    /**
     * Utility for shuffling the Dice elements in the bag
     */
    public void shuffleBag() {
        Collections.shuffle(dicePool);
    }

    /**
     * @return the number of Dice remaining in the bag
     */
    public int diceLeft() {
        return diceLeft;
    }

    /**
     * Extracts
     *
     * @return
     * @throws NoMoreBagElementException
     */
    public Dice extractDice() throws NoMoreBagElementException {
        if (diceLeft == 0) {
            throw new NoMoreBagElementException("Dice");
        } else {
            Dice extracted = dicePool.remove(dicePool.size() - 1);
            //System.out.println(extracted); // Better handle printing elsewhere
            diceLeft--;
            return extracted;
        }
    }

    public List<Dice> extractDice(int n) throws NoMoreBagElementException {
        List<Dice> extractedDice = new CopyOnWriteArrayList<Dice>();
        for (int i = 0; i < n; i++) {
            extractedDice.add(extractDice());
        }
        return extractedDice;
    }

    public void addDice(Dice die) {
        dicePool.add(die);
        this.shuffleBag();
        diceLeft++;
    }
}