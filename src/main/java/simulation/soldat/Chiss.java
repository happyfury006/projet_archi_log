package simulation.soldat;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.strategie.ADistance;

/** Côté obscur, tireur d'élite : combat à distance, fragile mais précis. */
public class Chiss extends Soldat {

    public Chiss(Position position) {
        super("Chiss", 'C', Camp.OBSCUR, position, 90, 24, 3, new ADistance());
    }
}
