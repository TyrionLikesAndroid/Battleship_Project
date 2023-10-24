
import java.awt.*;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * 
 */
public class DivideAndConquerStrategy extends AttackStrategy {

    private static final int SUBGRID_LENGTH = 5;
    private static final String SUBGRID_PATTERN_R = "R";
    private static final String SUBGRID_PATTERN_L = "L";
    private HashMap<String, PriorityQueue<Point>> hitMap;

    class PointCompare implements Comparator<Point> {

        public int compare(Point a, Point b)
        {
            if((a.x == b.x) && (a.y == b.y))
                return 0;

            if(a.x == b.x)
                return (a.y < b.y) ? -1 : 1;

            if(a.y == b.y)
                return (a.x < b.x) ? -1 : 1;

            return (a.x < b.x) ? -1 : 1;
        }
    }

    class DnCSubgrid implements Runnable {

        int xValue;
        int yValue;
        int gridSize;
        BattleGrid aGrid;
        String pattern;
        DivideAndConquerStrategy parent;
        Semaphore sem;

        public DnCSubgrid(int xValue, int yValue, int gridSize, BattleGrid aGrid, String pattern, DivideAndConquerStrategy parent, Semaphore sem)
        {
            this.xValue = xValue;
            this.yValue = yValue;
            this.gridSize = gridSize;
            this.aGrid = aGrid;
            this.pattern = pattern;
            this.parent = parent;
            this.sem = sem;

            //System.out.println("DnCSubgrid(" + xValue + "," + yValue + ") Pattern(" + pattern + ")");
        }

        public void run()
        {
            try {
                // Choose the starting points in each row based on the pattern
                int secondRowOffset = pattern.equals(SUBGRID_PATTERN_R) ? 0 : 1;
                int firstRowOffset = pattern.equals(SUBGRID_PATTERN_R) ? 1 : 0;

                // Attack the target cells in our subgrid based on the range and pattern provided
                int offset = firstRowOffset;
                for(int i = yValue; i < (yValue + gridSize); i++)
                {
                    for(int j = (xValue + offset); j < (xValue + gridSize); j += 2)
                    {
                        Point attackPoint = GameFactory.newPoint(j, i);
                        ShotResult result = aGrid.attemptShot(attackPoint);
                        if(result.isHit)
                            parent.notifyShipHit(attackPoint, result);
                    }
                    offset = (offset == firstRowOffset) ? secondRowOffset : firstRowOffset;
                }

                // Release the semaphore to give back a permit and free the main thread
                sem.release();
            }
            catch (Exception e) {
                // Throwing an exception
                System.out.println("Exception is caught ");
                e.printStackTrace();
            }
        }
    }

    public DivideAndConquerStrategy(String name) {

        super(name);

        hitMap = new HashMap<>();
    }

    public void attack(BattleGrid aGrid) {

        System.out.println("Attacking using strategy:" + getName());

        int xMax = aGrid.width;
        int yMax = aGrid.length;

        // Calculate how many subgrids we will need to attack out of the whole.  We will make a thread
        // for each of these subgrids and start it in parallel.  This attack will ultimately be bound
        // by the number of processors on the computer, but it will be more than 1 for sure.
        int subgrids = (yMax / SUBGRID_LENGTH) * (xMax / SUBGRID_LENGTH);
        String gridPattern = SUBGRID_PATTERN_L;
        Semaphore sem = new Semaphore(1 - subgrids);  // We want every subgrid to release before we conquer

        // Start the clock while we are single threaded so it doesn't get too confusing with race conditions
        aGrid.startTime = System.nanoTime();

        // Create the multi task objects and assign them the parameters needed to shoot in their subgrids
        for (int i = 1; i < yMax; i += SUBGRID_LENGTH) {
            for (int j = 1; j < xMax; j += SUBGRID_LENGTH) {
                // Create the subgrid and start the thread
                DnCSubgrid sGrid = new DnCSubgrid(j, i, SUBGRID_LENGTH, aGrid, gridPattern, this, sem);
                gridPattern = gridPattern.equals(SUBGRID_PATTERN_R) ? SUBGRID_PATTERN_L : SUBGRID_PATTERN_R;
                Thread thread = new Thread(sGrid);
                thread.start();
            }
            // Flip the pattern again when we change rows, it needs to be different vertically and horizonally
            gridPattern = gridPattern.equals(SUBGRID_PATTERN_R) ? SUBGRID_PATTERN_L : SUBGRID_PATTERN_R;
        }

        // Wait on a semaphore for all the threads to complete the divide strategy.  The permits are one less than
        // the number of subgrids, so the last one that finishes will release the code below.
        try {
            sem.acquire();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //System.out.println("Children threads are finished");

        // Sink the ships from the hitMap found in the divide portion of the algorithm
        Iterator<String> shipIter = hitMap.keySet().iterator();
        while(shipIter.hasNext())
        {
            String shipName = shipIter.next();
            PriorityQueue<Point> shipHits = hitMap.get(shipName);

            boolean sinkResult = shootToSink(aGrid, shipHits, shipName);
            if (! sinkResult) {
                // Bail out immediately if we ever fail on the sink algorithm.  Something is
                // wrong that we need to debug
                return;
            }

            // Once the ship is sunk, check game over condition
            if (aGrid.checkGameOver()) {
                System.out.println("Game over in " + aGrid.shotHistory.size() + " total shots");
                break;
            }
        }
    }

    private synchronized void notifyShipHit(Point aPoint, ShotResult result)
    {
        //System.out.println("Divide Strategy Hit: (" + aPoint.x + "," + aPoint.y + ") " + result.hitShipName);

        if(hitMap.containsKey(result.hitShipName))
            hitMap.get(result.hitShipName).add(aPoint);
        else
        {
            PriorityQueue<Point> aList = new PriorityQueue<>(new PointCompare());
            aList.add(aPoint);
            hitMap.put(result.hitShipName, aList);
        }
    }
}