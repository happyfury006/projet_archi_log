package abstract_factory;
import Strategy.*;
import State.*;
public class Wookie extends Soldat {
    public Wookie(int x, int y) {
        super(x, y, 100,2);
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