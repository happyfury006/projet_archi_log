public class Zabrak extends Soldat {
    
    public Zabrak(){
        this.dps=50;
        this.strategie = new AttaquerCac();
    }
    
    @Override
    protected void ajusterStrategie(){
        if (getHp() < 30) {
            setStrategie(new FuiteCac());
        } else {
            setStrategie(new AttaquerCac());
        }
    }
    // public void JouerTour(){
        
    // }
}