
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class SimplePlacementStrategy extends PlacementStrategy {
    public SimplePlacementStrategy() {
    }

    public Boolean placeShips(PriorityQueue<Ship> ships, BattleGrid aGrid)
    {
        // Simple strategy to place ships vertically in consecutive columns
        int column = 3;
        while(! ships.isEmpty())
        {
            LinkedList<Point> location = new LinkedList<>();
            Ship aShip = ships.remove();
            for(int i = 1; i <= aShip.getLength(); i++)
            {
                location.add(new Point(column,i));
            }

            column++;
            aShip.setLocation(location);
            aGrid.addToGrid(aShip);
        }
        return true;
    }

}