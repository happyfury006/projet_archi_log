package State;


import Strategy.*;
import abstract_factory.Soldat;
import java.util.List;

public class Fuite implements Etat {
    

    @Override
    public void agir(Soldat soldat, List<Soldat> ennemis) {
        System.out.println("Le soldat fuit !");
        // Appliquer la stratégie de fuite
        
        if (soldat.getPortee() == 2){
            soldat.setStrategie(new FuiteDist());
        }else{
            soldat.setStrategie(new FuiteCac());
        }
        
        soldat.getStrategie().executer(soldat, ennemis);
    }
}