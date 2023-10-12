import java.util.LinkedList;

public class Main {

    public static void main(String[] args)
    {
        LinkedList<AttackStrategy> aList = new LinkedList<>();
        aList.add(new SimpleAttackStrategy("Consecutive_Linear_1", 1));
        aList.add(new SimpleAttackStrategy("Consecutive_Linear_2", 2));
        aList.add(new SimpleAttackStrategy("Consecutive_Linear_3", 3));

        SimulationManager mgr = new SimulationManager(new RandomPlacementStrategy(), aList);
        mgr.runSimulation(1);
    }
}