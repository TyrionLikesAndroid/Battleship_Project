
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

            // Create the ships and the battle grid and give it to the placment strategy
            // to beging ship placement
            pStrat.placeShips(createShips(), createBattleGrid());

            // SMJ - Do the attack loop

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

        while(! ships.isEmpty()) {
            ship = ships.remove();
            System.out.println("Ship Created " + ship.name + ":" + ship.length);
        }

        return ships;
    }

    private BattleGrid createBattleGrid() {
        return new BattleGrid(10,10);
    }

}