package com.fai.minhasfinancas;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class SettingsActivity extends SherlockPreferenceActivity implements OnSharedPreferenceChangeListener {
	
	private static int prefsInt = R.xml.preferences;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Assim consigo rodar usando as sharedPreferences em todas as versões
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
        	// Leio os valores para preencher os summaries
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(SettingsActivity.this);
        	AddResourceApiLessThan11();
        } else {
        	AddResourceApi11AndGreater();
        }          
    }
    
    @Deprecated
    @Override
    protected void onResume() {
    	super.onResume();
    	if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
    		getPreferenceScreen().getSharedPreferences()
        		.registerOnSharedPreferenceChangeListener(this);
    	}
    };
       
    @SuppressWarnings("deprecation")
    protected void AddResourceApiLessThan11()
    {
        addPreferencesFromResource(prefsInt);
        
        final SharedPreferences prefs;
        
     // Leio os valores para preencher os summaries
        prefs = PreferenceManager
                .getDefaultSharedPreferences(SettingsActivity.this);

        EditTextPreference edit = (EditTextPreference) findPreference("Edit");
        if(prefs
                .getString("Edit", "").equals("")){
        	edit.setSummary("1 " + SettingsActivity.this
                    .getResources().getString(R.string.pref_edit_summary));
        }else{
        	edit
            .setSummary(prefs.getString("Edit", "1 " + SettingsActivity.this
                    .getResources().getString(R.string.pref_edit_summary)));
        }

        ListPreference List = (ListPreference) findPreference("List");
        if (List.getValue() == null || List.getValue().equals("")) {
            List.setValueIndex(0);
        }
        List.setSummary(prefs.getString("List", SettingsActivity.this
                .getResources().getString(R.string.list_summary1)));
    }
    
    @TargetApi(11)
    public static class PrefFragment extends PreferenceFragment {

        private OnSharedPreferenceChangeListener listener;
        private SharedPreferences prefs;

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(SettingsActivity.prefsInt);
            
            // Leio os valores para preencher os summaries
            prefs = PreferenceManager
                    .getDefaultSharedPreferences(getActivity());

            EditTextPreference edit = (EditTextPreference) findPreference("Edit");
            if(prefs
                    .getString("Edit", "").equals("")){
            	edit.setSummary("1 " + getActivity()
                        .getResources().getString(R.string.pref_edit_summary));
            }else{
            	edit
                .setSummary(prefs.getString("Edit", "1 " + getActivity()
                        .getResources().getString(R.string.pref_edit_summary)));
            }

        }
    }

    @TargetApi(11)
    protected void AddResourceApi11AndGreater() {
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new PrefFragment()).commit();
    }
    
    //Corrige um bug no backgroud no caso de sub-screen com as versoes antigas
    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference)
    {
        super.onPreferenceTreeClick(preferenceScreen, preference);
        if (preference!=null)
            if (preference instanceof PreferenceScreen)
                if (((PreferenceScreen)preference).getDialog()!=null)
                    ((PreferenceScreen)preference).getDialog().getWindow().getDecorView().setBackgroundDrawable(this.getWindow().getDecorView().getBackground().getConstantState().newDrawable());
        return false;
    }
       
    @Deprecated
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		if (SettingsActivity.this != null) {
            // Troco os sumários
            if (key.equalsIgnoreCase("Edit")) {
                EditTextPreference edit = (EditTextPreference) findPreference("Edit");
                
                if(sharedPreferences
                        .getString("Edit", "").equals("")){
                	edit.setSummary("1 " + SettingsActivity.this
                            .getResources().getString(R.string.pref_edit_summary));
                } else {
                	edit.setSummary(sharedPreferences
                        .getString("Edit", "1 " + SettingsActivity.this
                                .getResources().getString(R.string.pref_edit_summary)));
                }
            } else if (key.equalsIgnoreCase("List")) {
                ListPreference List = (ListPreference) findPreference("List");
                List.setSummary(prefs.getString(
                        "List",
                        SettingsActivity.this.getResources().getString(
                        R.string.list_summary1)));
                Intent resultIntent = new Intent();
                resultIntent.putExtra("update", "update");
                SettingsActivity.this.setResult(Activity.RESULT_OK,
                        resultIntent);
            } 
        }
	}

}
