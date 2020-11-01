import java.util.Random;
import java.util.Scanner;

public class Player2 {

    private static final Random random = new Random();

    public static <T> T getRandom(T[] array) {
        int rnd = random.nextInt(array.length);
        return array[rnd];
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tick = 0;
        while (true) {

            int result = in.nextInt();

            //Me
            int myPosition = in.nextInt();
            ActionType myPosture = ActionType.fromInteger(in.nextInt());
            ActionType myAttitude = ActionType.fromInteger(in.nextInt());
            int myEnergy = in.nextInt();
            int myScore = in.nextInt();

            //You
            int yourPosition = in.nextInt();
            ActionType yourPosture = ActionType.fromInteger(in.nextInt());
            ActionType yourAttitude = ActionType.fromInteger(in.nextInt());
            int yourEnergy = in.nextInt();
            int yourScore = in.nextInt();

            System.err.printf("ME  position=%d %s %s energy=%d score=%d %n", myPosition, myPosture.name(), myAttitude.name(), myEnergy, myScore);
            System.err.printf("YOU position=%d %s %s energy=%d score=%d %n", yourPosition, yourPosture.name(), yourAttitude.name(), yourEnergy, yourScore);
            System.err.printf("Result %d%n", result);

            ActionType myAction = getRandom(ActionType.getByLeague(0));
            int distance = (yourPosition - myPosition) / ActionType.FORWARD_MOVE.move;

            boolean youCanAttack = yourEnergy > distance + (yourAttitude == ActionType.OFFENSIVE_ATTITUDE ? 0 : 1);
            boolean iCanAttack = myEnergy > distance + (myAttitude == ActionType.OFFENSIVE_ATTITUDE ? 0 : 1);
            boolean leader = myScore > yourScore;
            boolean challenger = myScore < yourScore;
            boolean tired = myEnergy == 0;

            // panic mode: leak of energy
            if (tired) {
                myAction = ActionType.BREAK_ATTITUDE;
            } else if (iCanAttack) {
                myAction = ActionType.OFFENSIVE_ATTITUDE;
                if (myAction == myAttitude) {
                    myAction = ActionType.FORWARD_MOVE;
                }
            } else if (distance < 6) {
                myAction = ActionType.BACKWARD_MOVE;
            } else if (distance > 6) {
                myAction = ActionType.FORWARD_MOVE;
            } else if (myEnergy < 20) {
                myAction = ActionType.BREAK_ATTITUDE;
            } else {
                myAction = ActionType.DEFENSIVE_ATTITUDE;
            }

            System.err.printf("Playing %s, steps: %d %n", myAction.name(), distance);
            System.out.printf("%d%n", myAction.code);
        }
    }
}
