import java.util.LinkedList;

public class Main {

    public static void main(String[] args)
    {
        LinkedList<AttackStrategy> aList = new LinkedList<>();
        aList.add(new SimpleAttackStrategy("Consecutive_Linear_1", 1));
        aList.add(new SimpleAttackStrategy("Consecutive_Linear_2", 2));

        SimulationManager mgr = new SimulationManager(new SimplePlacementStrategy(), aList);
        mgr.runSimulation(100);
    }
}