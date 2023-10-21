
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.util.*;

/**
 * 
 */
public class SimulationManager {

    class ShipCompare implements Comparator<Ship> {

        public int compare(Ship a, Ship b)
        {
            return (b.length - a.length);
        }
    }

    private HashMap<Integer, HashMap<String, BattleGrid>> totalGames;
    private PlacementStrategy pStrat;
    LinkedList<AttackStrategy> aStrats;

    public SimulationManager(PlacementStrategy pStrat, LinkedList<AttackStrategy> aStrats) {
        this.pStrat = pStrat;
        this.aStrats = aStrats;
    }

    public void runSimulation(int numberOfGames) {

        if(numberOfGames == 0)
            return;

        // Create the map for our total results of all runs
        HashMap<String, BattleGrid> iterationGames;
        totalGames = new HashMap<>();

        for(int i=0; i< numberOfGames; i++)
        {
            // Create the array for our results for this iteration
            iterationGames = new HashMap<>();

            // Create the ships and give them to the battle grid for the ship layout
            // that we will use for this iteration with all the different strategies
            BattleGrid gameGrid = createBattleGrid();
            pStrat.placeShips(createShips(), gameGrid);

            // Do the attack loop
            Iterator<AttackStrategy> aIter = aStrats.iterator();
            while(aIter.hasNext())
            {
                // Clone the battle grid since we need a fresh one for each strategy
                BattleGrid gridClone = gameGrid.clone();

                // Attack with the current strategy
                AttackStrategy aStrat = aIter.next();
                aStrat.attack(gridClone);

                // Record the results and move on to the next strategy
                iterationGames.put(aStrat.getName(), gridClone);

                // Print out the battlegrid
                gridClone.printGrid();
            }

            // Add this iteration game map into the master game map
            totalGames.put(i, iterationGames);
        }

        //Write the results of all the runs to a CSV file to they can be loaded in Excel
        exportResults("attackResults.csv");
    }

    public void exportResults(String filename) {

        String outputFileName = System.getProperty("user.dir") + "/out/" + filename;
        try {
            File outFile = new File(outputFileName);

            // Delete it if it exists already
            if(outFile.exists()) {
                System.out.println("Deleting previous results file that exists.");
                outFile.delete();
            }

            // Create our fresh output file
            if (outFile.createNewFile()) {
                System.out.println("Results file created: " + outFile.getName());
            }

            // Create a file writer for our CSV file
            FileWriter writer = new FileWriter(outFile);

            // Iterate through our results and print them to the file
            Iterator<Map.Entry<Integer, HashMap<String, BattleGrid>>> iter = totalGames.entrySet().iterator();
            while(iter.hasNext()) {

                Map.Entry<Integer, HashMap<String, BattleGrid>> simulationEntry = iter.next();
                int attackCounter = simulationEntry.getKey();
                Iterator<Map.Entry<String, BattleGrid>> attackIter = simulationEntry.getValue().entrySet().iterator();

                while(attackIter.hasNext())
                {
                    Map.Entry<String, BattleGrid> battle = attackIter.next();
                    String attackStrategyName = battle.getKey();
                    BattleGrid battleResult = battle.getValue();

                    writer.write(attackCounter + "," + attackStrategyName + "," + battleResult.shotHistory.size() +
                            "," + battleResult.getDuration() + "\n");
                }
            }

            writer.close();

        } catch (Exception e) {
            System.out.println("An error occurred exporting the attack results");
            e.printStackTrace();
        }
    }

    private PriorityQueue<Ship> createShips() {

        PriorityQueue<Ship> ships = new PriorityQueue<>(new ShipCompare());

        Ship ship = GameFactory.createShip(GameFactory.BATTLESHIP);
        ships.add(ship);
        ship = GameFactory.createShip(GameFactory.CRUISER);
        ships.add(ship);
        ship = GameFactory.createShip(GameFactory.DESTROYER);
        ships.add(ship);
        ship = GameFactory.createShip(GameFactory.SUBMARINE);
        ships.add(ship);
        ship = GameFactory.createShip(GameFactory.CARRIER);
        ships.add(ship);

        return ships;
    }

    private BattleGrid createBattleGrid() {
        return GameFactory.createGrid();
    }

    public void testPlacement(int numberOfGames) {

        if(numberOfGames == 0)
            return;

        // Make a list to grab all of the grids after we place ships
        LinkedList<BattleGrid> gridList = new LinkedList<>();

        for(int i=0; i< numberOfGames; i++)
        {
            // Create a grid and place the ships
            BattleGrid gameGrid = createBattleGrid();
            pStrat.placeShips(createShips(), gameGrid);

            // Add this iteration game map into the master game map
            gridList.add(gameGrid);
        }

        // Make a map to hold the ship placement totals
        HashMap<Point, Integer> placeMap = new HashMap<>();
        Iterator<BattleGrid> gridIter = gridList.iterator();
        Iterator<Ship> shipIter;
        Iterator<Point> pntIter;

        while(gridIter.hasNext())
        {
            BattleGrid aGrid = gridIter.next();
            shipIter = aGrid.myShips.iterator();
            while(shipIter.hasNext())
            {
                Ship aShip = shipIter.next();
                pntIter = aShip.location.keySet().iterator();
                while(pntIter.hasNext())
                {
                    Point aPoint = pntIter.next();
                    if(placeMap.containsKey(aPoint))
                    {
                        int value = placeMap.get(aPoint);
                        value++;
                        placeMap.put(aPoint,value);
                    }
                    else
                    {
                        placeMap.put(aPoint,1);
                    }
                }
            }
        }

        for(int i= 1; i <= 10; i++)
        {
            for(int j= 1; j <= 10; j++)
            {
                Point testPoint = GameFactory.newPoint(i, j);
                Integer count = placeMap.get(testPoint);

                int out = (count == null) ? 0 : count;
                System.out.println("(" + testPoint.x + "," + testPoint.y + ") = " + out);
            }
        }
    }
}