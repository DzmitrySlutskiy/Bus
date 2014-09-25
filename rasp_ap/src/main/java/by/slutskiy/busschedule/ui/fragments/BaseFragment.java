package by.slutskiy.busschedule.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/**
 * BaseFragment
 * Version 1.0
 * 21.09.2014
 * Created by Dzmitry Slutskiy.
 */
public abstract class BaseFragment extends Fragment {

    BaseInteraction mListener;
    boolean mNeedRestartLoaders = false;
    private LoaderCallbacks mCallBack = null;

    BaseFragment() {/*   code    */}

    /**
     * Used for change arguments after creation fragment (for reuse)
     *
     * @param args arguments saved in Bundle
     */
    public abstract void changeArguments(Bundle args);

    /**
     * Every fragment have self CallBack implementation class, and must override this method for
     * get new instance his callback
     *
     * @return new callback instance
     */
    protected abstract LoaderCallbacks initCallBack();

    /**
     * every fragment must initialize self loaders in this method
     */
    protected abstract void initLoader();

    /**
     * initialize or restart loader with specified parameters
     *
     * @param args            arguments saved in Bundle for loader
     * @param loaderId        loader's ID
     * @param loaderCallbacks interface for loader callback
     * @param needRestart     if true loader must restart, otherwise init
     */
    void initLoader(Bundle args, int loaderId,
                    LoaderCallbacks loaderCallbacks, boolean needRestart) {
        if (needRestart) {
            getLoaderManager().restartLoader(loaderId, args, loaderCallbacks);
        } else {
            getLoaderManager().initLoader(loaderId, args, loaderCallbacks);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedlnstanceState) {
        super.onActivityCreated(savedlnstanceState);

        initLoader();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof BaseInteraction) {
            mListener = (BaseInteraction) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    /**
     * lazy init for loader's callback
     *
     * @return loader callback interface
     */
    LoaderCallbacks getCallBack() {
        if (mCallBack == null) {
            mCallBack = initCallBack();
        }

        return mCallBack;
    }

    /**
     * base interface for interaction with Activity, must extends in fragments with self realization
     * used only for save activity as interface in base class BaseFragment (mListener)
     */
    interface BaseInteraction {
    }
}