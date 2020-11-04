import com.codingame.gameengine.runner.MultiplayerGameRunner;

public class Main {
    public static void main(String[] args) {
        
        MultiplayerGameRunner gameRunner = new MultiplayerGameRunner();
        gameRunner.addAgent(Player.class);
        gameRunner.addAgent(Player2.class);
        gameRunner.setLeagueLevel(2);
        gameRunner.start();
    }
}
