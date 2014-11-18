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
abstract class BaseAdapter<T extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<T> {

    /*  private fields  */
    private Cursor mCursor;
    private int mLayoutId;

    /*  public constructors */

    BaseAdapter(Cursor cursor, int id) {
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
        View v = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);

        return getHolder(v);
    }

    protected abstract T getHolder(View v);

    // Return the size of your data set (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        moveCursorToPosition(position);

        return getFieldValueAsInt(BaseContract.COLUMN_ID);
    }

    private void moveCursorToPosition(int position) {
        if (mCursor.getPosition() != position) {
            mCursor.moveToPosition(position);
        }
    }

    @Override
    public void onBindViewHolder(T holder, int position) {
        moveCursorToPosition(position);
    }

    String getFieldValue(String name) {
        int fieldIndex = mCursor.getColumnIndex(name);
        if (fieldIndex >= 0) {
            return mCursor.getString(fieldIndex);
        } else {
            throw new IllegalArgumentException("Field \"" + name + "\" not found!");
        }
    }

    int getFieldValueAsInt(String name) {
        int fieldIndex = mCursor.getColumnIndex(name);
        if (fieldIndex >= 0) {
            return mCursor.getInt(fieldIndex);
        } else {
            throw new IllegalArgumentException("Field \"" + name + "\" not found!");
        }
    }
}
