package abstract_factory;

public class Factory_Cote_Lumineux implements AgentFactory {

    @Override
    public Soldat creerDist(int x, int y) {
        return new Chiss(x, y);
    }

    @Override
    public Soldat creerCac(int x, int y) {
        return new Twilek(x, y);
    }
}