/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package net.darkkilauea.intotheheavens.android;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
public class SaveGameActivity extends ListActivity implements View.OnClickListener
{
    public static final int ACTION_SAVE = 2;
    private List<HashMap<String, String>> _saveGameList = null;
    
    private Button _saveButton = null;
    private EditText _saveTextBox = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.save_game);
        
        _saveButton = (Button)findViewById(R.id.save_button);
        _saveTextBox = (EditText)findViewById(R.id.save_textbox);
        
        _saveButton.setOnClickListener(this);
        
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
            pair.put("subtitle", (DateUtils.isToday(file.lastModified()) ? getResources().getString(R.string.today) :  DateUtils.formatDateTime(this, file.lastModified(), DateUtils.FORMAT_SHOW_WEEKDAY | DateUtils.FORMAT_SHOW_DATE)) + 
                                 " @ " + DateUtils.formatDateTime(this, file.lastModified(), DateUtils.FORMAT_SHOW_TIME));
            
            _saveGameList.add(pair);
        }
        
        setListAdapter(new SimpleAdapter(this, _saveGameList, R.layout.save_game_row, new String[] { "title", "subtitle" }, new int[] { R.id.text1, R.id.text2 }));
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        _saveButton = null;
        _saveTextBox = null;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        super.onListItemClick(l, v, position, id);
        
        finishWithSaveName(_saveGameList.get(position).get("title"));
    }

    public void onClick(View v) 
    {
        if (v == _saveButton)
        {
            final String cleanSaveName = _saveTextBox.getText().toString()
                    .replace(File.separator, "")
                    .replace(File.pathSeparator, "")
                    .replace(".", "")
                    .trim();
            
            if (cleanSaveName.length() > 0)  
            {
                boolean found = false;
                for (HashMap<String, String> item : _saveGameList) 
                {
                    if (item.containsValue(cleanSaveName))
                    {
                        found = true;
                        break;
                    }
                }
                
                if (found)
                {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle(R.string.warning_duplicate_save_title);
                    dialog.setMessage(R.string.warning_duplicate_save_text);
                    dialog.setPositiveButton(R.string.yes, new OnClickListener() 
                    {
                        public void onClick(DialogInterface arg0, int arg1) 
                        {
                            arg0.dismiss();
                            finishWithSaveName(cleanSaveName);
                        }
                    });
                    dialog.setNegativeButton(R.string.no, new OnClickListener() 
                    {
                        public void onClick(DialogInterface arg0, int arg1) 
                        {
                            arg0.cancel();
                        }
                    });
                    dialog.show();
                }
                else finishWithSaveName(cleanSaveName);
            }
        }
    }
    
    protected void finishWithSaveName(String name)
    {
        File saveDir = this.getDir("savegames", MODE_PRIVATE);
        Bundle data = new Bundle();
        data.putString("Filename", saveDir.getPath() + File.separator + name + ".sav");
        
        Intent outIntent = new Intent();
        outIntent.putExtras(data);
        setResult(RESULT_OK, outIntent);
        finish();
    }
}
