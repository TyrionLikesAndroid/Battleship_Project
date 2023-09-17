
import java.util.*;

/**
 * 
 */
public class ShotResult {

    public Boolean isHit;
    public String hitShipName;
    public String hitShipStatus;

    public ShotResult(Boolean isHit, String name, String status) {
        this.isHit = isHit;
        this.hitShipName = name;
        this.hitShipStatus = status;
    }

}