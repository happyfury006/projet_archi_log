public abstract class Soldat implements Strategie {

    private int x;
    private int y;
    private int hp;
    private int dps;
    private Strategie strategie;

    
    public Soldat(int nx,int ny){
        x=nx;
        y=ny;
        hp=100;
        dps=0;
        
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        // Mise à jour de la stratégie en fonction des HP
        ajusterStrategie();
    }
    public Strategie getStrategie() {
        return strategie;
    }

    public void setStrategie(Strategie strategie) {
        this.strategie = strategie;
    }

    protected abstract void ajusterStrategie();

    public void agir() {
        if (strategie != null) {
            strategie.executer(this);
        } else {
            System.out.println("Aucune stratégie définie !");
        }

    public void JouerTour(){
        this.setHp(this.getHp);
        this.agir();
    }
}
}