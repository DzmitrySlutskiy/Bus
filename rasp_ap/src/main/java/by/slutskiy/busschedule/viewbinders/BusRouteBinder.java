package by.slutskiy.busschedule.viewbinders;

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
        if (id == R.id.tvBusNumber ||
                id == R.id.tvBeginStop ||
                id == R.id.tvEndStop) {
            TextView tvTemp = (TextView) view;
            tvTemp.setText((String) data);
            return true;
        }
            /*save item _id in layout tags*/
        if (id == R.id.itemlayout) {
            view.setTag(data);
            return true;
        }
        return false;
    }
}
