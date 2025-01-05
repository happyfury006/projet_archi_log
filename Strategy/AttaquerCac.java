package Strategy;
import abstract_factory.*;
public class AttaquerCac implements Strategie {
    
    private double distance(Soldat soldat, Soldat adversaire) {
        double dx = soldat.getX() - adversaire.getX();
        double dy = soldat.getY() - adversaire.getY();
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
    public void executer(Soldat soldat) {
        Soldat adversaireProche = trouverAdversaireLePlusProche(soldat);
        if (adversaireProche != null) {
            double distance = distance(soldat, adversaireProche);
            if (distance > soldat.getPortee()) {
                System.out.println("Le soldat se rapproche de l'adversaire.");
                int dx = soldat.getX() - adversaireProche.getX();
                int dy = soldat.getY() - adversaireProche.getY();
                if (dx != 0) {
                    int nx = soldat.getX() - (dx / Math.abs(dx));
                }
                
                if (dy != 0) {
                    int ny = soldat.getY() - (dy / Math.abs(dy));
                }
                soldat.seDeplacerVers(nx, ny);
            } else {
                System.out.println("Le soldat attaque au corps à corps avec " + soldat.getDps() + " de dégâts.");
                adversaireProche.setHp(adversaireProche.getHp()-soldat.getDps());
            }
        }
    }
}
