
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class SimpleAttackStrategy extends AttackStrategy {

    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";
    private static final String UNKNOWN = "UNKNOWN";

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

    void shootToSink(BattleGrid aGrid, Point firstHit, String targetShip)
    {
        // The sink strategy works as follows:
        // 1) Try to determine ship alignment first (horizontal vs vertical)
        // 2) Start shooting along that alignment

        // Determine the best direction to shoot based on available space
        String bestDirection = determineBestDirection(aGrid,firstHit, UNKNOWN);

        // Start shooting in that direction
        finishTargetShip(aGrid, firstHit, firstHit, targetShip, bestDirection, UNKNOWN);
    }

    void finishTargetShip(BattleGrid aGrid, Point currentHit, Point firstHit, String targetShip, String orientation, String shipOrientation) {

        Point newAttack = calculateNextAttack(currentHit, orientation);
        ShotResult result = aGrid.attemptShot(newAttack);
        if (result.isHit) {
            // We got another hit.  Verify that we hit the same ship or a different ship
            if (result.hitShipName.equals(targetShip)) {
                // If we didn't sink it, keep shooting
                if (!result.hitShipStatus.equals(Ship.SUNK)) {

                    // It's the second hit on the same ship, so lock in orientation at this point in case
                    // we shoot past the end of the ship.
                    String realShipOrient = (newAttack.x == firstHit.x) ? VERTICAL : HORIZONTAL;
                    finishTargetShip(aGrid, newAttack, firstHit, targetShip, orientation, realShipOrient);
                }
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

        // Our original orientation was wrong if we got here, so calculate another orientation
        // and try again
        String newOrientation = determineBestDirection(aGrid, firstHit, shipOrientation);

        // This should always succeed because we will have gone in both directions until it's sunk. Note
        // we had to go back to the first hit because we are changing direction
        finishTargetShip(aGrid, firstHit, firstHit, targetShip, newOrientation, shipOrientation);
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

    String determineBestDirection(BattleGrid aGrid, Point aPoint, String shipOrientation)
    {
        // The tree is used so we can automatically determine which direction is best
        // because the keys will be sorted based on distance
        TreeMap<Integer, String> evalMap = new TreeMap<>();
        int rightCheck = 0;
        int leftCheck = 0;
        int upCheck = 0;
        int downCheck = 0;

        // Evaluate the battle grid spacing and determine the most promising direction
        if(! shipOrientation.equals(VERTICAL)) {
            rightCheck = aGrid.findOpenSpaces(aPoint, BattleGrid.RIGHT);
            evalMap.put(rightCheck, "RIGHT");
            leftCheck = aGrid.findOpenSpaces(aPoint, BattleGrid.LEFT);
            evalMap.put(leftCheck, "LEFT");
        }

        if(! shipOrientation.equals(HORIZONTAL)) {
            upCheck = aGrid.findOpenSpaces(aPoint, BattleGrid.UP);
            evalMap.put(upCheck, "UP");
            downCheck = aGrid.findOpenSpaces(aPoint, BattleGrid.DOWN);
            evalMap.put(downCheck, "DOWN");
        }

        System.out.println("Open up=" + upCheck + " Open down=" + downCheck + " Open left=" + leftCheck +
                " Open right=" + rightCheck);

        // Get the last map entry, which is the direction with the most distance.  If there is a tie
        // the last one in the list will be the winner, which is fine for now.  We will use this to
        // determine the new attack point
        return evalMap.lastEntry().getValue();
    }
}