/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.boliu.android.viewtrip.session;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;

import org.boliu.android.viewtrip.MainActivity;
import org.boliu.android.viewtrip.R;
import org.boliu.android.viewtrip.ui.BaseActivity;
import org.boliu.android.viewtrip.util.LogUtils;

/**
 * Displays the details about a session. This Activity is launched via an {@code Intent} with
 * {@link Intent#ACTION_VIEW} and a {@link Uri} built with
 */

public class SessionDetailActivity extends BaseActivity {

    private static final String TAG = LogUtils.makeLogTag(SessionDetailActivity.class);

    private Handler mHandler = new Handler();

    private Uri mSessionUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.session_detail_act);

        boolean shouldBeFloatingWindow = true;
        if (shouldBeFloatingWindow) {
            setupFloatingWindow(R.dimen.session_details_floating_width,
                    R.dimen.session_details_floating_height, 1, 0.4f);
        }


    }

    public Uri getSessionUri() {
        return mSessionUri;
    }

    @Override
    public Intent getParentActivityIntent() {
        return new Intent(this, MainActivity.class);
    }
}
