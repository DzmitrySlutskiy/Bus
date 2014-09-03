package by.slutskiy.busschedule.utils;

import android.text.TextUtils;

/**
 * StringUtils
 * Version 1.0
 * 01.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class StringUtils {
    public static final String EMPTY_STRING = "";

    /*  private fields  */

    /*  public constructors */

    private StringUtils() {/*   code    */}

    /*  public methods  */

    /**
     * delete all substring from source string
     *
     * @param source     source string for delete
     * @param subStrings array of substring
     * @return clear text without subStrings substring
     */
    public static String deleteSubString(String source, String... subStrings) {
        for (String item : subStrings) {
            while (source.contains(item)) {
                if (item.length() < source.length()) {
                    int index = source.indexOf(item);
                    if (index == 0) {//start word
                        source = source.substring(item.length(), source.length());
                    } else if ((index > 0) &&
                            ((index + item.length()) <= source.length())) {
                        String startStr = source.substring(0, index);
                        String endStr = source.substring(index + item.length(), source.length());
                        source = startStr + endStr;
                    }
                }
            }
        }
        return source.trim();
    }

    /**
     * split source string to String array (using divider) and get string with specified index
     *
     * @param source  source string
     * @param index   index in result array
     * @param divider substring for split to array
     * @return return String with specified index, if index < 0 or > resultarray.length
     * return empty string
     */
    public static String getSubStringByIndex(String source, int index, String divider) {
        String[] result = TextUtils.split(source, divider);
        if (index > 0 && index < result.length) {
            return result[index];
        } else {
            return EMPTY_STRING;
        }
    }

    /**
     * Convert String str to int value, if parse error return default value defaultInt
     *
     * @param str        string for convert to int
     * @param defaultInt default int value if convert failed
     * @return integer value for str String, or default value defaultInt if str convert failed
     */
    public static int strToInt(String str, int defaultInt) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return defaultInt;
        }
    }

    /**
     * Convert String str to int value, if parse error return 0
     *
     * @param str string for convert
     * @return integer value for str String, or 0 if str convert failed
     */
    public static int strToInt(String str) {
        return strToInt(str, 0);
    }
}
