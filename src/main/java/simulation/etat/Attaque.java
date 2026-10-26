package simulation.etat;

import simulation.monde.Monde;
import simulation.soldat.Soldat;

/** Le soldat combat selon sa stratégie, jusqu'à ce qu'il soit trop affaibli. */
public class Attaque implements Etat {

    @Override
    public void agir(Soldat soldat, Monde monde) {
        if (soldat.estAffaibli()) {
            monde.signaler(soldat.description() + " est affaibli et bat en retraite.");
            soldat.changerEtat(new Fuite());
            soldat.getEtat().agir(soldat, monde);
            return;
        }
        soldat.getStrategie().executer(soldat, monde);
    }

    @Override
    public String getNom() {
        return "attaque";
    }
}
