package ingsw.Server.Utility;

import ingsw.Server.Dice.Dice;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import static java.lang.Math.max;

public class RoundTracker implements Serializable {
    private CopyOnWriteArrayList<CopyOnWriteArrayList<Dice>> roundTracker;

    public static final int ROUNDMAX = 10;

    public RoundTracker() {
        roundTracker = new CopyOnWriteArrayList<>();
        for (int i = 0; i < ROUNDMAX; i++)
            roundTracker.add(new CopyOnWriteArrayList<>());
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(boolean colored) {
        int height = 0;
        StringBuilder rep = new StringBuilder();
        rep.append("# Roundtracker #\n");
        rep.append("ROUND: ");
        for (int round = 0; round < ROUNDMAX; round++) {
            rep.append("  " + (round + 1) + "   ");
            height = max(height, (!roundTracker.get(round).isEmpty()) ? roundTracker.get(round).size() : 0);
        }
        rep.append("\nDICE:  ");
        for (int position = 0; position < height; position++) {
            for (int round = 0; round < ROUNDMAX; round++) {
                if (roundTracker.get(round).size() <= position) {
                    rep.append("     ");
                } else {
                    Dice toAdd = peekPosition(round, position);
                    rep.append(toAdd.toString(colored) + " ");
                }
                rep.append(" ");
            }
            rep.append("\n       ");
        }
        return rep.toString();
    }

    /**
     * @param round
     * @return obtain the List of Dice of a given round
     */
    public CopyOnWriteArrayList<Dice> peekPosition(int round) {
        if (round < 0 || round > ROUNDMAX - 1) {
            //System.out.println("Ignored move, not a valid target"); // consider an Exception
            return null;
        } else {
            return roundTracker.get(round);
        }
    }

    /**
     * @param round
     * @param position
     * @return obtain the Die of a given round and specific position
     */
    public Dice peekPosition(int round, int position) {
        if (round < 0 || round > ROUNDMAX - 1) {
            //System.out.println("Ignored move, not a valid target"); // consider an Exception
            return null;
        } else {
            if (position < 0 || position >= roundTracker.get(round).size()) {
                //System.out.println("Ignored move, not a valid target"); // consider an Exception
                return null;
            } else return roundTracker.get(round).get(position);
        }
    }

    /**
     * @param round
     * @param position
     * @param toPlace dice to Place in a certain round at a certain position
     */
    public void placePosition(int round, int position, Dice toPlace) {
        if (round < 0 || round > ROUNDMAX - 1) {
            //System.out.println("Ignored move, not a valid target"); // consider an Exception
        } else {
            if (position < 0 || position >= roundTracker.get(round).size()) {
                //System.out.println("Ignored move, not a valid target"); // consider an Exception
            } else {
                roundTracker.get(round).set(position, toPlace); // Overwrite existing Dice
            }
        }
    }

    /**
     * @param round
     * @param toPlace dice to Place in a certain round
     */
    public void placeDice(int round, Dice toPlace) {
        if (round < 0 || round > ROUNDMAX - 1) {
            //System.out.println("Ignored move, not a valid target"); // consider an Exception
        } else {
            roundTracker.get(round).add(toPlace); //Place Dice
        }
    }

    /**
     * Remove the die contained in a certain round and specific position
     * @param round
     * @param position
     */
    public void emptyPosition(int round, int position) {
        if (round < 0 || round > ROUNDMAX - 1) {
            //System.out.println("Ignored move, not a valid target"); // consider an Exception
        } else {
            if (position < 0 || position >= roundTracker.get(round).size()) {
                //System.out.println("Ignored move, not a valid target"); // consider an Exception
            } else {
                roundTracker.get(round).remove(position);
            }
        }
    }

    /**
     * @param round
     * @param position
     * @param toSwap
     * @return the dice in position round.position, it leaves a toSwap dice at that place
     */
    public Dice swapDice(int round, int position, Dice toSwap) {
        Dice toRet = peekPosition(round, position);
        placePosition(round, position, toSwap);
        return toRet;
    }

    /**
     * @param round
     * @param position
     * @return The dice in position round.position,
     */
    public Dice popDice(int round, int position) {
        Dice toRet = peekPosition(round, position);
        emptyPosition(round, position);
        return toRet;
    }
}
