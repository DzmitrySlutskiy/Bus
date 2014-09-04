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

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.BuildConfig;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.loaders.NewsLoader;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/*
 * NewsFragment - show news in list style
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class NewsFragment extends ListFragment implements LoaderCallbacks<List<String>> {

    private static final int LOADER_ID = 1;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment NewsFragment.
     */
    public static NewsFragment newInstance() {
        return new NewsFragment();
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

        List<String> updateText = new ArrayList<String>();
        updateText.add(getString(R.string.text_view_get_data));
        updateData(updateText);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                BuildConfig.PACKAGE_NAME, Context.MODE_PRIVATE);
        String lastUpdateStr = getString(R.string.text_view_update_date);

        Timestamp lastUpdate = new Timestamp(
                preferences.getLong(UpdateService.PREF_LAST_UPDATE, 0));

        if (lastUpdate.getTime() > 0) {
            lastUpdateStr += new SimpleDateFormat(UpdateService.USED_DATE_FORMAT).format(lastUpdate);
        }

        TextView tvUpdateDate = (TextView) fragmentView.findViewById(R.id.tvUpdateDate);
        tvUpdateDate.setText(lastUpdateStr);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        getLoaderManager().getLoader(LOADER_ID).forceLoad();        //begin load data from DB
    }

    @Override
    public void onActivityCreated(Bundle savedlnstanceState) {
        super.onActivityCreated(savedlnstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    /*  Async data loader callback implementation*/
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
    }

    /**
     * Update data in fragment list
     *
     * @param data list with news
     */
    private void updateData(List<String> data) {
        if (data != null) {
            String[] newsArray = new String[data.size()];
            for (int i = 0; i < data.size(); i++) {
                newsArray[i] = data.get(i);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, newsArray);
            setListAdapter(adapter);
        }
    }
}
