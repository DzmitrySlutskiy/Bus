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
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.Stop;
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

    private static final String ROUTE_ID = "mRouteId";
    private static final String STOP_DETAIL = "mStopDetail";

    private int mRouteId = Integer.MIN_VALUE;
    private boolean mNeedRestartLoaders = false;

    private List<Stop> mStopList;
    private OnRouteStopSelectedListener mListener;
    private String mStopDetail = "";

    private ListView mStopListView;
    private TextView mRouteNameView;

    /*  call backs  */
    private StringCallback mStringCallBack = null;
    private ListStopCallBack mListStopCallBack = null;

    private static RouteStopFragment sFragment = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param routeId Parameter 1.
     * @return A new instance of fragment RouteStopFragment.
     */
    public static RouteStopFragment getInstance(int routeId) {
        if (sFragment == null) {
            sFragment = new RouteStopFragment();
            sFragment.setRetainInstance(true);
        }
        sFragment.setRouteId(routeId);

        return sFragment;
    }

    public RouteStopFragment() {
        mStopList = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRouteId = savedInstanceState.getInt(ROUTE_ID, Integer.MIN_VALUE);
            mStopDetail = savedInstanceState.getString(STOP_DETAIL, "");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoader(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_stop, container, false);

        mRouteNameView = (TextView) view.findViewById(R.id.text_view_route_detail);
        mStopListView = (ListView) view.findViewById(R.id.list_view_stop);

        resetUI();

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ROUTE_ID, mRouteId);
        outState.putString(STOP_DETAIL, mStopDetail);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mStopList != null && position >= 0 && position < mStopList.size()) {
            String stopName = mStopList.get(position).getStopName();
            int key = mStopList.get(position).getKey();

            if (mListener != null) {
                if (mRouteId >= 0) {
                    mListener.OnRouteStopSelected(key, stopName, mStopDetail);
                } else {
                    mListener.OnStopSelected(key, stopName);
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

    /*  private methods */

    /**
     * reset UI elements to default values
     */
    private void resetUI() {
        //clear list and set text "get data"
        String[] newsArr;
        newsArr = new String[1];
        newsArr[0] = getString(R.string.text_view_get_data);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, newsArr);
        mStopListView.setAdapter(adapter);

        //reset text in textView to "get data"
        mRouteNameView.setText(getString(R.string.text_view_get_data));
    }

    private void initLoader(boolean needRestart) {
        Bundle args = new Bundle();
        args.putInt(StopLoader.ATT_ROUT_ID, mRouteId);

        initLoader(args, LOADER_ID_STOP_LIST, getListStopCallBack(), needRestart);

        //если загружаем конкретный маршрут запускаем дополнительный лоадер для инфо по маршруту
        if (mRouteId >= 0) {
            args = new Bundle();
            args.putInt(RouteDetailLoader.ATT_ROUT_ID, mRouteId);

            initLoader(args, LOADER_ID_STOP_DETAIL, getStringCallback(), needRestart);
        }
    }

    private void initLoader(Bundle args, int loaderId,
                            LoaderCallbacks loaderCallbacks, boolean needRestart) {
        if (needRestart) {
            getLoaderManager().restartLoader(loaderId, args, loaderCallbacks);
        } else {
            getLoaderManager().initLoader(loaderId, args, loaderCallbacks);
        }
    }

    private void setRouteId(int newId) {
        int oldRouteId = mRouteId;
        mRouteId = newId;

        //at this point fragment may be not attached to activity.
        //set flag to true - it's indicate what routeId was change and need restart data
        //loaders will be restarted when onHiddenChanged call
        //oldRouteId != Integer.MIN_VALUE = true if not first fragment use
        if (oldRouteId != newId && oldRouteId != Integer.MIN_VALUE) {
            mNeedRestartLoaders = true;
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (! hidden && mNeedRestartLoaders) {      //if data will be changed after hidden fragment
            resetUI();                              //reset UI because new data load need
            initLoader(true);                       //restart loaders
            mNeedRestartLoaders = false;            //reset flag
        }
    }

    /**
     * update data in list
     *
     * @param data stop list
     */
    private void updateData(List<Stop> data) {
        if (data != null) {
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

            updateStopDetailText();
        }
    }

    /**
     * OnRouteStopSelectedListener
     */
    public interface OnRouteStopSelectedListener {
        void OnRouteStopSelected(int _id, String stopName, String stopDetail);

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

    private void updateStopDetailText() {
        if (mRouteId < 0) {
            mRouteNameView.setText(getString(R.string.text_view_stop_list));
        } else {
            mRouteNameView.setText(getString(R.string.text_view_route) + "\t\t" + mStopDetail);
        }
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
            updateStopDetailText();
        }

        @Override
        public void onLoaderReset(Loader<String> stringLoader) {
        }
    }

    /*  Async data loader callback implementation for loader return List<Stop> object*/

    private class ListStopCallBack implements LoaderCallbacks<List<Stop>> {

        @Override
        public Loader<List<Stop>> onCreateLoader(int i, Bundle bundle) {
            return new StopLoader(getActivity().getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(Loader<List<Stop>> listLoader, List<Stop> data) {
            if (data != null) {
                updateData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<Stop>> listLoader) {
        }
    }
}
