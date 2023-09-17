
import java.util.*;

/**
 * 
 */
public abstract class AttackStrategy {

    public String name;

    public void AttackStrategy(String name) {

        this.name = name;
    }

    public abstract void attack(BattleGrid aGrid);

    public String getName() {
        return name;
    }

}