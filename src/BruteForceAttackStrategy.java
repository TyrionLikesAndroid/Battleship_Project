
import java.awt.*;
import java.util.*;

/**
 * 
 */
public class BruteForceAttackStrategy extends AttackStrategy {

    HashMap<Integer,Integer> cellValueMap;
    Point recyclePnt;

    class EntryCompare implements Comparator<Map.Entry<Integer,Integer>> {

        public int compare(Map.Entry<Integer,Integer> a, Map.Entry<Integer,Integer> b)
        {
            return (b.getKey() - a.getKey());
        }
    }

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
                recalculateDirtyData(nextShot,aGrid);
            }

            // Find the most valuable shot now that all the cells have been updated and attempt
            // a shot at that location
            nextShot = mostValuableNextShot(aGrid);
            //System.out.println("Next Shot (" + nextShot.x + "," + nextShot.y + ")");

            // After we select the best cell, assign the cell a value of zero so it won't
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

    private Point mostValuableNextShot(BattleGrid aGrid)
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

        // If shot history is empty, just return the first member of the candidate list because we have
        // to start somewhere for the rest of the algorithm to work
        int shiftedKey = 0;
        if(aGrid.quickShotLookup.isEmpty())
            shiftedKey = candidateList.getFirst().getKey();
        else
        {
            // Determine which point in our candidate list is the farthest from all of the points in
            // the shot history.  This is going to be a terrible part of the algorithm from a performance
            // point of view, but its a much better spacial approach to breaking ties other than just
            // choosing the first item in the list of tied candidates

            // Make a priority queue to put the results in.  This will automatically sort based on distance
            PriorityQueue<AbstractMap.SimpleEntry<Integer,Integer>> distances = new PriorityQueue<>(new BruteForceAttackStrategy.EntryCompare());

            // Iterate through our candidate cell list
            pntIter = candidateList.iterator();
            while(pntIter.hasNext())
            {
                int shortestRun = 10000;  // Choose a number that is bigger than all grid dimensions
                Map.Entry<Integer,Integer> anEntry = pntIter.next();
                recyclePnt.setLocation((anEntry.getKey() & 0x00FF),((anEntry.getKey() & 0xFF00) >> 8));

                // Calculate the distance of every run from this shot to a previous shot.  We want to choose
                // the point with the longest, shortest run.  This is the farthest point from the pack.
                Iterator<Point> shotIter = aGrid.quickShotLookup.iterator();
                while(shotIter.hasNext())
                {
                    Point shotPoint = shotIter.next();
                    int shotDistance = calculateRelativePointDistance(recyclePnt, shotPoint);
                    if(shortestRun > shotDistance)
                        shortestRun = shotDistance;
                }

                // Add the point into the distance list with the total distance.  We don't need to calculate
                // the average because that is the same denominator for all points being compared
                distances.add(new AbstractMap.SimpleEntry<>(shortestRun,anEntry.getKey()));
            }

            // After going through the whole list, the longest average distance should be sorted to the top
            shiftedKey = distances.poll().getValue();
        }

        // Convert the shifted key back into a point and return it
        int xValue = (shiftedKey & 0x00FF);
        int yValue = ((shiftedKey & 0xFF00) >> 8);

        //System.out.println("Best Available (" + xValue + "," + yValue + ") = " + candidateList.getFirst().getValue());

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

    private int calculateRelativePointDistance(Point a, Point b)
    {
        // We only need relative distances, so don't worry about taking the square
        // root for the actual distance.  This will save performance time.
        double xDiff = a.x - b.x;
        double yDiff = a.y - b.y;
        return (int) (Math.pow(xDiff,2) + Math.pow(yDiff,2));
    }

    protected void sinkStrategyShot(Point aPoint, BattleGrid aGrid)
    {
        super.sinkStrategyShot(aPoint, aGrid);
        //System.out.println("Child Shot Notify (" + aPoint.x + "," + aPoint.y + ")");

        updateValueMap(aPoint,0);
        recalculateDirtyData(aPoint, aGrid);
    }

    private void recalculateDirtyData(Point aPoint, BattleGrid aGrid)
    {
        // Every subsequent time through the loop, just update the rows that are now "dirty"
        // due to the last shot that was placed
        int yValue = aPoint.y;
        int xValue = aPoint.x;
        int cellValue = 0;

        for(int i = 1; i <= aGrid.width; i++)
        {
            // Only update the cells in the horizontal row. Skip any cells that have already been shot
            recyclePnt.setLocation(i, yValue);
            if (!aGrid.quickShotLookup.contains(recyclePnt)) {
                cellValue = calculateCellValue(recyclePnt, aGrid);
                updateValueMap(recyclePnt,cellValue);
            }
        }

        for(int j = 1; j <= aGrid.length; j++)
        {
            // Only update the cells in the horizontal row.  Skip any cells that have already been shot
            recyclePnt.setLocation(xValue, j);
            if (!aGrid.quickShotLookup.contains(recyclePnt)) {
                cellValue = calculateCellValue(recyclePnt, aGrid);
                updateValueMap(recyclePnt,cellValue);
            }
        }
    }

}