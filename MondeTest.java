import java.util.Arrays;

public class MondeTest {

    public static void main(String[] args) {
        // Test 1: Initialisation du monde
        Monde monde = new Monde();

        System.out.println("Test 1: Initialisation du monde");
        // Vérifier que le nombre de soldats lumineux et obscurs est correct
        int countLumineux = (int) Arrays.stream(monde.entiteLumineux).filter(s -> s != null).count();
        int countObscur = (int) Arrays.stream(monde.entiteObscur).filter(s -> s != null).count();
        System.out.println("Nombre de soldats lumineux: " + countLumineux);
        System.out.println("Nombre de soldats obscurs: " + countObscur);
        assert countLumineux == 5 : "Erreur: Il y a un problème dans l'initialisation des soldats lumineux.";
        assert countObscur == 5 : "Erreur: Il y a un problème dans l'initialisation des soldats obscurs.";
        
        // Test 2: Jouer un tour
        System.out.println("\nTest 2: Jouer un tour");
        int initialLumineux = countLumineux;
        int initialObscur = countObscur;

        monde.jouerTour();
        
        // Après un tour, un soldat est supposé être éliminé
        countLumineux = (int) Arrays.stream(monde.entiteLumineux).filter(s -> s != null).count();
        countObscur = (int) Arrays.stream(monde.entiteObscur).filter(s -> s != null).count();
        System.out.println("Nombre de soldats lumineux après un tour: " + countLumineux);
        System.out.println("Nombre de soldats obscurs après un tour: " + countObscur);
        
        assert countLumineux == initialLumineux : "Erreur: Le nombre de soldats lumineux a changé après un tour.";
        assert countObscur == initialObscur : "Erreur: Le nombre de soldats obscurs a changé après un tour.";

        // Test 3: Vérifier si la partie est terminée après 50 tours
        System.out.println("\nTest 3: Partie terminée après 50 tours");
        Monde monde50Tours = new Monde();
        for (int i = 0; i < 50; i++) {
            monde50Tours.jouerTour();
        }
        assert monde50Tours.isPartiFini() : "Erreur: La partie ne s'est pas terminée après 50 tours.";
        
        System.out.println("Test de la classe Monde terminé sans erreurs.");
    }
}
