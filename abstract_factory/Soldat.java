package abstract_factory;
import Strategy.*;
import java.util.List;
import java.util.Arrays;
// Soldat.java
public abstract class Soldat {

    private int x;
    private int y;
    private int hp;
    private int dps;
    private int portee;
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

    public Strategie getStrategie() {
        return strategie;
    }
    public int getPortee() {
        return portee;
    }
    public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }
    public void seDeplacerVers(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected abstract void ajusterStrategie();

    public void jouerTour(List<Soldat> ennemis) {
        if (strategie != null) {
            strategie.executer(this, ennemis);
            if (hp <= 0) {
                System.out.println("Le soldat est mort.");
                retirerSoldatDeLEquipe(this);
            }
        }
    }
    
    private void retirerSoldatDeLEquipe(Soldat soldat) {
        if (Arrays.asList(entiteLumineux).contains(soldat)) {
            for (int i = 0; i < entiteLumineux.length; i++) {
                if (entiteLumineux[i] == soldat) {
                    entiteLumineux[i] = null;
                    break;
                }
            }
        } else {
            for (int i = 0; i < entiteObscur.length; i++) {
                if (entiteObscur[i] == soldat) {
                    entiteObscur[i] = null;
                    break;
                }
            }
        }
    }
    
}