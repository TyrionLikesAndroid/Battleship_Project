
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 */
public class BruteForceAttackStrategy extends AttackStrategy {

    HashMap<Point,Integer> cellValueMap;

    public BruteForceAttackStrategy(String name) {

        super(name);

        cellValueMap = new HashMap<Point, Integer>();
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int xMax = aGrid.width;
        int yMax = aGrid.length;
        int xValue = 1;
        int yValue = 1;
        int cellValue = 0;

        // Make a run loop that will go forever.  The break condition will be game over
        while(true)
        {
            // Calculate the value for each cell in the grid based on the criteria
            // of open consecutive positions in each direction
            for (; yValue <= yMax; yValue++) {
                for (; xValue <= xMax; xValue++) {

                    // Skip any cells that have already registered a hit.  We want to ignore these in the
                    // future runs so the algorithm will get a little faster and stop looping
                    Point targetPoint = GameFactory.newPoint(xValue, yValue);
                    if(! aGrid.quickShotLookup.contains(targetPoint)) {
                        cellValue = calculateCellValue(targetPoint, aGrid);
                        cellValueMap.put(targetPoint, cellValue);
                        //System.out.println("BF Attack Value (" + targetPoint.x + "," + targetPoint.y + ") = " + cellValue);
                    }
                }

                // Reset X for the next row
                xValue = 1;
            }
            // Reset Y for next iteration
            yValue = 1;

            Point nextShot = mostValuableNextShot();
            //System.out.println("Next Shot (" + nextShot.x + "," + nextShot.y + ")");

            ShotResult result = aGrid.attemptShot(nextShot);

            // After we take a shot, assign the cell a value of zero immediately so it won't
            // be considered in the next iteration.
            cellValueMap.put(nextShot,0);

            if (result.isHit) {
                                // Transition to the sink algorithm, which will sink the ship and
                // then we can resume the search algorithm
                //System.out.println("STARTING SINK STRATEGY");
                boolean sinkResult = shootToSink(aGrid, nextShot, result.hitShipName);
                if (!sinkResult) {
                    // Bail out immediately if we ever fail on the sink algorithm.  Something is
                    // wrong that we need to debug
                    return;
                }

                // Once the ship is sunk, check game over condition
                if (aGrid.checkGameOver()) {
                    System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                    break;
                }
                //System.out.println("RESUMING SEARCH STRATEGY");
            }
        }
    }

    private int calculateCellValue(Point aPoint, BattleGrid aGrid)
    {
        // Don't let the calc get bigger than 5 in any direction since that's the largest
        // ship length we need to deal with.  Anything with 5 spaces can take all ships in
        // that direction
        int out = Math.min(5, aGrid.findOpenSpaces(aPoint,BattleGrid.UP));
        out += Math.min(5, aGrid.findOpenSpaces(aPoint,BattleGrid.DOWN));
        out += Math.min(5, aGrid.findOpenSpaces(aPoint,BattleGrid.RIGHT));
        out += Math.min(5, aGrid.findOpenSpaces(aPoint,BattleGrid.LEFT));

        return out;
    }

    private Point mostValuableNextShot()
    {
        Map.Entry<Point,Integer> bestShot = null;
        LinkedList<Map.Entry<Point,Integer>> candidateList = new LinkedList<>();

        Iterator<Map.Entry<Point,Integer>> pntIter = cellValueMap.entrySet().iterator();
        while(pntIter.hasNext())
        {
            Map.Entry<Point,Integer> anEntry = pntIter.next();
            if((bestShot == null) || bestShot.getValue().intValue() < anEntry.getValue())
            {
                // If we have a new best value, clear the list and add the new entry
                candidateList.clear();
                candidateList.add(anEntry);
                bestShot = anEntry;
            }
            else if(bestShot.getValue().intValue() == anEntry.getValue())
            {
                // In case of a tie, just add to the existing entry list
                candidateList.add(anEntry);
            }
        }

        // We should now have a list of the most valuable cells
        pntIter = candidateList.iterator();
        while(pntIter.hasNext())
        {
            Map.Entry<Point,Integer> anEntry = pntIter.next();
            //System.out.println("Best Available (" + anEntry.getKey().x + "," + anEntry.getKey().y + ") = " + anEntry.getValue());
        }

        // For now, just return the first one until we can get the basic flow working
        return candidateList.getFirst().getKey();
    }

}