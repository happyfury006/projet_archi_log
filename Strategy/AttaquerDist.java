package Strategy;
import abstract_factory.*;
import java.util.List;

public class AttaquerDist implements Strategie {
    
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
        if (adversaireProche != null) {
            int distance = distance(soldat, adversaireProche);
            if (distance > soldat.getPortee()) {
                System.out.println("Le soldat se rapproche de l'adversaire.");
                soldat.seDeplacerVers(adversaireProche.getX(), adversaireProche.getY());
            } else {
                System.out.println("Le soldat attaque à distance " + soldat.getDps() + " de dégâts.");
                adversaireProche.setHp(adversaireProche.getHp() - soldat.getDps());
            }
        }
        else {
            System.out.println("aucun ennemi à attaquer");
        }
    }
}