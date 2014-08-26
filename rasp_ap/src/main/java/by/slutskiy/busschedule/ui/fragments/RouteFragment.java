/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.slutskiy.busschedule.BusRoute;
import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.interfaces.OnRouteSelectedListener;
import by.slutskiy.busschedule.loaders.BusRouteLoader;
import by.slutskiy.busschedule.viewbinders.BusRouteBinder;


/*
 * RouteFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class RouteFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<BusRoute>> {

    private static final int LOADER_ID = 1;
    private OnRouteSelectedListener mListener;

    private ListView mBusList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment RouteFragment.
     */
    public static RouteFragment newInstance() {
        return new RouteFragment();
    }

    public RouteFragment() {
    }

    /*  public methods   */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().getLoader(LOADER_ID).forceLoad();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.routefragment, container, false);

        mBusList = (ListView) fragmentView.findViewById(R.id.lvBusList);
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
        View itemView = view.findViewById(R.id.itemlayout);
        if (itemView != null) {
            String tag = (String) itemView.getTag();
            if (mListener != null) {
                mListener.OnRouteSelected(Integer.parseInt(tag));
            }
        }
    }



    /*  implementation LoaderManager.LoaderCallbacks<List<String>>*/
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
        if (mBusList != null) mBusList.setOnItemClickListener(null);
        clearList();
    }

    /**
     * clear list with bus route (show "get data" string in list)
     */
    private void clearList() {
        if (mBusList != null) {
            List<String> updateText = new ArrayList<String>();
            updateText.add(getResources().getString(R.string.refresh_data));
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
        // упаковываем данные
        String attBus = "bus";
        String attBeginStop = "begin_stop";
        String attEndStop = "end_stop";
        String attId = "_id";

        ArrayList<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>(
                routesList.size());
        Map<String, Object> map;
        for (BusRoute bRoute : routesList) {
            map = new HashMap<String, Object>();
            map.put(attBus, bRoute.getBusNumber());
            map.put(attBeginStop, bRoute.getBeginStop());
            map.put(attEndStop, bRoute.getEndStop());
            map.put(attId, Long.toString(bRoute.getRouteId()));
            dataList.add(map);
        }

        String[] from = {attBus, attBeginStop,
                attEndStop, attId};
        int[] to = {R.id.tvBusNumber, R.id.tvBeginStop, R.id.tvEndStop, R.id.itemlayout};
        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), dataList, R.layout.routeitem,
                from, to);

        sAdapter.setViewBinder(new BusRouteBinder());

        if (mBusList != null) {
            mBusList.setAdapter(sAdapter);
            mBusList.setOnItemClickListener(this);
        }
    }
}
