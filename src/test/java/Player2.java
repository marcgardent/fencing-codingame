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
        return (defender + ActionType.PARRY.distance) + (striker + ActionType.LUNGE.distance) > 500;
    }

    public static boolean isTouchedWhenAttack(int striker, int defender) {
//        System.err.printf("Attack striker=%d defender=%d%n",
//                (striker + ActionType.LUNGE.offensiveRange),
//                (defender));
        return (defender) + (striker + ActionType.LUNGE.distance) > 500;
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tick = 0;
        while (true) {
            //Me
            int myPosition = in.nextInt();
            int myEnergy = in.nextInt();
            int myScore = in.nextInt();
            int myDrugCount = in.nextInt();
            int myEnergyMax = in.nextInt();
            int myBreakSkill = in.nextInt();
            int myForwardSkill = in.nextInt();
            int myDoubleForwardSkill = in.nextInt();
            int myBackwardSkill = in.nextInt();
            int myDoubleBackwardSkill = in.nextInt();
            int myLungeDistanceSkill = in.nextInt();
            int myParryDistanceSkill = in.nextInt();

            //ActionType myPosture = ActionType.fromString(in.next());

            //You
            int yourPosition = in.nextInt();
            int yourEnergy = in.nextInt();
            int yourScore = in.nextInt();
            int yourDrugCount = in.nextInt();
            int yourEnergyMax = in.nextInt();
            int yourBreakSkill = in.nextInt();
            int yourForwardSkill = in.nextInt();
            int yourDoubleForwardSkill = in.nextInt();
            int yourBackwardSkill = in.nextInt();
            int yourDoubleBackwardSkill = in.nextInt();
            int yourLungeDistanceSkill = in.nextInt();
            int yourParryDistanceSkill = in.nextInt();

            //ActionType yourPosture = ActionType.fromString(in.next());


            System.err.printf("YOU position=%d energy=%d score=%d %n", yourPosition, yourEnergy, yourScore);

            System.err.printf("ME  position=%d energy=%d score=%d %n", myPosition, myEnergy, myScore);

            System.err.printf(
                    "ME EnergyMax=%d BreakSkill=%d ForwardSkill=%d DoubleForwardSkill=%d BackwardSkill=%d DoubleBackwardSkill=%d LungeDistanceSkill=%d ParryDistanceSkill=%d %n",
                    myEnergyMax,
                    myBreakSkill,
                    myForwardSkill,
                    myDoubleForwardSkill,
                    myBackwardSkill,
                    myDoubleBackwardSkill,
                    myLungeDistanceSkill,
                    myParryDistanceSkill);

            int distance = (yourPosition - myPosition) / ActionType.WALK.move;

            boolean youCanAttack = yourEnergy > distance - ActionType.LUNGE.energy;
            boolean iCanAttack = myEnergy > distance - ActionType.LUNGE.energy;

            boolean leader = myScore > yourScore;
            boolean challenger = myScore < yourScore;

            boolean iCanWalk = 0 < myEnergy + ActionType.WALK.energy;
            boolean youCanWalk = 0 < yourEnergy + ActionType.WALK.energy;
            boolean iCanDoubleWalk = 0 < myEnergy + ActionType.DOUBLE_WALK.energy;
            boolean youCanDoubleWalk = 0 < yourEnergy + ActionType.DOUBLE_WALK.energy;

            boolean iCanRetreat = 0 < myEnergy + ActionType.RETREAT.energy && myPosition > 0;
            boolean youCanRetreat = 0 < yourEnergy + ActionType.RETREAT.energy && myPosition < 500;

            boolean iCanDoubleRetreat = 0 < myEnergy + ActionType.DOUBLE_RETREAT.energy && myPosition > 0;
            boolean youDoubleCanRetreat = 0 < yourEnergy + ActionType.DOUBLE_RETREAT.energy && myPosition < 500;


            boolean iCanParry = 0 < myEnergy + ActionType.PARRY.energy;
            boolean youCanParry = 0 < yourEnergy + ActionType.RETREAT.energy;

            boolean iCanLunge = 0 < myEnergy + ActionType.LUNGE.energy && isTouchedWhenAttack(myPosition, (500 - yourPosition));
            boolean youCanLunge = 0 < yourEnergy + ActionType.LUNGE.energy && isTouchedWhenAttack((500 - yourPosition), myPosition);

            boolean iMustLunge = 0 < myEnergy + ActionType.LUNGE.energy && isTouchedWhenAttackDefense(myPosition, (500 - yourPosition));
            boolean youMustLunge = 0 < yourEnergy + ActionType.LUNGE.energy && isTouchedWhenAttackDefense((500 - yourPosition), myPosition);

            boolean iCanForwardDrug = 0 < myEnergy + ActionType.WALK_DRUG.energy;
            boolean iCanDoubleForwardDrug = 0 < myEnergy + ActionType.DOUBLE_WALK_DRUG.energy;
            boolean iCanRetreatDrug = 0 < myEnergy + ActionType.RETREAT_DRUG.energy;
            boolean iCanDoubleRetreatDrug = 0 < myEnergy + ActionType.DOUBLE_RETREAT_DRUG.energy;

            boolean iCanLungeDrug = 0 < myEnergy + ActionType.LUNGE_DRUG.energy;
            boolean iCanParryDrug = 0 < myEnergy + ActionType.PARRY_DRUG.energy;

            boolean iCanEnergyMaxDrug = 0 < myEnergy + ActionType.ENERGY_MAX_DRUG.energy;
            boolean iCanBreakDrug = 0 < myEnergy + ActionType.BREAK_DRUG.energy;

//            boolean iCanPostureMiddle = 0 < yourEnergy + ActionType.MIDDLE_POSTURE.energy && myPosture != ActionType.MIDDLE_POSTURE;
//            boolean iCanPostureLeft   = 0 < yourEnergy + ActionType.MIDDLE_POSTURE.energy && myPosture != ActionType.LEFT_POSTURE;
//            boolean iCanPostureRight = 0 < yourEnergy + ActionType.MIDDLE_POSTURE.energy && myPosture != ActionType.RIGHT_POSTURE;

            System.err.printf("ME  canWalk=%b canRetreat=%b canParry=%b canLunge=%b mustLunge=%b %n", iCanWalk, iCanRetreat, iCanParry, iCanLunge, iMustLunge);
            System.err.printf("YOU canWalk=%b canRetreat=%b canParry=%b canLunge=%b mustLunge=%b %n", youCanWalk, youCanRetreat, youCanParry, youCanLunge, youMustLunge);

            ArrayList<ActionType> actions = new ArrayList<ActionType>();

            actions.add(ActionType.BREAK);
            if (iCanWalk) actions.add(ActionType.WALK);
            if (iCanRetreat) actions.add(ActionType.RETREAT);
            if (iCanDoubleWalk) actions.add(ActionType.DOUBLE_WALK);
            if (iCanDoubleRetreat) actions.add(ActionType.DOUBLE_RETREAT);
            if (youCanLunge) actions.add(ActionType.PARRY);
            if (iCanRetreat && youCanLunge) actions.add(ActionType.PARRY);
            if (iCanLunge) actions.add(ActionType.LUNGE);

            if (iCanForwardDrug) actions.add(ActionType.WALK_DRUG);
            if (iCanDoubleForwardDrug) actions.add(ActionType.DOUBLE_WALK_DRUG);
            if (iCanRetreatDrug) actions.add(ActionType.RETREAT_DRUG);
            if (iCanDoubleRetreatDrug) actions.add(ActionType.DOUBLE_RETREAT_DRUG);

            if (iCanLungeDrug) actions.add(ActionType.LUNGE_DRUG);
            if (iCanParryDrug) actions.add(ActionType.PARRY_DRUG);

            if (iCanEnergyMaxDrug) actions.add(ActionType.ENERGY_MAX_DRUG);
            if (iCanBreakDrug) actions.add(ActionType.BREAK_DRUG);

            ActionType myAction = getRandom(actions.stream().toArray(ActionType[]::new));
            //myAction = myPosture != ActionType.TOP_POSTURE ? ActionType.TOP_POSTURE : myAction;


            System.err.printf("Playing %s, steps: %d %n", myAction.name(), distance);
            System.out.printf("%s%n", ActionType.PARRY.name());
        }
    }
}