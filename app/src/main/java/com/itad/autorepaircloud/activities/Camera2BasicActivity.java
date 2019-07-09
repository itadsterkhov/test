/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.itad.autorepaircloud.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.itad.autorepaircloud.R;
import com.itad.autorepaircloud.fragments.interfaces.IFragmentMethods;
import com.itad.autorepaircloud.fragments.Camera2PhotoFragment;

public class Camera2BasicActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_basic);
        if (null == savedInstanceState) {
            Camera2PhotoFragment camera2PhotoFragment = Camera2PhotoFragment.newInstance(R.layout.fragment_camera2_basic);
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
