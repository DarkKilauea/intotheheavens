package net.darkkilauea.intotheheavens.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LauncherActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import net.darkkilauea.intotheheavens.GameMode.State;
import net.darkkilauea.intotheheavens.GameModeManager;
import net.darkkilauea.intotheheavens.IGameModeListener;
import net.darkkilauea.intotheheavens.ITHScript.Location;
import net.darkkilauea.intotheheavens.MainGameMode;
import net.darkkilauea.intotheheavens.WorldState;

public class MainActivity extends Activity implements View.OnKeyListener, IGameModeListener
{
    private GameModeManager _manager = null;
    private MainGameMode _mainMode = null;
    
    private boolean _saveGameActive = false;
    
    TextView _consoleView = null;
    AutoCompleteTextView _commandBox = null;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        _consoleView = (TextView)findViewById(R.id.console_textView);
        _commandBox = (AutoCompleteTextView)findViewById(R.id.command_textBox);
        
        if (savedInstanceState != null)
        {
            _saveGameActive = savedInstanceState.getBoolean("saveGameAllowed");
            _consoleView.setText(savedInstanceState.getString("consoleHistory"));
        }
        else
        {
            _saveGameActive = false;
            _consoleView.setText(R.string.welcomeMessage);
        }
        
        _consoleView.setMovementMethod(ScrollingMovementMethod.getInstance());
        _commandBox.setOnKeyListener(this);
        
        List<Object> storedData = (List<Object>)getLastNonConfigurationInstance();
        
        if (storedData == null)
        {
            _manager = new GameModeManager();

            _mainMode = new MainGameMode();
            _mainMode.registerListener(this);

            _manager.registerGameMode("Main", _mainMode);
        }
        else
        {
            _manager = (GameModeManager)storedData.get(0);
            
            _mainMode = (MainGameMode)storedData.get(1);
            _mainMode.registerListener(this);
            
            _manager.registerGameMode("Main", _mainMode);
        }
    }
    
    @Override
    protected void onStart() 
    {
        super.onStart();
        // The activity is about to become visible.
    }
    @Override
    protected void onResume() 
    {
        super.onResume();
        // The activity has become visible (it is now "resumed").
        
        _consoleView.post(new Runnable() 
        {
            public void run() 
            {
                autoScrollConsole();
            }
        });
    }
    @Override
    protected void onPause() 
    {
        super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
    }
    @Override
    protected void onStop() 
    {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    @Override
    protected void onDestroy() 
    {
        super.onDestroy();
        // The activity is about to be destroyed.
        _manager.unregisterGameMode("Main");
        _manager = null;
        
        _mainMode.unregisterListener(this);
        _mainMode = null;
        
        _consoleView = null;
        _commandBox = null;
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Save any custom state here
        outState.putBoolean("saveGameAllowed", _saveGameActive);
        outState.putString("consoleHistory", _consoleView.getText().toString());
    }
    
    @Override
    public Object onRetainNonConfigurationInstance() 
    {
        final List<Object> data = new ArrayList<Object>();
        data.add(_manager);
        data.add(_mainMode);
        
        return data;
    }

    public boolean onKey(View v, int keyCode, KeyEvent event) 
    {
        if (v == _commandBox && keyCode == KeyEvent.KEYCODE_ENTER)
        {
            if(_manager.getActiveMode() != null)
            {
                String commandText = _commandBox.getText().toString();
                if(commandText.startsWith("/") || commandText.startsWith("!"))
                {
                    commandText = commandText.substring(1, commandText.length());
                }

                if (commandText.length() == 0) return false;

                _consoleView.setText(_consoleView.getText().toString() + ">" + commandText + "\n");
                _commandBox.setText("");
                
                autoScrollConsole();
                
                _manager.getActiveMode().injectTextInput(commandText);

                return true;
            }
            else
            {
                _commandBox.setText("");
            }
        }
        
        return false;
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        // Handle item selection
        switch (item.getItemId()) 
        {
            case R.id.new_game:
                newGame();
                return true;
            case R.id.load_game:
                loadGame();
                return true;
            case R.id.save_game:
                saveGame();
                return true;
            case R.id.help:
                help();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onPrepareOptionsMenu (Menu menu)
    {
        menu.findItem(R.id.save_game).setEnabled(_saveGameActive);
        
        return true;
    }

    public void onStateChange(State state) 
    {
        //This does nothing!
    }

    public void onTextOutput(String output) 
    {
        _consoleView.setText(_consoleView.getText().toString() + output + "\n");
        autoScrollConsole();
    }

    public void onClearOutput() 
    {
        _consoleView.setText("");
        autoScrollConsole();
    }
    
    public void onLocationChange()
    {
        final File saveDir = this.getDir("savegames", MODE_PRIVATE);
        String saveFile = saveDir.getAbsolutePath() + File.separator + "autosave.sav";
        
        saveGame(saveFile);
    }
    
    protected void autoScrollConsole()
    {
        final int scrollAmount = _consoleView.getLayout().getLineTop(_consoleView.getLineCount()) - _consoleView.getHeight();
        if (scrollAmount > 0)
            _consoleView.scrollTo(0, scrollAmount);
        else
            _consoleView.scrollTo(0,0);
    }
    
    protected WorldState initializeWorld() throws IOException, Exception
    {
        WorldState world = new WorldState();
        InputStream inStream = this.getResources().openRawResource(R.raw.game01);
        world.loadLocationArchive(inStream);
        inStream.close();
        
        return world;
    }
    
    protected void newGame()
    {
        try
        {
            WorldState world = initializeWorld();
            
            Location startLocation = world.findLocation("Start");
            if(startLocation != null) world.setCurrentLocation(startLocation);
            else throw new Exception("Could not find initial location!");
            
            onClearOutput();
            
            MainGameMode mode = (MainGameMode)_manager.getMode("Main");
            mode.loadFromWorldState(world);
            
            _manager.setActiveMode("Main");
            _saveGameActive = true;
        }
        catch (Exception ex)
        {
            onTextOutput("Failed to start new game! \nException caught: " + ex.toString());
        }
    }
    
    protected void loadGame()
    {
        File saveDir = this.getDir("savegames", MODE_PRIVATE);
        Intent intent = new Intent(this, LoadGameActivity.class);
        intent.putExtra("saveGamePath", saveDir.getPath());
        
        startActivityForResult(intent, LoadGameActivity.ACTION_LOAD);
    }
    
    protected void loadGame(String filename)
    {
        try 
        {
            WorldState world = initializeWorld();

            FileInputStream stream = new FileInputStream(filename);
            if(world.loadState(stream))
            {
                onClearOutput();

                MainGameMode mode = (MainGameMode)_manager.getMode("Main");
                mode.loadFromWorldState(world);

                _manager.setActiveMode("Main");
                _saveGameActive = true;
            }
            else throw new Exception("Could not parse save file (Not a proper save or corrupted)");

            stream.close();
        }
        catch (Exception ex)
        {
            onTextOutput("Failed to load game! \nException caught: " + ex.toString());
        }
    }
    
    protected void saveGame()
    {
        File saveDir = this.getDir("savegames", MODE_PRIVATE);
        Intent intent = new Intent(this, SaveGameActivity.class);
        intent.putExtra("saveGamePath", saveDir.getPath());
        
        startActivityForResult(intent, SaveGameActivity.ACTION_SAVE);
    }
    
    protected void saveGame(String filename)
    {
        try 
        {
            FileOutputStream stream = new FileOutputStream(filename);

            MainGameMode mode = (MainGameMode)_manager.getMode("Main");
            mode.getWorldState().saveState(stream);

            stream.close();
        } 
        catch (Exception ex)
        {
            onTextOutput("Failed to save game! \nException caught: " + ex.toString());
        }
    }
    
    protected void help()
    {
        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
    
    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK)
        {
            switch (requestCode)
            {
                case LoadGameActivity.ACTION_LOAD:
                    loadGame(data.getStringExtra("Filename"));
                    break;
                case SaveGameActivity.ACTION_SAVE:
                    saveGame(data.getStringExtra("Filename"));
                    break;
                default:
                    break;
            }
        }
    }
}
