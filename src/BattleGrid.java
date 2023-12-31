
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class BattleGrid {

    public Integer length;
    public Integer width;
    public LinkedList<Point> shotHistory;
    public TreeSet<Point> quickShotLookup;
    public LinkedList<Ship> myShips;
    public long startTime = 0;
    public long endTime = 0;
    Point recyclePnt;

    public static final String LEFT = "LEFT";
    public static final String RIGHT = "RIGHT";
    public static final String UP = "UP";
    public static final String DOWN = "DOWN";

    class PointCompare implements Comparator<Point> {

        public int compare(Point a, Point b)
        {
            if((a.x == b.x) && (a.y == b.y))
                return 0;

            if(a.x == b.x)
                return (a.y < b.y) ? -1 : 1;

            if(a.y == b.y)
                return (a.x < b.x) ? -1 : 1;

            return (a.x < b.x) ? -1 : 1;
        }
    }

    public BattleGrid(int length, int width) {
        this.length = length;
        this.width = width;
        this.myShips = new LinkedList<>();
        this.shotHistory = new LinkedList<>();
        this.quickShotLookup = new TreeSet<>(new PointCompare());
        this.recyclePnt = new Point();
    }

    public BattleGrid clone()
    {
        BattleGrid cloneGrid = GameFactory.createGrid();
        Iterator<Ship> iter = myShips.iterator();
        while(iter.hasNext())
        {
            Ship aShip = iter.next();
            cloneGrid.addToGrid(aShip.clone());
        }
        return cloneGrid;
    }

    public boolean addToGrid(Ship ship) {

        //System.out.println("Ship Added To Grid " + ship.name + ":" + ship.length);
        myShips.add(ship);
        return true;
    }

    public synchronized ShotResult attemptShot(Point aPoint) {

        if(startTime == 0) {

            // start the attack timer on the first shot taken
            startTime = System.nanoTime();
            //System.out.println("Attack start= " + startTime);
        }

        ShotResult out = new ShotResult(false,"","");

        // See if the shot is duplicate, which is possible if it was made by a different
        // attack strategy (search strategy versus sink strategy).  If it's duplicate,
        // just treat it as a miss.  It has already been recorded, so no need to run
        // through the attack code
        if(quickShotLookup.contains(aPoint))
            return out;

        Iterator<Ship> shipIterator = myShips.iterator();
        while(shipIterator.hasNext())
        {
            Ship aShip = shipIterator.next();
            if(aShip.attemptHit(aPoint))
            {
                out = new ShotResult(true, aShip.getName(), aShip.getStatus());
                break;
            }
        }

        //if(! out.isHit)
        //    System.out.println("Miss X=" + aPoint.x + ",Y=" + aPoint.y);

        //Make a record of the attack shot
        recordShot(aPoint);

        return out;
    }

    public boolean checkGameOver() {

        boolean out = true;

        Iterator<Ship> shipIterator = myShips.iterator();
        while(shipIterator.hasNext())
        {
            Ship aShip = shipIterator.next();
            if(! aShip.getStatus().equals(Ship.SUNK))
            {
                out = false;
            }
        }

        if(out)
        {
            // Set the endTime the first moment we realize that the game is over
            if(endTime == 0) {

                endTime = System.nanoTime();
                //System.out.println("Attack end= " + endTime);
            }
        }

        return out;
    }

    public int findOpenSpaces(Point aPoint, String direction) {

        // This algorithm returns how many open/unhit spaces there are in a given direction
        // up to the boundary
        int out = 0;
        int yValue = aPoint.y;
        int xValue = aPoint.x;

        switch (direction) {
            case LEFT -> {
                for (int i = aPoint.x - 1; i > 0; i--) {
                    recyclePnt.setLocation(i, yValue);
                    if (!quickShotLookup.contains(recyclePnt))
                        out++;
                    else
                        break;
                }
            }
            case RIGHT -> {
                for (int i = aPoint.x + 1; i <= width; i++) {
                    recyclePnt.setLocation(i, yValue);
                    if (!quickShotLookup.contains(recyclePnt))
                        out++;
                    else
                        break;
                }
            }
            case UP -> {
                for (int i = aPoint.y - 1; i > 0; i--) {
                    recyclePnt.setLocation(xValue, i);
                    if (!quickShotLookup.contains(recyclePnt))
                        out++;
                    else
                        break;
                }
            }
            case DOWN -> {
                for (int i = aPoint.y + 1; i <= length; i++) {
                    recyclePnt.setLocation(xValue, i);
                    if (!quickShotLookup.contains(recyclePnt))
                        out++;
                    else
                        break;
                }
            }
        }

        return out;
    }

    public void printGrid() {

        for(int i = 1; i <= width; i++)
        {
            System.out.append("|");
            for(int j = 1; j <= length; j++)
            {
                // See if we have a shot at this position
                recyclePnt.setLocation(j, i);
                if(quickShotLookup.contains(recyclePnt))
                {
                    // See if we have a ship at this location
                    String shipAbbrev = checkShipLocation(recyclePnt);
                    if(! shipAbbrev.isEmpty())
                    {
                        System.out.append(shipAbbrev + "|");
                    }
                    else
                        System.out.append("X|");
                }
                else
                    System.out.append("O|");
            }

            System.out.println();
        }
    }

    String checkShipLocation(Point aPoint)
    {
        String out = "";

        // Make sure this location is on the grid in the first place.  Return F for fail.
        if((aPoint.x > length) || (aPoint.y > width))
            return "F";

        Iterator<Ship> shipIterator = myShips.iterator();
        while(shipIterator.hasNext())
        {
            Ship aShip = shipIterator.next();
            if(aShip.hasLocation(aPoint))
            {
                out = aShip.abbreviation;
                break;
            }
        }
        return out;
    }

    public long getDuration() {

        // This is in nanoseconds.  If we use the millesecond clock it will just be zero, so divide the
        // back to milliseconds in the spreadsheet.  Sometimes its negative, just send it to 1
        long duration = endTime - startTime;
        return (duration > 0) ? duration : 1;
    }

    private void recordShot(Point aPoint) {

        shotHistory.add(aPoint);
        quickShotLookup.add(aPoint);
    }
}