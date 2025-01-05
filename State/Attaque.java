package State;
import Strategy.*;
import abstract_factory.Soldat;
import java.util.List;

public class Attaque implements Etat {
    


    @Override
    public void agir(Soldat soldat, List<Soldat> ennemis) {
        System.out.println("Le soldat attaque !");
        // Appliquer la stratégie d'attaque
        
        if (soldat.getPortee() == 2){
            soldat.setStrategie(new AttaquerDist());
        }else{
            soldat.setStrategie(new AttaquerCac());
        }
        soldat.getStrategie().executer(soldat, ennemis);
    }
}