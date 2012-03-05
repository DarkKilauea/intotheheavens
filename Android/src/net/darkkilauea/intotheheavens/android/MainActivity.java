package net.darkkilauea.intotheheavens.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.main);
        
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        
        if (savedInstanceState != null)
        {
            _saveGameActive = savedInstanceState.getBoolean("saveGameAllowed");
            consoleView.setText(savedInstanceState.getString("consoleHistory"));
        }
        else
        {
            _saveGameActive = false;
            consoleView.setText(R.string.welcomeMessage);
        }
        
        consoleView.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        AutoCompleteTextView commandBox = (AutoCompleteTextView)findViewById(R.id.command_textBox);
        commandBox.setOnKeyListener(this);
        
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
        
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        consoleView.post(new Runnable() 
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
    }
    
    @Override
    protected void onSaveInstanceState (Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //Save any custom state here
        outState.putBoolean("saveGameAllowed", _saveGameActive);
        
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        outState.putString("consoleHistory", consoleView.getText().toString());
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
        AutoCompleteTextView commandBox = (AutoCompleteTextView)findViewById(R.id.command_textBox);
        if (v == commandBox && keyCode == KeyEvent.KEYCODE_ENTER)
        {
            if(_manager.getActiveMode() != null)
            {
                TextView consoleView = (TextView)findViewById(R.id.console_textView);

                String commandText = commandBox.getText().toString();
                if(commandText.startsWith("/") || commandText.startsWith("!"))
                {
                    commandText = commandText.substring(1, commandText.length());
                }

                if (commandText.length() == 0) return false;

                consoleView.setText(consoleView.getText().toString() + ">" + commandText + "\n");
                commandBox.setText("");
                
                autoScrollConsole();
                
                _manager.getActiveMode().injectTextInput(commandText);

                return true;
            }
            else
            {
                commandBox.setText("");
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
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        consoleView.setText(consoleView.getText().toString() + output + "\n");
        
        autoScrollConsole();
    }

    public void onClearOutput() 
    {
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        consoleView.setText("");
        
        autoScrollConsole();
    }
    
    protected void autoScrollConsole()
    {
        TextView consoleView = (TextView)findViewById(R.id.console_textView);
        
        final int scrollAmount = consoleView.getLayout().getLineTop(consoleView.getLineCount()) - consoleView.getHeight();
        if (scrollAmount > 0)
            consoleView.scrollTo(0, scrollAmount);
        else
            consoleView.scrollTo(0,0);
    }
    
    protected void newGame()
    {
        try
        {
            WorldState world = new WorldState();
            InputStream inStream = this.getResources().openRawResource(R.raw.game01);
            world.loadLocationArchive(inStream);
            inStream.close();
            
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
        //TODO: Load list of saved games
        
    }
    
    protected void saveGame()
    {
        
    }
}