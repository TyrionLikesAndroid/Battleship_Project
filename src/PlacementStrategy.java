
import java.util.*;

/**
 * 
 */
public abstract class PlacementStrategy {

    /**
     * Default constructor
     */
    public PlacementStrategy() {
    }

    public abstract boolean placeShips(PriorityQueue<Ship> ships, BattleGrid aGrid);

}