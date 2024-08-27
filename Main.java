package core;
public class Main {
    World world;
    public Main(World world) {
        this.world = world;
    }
    public static void main(String[] args) {
        int width = 60;
        int height = 40;
        World ourWorld = new World();
        ourWorld.runGame();
    }
}
