package Strategy;
import abstract_factory.*;
public class AttaquerDist implements Strategie {
    @Override
    public void executer(Soldat soldat) {
        System.out.println("Le soldat attaque à distance avec " + soldat.getDps() + " de dégâts.");
    }
}