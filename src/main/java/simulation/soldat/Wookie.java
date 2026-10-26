package simulation.soldat;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.strategie.ADistance;

/** Côté lumineux, combat à distance à l'arbalète laser. Robuste mais moins précis. */
public class Wookie extends Soldat {

    public Wookie(Position position) {
        super("Wookie", 'W', Camp.LUMINEUX, position, 120, 20, 3, new ADistance());
    }
}
