/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.android;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 *
 * @author joshua
 */
public class SettingsActivity extends PreferenceActivity 
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) 
    {
        super.onCreate(icicle);
        
        addPreferencesFromResource(R.xml.settings);
    }
}
