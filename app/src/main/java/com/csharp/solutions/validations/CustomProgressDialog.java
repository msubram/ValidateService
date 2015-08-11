package com.csharp.solutions.validations;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.ImageView;


public class CustomProgressDialog extends ProgressDialog {
    AnimationDrawable animation;
    static Context context=null;
    ProgressDialog  dialog=null;
    public static ProgressDialog ctor(Context ctxt) {
    CustomProgressDialog dialog = new CustomProgressDialog(ctxt);
    dialog.setIndeterminate(true);
    dialog.setCancelable(false);
      context = ctxt;
    return dialog;
  }

  public CustomProgressDialog(Context context) {

      super(context);
      dialog = new ProgressDialog(context);
      dialog.setMessage("Loading...");
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
