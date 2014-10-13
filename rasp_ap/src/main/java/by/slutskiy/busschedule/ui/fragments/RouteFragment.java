/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

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
public class RouteFragment extends BaseFragment implements OnItemClickListener {
    public static final String TAG = RouteFragment.class.getSimpleName();

    private static final int LOADER_ID = MainActivity.getNextLoaderId();

    private ListView mBusList;

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

        mBusList = (ListView) fragmentView.findViewById(R.id.list_view_bus);
        clearList();

        return fragmentView;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mListener != null && mListener instanceof OnRouteSelectedListener) {
            ((OnRouteSelectedListener) mListener).OnRouteSelected((int) id);
        }
    }

    @Override
    protected LoaderCallbacks initCallBack() {
        return new BusRouteCallback();
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
    private void updateData(Cursor routesList) {
        if (mBusList != null && routesList != null) {
            BusRouteAdapter adapter = new BusRouteAdapter(getActivity(), routesList);
            mBusList.setAdapter(adapter);
            mBusList.setOnItemClickListener(this);
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
            if (mBusList != null) {
                mBusList.setOnItemClickListener(null);
            }

            clearList();
        }
    }
}
