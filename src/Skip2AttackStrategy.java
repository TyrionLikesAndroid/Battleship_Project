
import java.awt.*;

/**
 * 
 */
public class Skip2AttackStrategy extends AttackStrategy {

    public Skip2AttackStrategy(String name) {

        super(name);
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        // It should always complete in no more than two sweeps of the board,  just need to shift
        // the second sweep to fill in the gaps
        if(! horizontalSweepByThree(aGrid, 0))
        {
            //System.out.println("Second sweep required to fill in gaps");
            horizontalSweepByThree(aGrid, 1);
        }
    }

    // Return true if the game is over after the sweep is complete
    boolean horizontalSweepByThree(BattleGrid aGrid, int offset)
    {
        boolean out = false;
        int xValue = 1 + offset;
        int yValue = 1;
        int xMax = aGrid.width;
        int yMax = aGrid.length;

        for(; yValue <= yMax; yValue++)
        {
            for(; xValue <= xMax; xValue += 3)
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
                        System.out.println("System error - sink algorithm didn't complete correctly");
                        return false;
                    }

                    // Once the ship is sunk, check game over condition
                    if(aGrid.checkGameOver())
                    {
                        out = true;
                        System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                        break;
                    }
                    //System.out.println("RESUMING SEARCH STRATEGY");
                }
            }

            // Reset for the next row, it should just keep wrapping
            xValue = xValue - xMax;

            // Check again since it's a double loop and we have to break twice
            if(out)
                break;
        }

        return out;
    }
}