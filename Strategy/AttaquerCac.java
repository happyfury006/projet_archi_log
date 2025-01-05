package Strategy;
import abstract_factory.*;
import java.util.List;

public class AttaquerCac implements Strategie {
    
    private int distance(Soldat soldat, Soldat adversaire) {
        int dx = soldat.getX() - adversaire.getX();
        int dy = soldat.getY() - adversaire.getY();
        return Math.sqrt(dx * dx + dy * dy);
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
                System.out.println("Le soldat attaque au corps à corps avec " + soldat.getDps() + " de dégâts.");
                adversaireProche.setHp(adversaireProche.getHp() - soldat.getDps());
            }
        }
        else {
            System.out.println("aucun ennemi à attaquer");
        }
    }
}
