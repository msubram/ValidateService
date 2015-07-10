package com.csharp.solutions.validations;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Created by Arputha on 04/07/2015.
 */
public class UpdateScreen extends ActionBarActivity {
    Button update;
    ImageView logo;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updatescreen);
        add_views();

        Drawable logo_drawable = getResources().getDrawable(getResources().getIdentifier("logo", "drawable", getPackageName()));
        Bitmap logo_bitmap = ((BitmapDrawable) logo_drawable).getBitmap();
        logo.setImageBitmap(logo_bitmap);


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(UpdateScreen.this, ValidateScreen.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case R.id.menu_validate:
                Intent intent = new Intent(UpdateScreen.this, ValidateScreen.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void add_views(){
        update=(Button)findViewById(R.id.button_update);
        logo=(ImageView)findViewById(R.id.imageview_logo);
        update.setTransformationMethod(null);

    }
    public void onBackPressed() {
        Intent intent = new Intent(UpdateScreen.this,RegistrationStep2.class);
        startActivity(intent);
    }
}
