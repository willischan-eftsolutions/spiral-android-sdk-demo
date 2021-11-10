package com.eftsolutions.integratedpaymentsolutiondemo.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Window;

import com.eftsolutions.integratedpaymentsolutiondemo.R;

public class LoadingDialog extends Dialog {
    public LoadingDialog(Context context) {
        super(context);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setCancelable(false);
        setContentView(R.layout.dialog_loading);
    }
}
