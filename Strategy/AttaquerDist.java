package Strategy;
import abstract_factory.*;
import java.util.List;

public class AttaquerDist implements Strategie {
    
    private double distance(Soldat soldat, Soldat adversaire) {
        double dx = soldat.getX() - adversaire.getX();
        double dy = soldat.getY() - adversaire.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private Soldat trouverAdversaireLePlusProche(Soldat soldat, List<Soldat> ennemis) {
        Soldat adversaireProche = null;
        double distanceMin = Double.MAX_VALUE;
        for (Soldat ennemi : ennemis) {
            double distance = distance(soldat, ennemi);
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
            double distance = distance(soldat, adversaireProche);
            if (distance > soldat.getPortee()) {
                System.out.println("Le soldat se rapproche de l'adversaire.");
                soldat.seDeplacerVers(adversaireProche.getX(), adversaireProche.getY());
            } else {
                System.out.println("Le soldat attaque à distance " + soldat.getDps() + " de dégâts.");
                adversaireProche.setHp(adversaireProche.getHp() - soldat.getDps());
            }
        }
    }
}