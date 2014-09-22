package by.slutskiy.busschedule.ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * BaseFragment
 * Version 1.0
 * 21.09.2014
 * Created by Dzmitry Slutskiy.
 */
public abstract class BaseFragment extends Fragment {

    BaseFragment() {/*   code    */}

    /**
     * Used for change arguments after creation fragment (for reuse)
     * @param args arguments saved in Bundle
     */
    public abstract void changeArguments(Bundle args);

}
