package simulation;

import java.util.Arrays;
import java.util.Random;
import javax.swing.SwingUtilities;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.ui.FenetreJeu;

/**
 * Point d'entrée. Lance l'interface graphique (contre l'ordinateur ou en simulation),
 * ou une simulation en texte avec l'option --console.
 */
public class Simulation {

    public static void main(String[] args) {
        if (Arrays.asList(args).contains("--console")) {
            jouerEnConsole();
        } else {
            SwingUtilities.invokeLater(() -> FenetreJeu.demanderMode(null)
                    .ifPresent(mode -> new FenetreJeu(mode).setVisible(true)));
        }
    }

    private static void jouerEnConsole() {
        Monde monde = Monde.nouvellePartie(new Random());
        monde.ajouterObservateur(System.out::println);
        System.out.println(carte(monde));
        while (!monde.isPartieFinie()) {
            monde.jouerTour();
            System.out.println(carte(monde));
        }
    }

    /** Carte en texte : majuscule = en attaque, minuscule = en fuite. */
    static String carte(Monde monde) {
        StringBuilder texte = new StringBuilder();
        for (int y = 0; y < monde.getHauteur(); y++) {
            for (int x = 0; x < monde.getLargeur(); x++) {
                char symbole = monde.getSoldatEn(new Position(x, y))
                        .map(s -> s.getEtat().getNom().equals("fuite")
                                ? Character.toLowerCase(s.getSymbole())
                                : s.getSymbole())
                        .orElse('.');
                texte.append(' ').append(symbole).append(' ');
            }
            texte.append('\n');
        }
        return texte.toString();
    }
}
