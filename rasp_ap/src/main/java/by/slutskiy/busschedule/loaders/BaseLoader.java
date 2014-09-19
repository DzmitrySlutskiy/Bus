package by.slutskiy.busschedule.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import java.util.Observable;
import java.util.Observer;

import by.slutskiy.busschedule.services.UpdateService;

/**
 * BaseLoader implements Observer interface
 * Version 1.0
 * 19.09.2014
 * Created by Dzmitry Slutskiy.
 */
abstract class BaseLoader<T> extends AsyncTaskLoader<T> implements Observer {

    BaseLoader(Context context) {
        super(context);
        UpdateService.addObserver(this);
    }

    /**
     * This method is called if the specified {@code Observable} object's
     * {@code notifyObservers} method is called (because the {@code Observable}
     * object has been updated.
     *
     * @param observable the {@link java.util.Observable} object.
     * @param data       the data passed to {@link java.util.Observable#notifyObservers(Object)}.
     */
    @Override
    public void update(Observable observable, Object data) {
        forceLoad();                //start a load.
    }

    @Override
    protected void onReset() {
        super.onReset();
        UpdateService.deleteObserver(this);
    }
}
