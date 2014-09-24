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
            super.deliverResult(mCacheData);
        }
    }

    @Override
    public void deliverResult(T data) {
        super.deliverResult(data);

        mCacheData = data;
    }
}
