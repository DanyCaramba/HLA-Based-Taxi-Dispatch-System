package TravelManager;

import INIT.Variables;

import java.util.LinkedList;
import java.util.Random;

public class TravelManager {

    LinkedList<Travel>travels = new LinkedList<>();
    int timeToNext;
    private Random random;

    public TravelManager() {
        random = new Random();
        timeToNext = generateTimeToNext();
    }

    public double generateTravelTime(double fedTime)
    {
        timeToNext=generateTimeToNext();

        int count = random.nextInt(Variables.a)+Variables.b;

        double travelEnd=fedTime+count;

        return travelEnd;
    }


    public int getTimeToNext() {
        return timeToNext;
    }

    private int generateTimeToNext()
    {
        return random.nextInt(10)+1;
    }
}
