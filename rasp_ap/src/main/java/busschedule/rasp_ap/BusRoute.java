/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

/*
 * BusRoute - define info about bus route: route begin and end stop name, bus number.
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

class BusRoute {

    /**
     * id route in database
     */
    private long mRouteId;

    /**
     * bus number
     */
    private String mBusNumber;

    /**
     * begin stop route
     */
    private String mBeginStop;

    /**
     * end stop route
     */
    private String mEndStop;


    public BusRoute() {/*   code    */}

    public long getRouteId() {
        return mRouteId;
    }

    public void setRouteId(long routeId) {
        this.mRouteId = routeId;
    }

    public String getBusNumber() {
        return mBusNumber;
    }

    public void setBusNumber(String busNumber) {
        this.mBusNumber = busNumber;
    }

    public String getBeginStop() {
        return mBeginStop;
    }

    public void setBeginStop(String beginStop) {
        this.mBeginStop = beginStop;
    }

    public String getEndStop() {
        return mEndStop;
    }

    public void setEndStop(String endStop) {
        this.mEndStop = endStop;
    }

    @Override
    public String toString() {
        return mBusNumber + "\t" + mBeginStop + " - " + mEndStop;
    }
}
