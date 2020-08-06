package com.cannontech.macs.schedule.wizard;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;

public class ScriptSchedulePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener {
	private javax.swing.JComboBox<String> ivjJComboBoxTemplate = null;
	private javax.swing.JLabel ivjJLabelTemplate = null;
	private javax.swing.JPanel ivjJPanelScriptText = null;
	private javax.swing.JScrollPane ivjJScrollPaneScript = null;
	private javax.swing.JButton ivjJButtonCheckScript = null;
	private javax.swing.JLabel ivjJLabelFileName = null;
	private javax.swing.JTextField ivjJTextFieldFileName = null;
	private javax.swing.JTextArea ivjJTextAreaScript = null;
/**
 * ScriptSchedulePanel constructor comment.
 */
public ScriptSchedulePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getJComboBoxTemplate()) 
		this.jComboBoxTemplate_ActionPerformed(e);
	if (e.getSource() == getJButtonCheckScript()) 
	    this.jButtonCheckScript_ActionPerformed(e);
}
/**
 * Method to handle events for the CaretListener interface.
 * @param e javax.swing.event.CaretEvent
 */
public void caretUpdate(javax.swing.event.CaretEvent e) {
	if (e.getSource() == getJTextFieldFileName()) 
	    this.fireInputUpdate();
	if (e.getSource() == getJTextAreaScript()) 
	    this.fireInputUpdate();
}
/**
 * Return the JButtonCheckScript property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonCheckScript() {
	if (ivjJButtonCheckScript == null) {
		try {
			ivjJButtonCheckScript = new javax.swing.JButton();
			ivjJButtonCheckScript.setName("JButtonCheckScript");
			ivjJButtonCheckScript.setToolTipText("Checks syntax of script");
			ivjJButtonCheckScript.setMnemonic('c');
			ivjJButtonCheckScript.setText("Check..");
			// user code begin {1}
			
			//dont show since not implemented
			ivjJButtonCheckScript.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonCheckScript;
}
/**
 * Return the JComboBoxTemplate property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox<String> getJComboBoxTemplate() {
	if (ivjJComboBoxTemplate == null) {
		try {
			ivjJComboBoxTemplate = new javax.swing.JComboBox<String>();
			ivjJComboBoxTemplate.setName("JComboBoxTemplate");
			// user code begin {1}

			ivjJComboBoxTemplate.addItem(CtiUtilities.STRING_NONE );
			
			//dont show since not implemented
			ivjJComboBoxTemplate.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxTemplate;
}
/**
 * Return the JLabelFileName property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelFileName() {
	if (ivjJLabelFileName == null) {
		try {
			ivjJLabelFileName = new javax.swing.JLabel();
			ivjJLabelFileName.setName("JLabelFileName");
			ivjJLabelFileName.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelFileName.setText("Script Name:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelFileName;
}
/**
 * Return the JLabelTemplate property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTemplate() {
	if (ivjJLabelTemplate == null) {
		try {
			ivjJLabelTemplate = new javax.swing.JLabel();
			ivjJLabelTemplate.setName("JLabelTemplate");
			ivjJLabelTemplate.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelTemplate.setText("Template:");
			// user code begin {1}
			
			//dont show since not implemented
			ivjJLabelTemplate.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTemplate;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelScriptText() {
	if (ivjJPanelScriptText == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Script");
			ivjJPanelScriptText = new javax.swing.JPanel();
			ivjJPanelScriptText.setName("JPanelScriptText");
			ivjJPanelScriptText.setBorder(ivjLocalBorder);
			ivjJPanelScriptText.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJScrollPaneScript = new java.awt.GridBagConstraints();
			constraintsJScrollPaneScript.gridx = 1; constraintsJScrollPaneScript.gridy = 2;
			constraintsJScrollPaneScript.gridwidth = 2;
			constraintsJScrollPaneScript.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJScrollPaneScript.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJScrollPaneScript.weightx = 1.0;
			constraintsJScrollPaneScript.weighty = 1.0;
			constraintsJScrollPaneScript.ipadx = 300;
			constraintsJScrollPaneScript.ipady = 204;
			constraintsJScrollPaneScript.insets = new java.awt.Insets(4, 11, 8, 15);
			getJPanelScriptText().add(getJScrollPaneScript(), constraintsJScrollPaneScript);

			java.awt.GridBagConstraints constraintsJLabelFileName = new java.awt.GridBagConstraints();
			constraintsJLabelFileName.gridx = 1; constraintsJLabelFileName.gridy = 1;
			constraintsJLabelFileName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelFileName.ipadx = 6;
			constraintsJLabelFileName.ipady = -2;
			constraintsJLabelFileName.insets = new java.awt.Insets(3, 13, 7, 0);
			getJPanelScriptText().add(getJLabelFileName(), constraintsJLabelFileName);

			java.awt.GridBagConstraints constraintsJTextFieldFileName = new java.awt.GridBagConstraints();
			constraintsJTextFieldFileName.gridx = 2; constraintsJTextFieldFileName.gridy = 1;
			constraintsJTextFieldFileName.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsJTextFieldFileName.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJTextFieldFileName.weightx = 1.0;
			constraintsJTextFieldFileName.ipadx = 249;
			constraintsJTextFieldFileName.insets = new java.awt.Insets(0, 1, 4, 15);
			getJPanelScriptText().add(getJTextFieldFileName(), constraintsJTextFieldFileName);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelScriptText;
}
/**
 * Return the JScrollPaneScript property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneScript() {
	if (ivjJScrollPaneScript == null) {
		try {
			ivjJScrollPaneScript = new javax.swing.JScrollPane();
			ivjJScrollPaneScript.setName("JScrollPaneScript");
			getJScrollPaneScript().setViewportView(getJTextAreaScript());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneScript;
}
/**
 * Return the JTextAreaScript property value.
 * @return javax.swing.JTextArea
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextArea getJTextAreaScript() {
	if (ivjJTextAreaScript == null) {
		try {
			ivjJTextAreaScript = new javax.swing.JTextArea();
			ivjJTextAreaScript.setName("JTextAreaScript");
			ivjJTextAreaScript.setBounds(0, 0, 333, 230);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextAreaScript;
}
/**
 * Return the JTextFieldFileName property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldFileName() {
	if (ivjJTextFieldFileName == null) {
		try {
			ivjJTextFieldFileName = new javax.swing.JTextField();
			ivjJTextFieldFileName.setName("JTextFieldFileName");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldFileName;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param o java.lang.Object
 */
public Object getValue(Object val)
{
	com.cannontech.message.macs.message.Schedule sch = (com.cannontech.message.macs.message.Schedule)val;

	sch.setScriptFileName( getJTextFieldFileName().getText() );
	sch.getNonPersistantData().getScript().setFileName( getJTextFieldFileName().getText() );

	// filter line separators in the script text area
	String text = getJTextAreaScript().getText();

	java.io.BufferedReader rdr = new java.io.BufferedReader(new java.io.StringReader(text));

	String endl = System.getProperty("line.separator");
	StringBuffer buf = new StringBuffer();
	String in;

	try 
	{
		while( (in = rdr.readLine()) != null )
			buf.append(in + endl);
	} 
	catch( java.io.IOException e ) 
	{
		CTILogger.error( e.getMessage(), e );
	}

	sch.getNonPersistantData().getScript().setFileContents( buf.toString() );

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJComboBoxTemplate().addActionListener(this);
	getJButtonCheckScript().addActionListener(this);
	getJTextFieldFileName().addCaretListener(this);
	getJTextAreaScript().addCaretListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ScriptSchedulePanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(366, 349);

		java.awt.GridBagConstraints constraintsJComboBoxTemplate = new java.awt.GridBagConstraints();
		constraintsJComboBoxTemplate.gridx = 2; constraintsJComboBoxTemplate.gridy = 1;
		constraintsJComboBoxTemplate.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsJComboBoxTemplate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJComboBoxTemplate.weightx = 1.0;
		constraintsJComboBoxTemplate.ipadx = 10;
		constraintsJComboBoxTemplate.insets = new java.awt.Insets(17, 1, 6, 30);
		add(getJComboBoxTemplate(), constraintsJComboBoxTemplate);

		java.awt.GridBagConstraints constraintsJLabelTemplate = new java.awt.GridBagConstraints();
		constraintsJLabelTemplate.gridx = 1; constraintsJLabelTemplate.gridy = 1;
		constraintsJLabelTemplate.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelTemplate.ipadx = 4;
		constraintsJLabelTemplate.insets = new java.awt.Insets(19, 7, 8, 1);
		add(getJLabelTemplate(), constraintsJLabelTemplate);

		java.awt.GridBagConstraints constraintsJPanelScriptText = new java.awt.GridBagConstraints();
		constraintsJPanelScriptText.gridx = 1; constraintsJPanelScriptText.gridy = 2;
		constraintsJPanelScriptText.gridwidth = 3;
		constraintsJPanelScriptText.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelScriptText.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelScriptText.weightx = 1.0;
		constraintsJPanelScriptText.weighty = 1.0;
		constraintsJPanelScriptText.ipadx = 348;
		constraintsJPanelScriptText.ipady = 286;
		constraintsJPanelScriptText.insets = new java.awt.Insets(7, 9, 10, 9);
		add(getJPanelScriptText(), constraintsJPanelScriptText);

		java.awt.GridBagConstraints constraintsJButtonCheckScript = new java.awt.GridBagConstraints();
		constraintsJButtonCheckScript.gridx = 3; constraintsJButtonCheckScript.gridy = 1;
		constraintsJButtonCheckScript.anchor = java.awt.GridBagConstraints.EAST;
		constraintsJButtonCheckScript.ipadx = 8;
		constraintsJButtonCheckScript.insets = new java.awt.Insets(15, 30, 6, 9);
		add(getJButtonCheckScript(), constraintsJButtonCheckScript);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	// user code end
}

/**
 * This method was created in VisualAge.
 * @return boolean
 */
public boolean isInputValid() 
{
	if( getJTextFieldFileName().getText() == null 
		|| getJTextFieldFileName().getText().length() <= 0 
		|| !getJTextFieldFileName().isEnabled() )
	{
		setErrorString("The Name text field must be filled in");
		return false;
	}

	if( getJTextAreaScript().getText() == null 
		|| getJTextAreaScript().getText().length() <= 0 
		|| !getJTextAreaScript().isEnabled() )
	{
		setErrorString("The Script text field must be filled in");
		return false;
	}
		
	return true;
}
/**
 * Comment
 */
public void jButtonCheckScript_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	CTILogger.info("Check script is not implemented");
	
	return;
}
/**
 * Comment
 */
public void jComboBoxTemplate_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	CTILogger.info("DO NOTHING FOR TEMPLATE JCOMBOBOX");
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ScriptSchedulePanel aScriptSchedulePanel;
		aScriptSchedulePanel = new ScriptSchedulePanel();
		frame.setContentPane(aScriptSchedulePanel);
		frame.setSize(aScriptSchedulePanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.show();
		java.awt.Insets insets = frame.getInsets();
		frame.setSize(frame.getWidth() + insets.left + insets.right, frame.getHeight() + insets.top + insets.bottom);
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (2/16/2001 11:20:58 AM)
 * @param text java.lang.String
 */
private void setScriptText(String text)
{
	try 
	{
		java.io.BufferedReader rdr =
			new java.io.BufferedReader(new java.io.StringReader(text));

		StringBuffer buf = new StringBuffer();
		String in;

		while ((in = rdr.readLine()) != null)
			buf.append(in + "\n");

		getJTextAreaScript().setText(buf.toString());
		
		//scroll us to the top
		getJTextAreaScript().setCaretPosition( 0 );
	}
	catch (java.io.IOException e) 
	{
		CTILogger.error( e.getMessage(), e );
	}
	 
}
/**
 * Insert the method's description here.
 * Creation date: (3/12/2001 5:13:39 PM)
 * @param file com.cannontech.message.macs.message.ScriptFile
 */
public void setScriptValues(final com.cannontech.message.macs.message.ScriptFile file) 
{
	try
	{
		int i = 0;

		for( i = 0; i < 25; i++ )  // 5 second timeout
		{
			if( this.isDisplayable() )
			{
CTILogger.info("		** TRUE - ScriptEditor isVisible()");
				break;
			}
			else
			{
CTILogger.info("		** Sleeping until ScriptEditor isVisible()");
				Thread.currentThread().sleep(200);
			}
		}

		if( i == 25 )
		{
			CTILogger.info("		** TimeOut occurred while waiting for our ScriptEditor screen to become Visible.");
			return;
		}

	}
	catch( InterruptedException e )
	{
		handleException(e);
	}

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJTextFieldFileName().setEnabled(true);
			getJTextFieldFileName().setText( file.getFileName() );

			getJTextAreaScript().setEnabled(true);
			getJTextAreaScript().setText(""); //clear any current text
			setScriptText( file.getFileContents() );
			CTILogger.info("		** Done setting script contents");

		}
	});

	
}
/**
 * This method was created in VisualAge.
 * @param o java.lang.Object
 */
public void setValue(Object o) 
{
	com.cannontech.message.macs.message.Schedule sched = (com.cannontech.message.macs.message.Schedule)o;

	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			// make the text fields disabled since we havent received the message from the server
			getJTextFieldFileName().setText("(Retrieving serving data...)");
			getJTextFieldFileName().setEnabled(false);

			getJTextAreaScript().setText("(Retrieving serving data...)");
			getJTextAreaScript().setEnabled(false);
		}
			
	});			


	//do not do the following because they are sent to us in a message from the server
	//getJTextFieldFileName().setText( sched.getScriptFileName() );
	//getJTextPaneScript().setText( sched.getNonPersistantData().getScript().getFileContents() );
}
}
