/*

Copyright 2018 tarekmabdallah91@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

 */
package com.example.tarek.popularmoviesapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.Toolbar;

import com.example.tarek.popularmoviesapp.R;

public class BackgroundColorUtils {

    private final Activity activity;
    private final Toolbar toolbar;
    private final Context context;

    public BackgroundColorUtils(Activity activity, Toolbar toolbar) {
        this.activity = activity;
        this.toolbar = toolbar;
        this.context = activity.getBaseContext();
    }

    public final void getSharedPreferenceColor(SharedPreferences sharedPreferences) {
        String theme = sharedPreferences.getString(context.getString(R.string.key_background_color_list),
                context.getString(R.string.value_color_black));
        setTheme(theme);

    }

    private void setTheme(String theme) {
        if (null != theme) {
            if (context.getResources().getString(R.string.value_color_blue_bright).equals(theme)) {
                changeBackgroundColorTo(R.color.blue_bright);
            } else if (context.getResources().getString(R.string.value_color_green_dark).equals(theme)) {
                changeBackgroundColorTo(R.color.green_dark);
            } else if (context.getResources().getString(R.string.value_color_orange_dark).equals(theme)) {
                changeBackgroundColorTo(R.color.orange_dark);
            } else if (context.getResources().getString(R.string.value_color_orange_light).equals(theme)) {
                changeBackgroundColorTo(R.color.orange_light);
            } else if (context.getResources().getString(R.string.value_color_purple).equals(theme)) {
                changeBackgroundColorTo(R.color.purple);
            } else if (context.getResources().getString(R.string.value_color_red_dark).equals(theme)) {
                changeBackgroundColorTo(R.color.red_dark);
            } else {
                changeBackgroundColorTo(R.color.black);
            }

        }
    }

    private void changeBackgroundColorTo(int colorId) {
        activity.getWindow().getDecorView().setBackgroundColor(context.getResources().getColor(colorId));
        if (null != toolbar) {
            if (R.color.black == colorId)
                toolbar.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary2));
            else
                toolbar.setBackgroundColor(context.getResources().getColor(R.color.black));
        }

    }
}
