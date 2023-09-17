
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class Ship {

    public String name;
    public int length;
    public HashMap<Point, Boolean> location;
    public static String HEALTHY = new String("HEALTHY");
    public static String SHOT = new String("SHOT");
    public static String SUNK = new String("SUNK");

    public Ship(String name, int length) {
        this.name = name;
        this.length = length;
        location = new HashMap<>();

        System.out.println("Ship Created " + this.name + ":" + this.length);
    }

    public Ship clone()
    {
        Ship cloneShip = new Ship(getName(), getLength());
        LinkedList<Point> clonePoints = new LinkedList<>();

        Iterator<Point> myPoints = location.keySet().iterator();
        while(myPoints.hasNext())
        {
            Point iPoint = myPoints.next();
            clonePoints.add(new Point(iPoint.x, iPoint.y));
        }
        cloneShip.setLocation(clonePoints);
        return cloneShip;
    }

    public void setLocation(LinkedList<Point> pointList) {

        Iterator<Point> pIter = pointList.iterator();
        while(pIter.hasNext())
        {
            Point aPoint = pIter.next();
            location.put(aPoint, false);  // Initial state is not hit
        }
    }

    public Boolean attemptHit(Point aPoint) {

        Boolean out = false;

        Iterator<Point> myPoints = location.keySet().iterator();
        while(myPoints.hasNext())
        {
            Point iPoint = myPoints.next();
            if(iPoint.equals(aPoint))
            {
                recordHit(aPoint);
                out = true;
                break;
            }
        }
        return out;
    }

    public String getStatus()
    {
        int numHits = 0;

        Iterator<Boolean> myHits = location.values().iterator();
        while(myHits.hasNext())
        {
            Boolean iHit = myHits.next();
            if(iHit)
                numHits++;
        }

        String out = HEALTHY;
        if(numHits > 0)
            out = (numHits < length ? SHOT : SUNK);

        return out;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    private void recordHit(Point aPoint) {
        location.put(aPoint,true);
    }

}