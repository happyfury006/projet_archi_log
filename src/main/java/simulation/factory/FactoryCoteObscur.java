package simulation.factory;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.soldat.Chiss;
import simulation.soldat.Soldat;
import simulation.soldat.Zabrak;

public class FactoryCoteObscur implements AgentFactory {

    @Override
    public Camp getCamp() {
        return Camp.OBSCUR;
    }

    @Override
    public Soldat creerSoldatCorpsACorps(Position position) {
        return new Zabrak(position);
    }

    @Override
    public Soldat creerSoldatADistance(Position position) {
        return new Chiss(position);
    }
}
