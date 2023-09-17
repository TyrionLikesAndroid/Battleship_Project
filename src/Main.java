import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;

public class Main {

    public static void main(String[] args)
    {
        LinkedList<AttackStrategy> aList = new LinkedList<>();
        aList.add(new SimpleAttackStrategy());

        SimulationManager mgr = new SimulationManager(new SimplePlacementStrategy(), aList);
        mgr.runSimulation(1);

    }
}