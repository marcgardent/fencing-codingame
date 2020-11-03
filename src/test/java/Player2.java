import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;


public class Player2 {

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

            ArrayList<ActionType> actions = new ArrayList<ActionType>();

            actions.add(ActionType.BREAK);
            if (iCanWalk) actions.add(ActionType.WALK);
            if (iCanRetreat) actions.add(ActionType.RETREAT);
            if (youCanLunge) actions.add(ActionType.PARRY);
            if (iCanAttack) actions.add(ActionType.LUNGE);

            ActionType myAction = getRandom(actions.stream().toArray(ActionType[]::new));

            System.err.printf("Playing %s, steps: %d %n", myAction.name(), distance);
            System.out.printf("%d%n", myAction.code);
        }
    }
}