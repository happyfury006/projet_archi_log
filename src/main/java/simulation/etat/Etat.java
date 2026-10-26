package simulation.etat;

import simulation.monde.Monde;
import simulation.soldat.Soldat;

/**
 * Pattern State : le mode dans lequel se trouve un soldat.
 * Chaque état décide lui-même quand passer à un autre état.
 */
public interface Etat {

    void agir(Soldat soldat, Monde monde);

    String getNom();
}
