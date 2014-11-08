package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.CursorLoader;

import by.slutskiy.busschedule.providers.contracts.NewsContract;

/**
 * background data loader
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsLoader extends CursorLoader {

    /**
     * @param context used to retrieve the application context.
     */
    public NewsLoader(Context context) {
        super(context, NewsContract.CONTENT_URI,
                NewsContract.availableColumns, null, null, null);
    }
}
