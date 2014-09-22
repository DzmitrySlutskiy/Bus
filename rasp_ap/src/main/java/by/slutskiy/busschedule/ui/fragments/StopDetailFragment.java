/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.data.entities.StopDetail;
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
public class StopDetailFragment extends BaseFragment implements OnItemClickListener {

    public static final String TAG = StopDetailFragment.class.getSimpleName();

    // the fragment initialization parameters
    private static final int LOADER_ID = MainActivity.getNextLoaderId();
    public static final String STOP_ID = "mStopId";
    public static final String STOP_NAME = "mStopName";

    private List<StopDetail> mStopDetList = null;
    private int mStopId;
    private String mStopName;
    private int mCurrentHour;

    private boolean mNeedRestartLoaders = false;

    private OnStopDetailListener mListener;

    private ListView mDetailList;
    private TextView mStopDetail;

    private StopDetailCallBack mStopDetailCallBack = null;

    public StopDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void changeArguments(Bundle args) {
        if (args != null) {
            int oldStopId = mStopId;
            String oldStopName = mStopName;

            mStopId = args.getInt(STOP_ID);
            mStopName = args.getString(STOP_NAME);

            if (oldStopId != mStopId || ! oldStopName.equals(mStopName)) {
                mNeedRestartLoaders = true;
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mStopId = savedInstanceState.getInt(STOP_ID);
            mStopName = savedInstanceState.getString(STOP_NAME);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getLoaderManager().initLoader(LOADER_ID, prepareLoaderArgs(),
                getStopDetailCallBack());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stop_detail, container, false);

        mStopDetail = (TextView) view.findViewById(R.id.text_view_stop_detail);

        Calendar rightNow = Calendar.getInstance();
        mCurrentHour = rightNow.get(Calendar.HOUR_OF_DAY);

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
        if (mStopDetList != null &&
                position >= 0 &&
                position < mStopDetList.size() &&
                mListener != null) {
            mListener.onStopDetailSelected((int) id, mStopName,
                    mStopDetList.get(position).getRouteName());
        }


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);

        if (! hidden && mNeedRestartLoaders) {      //if data will be changed after hidden fragment
            getLoaderManager().restartLoader(LOADER_ID, prepareLoaderArgs(),
                    getStopDetailCallBack());
            mNeedRestartLoaders = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(STOP_ID, mStopId);
        outState.putString(STOP_NAME, mStopName);
    }

    private Bundle prepareLoaderArgs() {
        Bundle args = new Bundle();
        args.putInt(StopDetailLoader.ATT_STOP_ID, mStopId);
        args.putInt(StopDetailLoader.ATT_HOUR, mCurrentHour);

        return args;
    }

    private StopDetailCallBack getStopDetailCallBack() {
        if (mStopDetailCallBack == null) {
            mStopDetailCallBack = new StopDetailCallBack();
        }

        return mStopDetailCallBack;
    }

    /*  Async data loader callback implementation*/
    private class StopDetailCallBack implements LoaderCallbacks<List<StopDetail>> {
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
    }

    /**
     * update list in fragment
     *
     * @param data list with StopDetail
     */
    private void updateData(List<StopDetail> data) {
        if (data != null) {
            mStopDetail.setText(mStopName);
            mStopDetList = data;

            StopDetailAdapter adapter = new StopDetailAdapter(getActivity(), data);
            mDetailList.setAdapter(adapter);
        } else {
            mDetailList.setAdapter(new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_list_item_1,
                    new String[]{getString(R.string.text_view_get_data)}));
        }
    }

    /**
     * OnStopDetailListener
     */
    public interface OnStopDetailListener {
        public void onStopDetailSelected(int routeListId, String stopName, String stopDetail);
    }

}
