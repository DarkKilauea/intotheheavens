/*
 * IntoTheHeavensDesktopApp.java
 */

package intotheheavensdesktop;

import java.io.File;
import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class IntoTheHeavensDesktopApp extends SingleFrameApplication 
{
    private String[] _args = null;
    private String _contentDir = null;
    private String _saveGameDir = null;
    private String _locationDir = null;
    private String _soundDir = null;
    private String _musicDir = null;
    
    public String[] getArgs()
    {
        return _args;
    }
    
    public String getContentDirectory()
    {
        return _contentDir;
    }
    
    public String getSaveGameDirectory()
    {
        return _saveGameDir;
    }
    
    public String getLocationDirectory()
    {
        return _locationDir;
    }
    
    public String getSoundDirectory()
    {
        return _soundDir;
    }
    
    public String getMusicDirectory()
    {
        return _musicDir;
    }
    
    @Override
    protected void initialize(String[] args)
    {
        _args = args;
        
        _contentDir = "base" + File.separator;
        for(int i = 0; i<args.length; i++)
        {
            if(args[i].endsWith("gamedir"))
            {
                _contentDir = args[i + 1];
                break;
            }
        }
        
        setContentDirectory(_contentDir);
    }
    
    /**
     * At startup create and show the main frame of the application.
     */
    @Override protected void startup() 
    {
        show(new IntoTheHeavensDesktopView(this));
    }

    /**
     * This method is to initialize the specified window by injecting resources.
     * Windows shown in our application come fully initialized from the GUI
     * builder, so this additional configuration is not needed.
     */
    @Override protected void configureWindow(java.awt.Window root) 
    {
    }

    /**
     * A convenient static getter for the application instance.
     * @return the instance of IntoTheHeavensDesktopApp
     */
    public static IntoTheHeavensDesktopApp getApplication() 
    {
        return Application.getInstance(IntoTheHeavensDesktopApp.class);
    }

    /**
     * Main method launching the application.
     */
    public static void main(String[] args) 
    {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Ethershard Castle");
                
        launch(IntoTheHeavensDesktopApp.class, args);
    }
    
    public void setContentDirectory(String dir)
    {
        String userHomeDir = System.getProperty("user.home");
        
        _contentDir = dir + File.separator;
        _saveGameDir = userHomeDir + File.separator + ".ethershardcastle" + File.separator + new File(_contentDir).getName() + File.separator + "savegames" + File.separator;
        _locationDir = _contentDir + File.separator + "locations" + File.separator;
        _soundDir = _contentDir + File.separator + "sounds" + File.separator;
        _musicDir = _contentDir + File.separator + "music" + File.separator;
        
        new File(_saveGameDir).mkdirs();
    }
}
