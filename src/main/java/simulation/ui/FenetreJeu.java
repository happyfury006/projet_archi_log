package simulation.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.Optional;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.ObservateurMonde;
import simulation.monde.Position;
import simulation.soldat.Soldat;

/**
 * Fenêtre principale : le plateau au centre, le journal des actions à droite,
 * les commandes en bas. Elle observe le monde pour mettre à jour l'affichage.
 *
 * Deux modes : le joueur contrôle un camp contre l'ordinateur, ou il regarde
 * l'ordinateur jouer les deux camps.
 */
@SuppressWarnings("serial")  // la fenêtre n'est jamais sérialisée
public final class FenetreJeu extends JFrame implements ObservateurMonde {

    public enum Mode {
        JOUEUR_LUMINEUX("Jouer le côté lumineux", Camp.LUMINEUX),
        JOUEUR_OBSCUR("Jouer le côté obscur", Camp.OBSCUR),
        SIMULATION("Regarder l'ordinateur contre lui-même", null);

        private final String libelle;
        private final Camp campJoueur;

        Mode(String libelle, Camp campJoueur) {
            this.libelle = libelle;
            this.campJoueur = campJoueur;
        }

        @Override
        public String toString() {
            return libelle;
        }
    }

    private static final int DELAI_PAR_DEFAUT_MS = 600;

    private final PlateauPanel plateau = new PlateauPanel();
    private final JTextArea journal = new JTextArea();
    private final JLabel statut = new JLabel();
    private final JLabel effectifs = new JLabel();
    private final JButton boutonTour = new JButton("Tour suivant");
    private final JButton boutonLecture = new JButton("Lecture");
    private final JButton boutonRepos = new JButton("Se reposer (+" + Monde.REPOS + " PV)");
    private final JButton boutonNouvelle = new JButton("Nouvelle partie");
    private final JSlider vitesse = new JSlider(100, 1500, DELAI_PAR_DEFAUT_MS);
    /** En simulation, joue un tour à chaque tick ; contre l'ordinateur, fait jouer un soldat adverse. */
    private final Timer minuterie = new Timer(DELAI_PAR_DEFAUT_MS, e -> etapeAutomatique());

    private Mode mode;
    private Monde monde;

    public FenetreJeu(Mode mode) {
        super("Simulation Star Wars : côté lumineux contre côté obscur");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setBackground(Couleurs.FOND);
        setLayout(new BorderLayout());

        add(construireEnTete(), BorderLayout.NORTH);
        add(plateau, BorderLayout.CENTER);
        add(construireJournal(), BorderLayout.EAST);
        add(construireCommandes(), BorderLayout.SOUTH);
        plateau.setSurClic(this::clicSurLePlateau);

        nouvellePartie(mode);
        pack();
        setMinimumSize(new Dimension(860, 560));
        setLocationRelativeTo(null);
    }

    /** Demande au joueur comment il veut jouer. Vide s'il ferme la fenêtre de choix. */
    public static Optional<Mode> demanderMode(Component parent) {
        Object choix = JOptionPane.showInputDialog(parent, "Comment voulez-vous jouer ?", "Nouvelle partie",
                JOptionPane.QUESTION_MESSAGE, null, Mode.values(), Mode.JOUEUR_LUMINEUX);
        return Optional.ofNullable((Mode) choix);
    }

    // --- Construction de l'interface ---

    private JPanel construireEnTete() {
        JPanel entete = new JPanel(new BorderLayout(24, 0));
        entete.setBackground(Couleurs.PANNEAU);
        entete.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        styliser(statut, 15, Font.BOLD, Couleurs.TEXTE);
        styliser(effectifs, 13, Font.PLAIN, Couleurs.TEXTE_SECONDAIRE);
        entete.add(statut, BorderLayout.WEST);
        entete.add(effectifs, BorderLayout.EAST);
        return entete;
    }

    private JScrollPane construireJournal() {
        journal.setEditable(false);
        journal.setLineWrap(true);
        journal.setWrapStyleWord(true);
        journal.setBackground(Couleurs.PANNEAU);
        journal.setForeground(Couleurs.TEXTE_SECONDAIRE);
        journal.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        journal.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        JScrollPane defilement = new JScrollPane(journal);
        defilement.setPreferredSize(new Dimension(330, 0));
        defilement.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Couleurs.CASE_CLAIRE));
        return defilement;
    }

    private JPanel construireCommandes() {
        JPanel commandes = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        commandes.setBackground(Couleurs.PANNEAU);

        boutonTour.addActionListener(e -> {
            plateau.effacerTirs();
            monde.jouerTour();
            mettreAJour();
        });
        boutonLecture.addActionListener(e -> basculerLecture());
        boutonRepos.addActionListener(e -> {
            plateau.effacerTirs();
            monde.reposerSoldatDuJoueur();
            apresActionDuJoueur();
        });
        boutonNouvelle.addActionListener(e -> demanderMode(this).ifPresent(this::nouvellePartie));

        // Le curseur règle la vitesse : à droite = plus rapide
        vitesse.setInverted(true);
        vitesse.setOpaque(false);
        vitesse.setPreferredSize(new Dimension(140, 24));
        vitesse.addChangeListener(e -> minuterie.setDelay(vitesse.getValue()));
        JLabel libelleVitesse = new JLabel("Vitesse");
        styliser(libelleVitesse, 12, Font.PLAIN, Couleurs.TEXTE_SECONDAIRE);

        commandes.add(boutonTour);
        commandes.add(boutonLecture);
        commandes.add(boutonRepos);
        commandes.add(boutonNouvelle);
        commandes.add(Box.createHorizontalStrut(8));
        commandes.add(libelleVitesse);
        commandes.add(vitesse);
        commandes.add(construireLegende());
        return commandes;
    }

    private JPanel construireLegende() {
        JPanel legende = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        legende.setOpaque(false);
        legende.add(pastille(Couleurs.LUMINEUX, "Lumineux (T, W)"));
        legende.add(pastille(Couleurs.OBSCUR, "Obscur (Z, C)"));
        legende.add(pastille(Couleurs.FUITE, "En fuite"));
        return legende;
    }

    private JLabel pastille(Color couleur, String texte) {
        JLabel label = new JLabel("<html><span style='color:" + hex(couleur) + "'>●</span> " + texte + "</html>");
        styliser(label, 12, Font.PLAIN, Couleurs.TEXTE_SECONDAIRE);
        return label;
    }

    private static void styliser(JLabel label, int taille, int style, Color couleur) {
        label.setFont(new Font(Font.SANS_SERIF, style, taille));
        label.setForeground(couleur);
    }

    private static String hex(Color couleur) {
        return String.format("#%02x%02x%02x", couleur.getRed(), couleur.getGreen(), couleur.getBlue());
    }

    // --- Déroulement ---

    private boolean contreLOrdinateur() {
        return mode.campJoueur != null;
    }

    private void nouvellePartie(Mode nouveauMode) {
        minuterie.stop();
        mode = nouveauMode;
        monde = Monde.nouvellePartie(new Random());
        monde.setCampJoueur(mode.campJoueur);
        monde.ajouterObservateur(this);
        journal.setText("");
        if (contreLOrdinateur()) {
            message("Vous jouez le " + mode.campJoueur.getLibelle() + ". Quand l'un de vos soldats est encadré, "
                    + "cliquez sur une case éclairée pour le déplacer, sur un ennemi entouré pour l'attaquer, "
                    + "ou reposez-vous pour récupérer des PV.");
        } else {
            message("Les soldats des deux camps jouent à tour de rôle.");
        }
        monde.getSoldatActif();  // ouvre le premier tour
        plateau.setMonde(monde);

        boutonTour.setVisible(!contreLOrdinateur());
        boutonLecture.setVisible(!contreLOrdinateur());
        boutonRepos.setVisible(contreLOrdinateur());
        if (contreLOrdinateur() && !monde.attendLeJoueur()) {
            minuterie.start();
        }
        mettreAJour();
    }

    private void etapeAutomatique() {
        if (contreLOrdinateur()) {
            if (monde.attendLeJoueur() || monde.isPartieFinie()) {
                minuterie.stop();
            } else {
                monde.avancer();
            }
        } else {
            plateau.effacerTirs();
            monde.jouerTour();
        }
        mettreAJour();
    }

    private void basculerLecture() {
        if (minuterie.isRunning()) {
            minuterie.stop();
        } else {
            minuterie.start();
        }
        mettreAJour();
    }

    private void clicSurLePlateau(Position position) {
        if (!monde.attendLeJoueur()) {
            return;
        }
        Optional<Soldat> cible = monde.ciblesPossibles().stream()
                .filter(s -> s.getPosition().equals(position))
                .findFirst();
        plateau.effacerTirs();
        if (cible.isPresent()) {
            monde.attaquerAvecSoldatDuJoueur(cible.get());
        } else if (monde.deplacementsPossibles().contains(position)) {
            monde.deplacerSoldatDuJoueur(position);
        } else {
            return;
        }
        apresActionDuJoueur();
    }

    /** L'ordinateur enchaîne ses soldats, un par un, jusqu'au prochain soldat du joueur. */
    private void apresActionDuJoueur() {
        if (!monde.isPartieFinie() && !monde.attendLeJoueur()) {
            minuterie.restart();
        }
        mettreAJour();
    }

    private void mettreAJour() {
        boolean finie = monde.isPartieFinie();
        if (finie) {
            minuterie.stop();
            statut.setText(texteDeFin(monde) + " après " + monde.getTour() + " tours");
        } else if (monde.attendLeJoueur()) {
            Soldat actif = monde.getSoldatActif().orElseThrow();
            statut.setText("Tour " + monde.getTour() + " · à vous : " + actif.getNom()
                    + " (" + actif.getPv() + " PV, portée " + actif.getPortee() + ")");
        } else if (contreLOrdinateur()) {
            statut.setText("Tour " + monde.getTour() + " · l'ordinateur joue…");
        } else {
            statut.setText("Tour " + monde.getTour() + " · le " + monde.getCampCourant().getLibelle() + " ouvre le tour");
        }
        effectifs.setText(String.format("<html><span style='color:%s'>Lumineux %d soldats · %d PV</span>"
                        + "&nbsp;&nbsp;&nbsp;<span style='color:%s'>Obscur %d soldats · %d PV</span></html>",
                hex(Couleurs.LUMINEUX), monde.getSoldats(Camp.LUMINEUX).size(), monde.totalPv(Camp.LUMINEUX),
                hex(Couleurs.OBSCUR), monde.getSoldats(Camp.OBSCUR).size(), monde.totalPv(Camp.OBSCUR)));
        boutonTour.setEnabled(!finie && !minuterie.isRunning());
        boutonLecture.setEnabled(!finie);
        boutonLecture.setText(minuterie.isRunning() ? "Pause" : "Lecture");
        boutonRepos.setEnabled(monde.attendLeJoueur());
        plateau.repaint();
    }

    /** « Victoire ! » ou « Défaite » contre l'ordinateur, sinon le camp gagnant. */
    static String texteDeFin(Monde monde) {
        if (monde.getVainqueur().isEmpty()) {
            return "Match nul";
        }
        Camp gagnant = monde.getVainqueur().get();
        return monde.getCampJoueur()
                .map(joueur -> gagnant == joueur ? "Victoire !" : "Défaite")
                .orElse("Victoire du " + gagnant.getLibelle());
    }

    // --- Observation du monde ---

    @Override
    public void message(String texte) {
        if (texte.startsWith("──") && journal.getDocument().getLength() > 0) {
            journal.append("\n");
        }
        journal.append(texte + "\n");
        journal.setCaretPosition(journal.getDocument().getLength());
    }

    @Override
    public void attaque(Soldat attaquant, Soldat cible, int degats) {
        plateau.ajouterTir(attaquant, cible);
    }
}
