package abstract_factory;

public class Zabrak extends Soldat {
    public Zabrak(int x, int y) {
        super(x, y, 50);
        setStrategie(new AttaquerCac());
    }

    @Override
    protected void ajusterStrategie() {
        setStrategie(getHp() < 30 ? new FuiteCac() : new AttaquerCac());
    }
}