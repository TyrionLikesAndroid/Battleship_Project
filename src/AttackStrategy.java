import java.awt.*;
import java.util.TreeMap;

public abstract class AttackStrategy {

    private static final String HORIZONTAL = "HORIZONTAL";
    private static final String VERTICAL = "VERTICAL";
    private static final String UNKNOWN = "UNKNOWN";

    public String name;

    public AttackStrategy(String name) {

        this.name = name;
    }

    public abstract void attack(BattleGrid aGrid);

    public String getName() {
        return name;
    }

    protected void shootToSink(BattleGrid aGrid, Point firstHit, String targetShip)
    {
        // Once the search strategy finds a ship, you call the sink strategy, which works as follows:
        // 1) Find the most valuable direction for the next shot
        // 2) Start attacking in that direction and go into the finishTargetShip recursive pattern

        // Determine the best direction to shoot based on available space
        String bestDirection = determineBestDirection(aGrid,firstHit, UNKNOWN);

        // Start shooting in that direction
        finishTargetShip(aGrid, firstHit, firstHit, targetShip, bestDirection, UNKNOWN);
    }

    private void finishTargetShip(BattleGrid aGrid, Point currentHit, Point firstHit, String targetShip, String orientation, String shipOrientation) {

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

    private Point calculateNextAttack(Point aPoint, String direction)
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

        System.out.println("Open up=" + upCheck + " Open down=" + downCheck + " Open left=" + leftCheck +
                " Open right=" + rightCheck);

        // Get the last map entry, which is the direction with the most distance.  If there is a tie
        // the last one in the list will be the winner, which is fine for now.  We will use this to
        // determine the new attack point
        return evalMap.lastEntry().getValue();
    }

}