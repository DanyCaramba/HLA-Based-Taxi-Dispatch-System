package Client;
import INIT.Variables;
import dissimlab.random.RNGenerator;
import java.util.Random;

public class Client {

    private final RNGenerator generator = new RNGenerator();
    private static final Random random = new Random();
    int clientID=0;
    int[] tab;
    int[] timeToNext;


    public Client()
    {
        timeToNext = new int[Variables.l];
        for(int i=0 ; i<timeToNext.length ; i++)
        {
            timeToNext[i]=generateTimeToNext(i);
            System.out.println(timeToNext[i]);
        }
    }

    public int[] produce(int i)
    {
        int[] client = new int[3];
        clientID++;
        tab=drawTwoUniqueNumbers(i+1);
        client[0]=this.clientID;
        client[1]=this.tab[0];
        client[2]=this.tab[1];

        for (int j=0 ; j<timeToNext.length ; j++)
        {
            if(i!=j)
                timeToNext[j]=timeToNext[j]-timeToNext[i];
        }

        timeToNext[i]=generateTimeToNext(i);


        return client;
    }


    public  int findShortestTimeIndex()
    {
        int shortestTimeToNext = Integer.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < timeToNext.length; i++) {
            if (timeToNext[i] < shortestTimeToNext) {
                shortestTimeToNext = timeToNext[i];
                index = i;
            }
        }

        return index;
    }

    private int generateTimeToNext(int l)
    {
        int x;

        if(l==0)
            x= (int) generator.exponential( 1 /Variables.lambda1);
        else if (l==1)
            x=(int) generator.exponential(1 /Variables.lambda2);
        else if (l==2)
            x= (int) generator.exponential( 1 /Variables.lambda3);
       else if (l==3)
            x=(int) generator.exponential(1 /Variables.lambda4);
        else if (l==4)
            x=(int) generator.exponential(1 /Variables.lambda5);
        else if (l==5)
            x=(int) generator.exponential(1 /Variables.lambda6);
        else if (l==6)
            x=(int) generator.exponential(1 /Variables.lambda7);
        else if (l==7)
            x=(int) generator.exponential(1 /Variables.lambda8);
        else if (l==8)
            x=(int) generator.exponential(1 /Variables.lambda9);
        else if (l==9)
            x=(int) generator.exponential(1 /Variables.lambda11);
        else
          x= (int) generator.exponential( 1 /Variables.lambda10);


        if (x!=0)
            return x;
        else
            return x+1;
    }

    public int[] drawTwoUniqueNumbers(int firstNumber)
    {
        int secondNumber;

        do {
            secondNumber = random.nextInt(Variables.l) + 1;
        } while (secondNumber == firstNumber);


        return new int[]{firstNumber, secondNumber};
    }

}
