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
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.StopDetail;
import by.slutskiy.busschedule.loaders.StopDetailLoader;
import by.slutskiy.busschedule.ui.viewbinders.StopDetailBinder;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static android.widget.AdapterView.OnItemClickListener;


/*
 * StopDetailFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class StopDetailFragment extends Fragment implements OnItemClickListener,
        LoaderCallbacks<List<StopDetail>> {

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
        View view = inflater.inflate(R.layout.fragment_stop_detail, container, false);

        TextView tvStopDetail = (TextView) view.findViewById(R.id.text_view_stop_detail);
        tvStopDetail.setText(mStopName);

        Calendar rightNow = Calendar.getInstance();
        mCurrentHour = rightNow.get(Calendar.HOUR_OF_DAY);

        mDetailList = (ListView) view.findViewById(R.id.list_view_stop_detail);
        mDetailList.setOnItemClickListener(this);

        updateData(null);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Loader<List<StopDetail>> stopDetailLoader = getLoaderManager().getLoader(LOADER_ID);
        if (stopDetailLoader != null) {
            stopDetailLoader.forceLoad();
        }
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
            map.put(ATT_ROUTE_NAME, getString(R.string.text_view_get_data));
            map.put(ATT_MINUTES, "");
            pData.add(map);
        }
        String[] from = {ATT_ROUTE_NAME, ATT_MINUTES};
        int[] to = {R.id.text_view_route_name, R.id.text_view_next_time};

        SimpleAdapter sAdapter = new SimpleAdapter(getActivity(), pData, R.layout.list_item_stop_detail,
                from, to);
        sAdapter.setViewBinder(new StopDetailBinder());

        mDetailList.setAdapter(sAdapter);
    }


    /**
     * OnStopDetailListener
     */
    public interface OnStopDetailListener {
        public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail);
    }

}
