/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.loaders.NewsLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.NewsAdapter;
import by.slutskiy.busschedule.utils.PreferenceUtils;
import by.slutskiy.busschedule.utils.StringUtils;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/*
 * NewsFragment - show news in list style
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class NewsFragment extends BaseFragment {
    public static final String TAG = NewsFragment.class.getSimpleName();

    private static final int LOADER_ID = MainActivity.getNextLoaderId();

    /*   UI   */
    private TextView mUpdateDate;
    private ProgressBar mProgress;

    private RecyclerView mRecyclerView;

    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void changeArguments(Bundle args) {
        //not needed - no arguments
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_news, container, false);

        mUpdateDate = (TextView) fragmentView.findViewById(R.id.text_view_update_date);
        mProgress = (ProgressBar) fragmentView.findViewById(android.R.id.progress);

        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        setLoadingProgressState(true);

        updateData(null);

        return fragmentView;
    }

    @Override
    protected void initLoader() {
        initLoader(null, LOADER_ID, getCallBack(), false);
    }

    @Override
    protected LoaderCallbacks initCallBack() {
        return new NewsCallback();
    }

    /**
     * Update data in fragment list
     *
     * @param data list with news
     */
    private void updateData(Cursor data) {
        if (data == null) {
            setLoadingProgressState(true);
        } else {
            setLoadingProgressState(false);

            RecyclerView.Adapter mAdapter = new NewsAdapter(data);
            mRecyclerView.setAdapter(mAdapter);
            updateTextView();
        }
    }

    /**
     * Update string in TextView with id R.id.text_view_update_date
     */
    private void updateTextView() {
        String lastUpdateStr = getString(R.string.text_view_update_date);

        Date lastUpdate = PreferenceUtils.getLastUpdateDate(getActivity());

        if (lastUpdate.getTime() > 0) {
            lastUpdateStr += StringUtils.formatDate(lastUpdate);
        }

        mUpdateDate.setText(lastUpdateStr);
    }

    /**
     * if state == true show progress bar and hide ListView
     *
     * @param state loading progress state
     */
    private void setLoadingProgressState(boolean state) {
        mRecyclerView.setVisibility(state ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    /*  Async data loader callback implementation*/
    private class NewsCallback implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {

            return new NewsLoader(getActivity().getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            updateData(data);                                           //update data in list
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            updateData(null);
        }
    }
}
