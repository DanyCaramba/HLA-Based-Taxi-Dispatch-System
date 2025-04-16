package RegionManager;

import INIT.Variables;

import java.util.LinkedList;

public class RegionManager {
    private static RegionManager instance = null;
    public Region region;
    public LinkedList<Region> regions;
    public RegionManager() {
        this.regions = new LinkedList<>();
        this.createRegions();
    }


    static public RegionManager getInstance()
    {
        if(instance==null) instance = new RegionManager();
        return instance;
    }

    public void createRegions(){

        for(int i = 0; i< Variables.l; i++)
        {
            region = new Region();
            regions.add(region);
            System.out.println("ID regionu: "+region.regionID);
        }
    }


    public void updateTaxiStatusAndMove(LinkedList<Region> regions, int taxiId, int newRegionId) {
        Taxi movingTaxi = null;
        Region oldRegion = null;

        for (Region region : regions) {
            for (Taxi taxi : region.getTaxiQueue()) {
                if (taxi.getTaxiID() == taxiId) {
                    movingTaxi = taxi;
                    oldRegion = region;
                    break;
                }
            }
            if (movingTaxi != null) {
                region.getTaxiQueue().remove(movingTaxi);
                break;
            }
        }


        if (movingTaxi != null && oldRegion != null)
        {
                    movingTaxi.isFree=true;
                    for (Region region : regions) {
                        if (region.regionID == newRegionId) {
                            region.taxiQueue.add(movingTaxi);
                            return;
                        }
                    }
        }
        else {
            System.out.println("Nie znaleziono taksówki o ID: " + taxiId + " w żadnym regionie");
        }
    }







}

