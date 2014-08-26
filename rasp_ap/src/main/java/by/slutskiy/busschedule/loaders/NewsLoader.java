package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.List;

import by.slutskiy.busschedule.data.DBHelper;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsLoader extends AsyncTaskLoader<List<String>> {

    /**
     * @param context used to retrieve the application context.
     */
    public NewsLoader(Context context) {
        super(context);

    }

    @Override
    public List<String> loadInBackground() {

        DBHelper dbHelper = DBHelper.getInstance(getContext());
        return dbHelper.getNews();
    }

}