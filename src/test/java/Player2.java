import java.util.Random;
import java.util.Scanner;

public class Player2 {

    private static final Random random = new Random();

    public static int getRandom(int[] array) {
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
            int myPosture = in.nextInt();
            int myAttitude = in.nextInt();
            int myEnergy = in.nextInt();
            int myScore = in.nextInt();


            //You
            int yourPosition = in.nextInt();
            int yourPosture = in.nextInt();
            int yourAttitude = in.nextInt();
            int yourEnergy = in.nextInt();
            int yourScore = in.nextInt();


            int myAction = getRandom(new int[]{0, 1, 2, 3, 4, 5, 6});
            System.out.printf("%d%n", myAction);
        }
    }
}
