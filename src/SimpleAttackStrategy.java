
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

        boolean offsetNeeded = false;
        boolean offsetSwitch = true;
        boolean isGameOver = false;
        int xValue = 1;
        int yValue = 1;
        int xMax = aGrid.width;
        int yMax = aGrid.length;

        // Make sure we are offsetting if the interval divides evenly into the width.
        // If we don't this, we will shoot in the same columns on every row.
        if(interval > 1)
            offsetNeeded = ((xMax % interval) == 0);

        // Simple strategy to guess horizontally in based on the offset provided.  This
        // strategy will always go one row at a time, left to right
        //System.out.println("STARTING SEARCH STRATEGY");
        for(; yValue <= yMax; yValue++)
        {
            for(; xValue <= xMax; xValue = xValue + interval)
            {
                Point attackPoint = GameFactory.newPoint(xValue, yValue);
                ShotResult result = aGrid.attemptShot(attackPoint);
                if(result.isHit)
                {
                    // Transition to the sink algorithm, which will sink the ship and
                    // then we can resume the search algorithm
                    //System.out.println("STARTING SINK STRATEGY");
                    boolean sinkResult = shootToSink(aGrid, attackPoint, result.hitShipName);
                    if(! sinkResult)
                    {
                        // Bail out immediately if we ever fail on the sink algorithm.  Something is
                        // wrong that we need to debug
                        return;
                    }

                    // Once the ship is sunk, check game over condition
                    if(aGrid.checkGameOver())
                    {
                        isGameOver = true;
                        System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                        break;
                    }
                    //System.out.println("RESUMING SEARCH STRATEGY");
                }
            }

            // Keep the pattern going as if it just wraps around to the next line
            if(offsetNeeded)
            {
                if(offsetSwitch)
                    xValue = 2;
                else
                    xValue = 1;

                offsetSwitch = ! offsetSwitch;  // Flip the switch.  This is silly code, but it should be fast
            }
            else
                xValue = 1;

            // Check again since it's a double loop and we have to break twice
            if(isGameOver)
                break;
        }
    }

}