
public abstract class AttackStrategy {

    public String name;

    public AttackStrategy(String name) {

        this.name = name;
    }

    public abstract void attack(BattleGrid aGrid);

    public String getName() {
        return name;
    }

}