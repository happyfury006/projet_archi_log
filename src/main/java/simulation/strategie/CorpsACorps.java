package simulation.strategie;

import simulation.monde.Monde;
import simulation.soldat.Soldat;

/** Fonce sur l'ennemi le plus proche et le frappe dès qu'il est au contact. */
public class CorpsACorps implements StrategieCombat {

    @Override
    public void executer(Soldat soldat, Monde monde) {
        monde.ennemiLePlusProche(soldat).ifPresent(cible -> {
            if (soldat.getPosition().distance(cible.getPosition()) <= soldat.getPortee()) {
                monde.attaquer(soldat, cible);
            } else {
                monde.rapprocher(soldat, cible.getPosition());
            }
        });
    }
}
