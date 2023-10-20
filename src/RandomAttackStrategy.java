
import java.awt.*;
import java.util.Random;

/**
 * 
 */
public class RandomAttackStrategy extends AttackStrategy {

    Random rnd;

    public RandomAttackStrategy(String name) {

        super(name);
        rnd = new Random();
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int xMax = aGrid.width;
        int yMax = aGrid.length;

        // Loop forever until the game is over
        while(true)
        {
            // Get a random integer within the boundary of our grid
            int xValue = rnd.nextInt(xMax) + 1;
            int yValue = rnd.nextInt(yMax) + 1;

            //System.out.println("Random Attack Value (" + xValue + "," + yValue + ")");

            Point attackPoint = GameFactory.newPoint(xValue,yValue);
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
                    System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                    break;
                }
                //System.out.println("RESUMING SEARCH STRATEGY");
            }
        }
    }

}