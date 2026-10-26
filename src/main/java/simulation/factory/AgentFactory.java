package simulation.factory;

import simulation.monde.Camp;
import simulation.monde.Position;
import simulation.soldat.Soldat;

/**
 * Pattern Abstract Factory : chaque camp fournit sa propre famille de soldats.
 * Le monde demande « un soldat de corps à corps » ou « un soldat à distance »
 * sans savoir quelle espèce il obtient.
 */
public interface AgentFactory {

    Camp getCamp();

    Soldat creerSoldatCorpsACorps(Position position);

    Soldat creerSoldatADistance(Position position);
}
