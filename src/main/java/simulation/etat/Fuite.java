package simulation.etat;

import java.util.Optional;
import simulation.monde.Monde;
import simulation.soldat.Soldat;

/**
 * Le soldat s'éloigne de l'ennemi le plus proche et récupère des PV.
 * Une fois rétabli, il repart à l'attaque. S'il est acculé, il se défend.
 */
public class Fuite implements Etat {

    public static final int REGENERATION = 10;

    @Override
    public void agir(Soldat soldat, Monde monde) {
        if (soldat.estRetabli()) {
            monde.signaler(soldat.description() + " est rétabli et repart au combat.");
            soldat.changerEtat(new Attaque());
            soldat.getEtat().agir(soldat, monde);
            return;
        }

        Optional<Soldat> menace = monde.ennemiLePlusProche(soldat);
        if (menace.isPresent()) {
            Soldat ennemi = menace.get();
            boolean aBouge = monde.eloigner(soldat, ennemi.getPosition());
            if (!aBouge && soldat.getPosition().distance(ennemi.getPosition()) <= soldat.getPortee()) {
                monde.signaler(soldat.description() + " est acculé et se défend.");
                monde.attaquer(soldat, ennemi);
                return;
            }
        }
        soldat.soigner(REGENERATION);
        monde.signaler(soldat.description() + " se met à l'abri et récupère (" + soldat.getPv() + " PV).");
    }

    @Override
    public String getNom() {
        return "fuite";
    }
}
