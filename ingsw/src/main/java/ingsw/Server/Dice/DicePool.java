package ingsw.Server.Dice;

import ingsw.Server.NoMoreBagElementException;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;

/**
 * Pool object with the purpose of containing the extracted Dice for the current turn
 */
public class DicePool implements Serializable {

    private List<Dice> dicePool;
    private int diceLeft;

    /**
     * Constructor of the DicePool which instantiates it to an empty list
     */
    public DicePool() {
        this.dicePool = new CopyOnWriteArrayList<>();
        this.diceLeft = 0;
    }

    /**
     * @return a string containing all the dice remaining in the pool
     */
    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean colored) {
        if (dicePool.size() == 0) { return "[ Empty DicePool ] ";
        } else {
            StringBuilder rep = new StringBuilder();
            rep.append("DICE-POOL\n");
            for (int i = 0; i < dicePool.size(); i++) {
                rep.append(" " + i + "   ");
            }
            rep.append("\n");
            for (Dice d : dicePool) {
                rep.append(d.toString(colored));
                rep.append(" ");
            }
            rep.deleteCharAt(rep.length() - 1);
            rep.append("\n");
            return rep.toString();
        }
    }

    /**
     * Empty all the elements hold in the dicePool
     * Used at the end of the round to reset the dice available
     */
    public void emptyDicePool() {
        this.dicePool = new CopyOnWriteArrayList<>();
        this.diceLeft = 0;
    }

    /**
     * Adds a given Die to the DicePool and increments the number of remaining Dice
     * @param die
     */
    public void addDice(Dice die) {
        dicePool.add(die);
        diceLeft++;
    }

    /**
     * Retrieves the List of all the Dice in the DicePool
     * @return dicePool reference to the actual dicePool
     */
    public List<Dice> getAllDice() {
        return dicePool;
    }

    /**
     * Retrieves the Die at the given index from the DicePool
     * @param idx index of the Die in DicePool
     * @return extracted
     * @throws NoMoreBagElementException if there is no dice in the Pool
     */
    public Dice getSingleDice(int idx) throws NoMoreBagElementException {
        if (diceLeft == 0) {
            throw new NoMoreBagElementException("Dice Pool");
        } else {
            Dice extracted = dicePool.remove(idx);
            diceLeft--;
            return extracted;
        }
    }

    /**
     * Get dice at position idx without removing it
     *  @param idx index of the Die in DicePool
     *  @return extracted
     *  @throws NoMoreBagElementException if there is no dice in the Pool
     */
    public Dice peekDice(int idx) throws NoMoreBagElementException {
        if (diceLeft == 0) {
            throw new NoMoreBagElementException("Dice Pool");
        } else {
            return dicePool.get(idx);
        }
    }
    /**
     * Allows to swap a Die at a given index with a given Die
     *
     * @param die external Die to swap
     * @param idx index of the Die in DicePool to swap
     * @return the Dice that has been swapped from the Pool
     * @throws NoMoreBagElementException
     */
    public Dice swapDice(Dice die, int idx) throws NoMoreBagElementException {
        if (diceLeft == 0) {
            throw new NoMoreBagElementException("Dice Pool");
        } else {
            Dice swappedDie = peekDice(idx);
            dicePool.set(idx, die);
            return swappedDie;
        }
    }
}
