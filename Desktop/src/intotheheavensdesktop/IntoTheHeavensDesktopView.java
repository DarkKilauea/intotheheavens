/*
 * IntoTheHeavensDesktopView.java
 */

package intotheheavensdesktop;

import intotheheavensdesktop.sound.AudioPlayer;
import java.io.IOException;
import net.darkkilauea.intotheheavens.GameMode.State;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import net.darkkilauea.intotheheavens.GameModeManager;
import net.darkkilauea.intotheheavens.IGameModeListener;
import net.darkkilauea.intotheheavens.ITHScript.Location;
import net.darkkilauea.intotheheavens.MainGameMode;
import net.darkkilauea.intotheheavens.WorldState;

/**
 * The application's main frame.
 */
public class IntoTheHeavensDesktopView extends FrameView implements IGameModeListener
{
    private GameModeManager _manager = new GameModeManager();
    private MainGameMode _mainMode = null;
    private String _contentDir = null;
    private String _saveGameDir = null;
    private String _locationDir = null;
    private String _soundDir = null;
    private String _musicDir = null;
    private Timer _audioUpdateTimer = null;
    private Map<Integer, AudioPlayer> _audioSources = new HashMap<Integer, AudioPlayer>();
    private int _lastAudioId;
    private float _masterVolume = 1.0f;
    private float _musicVolume = 1.0f;
    private float _soundEffectVolume = 1.0f;
    
    public IntoTheHeavensDesktopView(SingleFrameApplication app) 
    {
        super(app);

        initComponents();
        setupStatusBar();
        _audioUpdateTimer = new Timer(200, new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                for (AudioPlayer source : _audioSources.values()) 
                {
                    try
                    {
                        if (source.getState() == AudioPlayer.State.PLAYING)
                        {
                            source.update();
                        }
                    } 
                    catch (IOException ex)
                    {
                        try { source.stop(); }
                        catch (Exception ex2) { }
                    }
                }
            }
        });
        _audioUpdateTimer.setRepeats(true);
        _audioUpdateTimer.start();
        
        IntoTheHeavensDesktopApp application = IntoTheHeavensDesktopApp.getApplication();
        String[] args = application.getArgs();
        
        _contentDir = "base" + File.separator;
        for(int i = 0; i<args.length; i++)
        {
            if(args[i].endsWith("gamedir"))
            {
                _contentDir = args[i + 1];
                break;
            }
        }
        
        String userHomeDir = System.getProperty("user.home");
        
        _saveGameDir = userHomeDir + File.separator + ".intotheheavens" + File.separator + "savegames" + File.separator;
        _locationDir = _contentDir + File.separator + "locations" + File.separator;
        _soundDir = _contentDir + File.separator + "sounds" + File.separator;
        _musicDir = _contentDir + File.separator + "music" + File.separator;
        
        new File(_saveGameDir).mkdirs();
        
        _mainMode = new MainGameMode();
        _mainMode.setListener(this);
        
        _manager.registerGameMode("Main", _mainMode);
        
        consoleTextArea.setText(this.getResourceMap().getString("welcomeMessage") + "\n");
        saveGameMenuItem.setEnabled(false);
        archiveMenuItem.setEnabled(false);
    }
    
    private void setupStatusBar()
    {
        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) 
        {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) 
            {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) 
            {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) 
                {
                    if (!busyIconTimer.isRunning()) 
                    {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } 
                else if ("done".equals(propertyName)) 
                {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } 
                else if ("message".equals(propertyName)) 
                {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } 
                else if ("progress".equals(propertyName)) 
                {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        consoleTextArea = new javax.swing.JTextArea();
        commandTextField = new javax.swing.JTextField();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        newGameMenuItem = new javax.swing.JMenuItem();
        loadGameMenuItem = new javax.swing.JMenuItem();
        saveGameMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        exitMenuItem = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        archiveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(intotheheavensdesktop.IntoTheHeavensDesktopApp.class).getContext().getResourceMap(IntoTheHeavensDesktopView.class);
        consoleTextArea.setBackground(resourceMap.getColor("consoleTextArea.background")); // NOI18N
        consoleTextArea.setColumns(20);
        consoleTextArea.setEditable(false);
        consoleTextArea.setForeground(resourceMap.getColor("consoleTextArea.foreground")); // NOI18N
        consoleTextArea.setLineWrap(true);
        consoleTextArea.setRows(5);
        consoleTextArea.setWrapStyleWord(true);
        consoleTextArea.setName("consoleTextArea"); // NOI18N
        jScrollPane1.setViewportView(consoleTextArea);

        commandTextField.setText(resourceMap.getString("commandTextField.text")); // NOI18N
        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(intotheheavensdesktop.IntoTheHeavensDesktopApp.class).getContext().getActionMap(IntoTheHeavensDesktopView.class, this);
        commandTextField.setAction(actionMap.get("submitCommand")); // NOI18N
        commandTextField.setName("commandTextField"); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(commandTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(commandTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        newGameMenuItem.setAction(actionMap.get("newGameAction")); // NOI18N
        newGameMenuItem.setName("newGameMenuItem"); // NOI18N
        fileMenu.add(newGameMenuItem);

        loadGameMenuItem.setAction(actionMap.get("loadGameAction")); // NOI18N
        loadGameMenuItem.setText(resourceMap.getString("loadGameMenuItem.text")); // NOI18N
        loadGameMenuItem.setName("loadGameMenuItem"); // NOI18N
        fileMenu.add(loadGameMenuItem);

        saveGameMenuItem.setAction(actionMap.get("saveGameAction")); // NOI18N
        saveGameMenuItem.setText(resourceMap.getString("saveGameMenuItem.text")); // NOI18N
        saveGameMenuItem.setName("saveGameMenuItem"); // NOI18N
        fileMenu.add(saveGameMenuItem);
        fileMenu.add(jSeparator1);

        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        toolsMenu.setText(resourceMap.getString("toolsMenu.text")); // NOI18N
        toolsMenu.setName("toolsMenu"); // NOI18N

        archiveMenuItem.setAction(actionMap.get("archiveScripts")); // NOI18N
        archiveMenuItem.setText(resourceMap.getString("archiveMenuItem.text")); // NOI18N
        archiveMenuItem.setName("archiveMenuItem"); // NOI18N
        toolsMenu.add(archiveMenuItem);

        menuBar.add(toolsMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 263, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem archiveMenuItem;
    private javax.swing.JTextField commandTextField;
    private javax.swing.JTextArea consoleTextArea;
    private javax.swing.JMenuItem exitMenuItem;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenuItem loadGameMenuItem;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem newGameMenuItem;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JMenuItem saveGameMenuItem;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JMenu toolsMenu;
    // End of variables declaration//GEN-END:variables

    private Timer messageTimer;
    private Timer busyIconTimer;
    private Icon idleIcon;
    private Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;

    public void onStateChange(State state) 
    {
        
    }

    public void onTextOutput(String output) 
    {
        consoleTextArea.setText(consoleTextArea.getText() + output + "\n");
    }

    public void onClearOutput() 
    {
        consoleTextArea.setText("");
    }
    
    public void onLocationChange()
    {
        String saveFile = new File(_saveGameDir).getAbsolutePath() + File.separator + "autosave.sav";
        try 
        {
            FileOutputStream stream = new FileOutputStream(saveFile);

            MainGameMode mode = (MainGameMode)_manager.getMode("Main");
            mode.getWorldState().saveState(stream);

            stream.close();
        } 
        catch (Exception ex)
        {
            onTextOutput("Failed to auto save game! \nException caught: " + ex.toString());
        }
    }
    
    public int onStartAudio(String filename) 
    {
        try 
        {
            File soundFile = new File(_soundDir + filename);
            File musicFile = new File(_musicDir + filename);
            
            AudioPlayer source = null;
            if (soundFile.exists())
            {
                source = new AudioPlayer(soundFile);
                source.setVolume(_masterVolume * _soundEffectVolume);
            }
            else if (musicFile.exists()) 
            {
                source = new AudioPlayer(musicFile);
                source.setVolume(_masterVolume * _musicVolume);
            }
            
            if (source != null)
            {
                source.play();
                _audioSources.put(++_lastAudioId, source);
            }
            else return 0;
        } 
        catch (Exception ex) 
        {
            onTextOutput("Failed to start audio! \nException caught: " + ex.toString());
            return 0;
        } 
        
        return _lastAudioId;
    }
    
    public void onResumeAudio(int audioId)
    {
        try 
        {
            _audioSources.get(audioId).play();
        }
        catch (Exception ex) 
        {
            onTextOutput("Failed to resume audio! \nException caught: " + ex.toString());
        }
    }

    public void onPauseAudio(int audioId) 
    {
        _audioSources.get(audioId).pause();
    }

    public void onStopAudio(int audioId) 
    {
        AudioPlayer source = _audioSources.get(audioId);
        try { source.stop(); }
        catch (Exception ex) {}
        
        _audioSources.remove(audioId);
    }
    
    private void stopAllPlayingAudio()
    {
        for (AudioPlayer source : _audioSources.values()) 
        { 
            try { source.stop(); }
            catch (Exception ex) {}
        }
        
        _audioSources.clear();
    }

    @Action
    public void showAboutBox() 
    {
        if (aboutBox == null) 
        {
            JFrame mainFrame = IntoTheHeavensDesktopApp.getApplication().getMainFrame();
            aboutBox = new IntoTheHeavensDesktopAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        IntoTheHeavensDesktopApp.getApplication().show(aboutBox);
    }
    
    @Action
    public void newGameAction() 
    {
        try
        {
            stopAllPlayingAudio();
            WorldState world = new WorldState();
            
            File locDir = new File(_locationDir);
            if (!locDir.exists()) throw new Exception("Could not find location directory!");
            world.loadLocations(locDir);
            
            Location startLocation = world.findLocation("Start");
            if(startLocation != null) world.setCurrentLocation(startLocation);
            else throw new Exception("Could not find initial location!");
            
            onClearOutput();
            
            MainGameMode mode = (MainGameMode)_manager.getMode("Main");
            mode.loadFromWorldState(world);
            
            _manager.setActiveMode("Main");
            saveGameMenuItem.setEnabled(true);
            archiveMenuItem.setEnabled(true);
        }
        catch (Exception ex)
        {
            onTextOutput("Failed to start new game! \nException caught: " + ex.toString());
        }
    }

    @Action
    public void loadGameAction() 
    {
        JFrame mainFrame = IntoTheHeavensDesktopApp.getApplication().getMainFrame();
        JFileChooser dialog = getSaveFileChooser();
        
        if(dialog.showOpenDialog(mainFrame) == JFileChooser.APPROVE_OPTION)
        {
            File loadFile = dialog.getSelectedFile();
            if(!loadFile.getName().endsWith(".sav"))
            {
                loadFile = new File(loadFile.getPath() + ".sav");
            }
            
            try
            {
                stopAllPlayingAudio();
                WorldState world = new WorldState();
                
                File locDir = new File(_locationDir);
                if (!locDir.exists()) throw new Exception("Could not find location directory!");
                world.loadLocations(locDir);

                FileInputStream stream = new FileInputStream(loadFile);
                if(world.loadState(stream))
                {
                    onClearOutput();

                    MainGameMode mode = (MainGameMode)_manager.getMode("Main");
                    mode.loadFromWorldState(world);

                    _manager.setActiveMode("Main");
                    saveGameMenuItem.setEnabled(true);
                    archiveMenuItem.setEnabled(true);
                }
                else throw new Exception("Could not parse save file (Not a proper save or corrupted)");
                
                stream.close();
            }
            catch (Exception ex)
            {
                onTextOutput("Failed to load game! \nException caught: " + ex.toString());
            }
        }
    }
    
    @Action
    public void saveGameAction()
    {
        JFrame mainFrame = IntoTheHeavensDesktopApp.getApplication().getMainFrame();
        JFileChooser dialog = getSaveFileChooser();
        
        if(dialog.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION)
        {
            File saveFile = dialog.getSelectedFile();
            if(!saveFile.getName().endsWith(".sav"))
            {
                saveFile = new File(saveFile.getPath() + ".sav");
            }
            
            try 
            {
                FileOutputStream stream = new FileOutputStream(saveFile);
                
                MainGameMode mode = (MainGameMode)_manager.getMode("Main");
                mode.getWorldState().saveState(stream);
                
                stream.close();
            } 
            catch (Exception ex)
            {
                onTextOutput("Failed to save game! \nException caught: " + ex.toString());
            }
        }
    }

    @Action
    public void submitCommand() 
    {
        if(_manager.getActiveMode() != null)
        {
            String commandText = commandTextField.getText();
            if(commandText.startsWith("/") || commandText.startsWith("!"))
            {
                commandText = commandText.substring(1, commandText.length());
            }

            consoleTextArea.setText(consoleTextArea.getText() + "> " + commandText + "\n");
            commandTextField.setText(null);
        
            _manager.getActiveMode().injectTextInput(commandText);
        }
        else
        {
            commandTextField.setText(null);
        }
    }
    
    private JFileChooser getSaveFileChooser()
    {
        JFileChooser dialog = new JFileChooser(new File(_saveGameDir).getAbsolutePath());
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.setMultiSelectionEnabled(false);
        dialog.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) 
            {
                if(f.isDirectory() && !f.isHidden()) return true;
                else if(f.getName().endsWith(".sav"))
                {
                    return true;
                }
                else return false;
            }

            @Override
            public String getDescription() 
            {
                return "Save Game Files (.sav)";
            }
        });
        
        return dialog;
    }

    @Action
    public void archiveScripts() 
    {
        JFrame mainFrame = IntoTheHeavensDesktopApp.getApplication().getMainFrame();
        JFileChooser dialog = new JFileChooser(new File(_locationDir).getAbsolutePath());
        dialog.setAcceptAllFileFilterUsed(false);
        dialog.setMultiSelectionEnabled(false);
        dialog.setFileFilter(new FileFilter() {

            @Override
            public boolean accept(File f) 
            {
                if(f.isDirectory() && !f.isHidden()) return true;
                else if(f.getName().endsWith(".arc"))
                {
                    return true;
                }
                else return false;
            }

            @Override
            public String getDescription() 
            {
                return "Archived Location Files (.arc)";
            }
        });
        
        if(dialog.showSaveDialog(mainFrame) == JFileChooser.APPROVE_OPTION)
        {
            File archiveFile = dialog.getSelectedFile();
            if(!archiveFile.getName().endsWith(".arc"))
            {
                archiveFile = new File(archiveFile.getPath() + ".arc");
            }
            
            try 
            {
                FileOutputStream stream = new FileOutputStream(archiveFile);
                
                MainGameMode mode = (MainGameMode)_manager.getMode("Main");
                mode.getWorldState().archiveLocations(stream);
                
                stream.close();
            } 
            catch (Exception ex)
            {
                onTextOutput("Failed to archive locations! \nException caught: " + ex.toString());
            }
        }
    }
}
