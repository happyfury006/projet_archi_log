package abstract_factory;

public class Factory_Cote_Obscur implements AgentFactory {

    @Override
    public Soldat creerDist(int x, int y) {
        return new Wookie(x, y);
    }

    @Override
    public Soldat creerCac(int x, int y) {
        return new Zabrak(x, y);
    }
}