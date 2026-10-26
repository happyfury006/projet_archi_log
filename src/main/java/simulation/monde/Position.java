package simulation.monde;

/**
 * Case de la carte : x est la colonne, y la ligne.
 */
public record Position(int x, int y) {

    /** Distance en nombre de déplacements, diagonales comprises (distance de Tchebychev). */
    public int distance(Position autre) {
        return Math.max(Math.abs(x - autre.x), Math.abs(y - autre.y));
    }

    /** Carré de la distance euclidienne, utilisé pour départager deux cases à égalité. */
    public int distanceEuclidienneCarre(Position autre) {
        int dx = x - autre.x;
        int dy = y - autre.y;
        return dx * dx + dy * dy;
    }

    public Position decaler(int dx, int dy) {
        return new Position(x + dx, y + dy);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
