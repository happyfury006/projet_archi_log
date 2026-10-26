package simulation.monde;

public enum Camp {
    LUMINEUX("côté lumineux"),
    OBSCUR("côté obscur");

    private final String libelle;

    Camp(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public Camp adverse() {
        return this == LUMINEUX ? OBSCUR : LUMINEUX;
    }
}
