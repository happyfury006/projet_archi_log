package Strategy;
import abstract_factory.*;
public class FuiteDist implements Strategie {
    @Override
    public void executer(Soldat soldat) {
        System.out.println("Le soldat fuit à distance.");
    }
}