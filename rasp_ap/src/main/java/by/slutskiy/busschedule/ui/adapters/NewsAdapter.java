package by.slutskiy.busschedule.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.slutskiy.busschedule.R;

/**
 * Classname
 * Version information
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private List<String> mDataset;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NewsAdapter(List<String> myDataset) {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NewsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_text_view, parent, false);
        // set the view's size, margins, paddings and layout parameters
        //
        return new ViewHolder((TextView) v);
    }

    final Pattern mPattern = Pattern.compile("[0-9]{1,2}\\.[0-9]{1,2}.[0-9]{4}");
    final StyleSpan mSpan = new StyleSpan(android.graphics.Typeface.BOLD);

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        if (mDataset != null) {
//            mDataset.moveToPosition(position);
//            String news = mDataset.getString(mDataset.getColumnIndex(NewsContract.COLUMN_NEWS));

            String news = mDataset.get(position);

            final Matcher matcher = mPattern.matcher(news);

            final SpannableStringBuilder spannable = new SpannableStringBuilder(news);

            while (matcher.find()) {
                spannable.setSpan(
                        mSpan, matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                );
            }

            holder.mTextView.setText(spannable);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }

    public void add(String newNews) {
        if (mDataset == null) {
            mDataset = new ArrayList<String>();
        }
        mDataset.add(0, newNews);
//        notifyItemInserted(0);
//        for (int i = 0; i < mDataset.size() - 1; i++) {
//            notifyItemMoved(i, i + 1);
//        }
    }
//    public void changeCursor(Cursor cursor) {
//        mDataset = cursor;
////        notifyDataSetChanged();
//    }
}
