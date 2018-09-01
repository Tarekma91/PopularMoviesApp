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
package com.example.tarek.popularmoviesapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceCategory;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.example.tarek.popularmoviesapp.utils.MoviesConstantsUtils;


public class SettingsFragment extends PreferenceFragmentCompat implements MoviesConstantsUtils,
        SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.shared_preferences);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        int count = preferenceScreen.getPreferenceCount(); // 2 categories

        for (int i = ZERO; i < count; i++) {
            Preference category = preferenceScreen.getPreference(i);
            if (category instanceof PreferenceCategory) {
                PreferenceCategory preferenceCategory = (PreferenceCategory) category;
                int countCategoryList = preferenceCategory.getPreferenceCount();
                for (int k = ZERO; k < countCategoryList; k++) {
                    Preference preference = ((PreferenceCategory) category).getPreference(k);
                    if (!(preference instanceof CheckBoxPreference)) {
                        String key = preference.getKey();
                        String value = sharedPreferences.getString(key, EMPTY_STRING);
                        setSummaryPreference(preference, value);
                    }
                }
            }
        }

    }

    private void setSummaryPreference(Preference preference, String value) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int index = listPreference.findIndexOfValue(value);
            if (index >= ZERO) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            String newValue = sharedPreferences.getString(key, EMPTY_STRING);
            int index = listPreference.findIndexOfValue(newValue);
            if (index >= ZERO) {
                listPreference.setSummary(listPreference.getEntries()[index]);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

}
