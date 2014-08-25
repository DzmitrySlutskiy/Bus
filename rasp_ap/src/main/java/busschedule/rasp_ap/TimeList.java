/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

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
class TimeList {

    /*  private fields  */
    private int mHour;
    private String mMinutes;

    /*  public constructors */

    public TimeList() {
    }

    public int getHour() {
        return mHour;
    }

    public void setHour(int hour) {
        this.mHour = hour;
    }

    public String getMinutes() {
        return mMinutes;
    }

    public void setMinutes(String minutes) {
        this.mMinutes = minutes;
    }
    /*  private methods */

}
