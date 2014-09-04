package by.slutskiy.busschedule.ui.viewbinders;

import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import by.slutskiy.busschedule.R;

/**
 * BusRouteBinder - this class fill data in list item
 * Version 1.0
 * 25.08.2014
 * Created by Dzmitry Slutskiy.
 */
public class BusRouteBinder implements SimpleAdapter.ViewBinder {
    @Override
    public boolean setViewValue(View view, Object data,
                                String textRepresentation) {
        int id = view.getId();
        if (id == R.id.text_view_bus_number ||
                id == R.id.text_view_begin_stop ||
                id == R.id.text_view_end_stop) {
            TextView tvTemp = (TextView) view;
            tvTemp.setText((String) data);
            return true;
        }
            /*save item _id in layout tags*/
        if (id == R.id.list_view_item) {
            view.setTag(data);
            return true;
        }
        return false;
    }
}
