import java.awt.*;
import java.util.Vector;

public class GameFactory {

    public static final String BATTLESHIP = "Battleship";
    public static final String SUBMARINE = "Submarine";
    public static final String CARRIER = "Carrier";
    public static final String CRUISER = "Cruiser";
    public static final String DESTROYER = "Destroyer";

    private static final int BAT_LENGTH = 4;
    private static final int SUB_LENGTH = 3;
    private static final int CRU_LENGTH = 3;
    private static final int CAR_LENGTH = 5;
    private static final int DES_LENGTH = 2;

    private static Vector<BattleGrid> bGridVector;
    private static int bGridCounter = 0;
    private static Vector<Point> pntVector;
    private static int pntCounter = 0;
    private static Vector<Ship> carrierVector;
    private static int carrierCounter = 0;
    private static Vector<Ship> bshipVector;
    private static int bshipCounter = 0;
    private static Vector<Ship> subVector;
    private static int subCounter = 0;
    private static Vector<Ship> cruiserVector;
    private static int cruiserCounter = 0;
    private static Vector<Ship> destVector;
    private static int destCounter = 0;

    public static void initialize(int length, int width)
    {
        BattleGrid aGrid;
        Point aPoint;
        Ship aShip;

        bGridVector = new Vector<>();
        pntVector = new Vector<>();
        carrierVector = new Vector<>();
        bshipVector = new Vector<>();
        subVector = new Vector<>();
        cruiserVector = new Vector<>();
        destVector = new Vector<>();

        for(int i = 0; i < 2000; i++)
        {
            aGrid = new BattleGrid(length,width);
            bGridVector.add(i,aGrid);
        }

        for(int i = 0; i < 2000000; i++)
        {
            aPoint = new Point();
            pntVector.add(i,aPoint);
        }

        for(int i = 0; i < 1000; i++)
        {
            aShip = new Ship(BATTLESHIP, "B", BAT_LENGTH);
            bshipVector.add(i,aShip);
            aShip = new Ship(CARRIER, "C", CAR_LENGTH);
            carrierVector.add(i,aShip);
            aShip = new Ship(DESTROYER, "D", DES_LENGTH);
            destVector.add(i,aShip);
            aShip = new Ship(SUBMARINE, "S", SUB_LENGTH);
            subVector.add(i,aShip);
            aShip = new Ship(CRUISER, "R", CRU_LENGTH);
            cruiserVector.add(i,aShip);
        }
    }

    public static Ship createShip(String aString) {

        Ship out = null;
        switch (aString) {
            case BATTLESHIP -> {
                bshipCounter++;
                out = bshipVector.get(bshipCounter);
            }
            case CARRIER -> {
                carrierCounter++;
                out = carrierVector.get(carrierCounter);
            }
            case DESTROYER -> {
                destCounter++;
                out = destVector.get(destCounter);
            }
            case SUBMARINE -> {
                subCounter++;
                out = subVector.get(subCounter);
            }
            case CRUISER -> {
                cruiserCounter++;
                out = cruiserVector.get(cruiserCounter);
            }
        }

        return out;
    }

    static BattleGrid createGrid()
    {
        bGridCounter++;
        //System.out.println("createGrid:" + bGridCounter);
        return bGridVector.get(bGridCounter);
    }

    static Point newPoint(int x, int y)
    {
        Point out;
        pntCounter++;
        //System.out.println("newPoint:" + pntCounter);

        out = pntVector.get(pntCounter);
        out.setLocation(x,y);
        return out;
    }

    static Point newPoint(Point aPoint)
    {
        Point out;
        pntCounter++;
        //System.out.println("newPoint2:" + pntCounter);

        out = pntVector.get(pntCounter);
        out.setLocation(aPoint.x, aPoint.y);
        return out;
    }
}