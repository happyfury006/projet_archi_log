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
        partifini=0;
    }

    public boolean estRempliDeNull(Soldat[] tableau) {
    for (int i = 0; i < tableau.length; i++) {
        if (tableau[i] != null) {
            return false;  // Un élément n'est pas null
        }
    }
    return true;  // Tous les éléments sont null
}


    public void JouerTour() {
    if (super.partifini != 1) {
        int i;
        switch (tourjoueur) {
            case 0:
                if (Entitelumineux != null && !estRempliDeNull(Entitelumineux)) {  // Vérification si le tableau est rempli de null
                    for (i = 0; i < nbmaxsoldatside; i++) {
                        if (Entitelumineux[i] != null) {
                            Entitelumineux[i].JouerTour();
                        }
                    }
                    tourjoueur = 1;
                } else {
                    if (estRempliDeNull(Entiteobscur)) {
                        System.out.println("Le côté obscur de la force a gagné ! Bravo soldat.\n");
                        super.partifini=1;
                    }
                }
                break;
            case 1:
                if (Entiteobscur != null && !estRempliDeNull(Entiteobscur)) {  // Vérification si le tableau est rempli de null
                    for (i = 0; i < nbmaxsoldatside; i++) {
                        if (Entiteobscur[i] != null) {
                            Entiteobscur[i].JouerTour();
                        }
                    }
                    tourjoueur = 0;
                } else {
                    if (estRempliDeNull(Entitelumineux)) {
                        System.out.println("Le côté lumineux de la force a gagné ! Bravo soldat.\n");
                        super.partifini=1;
                    }
                }
                break;
        }
    } else {
        System.out.println("Un joueur a déjà gagné !!!\n");
    }
}

}

