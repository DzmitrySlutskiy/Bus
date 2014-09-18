/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.data.entities.BusRoute;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.loaders.BusRouteLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.BusRouteAdapter;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static android.widget.AdapterView.OnItemClickListener;


/*
 * RouteFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class RouteFragment extends Fragment implements OnItemClickListener {

    private static final int LOADER_ID = MainActivity.getNextLoaderId();
    private OnRouteSelectedListener mListener;
    private BusRouteCallback mCallBack = null;

    private ListView mBusList;

    private static RouteFragment sFragment = null;

    /**
     * use this method for create (or get) instance of RouteFragment
     *
     * @return RouteFragment instance
     */
    public static RouteFragment getInstance() {
        if (sFragment == null) {
            sFragment = new RouteFragment();
            sFragment.setRetainInstance(true);
        }

        return sFragment;
    }

    public RouteFragment() {
    }

    /*  public methods   */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, getCallBack());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_route, container, false);

        mBusList = (ListView) fragmentView.findViewById(R.id.list_view_bus);
        clearList();

        return fragmentView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnRouteSelectedListener) {
            mListener = (OnRouteSelectedListener) activity;
        } else {
            mListener = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null) {
            mListener.OnRouteSelected((int) id);
        }
    }


    /*  implementation LoaderManager.LoaderCallbacks<List<String>>*/
    private class BusRouteCallback implements LoaderCallbacks<List<BusRoute>> {

        @Override
        public Loader<List<BusRoute>> onCreateLoader(int id, Bundle args) {
            return new BusRouteLoader(getActivity().getApplicationContext());
        }

        @Override
        public void onLoadFinished(Loader<List<BusRoute>> loader, List<BusRoute> data) {
            updateData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<BusRoute>> loader) {
            if (mBusList != null) {
                mBusList.setOnItemClickListener(null);
            }

            clearList();
        }
    }

    /**
     * instantiate private field for BusRouteCallback class
     *
     * @return BusRouteCallback instance
     */
    private BusRouteCallback getCallBack() {
        if (mCallBack == null) {
            mCallBack = new BusRouteCallback();
        }

        return mCallBack;
    }

    /**
     * clear list with bus route (show "get data" string in list)
     */
    private void clearList() {
        if (mBusList != null) {
            List<String> updateText = new ArrayList<String>();
            updateText.add(getString(R.string.text_view_get_data));
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1, updateText);
            mBusList.setAdapter(adapter);
        }
    }

    /**
     * update list in fragment
     *
     * @param routesList list with route info
     */
    private void updateData(List<BusRoute> routesList) {
        if (mBusList != null && routesList != null) {
            BusRouteAdapter adapter = new BusRouteAdapter(getActivity(), routesList);
            mBusList.setAdapter(adapter);
            mBusList.setOnItemClickListener(this);
        }
    }

    /**
     * interface for interaction with activity
     */

    public interface OnRouteSelectedListener {
        public void OnRouteSelected(int _id);
    }
}
