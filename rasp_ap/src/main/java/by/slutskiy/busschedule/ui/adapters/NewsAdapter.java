package by.slutskiy.busschedule.ui.adapters;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.providers.contracts.NewsContract;

/**
 * NewsAdapter
 * Version information
 * 13.11.2014
 * Created by Dzmitry Slutskiy.
 */
public class NewsAdapter extends BaseAdapter<NewsAdapter.ViewHolder> {

    private final Pattern mPattern = Pattern.compile("[0-9]{1,2}\\.[0-9]{1,2}.[0-9]{4}");
    private final StyleSpan mSpan = new StyleSpan(android.graphics.Typeface.BOLD);

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView mTextView;

        public ViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }

    public NewsAdapter(Cursor cursor) {
        super(cursor, R.layout.item_text_view);
    }

    @Override
    public NewsAdapter.ViewHolder getHolder(View v) {
        return new ViewHolder((TextView) v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);

        String news = getFieldValue(NewsContract.COLUMN_NEWS);

        //set BOLD typeface for date
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
