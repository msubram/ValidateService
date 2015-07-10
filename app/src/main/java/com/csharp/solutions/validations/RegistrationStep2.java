package com.csharp.solutions.validations;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Arputha on 04/07/2015.
 */
public class RegistrationStep2 extends Activity {
    Button complete,retry;
    ImageView logo;
    Context context;
    EditText firstdigit,seconddigit,thirddigit,fourthdigit,fifthdigit;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_step2);
       add_views();
        context=this;
        Drawable logo_drawable = getResources().getDrawable(getResources().getIdentifier("logo", "drawable", getPackageName()));
        Bitmap logo_bitmap = ((BitmapDrawable) logo_drawable).getBitmap();
        logo.setImageBitmap(logo_bitmap);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(RegistrationStep2.this, UpdateScreen.class);
                startActivity(intent);
            }
        });

        firstdigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(firstdigit.getText().length()==1) {
                    firstdigit.clearFocus();
                    seconddigit.requestFocus();
                }
            }
        });

        seconddigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(seconddigit.getText().length()==1) {
                    seconddigit.clearFocus();
                    thirddigit.requestFocus();
                }
            }
        });

        thirddigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(thirddigit.getText().length()==1) {
                    thirddigit.clearFocus();
                    fourthdigit.requestFocus();
                }
            }
        });

        fourthdigit.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(fourthdigit.getText().length()==1) {
                    fourthdigit.clearFocus();
                    fifthdigit.requestFocus();
                }
            }

        });

//        fifthdigit.addTextChangedListener(new TextWatcher() {
//            public void afterTextChanged(Editable s) {
//            }
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//            }
//        });
    }

    public void add_views(){
        complete=(Button)findViewById(R.id.button_complete);
        retry=(Button)findViewById(R.id.button_retry);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        firstdigit=(EditText)findViewById(R.id.edittext_ota_first_digit);
        seconddigit=(EditText)findViewById(R.id.edittext_ota_second_digit);
        thirddigit=(EditText)findViewById(R.id.edittext_ota_third_digit);
        fourthdigit=(EditText)findViewById(R.id.edittext_ota_fourth_digit);
        fifthdigit=(EditText)findViewById(R.id.edittext_ota_fifth_digit);

        firstdigit.setInputType(InputType.TYPE_CLASS_NUMBER);
        complete.setTransformationMethod(null);
        retry.setTransformationMethod(null);
    }

    public void onBackPressed() {
        Intent intent = new Intent(RegistrationStep2.this,RegistrationStep1.class);
        startActivity(intent);
    }

}
