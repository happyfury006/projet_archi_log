package abstract_factory;
import Strategy.*;
import State.*;
import java.util.List;
import java.util.Arrays;
// Soldat.java
public abstract class Soldat {

    private int x;
    private int y;
    private int hp;
    private int dps;
    private int portee;
    private Etat etat;
    private Strategie strategie;
    

    public Soldat(int x, int y, int dps, int portee) {
        this.x = x;
        this.y = y;
        this.hp = 100;
        this.dps = dps;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        ajusterStrategie();
    }

    public int getDps() {
        return dps;
    }

    
    public Etat getEtat() {
        return etat;
    }
    public int getPortee() {
        return portee;
    }
    public void changerEtat(Etat etat) {
        this.etat = etat;
    }
    public void seDeplacerVers(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Strategie getStrategie() {
        return strategie;
    }
     public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }

    protected abstract void ajusterStrategie();

    public void jouerTour(List<Soldat> ennemis) {
        if (etat != null) {
            etat.agir(this, ennemis); // Appel à la méthode agir de l'état actuel
            if (hp <= 0) {
                System.out.println("Le soldat est mort.");
            }
        }
    }
    
}