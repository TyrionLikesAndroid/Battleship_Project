
import java.util.*;

/**
 * 
 */
public class ShipFactory {

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

    public static Ship createShip(String aString) {

        Ship out = null;
        if(aString.equals(BATTLESHIP))
            out = new Ship(BATTLESHIP, "B", BAT_LENGTH);
        else if(aString.equals(CARRIER))
            out = new Ship(CARRIER, "C", CAR_LENGTH);
        else if(aString.equals(DESTROYER))
            out = new Ship(DESTROYER, "D", DES_LENGTH);
        else if(aString.equals(SUBMARINE))
            out = new Ship(SUBMARINE, "S", SUB_LENGTH);
        else if(aString.equals(CRUISER))
            out = new Ship(CRUISER, "R", CRU_LENGTH);

        return out;
    }

}