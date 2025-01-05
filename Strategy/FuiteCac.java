package Strategy;
import abstract_factory.*;
import java.util.List;
public class FuiteCac implements Strategie {
    private double distance(Soldat soldat, Soldat adversaire) {
        double dx = soldat.getX() - adversaire.getX();
        double dy = soldat.getY() - adversaire.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
    
    private Soldat trouverAdversaireLePlusProche(Soldat soldat) {
        // Implémenter la logique pour trouver l'adversaire le plus proche
        // Cela peut impliquer de parcourir une liste d'adversaires et de calculer la distance
        
        return null; // Retourner l'adversaire le plus proche trouvé
    }
    @Override
    public void executer(Soldat soldat, List<Soldat> ennemis) {
        Soldat adversaireProche = trouverAdversaireLePlusProche(soldat, ennemis);
        if (adversaireProche != null) {
            double distance = distance(soldat, adversaireProche);
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
    }
}
