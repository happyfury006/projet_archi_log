public class Wookie extends Soldat  {
    
    public Wookie(){
        this.dps=100;
        this.strategie = new AttaquerDist();
    }
    
    @Override
    protected void ajusterStrategie(){
        if (getHp() < 30) {
            setStrategie(new FuiteDist());
        } else {
            setStrategie(new AttaquerDist());
        }
    }
    // public void JouerTour(){
        
    // }
}