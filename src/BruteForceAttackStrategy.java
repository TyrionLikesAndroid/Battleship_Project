
import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * 
 */
public class BruteForceAttackStrategy extends AttackStrategy {

    HashMap<Integer,Integer> cellValueMap;
    Point recyclePnt;

    public BruteForceAttackStrategy(String name) {

        super(name);

        cellValueMap = new HashMap<Integer, Integer>();
        recyclePnt = new Point();
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int xMax = aGrid.width;
        int yMax = aGrid.length;
        int xValue = 1;
        int yValue = 1;
        int cellValue = 0;
        Point nextShot = null;

        // Make a run loop that will go forever.  The break condition will be game over
        while(true)
        {
            if(nextShot == null) {

                // First time through, calculate the value for each cell in the grid based on the criteria
                // of open consecutive positions in each direction
                for (; yValue <= yMax; yValue++) {
                    for (; xValue <= xMax; xValue++) {

                        recyclePnt.setLocation(xValue,yValue);
                        cellValue = calculateCellValue(recyclePnt, aGrid);
                        updateValueMap(recyclePnt,cellValue);
                        //System.out.println("BF Attack Value (" + recyclePnt.x + "," + recyclePnt.y + ") = " + cellValue);
                    }

                    // Reset X for the next row
                    xValue = 1;
                }
            }
            else
            {
                // Every subsequent time through the loop, just update the rows that are now "dirty"
                // due to the last shot that was placed
                yValue = nextShot.y;
                xValue = nextShot.x;

                for(int i = 1; i <= xMax; i++)
                {
                    // Only update the cells in the horizontal row. Skip any cells that have already been shot
                    recyclePnt.setLocation(i, yValue);
                    if (!aGrid.quickShotLookup.contains(recyclePnt)) {
                        cellValue = calculateCellValue(recyclePnt, aGrid);
                        updateValueMap(recyclePnt,cellValue);
                    }
                }

                for(int j = 1; j <= yMax; j++)
                {
                    // Only update the cells in the horizontal row.  Skip any cells that have already been shot
                    recyclePnt.setLocation(xValue, j);
                    if (!aGrid.quickShotLookup.contains(recyclePnt)) {
                        cellValue = calculateCellValue(recyclePnt, aGrid);
                        updateValueMap(recyclePnt,cellValue);
                    }
                }
            }

            // Find the most valuable shot now that all the cells have been updated and attempt
            // a shot at that location
            nextShot = mostValuableNextShot();
            //System.out.println("Next Shot (" + nextShot.x + "," + nextShot.y + ")");


            // After we select the best ell, assign the cell a value of zero so it won't
            // be considered in the next iteration.
            updateValueMap(nextShot,0);

            ShotResult result = aGrid.attemptShot(nextShot);
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
        Map.Entry<Integer,Integer> bestShot = null;
        LinkedList<Map.Entry<Integer,Integer>> candidateList = new LinkedList<>();

        Iterator<Map.Entry<Integer,Integer>> pntIter = cellValueMap.entrySet().iterator();
        while(pntIter.hasNext())
        {
            Map.Entry<Integer,Integer> anEntry = pntIter.next();
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
            Map.Entry<Integer,Integer> anEntry = pntIter.next();
            //System.out.println("Best Available (" + anEntry.getKey().x + "," + anEntry.getKey().y + ") = " + anEntry.getValue());
        }

        // For now, just return the first one until we can get the basic flow working
        int combined = candidateList.getFirst().getKey();
        int xValue = (combined & 0x00FF);
        int yValue = ((combined & 0xFF00) >> 8);
        return GameFactory.newPoint(xValue, yValue);
    }

    private void updateValueMap(Point aPoint, int value)
    {
        // Convert the point into binary and stuff it into an integer.  This should eliminate
        // any need for the value map to use the heap when doing its operations.  Upper word
        // is the y coordinate and lower word is the x coordinate
        int combined = (aPoint.x | (aPoint.y << 8));
        cellValueMap.put(combined, value);
    }

}