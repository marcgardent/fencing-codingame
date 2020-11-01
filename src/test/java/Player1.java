import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

enum ActionType {
    //League 0
    SUPPRESSED(0, 0, 0, 0, 0, 0),
    MIDDLE_POSTURE(1, 0, -1, 0, 0, 0),
    FORWARD_MOVE(2, 0, -1, 50, 0, 0),
    BACKWARD_MOVE(3, 0, -1, -50, 0, 0),
    OFFENSIVE_ATTITUDE(4, 0, -1, 0, 0, 30),
    BREAK_ATTITUDE(5, 0, 2, 0, 0, 0),
    DEFENSIVE_ATTITUDE(6, 0, -1, 0, 30, 0),

    //League 1
    TOP_POSTURE(7, 1, -1, 0, 0, 0),
    BOTTOM_POSTURE(8, 1, -1, 0, 0, 0),

    //League 2
    DOUBLE_FORWARD_MOVE(9, 2, -1, 100, 0, 0),
    DOUBLE_BACKWARD_MOVE(10, 2, -1, -75, 0, 0),

    // league 3
    OFFENSIVE_RANGE_SKILL(11, 3, -5, 0, 0, 0),
    DEFENSIVE_RANGE_SKILL(12, 3, -5, 0, 0, 0),
    ENERGY_MAX_SKILL(13, 3, -5, 0, 0, 0),
    FORWARD_SKILL(14, 3, -5, 0, 0, 0),
    BACKWARD_SKILL(15, 3, -5, 0, 0, 0),
    DOUBLE_FORWARD_SKILL(16, 3, -5, 0, 0, 0),
    DOUBLE_BACKWARD_SKILL(17, 3, -5, 0, 0, 0);

    public final int code;
    public final int league;
    public final int energy;
    public final int move;
    public final int defensiveRange;
    public final int offensiveRange;

    ActionType(int code, int league, int energy, int move, int defensiveRange, int offensiveRange) {
        this.code = code;
        this.league = league;
        this.energy = energy;
        this.move = move;
        this.defensiveRange = defensiveRange;
        this.offensiveRange = offensiveRange;
    }

    public static ActionType[] getByLeague(int leagueLevel) {
        return Arrays.stream(ActionType.values())
                .filter(x -> x.league <= leagueLevel).toArray(ActionType[]::new);
    }

    public static ActionType fromInteger(int code) {
        return Arrays.stream(ActionType.values())
                .filter(x -> x.code == code).findFirst().orElseGet(() -> null);
    }
}

public class Player1 {

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
            int distance = yourPosition - myPosition;

            if (myEnergy == 0) {
                myAction = ActionType.BREAK_ATTITUDE;
            } else if (myScore <= yourScore && myEnergy > 10) {
                //attack
                if (myAttitude != ActionType.OFFENSIVE_ATTITUDE) {
                    myAction = ActionType.OFFENSIVE_ATTITUDE;
                } else {
                    myAction = ActionType.FORWARD_MOVE;
                }
            } else {
                //defense
                if (distance > 100) {
                    myAction = ActionType.BREAK_ATTITUDE;
                } else if (myAttitude != ActionType.DEFENSIVE_ATTITUDE) {
                    myAction = ActionType.DEFENSIVE_ATTITUDE;
                } else if (myPosition > 100) {
                    myAction = ActionType.BACKWARD_MOVE;
                } else {
                    myAction = ActionType.FORWARD_MOVE;
                }
            }
            System.out.printf("%d%n", myAction.code);
        }
    }
}
