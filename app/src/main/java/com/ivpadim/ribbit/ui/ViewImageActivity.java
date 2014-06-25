package com.ivpadim.ribbit.ui;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.ivpadim.ribbit.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ViewImageActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_view_image);

        ImageView imageView  = (ImageView) findViewById(R.id.imageView);
        Uri imageUri = getIntent().getData();

        setProgressBarIndeterminateVisibility(true);

        Picasso.with(this).load(imageUri.toString()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                setProgressBarIndeterminateVisibility(false);
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 10*1000);

            }

            @Override
            public void onError() {
                setProgressBarIndeterminateVisibility(false);
                finish();
            }
        });


    }
}
