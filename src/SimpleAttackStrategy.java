
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
                    // Transition to the sink algorithm, which will sink the ship and
                    // then we can resume the search algorithm
                    //System.out.println("Moving to sink strategy");
                    shootToSink(aGrid, attackPoint, result.hitShipName);

                    // Once the ship is sunk, check game over condition
                    if(aGrid.checkGameOver())
                    {
                        System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                        break;
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

    void shootToSink(BattleGrid aGrid, Point firstHit, String targetShip)
    {
        // The sink strategy works with the following priorities:
        // 1) Determine ship alignment first
        // 2) Shoot at the most valuable open space on that alignment
        // 3) Keep shooting on the proper alignment until sunk

        // The tree is used so we can automatically determine which direction is best
        // because the keys will be sorted based on distance
        TreeMap<Integer, String> evalMap = new TreeMap<>();

        // Evaluate the battle grid spacing and determine the most promising direction
        int rightCheck = aGrid.findOpenSpaces(firstHit, BattleGrid.RIGHT);
        evalMap.put(rightCheck,"RIGHT");
        int leftCheck = aGrid.findOpenSpaces(firstHit, BattleGrid.LEFT);
        evalMap.put(leftCheck,"LEFT");
        int upCheck = aGrid.findOpenSpaces(firstHit, BattleGrid.UP);
        evalMap.put(upCheck,"UP");
        int downCheck = aGrid.findOpenSpaces(firstHit, BattleGrid.DOWN);
        evalMap.put(downCheck,"DOWN");

        //System.out.println("Open up=" + upCheck + " Open down=" + downCheck + " Open left=" + leftCheck +
        //        " Open right=" + rightCheck);

        // Get the last map entry, which is the direction with the most distance.  If there is a tie
        // the last one in the list will be the winner, which is fine for now.  We will use this to
        // determine the new attack point
        Map.Entry<Integer,String> mostDistance = evalMap.lastEntry();

        // Start the final attack loop
        finishTargetShip(aGrid, firstHit, firstHit, targetShip, mostDistance.getValue());
    }

    void finishTargetShip(BattleGrid aGrid, Point currentHit, Point firstHit, String targetShip, String orientation) {

        Point newAttack = calculateNextAttack(currentHit, orientation);
        ShotResult result = aGrid.attemptShot(newAttack);
        if (result.isHit) {
            // We got another hit.  Verify that we hit the same ship or a different ship
            if (result.hitShipName.equals(targetShip)) {
                // If we didn't sink it, keep shooting
                if (!result.hitShipStatus.equals(Ship.SUNK))
                    finishTargetShip(aGrid, newAttack, firstHit, targetShip, orientation);

                return;
            } else {
                // This is not our target ship, so write a log so we can test this case
                // and branch off and sink the new ship we found
                System.out.println("Hit a different ship (" + result.hitShipName + "), pivoting to sink it first");

                // Start another shootToSink loop, it should finish and then come back and
                // continue sinking the original target that brought us here
                shootToSink(aGrid,newAttack,result.hitShipName);
            }
        }

        // We missed it, so try the other direction along our orientation
        String newOrientation = null;
        if (orientation.equals(BattleGrid.LEFT))
            newOrientation = BattleGrid.RIGHT;
        else if (orientation.equals(BattleGrid.RIGHT))
            newOrientation = BattleGrid.LEFT;
        else if (orientation.equals(BattleGrid.UP))
            newOrientation = BattleGrid.DOWN;
        else if (orientation.equals(BattleGrid.DOWN))
            newOrientation = BattleGrid.UP;

        // This should always succeed because we will have gone in both directions until it's sunk. Note
        // we had to go back to the first hit because we are changing direction
        finishTargetShip(aGrid, firstHit, firstHit, targetShip, newOrientation);
    }

    Point calculateNextAttack(Point aPoint, String direction)
    {
        Point newAttack = new Point(aPoint);
        if (direction.equals(BattleGrid.RIGHT))
            newAttack.x++;
        else if (direction.equals(BattleGrid.LEFT))
            newAttack.x--;
        else if (direction.equals(BattleGrid.UP))
            newAttack.y--;
        else if (direction.equals(BattleGrid.DOWN))
            newAttack.y++;

        return newAttack;
    }
}