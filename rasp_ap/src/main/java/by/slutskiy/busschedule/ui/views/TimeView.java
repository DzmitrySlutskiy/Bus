package by.slutskiy.busschedule.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.R;

/*
 * TimeViewLayout - show strings in horizontal list
 *
 * Version 1.0
 * 24.09.2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */
public class TimeView extends LinearLayout {

    private final List<String> mData = new ArrayList<String>();

    private final List<TextView> mTextViewList = new ArrayList<TextView>();

    private int mTextColor = Color.BLACK;
    private float mTextSize = R.dimen.std_text_size;
    private int mGravity;

    private LayoutParams mLayoutParams;

    public TimeView(Context context) {
        super(context);
        init(null, 0);
    }

    public TimeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public TimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.TimeView, defStyle, 0);
        try {
            mTextColor = a.getColor(R.styleable.TimeView_textColor, mTextColor);
            mTextSize = a.getDimension(R.styleable.TimeView_textSize, mTextSize);
        } finally {
            a.recycle();
        }

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);

        //layout params for TextViews
        mLayoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT, 1);

        //add example data for edit mode
        if (this.isInEditMode()) {
            List<String> list = new ArrayList<String>();
            list.add("Hour");
            list.add("4 5 6");
            list.add("8 24 10");
            setMinList(list);
        }
    }

    /**
     * Describes how the child views are positioned. Defaults to GRAVITY_TOP. If
     * this layout has a VERTICAL orientation, this controls where all the child
     * views are placed if there is extra vertical space. If this layout has a
     * HORIZONTAL orientation, this controls the alignment of the children.
     *
     * @param gravity See {@link android.view.Gravity}
     * @attr ref android.R.styleable#LinearLayout_gravity
     */
    @Override
    public void setGravity(int gravity) {
        super.setGravity(gravity);

        //save gravity (don't know how get from layout after setting this parameter)
        mGravity = gravity;
    }

    /**
     * Set list of string
     *
     * @param list The list of string to use.
     */
    public void setMinList(List<String> list) {
        mData.clear();
        mData.addAll(list);
        onDataChanged();
    }

    /**
     * call when data changed, create needed TextViews or delete unneeded
     */
    private void onDataChanged() {
        int size = mData.size();
        int needChildCount = size - getChildCount();
        boolean mustAdd = needChildCount > 0;
        //check needed TextView count : if need more - add, else remove unneeded
        for (int i = 0; i < Math.abs(needChildCount); i++) {
            if (mustAdd) {
                TextView view = getTextView();
                mTextViewList.add(view);
                addView(view);
            } else {
                removeView(mTextViewList.get(mTextViewList.size() - 1));
                mTextViewList.remove(mTextViewList.size() - 1);
            }
        }
        //set new text to textView
        for (int i = 0; i < mTextViewList.size(); i++) {
            mTextViewList.get(i).setText(mData.get(i));
        }
    }

    /**
     * create TextView instance
     *
     * @return TextView instance
     */
    private TextView getTextView() {
        TextView tvTemp = new TextView(getContext());

        tvTemp.setLayoutParams(mLayoutParams);
        initTextParam(tvTemp);

        return tvTemp;
    }

    /**
     * reset text parameters in TextViews
     */
    private void invalidateTextParameters() {
        for (TextView textView : mTextViewList) {
            initTextParam(textView);
        }
    }

    /**
     * Set parameters for textView
     *
     * @param textView TextView instance for set
     */
    private void initTextParam(TextView textView) {
        textView.setTextColor(mTextColor);
        //used COMPLEX_UNIT_PX because sp transform to px size when get attribute (init method)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        textView.setGravity(mGravity);
    }

    /**
     * Set text size
     *
     * @param newTextSize new text size for set
     */
    public void setTextSize(int newTextSize) {
        mTextSize = newTextSize;
        invalidateTextParameters();
    }

    /**
     * Set text color
     *
     * @param color new text color for set
     */
    public void setTextColor(int color) {
        mTextColor = color;
        invalidateTextParameters();
    }
}
