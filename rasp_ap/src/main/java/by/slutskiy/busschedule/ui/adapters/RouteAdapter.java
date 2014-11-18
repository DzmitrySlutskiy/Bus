package by.slutskiy.busschedule.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.RouteContract;

/**
 * RouteAdapter
 * Version 1.0
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class RouteAdapter extends BaseAdapter<RouteAdapter.ViewHolder> {

    private final onItemClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mBusNumber;
        public final TextView mRouteName;
        private final onItemClickListener mListener;

        public ViewHolder(View v, onItemClickListener listener) {
            super(v);

            mListener = listener;
            mBusNumber = (TextView) v.findViewById(R.id.text_view_bus_number);
            mRouteName = (TextView) v.findViewById(R.id.text_view_route_name);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick((Long) mRouteName.getTag());
            }
        }
    }

    public RouteAdapter(Cursor cursor, onItemClickListener listener) {
        super(cursor, R.layout.list_item_route);

        mListener = listener;
    }

    @Override
    public RouteAdapter.ViewHolder getHolder(View v) {
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.mBusNumber.setText(getFieldValue(RouteContract.COLUMN_BUS));
        holder.mRouteName.setText(getFieldValue(RouteContract.COLUMN_ROUTE_NAME));
        holder.mRouteName.setTag(getItemId(position));
    }

    public interface onItemClickListener {
        void onClick(long id);
    }
}
