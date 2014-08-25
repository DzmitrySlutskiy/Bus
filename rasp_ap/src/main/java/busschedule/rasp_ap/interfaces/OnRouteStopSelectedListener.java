package busschedule.rasp_ap.interfaces;

/**
 * OnRouteStopSelectedListener
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public interface OnRouteStopSelectedListener {
    void OnRouteStopSelected(int _id, int routeId, String stopName, String stopDetail);

    void OnStopSelected(int stopId, String stopName);
}
