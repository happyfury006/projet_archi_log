package abstract_factory;

// Soldat.java
public abstract class Soldat {

    private int x;
    private int y;
    private int hp;
    private int dps;
    private Strategie strategie;

    public Soldat(int x, int y, int dps) {
        this.x = x;
        this.y = y;
        this.hp = 100;
        this.dps = dps;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        ajusterStrategie();
    }

    public int getDps() {
        return dps;
    }

    public Strategie getStrategie() {
        return strategie;
    }

    public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }

    protected abstract void ajusterStrategie();

    public void jouerTour() {
        if (strategie != null) {
            strategie.executer(this);
        }
    }
}