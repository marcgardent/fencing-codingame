import java.util.Random;
import java.util.Scanner;

public class Player1 {

    private static final Random random = new Random();

    public static int getRandom(int[] array) {
        int rnd = random.nextInt(array.length);
        return array[rnd];
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int tick = 0;
        while (true) {


            //System.err.println(in.nextLine());
            //Me
            int myPosition = in.nextInt();
            int myEnergy = in.nextInt();
            int myScore = in.nextInt();

            //You
            int yourPosition = in.nextInt();
            int yourEnergy = in.nextInt();
            int yourScore = in.nextInt();

            int myMove = getRandom(new int[]{0, 1, 2});
            int myAction = getRandom(new int[]{0, 3, 4, 5});

            System.out.println(String.format("%d %d", myMove, myAction));
        }
    }
}
