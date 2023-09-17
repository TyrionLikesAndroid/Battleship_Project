
import java.util.*;

/**
 * 
 */
public class ShipFactory {

     /**
     * 
     */
    public static String BATTLESHIP = new String("Battleship");
    public static String SUBMARINE = new String("Submarine");
    public static String CARRIER = new String("Carrier");;
    public static String CRUISER = new String("Cruiser");;
    public static String DESTROYER = new String("Destroyer");;

    /**
     * 
     */
    private static int BAT_LENGTH = 4;
    private static int SUB_LENGTH = 3;
    private static int CRU_LENGTH = 3;
    private static int CAR_LENGTH = 5;
    private static int DES_LENGTH = 2;

    public static Ship createShip(String aString) {

        Ship out = null;
        if(aString.equals(BATTLESHIP))
            out = new Ship(BATTLESHIP, BAT_LENGTH);
        else if(aString.equals(CARRIER))
            out = new Ship(CARRIER, CAR_LENGTH);
        else if(aString.equals(DESTROYER))
            out = new Ship(DESTROYER, DES_LENGTH);
        else if(aString.equals(SUBMARINE))
            out = new Ship(SUBMARINE, SUB_LENGTH);
        else if(aString.equals(CRUISER))
            out = new Ship(CRUISER, CRU_LENGTH);

        return out;
    }

}