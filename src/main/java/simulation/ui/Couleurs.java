package simulation.ui;

import java.awt.Color;
import simulation.monde.Camp;

/** Palette commune à la fenêtre et au plateau. */
final class Couleurs {

    static final Color FOND = new Color(0x12, 0x15, 0x1f);
    static final Color PANNEAU = new Color(0x1a, 0x1e, 0x2b);
    static final Color CASE_CLAIRE = new Color(0x23, 0x29, 0x3a);
    static final Color CASE_FONCEE = new Color(0x1d, 0x22, 0x31);
    static final Color TEXTE = new Color(0xe6, 0xe9, 0xef);
    static final Color TEXTE_SECONDAIRE = new Color(0x9a, 0xa3, 0xb5);
    static final Color LUMINEUX = new Color(0x4d, 0xa8, 0xff);
    static final Color OBSCUR = new Color(0xff, 0x5c, 0x5c);
    static final Color FUITE = new Color(0xff, 0xc8, 0x3d);
    static final Color SURBRILLANCE = new Color(0xe6, 0xe9, 0xef, 0x30);
    static final Color CIBLE = new Color(0xff, 0xff, 0xff);
    static final Color PV_HAUT = new Color(0x4c, 0xd1, 0x7a);
    static final Color PV_MOYEN = new Color(0xff, 0xb0, 0x3d);
    static final Color PV_BAS = new Color(0xff, 0x5c, 0x5c);

    private Couleurs() {
    }

    static Color camp(Camp camp) {
        return camp == Camp.LUMINEUX ? LUMINEUX : OBSCUR;
    }

    static Color pv(double ratio) {
        return ratio > 0.6 ? PV_HAUT : ratio > 0.3 ? PV_MOYEN : PV_BAS;
    }
}
