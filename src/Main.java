import java.util.LinkedList;

public class Main {

    public static void main(String[] args)
    {
        GameFactory.initialize(10,10);

        LinkedList<AttackStrategy> aList = new LinkedList<>();
        aList.add(new SimpleAttackStrategy("Consecutive", 1));
        aList.add(new SimpleAttackStrategy("Skip Cell", 2));
        aList.add(new BruteForceAttackStrategy("Brute Force"));
        aList.add(new RandomAttackStrategy("Random"));
        aList.add(new DivideAndConquerStrategy("Divide N Conquer"));

        SimulationManager mgr = new SimulationManager(new RandomPlacementStrategy(), aList);

        //System.gc();
        //try { Thread.sleep(2000); } catch (Exception e) {e.printStackTrace();};

        mgr.runSimulation(1);
    }

    // This is a test function for checking out how well the placement strategy is using
    // the entire battle grid.
    public static void main2(String[] args)
    {
        GameFactory.initialize(10,10);
        LinkedList<AttackStrategy> aList = new LinkedList<>();

        SimulationManager mgr = new SimulationManager(new RandomPlacementStrategy(), aList);
        mgr.testPlacement(100);
    }
}