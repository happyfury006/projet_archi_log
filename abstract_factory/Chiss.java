package abstract_factory;
import Strategy.*;
public class Chiss extends Soldat {
    public Chiss(int x, int y) {
        super(x, y, 100);
        setStrategie(new AttaquerDist());
    }

    @Override
    protected void ajusterStrategie() {
        setStrategie(getHp() < 30 ? new FuiteDist() : new AttaquerDist());
    }
}