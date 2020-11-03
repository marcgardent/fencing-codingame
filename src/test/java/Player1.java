import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

enum ActionType {
    //League 0
    SUPPRESSED(0, Integer.MAX_VALUE, 0, 0, 0, 0),
    BREAK(1, 0, 2, 0, 0, 0),
    WALK(2, 0, -1, 20, 0, 0),
    RETREAT(3, 0, -1, -20, 0, 0),
    LUNGE(4, 0, -2, 0, 0, 30),
    PARRY(5, 0, -2, 0, 30, 0),

    //League 1
    MIDDLE_POSTURE(6, 1, -1, 0, 0, 0),
    TOP_POSTURE(7, 1, -1, 0, 0, 0),
    BOTTOM_POSTURE(8, 1, -1, 0, 0, 0),

    //League 2
    DOUBLE_FORWARD_MOVE(9, 2, -1, 40, 0, 0),
    DOUBLE_BACKWARD_MOVE(10, 2, -1, -30, 0, 0),

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

    public static boolean isTouchedWhenAttackDefense(int striker, int defender) {
//        System.err.printf("AttackDefense striker=%d defender=%d%n",
//                (striker + ActionType.LUNGE.offensiveRange),
//                (defender - ActionType.PARRY.defensiveRange));

        return (defender - ActionType.PARRY.defensiveRange) + (striker + ActionType.LUNGE.offensiveRange) > 500;
    }

    public static boolean isTouchedWhenAttack(int striker, int defender) {
//        System.err.printf("Attack striker=%d defender=%d%n",
//                (striker + ActionType.LUNGE.offensiveRange),
//                (defender));

        return (defender) + (striker + ActionType.LUNGE.offensiveRange) > 500;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tick = 0;
        while (true) {
            //Me
            int myPosition = in.nextInt();
            int myEnergy = in.nextInt();
            int myScore = in.nextInt();
            ActionType myPosture = ActionType.fromInteger(in.nextInt());

            //You
            int yourPosition = in.nextInt();
            int yourEnergy = in.nextInt();
            int yourScore = in.nextInt();
            ActionType yourPosture = ActionType.fromInteger(in.nextInt());

            System.err.printf("ME  position=%d %s energy=%d score=%d %n", myPosition, myPosture.name(), myEnergy, myScore);
            System.err.printf("YOU position=%d %s energy=%d score=%d %n", yourPosition, yourPosture.name(), yourEnergy, yourScore);

            ActionType myAction = getRandom(ActionType.getByLeague(0));
            int distance = (yourPosition - myPosition) / ActionType.WALK.move;

            boolean youCanAttack = yourEnergy > distance - ActionType.LUNGE.energy;
            boolean iCanAttack = myEnergy > distance - ActionType.LUNGE.energy;

            boolean leader = myScore > yourScore;
            boolean challenger = myScore < yourScore;

            boolean iCanWalk = 0 < myEnergy + ActionType.WALK.energy;
            boolean youCanWalk = 0 < yourEnergy + ActionType.WALK.energy;

            boolean iCanRetreat = 0 < myEnergy + ActionType.RETREAT.energy && myPosition > 0;
            boolean youCanRetreat = 0 < yourEnergy + ActionType.RETREAT.energy && myPosition < 500;

            boolean iCanParry = 0 < myEnergy + ActionType.PARRY.energy;
            boolean youCanParry = 0 < yourEnergy + ActionType.RETREAT.energy;

            boolean iCanLunge = 0 < myEnergy + ActionType.LUNGE.energy && isTouchedWhenAttack(myPosition, (500 - yourPosition));
            boolean youCanLunge = 0 < yourEnergy + ActionType.LUNGE.energy && isTouchedWhenAttack((500 - yourPosition), myPosition);

            boolean iMustLunge = 0 < myEnergy + ActionType.LUNGE.energy && isTouchedWhenAttackDefense(myPosition, (500 - yourPosition));
            boolean youMustLunge = 0 < yourEnergy + ActionType.LUNGE.energy && isTouchedWhenAttackDefense((500 - yourPosition), myPosition);

            System.err.printf("ME  canWalk=%b canRetreat=%b canParry=%b canLunge=%b mustLunge=%b %n", iCanWalk, iCanRetreat, iCanParry, iCanLunge, iMustLunge);
            System.err.printf("YOU canWalk=%b canRetreat=%b canParry=%b canLunge=%b mustLunge=%b %n", youCanWalk, youCanRetreat, youCanParry, youCanLunge, youMustLunge);

            //myAction = getRandom(new ActionType[]{ActionType.RETREAT, ActionType.LUNGE, ActionType.PARRY});

            // panic mode: leak of energy
            if (youMustLunge) {
                myAction = iCanRetreat ? ActionType.RETREAT : ActionType.BREAK;
            } else if (iMustLunge || iCanLunge) {
                myAction = ActionType.LUNGE;
            } else if (iCanAttack && iCanWalk) {
                myAction = ActionType.WALK;
            } else if (youCanAttack && iCanRetreat && distance < 5) {
                myAction = ActionType.RETREAT;
            } else {
                myAction = ActionType.BREAK;
            }

            System.err.printf("Playing %s, steps: %d %n", myAction.name(), distance);
            System.out.printf("%d%n", myAction.code);
        }
    }
}