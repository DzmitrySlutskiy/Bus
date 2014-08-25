/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import busschedule.rasp_ap.Constants;
import busschedule.rasp_ap.R;
import busschedule.rasp_ap.loaders.NewsLoader;

/*
 * NewsFragment - show news in list style
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class NewsFragment extends ListFragment implements
        LoaderManager.LoaderCallbacks<List<String>> {

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

        View fragmentView = inflater.inflate(R.layout.newsfragment, container, false);

        List<String> updateText = new ArrayList<String>();
        updateText.add(getResources().getString(R.string.refresh_data));
        updateData(updateText);

        SharedPreferences preferences = getActivity().getSharedPreferences(
                Constants.DEFAULT_SHARED_PREFS, Context.MODE_PRIVATE);
        String lastUpdateStr = getResources().getString(R.string.newsFrgUpdateStr);
        lastUpdateStr += preferences.getString(Constants.PREF_LAST_UPDATE, Constants.EMPTY_STRING);

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
     * @param data list with news
     */
    private void updateData(List<String> data) {

        String[] newsArray = new String[data.size()];
        for (int i = 0; i < data.size(); i++) {
            newsArray[i] = data.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArray);
        setListAdapter(adapter);
    }
}
