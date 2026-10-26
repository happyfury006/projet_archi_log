package simulation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import org.junit.jupiter.api.Test;
import simulation.etat.Attaque;
import simulation.etat.Fuite;
import simulation.factory.AgentFactory;
import simulation.factory.FactoryCoteLumineux;
import simulation.factory.FactoryCoteObscur;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.soldat.Chiss;
import simulation.soldat.Twilek;
import simulation.soldat.Wookie;
import simulation.soldat.Zabrak;
import simulation.strategie.ADistance;
import simulation.strategie.CorpsACorps;

class PatternsTest {

    private final Position origine = new Position(0, 0);

    // --- Abstract Factory ---

    @Test
    void chaqueFactoryCreeLesSoldatsDeSonCamp() {
        AgentFactory lumineux = new FactoryCoteLumineux();
        AgentFactory obscur = new FactoryCoteObscur();

        assertInstanceOf(Twilek.class, lumineux.creerSoldatCorpsACorps(origine));
        assertInstanceOf(Wookie.class, lumineux.creerSoldatADistance(origine));
        assertInstanceOf(Zabrak.class, obscur.creerSoldatCorpsACorps(origine));
        assertInstanceOf(Chiss.class, obscur.creerSoldatADistance(origine));

        for (AgentFactory factory : new AgentFactory[] {lumineux, obscur}) {
            assertEquals(factory.getCamp(), factory.creerSoldatCorpsACorps(origine).getCamp());
            assertEquals(factory.getCamp(), factory.creerSoldatADistance(origine).getCamp());
        }
    }

    @Test
    void laPorteeDependDuTypeDeSoldat() {
        assertEquals(1, new Twilek(origine).getPortee());
        assertEquals(1, new Zabrak(origine).getPortee());
        assertEquals(3, new Wookie(origine).getPortee());
        assertEquals(3, new Chiss(origine).getPortee());
        assertInstanceOf(CorpsACorps.class, new Twilek(origine).getStrategie());
        assertInstanceOf(ADistance.class, new Chiss(origine).getStrategie());
    }

    // --- Strategy ---

    @Test
    void leCorpsACorpsAvanceQuandLaCibleEstLoinPuisFrappe() {
        Monde monde = new Monde(8, 8, Camp.OBSCUR);
        Zabrak zabrak = new Zabrak(new Position(0, 0));
        Twilek twilek = new Twilek(new Position(2, 0));
        monde.ajouter(zabrak);
        monde.ajouter(twilek);

        zabrak.jouer(monde);
        assertEquals(new Position(1, 0), zabrak.getPosition());
        assertEquals(twilek.getPvMax(), twilek.getPv());

        zabrak.jouer(monde);
        assertEquals(twilek.getPvMax() - zabrak.getDegats(), twilek.getPv());
    }

    @Test
    void leTireurViseLEnnemiLePlusAffaibliAPortee() {
        Monde monde = new Monde(8, 8, Camp.OBSCUR);
        Chiss chiss = new Chiss(new Position(0, 0));
        Twilek proche = new Twilek(new Position(1, 0));
        Wookie blesse = new Wookie(new Position(3, 0));
        monde.ajouter(chiss);
        monde.ajouter(proche);
        monde.ajouter(blesse);
        blesse.recevoirDegats(60);

        chiss.jouer(monde);

        assertEquals(proche.getPvMax(), proche.getPv());
        assertEquals(blesse.getPvMax() - 60 - chiss.getDegats(), blesse.getPv());
    }

    // --- State ---

    @Test
    void unSoldatAffaibliPasseEnFuiteEtSEloigne() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(3, 3));
        Zabrak zabrak = new Zabrak(new Position(3, 4));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        twilek.recevoirDegats(80);

        twilek.jouer(monde);

        assertInstanceOf(Fuite.class, twilek.getEtat());
        assertEquals(2, twilek.getPosition().distance(zabrak.getPosition()));
        assertEquals(zabrak.getPvMax(), zabrak.getPv());
    }

    @Test
    void unSoldatEnFuiteSeSoigneEtRepartAuCombatUneFoisRetabli() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(7, 7));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        twilek.recevoirDegats(80);
        twilek.changerEtat(new Fuite());

        int pvAvant = twilek.getPv();
        twilek.jouer(monde);
        assertEquals(pvAvant + Fuite.REGENERATION, twilek.getPv());

        while (!twilek.estRetabli()) {
            twilek.jouer(monde);
        }
        twilek.jouer(monde);
        assertInstanceOf(Attaque.class, twilek.getEtat());
    }

    @Test
    void unSoldatEnFuiteAculeSeDefend() {
        Monde monde = new Monde(8, 8, Camp.LUMINEUX);
        Twilek twilek = new Twilek(new Position(0, 0));
        Zabrak zabrak = new Zabrak(new Position(1, 1));
        monde.ajouter(twilek);
        monde.ajouter(zabrak);
        // Bloque les deux autres cases voisines du coin
        monde.ajouter(new Zabrak(new Position(1, 0)));
        monde.ajouter(new Zabrak(new Position(0, 1)));
        twilek.recevoirDegats(80);
        twilek.changerEtat(new Fuite());

        twilek.jouer(monde);

        assertEquals(new Position(0, 0), twilek.getPosition());
        long blesses = monde.getSoldats(Camp.OBSCUR).stream().filter(s -> s.getPv() < s.getPvMax()).count();
        assertEquals(1, blesses);
        // En se défendant, il ne récupère pas de PV ce tour-ci
        assertEquals(twilek.getPvMax() - 80, twilek.getPv());
    }
}
