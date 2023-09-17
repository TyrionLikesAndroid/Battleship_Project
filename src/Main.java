import java.util.PriorityQueue;
import java.util.Comparator;

public class Main {

    public static void main(String[] args)
    {
        Ship ship1 = ShipFactory.createShip(ShipFactory.BATTLESHIP);
        Ship ship2 = ShipFactory.createShip(ShipFactory.CRUISER);
        Ship ship3 = ShipFactory.createShip(ShipFactory.DESTROYER);
        Ship ship4 = ShipFactory.createShip(ShipFactory.SUBMARINE);
        Ship ship5 = ShipFactory.createShip(ShipFactory.CARRIER);

        class ShipCompare implements Comparator<Ship> {

            public int compare(Ship a, Ship b)
            {
                return (b.length - a.length);
            }
        }

        PriorityQueue<Ship> ships = new PriorityQueue<>(new ShipCompare());
        ships.add(ship1);
        ships.add(ship2);
        ships.add(ship3);
        ships.add(ship4);
        ships.add(ship5);

        while(! ships.isEmpty()) {
            Ship aShip = ships.remove();
            System.out.println("Ship Created " + aShip.name + ":" + aShip.length);
        }

    }
}