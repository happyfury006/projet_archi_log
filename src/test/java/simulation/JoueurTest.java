package simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.soldat.Twilek;
import simulation.soldat.Wookie;
import simulation.soldat.Zabrak;

/** Mode joueur contre ordinateur : le monde attend le joueur quand c'est au tour de l'un de ses soldats. */
class JoueurTest {

    @Test
    void leMondeSArreteSurLesSoldatsDuJoueurEtFaitJouerLOrdinateur() {
        Monde monde = new Monde(8, 8, Camp.OBSCUR);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(0, 7));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        monde.setCampJoueur(Camp.LUMINEUX);

        // Le côté obscur ouvre le tour : c'est l'ordinateur qui joue d'abord
        assertFalse(monde.attendLeJoueur());
        monde.avancer();
        assertEquals(new Position(0, 6), zabrak.getPosition());

        assertTrue(monde.attendLeJoueur());
        assertEquals(twilek, monde.getSoldatActif().orElseThrow());
        assertThrows(IllegalStateException.class, monde::avancer);
    }

    @Test
    void leJoueurNePeutAllerQueSurUneCaseVoisineLibre() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        monde.ajouter(twilek);
        monde.ajouter(new Wookie(new Position(1, 0)));
        monde.ajouter(new Zabrak(new Position(7, 7)));
        monde.setCampJoueur(Camp.LUMINEUX);

        assertEquals(List.of(new Position(0, 1), new Position(1, 1)), monde.deplacementsPossibles());
        assertThrows(IllegalArgumentException.class, () -> monde.deplacerSoldatDuJoueur(new Position(1, 0)));
        assertThrows(IllegalArgumentException.class, () -> monde.deplacerSoldatDuJoueur(new Position(0, 2)));

        monde.deplacerSoldatDuJoueur(new Position(1, 1));
        assertEquals(new Position(1, 1), twilek.getPosition());
    }

    @Test
    void leJoueurNAttaqueQueLesEnnemisAPortee() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Wookie wookie = new Wookie(new Position(0, 0));
        Zabrak aPortee = new Zabrak(new Position(3, 3));
        Zabrak tropLoin = new Zabrak(new Position(4, 0));
        monde.ajouter(wookie);
        monde.ajouter(aPortee);
        monde.ajouter(tropLoin);
        monde.setCampJoueur(Camp.LUMINEUX);

        assertEquals(List.of(aPortee), monde.ciblesPossibles());
        assertThrows(IllegalArgumentException.class, () -> monde.attaquerAvecSoldatDuJoueur(tropLoin));

        monde.attaquerAvecSoldatDuJoueur(aPortee);
        assertEquals(aPortee.getPvMax() - wookie.getDegats(), aPortee.getPv());
    }

    @Test
    void seReposerRendDesPvSansDepasserLeMaximum() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        monde.ajouter(twilek);
        monde.ajouter(new Zabrak(new Position(7, 7)));
        monde.setCampJoueur(Camp.LUMINEUX);
        twilek.recevoirDegats(50);

        monde.reposerSoldatDuJoueur();

        assertEquals(twilek.getPvMax() - 50 + Monde.REPOS, twilek.getPv());
    }

    @Test
    void eliminerLeDernierEnnemiTermineLaPartieImmediatement() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(1, 0));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        monde.setCampJoueur(Camp.LUMINEUX);
        zabrak.recevoirDegats(zabrak.getPv() - 1);

        monde.attaquerAvecSoldatDuJoueur(zabrak);

        assertTrue(monde.isPartieFinie());
        assertEquals(Camp.LUMINEUX, monde.getVainqueur().orElseThrow());
        assertFalse(monde.attendLeJoueur());
    }

    @Test
    void unePartieContreLOrdinateurPeutSeJouerJusquAuBout() {
        // Un joueur qui se repose à chaque fois : l'ordinateur doit finir par gagner sans bloquer
        Monde monde = Monde.nouvellePartie(new Random(7));
        monde.setCampJoueur(Camp.LUMINEUX);
        while (!monde.isPartieFinie()) {
            if (monde.attendLeJoueur()) {
                monde.reposerSoldatDuJoueur();
            } else {
                monde.avancer();
            }
        }
        assertEquals(Camp.OBSCUR, monde.getVainqueur().orElseThrow());
    }
}
