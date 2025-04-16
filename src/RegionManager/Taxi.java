package RegionManager;

import java.util.Random;

public class Taxi {

   static int nextTaxiID = 1;
   int  taxiID=0;
    int destination;
    boolean isFree=true;

    Random random = new Random();

    public Taxi() {
        this.taxiID = nextTaxiID++;
        this.isFree=true;
        //System.out.println("Generouje nowe taxi o ID: "+taxiID);

    }

    public int getTaxiID() {
        return taxiID;
    }

    public void setTaxiID(int taxiID) {
        this.taxiID = taxiID;
    }


    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public boolean isFree() {
        return isFree;
    }

    public void setFree(boolean free) {
        isFree = free;
    }


}
