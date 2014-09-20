package by.slutskiy.busschedule.loaders;

import android.content.Context;

/**
 * CacheLoader
 * Version 1.0
 * 20.09.2014
 * Created by Dzmitry Slutskiy.
 */
abstract class CacheLoader<T> extends BaseLoader<T> {

    private T mCacheData = null;

    CacheLoader(Context context) {
        super(context);
    }

    /**
     * Handles a request to start the Loader.
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        if (mCacheData == null) {
            forceLoad();
        } else {
            deliverResult(mCacheData);
        }
    }

    /**
     * set new data to cache
     *
     * @param data data for set
     * @return new data in cache
     */
    protected T setCacheData(T data) {
        return mCacheData = data;
    }
}
