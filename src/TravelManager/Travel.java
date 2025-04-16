package TravelManager;

public class Travel {

    int travelID;

    static int nextTravelID=1;

    int taxiID;

    int regionFrom;

    int regionTo;

    double time;





    public Travel(int taxiID, int regionFrom, int regionTo, double t) {
        travelID = nextTravelID++;
        this.taxiID = taxiID;
        this.regionFrom = regionFrom;
        this.regionTo = regionTo;
        this.time =t;
        //this.time_to_end=tte;
    }

    public int getTravelID() {
        return travelID;
    }

    public void setTravelID(int travelID) {
        travelID = travelID;
    }

    public int getTaxiID() {
        return taxiID;
    }

    public void setTaxiID(int taxiID) {
        this.taxiID = taxiID;
    }

    public int getRegionFrom() {
        return regionFrom;
    }

    public void setRegionFrom(int regionFrom) {
        regionFrom = regionFrom;
    }

    public int getRegionTo() {
        return regionTo;
    }

    public void setRegionTo(int regionTo) {
        regionTo = regionTo;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }
}
