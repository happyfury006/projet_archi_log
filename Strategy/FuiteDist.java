package Strategy;
import abstract_factory.*;
import java.util.List;

public class FuiteDist implements Strategie {
    private int distance(Soldat soldat, Soldat adversaire) {
        int dx = soldat.getX() - adversaire.getX();
        int dy = soldat.getY() - adversaire.getY();
        return max(dx, dy);
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
        if (adversaireProche != null) {
            int distance = distance(soldat, adversaireProche);
            if (distance <= adversaireProche.getPortee()) {
                System.out.println("Le soldat s'enfuit de l'adversaire le plus proche.");
                int dx = soldat.getX() - adversaireProche.getX();
                int dy = soldat.getY() - adversaireProche.getY();
                int nx = soldat.getX() + dx;
                int ny = soldat.getY() + dy;
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