package abstract_factory;
import Strategy.*;
public class Twilek extends Soldat {
    public Twilek(int x, int y) {
        super(x, y, 50);
        setStrategie(new AttaquerCac());
    }

    @Override
    protected void ajusterStrategie() {
        setStrategie(getHp() < 30 ? new FuiteCac() : new AttaquerCac());
    }
}