package com.nuvola.gxpenses.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import com.nuvola.gxpenses.R;

public class LoadingDialog extends ProgressDialog {
    public LoadingDialog(final Activity activity) {
        super(activity);
        this.setIndeterminate(true);
        this.setMessage(activity.getString(R.string.loading_message));

        // Set up the OnCancel Event
        this.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                activity.finish();
            }
        });
    }
}
