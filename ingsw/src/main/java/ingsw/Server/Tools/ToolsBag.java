package ingsw.Server.Tools;
import ingsw.Server.GameFlow.GameType;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Tools.*;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bag of Tools that handles the correct instantiation of the Tools available in Sagrada.
 */
public class ToolsBag implements Serializable {

    private List<Tool> toolsPool;
    private int toolsLeft;

    /**
     * Constructor of ToolsBag which creates an instance for each of the available tools
     * and adds them to the List of available ones
     */
    public ToolsBag() {
        this(GameType.MULTIPLAYER);
    }

    public ToolsBag(GameType gameType) {
        toolsPool = new CopyOnWriteArrayList<>();
        toolsPool.add(new GlazingHammer(gameType));
        toolsPool.add(new EglomiseBrush(gameType));
        toolsPool.add(new Lathekin(gameType));
        toolsPool.add(new GrozingPliers(gameType));
        toolsPool.add(new CopperFoilBurnishes(gameType));
        toolsPool.add(new LensCutter(gameType));
        toolsPool.add(new FluxBrush(gameType));
        toolsPool.add(new RunningPliers(gameType));
        toolsPool.add(new CorkBackedStraightedge(gameType));
        toolsPool.add(new GrindingStone(gameType));
        toolsPool.add(new FluxRemover(gameType));
        toolsPool.add(new TapWheel(gameType));
        shuffleBag();
        toolsLeft = toolsPool.size();
    }
    /**
     * Getter for remaining tools
     */
    public int getToolsLeft() {
        return toolsLeft;
    }

    /**
     * Utility for shuffling the tools elements in the bag
     */
    private void shuffleBag() {
        Collections.shuffle(toolsPool);
    }

    /**
     * Obtains a tool from the bag and removes it to keep the consistency
     * @return extracted tools
     * @throws NoMoreBagElementException if there are no more elements left in the bag (unexpected)
     */
    public Tool getTools() throws NoMoreBagElementException {
        if (toolsLeft == 0) {
            throw new NoMoreBagElementException("tools");
        } else {
            Tool extracted = toolsPool.remove(toolsPool.size() - 1);
            //System.out.println(extracted); // Better handle printing elsewhere
            toolsLeft--;
            return extracted;
        }
    }
}
