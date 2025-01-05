package State;

import abstract_factory.Soldat;
import java.util.List;


public interface Etat {
    void agir(Soldat soldat, List<Soldat> ennemis);
}