/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.TypeList;
import by.slutskiy.busschedule.data.entities.TimeList;
import by.slutskiy.busschedule.loaders.TimeListLoader;
import by.slutskiy.busschedule.loaders.TypeListLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.viewbinders.TimeListBinder;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;

/*
 * TimeListFragment - show bus schedule for selected route and stop
 *
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class TimeListFragment extends Fragment {

    private static final int LOADER_TYPE_ID = MainActivity.getNextLoaderId();
    private static final int LOADER_TIME_ID = MainActivity.getNextLoaderId();

    private static final String ARG_ROUTE_LIST_ID = "routeListId";
    private static final String ARG_ROUTE_ID = "routeId";
    private static final String ARG_STOP_NAME = "stopName";
    private static final String ARG_ROUTE_DETAIL = "routeDetail";

    private int mRouteListId;
    private String mStopName = "";
    private String mRouteDetail = "";

    private ListView mTimeListView;
    private View mHeaderView;

    private List<TypeList> mTypeList;

    private CallBackImpl mCallBackImpl = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param routeListId Parameter 1.
     * @return A new instance of fragment TimeListFragment.
     */
    public static TimeListFragment newInstance(int routeListId, int routeId, String stopStr, String stopDetail) {
        TimeListFragment fragment = new TimeListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ROUTE_LIST_ID, routeListId);
        args.putInt(ARG_ROUTE_ID, routeId);
        args.putString(ARG_STOP_NAME, stopStr);
        args.putString(ARG_ROUTE_DETAIL, stopDetail);
        fragment.setArguments(args);

        return fragment;
    }

    public TimeListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mRouteListId = getArguments().getInt(ARG_ROUTE_LIST_ID);
            mStopName = getArguments().getString(ARG_STOP_NAME);
            mRouteDetail = getArguments().getString(ARG_ROUTE_DETAIL);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = new Bundle();
        args.putInt(TypeListLoader.ATT_ROUT_LIST_ID, mRouteListId);
        getLoaderManager().initLoader(LOADER_TYPE_ID, args, getCallBackImpl());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_time_list, container, false);

        TextView tvRouteName = (TextView) view.findViewById(R.id.text_view_route_detail_time);
        tvRouteName.setText(getString(R.string.text_view_route) + "\t\t" + mRouteDetail);

        TextView tvStopDetail = (TextView) view.findViewById(R.id.text_view_stop_detail);
        tvStopDetail.setText(getString(R.string.text_view_stop) + "\t\t" + mStopName);

        mTimeListView = (ListView) view.findViewById(R.id.list_view_time);
        mHeaderView = inflater.inflate(R.layout.list_item_time, (ViewGroup) view.findViewById(R.id.list_view_time), false);

        return view;
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
                    initListHeader((List<TypeList>) data);

                    //выполнился лоадер для типа времени, запускаем лоадер для отборки расписания
                    //т.к. от количества типа времени зависит как будет выводится результат
                    //лоадеры запускаются по цепочке, а не вместе
                    Bundle args = new Bundle();
                    args.putInt(TimeListLoader.ATT_ROUT_LIST_ID, mRouteListId);
                    getLoaderManager().initLoader(LOADER_TIME_ID, args, getCallBackImpl());
                } else if (loader.getId() == LOADER_TIME_ID) {
                    updateData((List<TimeList>) data);
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<List<?>> loader) {
        }
    }

    private CallBackImpl getCallBackImpl() {
        if (mCallBackImpl == null) {
            mCallBackImpl = new CallBackImpl();
        }

        return mCallBackImpl;
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
    private void initListHeader(List<TypeList> listHeader) {
        if (mTimeListView.getHeaderViewsCount() == 0 && mTimeListView.getAdapter() == null) {
            mTypeList = listHeader;
            LinearLayout layoutMinutes = (LinearLayout) mHeaderView.findViewById(R.id.layout_minutes);
            for (TypeList minute : mTypeList) {
                layoutMinutes.addView(getTextView(mHeaderView.getContext(), minute.getmType()));
            }
            mTimeListView.addHeaderView(mHeaderView);
        }
    }

    private void updateData(List<TimeList> timeList) {
        if (mTypeList == null || timeList == null) {
            return;
        }
        // упаковываем данные
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                timeList.size());
        Map<String, Object> map;
        String ATT_HOUR = "hour";
        String ATT_MIN = "min";
        for (int i = 0; i < timeList.size(); i += mTypeList.size()) {
            TimeList listItem = timeList.get(i);
            map = new HashMap<String, Object>();
            map.put(ATT_HOUR, listItem.getmHour());
            String[] minArray = new String[mTypeList.size()];
            for (int j = 0; j < mTypeList.size(); j++) {
                listItem = timeList.get(i + j);
                minArray[j] = listItem.getmMinutes();
            }
            map.put(ATT_MIN, minArray);
            data.add(map);
        }
        String[] from = {ATT_HOUR, ATT_MIN};
        int[] to = {R.id.text_view_hour, R.id.layout_minutes};

        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), data, R.layout.list_item_time,
                from, to);
        sAdapter.setViewBinder(new TimeListBinder());

        mTimeListView.setAdapter(sAdapter);
    }
}
