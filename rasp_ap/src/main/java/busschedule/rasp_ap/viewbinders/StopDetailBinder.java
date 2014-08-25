package busschedule.rasp_ap.viewbinders;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import busschedule.rasp_ap.R;

/**
 * this class fill list item
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class StopDetailBinder implements SimpleAdapter.ViewBinder {

    @Override
    public boolean setViewValue(View view, Object data,
                                String textRepresentation) {
        int id = view.getId();
        if (id == R.id.tvRouteName ||
                id == R.id.tvNextTime) {
            TextView tvTemp = (TextView) view;
            String strData = (String) data;
            tvTemp.setText(strData);

            return true;
        }
        return false;
    }
}
