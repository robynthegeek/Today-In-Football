package com.robynandcory.goodnewseveryone;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class NewsPreferenceFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreatePreferences(Bundle bundle, String string) {
            //get the Preferences from teh settings_activity XML file
            addPreferencesFromResource(R.xml.settings_activity);

            Preference numberOfStories = findPreference(getString(R.string.settings_number_of_stories_key));
            updateSettingsSummary(numberOfStories);
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            preference.setSummary(stringValue);
            return true;
        }

        private void updateSettingsSummary(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = sharedPreferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }

}
