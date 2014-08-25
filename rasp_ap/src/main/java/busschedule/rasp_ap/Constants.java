/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

/*
 * Constants - application constants.
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
class Constants {

    /*  preferences params  */
    public static final String DEFAULT_SHARED_PREFS = "BusSchedule.pref";
    public static final String PREF_LAST_UPDATE = "lastUpdate";

    public static final boolean DEBUG = false;

    /*   Log tags   */
    public static final String LOG_TAG = "BusSchedule";
    public static final String LOG_TAG_UPD = "BusScheduleUpd";

    /*   constants for update    */
    public static final String BUS_PARK_URL = "http://www.ap1.by/download/";
    public static final String FILE_NAME = "raspisanie_gorod.xls";

    /*   Message type definition for Handler
       * using for send message to main thread */
    public static final int MSG_START_PROGRESS = 0;
    public static final int MSG_UPDATE_PROGRESS = 1;
    public static final int MSG_UPDATE_CANCELED = 2;
    public static final int MSG_UPDATE_FILE_SIZE = 3;
    public static final int MSG_END_PROGRESS = 10;
    public static final int MSG_NO_INTERNET = 11;
    public static final int MSG_UPDATE_TEXT = 12;
    public static final int MSG_IO_ERROR = 13;
    public static final int MSG_UPDATE_FILE_STRUCTURE_ERROR = 20;
    public static final int MSG_UPDATE_DB_WORK_ERROR = 21;
    public static final int MSG_UPDATE_BIFF_ERROR = 22;
    public static final int MSG_APP_ERROR = 23;

    public static final int MAX_PERCENT = 100;

    public static final String EMPTY_STRING = "";
}
