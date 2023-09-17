
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class SimpleAttackStrategy extends AttackStrategy {

    public SimpleAttackStrategy(String name) {

        super.AttackStrategy(name);
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int xValue = 1;
        int yValue = 1;
        int xMax = aGrid.width;
        int yMax = aGrid.length;

        // Simple strategy to guess horizontally in consecutive order by row
        for(; yValue <= yMax; yValue++)
        {
            for(; xValue <= xMax; xValue++)
            {
                Point attackPoint = new Point(xValue, yValue);
                ShotResult result = aGrid.attemptShot(attackPoint);
                if(result.isHit)
                {
                    System.out.println("Hit: " + result.hitShipName + " : " + result.hitShipStatus);
                    if(result.hitShipStatus.equals(Ship.SUNK))
                    {
                        if(aGrid.checkGameOver())
                        {
                            int shots = 10*(yValue - 1) + xValue;
                            System.out.println("Game over in " + shots + " total shots");
                            break;
                        }
                    }
                }
            }

            // Reset x values for next loop
            xValue = 1;

            // Check again since it's a double loop and we have to break twice
            if(aGrid.checkGameOver())
                break;
        }

    }

}