
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class Ship {

    public String name;
    public int length;
    public HashMap<Point, Boolean> location = new HashMap<>();
    public static String HEALTHY = new String("HEALTHY");
    public static String SHOT = new String("SHOT");
    public static String SUNK = new String("SUNK");

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
    }

    public void setLocation(LinkedList<Point> pointList) {
        // TODO implement here
    }

    public Boolean attemptHit(Point aPoint) {
        return false;
    }

    public String getStatus() {
        return HEALTHY;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    private void recordHit(Point aPoint) {
        // TODO implement here
    }

}