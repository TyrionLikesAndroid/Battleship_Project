
import java.util.*;

/**
 * 
 */
public abstract class PlacementStrategy {

    /**
     * Default constructor
     */
    public void PlacementStrategy() {
    }

    public abstract Boolean placeShips(PriorityQueue<Ship> ships, BattleGrid aGrid);

}