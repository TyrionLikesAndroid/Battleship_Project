
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class Ship {

    public String name;
    public String abbreviation;
    public int length;
    public HashMap<Point, Boolean> location;
    public static final String HEALTHY = "HEALTHY";
    public static final String SHOT = "SHOT";
    public static final String SUNK = "SUNK";

    public Ship(String name, String abbreviation, int length) {
        this.name = name;
        this.length = length;
        this.abbreviation = abbreviation;
        location = new HashMap<>();

        //System.out.println("Ship Created " + this.name + ":" + this.length);
    }

    public Ship clone()
    {
        Ship cloneShip = new Ship(getName(), abbreviation, getLength());
        LinkedList<Point> clonePoints = new LinkedList<>();

        Iterator<Point> myPoints = location.keySet().iterator();
        while(myPoints.hasNext())
        {
            Point iPoint = myPoints.next();
            clonePoints.add(GameFactory.newPoint(iPoint.x, iPoint.y));
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

    public boolean attemptHit(Point aPoint) {

        boolean out = false;

        Iterator<Point> myPoints = location.keySet().iterator();
        while(myPoints.hasNext())
        {
            Point iPoint = myPoints.next();
            if(iPoint.equals(aPoint))
            {
                recordHit(aPoint);
                out = true;

                //System.out.println("Hit: X=" + aPoint.x + ",Y=" + aPoint.y + ",Ship=" + this.getName() + ",status=" + this.getStatus());

                break;
            }
        }

        return out;
    }

    boolean hasLocation(Point aPoint)
    {
        boolean out = false;

        Iterator<Point> myPoints = location.keySet().iterator();
        while(myPoints.hasNext())
        {
            Point iPoint = myPoints.next();
            if(iPoint.equals(aPoint))
            {
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