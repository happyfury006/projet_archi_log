package simulation.ui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.JPanel;
import simulation.monde.Camp;
import simulation.monde.Monde;
import simulation.monde.Position;
import simulation.soldat.Soldat;

/** Dessine la carte, les soldats et les attaques du dernier tour. */
@SuppressWarnings("serial")  // le panneau n'est jamais sérialisé
final class PlateauPanel extends JPanel {

    /** Une attaque à dessiner : un trait entre l'attaquant et sa cible. */
    private record Tir(Position depart, Position arrivee, Camp camp) {
    }

    private Monde monde;
    private final List<Tir> tirs = new ArrayList<>();
    private Consumer<Position> surClic = position -> { };

    PlateauPanel() {
        setBackground(Couleurs.FOND);
        setPreferredSize(new Dimension(640, 640));
        setToolTipText("");  // active les infobulles
        MouseAdapter souris = new MouseAdapter() {
            // mousePressed plutôt que mouseClicked, qui est ignoré si la souris bouge pendant le clic
            @Override
            public void mousePressed(MouseEvent event) {
                if (monde != null && monde.estDansLaCarte(caseSous(event.getPoint()))) {
                    surClic.accept(caseSous(event.getPoint()));
                }
            }

            @Override
            public void mouseMoved(MouseEvent event) {
                boolean cliquable = monde != null && estCliquable(caseSous(event.getPoint()));
                setCursor(Cursor.getPredefinedCursor(cliquable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
            }
        };
        addMouseListener(souris);
        addMouseMotionListener(souris);
    }

    /** Appelé avec la case cliquée par le joueur. */
    void setSurClic(Consumer<Position> surClic) {
        this.surClic = surClic;
    }

    private boolean estCliquable(Position position) {
        return monde.deplacementsPossibles().contains(position)
                || monde.ciblesPossibles().stream().anyMatch(s -> s.getPosition().equals(position));
    }

    void setMonde(Monde monde) {
        this.monde = monde;
        tirs.clear();
        repaint();
    }

    void effacerTirs() {
        tirs.clear();
    }

    void ajouterTir(Soldat attaquant, Soldat cible) {
        tirs.add(new Tir(attaquant.getPosition(), cible.getPosition(), attaquant.getCamp()));
    }

    // --- Géométrie ---

    private int tailleCase() {
        return Math.max(1, Math.min(getWidth() / monde.getLargeur(), getHeight() / monde.getHauteur()));
    }

    private Point origine() {
        int taille = tailleCase();
        return new Point((getWidth() - taille * monde.getLargeur()) / 2, (getHeight() - taille * monde.getHauteur()) / 2);
    }

    private Point centre(Position position) {
        int taille = tailleCase();
        Point o = origine();
        return new Point(o.x + position.x() * taille + taille / 2, o.y + position.y() * taille + taille / 2);
    }

    private Position caseSous(Point point) {
        int taille = tailleCase();
        Point o = origine();
        return new Position(Math.floorDiv(point.x - o.x, taille), Math.floorDiv(point.y - o.y, taille));
    }

    // --- Dessin ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (monde == null) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        dessinerCases(g2);
        if (monde.attendLeJoueur()) {
            dessinerChoixDuJoueur(g2);
        }
        dessinerTirs(g2);
        for (Soldat soldat : monde.getSoldats()) {
            dessinerSoldat(g2, soldat);
        }
        if (monde.isPartieFinie()) {
            dessinerFinDePartie(g2);
        }
        g2.dispose();
    }

    private void dessinerCases(Graphics2D g2) {
        int taille = tailleCase();
        Point o = origine();
        for (int y = 0; y < monde.getHauteur(); y++) {
            for (int x = 0; x < monde.getLargeur(); x++) {
                g2.setColor((x + y) % 2 == 0 ? Couleurs.CASE_CLAIRE : Couleurs.CASE_FONCEE);
                g2.fillRect(o.x + x * taille, o.y + y * taille, taille, taille);
            }
        }
    }

    /** Soldat à jouer, cases accessibles et ennemis à portée. */
    private void dessinerChoixDuJoueur(Graphics2D g2) {
        int taille = tailleCase();
        Point o = origine();
        Soldat actif = monde.getSoldatActif().orElseThrow();

        g2.setColor(Couleurs.SURBRILLANCE);
        for (Position position : monde.deplacementsPossibles()) {
            g2.fillRect(o.x + position.x() * taille + 2, o.y + position.y() * taille + 2, taille - 4, taille - 4);
        }

        Stroke ancien = g2.getStroke();
        g2.setStroke(new BasicStroke(Math.max(2f, taille / 20f)));
        g2.setColor(Couleurs.TEXTE);
        g2.drawRect(o.x + actif.getPosition().x() * taille + 2, o.y + actif.getPosition().y() * taille + 2, taille - 4, taille - 4);

        g2.setColor(Couleurs.CIBLE);
        for (Soldat cible : monde.ciblesPossibles()) {
            Point c = centre(cible.getPosition());
            int rayon = (int) (taille * 0.42);
            g2.drawOval(c.x - rayon, c.y - rayon - taille / 14, 2 * rayon, 2 * rayon);
        }
        g2.setStroke(ancien);
    }

    private void dessinerTirs(Graphics2D g2) {
        Stroke ancien = g2.getStroke();
        g2.setStroke(new BasicStroke(Math.max(2f, tailleCase() / 24f), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // La cible a pu bouger ou mourir depuis : le point d'impact montre où le coup est arrivé
        int impact = Math.max(6, tailleCase() / 6);
        for (Tir tir : tirs) {
            Point a = centre(tir.depart());
            Point b = centre(tir.arrivee());
            g2.setColor(Couleurs.camp(tir.camp()));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
            g2.drawLine(a.x, a.y, b.x, b.y);
            g2.fillOval(b.x - impact / 2, b.y - impact / 2, impact, impact);
        }
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setStroke(ancien);
    }

    private void dessinerSoldat(Graphics2D g2, Soldat soldat) {
        int taille = tailleCase();
        Point c = centre(soldat.getPosition());
        int diametre = (int) (taille * 0.62);
        int gauche = c.x - diametre / 2;
        int haut = c.y - diametre / 2 - taille / 14;

        // Pion
        g2.setColor(Couleurs.camp(soldat.getCamp()));
        g2.fillOval(gauche, haut, diametre, diametre);

        // Anneau pointillé quand le soldat est en fuite
        if (soldat.getEtat().getNom().equals("fuite")) {
            Stroke ancien = g2.getStroke();
            float epaisseur = Math.max(2f, taille / 22f);
            g2.setStroke(new BasicStroke(epaisseur, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1f,
                    new float[] {epaisseur * 1.5f, epaisseur * 1.8f}, 0f));
            g2.setColor(Couleurs.FUITE);
            int marge = (int) (epaisseur * 1.6);
            g2.drawOval(gauche - marge, haut - marge, diametre + 2 * marge, diametre + 2 * marge);
            g2.setStroke(ancien);
        }

        // Lettre de l'espèce
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(10, diametre / 2)));
        FontMetrics fm = g2.getFontMetrics();
        String lettre = String.valueOf(soldat.getSymbole());
        g2.setColor(Couleurs.FOND);
        g2.drawString(lettre, c.x - fm.stringWidth(lettre) / 2, haut + (diametre + fm.getAscent() - fm.getDescent()) / 2);

        // Barre de vie
        int largeurBarre = (int) (taille * 0.7);
        int hauteurBarre = Math.max(3, taille / 16);
        int xBarre = c.x - largeurBarre / 2;
        int yBarre = haut + diametre + Math.max(3, taille / 18);
        double ratio = (double) soldat.getPv() / soldat.getPvMax();
        g2.setColor(Couleurs.CASE_FONCEE.darker());
        g2.fillRoundRect(xBarre, yBarre, largeurBarre, hauteurBarre, hauteurBarre, hauteurBarre);
        g2.setColor(Couleurs.pv(ratio));
        g2.fillRoundRect(xBarre, yBarre, (int) (largeurBarre * ratio), hauteurBarre, hauteurBarre, hauteurBarre);
    }

    private void dessinerFinDePartie(Graphics2D g2) {
        String texte = FenetreJeu.texteDeFin(monde);
        g2.setFont(new Font(Font.SANS_SERIF, Font.BOLD, Math.max(16, getWidth() / 22)));
        FontMetrics fm = g2.getFontMetrics();
        int largeur = fm.stringWidth(texte) + 48;
        int hauteur = fm.getHeight() + 28;
        int x = (getWidth() - largeur) / 2;
        int y = (getHeight() - hauteur) / 2;

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.9f));
        g2.setColor(Couleurs.PANNEAU);
        g2.fillRoundRect(x, y, largeur, hauteur, 18, 18);
        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(monde.getVainqueur().map(Couleurs::camp).orElse(Couleurs.TEXTE));
        g2.drawRoundRect(x, y, largeur, hauteur, 18, 18);
        g2.drawString(texte, x + 24, y + 14 + fm.getAscent());
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        if (monde == null) {
            return null;
        }
        return monde.getSoldatEn(caseSous(event.getPoint()))
                .map(s -> "<html><b>" + s.getNom() + "</b> · " + s.getCamp().getLibelle()
                        + "<br>" + s.getPv() + " / " + s.getPvMax() + " PV"
                        + "<br>Dégâts " + s.getDegats() + " · portée " + s.getPortee()
                        + "<br>Mode : " + s.getEtat().getNom() + "</html>")
                .orElse(null);
    }
}
