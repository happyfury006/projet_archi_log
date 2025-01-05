import java.util.Random;
import abstract_factory.*;
import Strategy.*;
import java.util.Arrays;
import java.util.List;

public class Monde {

    private final int nbMaxSoldats = 5;
    private final Soldat[] entiteLumineux;
    private final Soldat[] entiteObscur;
    private final int[][] carte;
    private int tourJoueur;
    private int nbtour;
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

    public List<Soldat> getEnnemis(Soldat soldat) {
        if (Arrays.asList(entiteLumineux).contains(soldat)) {
            return Arrays.stream(entiteObscur)
                         .filter(s -> s != null && s.getHp() > 0)
                         .toList();
        } else {
            return Arrays.stream(entiteLumineux)
                         .filter(s -> s != null && s.getHp() > 0)
                         .toList();
        }
    }
    

    public void jouerTour() {
        if (tourJoueur == 0) {
            System.out.println("Tour du joueur Lumineux");
            for (int i = 0; i < entiteLumineux.length; i++) {
                Soldat soldat = entiteLumineux[i];
                if (soldat != null) {
                    soldat.jouerTour(getEnnemis(soldat));
                    if (soldat.getHp() <= 0) {
                        entiteLumineux[i] = null;
                    }
                }
            }
        } else {
            System.out.println("Tour du joueur Obscur");
            for (int i = 0; i < entiteObscur.length; i++) {
                Soldat soldat = entiteObscur[i];
                if (soldat != null) {
                    soldat.jouerTour(getEnnemis(soldat));
                    if (soldat.getHp() <= 0) {
                        entiteObscur[i] = null;
                    }
                }
            }
        }
        afficherCarte();
        tourJoueur = (tourJoueur + 1) % 2;
        nbtour++;
    
        if (estRempliDeNull(entiteLumineux) || estRempliDeNull(entiteObscur)) {
            partiFini = true;
            System.out.println("La partie est terminée !");
        } else if (nbtour == 50) {
            partiFini = true;
            System.out.println("Limite de tours atteinte !");
        }
    }
    


    private void initialiserSoldats(Soldat[] equipe, String camp) {
        Random random = new Random();
        for (int i = 0; i < nbMaxSoldats; i++) {
            int x, y;
            if (camp.equals("Lumineux")) {
                x = 0; // Placer les soldats lumineux sur la première ligne
                y = random.nextInt(nbMaxSoldats);
            } else {
                x = nbMaxSoldats - 1; // Placer les soldats obscurs sur la dernière ligne
                y = random.nextInt(nbMaxSoldats);
            }

            while (estOccupe(x, y)) { // Vérifier si la case est déjà occupée
                y = random.nextInt(nbMaxSoldats);
            }

            if (camp.equals("Lumineux")) {
                if (i < nbMaxSoldats / 2) {
                    equipe[i] = new Wookie(x, y); // Assurez-vous d'avoir une classe Wookie
                } else {
                    equipe[i] = new Twilek(x, y); // Assurez-vous d'avoir une classe Twilek
                }
            } else if (camp.equals("Obscur")) {
                if (i < nbMaxSoldats / 2) {
                    equipe[i] = new Chiss(x, y); // Assurez-vous d'avoir une classe Chiss
                } else {
                    equipe[i] = new Zabrak(x, y); // Assurez-vous d'avoir une classe Zabrak
                }
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


