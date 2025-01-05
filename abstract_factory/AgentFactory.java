// Abstract Factory-related files

public interface AgentFactory {
    Soldat creerDist(int x, int y);
    Soldat creerCac(int x, int y);
}