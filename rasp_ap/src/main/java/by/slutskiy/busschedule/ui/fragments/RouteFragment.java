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

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.loaders.BusRouteLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.RouteAdapter;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;


/*
 * RouteFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class RouteFragment extends BaseFragment implements RouteAdapter.onItemClickListener {
    public static final String TAG = RouteFragment.class.getSimpleName();

    private static final int LOADER_ID = MainActivity.getNextLoaderId();

    private RecyclerView mRecyclerView;
    private ProgressBar mProgress;

    public RouteFragment() {
    }

    @Override
    public void changeArguments(Bundle args) {
        //not needed - no arguments
    }

    @Override
    protected void initLoader() {
        initLoader(null, LOADER_ID, getCallBack(), false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_route, container, false);

        mProgress = (ProgressBar) fragmentView.findViewById(android.R.id.progress);
        mRecyclerView = (RecyclerView) fragmentView.findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        setLoadingProgressState(true);

        return fragmentView;
    }

    @Override
    protected LoaderCallbacks initCallBack() {
        return new BusRouteCallback();
    }

    /**
     * update list in fragment
     *
     * @param routesList list with route info
     */
    private void updateData(Cursor routesList) {
        if (mRecyclerView != null && routesList != null) {

            RouteAdapter adapter = new RouteAdapter(routesList, this);
            mRecyclerView.setAdapter(adapter);

            setLoadingProgressState(false);
        } else {
            setLoadingProgressState(true);
        }
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

    @Override
    public void onClick(long id) {
        if (mListener != null && mListener instanceof OnRouteSelectedListener) {
            ((OnRouteSelectedListener) mListener).OnRouteSelected((int) id);
        }
    }

    /**
     * interface for interaction with activity
     */

    public interface OnRouteSelectedListener extends BaseInteraction {
        public void OnRouteSelected(int _id);
    }

    /*  implementation LoaderManager.LoaderCallbacks<List<String>>*/
    private class BusRouteCallback implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            return new BusRouteLoader(getActivity().getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            updateData(data);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            setLoadingProgressState(true);
        }
    }
}
