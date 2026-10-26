package simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import org.junit.jupiter.api.Test;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.soldat.Soldat;
import simulation.soldat.Twilek;
import simulation.soldat.Zabrak;

class MondeTest {

    @Test
    void uneNouvellePartiePlaceCinqSoldatsParCampSurLeurLigneDeDepart() {
        Monde monde = Monde.nouvellePartie(new Random(1));

        List<Soldat> lumineux = monde.getSoldats(Camp.LUMINEUX);
        List<Soldat> obscur = monde.getSoldats(Camp.OBSCUR);
        assertEquals(Monde.SOLDATS_PAR_CAMP, lumineux.size());
        assertEquals(Monde.SOLDATS_PAR_CAMP, obscur.size());
        assertTrue(lumineux.stream().allMatch(s -> s.getPosition().y() == 0));
        assertTrue(obscur.stream().allMatch(s -> s.getPosition().y() == monde.getHauteur() - 1));
    }

    @Test
    void onNePeutPasPlacerDeuxSoldatsSurLaMemeCaseNiHorsDeLaCarte() {
        Monde monde = new Monde(4, 4, Camp.LUMINEUX);
        monde.ajouter(new Twilek(new Position(1, 1)));

        assertThrows(IllegalArgumentException.class, () -> monde.ajouter(new Zabrak(new Position(1, 1))));
        assertThrows(IllegalArgumentException.class, () -> monde.ajouter(new Zabrak(new Position(4, 0))));
    }

    @Test
    void laDistanceCompteLesDiagonalesDansLesDeuxSens() {
        Position a = new Position(5, 5);
        assertEquals(3, a.distance(new Position(2, 4)));
        assertEquals(3, a.distance(new Position(8, 7)));
        assertEquals(1, a.distance(new Position(4, 4)));
    }

    @Test
    void rapprocherAvanceDUneSeuleCaseSansSeTeleporter() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(3, 0));
        monde.ajouter(twilek);

        assertTrue(monde.rapprocher(twilek, new Position(3, 7)));
        assertEquals(new Position(3, 1), twilek.getPosition());
    }

    @Test
    void unSoldatAcculeDansUnCoinNePeutPasSEloigner() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        monde.ajouter(twilek);

        assertFalse(monde.eloigner(twilek, new Position(1, 1)));
        assertEquals(new Position(0, 0), twilek.getPosition());
    }

    @Test
    void unSoldatTueEstRetireImmediatementEtNeJouePlus() {
        Monde monde = new Monde(4, 4, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(1, 0));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        zabrak.recevoirDegats(zabrak.getPv() - 1);

        monde.attaquer(twilek, zabrak);

        assertFalse(zabrak.estVivant());
        assertEquals(List.of(twilek), monde.getSoldats());
    }

    @Test
    void onNePeutPasAttaquerHorsDePortee() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(3, 0));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);

        assertThrows(IllegalStateException.class, () -> monde.attaquer(twilek, zabrak));
    }

    @Test
    void lesPartiesSeTerminentSansChevauchementNiSortieDeCarte() {
        for (int graine = 0; graine < 200; graine++) {
            Monde monde = Monde.nouvellePartie(new Random(graine));
            while (!monde.isPartieFinie()) {
                monde.jouerTour();

                Set<Position> occupees = new HashSet<>();
                for (Soldat soldat : monde.getSoldats()) {
                    assertTrue(monde.estDansLaCarte(soldat.getPosition()), "graine " + graine);
                    assertTrue(occupees.add(soldat.getPosition()), "deux soldats sur une case, graine " + graine);
                    assertTrue(soldat.estVivant(), "soldat mort resté sur la carte, graine " + graine);
                }
            }
            assertTrue(monde.getTour() <= Monde.TOURS_MAX, "graine " + graine);
            // Avant la limite de tours, une partie ne s'arrête que si un camp est éliminé
            boolean unCampElimine = monde.getSoldats(Camp.LUMINEUX).isEmpty() || monde.getSoldats(Camp.OBSCUR).isEmpty();
            assertTrue(unCampElimine || monde.getTour() == Monde.TOURS_MAX, "graine " + graine);
        }
    }

    @Test
    void lesSoldatsDesDeuxCampsJouentEnAlternance() {
        Monde monde = new Monde(8, 8, Camp.OBSCUR);
        monde.ajouter(new Twilek(new Position(0, 0)));
        monde.ajouter(new Twilek(new Position(2, 0)));
        monde.ajouter(new Zabrak(new Position(0, 7)));
        monde.ajouter(new Zabrak(new Position(2, 7)));
        List<String> ordre = new ArrayList<>();
        monde.ajouterObservateur(message -> {
            if (message.contains(" avance ")) {
                ordre.add(message.substring(0, message.indexOf(' ')));
            }
        });

        monde.jouerTour();

        assertEquals(List.of("Zabrak", "Twi'lek", "Zabrak", "Twi'lek"), ordre);
        assertEquals(Camp.LUMINEUX, monde.getCampCourant());
    }

    @Test
    void commencerNeDonnePasDAvantageDecisif() {
        int parties = 0;
        int victoiresDuPremier = 0;
        for (long graine = 0; graine < 500; graine++) {
            Monde monde = Monde.nouvellePartie(new Random(graine * 0x9E3779B97F4A7C15L));
            Camp premier = monde.getCampCourant();
            while (!monde.isPartieFinie()) {
                monde.jouerTour();
            }
            if (monde.getVainqueur().isPresent()) {
                parties++;
                if (monde.getVainqueur().get() == premier) {
                    victoiresDuPremier++;
                }
            }
        }
        double taux = (double) victoiresDuPremier / parties;
        assertTrue(taux > 0.35 && taux < 0.65, "le premier camp gagne " + Math.round(taux * 100) + " % des parties");
    }

    @Test
    void laCarteTexteAfficheLesSoldatsALeurPlace() {
        Monde monde = new Monde(3, 2, Camp.LUMINEUX);
        monde.ajouter(new Twilek(new Position(0, 0)));
        monde.ajouter(new Zabrak(new Position(2, 1)));

        assertEquals(" T  .  . \n .  .  Z \n", Simulation.carte(monde));
    }
}
