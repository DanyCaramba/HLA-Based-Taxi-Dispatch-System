package RegionManager;

import INIT.Variables;

import java.util.LinkedList;

public class Region {
    int regionID;
    static int nextRegion=1;
    public LinkedList<ClientT> clientQueue;
    public LinkedList<Taxi> taxiQueue;
    public Region()
    {
        this.regionID=nextRegion++;
        this.clientQueue = new LinkedList<>();
        this.taxiQueue = new LinkedList<>();
        this.createTaxies();
    }

    public void createTaxies()
    {
        for(int i = 0; i< Variables.taxiNumber; i++)
        {
            Taxi taxi = new Taxi();
            taxiQueue.add(taxi);
        }
    }

    public Taxi addTaxi()
    {
        Taxi taxi = new Taxi();
        taxiQueue.add(taxi);
        return taxi;

    }

    public Taxi anyFree(){
        for (Taxi taxi : taxiQueue){
            if (taxi.isFree()){

                return taxi;
            }
        }
        return addTaxi();
    }

    public LinkedList<ClientT> getClientQueue() {
        return clientQueue;
    }

    public LinkedList<Taxi> getTaxiQueue() {
        return taxiQueue;
    }

    public void setRegionID(int regionID) {
        this.regionID = regionID;
    }

    public void setQueue(LinkedList<ClientT> queue) {
        this.clientQueue = queue;
    }

    public int getRegionID() {
        return regionID;
    }

}
