public class Main {
    public static void main(String[] args)
    {
        Ship ship1 = ShipFactory.createShip(ShipFactory.BATTLESHIP);
        System.out.println("Ship Created " + ship1.name + ":" + ship1.length);
        Ship ship2 = ShipFactory.createShip(ShipFactory.CRUISER);
        System.out.println("Ship Created " + ship2.name + ":" + ship2.length);
        Ship ship3 = ShipFactory.createShip(ShipFactory.DESTROYER);
        System.out.println("Ship Created " + ship3.name + ":" + ship3.length);
        Ship ship4 = ShipFactory.createShip(ShipFactory.SUBMARINE);
        System.out.println("Ship Created " + ship4.name + ":" + ship4.length);
        Ship ship5 = ShipFactory.createShip(ShipFactory.CARRIER);
        System.out.println("Ship Created " + ship5.name + ":" + ship5.length);
    }
}