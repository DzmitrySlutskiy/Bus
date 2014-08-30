package by.slutskiy.busschedule.data.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * News
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "News")
public class News {

    public static final String ID = "_id";
    public static final String NEWS_TEXT = "NewsText";

    /*  private fields  */
    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(columnName = NEWS_TEXT)
    private String mNewsText;

    /*  public constructors */

    public News() {
        this(- 1, "");
    }

    public News(int id, String newsText) {
        mId = id;
        mNewsText = newsText;
    }

    public News(String newsText) {
        this(- 1, newsText);
    }

    /*  public methods  */

    public int getmId() {
        return mId;
    }


    public String getmNewsText() {
        return mNewsText;
    }

    @Override
    public String toString() {
        return mNewsText;
    }
}
