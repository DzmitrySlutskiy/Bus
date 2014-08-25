/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * TimeListFragment - show bus schedule for selected route and stop
 *
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class TimeListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Object> {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ROUTE_LIST_ID = "routeListId";
    private static final String ARG_ROUTE_ID = "routeId";
    private static final String ARG_STOP_NAME = "stopName";
    private static final String ARG_ROUTE_DETAIL = "routeDetail";

    private static final int LOADER_TYPE_ID = 1;
    private static final int LOADER_TIME_ID = LOADER_TYPE_ID + 1;

    private int mRouteListId;
    private String mStopName = "";
    private String mRouteDetail = "";

    private int mHour;

    private ListView mTimeListView;
    private View mHeaderView;

    private List<String> mTypeList;

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
        Calendar rightNow = Calendar.getInstance();
        mHour = rightNow.get(Calendar.HOUR_OF_DAY);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = new Bundle();
        args.putInt(TimeListLoader.ATT_ROUT_LIST_ID, mRouteListId);
        getLoaderManager().initLoader(LOADER_TYPE_ID, args, this);

        args = new Bundle();
        args.putInt(TimeListLoader.ATT_ROUT_LIST_ID, mRouteListId);
        getLoaderManager().initLoader(LOADER_TIME_ID, args, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.timelistfragment, container, false);

        TextView tvRouteName = (TextView) view.findViewById(R.id.tvRouteDetailTimelist);
        tvRouteName.setText(getResources().getString(R.string.time_list_route) + "\t\t" + mRouteDetail);

        TextView tvStopDetail = (TextView) view.findViewById(R.id.tvStopDetail);
        tvStopDetail.setText(getResources().getString(R.string.time_list_stop) + "\t\t" + mStopName);

        mTimeListView = (ListView) view.findViewById(R.id.lvTimeList);
        mHeaderView = inflater.inflate(R.layout.timelistitem, (ViewGroup) view.findViewById(R.id.lvTimeList), false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Loader<Object> loader = getLoaderManager().getLoader(LOADER_TYPE_ID);
        if (loader != null) {
            loader.forceLoad();
        }
    }

    /*  Async data loader callback implementation*/
    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return new TimeListLoader(getActivity().getApplicationContext(), args);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

        if (loader.getId() == LOADER_TYPE_ID) {
            initListHeader((List<String>) data);

            //выполнился лоадер для типа времени, запускаем лоадер для отборки расписания
            //т.к. от количества типа времени зависит как будет выводится результат
            //лоадеры запускаются по цепочке, а не вместе
            Loader<Object> timeLoader = getLoaderManager().getLoader(LOADER_TIME_ID);
            if (timeLoader != null) {
                timeLoader.forceLoad();
            }
        } else if (loader.getId() == LOADER_TIME_ID) {
            updateData((List<TimeList>) data);
        }
    }


    @Override
    public void onLoaderReset(Loader<Object> loader) {
    }

    /*  private methods */
    private void initListHeader(List<String> listHeader) {
        if (mTimeListView.getHeaderViewsCount() == 0 && mTimeListView.getAdapter() == null) {
            mTypeList = listHeader;
            LinearLayout layoutMinutes = (LinearLayout) mHeaderView.findViewById(R.id.layoutMinutes);
            for (String minute : mTypeList) {
                layoutMinutes.addView(getTextView(mHeaderView.getContext(), minute));
            }
            mTimeListView.addHeaderView(mHeaderView);
        }
    }

    private TextView getTextView(Context context, String text) {
        TextView tvTemp = new TextView(context);
        LinearLayout.LayoutParams linLayoutParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        int dimPadding = Math.round(getResources().getDimension(R.dimen.d5dp));

        tvTemp.setPadding(dimPadding, dimPadding, dimPadding, dimPadding);
        tvTemp.setGravity(Gravity.CENTER_HORIZONTAL);
        tvTemp.setLayoutParams(linLayoutParam);
        tvTemp.setText(text);

        return tvTemp;
    }

    private void updateData(List<TimeList> timeList) {
        if (mTypeList == null || timeList == null) return;
        // упаковываем данные
        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>(
                timeList.size());
        Map<String, Object> map;
        String ATT_HOUR = "hour";
        String ATT_MIN = "min";
        for (int i = 0; i < timeList.size(); i += mTypeList.size()) {
            TimeList listItem = timeList.get(i);
            map = new HashMap<String, Object>();
            map.put(ATT_HOUR, listItem.getHour());
            String[] minArray = new String[mTypeList.size()];
            for (int j = 0; j < mTypeList.size(); j++) {
                listItem = timeList.get(i + j);
                minArray[j] = listItem.getMinutes();
            }
            map.put(ATT_MIN, minArray);
            data.add(map);
        }
        String[] from = {ATT_HOUR, ATT_MIN};
        int[] to = {R.id.tvHour, R.id.layoutMinutes};

        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), data, R.layout.timelistitem,
                from, to);
        sAdapter.setViewBinder(new TimeListBinder());

        mTimeListView.setAdapter(sAdapter);
    }

    /**
     *
     */
    public interface OnTimeListSelectedListener {
        //public void onTimeListSelected(Uri uri);
    }

    /*  inner class */

    /**
     * fill list items
     */
    private class TimeListBinder implements SimpleAdapter.ViewBinder {

        private boolean useBoldFont = false;

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int id = view.getId();
            if (id == R.id.tvHour) {
                TextView tvTemp = (TextView) view;
                int hourItem = (Integer) data;
                useBoldFont = (hourItem == mHour);
                tvTemp.setText(Integer.toString(hourItem));
            } else if (id == R.id.layoutMinutes) {
                String[] minArray = (String[]) data;
                LinearLayout minPanel = ((LinearLayout) view);

                //если в Layout уже существуют view - значит пользователь прокручивает
                // список в обратном направлении и система подставила уже использованный view
                // (который вышел за пределы видимости экрана при прокрутке) проверяем достаточно
                // ли элементов для вывода минут и добавляем в случае необходимости
                if (minPanel.getChildCount() < minArray.length) {
                    int childNeeded = minArray.length - minPanel.getChildCount();
                    for (int i = 0; i < childNeeded; i++) {
                        minPanel.addView(getTextView(view.getContext(), ""));
                    }
                }
                for (int i = 0; i < minArray.length; i++) {
                    TextView tView = ((TextView) minPanel.getChildAt(i));
                    if (tView != null) {
                        tView.setText(minArray[i]);
                    }
                }
            }
            view.setBackgroundColor(useBoldFont ? getResources().getColor(R.color.currenthour) :
                    getResources().getColor(R.color.not_currenthour));

            return true;
        }
    }

    /**
     * background loader
     * if Id set as LOADER_TYPE_ID task load TypeList
     * if Id set as LOADER_TIME_ID task load TimeList
     * data will return as reference to Object class
     * in onLoadFinished data will cast to self type
     * this method help create universal loader for 2 different task
     */
    static class TimeListLoader extends AsyncTaskLoader<Object> {
        public static final String ATT_ROUT_LIST_ID = "routeListIdLoader";
        private int mRouteListIdLoader;

        /**
         * Stores away the application context associated with context. Since Loaders can be used
         * across multiple activities it's dangerous to store the context directly.
         *
         * @param context used to retrieve the application context.
         */
        public TimeListLoader(Context context, Bundle args) {
            super(context);

            if (args != null) {
                mRouteListIdLoader = args.getInt(ATT_ROUT_LIST_ID);
            }
        }

        @Override
        public Object loadInBackground() {
            DBHelper dbHelper = DBHelper.getInstance(getContext());

            return (getId() == LOADER_TYPE_ID)
                    ? dbHelper.getTypeListByRouteListId(mRouteListIdLoader)
                    : dbHelper.getTimeListByRouteListId(mRouteListIdLoader);
        }
    }
}