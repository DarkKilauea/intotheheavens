/*
 * IntoTheHeavensDesktopApp.java
 */

package intotheheavensdesktop;

import org.jdesktop.application.Application;
import org.jdesktop.application.SingleFrameApplication;

/**
 * The main class of the application.
 */
public class IntoTheHeavensDesktopApp extends SingleFrameApplication 
{
    protected String[] _args = null;
    
    public String[] getArgs()
    {
        return _args;
    }
    
    @Override
    protected void initialize(String[] args)
    {
        _args = args;
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
}
