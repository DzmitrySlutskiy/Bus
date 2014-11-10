/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.loaders.RouteDetailLoader;
import by.slutskiy.busschedule.loaders.StopLoader;
import by.slutskiy.busschedule.providers.contracts.BaseContract;
import by.slutskiy.busschedule.providers.contracts.RouteContract;
import by.slutskiy.busschedule.providers.contracts.StopContract;
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

public class RouteStopFragment extends BaseFragment implements OnItemClickListener {

    public static final String TAG = RouteStopFragment.class.getSimpleName();
    public static final String ROUTE_ID = "mRouteId";
    private static final int LOADER_ID_STOP_DETAIL = MainActivity.getNextLoaderId();
    private static final int LOADER_ID_STOP_LIST = MainActivity.getNextLoaderId();
    private static final String STOP_DETAIL = "mStopDetail";

    private int mRouteId = Integer.MIN_VALUE;

    private Cursor mStopList;
    private String mStopDetail = "";

    private ListView mStopListView;
    private TextView mRouteNameView;
    private ProgressBar mProgress;
    /*  call backs  */
    private StringCallback mStringCallBack = null;

    public RouteStopFragment() {
        mStopList = null;
    }

    @Override
    public void changeArguments(Bundle args) {
        if (args != null) {
            int oldRouteId = mRouteId;
            mRouteId = args.getInt(ROUTE_ID);

            if (oldRouteId != mRouteId && oldRouteId != Integer.MIN_VALUE) {
                mNeedRestartLoaders = true;
            }
        }
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_stop, container, false);

        mRouteNameView = (TextView) view.findViewById(R.id.text_view_route_detail);
        mStopListView = (ListView) view.findViewById(R.id.list_view_stop);
        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);

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
        if (mStopList != null && mStopList.moveToPosition(position)) {

            String stopName = mStopList.getString(mStopList.getColumnIndex(StopContract.COLUMN_STOP_NAME));
            int key = mStopList.getInt(mStopList.getColumnIndex(BaseContract.COLUMN_ID));

            if (mListener != null && mListener instanceof OnRouteStopSelectedListener) {
                OnRouteStopSelectedListener listener = (OnRouteStopSelectedListener) mListener;
                if (mRouteId >= 0) {
                    listener.OnRouteStopSelected(key, stopName, mStopDetail);
                } else {
                    listener.OnStopSelected(key, stopName);
                }
            }
        }
    }

    /**
     * reset UI elements to default values
     */
    private void resetUI() {
        setLoadingProgressState(true);
        mRouteNameView.setText("");
    }

    @Override
    protected void initLoader() {
        Bundle args = null;
        if (mRouteId > 0) {
            args = new Bundle();
            args.putInt(StopLoader.ATT_ROUT_ID, mRouteId);
        }
        initLoader(args, LOADER_ID_STOP_LIST, getCallBack(), mNeedRestartLoaders);

        //если загружаем конкретный маршрут запускаем дополнительный лоадер для инфо по маршруту
        if (mRouteId >= 0) {
            args = new Bundle();
            args.putInt(RouteDetailLoader.ATT_ROUT_ID, mRouteId);

            initLoader(args, LOADER_ID_STOP_DETAIL, getStringCallback(), mNeedRestartLoaders);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (! hidden && mNeedRestartLoaders) {      //if data will be changed after hidden fragment
            resetUI();                              //reset UI because new data load need
            initLoader();                       //restart loaders
            mNeedRestartLoaders = false;            //reset flag
        }
    }

    @Override
    protected LoaderCallbacks initCallBack() {
        return new ListStopCallBack();
    }

    /**
     * update data in list
     *
     * @param data stop list
     */
    private void updateData(Cursor data) {
        if (data != null) {
            mStopList = data;

            CursorAdapter cursorAdapter = (CursorAdapter) mStopListView.getAdapter();
            if (cursorAdapter == null) {
                cursorAdapter = new SimpleCursorAdapter(
                        getActivity(), android.R.layout.simple_list_item_1, data,
                        new String[]{StopContract.COLUMN_STOP_NAME},
                        new int[]{android.R.id.text1}, 0);

                mStopListView.setAdapter(cursorAdapter);
            } else {
                cursorAdapter.changeCursor(data);
            }

            mStopListView.setOnItemClickListener(this);

            setLoadingProgressState(false);
            updateStopDetailText();
        }
    }

    /**
     * if state == true show progress bar and hide ListView
     *
     * @param state loading progress state
     */
    private void setLoadingProgressState(boolean state) {
        mStopListView.setVisibility(state ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    private LoaderCallbacks getStringCallback() {
        if (mStringCallBack == null) {
            mStringCallBack = new StringCallback();
        }

        return mStringCallBack;
    }

    private void updateStopDetailText() {
        if (mRouteId < 0) {
            mRouteNameView.setText(getString(R.string.text_view_stop_list));
        } else {
            mRouteNameView.setText(getString(R.string.text_view_route) + "\t\t" + mStopDetail);
        }
    }

    /**
     * OnRouteStopSelectedListener
     */
    public interface OnRouteStopSelectedListener extends BaseInteraction {
        void OnRouteStopSelected(int _id, String stopName, String stopDetail);

        void OnStopSelected(int stopId, String stopName);
    }

    /*  Async data loader callback implementation
    * for loader return String object*/
    private class StringCallback implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new RouteDetailLoader(getActivity().getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> stringLoader, Cursor data) {
            if (data.moveToFirst()) {
                String busNumber = "";
                String routeName = "";

                int fieldIndex = data.getColumnIndex(RouteContract.COLUMN_BUS);//1
                if (fieldIndex >= 0) {
                    busNumber = data.getString(fieldIndex);
                }

                fieldIndex = data.getColumnIndex(RouteContract.COLUMN_ROUTE_NAME);//2
                if (fieldIndex >= 0) {
                    routeName = data.getString(fieldIndex);
                }

                mStopDetail = busNumber + "   " + routeName;
            }

            updateStopDetailText();
        }

        @Override
        public void onLoaderReset(Loader<Cursor> stringLoader) {
            resetUI();
        }
    }

    /*  Async data loader callback implementation for loader return List<Stop> object*/

    private class ListStopCallBack implements LoaderCallbacks<Cursor> {

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            return new StopLoader(getActivity().getApplicationContext(), bundle);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> listLoader, Cursor data) {
            if (data != null) {
                updateData(data);
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> listLoader) {
            resetUI();
        }
    }
}
