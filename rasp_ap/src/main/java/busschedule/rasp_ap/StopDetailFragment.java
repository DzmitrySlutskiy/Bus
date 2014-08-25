/*
 * Bus schedule for Grodno
 */

package busschedule.rasp_ap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 * StopDetailFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class StopDetailFragment extends Fragment implements AdapterView.OnItemClickListener, LoaderManager.LoaderCallbacks<List<StopDetail>> {
    // the fragment initialization parameters
    private static final String ARG_STOP_ID = "stopId";
    private static final String ARG_STOP_NAME = "stopName";
    private static final int LOADER_ID = 1;

    private List<StopDetail> mStopDetList = null;
    private int mStopId;
    private String mStopName;
    private int mCurrentHour;

    private OnStopDetailListener mListener;

    private ListView mDetailList;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stopId   Parameter 1.
     * @param stopName Parameter 2.
     * @return A new instance of fragment StopDetailFragment.
     */
    public static StopDetailFragment newInstance(int stopId, String stopName) {
        StopDetailFragment fragment = new StopDetailFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STOP_ID, stopId);
        args.putString(ARG_STOP_NAME, stopName);
        fragment.setArguments(args);

        return fragment;
    }

    public StopDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle args = new Bundle();
        args.putInt(StopDetailLoader.ATT_STOP_ID, mStopId);
        args.putInt(StopDetailLoader.ATT_HOUR, mCurrentHour);
        getLoaderManager().initLoader(LOADER_ID, args, this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mStopId = getArguments().getInt(ARG_STOP_ID);
            mStopName = getArguments().getString(ARG_STOP_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.stopdetailfragment, container, false);

        TextView tvStopDetail = (TextView) view.findViewById(R.id.tvStopDetail);
        tvStopDetail.setText(mStopName);

        Calendar rightNow = Calendar.getInstance();
        mCurrentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        mDetailList = (ListView) view.findViewById(R.id.lvStopDetail);
        mDetailList.setOnItemClickListener(this);

        updateData(null);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        getLoaderManager().getLoader(LOADER_ID).forceLoad();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (activity instanceof OnStopDetailListener) {
            mListener = (OnStopDetailListener) activity;
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
        if (mStopDetList != null) {
            if (position >= 0 && position < mStopDetList.size()) {
                if (mListener != null && mStopDetList.get(position).getRouteId() >= 0) {
                    StopDetail stopDetail = mStopDetList.get(position);
                    mListener.onStopDetailSelected(stopDetail.getRouteListId(),
                            stopDetail.getRouteId(), mStopName, stopDetail.getRouteName());
                }
            }
        }
    }

    /*  Async data loader callback implementation*/
    @Override
    public Loader<List<StopDetail>> onCreateLoader(int id, Bundle args) {
        return new StopDetailLoader(getActivity().getApplicationContext(), args);
    }

    @Override
    public void onLoadFinished(Loader<List<StopDetail>> loader, List<StopDetail> data) {
        updateData(data);
    }

    @Override
    public void onLoaderReset(Loader<List<StopDetail>> loader) {
    }


    public interface OnStopDetailListener {
        public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail);
    }

    /**
     * update list in fragment
     *
     * @param data list with StopDetail
     */
    private void updateData(List<StopDetail> data) {
        ArrayList<Map<String, Object>> pData = new ArrayList<Map<String, Object>>();
        mStopDetList = data;

        String ATT_ROUTE_NAME = "routeName";
        String ATT_MINUTES = "minutes";

        if (data != null) {
            Map<String, Object> map;

            for (StopDetail stopDetail : mStopDetList) {
                map = new HashMap<String, Object>();
                map.put(ATT_ROUTE_NAME, stopDetail.getRouteName());
                String minutes = "";
                List<String> minList = stopDetail.getMinuteList();
                for (String aMinList : minList) {
                    minutes = minutes + aMinList + "   ";
                }
                map.put(ATT_MINUTES, minutes);
                pData.add(map);
            }
        } else {
            Map<String, Object> map;

            map = new HashMap<String, Object>();
            map.put(ATT_ROUTE_NAME, getResources().getString(R.string.refresh_data));
            map.put(ATT_MINUTES, "");
            pData.add(map);
        }
        String[] from = {ATT_ROUTE_NAME, ATT_MINUTES};
        int[] to = {R.id.tvRouteName, R.id.tvNextTime};

        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), pData, R.layout.stopdetailitem,
                from, to);
        sAdapter.setViewBinder(new StopDetailBinder());

        mDetailList.setAdapter(sAdapter);
    }

    /**
     * this class fill list item
     */
    private class StopDetailBinder implements SimpleAdapter.ViewBinder {

        @Override
        public boolean setViewValue(View view, Object data,
                                    String textRepresentation) {
            int id = view.getId();
            if (id == R.id.tvRouteName ||
                    id == R.id.tvNextTime) {
                TextView tvTemp = (TextView) view;
                String strData = (String) data;
                tvTemp.setText(strData);

                return true;
            }
            return false;
        }
    }

    /**
     * background task loader
     */
    static class StopDetailLoader extends AsyncTaskLoader<List<StopDetail>> {

        public static final String ATT_STOP_ID = "stopId";
        public static final String ATT_HOUR = "currentHour";
        private final int mStopIdLoader;
        private final int mCurrentHourLoader;

        /**
         * Stores away the application context associated with context. Since Loaders can be used
         * across multiple activities it's dangerous to store the context directly.
         *
         * @param context used to retrieve the application context.
         */
        public StopDetailLoader(Context context, Bundle args) {
            super(context);

            mStopIdLoader = args.getInt(ATT_STOP_ID);
            mCurrentHourLoader = args.getInt(ATT_HOUR);
        }

        @Override
        public List<StopDetail> loadInBackground() {
            DBHelper dbHelper1 = DBHelper.getInstance(getContext());

            return dbHelper1.getStopDetail(mStopIdLoader, mCurrentHourLoader);
        }
    }
}
