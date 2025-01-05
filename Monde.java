// Monde.java
import java.util.Random;
import abstract_factory.*;
import Strategy.*;

public class Monde {

    private final int nbMaxSoldats = 10;
    private final Soldat[] entiteLumineux;
    private final Soldat[] entiteObscur;
    private final int[][] carte;
    private int tourJoueur;
    private boolean partiFini;

    public Monde() {
        entiteLumineux = new Soldat[nbMaxSoldats];
        entiteObscur = new Soldat[nbMaxSoldats];
        carte = new int[nbMaxSoldats][nbMaxSoldats];
        tourJoueur = new Random().nextInt(2);
        partiFini = false;
        System.out.println("Le joueur " + (tourJoueur == 0 ? "Lumineux" : "Obscur") + " commence à jouer.");
    }

    public boolean isPartiFini() {
        return partiFini;
    }

    public void afficherCarte() {
        for (int i = 0; i < nbMaxSoldats; i++) {
            for (int j = 0; j < nbMaxSoldats; j++) {
                boolean found = false;
                for (Soldat soldat : entiteLumineux) {
                    if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
                        System.out.print(" L ");
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (Soldat soldat : entiteObscur) {
                        if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
                            System.out.print(" O ");
                            found = true;
                            break;
                        }
                    }
                }
                if (!found) {
                    System.out.print(" . ");
                }
            }
            System.out.println();
        }
    }

    public void jouerTour() {
        Soldat[] equipeActuelle = (tourJoueur == 0) ? entiteLumineux : entiteObscur;
        Soldat[] equipeAdverse = (tourJoueur == 0) ? entiteObscur : entiteLumineux;

        if (equipeActuelle != null && !estRempliDeNull(equipeActuelle)) {
            for (Soldat soldat : equipeActuelle) {
                if (soldat != null) {
                    soldat.jouerTour();
                }
            }
            tourJoueur = (tourJoueur + 1) % 2;
        } else {
            if (estRempliDeNull(equipeAdverse)) {
                System.out.println("Le camp " + (tourJoueur == 0 ? "obscur" : "lumineux") + " a gagné !");
                partiFini = true;
            }
        }
    }

    private boolean estRempliDeNull(Soldat[] tableau) {
        for (Soldat soldat : tableau) {
            if (soldat != null) {
                return false;
            }
        }
        return true;
    }
}