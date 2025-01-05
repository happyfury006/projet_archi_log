
// Simulation.java
public class Simulation {

    public static void main(String[] args) {
        Simulation simulation = new Simulation();
        simulation.routine();
    }

    public void routine() {
        Monde monde = new Monde();
        monde.afficherCarte();
        // while (!monde.isPartiFini()) {
        //     monde.jouerTour();
        // }
    }
}
