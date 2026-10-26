package simulation.soldat;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.strategie.CorpsACorps;

/** Côté lumineux, jedi au sabre laser : combat au corps à corps. */
public class Twilek extends Soldat {

    public Twilek(Position position) {
        super("Twi'lek", 'T', Camp.LUMINEUX, position, 100, 30, 1, new CorpsACorps());
    }
}
