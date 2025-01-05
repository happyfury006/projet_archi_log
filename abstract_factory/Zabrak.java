package abstract_factory;
import Strategy.*;
public class Zabrak extends Soldat {
    public Zabrak(int x, int y) {
        super(x, y, 50,1);
        setStrategie(new AttaquerCac());
    }

    @Override
    protected void ajusterStrategie() {
        setStrategie(getHp() < 30 ? new FuiteCac() : new AttaquerCac());
    }
}