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
import android.widget.TextView;

import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.RouteList;
import by.slutskiy.busschedule.loaders.StopDetailLoader;
import by.slutskiy.busschedule.ui.activity.MainActivity;
import by.slutskiy.busschedule.ui.adapters.StopDetailAdapter;

import static android.support.v4.app.LoaderManager.LoaderCallbacks;
import static android.widget.AdapterView.OnItemClickListener;


/*
 * StopDetailFragment
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class StopDetailFragment extends Fragment implements OnItemClickListener {

    // the fragment initialization parameters
    private static final String ARG_STOP_ID = "stopId";
    private static final String ARG_STOP_NAME = "stopName";
    private static final int LOADER_ID = MainActivity.getNextLoaderId();

    private List<RouteList> mStopDetList = null;
    private int mStopId;
    private String mStopName;

    private OnStopDetailListener mListener;

    private ListView mDetailList;

    private StopDetailCallBack mStopDetailCallBack = null;

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
        getLoaderManager().initLoader(LOADER_ID, args, getStopDetailCallBack());
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

        mDetailList = (ListView) view.findViewById(R.id.list_view_stop_detail);
        mDetailList.setOnItemClickListener(this);

        updateData(null);

        return view;
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
                RouteList routeList = mStopDetList.get(position);

                if (mListener != null && routeList.getmRoutes().getmId() >= 0) {

                    mListener.onStopDetailSelected(routeList.getmId(),
                            routeList.getmRoutes().getmId(), mStopName,
                            routeList.getmRoutes().toString());
                }
            }
        }
    }

    private StopDetailCallBack getStopDetailCallBack() {
        if (mStopDetailCallBack == null) {
            mStopDetailCallBack = new StopDetailCallBack();
        }

        return mStopDetailCallBack;
    }

    /*  Async data loader callback implementation*/
    private class StopDetailCallBack implements LoaderCallbacks<List<RouteList>> {
        @Override
        public Loader<List<RouteList>> onCreateLoader(int id, Bundle args) {
            return new StopDetailLoader(getActivity().getApplicationContext(), args);
        }

        @Override
        public void onLoadFinished(Loader<List<RouteList>> loader, List<RouteList> data) {
            updateData(data);
        }

        @Override
        public void onLoaderReset(Loader<List<RouteList>> loader) {
        }
    }

    /**
     * update list in fragment
     *
     * @param data list with StopDetail
     */
    private void updateData(List<RouteList> data) {
        mStopDetList = data;
        StopDetailAdapter adapter = new StopDetailAdapter(getActivity(), mStopDetList);
        mDetailList.setAdapter(adapter);
    }


    /**
     * OnStopDetailListener
     */
    public interface OnStopDetailListener {
        public void onStopDetailSelected(int routeListId, int routeId, String stopName, String stopDetail);
    }

}
