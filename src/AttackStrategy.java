
import java.util.*;

/**
 * 
 */
public abstract class AttackStrategy {

    /**
     * Default constructor
     */
    public AttackStrategy() {
    }

    public String name;

    public void AttackStrategy(String name) {
        // TODO implement here
    }

    public abstract void attack(BattleGrid aGrid);

    public String getName() {
        return "Bob";
    }

}