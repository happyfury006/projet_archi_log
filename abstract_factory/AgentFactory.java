// Abstract Factory-related files
package abstract_factory;

public interface AgentFactory {
    Soldat creerDist(int x, int y);
    Soldat creerCac(int x, int y);
}