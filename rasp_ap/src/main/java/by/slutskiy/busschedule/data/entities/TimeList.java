package by.slutskiy.busschedule.data.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TimeList
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "TimeList")
public class TimeList {

    public static final String ID = "_id";
    public static final String ROUTE_LIST_ID = "RouteListId";
    public static final String HOUR = "Hour";
    public static final String MINUTES = "Minutes";
    public static final String DAY_TYPE_ID = "DayTypeId";


    /*  private fields  */

    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ROUTE_LIST_ID, index = true)
    private RouteList mRouteList;

    @DatabaseField(columnName = HOUR)
    private int mHour;

    @DatabaseField(columnName = MINUTES)
    private String mMinutes;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = DAY_TYPE_ID)
    private TypeList mDayType;

    /*  public constructors */

    public TimeList() {
        mId = - 1;
    }

    /*  public methods  */

    public int getmId() {
        return mId;
    }

    public void setmRouteList(RouteList mRouteList) {
        this.mRouteList = mRouteList;
    }

    public int getmHour() {
        return mHour;
    }

    public void setmHour(int mHour) {
        this.mHour = mHour;
    }

    public String getmMinutes() {
        return mMinutes;
    }

    public void setmMinutes(String mMinutes) {
        this.mMinutes = mMinutes;
    }

    public TypeList getmDayType() {
        return mDayType;
    }

    public void setmDayType(TypeList mDayType) {
        this.mDayType = mDayType;
    }

    @Override
    public String toString() {
        return "" + mHour + " : " + mMinutes;
    }
}
