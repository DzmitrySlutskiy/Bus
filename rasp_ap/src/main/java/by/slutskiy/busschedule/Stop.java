/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule;

import android.support.annotation.NonNull;

/*
 * Stop class encapsulation stop name and stopId in database
 * implements interface Comparable for sorting list
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class Stop implements Comparable {

    /*  private fields  */
    private int mKey;
    private String mStopName;
    /*  public constructors */

    public Stop() {
        mStopName = "";
    }

    /*  public methods  */
    public int getKey() {
        return mKey;
    }

    public void setKey(int key) {
        this.mKey = key;
    }

    public String getStopName() {
        return mStopName;
    }

    public void setStopName(String stopName) {
        this.mStopName = stopName;
    }

    /*   implements Comparable for sorting   */
    @Override
    public int compareTo(@NonNull Object another) {
        return ((another != null) && (another instanceof Stop))
                ? mStopName.compareTo(((Stop) another).getStopName())
                : 0;
    }
}
