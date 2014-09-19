package by.slutskiy.busschedule.loaders;

import android.content.Context;

import java.util.List;
import java.util.Observer;

import by.slutskiy.busschedule.data.DBReader;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsLoader extends BaseLoader<List<String>> implements Observer {

    private List<String> mNews = null;

    /**
     * @param context used to retrieve the application context.
     */
    public NewsLoader(Context context) {
        super(context);
    }

    @Override
    public List<String> loadInBackground() {
        DBReader dbReader = DBReader.getInstance(getContext());

        return mNews = dbReader.getNews();
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mNews == null) {
            forceLoad();
        } else {
            deliverResult(mNews);
        }
    }
}
