package simulation.strategie;

import java.util.Comparator;
import java.util.Optional;
import simulation.monde.Monde;
import simulation.soldat.Soldat;

/**
 * Tire sur l'ennemi le plus affaibli à portée, pour achever les cibles en priorité.
 * Si personne n'est à portée, se rapproche de l'ennemi le plus proche.
 */
public class ADistance implements StrategieCombat {

    @Override
    public void executer(Soldat soldat, Monde monde) {
        Optional<Soldat> cibleAPortee = monde.getEnnemis(soldat).stream()
                .filter(e -> soldat.getPosition().distance(e.getPosition()) <= soldat.getPortee())
                .min(Comparator.comparingInt(Soldat::getPv));

        if (cibleAPortee.isPresent()) {
            monde.attaquer(soldat, cibleAPortee.get());
        } else {
            monde.ennemiLePlusProche(soldat).ifPresent(e -> monde.rapprocher(soldat, e.getPosition()));
        }
    }
}
