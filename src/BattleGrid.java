
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class BattleGrid {

    /**
     * Default constructor
     */
    public BattleGrid() {
    }

    /**
     * 
     */
    public Integer length;

    /**
     * 
     */
    public Integer width;

    /**
     * 
     */
    public LinkedList<Point> shotHistory;

    public BattleGrid(int length, int width) {
        // TODO implement here
    }

    public void addToGrid(Ship ship) {
        // TODO implement here
    }

    public ShotResult attemptShot(Point aPoint) {
        return new ShotResult(false,"","");
    }

    public Boolean checkGameOver() {
        return true;
    }

    public void printShipLayout() {
        // TODO implement here
    }

    public void printShotMap() {
        // TODO implement here
    }

    private void recordShot(Point aPoint) {
        // TODO implement here
    }

}