package simulation.soldat;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.strategie.CorpsACorps;

/** Côté obscur, sith au sabre laser : combat au corps à corps. */
public class Zabrak extends Soldat {

    public Zabrak(Position position) {
        super("Zabrak", 'Z', Camp.OBSCUR, position, 100, 30, 1, new CorpsACorps());
    }
}
