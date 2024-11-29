public class Monde {
    
    private int nbmaxsoldatside = 10;
    private Soldat[] Entitelumineux;
    private Soldat[] Entiteobscur;
    private int[][] Carte;
    private int tourjoueur;

    public Monde() {
        // Initialisation des tableaux
        Entitelumineux = new Soldat[nbmaxsoldatside];
        Entiteobscur = new Soldat[nbmaxsoldatside];
        Carte = new int[nbmaxsoldatside][nbmaxsoldatside];

        // Initialisation du tour du joueur
        tourjoueur = (int)(Math.random() * 2);
        System.out.println("Le joueur " + tourjoueur + " commence a jouer \n");
    }

    public void JouerTour() {
        int i;
        for (i = 0; i < nbmaxsoldatside; i++) {
            // Assurez-vous que la méthode PasserSaison() existe dans la classe Soldat
            if (Entitelumineux[i] != null) {
                Entitelumineux[i].JouerTour();
            }
            if (Entiteobscur[i] != null) {
                Entiteobscur[i].JouerTour();
            }
        }
    }
}
