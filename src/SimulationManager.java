
import java.util.*;

/**
 * 
 */
public class SimulationManager {


    private HashMap<String, BattleGrid> completedGames;

    public void SimulationManager(PlacementStrategy pStrat, LinkedList<AttackStrategy> aStrats) {
        // TODO implement here
    }

    public void runSimulation(int numberOfGames) {
        // TODO implement here
    }

    public void exportResults(String filename) {
        // TODO implement here
    }

    private PriorityQueue<Ship> createShips() {
        return new PriorityQueue<Ship>();
    }

    private BattleGrid createBattleGrid() {
        return new BattleGrid(10,10);
    }

    private void recordBattleResult(BattleGrid aGrid) {
        // TODO implement here
    }

}