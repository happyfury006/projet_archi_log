public class Simulation{

    private Monde monde = new Monde(); 

    public void routine(){
        int i=0
        while(i<1000){
            monde.JouerTour();
        }
    }
    public static void main(String args[]) {
        this.routine();
    }

}