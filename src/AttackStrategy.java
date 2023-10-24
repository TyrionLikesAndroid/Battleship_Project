import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.TreeMap;

public abstract class AttackStrategy {

    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";
    private static final String UNKNOWN = "UNKNOWN";
    private static final String NO_MOVE_AVAILABLE = "NO MOVE";

    public String name;

    public AttackStrategy(String name) {

        this.name = name;
    }

    public abstract void attack(BattleGrid aGrid);

    public String getName() {
        return name;
    }

    protected boolean shootToSink(BattleGrid aGrid, Point firstHit, String targetShip)
    {
        // Once the search strategy finds a ship, you call the sink strategy, which works as follows:
        // 1) Find the most valuable direction for the next shot
        // 2) Start attacking in that direction and go into the finishTargetShip recursive pattern

        // Determine the best direction to shoot based on available space
        String bestDirection = determineBestDirection(aGrid,firstHit, UNKNOWN);

        // Start shooting in that direction
        return finishTargetShip(aGrid, firstHit, firstHit, targetShip, bestDirection, UNKNOWN);
    }

    protected boolean shootToSink(BattleGrid aGrid, PriorityQueue<Point> pointList, String targetShip)
    {
        // This is the list version of shootToSink which is only used by the DnC algorithm.  If we have
        // a list with only one point, just throw it to the old algorithm that is built to handle the first hit.
        if(pointList.size() == 1)
            return shootToSink(aGrid, pointList.poll(),targetShip);

        // If we get here, the particular ship has more than one hits on it already.  Let's first hit the points
        // in the gaps because we know that they will be hits.  We know we will have at least two points, and
        // possibly a third if this is the carrier.
        Point firstHit = pointList.poll();
        Point secondHit = pointList.poll();
        Point thirdHit = pointList.poll();

        // See if this is horizontal or vertical alignment
        ShotResult hitResult;
        boolean isHorizontal = (firstHit.y - secondHit.y) == 0;
        if(isHorizontal)
            hitResult = aGrid.attemptShot(GameFactory.newPoint((firstHit.x + 1), firstHit.y));
        else
            hitResult = aGrid.attemptShot(GameFactory.newPoint(firstHit.x, (firstHit.y + 1)));

        // See if the ship is sunk, if so then exit
        if(hitResult.hitShipStatus.equals(Ship.SUNK))
            return true;

        // Ship is not sunk, so see if we have a second gap to hit
        Point lastHit = secondHit;
        if(thirdHit != null)
        {
            lastHit = thirdHit;
            if(isHorizontal)
                hitResult = aGrid.attemptShot(GameFactory.newPoint((secondHit.x + 1), secondHit.y));
            else
                hitResult = aGrid.attemptShot(GameFactory.newPoint(secondHit.x, (secondHit.y + 1)));

            // See if the ship is sunk, if so then exit
            if(hitResult.hitShipStatus.equals(Ship.SUNK))
                return true;
        }

        // If ship is still not sunk, we need to shoot at either end of the line for one more hit.  See if
        // either of the ends are blocked, because that will make the decision easy and we shoot the other
        String direction1 = determineBestDirection(aGrid, firstHit, isHorizontal ? HORIZONTAL : VERTICAL);
        String direction2 = determineBestDirection(aGrid, lastHit, isHorizontal ? HORIZONTAL : VERTICAL);

        if(direction1.equals(NO_MOVE_AVAILABLE))
            return finishTargetShip(aGrid, lastHit, lastHit, targetShip, direction2, isHorizontal ? HORIZONTAL : VERTICAL);

        if(direction2.equals(NO_MOVE_AVAILABLE))
            return finishTargetShip(aGrid, firstHit, firstHit, targetShip, direction1, isHorizontal ? HORIZONTAL : VERTICAL);

        // Both ends of the ship are viable, so we just need to guess.  At most we will miss once.
        if(isHorizontal)
            hitResult = aGrid.attemptShot(GameFactory.newPoint((firstHit.x - 1), firstHit.y));
        else
            hitResult = aGrid.attemptShot(GameFactory.newPoint(firstHit.x, (firstHit.y - 1)));

        // See if the ship is sunk, if so then exit
        if(hitResult.hitShipStatus.equals(Ship.SUNK))
            return true;

        // If we are here, we missed, so try the other one.
        if(isHorizontal)
            hitResult = aGrid.attemptShot(GameFactory.newPoint((lastHit.x + 1), lastHit.y));
        else
            hitResult = aGrid.attemptShot(GameFactory.newPoint(lastHit.x, (lastHit.y + 1)));

        // See if the ship is sunk, if so then exit
        if(hitResult.hitShipStatus.equals(Ship.SUNK))
            return true;

        return true;
    }

    private boolean finishTargetShip(BattleGrid aGrid, Point currentHit, Point firstHit, String targetShip, String orientation, String shipOrientation) {

        // Calculate the next attack based on the desired orientation
        Point newAttack = calculateNextAttack(currentHit, orientation);

        // Take the shot
        ShotResult result = aGrid.attemptShot(newAttack);

        // Notify the subclass that a shot was taken.  Depending on the strategy in place, they may be monitoring shots
        sinkStrategyShot(newAttack, aGrid);

        if (result.isHit) {

            // We got another hit.  Verify that we hit the same ship or a different ship
            if (result.hitShipName.equals(targetShip)) {
                // If we didn't sink it, keep shooting
                if (!result.hitShipStatus.equals(Ship.SUNK)) {

                    // It's the second hit on the same ship, so lock in orientation at this point in case
                    // we shoot past the end of the ship.
                    String realShipOrient = (newAttack.x == firstHit.x) ? VERTICAL : HORIZONTAL;
                    return finishTargetShip(aGrid, newAttack, firstHit, targetShip, orientation, realShipOrient);
                }
                return true;
            } else {
                // This is not our target ship, so write a log so we can test this case
                // and branch off and sink the new ship we found
                //System.out.println("Hit a different ship (" + result.hitShipName + "), pivoting to sink it first");

                // Start another shootToSink loop, it should finish and then come back and
                // continue sinking the original target that brought us here
                shootToSink(aGrid,newAttack,result.hitShipName);
            }
        }

        // Our original orientation was wrong if we got here, so calculate another orientation
        // and try again
        String newOrientation = determineBestDirection(aGrid, firstHit, shipOrientation);
        if(newOrientation.equals(NO_MOVE_AVAILABLE)) {

            // Bail out and write an error if no moves are available.  This is most likely due to a
            // placement error so we need to debug this run
            //System.out.println("Stopping the attack - No valid moves available");
            return false;
        }

        // This should always succeed because we will have gone in both directions until it's sunk. Note
        // we had to go back to the first hit because we are changing direction
        return finishTargetShip(aGrid, firstHit, firstHit, targetShip, newOrientation, shipOrientation);
    }

    private Point calculateNextAttack(Point aPoint, String direction)
    {
        Point newAttack = GameFactory.newPoint(aPoint);
        switch (direction) {
            case BattleGrid.RIGHT -> newAttack.x++;
            case BattleGrid.LEFT -> newAttack.x--;
            case BattleGrid.UP -> newAttack.y--;
            case BattleGrid.DOWN -> newAttack.y++;
        }

        return newAttack;
    }

    private String determineBestDirection(BattleGrid aGrid, Point aPoint, String shipOrientation)
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

        //System.out.println("Open up=" + upCheck + " Open down=" + downCheck + " Open left=" + leftCheck +
        //        " Open right=" + rightCheck);

        // Prepare output value.  Make sure we have at least one direction that has a non-zero distance
        String out = "";
        if((upCheck + downCheck + leftCheck + rightCheck) > 0)
        {
            // Get the last map entry, which is the direction with the most distance.  If there is a tie
            // the last one in the list will be the winner, which is fine for now.  We will use this to
            // determine the new attack point
            out = evalMap.lastEntry().getValue();
        }
        else
            out = NO_MOVE_AVAILABLE;

        return out;
    }

    protected void sinkStrategyShot(Point aPoint, BattleGrid aGrid)
    {
        //System.out.println("Base Shot Notify (" + aPoint.x + "," + aPoint.y + ")");
    }

}