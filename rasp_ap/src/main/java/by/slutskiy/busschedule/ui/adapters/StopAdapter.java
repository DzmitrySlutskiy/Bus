package by.slutskiy.busschedule.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.StopContract;

/**
 * RouteAdapter
 * Version 1.0
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopAdapter extends BaseAdapter<StopAdapter.ViewHolder> {

    private final onItemClickListener mListener;

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mStop;

        private final onItemClickListener mListener;

        public ViewHolder(View v, onItemClickListener listener) {
            super(v);

            mListener = listener;
            mStop = (TextView) v.findViewById(R.id.text_view_stop);

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onClick(mStop.getText().toString(), (Long) mStop.getTag());
            }
        }
    }

    public StopAdapter(Cursor cursor, onItemClickListener listener) {
        super(cursor, R.layout.list_item_stop);

        mListener = listener;
    }

    @Override
    public StopAdapter.ViewHolder getHolder(View v) {
        return new ViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        holder.mStop.setText(getFieldValue(StopContract.COLUMN_STOP_NAME));
        holder.mStop.setTag(getItemId(position));
    }

    public interface onItemClickListener {
        void onClick(String stopName, long id);
    }
}
