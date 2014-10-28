package by.slutskiy.busschedule.providers.contracts;

import android.net.Uri;

import java.util.Arrays;
import java.util.HashSet;

import by.slutskiy.busschedule.data.DBStructure;

/**
 * BaseContract
 * Version 1.0
 * 27.10.2014
 * Created by Dzmitry Slutskiy.
 */
public class BaseContract {

    public static final String COLUMN_ID = DBStructure.KEY_ID;

    public static final String AUTHORITY = "by.slutskiy.busschedule.providers.busprovider";

    static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

    BaseContract() {/*   code    */}

    /**
     * Check if the caller has requested a column which does not exists
     *
     * @param projection requested columns
     * @throws IllegalArgumentException if requested column does not exists in current table
     */
    static void checkColumns(String[] available, String[] projection) {
        if (projection != null && available != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));

            if (! availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection! available:" +
                        Arrays.toString(available) + " requested: " + Arrays.toString(projection) + ". See NewsContract!");
            }
        } else {
            throw new IllegalArgumentException("available and projection can't be null");
        }
    }
}
