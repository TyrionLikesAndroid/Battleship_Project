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
            boolean shipPlaced = false;

            int rotation = rand.nextInt(2);
            //System.out.println("rotation " + rotation);

            if (rotation == 1) {
                int row = rand.nextInt(rows) + 1;
                //this is to make sure the ship is placed horizontally within the 10x10 grid
                int col = rand.nextInt(columns - aShip.getLength()) + 1;
                //System.out.println("row = " + row + " col = " + col);
                String check = aGrid.checkShipLocation(GameFactory.newPoint(col, row));
                //System.out.println(check);

                while(! shipPlaced) {
                    //check if the starting position is occupied
                    while (!check.equals("")) {
                        row = rand.nextInt(rows) + 1;
                        col = rand.nextInt(columns - aShip.getLength()) + 1;
                        check = aGrid.checkShipLocation(GameFactory.newPoint(col, row));
                    }
                    //System.out.println("rerun: row = " + row + " col = " + col);

                    for (int x = col; x < col + aShip.getLength(); x++) {
                        //Make sure we aren't overlapping another ship
                        Point aPoint = GameFactory.newPoint(row, x);
                        check = aGrid.checkShipLocation(aPoint);
                        if (check.equals(""))
                        {
                            //System.out.println("Good location (" + aPoint.x + "," + aPoint.y + ")");
                            location.add(aPoint);
                        }
                        else
                        {
                            //System.out.println("Horizontal ships overlapping, try again");
                            location.clear();  // reset the list so we can try again
                            break;
                        }

                        // Return true if the whole ship got placed
                        if(location.size() == aShip.length)
                            shipPlaced = true;
                    }
                }

                aShip.setLocation(location);
                aGrid.addToGrid(aShip);
                //vertical
            } else {
                int col = rand.nextInt(columns) + 1;
                //this is to make sure the ship is placed vertically within the 10x10 grid
                int row = rand.nextInt(rows - aShip.getLength()) + 1;
                //System.out.println("row = " + row + " col = " + col);
                String check = aGrid.checkShipLocation(GameFactory.newPoint(col, row));
                //System.out.println(check);

                while(! shipPlaced) {
                    //check if the random position is occupied
                    while (!check.equals("")) {
                        row = rand.nextInt(rows) + 1;
                        col = rand.nextInt(columns - aShip.getLength()) + 1;
                        check = aGrid.checkShipLocation(GameFactory.newPoint(col, row));
                    }
                    //System.out.println("rerun: row = " + row + " col = " + col);
                    for (int y = row; y < row + aShip.getLength(); y++) {
                        //Make sure we aren't overlapping another ship
                        Point aPoint = GameFactory.newPoint(y, col);
                        check = aGrid.checkShipLocation(aPoint);
                        if (check.equals(""))
                        {
                            //System.out.println("Good location (" + aPoint.x + "," + aPoint.y + ")");
                            location.add(aPoint);
                        }
                        else
                        {
                            //System.out.println("Vertical ships overlapping, try again");
                            location.clear();  // reset the list so we can try again
                            break;
                        }

                        // Return true if the whole ship got placed
                        if(location.size() == aShip.length)
                            shipPlaced = true;
                    }
                }
                aShip.setLocation(location);
                aGrid.addToGrid(aShip);
            }
        }
        return true;
    }

}