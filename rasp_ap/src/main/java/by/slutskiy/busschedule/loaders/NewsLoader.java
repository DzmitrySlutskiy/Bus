package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.sql.SQLException;
import java.util.List;

import by.slutskiy.busschedule.data.OrmDBHelper;
import by.slutskiy.busschedule.data.entities.News;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /**
     * @param context used to retrieve the application context.
     */
    public NewsLoader(Context context) {
        super(context);

    }

    @Override
    public List<News> loadInBackground() {

        OrmDBHelper dbHelper = OrmDBHelper.getReaderInstance(getContext());

        if (dbHelper == null) {
            return null;
        }

        try {
            return dbHelper.getNewsDao().queryForAll();
        } catch (SQLException e) {
            return null;
        }
    }

}
