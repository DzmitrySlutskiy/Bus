/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.loaders.NewsLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/*
 * NewsFragment - show news in list style
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class NewsFragment extends ListFragment {

    private static final int LOADER_ID = MainActivity.getNextLoaderId();
    private NewsCallback mCallBack = null;

    private static NewsFragment sFragment = null;

    /*   UI   */
    private TextView mUpdateDate;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment getInstance() {
        if (sFragment == null) {
            sFragment = new NewsFragment();
            //sad activity to save this instance when configuration changed
            sFragment.setRetainInstance(true);
        }

        return sFragment;
    }

    public NewsFragment() {
        // Required empty public constructor
    }

    /*  public methods */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragmentView = inflater.inflate(R.layout.fragment_news, container, false);
        mUpdateDate = (TextView) fragmentView.findViewById(R.id.text_view_update_date);

        updateData(null);

        return fragmentView;
    }

    @Override
    public void onActivityCreated(Bundle savedlnstanceState) {
        super.onActivityCreated(savedlnstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, getCallBack());
    }

    /*  Async data loader callback implementation*/
    private class NewsCallback implements LoaderCallbacks<List<String>> {

        @Override
        public Loader<List<String>> onCreateLoader(int id, Bundle args) {
            return new NewsLoader(getActivity().getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
            updateData(data);                                           //update data in list
        }

        @Override
        public void onLoaderReset(Loader<List<String>> loader) {
            updateData(null);
        }
    }


    private NewsCallback getCallBack() {
        if (mCallBack == null) {
            mCallBack = new NewsCallback();
        }

        return mCallBack;
    }

    /**
     * Update data in fragment list
     *
     * @param data list with news
     */
    private void updateData(List<String> data) {
        String[] newsArray;
        if (data == null) {
            newsArray = new String[]{getString(R.string.text_view_get_data)};
        } else {
            newsArray = data.toArray(new String[data.size()]);
        }

        updateTextView();

        setListAdapter(new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArray));
    }

    /**
     * Update string in TextView with id R.id.text_view_update_date
     */
    private void updateTextView() {
        SharedPreferences preferences = getActivity().getSharedPreferences(
                BuildConfig.PACKAGE_NAME, Context.MODE_PRIVATE);
        String lastUpdateStr = getString(R.string.text_view_update_date);

        Date lastUpdate = new Date(
                preferences.getLong(UpdateService.PREF_LAST_UPDATE, 0));

        if (lastUpdate.getTime() > 0) {
            lastUpdateStr += new SimpleDateFormat(UpdateService.USED_DATE_FORMAT).format(lastUpdate);
        }

        mUpdateDate.setText(lastUpdateStr);
    }
}
