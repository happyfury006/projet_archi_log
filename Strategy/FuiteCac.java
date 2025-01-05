package Strategy;
import abstract_factory.*;
import java.util.List;

public class FuiteCac implements Strategie {
    private int distance(Soldat soldat, Soldat adversaire) {
        int dx = soldat.getX() - adversaire.getX();
        int dy = soldat.getY() - adversaire.getY();
        return Math.max(dx, dy);
    }
    
    private Soldat trouverAdversaireLePlusProche(Soldat soldat, List<Soldat> ennemis) {
        Soldat adversaireProche = null;
        int distanceMin = 1000;
        for (Soldat ennemi : ennemis) {
            int distance = distance(soldat, ennemi);
            if (distance < distanceMin) {
                distanceMin = distance;
                adversaireProche = ennemi;
            }
        }
        return adversaireProche;
    }
    @Override
    public void executer(Soldat soldat, List<Soldat> ennemis) {
        Soldat adversaireProche = trouverAdversaireLePlusProche(soldat, ennemis);
        int nx=0;
        int ny=0;
        int dy=0;
        int dx=0;
        if (adversaireProche != null) {
            int distance = distance(soldat, adversaireProche);
            if (distance <= adversaireProche.getPortee()) {
                System.out.println("Le soldat s'enfuit de l'adversaire le plus proche.");
                dx = soldat.getX() - adversaireProche.getX();
                dy = soldat.getY() - adversaireProche.getY();
                if (dx != 0) {
                    nx = soldat.getX() + (dx / Math.abs(dx));
                }
                
                if (dy != 0) {
                    ny = soldat.getY() + (dy / Math.abs(dy));
                }
                soldat.seDeplacerVers(nx, ny);
            } else {
                System.out.println("Le soldat reste sur place.");
                
            }
        }
        else {
            System.out.println("aucun ennemi à attaquer");
        }
    }
}
