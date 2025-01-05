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
        tourJoueur = new Random().nextInt(2);  // Le joueur qui commence est choisi aléatoirement
        partiFini = false;

        // Initialiser les équipes
        initialiserSoldats(entiteLumineux, "Lumineux");
        initialiserSoldats(entiteObscur, "Obscur");

        // Positionner les soldats de manière aléatoire
        positionnerSoldatsAleatoirement(entiteLumineux);
        positionnerSoldatsAleatoirement(entiteObscur);

        System.out.println("Le joueur " + (tourJoueur == 0 ? "Lumineux" : "Obscur") + " commence à jouer.");
    }

    public boolean isPartiFini() {
        return partiFini;
    }

    // public void afficherCarte() {
    //     for (int i = 0; i < nbMaxSoldats; i++) {
    //         for (int j = 0; j < nbMaxSoldats; j++) {
    //             boolean found = false;
    //             for (Soldat soldat : entiteLumineux) {
    //                 if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
    //                     System.out.print(" L ");
    //                     found = true;
    //                     break;
    //                 }
    //             }
    //             if (!found) {
    //                 for (Soldat soldat : entiteObscur) {
    //                     if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
    //                         System.out.print(" O ");
    //                         found = true;
    //                         break;
    //                     }
    //                 }
    //             }
    //             if (!found) {
    //                 System.out.print(" . ");
    //             }
    //         }
    //         System.out.println();
    //     }
    // }
    private void afficherCarte() {
        for (int i = 0; i < nbMaxSoldats; i++) {
            for (int j = 0; j < nbMaxSoldats; j++) {
                boolean found = false;
                for (Soldat soldat : entiteLumineux) {
                    if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
                        if (soldat instanceof Wookie) {
                            System.out.print(" W ");
                        } else if (soldat instanceof Twilek) {
                            System.out.print(" T ");
                        }
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    for (Soldat soldat : entiteObscur) {
                        if (soldat != null && soldat.getX() == i && soldat.getY() == j) {
                            if (soldat instanceof Zabrak) {
                                System.out.print(" Z ");
                            } else if (soldat instanceof Chiss) {
                                System.out.print(" C ");
                            }
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

        System.out.println("Tour du joueur " + (tourJoueur == 0 ? "Lumineux" : "Obscur"));

        if (equipeActuelle != null && !estRempliDeNull(equipeActuelle)) {
            for (Soldat soldat : equipeActuelle) {
                if (soldat != null) {
                    Random random = new Random();
                    int action = random.nextInt(2); // 0 pour attaque, 1 pour fuite
                    if (action == 0) {
                        soldat.attaquer(equipeAdverse);
                    } else {
                        soldat.fuir();
                    }
                }
            }

            // Affichage de l'état après le tour
            afficherCarte();

            // Changer de tour
            tourJoueur = (tourJoueur + 1) % 2;
        } else {
            if (estRempliDeNull(equipeAdverse)) {
                System.out.println("Le camp " + (tourJoueur == 0 ? "obscur" : "lumineux") + " a gagné !");
                partiFini = true;
            }
        }
    }

    private void initialiserSoldats(Soldat[] equipe, String camp) {
        Random random = new Random();
        for (int i = 0; i < nbMaxSoldats; i++) {
            if (camp.equals("Lumineux")) {
                if (i < nbMaxSoldats / 2) {
                    equipe[i] = new Wookie(); // Assurez-vous d'avoir une classe Tireur
                } else {
                    equipe[i] = new Twilek(); // Assurez-vous d'avoir une classe CorpsACorps
                }
            } else if (camp.equals("Obscur")) {
                if (i < nbMaxSoldats / 2) {
                    equipe[i] = new Chiss();
                } else {
                    equipe[i] = new Zabrak();
                }
            }
        }
    }

    private void positionnerSoldatsAleatoirement(Soldat[] equipe) {
        Random random = new Random();
        for (Soldat soldat : equipe) {
            if (soldat != null) {
                int x, y;
                do {
                    x = random.nextInt(nbMaxSoldats);
                    y = random.nextInt(nbMaxSoldats);
                } while (estOccupe(x, y)); // Vérifier si la case est déjà occupée
                soldat.setPosition(x, y);
            }
        }
    }

    private boolean estOccupe(int x, int y) {
        // Vérifie si la case (x, y) est déjà occupée par un soldat (toutes les équipes)
        for (Soldat soldat : entiteLumineux) {
            if (soldat != null && soldat.getX() == x && soldat.getY() == y) {
                return true;
            }
        }
        for (Soldat soldat : entiteObscur) {
            if (soldat != null && soldat.getX() == x && soldat.getY() == y) {
                return true;
            }
        }
        return false;
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
