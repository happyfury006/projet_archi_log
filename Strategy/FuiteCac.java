package Strategy;
import abstract_factory.*;
public class FuiteCac implements Strategie {
    @Override
    public void executer(Soldat soldat) {
        System.out.println("Le soldat fuit au corps à corps.");
    }
}
