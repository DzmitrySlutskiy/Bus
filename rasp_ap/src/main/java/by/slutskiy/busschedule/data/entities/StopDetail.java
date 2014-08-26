/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.data.entities;

import java.util.ArrayList;
import java.util.List;

/**
 * detail information about stop
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class StopDetail {

    /*  private fields  */
    private int mRouteListId;
    private int mRouteId;
    private String mRouteName;
    private final List<String> mMinuteList;

    /*  public constructors */

    public StopDetail() {
        mMinuteList = new ArrayList<String>();
    }

    /*  public methods  */
    public int getRouteListId() {
        return mRouteListId;
    }

    public void setRouteListId(int routeListId) {
        this.mRouteListId = routeListId;
    }

    public int getRouteId() {
        return mRouteId;
    }

    public void setRouteId(int routeId) {
        this.mRouteId = routeId;
    }

    public String getRouteName() {
        return mRouteName;
    }

    public void setRouteName(String routeName) {
        this.mRouteName = routeName;
    }

    public List<String> getMinuteList() {
        return mMinuteList;
    }

    public void addMinute(String min) {
        mMinuteList.add(min);
    }

}
