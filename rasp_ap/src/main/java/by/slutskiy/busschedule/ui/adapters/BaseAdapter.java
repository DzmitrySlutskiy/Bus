package by.slutskiy.busschedule.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import by.slutskiy.busschedule.providers.contracts.BaseContract;

/**
 * BaseAdapter
 * Version information
 * 17.11.2014
 * Created by Dzmitry Slutskiy.
 */
public abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    protected Cursor mCursor;
    protected int mLayoutId;
    /*  private fields  */

    /*  public constructors */

    public BaseAdapter(Cursor cursor, int id) {
        if (cursor == null) {
            throw new IllegalArgumentException("Cursor can't be null");
        }

        mCursor = cursor;
        mLayoutId = id;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public T onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(mLayoutId, parent, false);

        return getHolder(v);
    }

    public abstract T getHolder(View v);

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        if (mCursor.getPosition() != position) {
            mCursor.moveToPosition(position);
        }

        return mCursor.getInt(mCursor.getColumnIndex(BaseContract.COLUMN_ID));
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        if (mCursor != null) {
            mCursor.moveToPosition(position);
        }
    }

    protected String getFieldValue(String name) {
        int fieldIndex = mCursor.getColumnIndex(name);
        if (fieldIndex >= 0) {
            return mCursor.getString(fieldIndex);
        } else {
            throw new IllegalArgumentException("Field \"" + name + "\" not found!");
        }
    }

    protected int getFieldValueAsInt(String name) {
        int fieldIndex = mCursor.getColumnIndex(name);
        if (fieldIndex >= 0) {
            return mCursor.getInt(fieldIndex);
        } else {
            throw new IllegalArgumentException("Field \"" + name + "\" not found!");
        }
    }
}
