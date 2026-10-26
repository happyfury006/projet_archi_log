package simulation.monde;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import simulation.etat.Fuite;
import simulation.factory.AgentFactory;
import simulation.factory.FactoryCoteLumineux;
import simulation.factory.FactoryCoteObscur;
import simulation.soldat.Soldat;

/**
 * La carte et les règles du jeu : qui joue, qui peut aller où, qui gagne.
 * Les soldats décident de leurs actions ; le monde vérifie qu'elles sont possibles et les applique.
 */
public class Monde {

    public static final int TAILLE_PAR_DEFAUT = 8;
    public static final int SOLDATS_PAR_CAMP = 5;
    public static final int TOURS_MAX = 100;
    /** PV récupérés quand un soldat du joueur passe son tour pour se reposer. */
    public static final int REPOS = Fuite.REGENERATION;

    private final int largeur;
    private final int hauteur;
    private final List<Soldat> soldats = new ArrayList<>();
    private final List<ObservateurMonde> observateurs = new ArrayList<>();
    private final Deque<Soldat> fileDuTour = new ArrayDeque<>();

    private Camp campCourant;
    private Camp campJoueur;
    private int tour = 1;
    private boolean tourCommence;
    private boolean partieFinie;
    private Camp vainqueur;

    public Monde(int largeur, int hauteur, Camp premierCamp) {
        this.largeur = largeur;
        this.hauteur = hauteur;
        this.campCourant = premierCamp;
    }

    /** Partie standard : chaque camp aligne ses soldats sur sa ligne de départ, placés au hasard. */
    public static Monde nouvellePartie(Random random) {
        Monde monde = new Monde(TAILLE_PAR_DEFAUT, TAILLE_PAR_DEFAUT, random.nextBoolean() ? Camp.LUMINEUX : Camp.OBSCUR);
        monde.deployer(new FactoryCoteLumineux(), 0, random);
        monde.deployer(new FactoryCoteObscur(), monde.hauteur - 1, random);
        return monde;
    }

    private void deployer(AgentFactory factory, int ligne, Random random) {
        List<Integer> colonnes = new ArrayList<>();
        for (int x = 0; x < largeur; x++) {
            colonnes.add(x);
        }
        Collections.shuffle(colonnes, random);
        for (int i = 0; i < SOLDATS_PAR_CAMP; i++) {
            Position position = new Position(colonnes.get(i), ligne);
            ajouter(i % 2 == 0 ? factory.creerSoldatCorpsACorps(position) : factory.creerSoldatADistance(position));
        }
    }

    public void ajouter(Soldat soldat) {
        if (!estDansLaCarte(soldat.getPosition()) || !estLibre(soldat.getPosition())) {
            throw new IllegalArgumentException("Case invalide ou occupée : " + soldat.getPosition());
        }
        soldats.add(soldat);
    }

    // --- Déroulement de la partie ---
    //
    // Chaque tour, chaque soldat joue une fois, en alternant les camps (un soldat de chaque côté
    // à tour de rôle), et le camp qui ouvre le tour change à chaque tour. Faire jouer un camp
    // entier d'un coup donnait un avantage énorme au premier joueur : sur 2 000 parties simulées,
    // le camp qui commençait gagnait plus de 9 fois sur 10.
    //
    // Le monde avance soldat par soldat. Les soldats de l'ordinateur jouent avec avancer() ;
    // quand vient le tour d'un soldat du joueur, le monde attend une action du joueur.

    /** Le camp contrôlé par le joueur, ou null si les deux camps sont joués par l'ordinateur. */
    public void setCampJoueur(Camp camp) {
        this.campJoueur = camp;
    }

    public Optional<Camp> getCampJoueur() {
        return Optional.ofNullable(campJoueur);
    }

    /** Le soldat qui doit jouer maintenant. */
    public Optional<Soldat> getSoldatActif() {
        if (partieFinie) {
            return Optional.empty();
        }
        if (!tourCommence) {
            commencerTour();
        }
        // Un soldat tué plus tôt dans ce tour ne joue plus
        while (!fileDuTour.isEmpty() && !fileDuTour.peekFirst().estVivant()) {
            fileDuTour.pollFirst();
        }
        return Optional.ofNullable(fileDuTour.peekFirst());
    }

    public boolean attendLeJoueur() {
        return getSoldatActif().map(s -> s.getCamp() == campJoueur).orElse(false);
    }

    /** Fait jouer le prochain soldat de l'ordinateur. */
    public void avancer() {
        Soldat soldat = getSoldatActif().orElse(null);
        if (soldat == null) {
            return;
        }
        if (soldat.getCamp() == campJoueur) {
            throw new IllegalStateException("C'est au joueur de faire jouer " + soldat.description());
        }
        soldat.jouer(this);
        terminerAction();
    }

    /** Joue le reste du tour en cours (uniquement des soldats de l'ordinateur). */
    public void jouerTour() {
        int tourEnCours = tour;
        while (!partieFinie && tour == tourEnCours) {
            avancer();
        }
    }

    private void commencerTour() {
        tourCommence = true;
        signaler("── Tour " + tour + " : le " + campCourant.getLibelle() + " ouvre le tour ──");
        Iterator<Soldat> premiers = getSoldats(campCourant).iterator();
        Iterator<Soldat> seconds = getSoldats(campCourant.adverse()).iterator();
        while (premiers.hasNext() || seconds.hasNext()) {
            if (premiers.hasNext()) {
                fileDuTour.addLast(premiers.next());
            }
            if (seconds.hasNext()) {
                fileDuTour.addLast(seconds.next());
            }
        }
    }

    private void terminerAction() {
        fileDuTour.pollFirst();
        if (verifierElimination()) {
            return;
        }
        while (!fileDuTour.isEmpty() && !fileDuTour.peekFirst().estVivant()) {
            fileDuTour.pollFirst();
        }
        if (fileDuTour.isEmpty()) {
            terminerTour();
        }
    }

    private void terminerTour() {
        if (tour >= TOURS_MAX) {
            terminerAuxPoints();
            return;
        }
        campCourant = campCourant.adverse();
        tour++;
        tourCommence = false;
    }

    private boolean verifierElimination() {
        boolean lumineuxEnVie = !getSoldats(Camp.LUMINEUX).isEmpty();
        boolean obscurEnVie = !getSoldats(Camp.OBSCUR).isEmpty();
        if (lumineuxEnVie && obscurEnVie) {
            return false;
        }
        partieFinie = true;
        vainqueur = lumineuxEnVie ? Camp.LUMINEUX : Camp.OBSCUR;
        signaler("Victoire du " + vainqueur.getLibelle() + " !");
        return true;
    }

    private void terminerAuxPoints() {
        partieFinie = true;
        int pvLumineux = totalPv(Camp.LUMINEUX);
        int pvObscur = totalPv(Camp.OBSCUR);
        if (pvLumineux != pvObscur) {
            vainqueur = pvLumineux > pvObscur ? Camp.LUMINEUX : Camp.OBSCUR;
            signaler("Limite de tours atteinte : victoire aux points du " + vainqueur.getLibelle() + ".");
        } else {
            signaler("Limite de tours atteinte : match nul.");
        }
    }

    // --- Actions du joueur (sur le soldat actif) ---

    /** Cases où le soldat actif du joueur peut aller. */
    public List<Position> deplacementsPossibles() {
        return attendLeJoueur() ? casesVoisinesLibres(getSoldatActif().get().getPosition()) : List.of();
    }

    /** Ennemis que le soldat actif du joueur peut attaquer. */
    public List<Soldat> ciblesPossibles() {
        if (!attendLeJoueur()) {
            return List.of();
        }
        Soldat actif = getSoldatActif().get();
        return getEnnemis(actif).stream()
                .filter(e -> actif.getPosition().distance(e.getPosition()) <= actif.getPortee())
                .toList();
    }

    public void deplacerSoldatDuJoueur(Position destination) {
        if (!deplacementsPossibles().contains(destination)) {
            throw new IllegalArgumentException("Déplacement impossible vers " + destination);
        }
        Soldat actif = getSoldatActif().get();
        String avant = actif.description();
        actif.placer(destination);
        signaler(avant + " avance vers " + destination + ".");
        terminerAction();
    }

    public void attaquerAvecSoldatDuJoueur(Soldat cible) {
        if (!ciblesPossibles().contains(cible)) {
            throw new IllegalArgumentException("Cible hors de portée : " + cible.description());
        }
        attaquer(getSoldatActif().get(), cible);
        terminerAction();
    }

    /** Le soldat du joueur reste sur place et récupère des PV, comme un soldat en fuite. */
    public void reposerSoldatDuJoueur() {
        if (!attendLeJoueur()) {
            throw new IllegalStateException("Ce n'est pas au joueur de jouer");
        }
        Soldat actif = getSoldatActif().get();
        actif.soigner(REPOS);
        signaler(actif.description() + " se repose (" + actif.getPv() + " PV).");
        terminerAction();
    }

    // --- Actions demandées par les soldats ---

    public void attaquer(Soldat attaquant, Soldat cible) {
        int portee = attaquant.getPosition().distance(cible.getPosition());
        if (portee > attaquant.getPortee() || attaquant.getCamp() == cible.getCamp() || !cible.estVivant()) {
            throw new IllegalStateException(attaquant.description() + " ne peut pas attaquer " + cible.description());
        }
        cible.recevoirDegats(attaquant.getDegats());
        signaler(attaquant.description() + (attaquant.getPortee() > 1 ? " tire sur " : " frappe ")
                + cible.description() + " : -" + attaquant.getDegats() + " PV (" + cible.getPv() + " restants).");
        for (ObservateurMonde observateur : observateurs) {
            observateur.attaque(attaquant, cible, attaquant.getDegats());
        }
        if (!cible.estVivant()) {
            soldats.remove(cible);
            signaler(cible.description() + " est éliminé.");
        }
    }

    /** Avance d'une case vers la cible. Renvoie false si aucune case libre ne rapproche. */
    public boolean rapprocher(Soldat soldat, Position cible) {
        Comparator<Position> plusProche = Comparator
                .comparingInt((Position p) -> p.distance(cible))
                .thenComparingInt(p -> p.distanceEuclidienneCarre(cible));
        return deplacerVersMeilleureCase(soldat, plusProche, "avance vers");
    }

    /** Recule d'une case en s'éloignant de la menace. Renvoie false si le soldat est acculé. */
    public boolean eloigner(Soldat soldat, Position menace) {
        Comparator<Position> plusLoin = Comparator
                .comparingInt((Position p) -> -p.distance(menace))
                .thenComparingInt(p -> -p.distanceEuclidienneCarre(menace));
        return deplacerVersMeilleureCase(soldat, plusLoin, "recule vers");
    }

    /** Choisit la meilleure case voisine libre selon le critère, et ne bouge que si c'est mieux qu'ici. */
    private boolean deplacerVersMeilleureCase(Soldat soldat, Comparator<Position> critere, String verbe) {
        Position actuelle = soldat.getPosition();
        Optional<Position> meilleure = casesVoisinesLibres(actuelle).stream().min(critere);
        if (meilleure.isEmpty() || critere.compare(meilleure.get(), actuelle) >= 0) {
            return false;
        }
        String avant = soldat.description();
        soldat.placer(meilleure.get());
        signaler(avant + " " + verbe + " " + meilleure.get() + ".");
        return true;
    }

    private List<Position> casesVoisinesLibres(Position centre) {
        List<Position> cases = new ArrayList<>();
        for (int dy = -1; dy <= 1; dy++) {
            for (int dx = -1; dx <= 1; dx++) {
                Position voisine = centre.decaler(dx, dy);
                if ((dx != 0 || dy != 0) && estDansLaCarte(voisine) && estLibre(voisine)) {
                    cases.add(voisine);
                }
            }
        }
        return cases;
    }

    // --- Requêtes ---

    public boolean estDansLaCarte(Position position) {
        return position.x() >= 0 && position.x() < largeur && position.y() >= 0 && position.y() < hauteur;
    }

    public boolean estLibre(Position position) {
        return getSoldatEn(position).isEmpty();
    }

    public Optional<Soldat> getSoldatEn(Position position) {
        return soldats.stream().filter(s -> s.getPosition().equals(position)).findFirst();
    }

    public List<Soldat> getSoldats() {
        return List.copyOf(soldats);
    }

    public List<Soldat> getSoldats(Camp camp) {
        return soldats.stream().filter(s -> s.getCamp() == camp).toList();
    }

    public List<Soldat> getEnnemis(Soldat soldat) {
        return getSoldats(soldat.getCamp().adverse());
    }

    public Optional<Soldat> ennemiLePlusProche(Soldat soldat) {
        Position ici = soldat.getPosition();
        return getEnnemis(soldat).stream().min(Comparator
                .comparingInt((Soldat e) -> ici.distance(e.getPosition()))
                .thenComparingInt(e -> ici.distanceEuclidienneCarre(e.getPosition())));
    }

    public int totalPv(Camp camp) {
        return getSoldats(camp).stream().mapToInt(Soldat::getPv).sum();
    }

    public void ajouterObservateur(ObservateurMonde observateur) {
        observateurs.add(observateur);
    }

    public void signaler(String message) {
        for (ObservateurMonde observateur : observateurs) {
            observateur.message(message);
        }
    }

    public int getLargeur() {
        return largeur;
    }

    public int getHauteur() {
        return hauteur;
    }

    public Camp getCampCourant() {
        return campCourant;
    }

    public int getTour() {
        return tour;
    }

    public boolean isPartieFinie() {
        return partieFinie;
    }

    /** Vide en cas de match nul ou si la partie n'est pas terminée. */
    public Optional<Camp> getVainqueur() {
        return Optional.ofNullable(vainqueur);
    }
}
