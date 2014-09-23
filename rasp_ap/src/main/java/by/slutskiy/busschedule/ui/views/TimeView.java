package by.slutskiy.busschedule.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import by.slutskiy.busschedule.R;

import static android.graphics.Paint.Align.*;

/**
 * Show List of String like horizontal list with horizontal and vertical align
 */
public class TimeView extends View {

    private static final int VERTICAL_ALIGN_TOP = 0;
    private static final int VERTICAL_ALIGN_CENTER = 1;
    private static final int VERTICAL_ALIGN_BOTTOM = 2;

    private final List<Item> mData = new ArrayList<Item>();

    private int mTextColor = Color.BLACK;
    private float mTextDimension = R.dimen.std_text_size;
    private Paint.Align mTextAlign = Paint.Align.CENTER;
    private int mVerticalAlign = VERTICAL_ALIGN_CENTER;

    private TextPaint mTextPaint;

    /*   padding   */
    private int mPaddingLeft;
    private int mPaddingRight;
    private int mPaddingTop;
    private int mPaddingBottom;

    private int mContentWidth;
    private int mContentHeight;

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

            mTextDimension = a.getDimension(R.styleable.TimeView_textSize, mTextDimension);

            switch (a.getInteger(R.styleable.TimeView_textAlign, 0)) {
                case 0:
                    mTextAlign = LEFT;
                    break;
                case 1:
                    mTextAlign = CENTER;
                    break;
                case 2:
                    mTextAlign = RIGHT;
                    break;
            }
            mVerticalAlign = a.getInteger(R.styleable.TimeView_verticalAlign, VERTICAL_ALIGN_CENTER);

        } finally {
            a.recycle();
        }


        // Set up a default TextPaint object
        mTextPaint = new TextPaint();
        mTextPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextAlign(mTextAlign);
        mTextPaint.setTextSize(mTextDimension);


        //add example data for edit mode
        if (this.isInEditMode()) {
            List<String> list = new ArrayList<String>();
            list.add("Hour");
            list.add("4 5 6");
            list.add("8 24 10");
            setMinList(list);
        }
        invalidateTextPaintAndMeasurements();
    }

    /**
     * set params to TextPaint and recalculate items coordinate, redraw view if visible
     */
    private void invalidateTextPaintAndMeasurements() {
        mTextPaint.setTextSize(mTextDimension);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextAlign(mTextAlign);

        //do recalculate {X,Y} for all items because Text dimension or Align can changed
        onDataChanged();

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw the text.
        if (mData.size() > 0) {
            for (Item item : mData) {
                canvas.drawText(item.mText,
                        item.mX,
                        item.mY,
                        mTextPaint);
            }
        }
    }

    /**
     * recalculate {X,Y} coordinate for all item
     */
    private void onDataChanged() {
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();

        int itemWidth;
        if (mData.size() > 0) {
            itemWidth = mContentWidth / mData.size();
        } else {
            itemWidth = mContentWidth;
        }

        float dx;
        float dy;

        //calculate dx with horizontal align
        switch (mTextAlign) {
            case LEFT:
                dx = mPaddingLeft;
                break;

            case RIGHT:
                dx = mPaddingLeft + itemWidth;
                break;

            default:
            case CENTER:
                dx = mPaddingLeft + itemWidth / 2;
                break;
        }

        //calculate dy with vertical align
        switch (mVerticalAlign) {
            case VERTICAL_ALIGN_TOP:
                dy = mPaddingTop - fontMetrics.top;
                break;

            case VERTICAL_ALIGN_BOTTOM:
                dy = mPaddingTop + mContentHeight - fontMetrics.bottom;
                break;

            default:
            case VERTICAL_ALIGN_CENTER:
                dy = mPaddingTop + (mContentHeight - fontMetrics.top - fontMetrics.bottom) / 2;
                break;
        }

        //recalculate {X,Y} coordinate for all items with deltaX (depend from align)
        for (int i = 0; i < mData.size(); i++) {
            Item item = mData.get(i);
            item.mX = dx + itemWidth * i;
            item.mY = dy;
        }
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     *
     * @param w    Current width of this view.
     * @param h    Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        initPaddingParams();

        //recalculate content size
        mContentWidth = getWidth() - mPaddingLeft - mPaddingRight;
        mContentHeight = getHeight() - mPaddingTop - mPaddingBottom;

        //do recalculate {X,Y} for all items
        onDataChanged();
    }

    private void initPaddingParams(){
        mPaddingLeft = getPaddingLeft();
        mPaddingTop = getPaddingTop();
        mPaddingRight = getPaddingRight();
        mPaddingBottom = getPaddingBottom();
    }

    /**
     * Sets the view's string attribute value. In the example view, this string
     * is the text to draw.
     *
     * @param list The list of string attribute value to use.
     */
    public void setMinList(List<String> list) {
        mData.clear();

        for (String item : list) {
            Item dataItem = new Item();
            dataItem.mText = item;
            mData.add(dataItem);
        }

        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the color attribute value.
     *
     * @return The color attribute value.
     */
    public int getTextColor() {
        return mTextColor;
    }

    /**
     * Sets the view's color attribute value. In the view, this color
     * is the font color.
     *
     * @param color The color attribute value to use.
     */
    public void setTextColor(int color) {
        mTextColor = color;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the text align attribute value.
     *
     * @return The text align attribute value.
     */
    public Paint.Align getTextAlign() {
        return mTextAlign;
    }

    /**
     * Sets the view's text align attribute value.
     *
     * @param align The text align attribute value to use.
     */
    public void setTextAlign(Paint.Align align) {
        mTextAlign = align;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the text size dimension attribute value.
     *
     * @return The text size dimension attribute value.
     */
    public float getTextDimension() {
        return mTextDimension;
    }

    /**
     * Sets the view's text size dimension attribute value.
     *
     * @param textDimension The text size dimension attribute value to use.
     */
    public void setTextDimension(float textDimension) {
        mTextDimension = textDimension;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * Gets the text vertical align
     * @return vertical align attribute value
     */
    public int getVerticalAlign() {
        return mVerticalAlign;
    }

    /**
     * Sets the view's text vertical align attribute value
     * @param mVerticalAlign The vertical value to use
     */
    public void setVerticalAlign(int mVerticalAlign) {
        this.mVerticalAlign = mVerticalAlign;
        invalidateTextPaintAndMeasurements();
    }

    /**
     * inner class used for save calculated coordinate {X,Y} and used when need draw items
     */
    private class Item {
        public String mText;
        public float mX;
        public float mY;
    }
}
