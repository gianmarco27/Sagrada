package ingsw.Server.Utility;

import ingsw.Server.Player;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Custom class that implements and hold the logic related to players turns
 */
public class TurnHandler implements Serializable {

    private List<Player> playersOrder;
    private int currentPlayer;
    public boolean firstPhase;

    public TurnHandler() {
        this.playersOrder = new CopyOnWriteArrayList<>();
        this.currentPlayer = 0;
        this.firstPhase = true;
    }

    public TurnHandler(List<Player> players) {
        this();
        List<Player> shuffledPlayer = new CopyOnWriteArrayList<>(players);
        Collections.shuffle(shuffledPlayer);
        this.playersOrder.addAll(shuffledPlayer);
    }

    @Override
    public String toString() {
        if (playersOrder.size() == 0) {
            return "[ No player order set ] ";
        } else {
            StringBuilder rep = new StringBuilder();
            for (Player p : playersOrder) {
                rep.append(p.getName());
                rep.append("-");
            }
            rep.deleteCharAt(rep.length() - 1);
            return rep.toString();
        }
    };

    /**
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return playersOrder.get(currentPlayer);
    }

    /**
     * @return obtain a list containing the current players order
     */
    public List<Player> getPlayersOrder() {
        return playersOrder;
    }

    /**
     * @return if there is a next player for this round. The method is not side-effect free
     * after asking it moves the tracker to the following players
     */
    public boolean hasNextPlayer() {
        if (firstPhase && currentPlayer < playersOrder.size() - 1) {
            currentPlayer++;
            return true;
        } else if (firstPhase && currentPlayer == playersOrder.size() - 1) {
            firstPhase = false;
            return true;
        } else if (!firstPhase && currentPlayer > 0) {
            currentPlayer--;
            return true;
        } else if (!firstPhase && currentPlayer == 0) {
            firstPhase = true;
            currentPlayer = 0;
//            System.out.println("Round finish!");
            Collections.rotate(playersOrder,-1);
            return false;
        }
        return false;
    }
}
