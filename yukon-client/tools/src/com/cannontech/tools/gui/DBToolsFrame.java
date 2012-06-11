package com.cannontech.tools.gui;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileWriter;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JRadioButton;
import javax.swing.filechooser.FileFilter;

import com.cannontech.clientutils.popup.PopUpMenuShower;
import com.cannontech.dbconverter.converter.DBConverter;
import com.cannontech.dbtools.DBCompare.DBCompare;
import com.cannontech.dbtools.image.ImageInserter;
import com.cannontech.dbtools.tools.ModifyConstraints;
import com.cannontech.dbtools.updater.DBUpdater;

/**
 * This is just a GUI interface for all tools that need an ouput panel
 * Creation date: (7/11/2001 9:49:29 AM)
 * @author: Eric Schmit
 */
class DBToolsFrame extends javax.swing.JFrame implements IMessageFrame, java.awt.event.ActionListener, javax.swing.event.PopupMenuListener 
{
	private static final String DEF_PATH =
		System.getProperty("user.dir") + IRunnableDBTool.FS;
		//"c:" + FS + "yukon" + FS + "client" + FS + "export" + FS;

	/** 
	 * All the possible tools available for use, do not change the order of this!
	 * Add any new items to the end of the list.
	 */ 
	public static final IRunnableDBTool[] ALL_TOOLS =
	{
		new DBUpdater(),
		new DBConverter(),
		new ModifyConstraints(),
		new ImageInserter(),
		new DBCompare()
	};
	
	private TextMsgPanePopUp textMsgPanePopUp = null;
	private ButtonGroup buttGroup = new ButtonGroup();
	private javax.swing.JPanel ivjMainPanel = null;
	private javax.swing.JTextField ivjPathField = null;
	private javax.swing.JButton ivjStartButton = null;
	private javax.swing.JPanel ivjButtonPanel = null;
	//private String thePath = DEF_PATH;
	private javax.swing.JFileChooser chooser = null;
	private Vector ourOutput = null;
	private javax.swing.JScrollPane ivjOutputScrollPane = null;
	private javax.swing.JTextArea ivjMessageArea = null;
	private Vector outputVector = null;
	private boolean gonnaPrint = false;
	private javax.swing.JButton ivjBrowseButton = null;	
	private javax.swing.JButton ivjBrowseXMLButton = null;    
    private javax.swing.JButton ivjSaveButton = null;    
    private javax.swing.JLabel ivjJLabelMsgs = null;
	private javax.swing.JLabel ivjJLabelOption = null;
	private javax.swing.JPanel ivjJPanelTools = null;
	private javax.swing.JRadioButton ivjJRadioButton0 = null;
	private javax.swing.JRadioButton ivjJRadioButton1 = null;
	private javax.swing.JRadioButton ivjJRadioButton2 = null;
	private javax.swing.JRadioButton ivjJRadioButton3 = null;
	private javax.swing.JRadioButton ivjJRadioButton4 = null;
	private javax.swing.JRadioButton ivjJRadioButton5 = null;
	private java.awt.FlowLayout ivjJPanelToolsFlowLayout = null;

/**
 * DBToolsFrame constructor comment.
 */
public DBToolsFrame()
{
	super();
	initialize();
}


/**
 * DBToolsFrame constructor comment.
 * @param title java.lang.String
 */
public DBToolsFrame(String title) 
{
	super(title);
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */

public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getStartButton()) 
		connEtoC1(e);
	if (e.getSource() == getBrowseButton()) 
		connEtoC2(e);
	if (e.getSource() == getBrowseXMLButton()) 
        connEtoC3(e);
    if (e.getSource() == getSaveButton()) 
	    connEtoC4(e);

}


//=======================================================================================
//Creation date: (7/11/2001 2:17:46 PM)
// @param output java.lang.String
//here, we pass our messages from dbconverter off to the eventqueue thread so they can
//get drawn before the whole damn thing is done
//=======================================================================================
public void addOutput(final String output) 
{
	javax.swing.SwingUtilities.invokeLater(new Runnable()
	{
		public void run()
		{
			getMessageArea().append(output + IRunnableDBTool.LF);
		}
	});

}


public void addOutputNoLN(final String output) 
{
    javax.swing.SwingUtilities.invokeLater(new Runnable()
    {
        public void run()
        {
            getMessageArea().append(output);
        }
    });

}

/**
 * Comment
 */
public void browseButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	getChooser();		
	return;
}

/**
 * Comment
 */
public void browseXMLButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    getBrowseXMLChooser();       
    return;
}

/**
 * Comment
 */
public void saveButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
    getSaveChooser();       
    return;
}

/**
 * connEtoC1:  (StartButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBToolsFrame.startButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) 
{
	try {
		this.startButton_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}


/**
 * connEtoC2:  (BrowseButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBToolsFrame.browseButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		this.browseButton_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * connEtoC3:  (BrowseButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBToolsFrame.browseXMLButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
    try {
        this.browseXMLButton_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * connEtoC4:  (BrowseButton.action.actionPerformed(java.awt.event.ActionEvent) --> DBToolsFrame.saveButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
    try {
        this.saveButton_ActionPerformed(arg1);
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
}

/**
 * Insert the method's description here.
 * Creation date: (12/11/2001 12:06:46 PM)
 */
public void finish( final String msg )
{
	javax.swing.JFrame box	= new javax.swing.JFrame("Complete");
	javax.swing.JOptionPane done = new  javax.swing.JOptionPane( );

	box.setResizable(false);
	box.setIconImage(java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
		
	javax.swing.JOptionPane.showMessageDialog(
			box, (msg == null ? "Tool Operation Completed" : msg) );
}


/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getBrowseButton() {
	if (ivjBrowseButton == null) {
		try {
			ivjBrowseButton = new javax.swing.JButton();
			ivjBrowseButton.setName("BrowseButton");
			ivjBrowseButton.setText("Browse");
			ivjBrowseButton.setMinimumSize(new java.awt.Dimension(90, 25));
			ivjBrowseButton.setMaximumSize(new java.awt.Dimension(90, 25));
			ivjBrowseButton.setPreferredSize(new java.awt.Dimension(90, 25));
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjBrowseButton;
}

/**
 * Return the JButton1 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getBrowseXMLButton() {
    if (ivjBrowseXMLButton == null) {
        try {
            ivjBrowseXMLButton = new javax.swing.JButton();
            ivjBrowseXMLButton.setName("BrowseXMLButton");
            ivjBrowseXMLButton.setText("Browse");
            ivjBrowseXMLButton.setMinimumSize(new java.awt.Dimension(90, 25));
            ivjBrowseXMLButton.setMaximumSize(new java.awt.Dimension(90, 25));
            ivjBrowseXMLButton.setPreferredSize(new java.awt.Dimension(90, 25));
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjBrowseXMLButton;
}

/**
 * Return the JButton2 property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getSaveButton() {
    if (ivjSaveButton == null) {
        try {
            ivjSaveButton = new javax.swing.JButton();
            ivjSaveButton.setName("SaveButton");
            ivjSaveButton.setText("Save...");
            ivjSaveButton.setMinimumSize(new java.awt.Dimension(90, 25));
            ivjSaveButton.setMaximumSize(new java.awt.Dimension(90, 25));
            ivjSaveButton.setPreferredSize(new java.awt.Dimension(90, 25));
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return ivjSaveButton;
}

/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getButtonPanel() {
	if (ivjButtonPanel == null) {
		try {
			ivjButtonPanel = new javax.swing.JPanel();
			ivjButtonPanel.setName("ButtonPanel");
			ivjButtonPanel.setBorder(new javax.swing.border.EtchedBorder());
			ivjButtonPanel.setLayout(new java.awt.GridBagLayout());
			ivjButtonPanel.setMaximumSize(new java.awt.Dimension(32767, 32767));

			java.awt.GridBagConstraints constraintsStartButton = new java.awt.GridBagConstraints();
			constraintsStartButton.gridx = 2; constraintsStartButton.gridy = 2;
			constraintsStartButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsStartButton.insets = new java.awt.Insets(4, 3, 10, 6);
			constraintsStartButton.weightx = 1.0;
			getButtonPanel().add(getStartButton(), constraintsStartButton);

			java.awt.GridBagConstraints constraintsSaveButton = new java.awt.GridBagConstraints();
			constraintsSaveButton.gridx = 3; constraintsSaveButton.gridy = 2;
			constraintsSaveButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsSaveButton.insets = new java.awt.Insets(5, 3, 10, 6);
			constraintsSaveButton.weightx = 1.0;
			getButtonPanel().add(getSaveButton(), constraintsSaveButton);
			
			java.awt.GridBagConstraints constraintsPathField = new java.awt.GridBagConstraints();
			constraintsPathField.gridx = 2; constraintsPathField.gridy = 1;
			constraintsPathField.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsPathField.anchor = java.awt.GridBagConstraints.WEST;
			constraintsPathField.weightx = 1.0;
			constraintsPathField.insets = new java.awt.Insets(14, 3, 4, 6);
			getButtonPanel().add(getPathField(), constraintsPathField);

			java.awt.GridBagConstraints constraintsBrowseButton = new java.awt.GridBagConstraints();
			constraintsBrowseButton.gridx = 3; constraintsBrowseButton.gridy = 1;
			constraintsBrowseButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBrowseButton.insets = new java.awt.Insets(12, 6, 6, 71);
			getButtonPanel().add(getBrowseButton(), constraintsBrowseButton);

			java.awt.GridBagConstraints constraintsBrowseXMLButton = new java.awt.GridBagConstraints();
			constraintsBrowseXMLButton.gridx = 3; constraintsBrowseXMLButton.gridy = 1;
			constraintsBrowseXMLButton.anchor = java.awt.GridBagConstraints.WEST;
			constraintsBrowseXMLButton.insets = new java.awt.Insets(12, 6, 6, 71);
            getButtonPanel().add(getBrowseXMLButton(), constraintsBrowseXMLButton);
			
			java.awt.GridBagConstraints constraintsJLabelOption = new java.awt.GridBagConstraints();
			constraintsJLabelOption.gridx = 1; constraintsJLabelOption.gridy = 1;
			constraintsJLabelOption.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelOption.insets = new java.awt.Insets(17, 12, 4, 3);
			getButtonPanel().add(getJLabelOption(), constraintsJLabelOption);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjButtonPanel;
}

/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 11:24:36 AM)
 */
public void getChooser()
{
	//File temp = new File(shorterPath);
	
	//This will need to be updated someday for a new version of swing
	java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
	JFileChooser fileChooser = new JFileChooser();


	//we set the chooser so it will only look for dirs	
	fileChooser.setAcceptAllFileFilterUsed( false );
	fileChooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
	fileChooser.setApproveButtonText("Select");
	fileChooser.setApproveButtonMnemonic('s');
	
	//allow them to see only directories
	fileChooser.setFileFilter( new FileFilter()
	{
		public boolean accept(File f) { return f.isDirectory(); };
		public String getDescription() { return "Directories Only"; };
	});


	//set the chooser to the current location
	fileChooser.setCurrentDirectory(
			new File(getPathField().getText()) );


	int res = fileChooser.showOpenDialog( this );
	if( res == javax.swing.JFileChooser.APPROVE_OPTION )
	{
		try
		{
			//thePath = fileChooser.getSelectedFile().getPath();
			
			getPathField().setText(
				fileChooser.getSelectedFile().getPath() + IRunnableDBTool.FS);

			com.cannontech.clientutils.CTILogger.info("** Chooser path was: " + 
				getPathField().getText() );
		}
		catch (Exception exep)
		{
			javax.swing.JOptionPane.showMessageDialog(parent,"An error occurred opening file","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
		}
	}

	return;
}

/**
 * Insert the method's description here.
 * Creation date: (7/12/2001 11:24:36 AM)
 */
public void getBrowseXMLChooser()
{
    java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
    JFileChooser fileChooser = new JFileChooser();

    // We set the chooser so it will only look for dirs  
    fileChooser.setAcceptAllFileFilterUsed( true );
    fileChooser.setApproveButtonText("Select");
    fileChooser.setApproveButtonMnemonic('s');
    
    // Allow them to see only directories and XML Files
    fileChooser.setFileFilter( new FileFilter()
    {
        public boolean accept(File f) { return f.getAbsolutePath().endsWith(".xml") || f.isDirectory(); };
        public String getDescription() { return "XML Files Only"; };
    });


    //set the chooser to the current location
    fileChooser.setCurrentDirectory(
            new File(getPathField().getText()) );


    int res = fileChooser.showOpenDialog( this );
    if( res == javax.swing.JFileChooser.APPROVE_OPTION )
    {
        try
        {
            getPathField().setText(fileChooser.getSelectedFile().getPath());

            com.cannontech.clientutils.CTILogger.info("** Chooser path was: " + 
                getPathField().getText() );
        }
        catch (Exception exep)
        {
            javax.swing.JOptionPane.showMessageDialog(parent,"An error occurred opening file","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    return;
}

/**
 */
public void getXMLFileChooser()
{
    //File temp = new File(shorterPath);
    
    //This will need to be updated someday for a new version of swing
    java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
    JFileChooser fileChooser = new JFileChooser();


    //we set the chooser so it will only look for dirs  
    fileChooser.setAcceptAllFileFilterUsed( true );
    fileChooser.setFileSelectionMode( JFileChooser.SAVE_DIALOG );
    fileChooser.setApproveButtonText("Select");
    fileChooser.setApproveButtonMnemonic('s');
    
    //allow them to see only directories
    fileChooser.setFileFilter( new FileFilter()
    {
        public boolean accept(File f) { return f.getName().endsWith(".xml"); };
        public String getDescription() { return "XML Files Only"; };
    });


    //set the chooser to the current location
    fileChooser.setCurrentDirectory(
            new File(getPathField().getText()) );


    int res = fileChooser.showOpenDialog( this );
    if( res == javax.swing.JFileChooser.APPROVE_OPTION )
    {
        try
        {
            //thePath = fileChooser.getSelectedFile().getPath();
            
            getPathField().setText(
                fileChooser.getSelectedFile().getPath() + IRunnableDBTool.FS);

            com.cannontech.clientutils.CTILogger.info("** Chooser path was: " + 
                getPathField().getText() );
        }
        catch (Exception exep)
        {
            javax.swing.JOptionPane.showMessageDialog(parent,"An error occurred opening file","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    return;
}

/**
 */
public void getSaveChooser()
{
    //File temp = new File(shorterPath);
    
    //This will need to be updated someday for a new version of swing
    java.awt.Frame parent = com.cannontech.common.util.CtiUtilities.getParentFrame(this);
    JFileChooser fileChooser = new JFileChooser();


    //we set the chooser so it will only look for dirs  
    fileChooser.setAcceptAllFileFilterUsed( true );
    fileChooser.setFileSelectionMode( JFileChooser.SAVE_DIALOG );
    fileChooser.setApproveButtonText("Save");
    fileChooser.setApproveButtonMnemonic('s');
    
    //set the chooser to the current location
//    fileChooser.setCurrentDirectory(
//            new File(getPathField().getText()) );


    int res = fileChooser.showSaveDialog( this );
    if( res == javax.swing.JFileChooser.APPROVE_OPTION )
    {
        try
        {
            //thePath = fileChooser.getSelectedFile().getPath();
            String path = fileChooser.getSelectedFile().getPath();

            String text = getMessageArea().getText();
            FileWriter fileWriter = new FileWriter(path);
            fileWriter.write(text);
            fileWriter.close();
            
            com.cannontech.clientutils.CTILogger.info("** Saving screen results to " + path );
            
        }
        catch (Exception exep)
        {
            javax.swing.JOptionPane.showMessageDialog(parent,"An error occurred while saving the file","Error",javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    return;
}


/**
 * Return the JLabel1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMsgs() {
	if (ivjJLabelMsgs == null) {
		try {
			ivjJLabelMsgs = new javax.swing.JLabel();
			ivjJLabelMsgs.setName("JLabelMsgs");
			ivjJLabelMsgs.setPreferredSize(new java.awt.Dimension(250, 14));
			ivjJLabelMsgs.setText("Output Messages");
//			ivjJLabelMsgs.setMaximumSize(new java.awt.Dimension(250, 14));
//			ivjJLabelMsgs.setMinimumSize(new java.awt.Dimension(250, 14));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelMsgs;
}

/**
 * Return the JLabelOption property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getJLabelOption() {
	if (ivjJLabelOption == null) {
		try {
			ivjJLabelOption = new javax.swing.JLabel();
			ivjJLabelOption.setName("JLabelOption");
			ivjJLabelOption.setText("Directory:");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelOption;
}

/**
 * Return the JPanelTools property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelTools() {
	if (ivjJPanelTools == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Available Tools");
			ivjJPanelTools = new javax.swing.JPanel();
			ivjJPanelTools.setName("JPanelTools");
			ivjJPanelTools.setBorder(ivjLocalBorder);
			ivjJPanelTools.setLayout(getJPanelToolsFlowLayout());
			
			ivjJPanelTools.setMinimumSize(new java.awt.Dimension(130, 90));
			ivjJPanelTools.setMaximumSize(new java.awt.Dimension(130, 90));
			ivjJPanelTools.setPreferredSize(new java.awt.Dimension(130, 90));
			
			
			getJPanelTools().add(getJRadioButton0(), getJRadioButton0().getName());
			getJPanelTools().add(getJRadioButton1(), getJRadioButton1().getName());
			getJPanelTools().add(getJRadioButton2(), getJRadioButton2().getName());
			getJPanelTools().add(getJRadioButton3(), getJRadioButton3().getName());
			getJPanelTools().add(getJRadioButton4(), getJRadioButton4().getName());
			getJPanelTools().add(getJRadioButton5(), getJRadioButton5().getName());

			//this listener will say what to do on a selection
			final ActionListener al = new ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e) 
				{
					if( e.getSource() instanceof JRadioButton )
					{
						JRadioButton butt = (JRadioButton)e.getSource();
						
						//store the tool in the radio button
						IRunnableDBTool tool = (IRunnableDBTool)butt.getClientProperty( butt );

						getJLabelOption().setText( tool.getParamText() );
						
						if( tool.getDefaultValue() != null )
							getPathField().setText( tool.getDefaultValue() );

						if(!(tool instanceof ModifyConstraints)) {
						    // Turn on the browse functionality to look for XML Files.
						    if (tool instanceof DBCompare) {
						        getBrowseButton().setVisible(false);
						        getBrowseXMLButton().setVisible(true);
						    // Revert back to browsing for directories.
						    } else {
						        getBrowseButton().setVisible(true);
	                            getBrowseXMLButton().setVisible(false);
						    }
						    
						}
					}
				}
			};


			for( int i = 0; i < getJPanelTools().getComponentCount(); i++ )
			{
				if( getJPanelTools().getComponent(i) instanceof JRadioButton )
				{					
					JRadioButton butt = (JRadioButton)getJPanelTools().getComponent(i);
					
					if( i < ALL_TOOLS.length )
					{
						buttGroup.add( butt );
						butt.setText( ALL_TOOLS[i].getName() );
						butt.addActionListener( al );
						
						//store the tool in the radio button
						butt.putClientProperty( butt, ALL_TOOLS[i] );
					}
					else
					{
						butt.setVisible( false );
					}
				}
					
			}
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelTools;
}

/**
 * Return the JPanelToolsFlowLayout property value.
 * @return java.awt.FlowLayout
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private java.awt.FlowLayout getJPanelToolsFlowLayout() {
	java.awt.FlowLayout ivjJPanelToolsFlowLayout = null;
	try {
		/* Create part */
		ivjJPanelToolsFlowLayout = new java.awt.FlowLayout();
		ivjJPanelToolsFlowLayout.setAlignment(java.awt.FlowLayout.LEFT);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	};
	return ivjJPanelToolsFlowLayout;
}


/**
 * Return the JRadioButton0 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton0() {
	if (ivjJRadioButton0 == null) {
		try {
			ivjJRadioButton0 = new javax.swing.JRadioButton();
			ivjJRadioButton0.setName("JRadioButton0");
			ivjJRadioButton0.setText("JRadioButton0");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton0;
}


/**
 * Return the JRadioButton1 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton1() {
	if (ivjJRadioButton1 == null) {
		try {
			ivjJRadioButton1 = new javax.swing.JRadioButton();
			ivjJRadioButton1.setName("JRadioButton1");
			ivjJRadioButton1.setText("JRadioButton1");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton1;
}

/**
 * Return the JRadioButton2 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton2() {
	if (ivjJRadioButton2 == null) {
		try {
			ivjJRadioButton2 = new javax.swing.JRadioButton();
			ivjJRadioButton2.setName("JRadioButton2");
			ivjJRadioButton2.setText("JRadioButton2");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton2;
}


/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton3() {
	if (ivjJRadioButton3 == null) {
		try {
			ivjJRadioButton3 = new javax.swing.JRadioButton();
			ivjJRadioButton3.setName("JRadioButton3");
			ivjJRadioButton3.setText("JRadioButton3");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton3;
}


/**
 * Return the JRadioButton4 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton4() {
	if (ivjJRadioButton4 == null) {
		try {
			ivjJRadioButton4 = new javax.swing.JRadioButton();
			ivjJRadioButton4.setName("JRadioButton4");
			ivjJRadioButton4.setText("JRadioButton4");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton4;
}


/**
 * Return the JRadioButton5 property value.
 * @return javax.swing.JRadioButton
 */
private javax.swing.JRadioButton getJRadioButton5() {
	if (ivjJRadioButton5 == null) {
		try {
			ivjJRadioButton5 = new javax.swing.JRadioButton();
			ivjJRadioButton5.setName("JRadioButton5");
			ivjJRadioButton5.setText("JRadioButton5");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJRadioButton5;
}


/**
 * Return the MainPanel property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getMainPanel() {
	if (ivjMainPanel == null) {
		try {
			ivjMainPanel = new javax.swing.JPanel();
			ivjMainPanel.setName("MainPanel");
			ivjMainPanel.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJLabelMsgs = new java.awt.GridBagConstraints();
			constraintsJLabelMsgs.gridx = 1; constraintsJLabelMsgs.gridy = 1;
			constraintsJLabelMsgs.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMsgs.insets = new java.awt.Insets(11, 16, 2, 2);
			getMainPanel().add(getJLabelMsgs(), constraintsJLabelMsgs);

			java.awt.GridBagConstraints constraintsOutputScrollPane = new java.awt.GridBagConstraints();
			constraintsOutputScrollPane.gridx = 1; constraintsOutputScrollPane.gridy = 2;
			constraintsOutputScrollPane.fill = java.awt.GridBagConstraints.BOTH;
			constraintsOutputScrollPane.anchor = java.awt.GridBagConstraints.WEST;
			constraintsOutputScrollPane.weightx = 1.0;
			constraintsOutputScrollPane.weighty = 1.0;
			constraintsOutputScrollPane.insets = new java.awt.Insets(2, 12, 2, 2);
			getMainPanel().add(getOutputScrollPane(), constraintsOutputScrollPane);

			java.awt.GridBagConstraints constraintsButtonPanel = new java.awt.GridBagConstraints();
			constraintsButtonPanel.gridx = 1; constraintsButtonPanel.gridy = 3;
			constraintsButtonPanel.gridwidth = 2;
			constraintsButtonPanel.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsButtonPanel.anchor = java.awt.GridBagConstraints.SOUTH;
			constraintsButtonPanel.weightx = 0.0;
			constraintsButtonPanel.weighty = 0.0;
			constraintsButtonPanel.insets = new java.awt.Insets(3, 12, 10, 9);
			getMainPanel().add(getButtonPanel(), constraintsButtonPanel);

			java.awt.GridBagConstraints constraintsJPanelTools = new java.awt.GridBagConstraints();
			constraintsJPanelTools.gridx = 2; constraintsJPanelTools.gridy = 2;
			constraintsJPanelTools.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanelTools.anchor = java.awt.GridBagConstraints.EAST;
			constraintsJPanelTools.weightx = 0.0;
			constraintsJPanelTools.weighty = 0.0;
			constraintsJPanelTools.insets = new java.awt.Insets(2, 2, 2, 8);
			getMainPanel().add(getJPanelTools(), constraintsJPanelTools);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjMainPanel;
}

/**
 * Return the MessageArea property value.
 * @return javax.swing.JTextArea
 */
private javax.swing.JTextArea getMessageArea() 
{
	if (ivjMessageArea == null) 
	{
		try
		{
			ivjMessageArea = new javax.swing.JTextArea();
			ivjMessageArea.setName("MessageArea");
			
			ivjMessageArea.setBounds(0, 0, 160, 120);			

			ivjMessageArea.setEditable( false );
		}
		catch (java.lang.Throwable ivjExc) 
		{
			handleException(ivjExc);
		}
	}
	return ivjMessageArea;
}


/**
 * Return the OutputScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getOutputScrollPane() {
	if (ivjOutputScrollPane == null) {
		try {
			ivjOutputScrollPane = new javax.swing.JScrollPane();
			ivjOutputScrollPane.setName("OutputScrollPane");
			ivjOutputScrollPane.setAutoscrolls(true);
			ivjOutputScrollPane.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjOutputScrollPane.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			getOutputScrollPane().setViewportView(getMessageArea());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjOutputScrollPane;
}

/**
 * Insert the method's description here.
 * Creation date: (7/13/2001 10:41:00 AM)
 * @return java.util.Vector
 */
public Vector getOutputVector()
{
	if(outputVector == null)
		outputVector = new Vector();
	return outputVector;
}


/**
 * Return the PathField property value.
 * @return javax.swing.JTextField
 */
private javax.swing.JTextField getPathField() {
	if (ivjPathField == null) {
		try {
			ivjPathField = new javax.swing.JTextField();
			ivjPathField.setName("PathField");
			ivjPathField.setFont(new java.awt.Font("Arial", 1, 12));
			ivjPathField.setEditable(true);
			
			ivjPathField.setText( DEF_PATH );
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjPathField;
}

/**
 * Return the StartButton property value.
 * @return javax.swing.JButton
 */
private javax.swing.JButton getStartButton() {
	if (ivjStartButton == null) {
		try {
			ivjStartButton = new javax.swing.JButton();
			ivjStartButton.setName("StartButton");
			ivjStartButton.setText("Start...");
			
			ivjStartButton.setMinimumSize(new java.awt.Dimension(90, 25));
			ivjStartButton.setMaximumSize(new java.awt.Dimension(90, 25));
			ivjStartButton.setPreferredSize(new java.awt.Dimension(90, 25));
			
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjStartButton;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) 
{
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}


/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
private void initConnections() throws java.lang.Exception {
	
	// init the popup box connections for the MsgPanel
	MouseListener msgList = new PopUpMenuShower( getTextMsgPanePopUp() );
	getMessageArea().addMouseListener( msgList );
	getTextMsgPanePopUp().addPopupMenuListener( this );

	getStartButton().addActionListener(this);
	getBrowseButton().addActionListener(this);
	getBrowseXMLButton().addActionListener(this);
    getSaveButton().addActionListener(this);
}

private TextMsgPanePopUp getTextMsgPanePopUp()
{
	if( textMsgPanePopUp == null )
		textMsgPanePopUp = new TextMsgPanePopUp();
	
	return textMsgPanePopUp;
}


/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("ConverterFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setSize(703, 439);
		setTitle("Tools Control Panel");
		setContentPane(getMainPanel());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	
	//ourConverter = new DBConverter(thePath + "\\");
	//ourConverter = new DBConverter(thePath);
//	ourConverter.setCf(this);
//	getChooser();

	getJRadioButton0().doClick();

}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) 
{
	try
	{
		System.setProperty("cti.app.name", "DBToolsFrame");

		DBToolsFrame aConverterFrame;
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		aConverterFrame = new DBToolsFrame();
		aConverterFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aConverterFrame.show();
		java.awt.Insets insets = aConverterFrame.getInsets();
		aConverterFrame.setSize(aConverterFrame.getWidth() + insets.left + insets.right, aConverterFrame.getHeight() + insets.top + insets.bottom);
		aConverterFrame.setLocation(220, 150);
		
		aConverterFrame.setIconImage(
				java.awt.Toolkit.getDefaultToolkit().getImage("ctismall.gif"));
		
		aConverterFrame.setVisible(true);
	}
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}


/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e)
{
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e)
{	
}
/**
 * Insert the method's description here.
 * Creation date: (8/25/00 9:45:22 AM)
 * @param e javax.swing.event.PopupMenuEvent
 */
public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) 
{
	if( e.getSource() == getTextMsgPanePopUp() )
	{
		getTextMsgPanePopUp().setTextArea( getMessageArea() );
	}
}

/**
 * Use this method to set all properties needed by any of the tools.
 * This method takes the user entered values and stores them in the System space.
 * 
 */
private void setProps()
{
	// get the text from the input field
	System.setProperty( IRunnableDBTool.PROP_VALUE, getPathField().getText() );
}

private JRadioButton getSelectedButton()
{
	for( int i = 0; i < getJPanelTools().getComponentCount(); i++ )
	{		
		if( getJPanelTools().getComponent(i) instanceof JRadioButton )
		{
			if( ((JRadioButton)getJPanelTools().getComponent(i)).isSelected() )
				return (JRadioButton)getJPanelTools().getComponent(i);
		}
		
	}
	
	return null;
}

//=====================================================================================
//we null out any previous threads that might exist, then make a new one, and start
//the ball rollin'
//=====================================================================================
public void startButton_ActionPerformed(java.awt.event.ActionEvent actionEvent)
{
	setProps();
	
	//store the tool in the radio button
	JRadioButton butt = getSelectedButton();
	
	if( butt != null )
	{
		IRunnableDBTool tool = (IRunnableDBTool)butt.getClientProperty( butt );

		tool.setIMessageFrame( this );
	
		//printThread = null;
		Thread t = new Thread( tool );
		t.setName("DBTool");
		t.start();
	}
}

}