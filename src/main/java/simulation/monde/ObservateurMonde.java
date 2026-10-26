package simulation.monde;

import simulation.soldat.Soldat;

/**
 * Pattern Observer : la console et l'interface graphique s'abonnent au monde
 * pour être prévenues de ce qui se passe, sans que le monde ne les connaisse.
 */
public interface ObservateurMonde {

    /** Message à afficher dans le journal de la partie. */
    void message(String texte);

    /** Une attaque vient d'avoir lieu (utile pour l'animer). */
    default void attaque(Soldat attaquant, Soldat cible, int degats) {
    }
}
