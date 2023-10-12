import java.awt.*;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Random;

/**
 * 
 */
public class RandomPlacementStrategy extends PlacementStrategy {
    public RandomPlacementStrategy() {

        super();
    }

    public boolean placeShips(PriorityQueue<Ship> ships, BattleGrid aGrid)
    {
        int rows = aGrid.width;
        int columns = aGrid.length;
        Random rand = new Random();

        //horizontal
        //random row (width) & column (length) to place the ship
        while (! ships.isEmpty()) {
            LinkedList<Point> location = new LinkedList<>();
            Ship aShip = ships.remove();
            int rotation = rand.nextInt(2);
            System.out.println("rotation " + rotation);
            if (rotation == 1) {
                int row = rand.nextInt(rows);
                //this is to make sure the ship is placed horizontally within the 10x10 grid
                int col = rand.nextInt(columns - aShip.getLength());
                System.out.println("row = " + row + " col = " + col);
                String check = aGrid.checkShipLocation(new Point(col, row));
                System.out.println(check);
                //check if the random position is occupied
                while (!check.equals("")) {
                    row = rand.nextInt(rows);
                    col = rand.nextInt(columns - aShip.getLength());
                    check = aGrid.checkShipLocation(new Point(col, row));
                }
                System.out.println("rerun: row = " + row + " col = " + col);

                for (int x = col; x < col + aShip.getLength(); x++) {
                    location.add(new Point(row, x));
                }
                aShip.setLocation(location);
                aGrid.addToGrid(aShip);
                //vertical
            } else {
                int col = rand.nextInt(columns);
                //this is to make sure the ship is placed vertically within the 10x10 grid
                int row = rand.nextInt(rows - aShip.getLength());
                System.out.println("row = " + row + " col = " + col);
                String check = aGrid.checkShipLocation(new Point(col, row));
                System.out.println(check);
                //check if the random position is occupied
                while (!check.equals("")) {
                    row = rand.nextInt(rows);
                    col = rand.nextInt(columns - aShip.getLength());
                    check = aGrid.checkShipLocation(new Point(col, row));
                }
                System.out.println("rerun: row = " + row + " col = " + col);
                for (int y = row; y < row + aShip.getLength(); y++) {
                    location.add(new Point(y, col));
                }
                aShip.setLocation(location);
                aGrid.addToGrid(aShip);
            }
        }
        return true;
    }

}