package com.cannontech.logger.config;

/**
 * Insert the type's description here.
 * Creation date: (5/9/00 3:58:40 PM)
 * @author: 
 * @Version: <version>
 */
import com.cannontech.clientutils.parametersfile.ParametersFile;
import com.cannontech.database.SqlStatement;
import com.cannontech.logger.Logger;
import com.cannontech.logger.LoggerClient;

public class LoggerMainFrame extends javax.swing.JFrame {
	public static final String[] PARAMETER_LIST =
	{
		"REGISTRATION",
		"COLOR_TOGGLE",
		"OUTPUT_HOURS",
		"OUTPUT_MINUTES",
		"OUTPUT_SECONDS",
		"PRINT_TITLE",
		
		/* ADD NEW PARAMETERS HERE */
			
		"COLUMN0_NAME", /* COLUMN DATA MUST GO AT THE BOTTOM OF THIS ARRAY */
		"COLUMN1_NAME", /* this column data and the bottom column data must have a */
		"COLUMN2_NAME", /*   1 to 1 relation */   
		"COLUMN3_NAME",
		"COLUMN4_NAME",		
		"COLUMN0_DESCRIPTION",
		"COLUMN1_DESCRIPTION",
		"COLUMN2_DESCRIPTION",
		"COLUMN3_DESCRIPTION",
		"COLUMN4_DESCRIPTION",		
	};
	private int priorityColors[] = null;
	public static final String SERVICE_NAME = "CTILogger";
	public static final String PARAMETER_FILE_NAME = "LoggerParameters.dat";
	private javax.swing.ButtonGroup buttonGroup = null;
	private final ParametersFile parametersFile = new ParametersFile( PARAMETER_FILE_NAME );
	private javax.swing.JPanel ivjJFrameContentPane = null;
	private javax.swing.JButton ivjJButtonPrinterSetup = null;
	private javax.swing.JPanel ivjJPanel1 = null;
	private javax.swing.JTabbedPane ivjJTabbedPane = null;
	private javax.swing.JPanel ivjSetupPage = null;
	private javax.swing.JButton ivjJButtonExit = null;
	private javax.swing.JPanel ivjJPanel2 = null;
	private javax.swing.JPanel ivjJPanel3 = null;
	IvjEventHandler ivjEventHandler = new IvjEventHandler();
	private javax.swing.JButton ivjJButtonSave = null;
	private javax.swing.JButton ivjJButtonSaveExit = null;
	private javax.swing.JComboBox ivjJComboBoxRegistration = null;
	private javax.swing.JLabel ivjJLabelRegistration = null;
	private javax.swing.JCheckBox ivjJCheckBoxDisable = null;
	private javax.swing.JComboBox ivjJComboBoxColor1 = null;
	private javax.swing.JComboBox ivjJComboBoxColor2 = null;
	private javax.swing.JComboBox ivjJComboBoxColor3 = null;
	private javax.swing.JComboBox ivjJComboBoxColor4 = null;
	private javax.swing.JComboBox ivjJComboBoxColor5 = null;
	private javax.swing.JComboBox ivjJComboBoxColor6 = null;
	private javax.swing.JLabel ivjJLabelPri1 = null;
	private javax.swing.JLabel ivjJLabelPri2 = null;
	private javax.swing.JLabel ivjJLabelPri3 = null;
	private javax.swing.JLabel ivjJLabelPri4 = null;
	private javax.swing.JLabel ivjJLabelPri5 = null;
	private javax.swing.JLabel ivjJLabelPri6 = null;
	private javax.swing.JPanel ivjJPanel4 = null;
	private javax.swing.JLabel ivjJLabelHrs = null;
	private javax.swing.JLabel ivjJLabelMins = null;
	private javax.swing.JLabel ivjJLabelSecs = null;
	private javax.swing.JTextField ivjJTextFieldTimeHours = null;
	private javax.swing.JTextField ivjJTextFieldTimeMinutes = null;
	private javax.swing.JTextField ivjJTextFieldTimeSeconds = null;
	private javax.swing.JLabel ivjJLabelTitle = null;
	private javax.swing.JPanel ivjJPanel5 = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableColumn = null;

class IvjEventHandler implements java.awt.event.ActionListener {
		public void actionPerformed(java.awt.event.ActionEvent e) {
			if (e.getSource() == LoggerMainFrame.this.getJButtonSaveExit()) 
				connEtoC1(e);
			if (e.getSource() == LoggerMainFrame.this.getJButtonSave()) 
				connEtoC2(e);
			if (e.getSource() == LoggerMainFrame.this.getJButtonExit()) 
				connEtoC4(e);
			if (e.getSource() == LoggerMainFrame.this.getJButtonPrinterSetup()) 
				connEtoC5(e);
		};
	};
	private javax.swing.JTextField ivjJTextFieldTitle = null;
/**
 * LoggerMainFrame constructor comment.
 */
public LoggerMainFrame() {
	super();
	initialize();
}
/**
 * LoggerMainFrame constructor comment.
 * @param title java.lang.String
 */
public LoggerMainFrame(String title) {
	super(title);
}
/**
 * connEtoC1:  (JButtonSaveRestart.action.actionPerformed(java.awt.event.ActionEvent) --> LoggerMainFrame.jButtonSaveRestart_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSaveExit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (JButtonStart.action.actionPerformed(java.awt.event.ActionEvent) --> LoggerMainFrame.jButtonStart_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonSave_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (JButtonExit.action.actionPerformed(java.awt.event.ActionEvent) --> LoggerMainFrame.jButtonExit_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonExit_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (JButtonPrinterSetup.action.actionPerformed(java.awt.event.ActionEvent) --> LoggerMainFrame.jButtonPrinterSetup_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.jButtonPrinterSetup_ActionPerformed(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/00 3:47:14 PM)
 */
private int[] getDBColors() 
{
	if( priorityColors == null )
	{
		SqlStatement statement = new SqlStatement("select foregroundcolor " +
			"from state where stategroupid=" + com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ALARM +
			" order by rawstate",
			LoggerClient.DBALIAS );

		try
		{
			statement.execute();
			priorityColors = new int[statement.getRowCount()];
			
			for( int i = 0; i < statement.getRowCount(); i++ )
			{
				priorityColors[i] = Integer.parseInt( statement.getRow(i)[0].toString() );
			}
		}
		catch( com.cannontech.common.util.CommandExecutionException ex )
		{
			handleException( ex );
		}
	}
	
	return priorityColors;	
}
/**
 * Return the JButtonExit property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonExit() {
	if (ivjJButtonExit == null) {
		try {
			ivjJButtonExit = new javax.swing.JButton();
			ivjJButtonExit.setName("JButtonExit");
			ivjJButtonExit.setMnemonic('x');
			ivjJButtonExit.setText("Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonExit;
}
/**
 * Return the JButtonPrinterSetup property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonPrinterSetup() {
	if (ivjJButtonPrinterSetup == null) {
		try {
			ivjJButtonPrinterSetup = new javax.swing.JButton();
			ivjJButtonPrinterSetup.setName("JButtonPrinterSetup");
			ivjJButtonPrinterSetup.setMnemonic('s');
			ivjJButtonPrinterSetup.setText("Printer Setup...");
			ivjJButtonPrinterSetup.setBounds(337, 15, 119, 27);
			// user code begin {1}

			/* REMOVE THIS ONCE THE PRINTER SETUP WORKS */
			ivjJButtonPrinterSetup.setVisible( false );
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonPrinterSetup;
}
/**
 * Return the JButtonStart property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSave() {
	if (ivjJButtonSave == null) {
		try {
			ivjJButtonSave = new javax.swing.JButton();
			ivjJButtonSave.setName("JButtonSave");
			ivjJButtonSave.setMnemonic('v');
			ivjJButtonSave.setText("Save");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSave;
}
/**
 * Return the JButtonSaveRestart property value.
 * @return javax.swing.JButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JButton getJButtonSaveExit() {
	if (ivjJButtonSaveExit == null) {
		try {
			ivjJButtonSaveExit = new javax.swing.JButton();
			ivjJButtonSaveExit.setName("JButtonSaveExit");
			ivjJButtonSaveExit.setMnemonic('S');
			ivjJButtonSaveExit.setText("Save & Exit");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJButtonSaveExit;
}
/**
 * Return the JCheckBoxDisable property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getJCheckBoxDisable() {
	if (ivjJCheckBoxDisable == null) {
		try {
			ivjJCheckBoxDisable = new javax.swing.JCheckBox();
			ivjJCheckBoxDisable.setName("JCheckBoxDisable");
			ivjJCheckBoxDisable.setText("Disable Color");
			ivjJCheckBoxDisable.setBounds(18, 23, 97, 25);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJCheckBoxDisable;
}
/**
 * Return the JComboBoxColor1 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor1() {
	if (ivjJComboBoxColor1 == null) {
		try {
			ivjJComboBoxColor1 = new javax.swing.JComboBox();
			ivjJComboBoxColor1.setName("JComboBoxColor1");
			ivjJComboBoxColor1.setBounds(78, 50, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor1.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor1;
}
/**
 * Return the JComboBoxColor2 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor2() {
	if (ivjJComboBoxColor2 == null) {
		try {
			ivjJComboBoxColor2 = new javax.swing.JComboBox();
			ivjJComboBoxColor2.setName("JComboBoxColor2");
			ivjJComboBoxColor2.setBounds(78, 91, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor2.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor2;
}
/**
 * Return the JComboBoxColor3 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor3() {
	if (ivjJComboBoxColor3 == null) {
		try {
			ivjJComboBoxColor3 = new javax.swing.JComboBox();
			ivjJComboBoxColor3.setName("JComboBoxColor3");
			ivjJComboBoxColor3.setBounds(78, 133, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor3.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor3;
}
/**
 * Return the JComboBoxColor4 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor4() {
	if (ivjJComboBoxColor4 == null) {
		try {
			ivjJComboBoxColor4 = new javax.swing.JComboBox();
			ivjJComboBoxColor4.setName("JComboBoxColor4");
			ivjJComboBoxColor4.setBounds(337, 133, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor4.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor4;
}
/**
 * Return the JComboBoxColor5 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor5() {
	if (ivjJComboBoxColor5 == null) {
		try {
			ivjJComboBoxColor5 = new javax.swing.JComboBox();
			ivjJComboBoxColor5.setName("JComboBoxColor5");
			ivjJComboBoxColor5.setBounds(337, 50, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor5.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor5;
}
/**
 * Return the JComboBoxColor6 property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxColor6() {
	if (ivjJComboBoxColor6 == null) {
		try {
			ivjJComboBoxColor6 = new javax.swing.JComboBox();
			ivjJComboBoxColor6.setName("JComboBoxColor6");
			ivjJComboBoxColor6.setBounds(337, 91, 138, 38);
			// user code begin {1}
			ivjJComboBoxColor6.setRenderer(new com.cannontech.common.gui.util.ColorComboBoxCellRenderer());
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxColor6;
}
/**
 * Return the JComboBoxOutputType property value.
 * @return javax.swing.JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JComboBox getJComboBoxRegistration() {
	if (ivjJComboBoxRegistration == null) {
		try {
			ivjJComboBoxRegistration = new javax.swing.JComboBox();
			ivjJComboBoxRegistration.setName("JComboBoxRegistration");
			ivjJComboBoxRegistration.setBounds(89, 21, 130, 21);
			// user code begin {1}

			ivjJComboBoxRegistration.addItem("ALL");
			ivjJComboBoxRegistration.addItem("EVENTS");
			ivjJComboBoxRegistration.addItem("ALARMS");			
			ivjJComboBoxRegistration.addItem("CALCULATED");
			ivjJComboBoxRegistration.addItem("ACCUMULATOR");
			ivjJComboBoxRegistration.addItem("ANALOG");
			ivjJComboBoxRegistration.addItem("STATUS");			
			//ivjJComboBoxRegistration.addItem("LOAD CONTROL");

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJComboBoxRegistration;
}
/**
 * Return the JFrameContentPane property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJFrameContentPane() {
	if (ivjJFrameContentPane == null) {
		try {
			ivjJFrameContentPane = new javax.swing.JPanel();
			ivjJFrameContentPane.setName("JFrameContentPane");
			ivjJFrameContentPane.setLayout(null);
			getJFrameContentPane().add(getJPanel2(), getJPanel2().getName());
			getJFrameContentPane().add(getJTabbedPane(), getJTabbedPane().getName());
			getJFrameContentPane().add(getJPanel3(), getJPanel3().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJFrameContentPane;
}
/**
 * Return the JLabelHrs property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelHrs() {
	if (ivjJLabelHrs == null) {
		try {
			ivjJLabelHrs = new javax.swing.JLabel();
			ivjJLabelHrs.setName("JLabelHrs");
			ivjJLabelHrs.setText("Hours");
			ivjJLabelHrs.setBounds(8, 29, 37, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelHrs;
}
/**
 * Return the JLabelMins property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMins() {
	if (ivjJLabelMins == null) {
		try {
			ivjJLabelMins = new javax.swing.JLabel();
			ivjJLabelMins.setName("JLabelMins");
			ivjJLabelMins.setText("Minutes");
			ivjJLabelMins.setBounds(132, 29, 45, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMins;
}
/**
 * Return the JLabelPri1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri1() {
	if (ivjJLabelPri1 == null) {
		try {
			ivjJLabelPri1 = new javax.swing.JLabel();
			ivjJLabelPri1.setName("JLabelPri1");
			ivjJLabelPri1.setText("Priority 1");
			ivjJLabelPri1.setBounds(18, 62, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri1;
}
/**
 * Return the JLabelPri2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri2() {
	if (ivjJLabelPri2 == null) {
		try {
			ivjJLabelPri2 = new javax.swing.JLabel();
			ivjJLabelPri2.setName("JLabelPri2");
			ivjJLabelPri2.setText("Priority 2");
			ivjJLabelPri2.setBounds(18, 100, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri2;
}
/**
 * Return the JLabelPri3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri3() {
	if (ivjJLabelPri3 == null) {
		try {
			ivjJLabelPri3 = new javax.swing.JLabel();
			ivjJLabelPri3.setName("JLabelPri3");
			ivjJLabelPri3.setText("Priority 3");
			ivjJLabelPri3.setBounds(18, 139, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri3;
}
/**
 * Return the JLabelPri4 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri4() {
	if (ivjJLabelPri4 == null) {
		try {
			ivjJLabelPri4 = new javax.swing.JLabel();
			ivjJLabelPri4.setName("JLabelPri4");
			ivjJLabelPri4.setText("Priority 4");
			ivjJLabelPri4.setBounds(278, 62, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri4;
}
/**
 * Return the JLabelPri5 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri5() {
	if (ivjJLabelPri5 == null) {
		try {
			ivjJLabelPri5 = new javax.swing.JLabel();
			ivjJLabelPri5.setName("JLabelPri5");
			ivjJLabelPri5.setText("Priority 5");
			ivjJLabelPri5.setBounds(278, 100, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri5;
}
/**
 * Return the JLabelPri6 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelPri6() {
	if (ivjJLabelPri6 == null) {
		try {
			ivjJLabelPri6 = new javax.swing.JLabel();
			ivjJLabelPri6.setName("JLabelPri6");
			ivjJLabelPri6.setText("Priority 6");
			ivjJLabelPri6.setBounds(278, 139, 54, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelPri6;
}
/**
 * Return the JLabelRegistration1 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelRegistration() {
	if (ivjJLabelRegistration == null) {
		try {
			ivjJLabelRegistration = new javax.swing.JLabel();
			ivjJLabelRegistration.setName("JLabelRegistration");
			ivjJLabelRegistration.setText("Registration");
			ivjJLabelRegistration.setBounds(11, 25, 72, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelRegistration;
}
/**
 * Return the JLabelSecs property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSecs() {
	if (ivjJLabelSecs == null) {
		try {
			ivjJLabelSecs = new javax.swing.JLabel();
			ivjJLabelSecs.setName("JLabelSecs");
			ivjJLabelSecs.setText("Seconds");
			ivjJLabelSecs.setBounds(247, 29, 55, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSecs;
}
/**
 * Return the JLabelTitle property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelTitle() {
	if (ivjJLabelTitle == null) {
		try {
			ivjJLabelTitle = new javax.swing.JLabel();
			ivjJLabelTitle.setName("JLabelTitle");
			ivjJLabelTitle.setText("Title");
			ivjJLabelTitle.setBounds(12, 14, 27, 17);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelTitle;
}
/**
 * Return the JPanel1 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel1() {
	if (ivjJPanel1 == null) {
		try {
			ivjJPanel1 = new javax.swing.JPanel();
			ivjJPanel1.setName("JPanel1");
			ivjJPanel1.setLayout(null);
			getJPanel1().add(getJButtonPrinterSetup(), getJButtonPrinterSetup().getName());
			getJPanel1().add(getJLabelRegistration(), getJLabelRegistration().getName());
			getJPanel1().add(getJComboBoxRegistration(), getJComboBoxRegistration().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel1;
}
/**
 * Return the JPanel2 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel2() {
	if (ivjJPanel2 == null) {
		try {
			ivjJPanel2 = new javax.swing.JPanel();
			ivjJPanel2.setName("JPanel2");
			ivjJPanel2.setLayout(new java.awt.GridBagLayout());
			ivjJPanel2.setBounds(5, 305, 507, 37);

			java.awt.GridBagConstraints constraintsJButtonExit = new java.awt.GridBagConstraints();
			constraintsJButtonExit.gridx = 3; constraintsJButtonExit.gridy = 1;
			constraintsJButtonExit.ipadx = 30;
			constraintsJButtonExit.insets = new java.awt.Insets(5, 3, 5, 2);
			getJPanel2().add(getJButtonExit(), constraintsJButtonExit);

			java.awt.GridBagConstraints constraintsJButtonSave = new java.awt.GridBagConstraints();
			constraintsJButtonSave.gridx = 2; constraintsJButtonSave.gridy = 1;
			constraintsJButtonSave.ipadx = 22;
			constraintsJButtonSave.insets = new java.awt.Insets(5, 5, 5, 2);
			getJPanel2().add(getJButtonSave(), constraintsJButtonSave);

			java.awt.GridBagConstraints constraintsJButtonSaveExit = new java.awt.GridBagConstraints();
			constraintsJButtonSaveExit.gridx = 1; constraintsJButtonSaveExit.gridy = 1;
			constraintsJButtonSaveExit.ipadx = 6;
			constraintsJButtonSaveExit.insets = new java.awt.Insets(5, 215, 5, 5);
			getJPanel2().add(getJButtonSaveExit(), constraintsJButtonSaveExit);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel2;
}
/**
 * Return the JPanel3 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel3() {
	if (ivjJPanel3 == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitlePosition(2);
			ivjLocalBorder.setTitle("Automatic Output Time (Set all to 0 to disable)");
			ivjJPanel3 = new javax.swing.JPanel();
			ivjJPanel3.setName("JPanel3");
			ivjJPanel3.setBorder(ivjLocalBorder);
			ivjJPanel3.setLayout(null);
			ivjJPanel3.setBounds(12, 244, 495, 59);
			getJPanel3().add(getJTextFieldTimeHours(), getJTextFieldTimeHours().getName());
			getJPanel3().add(getJTextFieldTimeMinutes(), getJTextFieldTimeMinutes().getName());
			getJPanel3().add(getJTextFieldTimeSeconds(), getJTextFieldTimeSeconds().getName());
			getJPanel3().add(getJLabelHrs(), getJLabelHrs().getName());
			getJPanel3().add(getJLabelMins(), getJLabelMins().getName());
			getJPanel3().add(getJLabelSecs(), getJLabelSecs().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel3;
}
/**
 * Return the JPanel4 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel4() {
	if (ivjJPanel4 == null) {
		try {
			ivjJPanel4 = new javax.swing.JPanel();
			ivjJPanel4.setName("JPanel4");
			ivjJPanel4.setLayout(null);
			getJPanel4().add(getJCheckBoxDisable(), getJCheckBoxDisable().getName());
			getJPanel4().add(getJLabelPri1(), getJLabelPri1().getName());
			getJPanel4().add(getJLabelPri2(), getJLabelPri2().getName());
			getJPanel4().add(getJLabelPri3(), getJLabelPri3().getName());
			getJPanel4().add(getJComboBoxColor3(), getJComboBoxColor3().getName());
			getJPanel4().add(getJComboBoxColor2(), getJComboBoxColor2().getName());
			getJPanel4().add(getJComboBoxColor1(), getJComboBoxColor1().getName());
			getJPanel4().add(getJLabelPri4(), getJLabelPri4().getName());
			getJPanel4().add(getJLabelPri5(), getJLabelPri5().getName());
			getJPanel4().add(getJLabelPri6(), getJLabelPri6().getName());
			getJPanel4().add(getJComboBoxColor4(), getJComboBoxColor4().getName());
			getJPanel4().add(getJComboBoxColor6(), getJComboBoxColor6().getName());
			getJPanel4().add(getJComboBoxColor5(), getJComboBoxColor5().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel4;
}
/**
 * Return the JPanel5 property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanel5() {
	if (ivjJPanel5 == null) {
		try {
			ivjJPanel5 = new javax.swing.JPanel();
			ivjJPanel5.setName("JPanel5");
			ivjJPanel5.setLayout(null);
			getJPanel5().add(getJLabelTitle(), getJLabelTitle().getName());
			getJPanel5().add(getJTextFieldTitle(), getJTextFieldTitle().getName());
			getJPanel5().add(getJScrollPaneTable(), getJScrollPaneTable().getName());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanel5;
}
/**
 * Return the JScrollPaneTable property value.
 * @return javax.swing.JScrollPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JScrollPane getJScrollPaneTable() {
	if (ivjJScrollPaneTable == null) {
		try {
			ivjJScrollPaneTable = new javax.swing.JScrollPane();
			ivjJScrollPaneTable.setName("JScrollPaneTable");
			ivjJScrollPaneTable.setVerticalScrollBarPolicy(javax.swing.JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneTable.setHorizontalScrollBarPolicy(javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ivjJScrollPaneTable.setBounds(12, 43, 467, 132);
			getJScrollPaneTable().setViewportView(getJTableColumn());
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJScrollPaneTable;
}
/**
 * Return the JTabbedPane property value.
 * @return javax.swing.JTabbedPane
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTabbedPane getJTabbedPane() {
	if (ivjJTabbedPane == null) {
		try {
			ivjJTabbedPane = new javax.swing.JTabbedPane();
			ivjJTabbedPane.setName("JTabbedPane");
			ivjJTabbedPane.setBounds(9, 12, 497, 219);
			ivjJTabbedPane.insertTab("Setup", null, getSetupPage(), null, 0);
			ivjJTabbedPane.insertTab("Alarms", null, getJPanel4(), null, 1);
			ivjJTabbedPane.insertTab("Print Out", null, getJPanel5(), null, 2);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTabbedPane;
}
/**
 * Return the JTableColumn property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableColumn() {
	if (ivjJTableColumn == null) {
		try {
			ivjJTableColumn = new javax.swing.JTable();
			ivjJTableColumn.setName("JTableColumn");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableColumn.getTableHeader());
			ivjJTableColumn.setToolTipText("Print out column info");
			ivjJTableColumn.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
			ivjJTableColumn.setBounds(0, 0, 200, 200);
			ivjJTableColumn.setAutoCreateColumnsFromModel(true);
			// user code begin {1}

			initTableDefaults();
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableColumn;
}
/**
 * Return the JTextFieldOutputTime property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTimeHours() {
	if (ivjJTextFieldTimeHours == null) {
		try {
			ivjJTextFieldTimeHours = new javax.swing.JTextField();
			ivjJTextFieldTimeHours.setName("JTextFieldTimeHours");
			ivjJTextFieldTimeHours.setToolTipText("How often output will be sent");
			ivjJTextFieldTimeHours.setText("0");
			ivjJTextFieldTimeHours.setBounds(44, 26, 59, 21);
			// user code begin {1}

			javax.swing.text.Document rangeOne = new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 999999 );
			ivjJTextFieldTimeHours.setDocument(rangeOne);
			ivjJTextFieldTimeHours.setText("0");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeHours;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTimeMinutes() {
	if (ivjJTextFieldTimeMinutes == null) {
		try {
			ivjJTextFieldTimeMinutes = new javax.swing.JTextField();
			ivjJTextFieldTimeMinutes.setName("JTextFieldTimeMinutes");
			ivjJTextFieldTimeMinutes.setText("0");
			ivjJTextFieldTimeMinutes.setBounds(181, 26, 34, 21);
			// user code begin {1}
			
			javax.swing.text.Document rangeOne = new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 59 );
			ivjJTextFieldTimeMinutes.setDocument(rangeOne);
			ivjJTextFieldTimeMinutes.setText("0");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeMinutes;
}
/**
 * Return the JTextField2 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTimeSeconds() {
	if (ivjJTextFieldTimeSeconds == null) {
		try {
			ivjJTextFieldTimeSeconds = new javax.swing.JTextField();
			ivjJTextFieldTimeSeconds.setName("JTextFieldTimeSeconds");
			ivjJTextFieldTimeSeconds.setText("0");
			ivjJTextFieldTimeSeconds.setBounds(301, 26, 34, 21);
			// user code begin {1}
			
			javax.swing.text.Document rangeOne = new com.cannontech.common.gui.unchanging.LongRangeDocument( 0, 59 );
			ivjJTextFieldTimeSeconds.setDocument(rangeOne);
			ivjJTextFieldTimeSeconds.setText("0");
			
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTimeSeconds;
}
/**
 * Return the JTextField1 property value.
 * @return javax.swing.JTextField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTextField getJTextFieldTitle() {
	if (ivjJTextFieldTitle == null) {
		try {
			ivjJTextFieldTitle = new javax.swing.JTextField();
			ivjJTextFieldTitle.setName("JTextFieldTitle");
			ivjJTextFieldTitle.setBounds(45, 12, 160, 21);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTextFieldTitle;
}
/**
 * Return the SetupPage property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getSetupPage() {
	if (ivjSetupPage == null) {
		try {
			ivjSetupPage = new javax.swing.JPanel();
			ivjSetupPage.setName("SetupPage");
			ivjSetupPage.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsJPanel1 = new java.awt.GridBagConstraints();
			constraintsJPanel1.gridx = 1; constraintsJPanel1.gridy = 1;
			constraintsJPanel1.fill = java.awt.GridBagConstraints.BOTH;
			constraintsJPanel1.weightx = 1.0;
			constraintsJPanel1.weighty = 1.0;
			constraintsJPanel1.ipadx = -10;
			constraintsJPanel1.ipady = -26;
			constraintsJPanel1.insets = new java.awt.Insets(8, 8, 18, 12);
			getSetupPage().add(getJPanel1(), constraintsJPanel1);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjSetupPage;
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 2:22:23 PM)
 */
public ConstantTableModel getTableModel() 
{
	return (ConstantTableModel)getJTableColumn().getModel();
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(java.lang.Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Insert the method's description here.
 * Creation date: (7/19/00 3:47:14 PM)
 */
private void initComboBoxes()
{
	javax.swing.JComboBox colorComboBoxes[] =
	{ 	
		getJComboBoxColor1(),
		getJComboBoxColor2(),
		getJComboBoxColor3(),
		getJComboBoxColor4(),
		getJComboBoxColor5(),
		getJComboBoxColor6()
	};

	for(int i=0;i<colorComboBoxes.length;i++)
	{
		colorComboBoxes[i].addItem("Green");
		colorComboBoxes[i].addItem("Red");
		colorComboBoxes[i].addItem("White");
		colorComboBoxes[i].addItem("Yellow");
		colorComboBoxes[i].addItem("Blue");
		colorComboBoxes[i].addItem("Cyan");
		colorComboBoxes[i].addItem("Black");
		colorComboBoxes[i].addItem("Magenta");
		colorComboBoxes[i].addItem("Orange");
		colorComboBoxes[i].addItem("Pink");
		colorComboBoxes[i].addItem("Gray");
	}
	
	for(int i=0;i<colorComboBoxes.length;i++)
	{
		colorComboBoxes[i].setSelectedIndex( getDBColors()[i] );
	}
	
	for(int i=0;i<colorComboBoxes.length;i++) // disable all boxes
		colorComboBoxes[i].setEnabled(false);

}
/**
 * Initializes connections
 * @exception java.lang.Exception The exception description.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getJButtonSaveExit().addActionListener(ivjEventHandler);
	getJButtonSave().addActionListener(ivjEventHandler);
	getJButtonExit().addActionListener(ivjEventHandler);
	getJButtonPrinterSetup().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("LoggerMainFrame");
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setResizable(false);
		setSize(517, 407);
		setTitle("Logger Config");
		setContentPane(getJFrameContentPane());
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}

	initComboBoxes();

	retrieveParameters();
	
	// user code end
}
/**
 * Insert the method's description here.
 * Creation date: (7/20/00 5:04:32 PM)
 */
private void initTableDefaults()
{
	String[] columns = 
	{
		"Column Number",
		"Column Name",
		"Column Length",
		"Column Description"
	};

	getJTableColumn().setModel( new ConstantTableModel(columns) );
	
	for( int i = 0; i < Logger.COLUMN_LENGTHS.length; i++ )
	{
		Object[] cells = 
		{
			String.valueOf( i+1 ), // column number
			Logger.DEFAULT_COLUMN_NAMES[i], // column names
			String.valueOf( Logger.COLUMN_LENGTHS[i] ),  // column lengths
			"" // column description
		};
		
		getTableModel().addRow( cells );
	}

	int[] readOnlyColumns = { 0, 2 };			
	getTableModel().setReadOnlyColumns( readOnlyColumns );
}
/**
 * Comment
 */
public void jButtonExit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	System.exit(0);
	return;
}
/**
 * Comment
 */
public void jButtonPrinterSetup_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{

	/* FIND A WAY TO GET THE PRINTER SELECTED AND WRITE IT TO THE
	   PARAMETERS FILE */
	   
/*	java.awt.print.PrinterJob printerJob = java.awt.print.PrinterJob.getPrinterJob();
	
	printerJob.printDialog();

com.cannontech.clientutils.CTILogger.info(	printerJob.getUserName() + "  " + 
	printerJob.toString() );
*/

	return;
}
/**
 * Comment
 */
public void jButtonSave_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	writeParameters();
	
	return;
}
/**
 * Comment
 */
public void jButtonSaveExit_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	writeParameters();
	System.exit(0);
	
	return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) 
{
	try 
	{
		javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

		LoggerMainFrame aLoggerMainFrame;
		aLoggerMainFrame = new LoggerMainFrame();
		aLoggerMainFrame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		aLoggerMainFrame.setVisible(true);
	} 
	catch (Throwable exception) 
	{
		System.err.println("Exception occurred in main() of javax.swing.JFrame");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (5/17/00 10:20:28 AM)
 * Version: <version>
 */
private void retrieveParameters() 
{
	if( parametersFile.parametersExisted() )
	{
		try
		{
			getJTextFieldTimeHours().setText( parametersFile.getParameterValue("OUTPUT_HOURS", "0") );
			getJTextFieldTimeMinutes().setText( parametersFile.getParameterValue("OUTPUT_MINUTES", "0") );
			getJTextFieldTimeSeconds().setText( parametersFile.getParameterValue("OUTPUT_SECONDS", "0") );
			getJComboBoxRegistration().setSelectedItem( parametersFile.getParameterValue("REGISTRATION", "ALL") );
			getJCheckBoxDisable().setSelected( !Boolean.getBoolean( parametersFile.getParameterValue("COLOR_TOGGLE", "false") ) );
			getJTextFieldTitle().setText( parametersFile.getParameterValue("PRINT_TITLE", "") );


			int backIndex = (PARAMETER_LIST.length - 1) - (Logger.COLUMN_LENGTHS.length * 2 - 1);

			getTableModel().removeAllRows();
			
			for( int i = Logger.COLUMN_LENGTHS.length-1, j=1; i >= 0; i--,j++ )
			{
				Object[] cells = 
				{
					String.valueOf( j ), // column number
					parametersFile.getParameterValue( PARAMETER_LIST[ backIndex ], "0"), // column name
					String.valueOf( Logger.COLUMN_LENGTHS[j-1] ), // column length
					parametersFile.getParameterValue( PARAMETER_LIST[ backIndex + Logger.COLUMN_LENGTHS.length ], "0" ) // column description
				};
				
				backIndex++;
				
				getTableModel().addRow( cells );
			}
		}
		catch( com.cannontech.clientutils.parametersfile.ParameterNotFoundException ex )
		{
			com.cannontech.clientutils.CTILogger.info(ex.getMessage()); // no biggy, keep going
		}
		
	}
		
}
/**
 * Comment
 */
private void writeParameters() 
{

	if( getJTableColumn().isEditing() )
		getJTableColumn().getCellEditor().stopCellEditing();
	
	String[] paramValues = new String[ PARAMETER_LIST.length ];
	
	paramValues[0] = getJComboBoxRegistration().getSelectedItem().toString();
	paramValues[1] = String.valueOf( !getJCheckBoxDisable().isSelected() );
	paramValues[2] = getJTextFieldTimeHours().getText().equalsIgnoreCase("") ? "0" : getJTextFieldTimeHours().getText();
	paramValues[3] = getJTextFieldTimeMinutes().getText().equalsIgnoreCase("") ? "0" : getJTextFieldTimeMinutes().getText();
	paramValues[4] = getJTextFieldTimeSeconds().getText().equalsIgnoreCase("") ? "0" : getJTextFieldTimeSeconds().getText();
	paramValues[5] = getJTextFieldTitle().getText();  //PRINT_TITLE

		
	Object[] colNames = getTableModel().getColumnData(1);
	Object[] colDescription = getTableModel().getColumnData(3);


	int colInfoIndex = (PARAMETER_LIST.length - 1) - (Logger.COLUMN_LENGTHS.length * 2 - 1);
	// write the column names
	for( int i = 0; i < Logger.COLUMN_LENGTHS.length; i++ )
		paramValues[colInfoIndex++] = colNames[i].toString();

	// write the column descriptions
	for( int i = 0; i < Logger.COLUMN_LENGTHS.length; i++ )
		paramValues[colInfoIndex++] = colDescription[i].toString();
	
		
	//update the non dynamic params
	parametersFile.updateValues( PARAMETER_LIST, paramValues );

	return;
}
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88G7DF854ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E13DBD8FDCD4D55634D9D2D9E1CBC5C549969995A51BD4E4E8465AFE145452E6C5E59B3516EED9393B6E2E155F57630F08C90A020AE69B15A6160B262004087C3385C4A199114A4201991801E1E618F9832302FE675E7B4E1D775E3C99107D7C7C7D0E736E79F36F39771EFB6F39775C7790F2BF89DB12121AAB88A9CB84695F1E94C1D8FC11A064673C7BD744859863B284593FCF81D689CF652443EF9050
	42DF0F4B48964A6C19108E07F4583C380CF5701CA3D45A977C829EFC14938F3405271E3942FFB9776C42F21652741F0B3260F785C08460486FC2A3A9FFD9F11E04CF107088A92902B02B1370ECAA5EA861CAA05D849084905E4978E361F799AD79225292096F7878B0E161E3DBD2BFA4794872C4585845E4AB245FFCE1D24A3CAED2562162ACC9468D69A8GD279B4614355BB61372C29272B3C4356DB582137DAEDD65158E1AA34583A3A0C0E428E1B35535CD5DA5AC0130774E66B378EFD2FD1908CA6126F5545
	19B49FC15886698594973F0E1495814F538112B87E4ABC02AFB247E5B4G984CD83FFDDB4AA47DDBCFCF9296BFEBCDDB6923FE03E6D62FF03327FE412EF7172E27FD7117A2526649C0CB6F0E4B4883A883B88EB08870913501F7F6FD893F156DE2352D3757E62D345B2BAC8647A11B41E8016752D220C838A3468E1B43A0882C1F1B0B37E968638F181C7E1C65D81BC46C42BE0FBCB2D118BD79E06AD20DB6199D31A3B5381B35C5E43727CDD859577BACBB21DBE9134A320F89723267DFECCBDB22D1F67453DBD3
	1325FC964A4A5E54C3479F6D6BDCF8CE116C03613F25F8138C4F6C7389EAB3A46D865A6C9E340D569528CBC37A75C276578D1A6D9092D719DAE1E1BA34DA543A242DA7F90F3130B217F1DD32A9BECC06E73ADCCDF5A469D820657746E5B07BFAEFB7992FE3A09D869083108830GA04B8AFCD634312A226DA33431A63DC32CF219AD8623C3E0326F952D05DF23DBF4686B2CCED1EF6DB036689DE6FD3B456894A41E74E252866D8AB9B65FF4C0997FB6DA0D8E3DE836D9A1F79829225161E4BC2B0BD7919E0711D2
	F566DEA39450EB97B05F8D45B970EB51BB4523F603DEB4FAB8987D33E252A67D7A964891B200E756AE85A5243DAA207E2DGA65E8EB7527C8E9A9DD62305CCA0369E17DDC7EAA244D8C9BB7F1A4E9D03709CEC45B65E748D41AD06F42E8D7319D53CDA16CF0D3903282FF71C44B1D615C76664829B5A4CDB2DE8B3B59F5EAAECFB3ADE53E6A85D46EC6538CDBDF69AE81EC4AF161E352EF518F1DBE0539ED3A31FAB5E9C360C34D1663F027755D525F4AC002EE1G53F99B2F5C1D4FEC36CA6FB42215B6BA40400C
	86CF0BB3BD172D4F165AB3CD52FD992F43DACACB02FC85FBDCC681C0139D5B70063CD6195C8A196CE86B645D0651348C127F1273D086EB2BA5FB487AE46CB7DA459A235DE8B54003D322E56DA1E5C963375346E5D6D33C9D46FBB58CB0A747676DA1E3D2AF0A8EF33BCBB4B69BC551EC6D725073E9D9CED73B5DB3D5E0D9DB7610BA5A86E03CF37EEDFB48D801C55BEA241D46C5A4FAA19D2BC60B3197142E36392CA243971590FB6FB11E14660A1292627C746BADAEB26FB0DFA1CA3F1C5441E16E85D3A0D31790
	EF276BBD77F5883881C7FC1ED0780D3263189C789C476426D33F922637EDCE55DAD3C2EE2DC94D6EE363E8D31FE7CCB21D36FE232D7BF6BAE7C8F8A753754553245D54CE8B9B83B37712FCEAA07FD6GFB9F0E01BB3FA1F41CE7A4632F36D9AC2CA9F99B0714929BF79A7BDCC6181E4904F610375B045255AADA0B54060CFEDBA9E9C30BECAEECF010B62CDF0F3EE0A0689386B03D0F352360E06D4078D6ECC03E90C8C7B9BCFC098A3EB84E17670C4B58617470B5B965FC7D9F717CG9F851068E4FC59A263E36D
	794E86524754CF6C56776B5D054E8130535252FA5A304C975C39BBD39AEBA7C4747966D2FB8B01F494C03C08767064076873857704894937EC706953A6CBFA243BBC7610FFA17AFCEB7611B2EB0026F3E1BFFE36D73E6ED4BA1D368EB3DD7224BEF8F22F5CDE986E193D9F40EF275961946BE0C03B313FE66FA57A12B5D1C2CBBE02137A8E24EDCCD0EE98C0C9BF41DF3BBCD726FB92608540EF82C82379BD3F1E785BFAEAD0056C2749686834B920918BC84B966A8744C2BA3794D6D27281723C957B61397D6570
	5BDD6592C51B35D95FEFBC60B60BED152A0C9AFC6734F7EB8B7AF9B767D4C8FAA65CFD3DD0F6B6A7D54B6F8EDC79F1EAC0BFEB7B08FECF9F8C5E1311B67F67771632765833815BE3A170EC9B407678C233BD8C83387FD8E1A9D556671C6B424AFF03176F063C23F87945D5DA654F636557893272C755162C7C899FE1790BA16FA2B7167FC62D3AFC8B50025CE88F999B7FB3FBF8FEDB15C77FA6079976EE8ED8C4DC76F32E87DBBF776CA6FB06F050AF466D6D4FA428F04910E63E740FF4EEDE68D67B7BC60ACFBF
	2976775FACC1FFBF97E86E13687F051713F9E6B6249781E41F829F82209640F48A473721EC17B00CFFDDC75A412A37B4AB96CE74F37711F2E12D13527F5CC75A122EF582364349756A3D5318D34A3A072952CCEE2C175CBFD56999AAB9560FAB1B57EB4F2F2CE70146565A6CFD7235F6B664A51C06BEGA881E07370934BB3A5FD1ACE63FCBB742DFC3E0DDD39B3F5390F79B67034B456CFFB467AB63E47BEDCC1560BF9C0CB9F0C4B5888D082F09C408D90BA08FD9431EF5FF0FDC4CDCFE72B86DCAD2C0755EA1F
	0537693E8D236B0B03DEF26A3E089DD44A31767B09375F2CC1FE4EB3CA9D5E9FD6072CA1AD9DACD5284326A15461560FC62743E4AFB9358E47D5BAB0DB6BF028EDCD843E900048A134354F6AD04F9900E37B3A43548FD88C697CB3083B15620AA05D40F10777905C89C83BB94ED6C2F021100EBA037556FFC3FC53D81E6E21E9E277AC7D57126176CDC4569F5D9F30B2323EB91FFBB3B6E66EAA773D2F08AE575ED7CCA9575ED7CCAFDF457010077C14816971657E779D0965DA7B0E0FE9BD9FDD3B12666DF49A1D
	F4905768C53DC41BAB2E91B4769F0769112140563C28754BA41E83222D7AEB8167403FEE7561E3B6694113E3B6997A0D5AA6499EFDB6746F02B3E8133554A7A4F9E61EC5BFF4ED85BD2300F4AB007DAC5A4ED34B55E3A390E821E7193DC41D75369BB68FBEDDC9567599C0CF86D884109DGFEF6G4EFF1D7B07DD238E387D2CD12D953EED212DC25B960C954407F65ADC0E8E233A3FAD95F91C6619E96BC4E3AF699A46632272225E51E5743217C1AA4FE83E64AF2A70E5D32C5F458D72FE5F4947D63BD79FB4C1
	BB9A82D85B3B833C7B0065D77011BC3FA21EDFDF15BA3FF110C71814CF344F7CE6AB72AB96F09E2C3529EDAE9E72188FA0DC00B6F7038367413C8B5066F656921BEB023489A060823439ADF5EA7DC20096F98153EB7A853E74B36EA6E34BE06C543BACB05CEDCEE323DDBA6A83FC56AFC819813B5176E7DD00BEE66CEED2AF0751E9BEC5F71C48BB0B72929F206997688F7FFA0078851D3029032EEB369BBB04C5145738875B40GE9B7C088C08CC052AF301C0BCBC89E22D924E5D4DCC862A74F568ADC7F1BBFA5
	7641F60C55A44AC026A6AB61E5EDF8E9A96ABE66C2EC43E3E5248DE3A1BD8BE07E0558060776295BB0FD0C275D7246F837A173C797D4127C360DE17E68D20EDFCC71B5B2BC73D3B752F903240F83ED7A98DC27DE27BE42260B407F3988F105D98457G69639CF7E5962EF1260BF0EEF8315277581ED329BD36DF2F541E6797D47A1F6797D57A9A13CC3F133C9EB1DC67294B319E899C77CFDE0FA40E8B2CC259020B9177E4954A16F15C6EEA142D859C9BD3193D72B195F2B173E3E740EF5245B82682ECB82696DC
	0C76F0F79F690F0231F19995G2DE3519EBADDF2FB3083FE4CD80F8D040C75E58F9369980D9C2B3607FBA8FE46D835BD145422BD4CA6BA84E29D4B299F94CE529CB7166292A0BD1B636657E1DB4C8B44F1F3AD5DF349163C42E3E62B41B6GE304C4GC4E7E1CDDDF3D3E37301364643ED558D7040FC7A3081F9DC306403BD1AE7EA54473CC47265255F58311FCBE3320E1EA1F66AADCE4FB83CEC8F4E8B2D409FF48941BD30C73E0FA5325B613FC278E574B794F4E2F38784B97D6392B91D683FBCDDCEEFD45009
	CEDFCB722C2EB3B7637EE5714C8841F26FB7BE4F0B1224BAAE16FE1BDABE97B6DD7A5ED2164F6FFE1437D976751F262678482768D2A916FA29BAB6F0E529FA6E91AF7558D760253EECEDA61D4BC2AFD55B5A33949FFD295A56FEAAC3DBCBG5A0E4B50068C543F8906F484477D319C6DAA61B29C93FF27F35A82C8E70E83DFFC9C62D32B881E1C891273C3F25648701FD5E87938C4DE0DA379E9793179BE70AB84DC1B77EA162165BBAF71130F2FB218CDCF20F3D02F55880E1739032DF5B9F47E3A8D20EC9C0E3B
	F9D5E49FA142E44B562AD76A0905D84651F9A12BE618F26450GB28456C836F4821E51BED979B753B975CD524FC0E365ADE97014D76C373CA54314B7831E7F25A86FBE5A86FF86C86465959C7214F7285ADFF9CB07A9AF991E77C94FCC372FB2097F2F613B6B746D6DC603E21D2F6845BB837347713B81D6E2131BAE07F583C0F7B95A64C5B675D833DC6E3131004B157696FCB91EC7FD6620F790AED71FC7ED2378184B5567D151FDF89E958F34EDD760389AF291DD03A09D4EF1B3A8AE9A52719CD7BE0063EF7E
	9528FBA4DDAB4A2E84FFGE070CA4447D0BCB9EB8F835CF40EAF2F753547A43278DCF66548773247E53C2C0CE3BE4B100F2BF3594BAED1496588C76B3C471F056266925B3992475FCDF54A71B7F41858A773B7536A51BFD8G7C69C1502FGB5G06A0BCAB7C321164B98652E1C168DBED2C7B8AFEDB745D520E6B0041AC5A9CF44B8548A6078DBCE671A44352480C54AEC1FBC6F7CF63612538AEC5CF39D90839781D94DF67EA5303D8DFA4CB3F73F41FD3BC2BCBF09329EF76F830BD00A6G9340A0C070F82CDB
	875DB7101DA328DFA90A7A8E930DE060063245EC9C702009734E723E7FBB22A94B63763A4D3E5A87F22807215B585E8D1BBB1D3ECBC09FF1E2A95AFB44F834774DE5FC3D990F767BA4DD2F96C0BA7D2A380C0DD7A17EC50AA7676EE45F340363BB4A06BBD361EB10AC4F11768FABE3F044E5E04C585F7A223D5E1C5EC7E225967DC91BCB04FE3E0A344FD57C1CD687E98BC0B0C044D5AC7E40EC7C5DF264497CA5548D208C60B84060AFF10C44FFC46C295E931F104A1CF4C606A7F193895FC64F8D6A1571A10976
	E596795D523CC578042621DC41730F0D41634D82376D17335036B75EF903D0736CBBA91F7B306D50DF32FE197FCB593D336E45CBD9398FAC276576EF114A782DCE60ED77F895253911362CC560FDF720575F185C946C4B4FCA3391FBAB89D67BD9ABA93EA9D86DE7DDEDC5BF4B843404E03471D09B5A7D0E2B9137368F6DDE47F13BA94E8D69F1D723ED97B8485C964253BBE91A44FED8FACA1FBF3BA4F267B297E95B7ED3FE4B78CF4EABBD369F6CA2365FEEF350CDE74C5524BD6EF4113AC6DE0DB6BC3554FBFC
	3C69C25BDF8CFC3957405E906078B5ACBE46FC14238367620309A09BF88D73D1C8BA941E5FFC7F7D779F87E0FA0424911B2F770E15C9F5595ECCE277758D7AF6236508314B6C9471F00551EDB5A46F7AEADBEF3B2D4A66D63288AC5E7749C179BEA6C22ACF42B5988F7C60101C1EAC51B32F45363A300E0E87679649D75ADA0FFB310D5762DE6CD83DFAAF36C7106F25AE29D76F45BED1509F2CD76F455ED6505F2CD76F45EEC9E5F4693EC5A34173B385360CCA345D0DC456ECB50BE63D45FC0AB6CEB3B0DA3B58
	DA74DC8B216B20AEF600E000880058EBF10D1B527819C057B80D756D00214B08FBE2AC6F46A65FE7A961CD435F253B2B6983EA3FE0923DA4FF44CFEEA2E3C83459A91261E21A68B91A394BA4B2D689BF2D0974114558C9514C3EE71522FDA7DD4B6CFB817C260730F30E0DA1740EE4197C8CC4FBFC691691BD588978CE897741EEBA774BBCEB065797FBF8F7C9E717EF1D520A979A20FCB7G1B7F266C433B41C1000B8F4133402FCF10D89F7769C96C3834745F8E3321CA5F55C0C7B3335154F234D161BA7467F7
	1453B99852CDG266BB08E697E8257144809578B8D8187B57771493A1D298157C9F7FD2F732CA9912EF71732B27377E31949576138B83CDFBDAE8EAA6CBAE03FFADCF4244A69776CD70F0BFFA84EB8666CD70F0B65D27E4C36AFE8A573D55E75F199C5G2DGF600B1G21GD1GB3G12G96GE45EGF6G30832086C087E082983C8147422F1A378A3EFDD929A36800F0684DE4C8C87E5A71DEDC2302EE4075E699DD7B22A11DG104C71EBA81E44759682AEAF145F55330E54EF728549D2D9180E3EC13D9709
	7E0F4BD8AAF86FBF70190D43C77859FDD9A87343F470EB8AE5314C00D042732F3E614761A13A460B7AF69DB95EE63E4A272785CE8F7B49B3763E16F08F3B8481FD29FDC67939530E53A929E99AF6CFA0BE1435D3D228672C0E0D53B57DB8CEB3EF04BD7D0DB8CE3BBEA3BEBD3F43EFEE6FADF47577966A5B45C2A318CCE969E1174552A0AD3F98636E4771633E91476CE5B47FA8C84783A483F87B098B76EB61D8FB5CB8A888BC165769A316C7D7D726C34BC1BC6FDF64290B65A3EDB9DCF5A57DAB8F6199420E1B
	50E68B6B4FF58F4746594BAD3E5710F9AD432FA1FFEE217749E06E571D34CB31303F37901F47565969B472356A3D16EC0E2BF3B6591CE632BA323EDDDC0AFDEB027A045D04E760B5FB311F12EE42FEF2507B0B599362B28AGAAA6605C5A5DA3775749FC696B0C34F582332D41891E39F585370DAFCE6339FB6C84AC7393015E6F01F4D6986C4543304D27FB45E1C6B2E6D789CF0CD2EEE979394AB15FBF1C2EC7E5E14C77CF61783BA83ED606E77D313B9C7BC3873418B0BCF318B9C46CFA632F401F83E882B081
	8482047F8A676D20E3BB04E1E23FBA1B3D818C4EA28B59307AED5EBFBA9B3E3A55378D07368EEF433736EA7BC1F736FA7BC1D12D5AFE50FD2DF2BF08619EECA5633A8B562AD64EB71D633E6738C7E81E84F74CEC90CD9CBF136AC57035C6D2A0621F24FA8DD0DC2B04FBC6067BDE423D50CA46A1D459E4F320ECF2AB591FF41AAD96A966C6F94CD698163CBD7EC4F5B45B75B23D5F62B822B75B43BBF99CAB867ABF81E0BEC0564DF1995BEEC61F5ED8CF6AC6F79ACD7AAE4FBD08A7BF752C99C592AED170600AA5
	9FAE6830D67C398172BFF1B34653FE36EA713801BE6EE674735E3421CD0783EE764DB847945BF10E493D8547FB071D70B6C05AGB0F88B4EB171BF4963B079DF25EB7AEF0BFE5A1E9AFC8B1BDBE2EE714CB1333A97CB7748761FA5FDB07F96FE276FB4717BFD79BA640E985BC560BC70D17F68660F2D2314EBBFE7B9B63F3CCF57D4A1DCBD6FE48E90FCD638FA5E994F5778BC208D0663BC7B63C12D72134325772F423D4F8B185C37077C691DF92BAF39543D23EB276CD14A551D339CEB475768DA3871D6F57B3E
	CD7185372A5B573D975B3788E8A137623CEE944878D98C693C09F199E5GBAG93C040C43451B9033B7C5868911B0DDEE7A0B79B409DB7BAE41EB92B63D72765F58C06FCA3A6FA5ABFF6222F732CEB695A1BB8115565C30E1F4856E4991E55F1B34513F45EED307F388DED688EF2F59C6F844A1E4B4A7FD3BFFC787EBDFADE4B105E939B723D863EB024FD2F6265A16DFB95F3077C5F0FFBFB4877FBB9FF9F123F174366586BFAF10EDDF09B6ED77F2BD73DDF3DBFC53E5FFC27D73DDFA5366A21FFE5D56FD77F28
	582F7EE4D56FD7F3A4F926D37CE174738AA2F05E0FEBA236F38252EE002088DCA32E31A2EF84678D22EBC4B2249781E45F4E6FE3FA456CC6E6A3A9765149151E331C94F7277A975C2E1EBB12A93E68F6755CE136635CD18134705BF16E38B7054C9D19F700DF8CD08BF082C08498F7874E9D971FA990FC4F9DECF36619B018CC75D96217B8956B5E12810157FB056C795A7E73BF06234EEB996C6C42C17704E150EED1F760797A338771BCA31663FEBA043864BB506E666F7534475FE44FE7761E7F3639763C1641
	6C41AA781E5FDC4270BE7E99C15B479FA3F87B7817885ABE7E15025C47E7FDD1C5770B69F7425AF9A7760FE3886F2814F15C7CB358E7A76E44BE3B112E3BE3A09D8E908FB09FA0BDB2AEE3E3A40F23D31EA2C86BGC400E000E800D91C67AB6104C63B117C48EF781D5EEBAB499F1F67CB7474C85FF117B1DC67EFB54B423821563D158FE54F78AE6AF2BF7D0D6F552E92EE729B8373ED0F0F9FD64BAD1455AD73AEE5BDB75E0577A62E780E5E8D3A0B4DFD244F257DB3455BE5F8B6A746537502242300B66EEE4F
	79213AFC53A4CF190313146587CD42725369BD0C70C96A58DDB64547CED2476E2E31E36CEE9650CA22F84C193C56AE3545EB32E71B7D7C4F850167358CA9661062FBAEF8A4E578396009946D39E0D60A77DC70DC0A76DC70D20AFCAEE07B57CB8EAA6FF49CA6E8E92F7C56C15C2306C2BFC581A482ACG487EB5464445C3483383F0B5G8600B1G613F467D74772765B1C7F64EEBBF0DF2B300EFB6C06EBDF1998577307B860C6706A16429380745E4DA6157A271C877CE4EA24F984007G44GA481AC3A874BEF
	58C96308BA286437E62345C03E13D0EBF3017F47684F7D3C460BFE48EC85B752F397FEC703B7CF333143E6B5F8F83650FB5B6C4434566141BFF364830E07FC1D080FAC7260A1AFA746A4DF9164FEE05EE4D65F0A499813ACCA53226BB87D53ECAD3A0953B3D72BE31E04BE4869EF67E85103B87D65B5DA74704938EE4C21FEEA8C2493B86E4BA6DCCB96F0DCBD4565C6C33FC763BC907C9DF9AF208852CDGA600G009000E800C40079G697742FA83D0845084E039176F1928FCA024A3G62816681E45E87E5G
	548218G8281A2G62816681E45E0F72CFB4A57A994B24EC72CB4AD553089E8151CAFC3A0CAFBADAE3CD5348C71127CC06543DC8EA9344D9D472A19AE5F86639A55F792B5BAA69F71D860D784D6AEFB264481EBF5410D96327AC8272F5F72560D9C36573261C5F577A4BEC699E3A5FA900FEAC3B9F6DEB110D7BAF9C17C74F4E46C0BA04777987EC8D03F4ACC092472F257805104E0D01360FC1FC68306BC538144FC85607D8959FAB635EE14BD03757C85B1AG5BA366B670FB63B138472DEAD06FF1499D22C281
	7728E203FA0F3BCBC11F58205E630ED750139A54FB5CF502FC0FFB7DA154A9086B94FFC82D53D70ABC5FBD2456C9C95FF6D82D5392453E7D6743EA1DA6CB724CE789B94D6F1A02CE4283987B1A7BB10977EAFD0B2B4BE5AEF409E6CBE9A98DBFD6C9CB1FD447645398C7ABF9G77668F8DD2FF8D5201GE18F609970EF06F08F9BC3F89F407D7D72B358CE85D3B00FEF4E5033GC83B810226A06F5633489BB1055FC9B92BEE5381C51BDDF2D65D26FFD2345903E755ED7A1262AC634D336AB645339056F61B8B94
	F795E4770FF105E53A2FCF41F30CE4DE1F1EB4EC3F3C8791571A0D38A60E5B3F9AF1019C77C5AE6226F3DC5EDA9E57FC90FD06778EFB5F01DA5D48FD1DD8660F10675AD8960BE731694A2661E3537AA66F58F49FFDC516517F351F449D6B2BCD460E1EAA1B3B466CA42EA023FD3B5F63B3B4B94CDF234C4FCA7CCE4477AB713B90FF45FEFA6F90233455B60B4D31CB225D26C1433BAC53B5E898BFFFCED9D6911675E7A53E98714BAA94785D087F34423B8C0C3714EA50F6CB34E68DDA3164D34D7DCEEE0FA724BE
	8B0CC51FE6F6335A27A174D0CEFF7C2896BD0A53279D53224F607462B675BDB2C2CF6274C7CEE85167F37A51F6AD3AB09569AFF4E8151F4B6989C6ADFA8127CF6D522217F17A5DE62D721BB83D3CC70BEE6074A7ACDAF477D49CEF0FDA3D47DBB20F110D1B6A99EF61D3593D97B65ED65A079FEF5B6D5E63ED42924FF83B6267655C8EA90B041F73B535A7194F6F210DBDCE47E8F30743E63110B0827DB8902BEBDD1F3AAD093E313CAD0A1CEA7F1C509327E2ECF00AA82F539C293E5953E04FBD8DFD10758D23BB
	E71860B73E64DB6E2653434915CD5372E3A20715D3FF5B00493DCA65DA270D4C0732CC437308DDF43D8C1826BEA3AE2378F15354E7440F8C627A9A8234453CFD9F9D9ADDBBBDBBCA396CB323BB933F0B2D798F296BFB9F45DB9ED257F77F992C2F9BE8339F62F7F14E0ECE6FFF2690390A694AFE52C9E953F4DF71CAFB1AD6F90392FF10CF391F32356442A57E981FF2B52B356492A47EE41FF2DF65EA49AD1478B39F76A57771DAAD390D927F8E8DB956BF250D58BFB540B378B07AF683949F8D698400B98F238F
	DA5E626B7E1564BDC8BE67BF0F200F3378E174FBF2F408AB0AC3DCFA8B62AC9C7737E3088B67B8CB9B5E671A4DF10BCFA0AE77B7089B570E38860EDBED606F0DF3DCDAA762E2B9EEE1976296F25C2BE67E7D5A9908F375A04E40F1CB78BBEB21B350174E6477A9E681AE6B91EC5B8394DF836963G62A37CFD66BEACFF9C4775B8B15F990F70BBF087FC3CCB67D99D84747D7FA222BF3B18177FB7FA07206837F1998DG86G76ED0E69B9E4DFED36DA4CA4CACD3F94AA60FEF7B15DC38C82EF48EFF1EF7B6FC102
	0B0474745F62387EE6C860B6D9A5FB1EB9F87E4FD5677CBF14B1790C27CE31326783E74EFF59BF1F57B2D81F96537DCF8274679C5E4F219437G5242FFA1AEA5954FBA32B8EEEC9A726D6038F9E948D746F1E2B6721D60382733114F42F1BF527DD4902443B8AEF6B572C5F2DCD5AE72A5F25CAD3948B71B630A56A21F900F382B5772FA44238DD7B40EEEEDBAFC4EF2EC6DFC034E35BB6255EB6AC20AAF0BD72F29C3FC4E2E85DAA4577B73A3DA65E7BFAAFDAF73D1DFEBC71ECECB2EC462EF70A9372CC5CB6E04
	44AF7A14FB771816DC20449F6AD32E2F4DDF7BC67914CB517B131B61D3EEFC3BBF39A41FF26B8CDAF273A5FE61B1DFF2191DDAF23992FF01CF39F73A34644AA47EA61FF22F1B7D554F60D36ED40FBFB937CF398F7C46AA477914DBB14A7BA29B4FD90E0D3B87687A9C7618FABC46D1FC64E36A71F8102F73B1C05BB8133F1B5E27D5FE40CCD64F6019BEFDD527BF3DA3FC4A7DC3C439D860C9D4714D5110E375B2533B060BE62A5F05F7D2FCE6027ADD78777A71FCB28FE8038938274E99245F07A23BD63C3CAE60
	7786368D22EF134C79FF9E747E9ED088290A3467ED206FCA0BB69B6C126DE4D7AC71BC186961191C220CA7FEC60EF824F8E2DEAAFFC79C4ACCFF9C76ECGB5G0647711B878716FB785FCF6331BD2007G44GA481AC627C26AC8F7F7CEC644FFBA2AEE39BC0830085E05C93D84749FDCA7DAE7B50A37F54EA140F847E69G49G426F62B272FF07655D3D4243BFA51747A7012E839081028122B97F13ABBD7C11EB11FF96506781E4A646E594G54A6726F96B68A5CFE5E15BDDF58F87E7D195073DA86DB5FCB69
	BE448475F5A7621ABF61882E79E3B8EE3B8E79A2B8AEC807FC519C175F02FCC99CF7C18B724D6138D52D481779A43F73520AFC399C97580638920E4B53A36EB847C534A3AE00638A8DD8C6B847DDE9C03EA80E7B249379E6F1DCC0A73F9F45F159A664CB7FBD627ACC48174DF1A95D08AB623871BD08EB6538CF79FBE6033FC7FBFB48663131F85973F73673EFEF5D67358C49D76E63BE2B273D9C5CE7653831CE44A5F25C8A7A5D1005104EFB8AF1DD94378D52BA0E0BF3212C69A9ECD75BG19E7467174C91A8E
	67691481F55D896F7FED7BEE70AA63BFFD07DC1DD70ED0BDC856A272EDC0363FADCF45D8D37CD398EB32247A0F5F5D142A0EB595AB6809296AD813B2D6151A260EB5FDAF48E30A1B53797B2E33D02766F47FBADD36523FCED3D72AF5D2FEBF6B6D15EA1DFAD264BA2DDE05BA15F01DAAD6795749314A3FCE612B7C6B744CAA35CEBF264AF5FAE7B56AE467BAED49D16B240C87FF1F2356E92F329DF354BAA95F711D1A2356E915226F12F3F96C126B34E20DFF1DAA5678574931C62D530F8A7A24B5EA1D16A97A6E
	77EB793B4CDCA7E11DFF1DF62E732F13E11DDAA71B02FE4DBA35CEEFCBBA31F1785D9125DF331004A7A43F633EA35CAFFA9A7CA20086G8B40382751CFD9375A43FF1B0E7BC1C01F8E108CA0BC83FE50B348DF1A63618FE961FE9050F5GA2C0B0C0B4678F4E7570DF524A7DA0204F8348FC967CA0005AE771DE5704B664B181EE90A094A096E0B667592E675F24GDCF6D2DC468E00A6GFB9272BC540EBCC1008B871881B087A07DB9546D7EB59E5DA69A10FF9350CBGDA815CGA11C7F79359E7EEBBB797BE4
	C0CFG188F1075BC347373483FAC4F439FE8C27E86201FG88G8883187EBC6A1C570DBC4900DB88107B822C6DG3A9710A72287F9C4408582C4G44834C63BCA57CDD374C59308F82A883B88EB0B89BF55B335E23DB9E7FFEF4A85023G928196GE43F08F95E6464B1F04055G98G46G043F08BE76E697395351EFF408668E3D059D02D6699DCDB60B396324347F2820EBA639BFB1034B1DF191E5CCB6077994F9A747CB9277AE57D3D972BD3374BF20DE4F716F1354826EB840A0C0A8C06C9FB07F2BF6124D
	C2BD0D54D55BAC2EDEAB0B375DB120F8976F8F6C2E0F7092347BCB38DED7270EEE4F7BE57A6864562D9A1D5C7FE846B90617FBD5B34EB13C5C331AF18EFF40665059BC9E316DA51CC3F31B55F32872EE51374D6AB9F43B026EEED64F213FD1506FE8D64F211B85793C7EB80F079838CECB0E7A1F57779CD56BB424201B0F2AF56AD550EFB86ADF27E9BC9E934AF57AFB0BFF1D8A0E29F5FAD4C17F6118FF1D469D732F5324D654E99657E9EE2BFF1D56FF2F56E91D62BE51416F7D6BB4743DFF1D9EE9C31D3212D1
	277FEED36BF4C811677693EA1D16ABF4521FD06B34C6C17F658935CEB7AAF4BA2AC71DEA38CE176B7D6B7410DE2D537D8A7A1F757EF57A3C5D3FCE2F36236FB248F54A69702F53018E35CEC685FD20433FCE37F77857A90147832339CE69C67F76345B68FF5C190C7E6D69FA23DA27FFA9FC6129BC9EB91F6B7437CE7FBAED6972BF6E3E6F722F53E5DD7EF53A5B04BAEDFA99F5FA4D64DF27BC337FF1D7EF762F5320593FCE655D2853F12ED3FF37FFFB0A6876BF6E1E6F76EFCFD9BDEA1DEED16874C48F6A944C
	F54A307857294462FF5CF5DB7C6B94EA712F534F3D28D38257699A2F77C915BA45FB3DCF2A9CF76F767A57E91B577B24CA1D4A9DB897243F42776A8E7F76F413C32D53478AFA22C32D1372DBE729CE35CEF8AF1469B44309BA15F11D7E62D46B245C97EF927D3FF75BA22AF52AD434D32028568977034CEFBC66A26F94483F77AD0B6C97369CB8222B2B2EECE8EB2EBE525850D0D5F9242D32396D700183B587EA187C2FDD3B7D4857B69E297B217130CEBB87EC1777DD58AEA2EF17FF752B5BE51922DEED7D6AF6
	B926E817717D6AF639CDF10777B77D3E7A0A5DFB68BE6D631BC15443E6FA3FC23F33CB720EFC856FBB6CBDC57C70B910DE8C107BC77431DFD644CB37813E620F7E4F44185C6F9571D29D4818C6A4779BC53CF490E402C6A4975DAA178B8719189149BD260817A600CC720864FED044CB9702CC661C11483D6EB7DE6ADB6EC9C53CF4A31435E3C46545A96225B5A053BAA23949A6391C85E482C6A4D75D2D78BE8348C40CC86EA9C53CB496E492C7A4572108174E8119C5A35353B13AFEF854EF5C5137DC7AA83F19
	B2F70048E53D9A17117F2AF73D0AD4385AD7714E65A4BA1E0F3F2A0E4F46D03C69D5F5FC36729446E7C520A53D4A5F5721FB42325762B2483E0D617E1B62F4005B45F1FF52A1DFAF473D25C33E4FB96E7996EF3E17DA3C79E636FA73A536FA733D5C06FCE41DE538575A106F90676B53FB73756B3D7966993C4BFD5360DD6EF31D5EFCFF687466FB4C644D77BB13B75F8F5D5E7A35F5FB6B77FA0FB75F5BBD5EFC27FA3D794E76FA737D8FDD8B6C008BFC8D7B5C68A638E0C8C7F15C94B746D3E6F05C8BA789EE9E
	24336762FAF16FA9015B6EB459733FCF1D7F58550973DA86DBDF75DF10F1A2FD1B15FEE60B61B73ABF746071B3DB122F32462D9D237FE10340F9865D248C3DCB349DA1FFE348C83EF406E7EBD73B71FE632639F83676249B4F470E834E8E9084908510B8974F567EF2F20314E7B579FB6552791A73DB072D17FEC88D73DFF18A73DF40E5A35DDF8AAA7F08FA900515C7F50DEDC78EB4577DF0G5C21D6DDDBF5E34351C30751A77A071B47F56621CF3479245AA79A2F70090E1DD47BC4C45EC33F64245AA7D24ABF
	F8D26D93213CF476F90A1E43BBDCC6E6A70B5D67BA972F2379642E7E87E49C792630C494BEE4FF96B93F5AE66DB4F70942F2721D707CAF72492742C5A11D2460072532256FA467534F17959696BA9D42CA7E17C46B68B753127BF715267D711634D4A13DCE48E06F14578969644570BAE189FDEF3CCEC893EDF637DD2A6333526F92E71FF006BDC9106A05CDC32CA8AC21CAF0AA89B1109E2337D8C4A1CB7EC1E6D1482834689D3DCED1D8C62B105F68026AEC2F840B6A850E8E12363B44FC7262F5FE81E41C2F37
	D872C5DB7E8E721F013DA335D594BE2E358D645BBAC523B55F46A4864C4058EE4CF79A2D10E34A8E8FAC21EF010B42D269EDEFF8125E6906C617DE3B4A272F9109428720154DE196CF66FFADCF6C14A7F64993C572C431BC319BCA266F1208429AD6D750B43F030E18FCB3795A917BF65B563372EF10AE3AA84F675F8B1A773A74B7F4DE77FC3B0DFD6B66B19ABFABF9A3AEC37786FE5BE6C3AE795BA33E7E26C0F3AFB42C283774D4DA8D646F8BD41DE46F41B3595F65127C1D48A391256F7965AE1346B14A7CAF
	D0CB878855F7AD27BEA9GGA406GGD0CB818294G94G88G88G7DF854AC55F7AD27BEA9GGA406GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGGF8A9GGGG
**end of data**/
}
}
