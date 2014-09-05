/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.StopList;
import by.slutskiy.busschedule.loaders.RouteDetailLoader;
import by.slutskiy.busschedule.loaders.StopLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static android.widget.AdapterView.OnItemClickListener;

/*
 * RouteStopFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class RouteStopFragment extends Fragment implements OnItemClickListener {

    private static final int LOADER_ID_STOP_DETAIL = MainActivity.getNextLoaderId();
    private static final int LOADER_ID_STOP_LIST = MainActivity.getNextLoaderId();

    private static final String ARG_ROUTE_ID = "routeId";

    private static final String PREF_NAME = "RouteStopFragment";
    private static final String SAVE_SCROLL_POS = "scrollPos";

    private int mRouteId;
    private List<StopList> mStopList;
    private OnRouteStopSelectedListener mListener;
    private String mStopDetail = "";

    private ListView mStopListView;
    private TextView mRouteNameView;

    /*  call backs  */
    private StringCallback mStringCallBack = null;
    private ListStopCallBack mListStopCallBack = null;

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
        getLoaderManager().initLoader(LOADER_ID_STOP_LIST, args, getListStopCallBack());

        //если загружаем конкретный маршрут запускаем дополнительный лоадер для инфо по маршруту
        if (mRouteId >= 0) {
            args = new Bundle();
            args.putInt(RouteDetailLoader.ATT_ROUT_ID, mRouteId);
            getLoaderManager().initLoader(LOADER_ID_STOP_DETAIL, args, getStringCallback());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_stop, container, false);

        mRouteNameView = (TextView) view.findViewById(R.id.text_view_route_detail);
        mStopListView = (ListView) view.findViewById(R.id.list_view_stop);

        String[] newsArr;
        newsArr = new String[1];
        newsArr[0] = getString(R.string.text_view_get_data);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArr);
        mStopListView.setAdapter(adapter);
        if (mRouteId < 0) {
            mRouteNameView.setText(getString(R.string.text_view_stop_list));
        }

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mStopList != null && position >= 0 && position < mStopList.size()) {
            if (mRouteId >= 0) {
                int routeListId = mStopList.get(position).getmId();
                String stopName = mStopList.get(position).getmStopName();
                if (mListener != null && routeListId >= 0)
                    mListener.OnRouteStopSelected(routeListId, mRouteId, stopName, mStopDetail);
            } else {
                int stopId = mStopList.get(position).getmId();
                String stopName = mStopList.get(position).getmStopName();
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

    private void updateData(List<StopList> data) {
        String[] newsArr;

        if (data != null) {
            mStopList = data;

            /* if mRoute< 0 this fragment show all stop list*/
            if (mRouteId < 0) {
                Collections.sort(mStopList);
            }
            newsArr = new String[mStopList.size()];
            for (int i = 0; i < mStopList.size(); i++) {
                newsArr[i] = mStopList.get(i).getmStopName();
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

    /**
     * OnRouteStopSelectedListener
     */
    public interface OnRouteStopSelectedListener {
        void OnRouteStopSelected(int _id, int routeId, String stopName, String stopDetail);

        void OnStopSelected(int stopId, String stopName);
    }


    private StringCallback getStringCallback() {
        if (mStringCallBack == null) {
            mStringCallBack = new StringCallback();
        }

        return mStringCallBack;
    }

    private ListStopCallBack getListStopCallBack() {
        if (mListStopCallBack == null) {
            mListStopCallBack = new ListStopCallBack();
        }

        return mListStopCallBack;
    }

    /*  Async data loader callback implementation
    * for loader return String object*/
    private class StringCallback implements LoaderCallbacks<String> {
        @Override
        public Loader<String> onCreateLoader(int i, Bundle bundle) {
            return new RouteDetailLoader(getActivity().getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(Loader<String> stringLoader, String data) {
            mStopDetail = data;
            mRouteNameView.setText(getString(R.string.text_view_route) + "\t\t" + mStopDetail);
        }

        @Override
        public void onLoaderReset(Loader<String> stringLoader) {
        }
    }

    /*  Async data loader callback implementation for loader return List<Stop> object*/
    private class ListStopCallBack implements LoaderCallbacks<List<StopList>> {

        @Override
        public Loader<List<StopList>> onCreateLoader(int i, Bundle bundle) {
            return new StopLoader(getActivity().getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(Loader<List<StopList>> listLoader, List<StopList> data) {
            if (data != null) {
                updateData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<StopList>> listLoader) {
        }
    }
}
