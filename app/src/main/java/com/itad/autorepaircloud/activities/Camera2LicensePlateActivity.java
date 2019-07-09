package com.itad.autorepaircloud.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.itad.autorepaircloud.R;
import com.itad.autorepaircloud.fragments.interfaces.IFragmentMethods;
import com.itad.autorepaircloud.fragments.Camera2PhotoFragment;


public class Camera2LicensePlateActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_basic);
        if (null == savedInstanceState) {
            Camera2PhotoFragment camera2PhotoFragment = Camera2PhotoFragment.newInstance(R.layout.fragment_camera2_license_plate);
            camera2PhotoFragment.setArguments(getIntent().getExtras());

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, camera2PhotoFragment)
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        @SuppressLint("ResourceType")
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (!(fragment instanceof IFragmentMethods) || !((IFragmentMethods) fragment).onBackPressed()) {
            super.onBackPressed();
        }
    }
}