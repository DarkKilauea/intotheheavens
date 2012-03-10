/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.android;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author joshua
 */
public class LoadGameActivity extends ListActivity 
{
    public static final int ACTION_LOAD = 1;
    private List<HashMap<String, String>> _saveGameList = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.load_game);
        
        File saveDir = new File(getIntent().getExtras().getString("saveGamePath"));
        File[] saveGames = saveDir.listFiles(new FilenameFilter() 
        {
            public boolean accept(File dir, String filename) 
            {
                if (filename.endsWith(".sav")) return true;
                else return false;
            }
        });
        
        //Remove the .sav off the files to make them 'friendlier'
        _saveGameList = new ArrayList<HashMap<String, String>>();
        for (File file : saveGames) 
        {
            HashMap pair = new HashMap();
            pair.put("title", file.getName().replace(".sav", ""));
            
            String subTitle = getResources().getString(R.string.last_played) + " ";
            subTitle += DateUtils.isToday(file.lastModified()) ? getResources().getString(R.string.today) :  DateUtils.formatDateTime(this, file.lastModified(), DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE); 
            subTitle += " @ " + DateUtils.formatDateTime(this, file.lastModified(), DateUtils.FORMAT_SHOW_TIME);
            pair.put("subtitle", subTitle);
            
            _saveGameList.add(pair);
        }
        
        setListAdapter(new SimpleAdapter(this, _saveGameList, R.layout.save_game_row, new String[] { "title", "subtitle" }, new int[] { R.id.text1, R.id.text2 }));
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        
        File saveDir = this.getDir("savegames", MODE_PRIVATE);
        Bundle data = new Bundle();
        data.putString("Filename", saveDir.getPath() + File.separator + _saveGameList.get(position).get("title") + ".sav");
        
        Intent outIntent = new Intent();
        outIntent.putExtras(data);
        setResult(RESULT_OK, outIntent);
        finish();
    }
}
