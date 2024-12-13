public class Twilek extends Soldat {
    
    public Twilek(){
        
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