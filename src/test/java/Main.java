import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {

        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.addAgent(Player.class);
        //gameRunner.addAgent(Player2.class);
        gameRunner.addAgent("D:\\Temp\\codeRoyale\\Fencing\\main\\bin\\Debug\\netcoreapp3.1\\main.exe");
        //gameRunner.addAgent("D:\\Temp\\codeRoyale\\Fencing\\main\\bin\\Debug\\netcoreapp3.1\\main.exe");
        gameRunner.setLeagueLevel(3);
        gameRunner.start();
    }
}
