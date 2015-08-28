package com.csharp.solutions.validations;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;

import util.GlobalClass;


public class CustomProgressDialog extends ProgressDialog {
    static Context context=null;
    ProgressDialog  dialog=null;

    public static ProgressDialog ctor(Context ctxt, String loadingmessage) {

    CustomProgressDialog dialog = new CustomProgressDialog(ctxt,loadingmessage);
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
    context = ctxt;
    return dialog;
  }

  public CustomProgressDialog(Context context, String loadingmessage) {

      super(context);
      dialog = new ProgressDialog(context);
      dialog.setMessage(loadingmessage);
      dialog.setIndeterminate(true);
      dialog.setCancelable(false);
  }




  @Override
  public void show() {
    super.show();
      dialog.show();
  }

  @Override
  public void dismiss() {
    super.dismiss();
      dialog.dismiss();
  }
}
