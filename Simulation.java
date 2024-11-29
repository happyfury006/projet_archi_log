public class Simulation{

    private int partifini=0;
    private Monde monde = new Monde(); 

    public void routine(){
        while(partifini!=1){
            monde.JouerTour();
        }
    }
    public static void main(String args[]) {
        this.routine();
    }

}