package ingsw.Server.Objective;
import ingsw.Server.NoMoreBagElementException;
import ingsw.Server.Objective.*;

import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Bag of Objectives that handles the correct instantiation and retrieval of the Objective available in Sagrada.
 */
public class ObjectiveBag implements Serializable {

    private List<Objective> publicObjPool;
    private int publicObjLeft;

    private List<Objective> privateObjPool;
    private int privateObjLeft;

    /**
     * Constructor of ObjectiveBag which creates an instance for each of the available Objectives
     * and adds them to the List of available ones
     */
    public ObjectiveBag() {

        privateObjPool = new CopyOnWriteArrayList<>();
        publicObjPool = new CopyOnWriteArrayList<>();
        // Instantiate a card for each public
        publicObjPool.add(new RowColorVariety());
        publicObjPool.add(new ColumnColorVariety());
        publicObjPool.add(new ColumnShadeVariety());
        publicObjPool.add(new RowShadeVariety());
        publicObjPool.add(new ColorVariety());
        publicObjPool.add(new ShadeVariety());
        publicObjPool.add(new DeepShades());
        publicObjPool.add(new MediumShades());
        publicObjPool.add(new LightShades());
        publicObjPool.add(new ColorDiagonals());
        publicObjLeft = 10;
        // Instantiate a card for each private
        privateObjPool.add(new ShadesOfPurple());
        privateObjPool.add(new ShadesOfRed());
        privateObjPool.add(new ShadesOfYellow());
        privateObjPool.add(new ShadesOfGreen());
        privateObjPool.add(new ShadesOfBlue());
        privateObjLeft = 5;

        shuffleBag(publicObjPool);
        shuffleBag(privateObjPool);
    }
    /**
     * Utility for shuffling the Objective elements in the bag
     */
    private void shuffleBag(List<Objective> toShuffle) {
        Collections.shuffle(toShuffle);
    }

    /**
     * Obtains an Objective from the bag and removes it to keep the consistency
     * @return extracted Objective
     * @throws NoMoreBagElementException if there are no more elements left in the bag (unexpected)
     */
    public PrivateObjective getPrivateObj() throws NoMoreBagElementException {
        if (privateObjLeft == 0) {
            throw new NoMoreBagElementException("privateObjective");
        } else {
            PrivateObjective extracted = (PrivateObjective) privateObjPool.remove(privateObjPool.size() - 1);
            //System.out.println(extracted); // Better handle printing elsewhere
            privateObjLeft--;
            return extracted;
        }
    }

    /**
     * Extracts one Public Objective from the Pool and then decreases the remaining Objectives counter
     * @return extracted Objective
     * @throws NoMoreBagElementException if there are no more elements left in the bag (unexpected)
     */
    public PublicObjective getPublicObj() throws NoMoreBagElementException {
        if (publicObjLeft == 0) {
            throw new NoMoreBagElementException("publicObjective");
        } else {
            PublicObjective extracted = (PublicObjective) publicObjPool.remove(publicObjPool.size() - 1);
            //System.out.println(extracted); // Better handle printing elsewhere
            publicObjLeft--;
            return extracted;
        }
    }

}
