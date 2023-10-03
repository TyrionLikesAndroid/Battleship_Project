
import java.awt.*;

/**
 * 
 */
public class SimpleAttackStrategy extends AttackStrategy {

    int interval = 1;

    public SimpleAttackStrategy(String name, int interval) {

        super(name);

        // Don't ever allow an interval less than one, we have to have a positive integer
        // to run a search
        if(interval > 0)
            this.interval = interval;
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int offsetNeeded = 0;
        int xValue = 1;
        int yValue = 1;
        int xMax = aGrid.width;
        int yMax = aGrid.length;

        // Make sure we are offsetting if the interval divides evenly into the width.
        // If we don't this, we will shoot in the same columns on every row.
        if(interval > 1)
            offsetNeeded = ((xMax % interval) == 0) ? 1 : 0;

        // Simple strategy to guess horizontally in based on the offset provided.  This
        // strategy will always go one row at a time, left to right
        System.out.println("STARTING SEARCH STRATEGY");
        for(; yValue <= yMax; yValue++)
        {
            for(; xValue <= xMax; xValue = xValue + interval)
            {
                Point attackPoint = new Point(xValue, yValue);
                ShotResult result = aGrid.attemptShot(attackPoint);
                if(result.isHit)
                {
                    // Transition to the sink algorithm, which will sink the ship and
                    // then we can resume the search algorithm
                    System.out.println("STARTING SINK STRATEGY");
                    shootToSink(aGrid, attackPoint, result.hitShipName);

                    // Once the ship is sunk, check game over condition
                    if(aGrid.checkGameOver())
                    {
                        System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                        break;
                    }
                    System.out.println("RESUMING SEARCH STRATEGY");
                }
            }

            // Keep the pattern going as if it just wraps around to the next line
            xValue = xValue - xMax + offsetNeeded;

            // Check again since it's a double loop and we have to break twice
            if(aGrid.checkGameOver())
                break;
        }
    }

}