package com.csharp.solutions.validations;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class RegistrationStep1 extends Activity {
    Button registration;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_step1);
        add_views();

        Drawable logo_drawable = getResources().getDrawable(getResources().getIdentifier("logo", "drawable", getPackageName()));
        Bitmap logo_bitmap = ((BitmapDrawable) logo_drawable).getBitmap();
        logo.setImageBitmap(logo_bitmap);


        registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(RegistrationStep1.this, RegistrationStep2.class);
                startActivity(intent);
            }
        });

    }

    public void add_views(){
        registration=(Button)findViewById(R.id.button_register);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        registration.setTransformationMethod(null);
    }

    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);

    }




}
