/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StorySelectionDialog.java
 *
 * Created on Aug 28, 2012, 7:58:22 PM
 */
package intotheheavensdesktop;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author joshua
 */
public class StorySelectionDialog extends javax.swing.JDialog 
{
    private List<File> _storyList = new ArrayList<File>();
    
    /** Creates new form StorySelectionDialog */
    public StorySelectionDialog(java.awt.Frame parent, boolean modal) 
    {
        super(parent, modal);
        initComponents();
        
        IntoTheHeavensDesktopApp app = IntoTheHeavensDesktopApp.getApplication();
        File currentDir = new File(app.getContentDirectory());
        
        _storyList = scanDirectory(new File("."));
        
        modList.setListData(_storyList.toArray());
        modList.addListSelectionListener(new ListSelectionListener() {
            
            public void valueChanged(ListSelectionEvent e) 
            {
                File selection = (File)modList.getSelectedValue();
                loadStoryDetails(selection);
            }
        });
        modList.setSelectedValue(currentDir, true);
        
        loadStoryDetails(currentDir);
    }
    
    private List<File> scanDirectory(File dir)
    {
        List<File> output = new ArrayList<File>();
        
        for (File file : dir.listFiles()) 
        {
            if (file.isDirectory())
            {
                output.addAll(scanDirectory(file));
            }
            else if (file.isFile() && file.getName().equals("info.txt"))
            {
                output.add(dir);
            }
        }   
        
        return output;
    }
    
    private void loadStoryDetails(File storyDir)
    {
        if (storyDir.isDirectory())
        {
            try 
            {
                File infoFile = new File(storyDir.getAbsolutePath() + File.separator + "info.txt");
                
                FileInputStream input = new FileInputStream(infoFile);
                Properties infoProp = new Properties();
                infoProp.load(input);
                
                nameField.setText(infoProp.getProperty("Title"));
                authorField.setText(infoProp.getProperty("Author"));
                versionField.setText(infoProp.getProperty("Version"));
                descriptionArea.setText(infoProp.getProperty("Description"));
                
                nameField.setEnabled(true);
                authorField.setEnabled(true);
                versionField.setEnabled(true);
                descriptionArea.setEnabled(true);
                
                publishButton.setEnabled(true);
                removeButton.setEnabled(true);
                activateButton.setEnabled(true);
                
                input.close();
            } 
            catch (Exception ex) 
            {
                nameField.setText("");
                authorField.setText("");
                versionField.setText("");
                descriptionArea.setText(ex.toString());
                
                nameField.setEnabled(false);
                authorField.setEnabled(false);
                versionField.setEnabled(false);
                descriptionArea.setEnabled(false);
                
                publishButton.setEnabled(false);
                removeButton.setEnabled(false);
                activateButton.setEnabled(false);
            }
        }
        else if (storyDir.getName().endsWith(".sty"))
        {
            
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        nameField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        descriptionArea = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        publishButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        activateButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        authorField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        versionField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        newModButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        modList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(intotheheavensdesktop.IntoTheHeavensDesktopApp.class).getContext().getResourceMap(StorySelectionDialog.class);
        setTitle(resourceMap.getString("Manage Stories.title")); // NOI18N
        setName("Manage Stories"); // NOI18N

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setName("jSplitPane1"); // NOI18N

        jPanel1.setName("jPanel1"); // NOI18N

        nameField.setText(resourceMap.getString("nameField.text")); // NOI18N
        nameField.setName("nameField"); // NOI18N

        jScrollPane2.setName("jScrollPane2"); // NOI18N

        descriptionArea.setColumns(20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setRows(5);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setName("descriptionArea"); // NOI18N
        jScrollPane2.setViewportView(descriptionArea);

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        publishButton.setText(resourceMap.getString("publishButton.text")); // NOI18N
        publishButton.setName("publishButton"); // NOI18N

        removeButton.setText(resourceMap.getString("removeButton.text")); // NOI18N
        removeButton.setName("removeButton"); // NOI18N

        activateButton.setText(resourceMap.getString("activateButton.text")); // NOI18N
        activateButton.setName("activateButton"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        authorField.setText(resourceMap.getString("authorField.text")); // NOI18N
        authorField.setName("authorField"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        versionField.setText(resourceMap.getString("versionField.text")); // NOI18N
        versionField.setName("versionField"); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(publishButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 144, Short.MAX_VALUE)
                        .addComponent(activateButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(authorField, javax.swing.GroupLayout.PREFERRED_SIZE, 245, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(81, 81, 81))
                            .addComponent(versionField, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)))
                    .addComponent(jLabel2)
                    .addComponent(nameField, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nameField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(authorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(versionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(publishButton)
                    .addComponent(removeButton)
                    .addComponent(activateButton))
                .addContainerGap())
        );

        jSplitPane1.setRightComponent(jPanel1);

        jPanel2.setName("jPanel2"); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(128, 388));

        newModButton.setText(resourceMap.getString("newModButton.text")); // NOI18N
        newModButton.setName("newModButton"); // NOI18N

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        modList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        modList.setName("modList"); // NOI18N
        jScrollPane1.setViewportView(modList);

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
                            .addComponent(newModButton, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(newModButton, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane1.setLeftComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 595, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton activateButton;
    private javax.swing.JTextField authorField;
    private javax.swing.JTextArea descriptionArea;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JList modList;
    private javax.swing.JTextField nameField;
    private javax.swing.JButton newModButton;
    private javax.swing.JButton publishButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JTextField versionField;
    // End of variables declaration//GEN-END:variables
}
