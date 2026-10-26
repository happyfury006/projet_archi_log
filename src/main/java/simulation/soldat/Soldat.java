package simulation.soldat;

import simulation.etat.Attaque;
import simulation.etat.Etat;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.strategie.StrategieCombat;

/**
 * Un soldat sur la carte.
 *
 * Son comportement combine deux patterns :
 * - son état (State) décide s'il attaque ou s'il fuit, et quand il change de mode ;
 * - sa stratégie de combat (Strategy) décide comment il attaque.
 */
public abstract class Soldat {

    /** En dessous de cette part de ses PV, le soldat bat en retraite. */
    private static final int SEUIL_FUITE_POURCENT = 30;
    /** À partir de cette part de ses PV, il retourne au combat. */
    private static final int SEUIL_RETOUR_POURCENT = 70;

    private final String nom;
    private final char symbole;
    private final Camp camp;
    private final int pvMax;
    private final int degats;
    private final int portee;
    private final StrategieCombat strategie;

    private Position position;
    private int pv;
    private Etat etat = new Attaque();

    protected Soldat(String nom, char symbole, Camp camp, Position position,
                     int pvMax, int degats, int portee, StrategieCombat strategie) {
        this.nom = nom;
        this.symbole = symbole;
        this.camp = camp;
        this.position = position;
        this.pvMax = pvMax;
        this.pv = pvMax;
        this.degats = degats;
        this.portee = portee;
        this.strategie = strategie;
    }

    /** Le soldat joue son action du tour ; c'est son état qui décide quoi faire. */
    public void jouer(Monde monde) {
        etat.agir(this, monde);
    }

    public void recevoirDegats(int quantite) {
        pv = Math.max(0, pv - quantite);
    }

    public void soigner(int quantite) {
        pv = Math.min(pvMax, pv + quantite);
    }

    public boolean estVivant() {
        return pv > 0;
    }

    public boolean estAffaibli() {
        return pv * 100 < pvMax * SEUIL_FUITE_POURCENT;
    }

    public boolean estRetabli() {
        return pv * 100 >= pvMax * SEUIL_RETOUR_POURCENT;
    }

    public void changerEtat(Etat nouvelEtat) {
        this.etat = nouvelEtat;
    }

    /** Ne doit être appelé que par le monde, qui vérifie que la case est libre. */
    public void placer(Position nouvellePosition) {
        this.position = nouvellePosition;
    }

    public String description() {
        return nom + " " + (camp == Camp.LUMINEUX ? "lumineux" : "obscur") + " " + position;
    }

    public String getNom() {
        return nom;
    }

    public char getSymbole() {
        return symbole;
    }

    public Camp getCamp() {
        return camp;
    }

    public Position getPosition() {
        return position;
    }

    public int getPv() {
        return pv;
    }

    public int getPvMax() {
        return pvMax;
    }

    public int getDegats() {
        return degats;
    }

    public int getPortee() {
        return portee;
    }

    public Etat getEtat() {
        return etat;
    }

    public StrategieCombat getStrategie() {
        return strategie;
    }
}
