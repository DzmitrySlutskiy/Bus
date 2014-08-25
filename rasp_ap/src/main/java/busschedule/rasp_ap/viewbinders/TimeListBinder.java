package busschedule.rasp_ap.viewbinders;

import android.content.res.Resources;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.Calendar;

import busschedule.rasp_ap.R;
import busschedule.rasp_ap.ui.fragments.TimeListFragment;

/**
 * fill list items
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class TimeListBinder implements SimpleAdapter.ViewBinder {

    private boolean useBoldFont = false;

    private final Calendar rightNow = Calendar.getInstance();
    private final int mHour = rightNow.get(Calendar.HOUR_OF_DAY);

    @Override
    public boolean setViewValue(View view, Object data,
                                String textRepresentation) {
        int id = view.getId();
        if (id == R.id.tvHour) {
            TextView tvTemp = (TextView) view;
            int hourItem = (Integer) data;
            useBoldFont = (hourItem == mHour);
            tvTemp.setText(Integer.toString(hourItem));
        } else if (id == R.id.layoutMinutes) {
            String[] minArray = (String[]) data;
            LinearLayout minPanel = ((LinearLayout) view);

            //если юзается уже использованный view
            // (который вышел за пределы видимости экрана при прокрутке) проверяем достаточно
            // ли элементов для вывода минут и добавляем в случае необходимости
            if (minPanel.getChildCount() < minArray.length) {
                int childNeeded = minArray.length - minPanel.getChildCount();
                for (int i = 0; i < childNeeded; i++) {
                    minPanel.addView(TimeListFragment.getTextView(view.getContext(), ""));
                }
            }
            for (int i = 0; i < minArray.length; i++) {
                TextView tView = ((TextView) minPanel.getChildAt(i));
                if (tView != null) {
                    tView.setText(minArray[i]);
                }
            }
        }

        Resources resources = view.getContext().getApplicationContext().getResources();

        if (resources != null) {
            view.setBackgroundColor(useBoldFont ? resources.getColor(R.color.currenthour) :
                    resources.getColor(R.color.not_currenthour));
        }

        return true;
    }
}