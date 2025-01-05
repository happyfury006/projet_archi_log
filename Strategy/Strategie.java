package Strategy;
import abstract_factory.*;
import java.util.List;
// Strategie.java
public interface Strategie {
    void executer(Soldat soldat,List<Soldat> ennemis);
}
