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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.loaders.TimeListLoader;
import by.slutskiy.busschedule.loaders.TypeListLoader;
import by.slutskiy.busschedule.providers.contracts.TimeListContract;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.TimeAdapter;
import by.slutskiy.busschedule.ui.views.TimeView;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/*
 * TimeListFragment - show bus schedule for selected route and stop
 *
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class TimeListFragment extends BaseFragment {

    public static final String TAG = TimeListFragment.class.getSimpleName();
    public static final String ROUTE_LIST_ID = "routeListId";
    public static final String STOP_NAME = "stopStr";
    public static final String STOP_DETAIL = "stopDetail";

    private static final int LOADER_TYPE_ID = MainActivity.getNextLoaderId();
    private static final int LOADER_TIME_ID = MainActivity.getNextLoaderId();

    private int mRouteListId;
    private String mStopName = "";
    private String mRouteDetail = "";

    private RecyclerView mRecyclerView;
    private View mHeaderView;
    private TimeView mTimeView;
    private TextView mRouteName;
    private TextView mStopDetail;
    private ProgressBar mProgress;

    public TimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void changeArguments(Bundle args) {
        if (args != null) {
            int oldRouteListId = mRouteListId;

            mRouteListId = args.getInt(ROUTE_LIST_ID);
            mStopName = args.getString(STOP_NAME);
            mRouteDetail = args.getString(STOP_DETAIL);

            if (oldRouteListId != mRouteListId) {
                mNeedRestartLoaders = true;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mRouteListId = savedInstanceState.getInt("mRouteListId");
            mStopName = savedInstanceState.getString("mStopName");
            mRouteDetail = savedInstanceState.getString("mRouteDetail");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);

        mRouteName = (TextView) view.findViewById(R.id.text_view_route_detail_time);
        mStopDetail = (TextView) view.findViewById(R.id.text_view_stop_detail);

        updateTextView();

        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        mRecyclerView.setItemAnimator(itemAnimator);

        mHeaderView = view.findViewById(R.id.header);
        mTimeView = (TimeView) mHeaderView.findViewById(R.id.time_view);

        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);

        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (! hidden && mNeedRestartLoaders) {      //if data will be changed after hidden fragment
            updateTextView();
            initLoader();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("mRouteListId", mRouteListId);
        outState.putString("mStopName", mStopName);
        outState.putString("mRouteDetail", mRouteDetail);
    }

    @Override
    protected void initLoader() {
        Bundle args = new Bundle();
        args.putInt(TypeListLoader.ATT_ROUT_LIST_ID, mRouteListId);

        initLoader(args, LOADER_TYPE_ID, getCallBack(), mNeedRestartLoaders);
    }

    @Override
    protected LoaderCallbacks initCallBack() {
        return new CallBackImpl();
    }

    private void updateTextView() {
        mRouteName.setText(getString(R.string.text_view_route) + "\t\t" + mRouteDetail);
        mStopDetail.setText(getString(R.string.text_view_stop) + "\t\t" + mStopName);
    }

    private void initListHeader(Cursor listHeader) {
        List<String> mTypeList = new ArrayList<String>();
        listHeader.moveToFirst();

        int index = listHeader.getColumnIndex(TimeListContract.COLUMN_TYPES);
        String type = listHeader.getString(index);

        String[] result = TextUtils.split(type, UpdateService.TYPE_DELIMITER);
        Collections.addAll(mTypeList, result);

        TimeView timeView = (TimeView) mHeaderView.findViewById(R.id.time_view);
        timeView.setMinList(mTypeList);

        mTimeView.setMinList(mTypeList);
    }

    private void updateData(Cursor timeList) {
        if (timeList == null) {
            return;
        }

        TimeAdapter adapter = new TimeAdapter(timeList, getActivity());
        mRecyclerView.setAdapter(adapter);
        setLoadingProgressState(false);
    }

    /**
     * if state == true show progress bar and hide ListView
     *
     * @param state loading progress state
     */
    private void setLoadingProgressState(boolean state) {
        mRecyclerView.setVisibility(state ? View.GONE : View.VISIBLE);
        mProgress.setVisibility(state ? View.VISIBLE : View.GONE);
        mHeaderView.setVisibility(state ? View.GONE : View.VISIBLE);
    }

    /*  Async data loader callback implementation*/
    private class CallBackImpl implements LoaderCallbacks<Cursor> {
        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            setLoadingProgressState(true);
            if (id == LOADER_TYPE_ID) {
                return new TypeListLoader(getActivity().getApplicationContext(), args);
            } else if (id == LOADER_TIME_ID) {
                return new TimeListLoader(getActivity().getApplicationContext(), args);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (loader.getId() == LOADER_TYPE_ID) {
                    initListHeader(data);

                    //выполнился лоадер для типа времени, запускаем лоадер для отборки расписания
                    //т.к. от количества типа времени зависит как будет выводится результат
                    //лоадеры запускаются по цепочке, а не вместе
                    Bundle args = new Bundle();
                    args.putInt(TimeListLoader.ATT_ROUT_LIST_ID, mRouteListId);

                    initLoader(args, LOADER_TIME_ID, getCallBack(), mNeedRestartLoaders);
                    mNeedRestartLoaders = false;
                } else if (loader.getId() == LOADER_TIME_ID) {
                    updateData(data);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {
            setLoadingProgressState(true);
        }
    }
}