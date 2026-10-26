package simulation.factory;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.soldat.Soldat;
import simulation.soldat.Twilek;
import simulation.soldat.Wookie;

public class FactoryCoteLumineux implements AgentFactory {

    @Override
    public Camp getCamp() {
        return Camp.LUMINEUX;
    }

    @Override
    public Soldat creerSoldatCorpsACorps(Position position) {
        return new Twilek(position);
    }

    @Override
    public Soldat creerSoldatADistance(Position position) {
        return new Wookie(position);
    }
}
