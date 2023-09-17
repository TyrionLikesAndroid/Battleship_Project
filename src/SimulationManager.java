
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
        HashMap<String, BattleGrid> iterationGames = null;
        totalGames = new HashMap<Integer, HashMap<String, BattleGrid>>();

        for(int i=0; i< numberOfGames; i++)
        {
            // Create the array for our results for this iteration
            iterationGames = new HashMap<String, BattleGrid>();

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
            }

            // Add this iteration game map into the master game map
            totalGames.put(i, iterationGames);
        }
    }

    public void exportResults(String filename) {
        // TODO implement here
    }

    private PriorityQueue<Ship> createShips() {

        PriorityQueue<Ship> ships = new PriorityQueue<>(new ShipCompare());

        Ship ship = ShipFactory.createShip(ShipFactory.BATTLESHIP);
        ships.add(ship);
        ship = ShipFactory.createShip(ShipFactory.CRUISER);
        ships.add(ship);
        ship = ShipFactory.createShip(ShipFactory.DESTROYER);
        ships.add(ship);
        ship = ShipFactory.createShip(ShipFactory.SUBMARINE);
        ships.add(ship);
        ship = ShipFactory.createShip(ShipFactory.CARRIER);
        ships.add(ship);

        return ships;
    }

    private BattleGrid createBattleGrid() {
        return new BattleGrid(10,10);
    }

}