/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.data.entities;

import java.util.ArrayList;
import java.util.List;

/*
 * TimeList
 * time for bus schedule for current {@code mHour}
 * used like mHour = 15 mMinutes = "10 35 57" - in hour = 15 bus will come at 10, 35 and 57 minutes
 *
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class TimeList {

    /*  private fields  */
    private int mHour;
    private List<String> minList = new ArrayList<String>();
    /*  public constructors */

    public TimeList() {
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        this.mHour = hour;
    }

    public int getMinSize() {
        return minList.size();
    }

    public String getMin(int position) {
        if (position >= 0 && position < minList.size()) {
            return minList.get(position);
        }
        return "";
    }

    public void addMin(String min) {
        minList.add(min);
    }

    public List<String> getMin(){
        return minList;
    }
}
