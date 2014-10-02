package by.slutskiy.busschedule.ui.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import by.slutskiy.busschedule.R;
import by.slutskiy.busschedule.services.UpdateService;
import by.slutskiy.busschedule.utils.PreferenceUtils;

/**
 * A simple {@link DialogFragment } subclass.
 */
public class RunUpdateDialogFragment extends DialogFragment {

    public RunUpdateDialogFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.toast_update_available)
                .setPositiveButton(R.string.dialog_run_update_button_positive,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                UpdateService.runUpdateService(getActivity());
                            }
                        })
                .setNegativeButton(R.string.dialog_run_update_button_negative,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                PreferenceUtils.setUpdateState(getActivity(), false);
                            }
                        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
