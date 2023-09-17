
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class BattleGrid {

    public Integer length;
    public Integer width;
    public LinkedList<Point> shotHistory;
    public LinkedList<Ship> myShips;

    public BattleGrid(int length, int width) {
        this.length = length;
        this.width = width;
        this.myShips = new LinkedList<Ship>();
        this.shotHistory = new LinkedList<Point>();
    }

    public BattleGrid clone()
    {
        BattleGrid cloneGrid = new BattleGrid(this.length, this.width);
        Iterator<Ship> iter = myShips.iterator();
        while(iter.hasNext())
        {
            Ship aShip = iter.next();
            cloneGrid.addToGrid(aShip.clone());
        }
        return cloneGrid;
    }

    public Boolean addToGrid(Ship ship) {

        System.out.println("Ship Added To Grid " + ship.name + ":" + ship.length);
        myShips.add(ship);
        return true;
    }

    public ShotResult attemptShot(Point aPoint) {

        ShotResult out = new ShotResult(false,"","");
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
        return out;
    }

    public Boolean checkGameOver() {

        Boolean out = true;

        Iterator<Ship> shipIterator = myShips.iterator();
        while(shipIterator.hasNext())
        {
            Ship aShip = shipIterator.next();
            if(! aShip.getStatus().equals(Ship.SUNK))
            {
                out = false;
            }
        }
        return out;
    }

    public void printShipLayout() {
        // TODO implement here
    }

    public void printShotMap() {
        // TODO implement here
    }

    private void recordShot(Point aPoint) {
        shotHistory.add(aPoint);
    }

}