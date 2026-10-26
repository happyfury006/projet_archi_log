package simulation.strategie;

import simulation.monde.Monde;
import simulation.soldat.Soldat;

/** Pattern Strategy : la façon dont un soldat combat quand il est en mode attaque. */
public interface StrategieCombat {

    void executer(Soldat soldat, Monde monde);
}
