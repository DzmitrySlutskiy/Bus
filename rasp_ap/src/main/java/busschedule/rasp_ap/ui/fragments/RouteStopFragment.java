/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import busschedule.rasp_ap.R;
import busschedule.rasp_ap.Stop;
import busschedule.rasp_ap.loaders.StopLoader;
import busschedule.rasp_ap.interfaces.OnRouteStopSelectedListener;

/*
 * RouteStopFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class RouteStopFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<Object> {

    public static final int LOADER_ID_STOP_DETAIL = 1;
    private static final int LOADER_ID_STOP_LIST = LOADER_ID_STOP_DETAIL + 1;

    private static final String ARG_ROUTE_ID = "routeId";

    private static final String PREF_NAME = "RouteStopFragment";
    private static final String SAVE_SCROLL_POS = "scrollPos";

    private int mRouteId;
    private List<Stop> mStopList;
    private OnRouteStopSelectedListener mListener;
    private String mStopDetail = "";

    private ListView mStopListView;
    private TextView mRouteNameView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param routeId Parameter 1.
     * @return A new instance of fragment RouteStopFragment.
     */
    public static RouteStopFragment newInstance(int routeId) {
        RouteStopFragment fragment = new RouteStopFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUTE_ID, routeId);
        fragment.setArguments(args);

        return fragment;
    }

    public RouteStopFragment() {
        mStopList = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args != null) {
            mRouteId = args.getInt(ARG_ROUTE_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = new Bundle();
        args.putInt(StopLoader.ATT_ROUT_ID, mRouteId);
        getLoaderManager().initLoader(LOADER_ID_STOP_LIST, args, this);

        //если загружаем конкретный маршрут запускаем дополнительный лоадер для инфо по маршруту
        if (mRouteId >= 0) {
            args = new Bundle();
            args.putInt(StopLoader.ATT_ROUT_ID, mRouteId);
            getLoaderManager().initLoader(LOADER_ID_STOP_DETAIL, args, this);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        Loader<Object> loader = getLoaderManager().getLoader(LOADER_ID_STOP_DETAIL);
        if (loader != null) {
            loader.forceLoad();
        }

        loader = getLoaderManager().getLoader(LOADER_ID_STOP_LIST);
        if (loader != null) {
            loader.forceLoad();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.routestopfragment, container, false);

        mRouteNameView = (TextView) view.findViewById(R.id.tvRouteDetail);
        mStopListView = (ListView) view.findViewById(R.id.lvStopList);

        String[] newsArr;
        newsArr = new String[1];
        newsArr[0] = getResources().getString(R.string.refresh_data);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArr);
        mStopListView.setAdapter(adapter);
        if (mRouteId < 0) {
            mRouteNameView.setText(getResources().getString(R.string.full_stop_list));
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mStopList != null && position >= 0 && position < mStopList.size()) {
            if (mRouteId >= 0) {
                int routeListId = mStopList.get(position).getKey();
                String stopName = mStopList.get(position).getStopName();
                if (mListener != null && routeListId >= 0)
                    mListener.OnRouteStopSelected(routeListId, mRouteId, stopName, mStopDetail);
            } else {
                int stopId = mStopList.get(position).getKey();
                String stopName = mStopList.get(position).getStopName();
                if (mListener != null) {
                    mListener.OnStopSelected(stopId, stopName);
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnRouteStopSelectedListener) {
            mListener = (OnRouteStopSelectedListener) activity;
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
    public void onDestroy() {
        super.onDestroy();

        //set position to 0
        saveListPosition(0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mRouteId < 0) {
            saveListPosition(mStopListView.getFirstVisiblePosition());
        }
    }

    /*  Async data loader callback implementation*/
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new StopLoader(getActivity().getApplicationContext(), args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {
        if (data instanceof String) {
            mStopDetail = (String) data;
            mRouteNameView.setText(getResources().getString(R.string.time_list_route) + "\t\t" + mStopDetail);
        } else if (data instanceof List<?>) {
            updateData((List<Stop>) data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {
    }

    /*  private methods */

    /**
     * save list position in shared preference (if user return back, him show last position in list)
     *
     * @param position position in list
     */
    private void saveListPosition(int position) {
        SharedPreferences.Editor editor = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit();
        editor.putInt(SAVE_SCROLL_POS, position);
        editor.commit();
    }

    /**
     * restore saved scroll position
     *
     * @return int position
     */
    private int restoreListPosition() {
        SharedPreferences prefs = getActivity().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        return prefs.getInt(SAVE_SCROLL_POS, 0);
    }

    /**
     * update data in list
     *
     * @param data stop list
     */
    private void updateData(List<Stop> data) {
        String[] newsArr;

        mStopList = data;

        /* if mRoute< 0 this fragment show all stop list*/
        if (mRouteId < 0) {
            Collections.sort(mStopList);
        }

        newsArr = new String[mStopList.size()];
        for (int i = 0; i < mStopList.size(); i++) {
            newsArr[i] = mStopList.get(i).getStopName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArr);
        mStopListView.setAdapter(adapter);
        mStopListView.setOnItemClickListener(this);
        if (mRouteId < 0) {
            mStopListView.setSelection(restoreListPosition());
        }
    }
}
