public class AttaquerCac implements Strategie {
    @Override
    public void executer(Soldat soldat) {
        System.out.println("Le soldat attaque au corps à corps avec " + soldat.getDps() + " de dégâts.");
    }
}