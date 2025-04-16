package RegionManager;

public class ClientT {

    int clientID;

    int regionFrom;

    int regionTo;


    public ClientT(int clientID, int regionFrom, int regionTo) {
        this.clientID = clientID;
        this.regionFrom = regionFrom;
        this.regionTo = regionTo;
    }



    public int getClientID() {
        return clientID;
    }

    public void setClientID(int clientID) {
        this.clientID = clientID;
    }

    public int getRegionFrom() {
        return regionFrom;
    }

    public void setRegionFrom(int regionFrom) {
        this.regionFrom = regionFrom;
    }

    public int getRegionTo() {
        return regionTo;
    }

    public void setRegionTo(int regionTo) {
        regionTo = regionTo;
    }
}
