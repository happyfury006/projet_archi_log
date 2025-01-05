package abstract_factory;
import Strategy.*;
import State.*;
public class Twilek extends Soldat {
    public Twilek(int x, int y) {
        super(x, y, 50,1);
        changerEtat(new Attaque());
    }

    @Override
    protected void ajusterStrategie() {
        if (getHp() < 30) {
            changerEtat(new Fuite()); // Changer l'état à Fuite si les PV sont inférieurs à 30
        } else {
            changerEtat(new Attaque()); // Changer l'état à Attaque si les PV sont supérieurs ou égaux à 30
        }
    }
}