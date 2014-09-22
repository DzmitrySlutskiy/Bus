/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.loaders.TimeListLoader;
import by.slutskiy.busschedule.loaders.TypeListLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.TimeAdapter;

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
    private boolean mNeedRestartLoaders = false;

    private ListView mTimeListView;
    private View mHeaderView;
    private TextView mRouteName;
    private TextView mStopDetail;

    private List<String> mTypeList;

    private CallBackImpl mCallBackImpl = null;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initLoader();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);

        mRouteName = (TextView) view.findViewById(R.id.text_view_route_detail_time);
        mStopDetail = (TextView) view.findViewById(R.id.text_view_stop_detail);

        updateTextView();

        mTimeListView = (ListView) view.findViewById(R.id.list_view_time);
        mHeaderView = inflater.inflate(R.layout.list_item_time, (ViewGroup) view.findViewById(R.id.list_view_time), false);


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

    private void initLoader() {
        Bundle args = new Bundle();
        args.putInt(TypeListLoader.ATT_ROUT_LIST_ID, mRouteListId);

        if (mNeedRestartLoaders) {
            getLoaderManager().restartLoader(LOADER_TYPE_ID, args, getCallBackImpl());
        } else {
            getLoaderManager().initLoader(LOADER_TYPE_ID, args, getCallBackImpl());
        }
    }

    /*  Async data loader callback implementation*/
    private class CallBackImpl implements LoaderCallbacks<List<?>> {
        @Override
        public Loader<List<?>> onCreateLoader(int id, Bundle args) {
            if (id == LOADER_TYPE_ID) {
                return new TypeListLoader(getActivity().getApplicationContext(), args);
            } else if (id == LOADER_TIME_ID) {
                return new TimeListLoader(getActivity().getApplicationContext(), args);
            }
            return null;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onLoadFinished(Loader<List<?>> loader, List<?> data) {
            if (data != null) {
                if (loader.getId() == LOADER_TYPE_ID) {
                    initListHeader((List<String>) data);

                    //выполнился лоадер для типа времени, запускаем лоадер для отборки расписания
                    //т.к. от количества типа времени зависит как будет выводится результат
                    //лоадеры запускаются по цепочке, а не вместе
                    Bundle args = new Bundle();
                    args.putInt(TimeListLoader.ATT_ROUT_LIST_ID, mRouteListId);
                    if (mNeedRestartLoaders) {
                        mNeedRestartLoaders = false;
                        getLoaderManager().restartLoader(LOADER_TIME_ID, args, getCallBackImpl());
                    } else {
                        getLoaderManager().initLoader(LOADER_TIME_ID, args, getCallBackImpl());
                    }
                } else if (loader.getId() == LOADER_TIME_ID) {
                    updateData((List<TimeList>) data);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<List<?>> loader) {
        }
    }

    public static TextView getTextView(Context context, String text) {
        TextView tvTemp = new TextView(context);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);

        Resources resources = context.getApplicationContext().getResources();

        int dimPadding = Math.round(resources.getDimension(R.dimen.text_view_time_list_padding));

        tvTemp.setPadding(dimPadding, dimPadding, dimPadding, dimPadding);
        tvTemp.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTemp.setLayoutParams(linLayoutParam);
        tvTemp.setText(text);

        return tvTemp;
    }

    /*  private methods */
    private CallBackImpl getCallBackImpl() {
        if (mCallBackImpl == null) {
            mCallBackImpl = new CallBackImpl();
        }

        return mCallBackImpl;
    }

    private void updateTextView() {
        mRouteName.setText(getString(R.string.text_view_route) + "\t\t" + mRouteDetail);
        mStopDetail.setText(getString(R.string.text_view_stop) + "\t\t" + mStopName);
    }

    private void initListHeader(List<String> listHeader) {
        //delete old header, if exists
        if (mTimeListView.getHeaderViewsCount() > 0 && mHeaderView != null) {
            mTimeListView.removeHeaderView(mHeaderView);        //remove header from list view
        }

        //need check for adapter state because exception handled
        // java.lang.IllegalStateException: Cannot add header view to
        // list -- setAdapter has already been called.
        if (mTimeListView.getAdapter() != null) {
            mTimeListView.setAdapter(null);                     //delete adapter
        }

        if (mTimeListView.getHeaderViewsCount() == 0 && mTimeListView.getAdapter() == null) {
            mTypeList = listHeader;

            LinearLayout layoutMinutes = (LinearLayout) mHeaderView.findViewById(R.id.layout_minutes);
            layoutMinutes.removeAllViews();
            for (String minute : mTypeList) {
                layoutMinutes.addView(getTextView(mHeaderView.getContext(), minute));
            }
            mTimeListView.addHeaderView(mHeaderView);
        }
    }

    private void updateData(List<TimeList> timeList) {
        if (mTypeList == null || timeList == null) {
            return;
        }

        TimeAdapter adapter = new TimeAdapter(getActivity(), timeList);

        mTimeListView.setAdapter(adapter);
    }
}