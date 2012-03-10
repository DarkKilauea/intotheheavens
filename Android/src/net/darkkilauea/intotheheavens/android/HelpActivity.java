/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.android;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.WebView;

/**
 *
 * @author joshua
 */
public class HelpActivity extends Activity 
{
    private WebView _webView = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.help);
        
        Resources res = getResources();
        String html = res.getString(R.string.help_html)
                    .replace("{{app_name}}", res.getString(R.string.app_name))
                    .replace("{{app_version}}", res.getString(R.string.app_version))
                    .replace("{{app_vender}}", res.getString(R.string.app_vender))
                    .replace("{{app_website}}", res.getString(R.string.app_website));
        
        _webView = (WebView)findViewById(R.id.help_webview);
        _webView.loadData(html, "text/html", "utf-8");
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        
        _webView = null;
    }
}
