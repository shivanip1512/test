package com.cannontech.loadcontrol.gui;

/**
 * Insert the type's description here.
 * Creation date: (9/19/00 10:16:41 AM)
 * @author: 
 */
import com.cannontech.core.dao.PointDao;
import com.cannontech.loadcontrol.data.LMControlArea;
import com.cannontech.loadcontrol.datamodels.ControlAreaRowData;
import com.cannontech.loadcontrol.datamodels.ControlAreaTriggerTableModel;
import com.cannontech.spring.YukonSpringHook;

public class ControlAreaPanel extends javax.swing.JPanel 
{
	private ControlAreaTriggerTableModel tableModel = null;
	public static final String TEXT_RED = "<@RED@>";
	public static final String TEXT_REGULAR = "regular";
	private javax.swing.JLabel ivjJLabelActualDayPeak = null;
	private javax.swing.JLabel ivjJLabelActualLoad = null;
	private javax.swing.JLabel ivjJLabelActualMonthPeak = null;
	private javax.swing.JLabel ivjJLabelActualProjected = null;
	private javax.swing.JLabel ivjJLabelActualThreshold = null;
	private javax.swing.JLabel ivjJLabelControlState = null;
	private javax.swing.JLabel ivjJLabelDayPeak = null;
	private javax.swing.JLabel ivjJLabelLoad = null;
	private javax.swing.JLabel ivjJLabelMonthPeak = null;
	private javax.swing.JLabel ivjJLabelProjected = null;
	private javax.swing.JLabel ivjJLabelThreshold = null;
	private javax.swing.JPanel ivjJPanelPeak = null;
	private javax.swing.JLabel ivjJLabelStartTime = null;
	private javax.swing.JLabel ivjJLabelStopTime = null;
	private javax.swing.JLabel ivjJLabelActualSecondaryLoad = null;
	private javax.swing.JLabel ivjJLabelActualStartTime = null;
	private javax.swing.JLabel ivjJLabelActualStopTime = null;
	private javax.swing.JLabel ivjJLabelSecondaryLoad = null;
	private javax.swing.JLabel ivjJLabelActualControl = null;
	private javax.swing.JScrollPane ivjJScrollPaneTable = null;
	private javax.swing.JTable ivjJTableTriggers = null;
/**
 * ControlAreaPanel constructor comment.
 */
public ControlAreaPanel() {
	super();
	initialize();
}
/**
 * ControlAreaPanel constructor comment.
 * @param layout java.awt.LayoutManager
 */
public ControlAreaPanel(java.awt.LayoutManager layout) {
	super(layout);
}
/**
 * ControlAreaPanel constructor comment.
 * @param layout java.awt.LayoutManager
 * @param isDoubleBuffered boolean
 */
public ControlAreaPanel(java.awt.LayoutManager layout, boolean isDoubleBuffered) {
	super(layout, isDoubleBuffered);
}
/**
 * ControlAreaPanel constructor comment.
 * @param isDoubleBuffered boolean
 */
public ControlAreaPanel(boolean isDoubleBuffered) {
	super(isDoubleBuffered);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 11:48:37 AM)
 */
public void clear() 
{
	setGUIDefaults();
}
/**
 * Return the JLabel2 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualControl() {
	if (ivjJLabelActualControl == null) {
		try {
			ivjJLabelActualControl = new javax.swing.JLabel();
			ivjJLabelActualControl.setName("JLabelActualControl");
			ivjJLabelActualControl.setFont(new java.awt.Font("Arial", 1, 12));
			ivjJLabelActualControl.setText("INACTIVE");
			// user code begin {1}
			ivjJLabelActualControl.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
			ivjJLabelActualControl.setText( com.cannontech.common.util.CtiUtilities.STRING_NONE );
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualControl;
}
/**
 * Return the JLabelActualDayPeak property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualDayPeak() {
	if (ivjJLabelActualDayPeak == null) {
		try {
			ivjJLabelActualDayPeak = new javax.swing.JLabel();
			ivjJLabelActualDayPeak.setName("JLabelActualDayPeak");
			ivjJLabelActualDayPeak.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualDayPeak.setText("------.--");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualDayPeak;
}
/**
 * Return the JLabelActualLoad property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualLoad() {
	if (ivjJLabelActualLoad == null) {
		try {
			ivjJLabelActualLoad = new javax.swing.JLabel();
			ivjJLabelActualLoad.setName("JLabelActualLoad");
			ivjJLabelActualLoad.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualLoad.setText("------.--");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualLoad;
}
/**
 * Return the JLabelActualMonthPeak property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualMonthPeak() {
	if (ivjJLabelActualMonthPeak == null) {
		try {
			ivjJLabelActualMonthPeak = new javax.swing.JLabel();
			ivjJLabelActualMonthPeak.setName("JLabelActualMonthPeak");
			ivjJLabelActualMonthPeak.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualMonthPeak.setText("------.--");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualMonthPeak;
}
/**
 * Return the JLabelActualProjected property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualProjected() {
	if (ivjJLabelActualProjected == null) {
		try {
			ivjJLabelActualProjected = new javax.swing.JLabel();
			ivjJLabelActualProjected.setName("JLabelActualProjected");
			ivjJLabelActualProjected.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualProjected.setText("------.--");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualProjected;
}
/**
 * Return the JLabelActualSecondaryLoad property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualSecondaryLoad() {
	if (ivjJLabelActualSecondaryLoad == null) {
		try {
			ivjJLabelActualSecondaryLoad = new javax.swing.JLabel();
			ivjJLabelActualSecondaryLoad.setName("JLabelActualSecondaryLoad");
			ivjJLabelActualSecondaryLoad.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualSecondaryLoad.setText("NOT PRESENT");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualSecondaryLoad;
}
/**
 * Return the JLabelActualStartStop property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualStartTime() {
	if (ivjJLabelActualStartTime == null) {
		try {
			ivjJLabelActualStartTime = new javax.swing.JLabel();
			ivjJLabelActualStartTime.setName("JLabelActualStartTime");
			ivjJLabelActualStartTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualStartTime.setText("--:--:-- ----------");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualStartTime;
}
/**
 * Return the JLabel3 property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualStopTime() {
	if (ivjJLabelActualStopTime == null) {
		try {
			ivjJLabelActualStopTime = new javax.swing.JLabel();
			ivjJLabelActualStopTime.setName("JLabelActualStopTime");
			ivjJLabelActualStopTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualStopTime.setText("--:--:-- ----------");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualStopTime;
}
/**
 * Return the JLabelActualThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelActualThreshold() {
	if (ivjJLabelActualThreshold == null) {
		try {
			ivjJLabelActualThreshold = new javax.swing.JLabel();
			ivjJLabelActualThreshold.setName("JLabelActualThreshold");
			ivjJLabelActualThreshold.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelActualThreshold.setText("------.--");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelActualThreshold;
}
/**
 * Return the JLabelControlState property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelControlState() {
	if (ivjJLabelControlState == null) {
		try {
			ivjJLabelControlState = new javax.swing.JLabel();
			ivjJLabelControlState.setName("JLabelControlState");
			ivjJLabelControlState.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelControlState.setText("Control State:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelControlState;
}
/**
 * Return the JLabelDayPeak property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelDayPeak() {
	if (ivjJLabelDayPeak == null) {
		try {
			ivjJLabelDayPeak = new javax.swing.JLabel();
			ivjJLabelDayPeak.setName("JLabelDayPeak");
			ivjJLabelDayPeak.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelDayPeak.setText("Day:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelDayPeak;
}
/**
 * Return the JLabelLoad property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelLoad() {
	if (ivjJLabelLoad == null) {
		try {
			ivjJLabelLoad = new javax.swing.JLabel();
			ivjJLabelLoad.setName("JLabelLoad");
			ivjJLabelLoad.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelLoad.setText("Load:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelLoad;
}
/**
 * Return the JLabelMonthPeak property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelMonthPeak() {
	if (ivjJLabelMonthPeak == null) {
		try {
			ivjJLabelMonthPeak = new javax.swing.JLabel();
			ivjJLabelMonthPeak.setName("JLabelMonthPeak");
			ivjJLabelMonthPeak.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelMonthPeak.setText("Month:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelMonthPeak;
}
/**
 * Return the JLabelProjected property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelProjected() {
	if (ivjJLabelProjected == null) {
		try {
			ivjJLabelProjected = new javax.swing.JLabel();
			ivjJLabelProjected.setName("JLabelProjected");
			ivjJLabelProjected.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelProjected.setText("Projected:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelProjected;
}
/**
 * Return the JLabelSecondaryLoad property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelSecondaryLoad() {
	if (ivjJLabelSecondaryLoad == null) {
		try {
			ivjJLabelSecondaryLoad = new javax.swing.JLabel();
			ivjJLabelSecondaryLoad.setName("JLabelSecondaryLoad");
			ivjJLabelSecondaryLoad.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelSecondaryLoad.setText("Secondary Load:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelSecondaryLoad;
}
/**
 * Return the JLabelStartStopTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStartTime() {
	if (ivjJLabelStartTime == null) {
		try {
			ivjJLabelStartTime = new javax.swing.JLabel();
			ivjJLabelStartTime.setName("JLabelStartTime");
			ivjJLabelStartTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStartTime.setText("Start Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStartTime;
}
/**
 * Return the JLabelStopTime property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelStopTime() {
	if (ivjJLabelStopTime == null) {
		try {
			ivjJLabelStopTime = new javax.swing.JLabel();
			ivjJLabelStopTime.setName("JLabelStopTime");
			ivjJLabelStopTime.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelStopTime.setText("Stop Time:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelStopTime;
}
/**
 * Return the JLabelThreshold property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getJLabelThreshold() {
	if (ivjJLabelThreshold == null) {
		try {
			ivjJLabelThreshold = new javax.swing.JLabel();
			ivjJLabelThreshold.setName("JLabelThreshold");
			ivjJLabelThreshold.setFont(new java.awt.Font("dialog", 0, 12));
			ivjJLabelThreshold.setText("Threshold:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJLabelThreshold;
}
/**
 * Return the JPanelPeak property value.
 * @return javax.swing.JPanel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JPanel getJPanelPeak() {
	if (ivjJPanelPeak == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjLocalBorder.setTitle("Peak");
			ivjJPanelPeak = new javax.swing.JPanel();
			ivjJPanelPeak.setName("JPanelPeak");
			ivjJPanelPeak.setPreferredSize(new java.awt.Dimension(200, 48));
			ivjJPanelPeak.setBorder(ivjLocalBorder);
			ivjJPanelPeak.setLayout(new java.awt.GridBagLayout());
			ivjJPanelPeak.setMinimumSize(new java.awt.Dimension(200, 48));

			java.awt.GridBagConstraints constraintsJLabelDayPeak = new java.awt.GridBagConstraints();
			constraintsJLabelDayPeak.gridx = 1; constraintsJLabelDayPeak.gridy = 1;
			constraintsJLabelDayPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelDayPeak.ipadx = 7;
			constraintsJLabelDayPeak.ipady = 3;
			constraintsJLabelDayPeak.insets = new java.awt.Insets(0, 15, 3, 1);
			getJPanelPeak().add(getJLabelDayPeak(), constraintsJLabelDayPeak);

			java.awt.GridBagConstraints constraintsJLabelActualDayPeak = new java.awt.GridBagConstraints();
			constraintsJLabelActualDayPeak.gridx = 2; constraintsJLabelActualDayPeak.gridy = 1;
			constraintsJLabelActualDayPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualDayPeak.ipadx = 21;
			constraintsJLabelActualDayPeak.ipady = 3;
			constraintsJLabelActualDayPeak.insets = new java.awt.Insets(0, 2, 3, 2);
			getJPanelPeak().add(getJLabelActualDayPeak(), constraintsJLabelActualDayPeak);

			java.awt.GridBagConstraints constraintsJLabelMonthPeak = new java.awt.GridBagConstraints();
			constraintsJLabelMonthPeak.gridx = 3; constraintsJLabelMonthPeak.gridy = 1;
			constraintsJLabelMonthPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelMonthPeak.ipadx = 1;
			constraintsJLabelMonthPeak.ipady = 3;
			constraintsJLabelMonthPeak.insets = new java.awt.Insets(0, 2, 3, 2);
			getJPanelPeak().add(getJLabelMonthPeak(), constraintsJLabelMonthPeak);

			java.awt.GridBagConstraints constraintsJLabelActualMonthPeak = new java.awt.GridBagConstraints();
			constraintsJLabelActualMonthPeak.gridx = 4; constraintsJLabelActualMonthPeak.gridy = 1;
			constraintsJLabelActualMonthPeak.anchor = java.awt.GridBagConstraints.WEST;
			constraintsJLabelActualMonthPeak.ipadx = 17;
			constraintsJLabelActualMonthPeak.ipady = 3;
			constraintsJLabelActualMonthPeak.insets = new java.awt.Insets(0, 3, 3, 20);
			getJPanelPeak().add(getJLabelActualMonthPeak(), constraintsJLabelActualMonthPeak);
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJPanelPeak;
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
			getJScrollPaneTable().setViewportView(getJTableTriggers());
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
 * Return the JTableTriggers property value.
 * @return javax.swing.JTable
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JTable getJTableTriggers() {
	if (ivjJTableTriggers == null) {
		try {
			ivjJTableTriggers = new javax.swing.JTable();
			ivjJTableTriggers.setName("JTableTriggers");
			getJScrollPaneTable().setColumnHeaderView(ivjJTableTriggers.getTableHeader());
			ivjJTableTriggers.setBounds(0, 0, 200, 200);
			// user code begin {1}

			ivjJTableTriggers.setModel( getTableModel() );
			ivjJTableTriggers.createDefaultColumnsFromModel();
			ivjJTableTriggers.setBackground(getBackground());
			ivjJTableTriggers.setDefaultRenderer( Object.class, new ControlAreaCellRenderer() );
			ivjJTableTriggers.setShowGrid(false);

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjJTableTriggers;
}
/**
 * Insert the method's description here.
 * Creation date: (7/25/2001 11:56:05 AM)
 * @return com.cannontech.loadcontrol.datamodels.ControlAreaTriggerTableModel
 */
public com.cannontech.loadcontrol.datamodels.ControlAreaTriggerTableModel getTableModel() 
{
	if( tableModel == null )
		tableModel = new ControlAreaTriggerTableModel();

	return tableModel;
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
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("ControlAreaPanel");
		setLayout(new java.awt.GridBagLayout());
		setSize(717, 60);

		java.awt.GridBagConstraints constraintsJLabelLoad = new java.awt.GridBagConstraints();
		constraintsJLabelLoad.gridx = 1; constraintsJLabelLoad.gridy = 1;
		constraintsJLabelLoad.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelLoad.ipadx = 1;
		constraintsJLabelLoad.ipady = 1;
		constraintsJLabelLoad.insets = new java.awt.Insets(3, 10, 0, 32);
		add(getJLabelLoad(), constraintsJLabelLoad);

		java.awt.GridBagConstraints constraintsJLabelThreshold = new java.awt.GridBagConstraints();
		constraintsJLabelThreshold.gridx = 1; constraintsJLabelThreshold.gridy = 2;
		constraintsJLabelThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelThreshold.ipadx = 2;
		constraintsJLabelThreshold.ipady = 1;
		constraintsJLabelThreshold.insets = new java.awt.Insets(1, 10, 0, 3);
		add(getJLabelThreshold(), constraintsJLabelThreshold);

		java.awt.GridBagConstraints constraintsJLabelControlState = new java.awt.GridBagConstraints();
		constraintsJLabelControlState.gridx = 3; constraintsJLabelControlState.gridy = 1;
		constraintsJLabelControlState.ipadx = 5;
		constraintsJLabelControlState.ipady = 1;
		constraintsJLabelControlState.insets = new java.awt.Insets(3, 2, 0, 3);
		add(getJLabelControlState(), constraintsJLabelControlState);

		java.awt.GridBagConstraints constraintsJLabelProjected = new java.awt.GridBagConstraints();
		constraintsJLabelProjected.gridx = 1; constraintsJLabelProjected.gridy = 3;
		constraintsJLabelProjected.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelProjected.ipadx = 5;
		constraintsJLabelProjected.ipady = 1;
		constraintsJLabelProjected.insets = new java.awt.Insets(0, 10, 5, 4);
		add(getJLabelProjected(), constraintsJLabelProjected);

		java.awt.GridBagConstraints constraintsJLabelStartTime = new java.awt.GridBagConstraints();
		constraintsJLabelStartTime.gridx = 3; constraintsJLabelStartTime.gridy = 2;
		constraintsJLabelStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStartTime.ipadx = 8;
		constraintsJLabelStartTime.ipady = 1;
		constraintsJLabelStartTime.insets = new java.awt.Insets(1, 2, 0, 15);
		add(getJLabelStartTime(), constraintsJLabelStartTime);

		java.awt.GridBagConstraints constraintsJLabelActualLoad = new java.awt.GridBagConstraints();
		constraintsJLabelActualLoad.gridx = 2; constraintsJLabelActualLoad.gridy = 1;
		constraintsJLabelActualLoad.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualLoad.ipadx = 20;
		constraintsJLabelActualLoad.ipady = 1;
		constraintsJLabelActualLoad.insets = new java.awt.Insets(3, 3, 0, 2);
		add(getJLabelActualLoad(), constraintsJLabelActualLoad);

		java.awt.GridBagConstraints constraintsJLabelActualStartTime = new java.awt.GridBagConstraints();
		constraintsJLabelActualStartTime.gridx = 4; constraintsJLabelActualStartTime.gridy = 2;
		constraintsJLabelActualStartTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualStartTime.ipadx = 5;
		constraintsJLabelActualStartTime.ipady = 1;
		constraintsJLabelActualStartTime.insets = new java.awt.Insets(1, 3, 0, 10);
		add(getJLabelActualStartTime(), constraintsJLabelActualStartTime);

		java.awt.GridBagConstraints constraintsJLabelActualControl = new java.awt.GridBagConstraints();
		constraintsJLabelActualControl.gridx = 4; constraintsJLabelActualControl.gridy = 1;
		constraintsJLabelActualControl.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualControl.ipadx = 6;
		constraintsJLabelActualControl.ipady = 3;
		constraintsJLabelActualControl.insets = new java.awt.Insets(3, 3, 0, 30);
		add(getJLabelActualControl(), constraintsJLabelActualControl);

		java.awt.GridBagConstraints constraintsJLabelActualProjected = new java.awt.GridBagConstraints();
		constraintsJLabelActualProjected.gridx = 2; constraintsJLabelActualProjected.gridy = 3;
		constraintsJLabelActualProjected.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualProjected.ipadx = 20;
		constraintsJLabelActualProjected.ipady = 1;
		constraintsJLabelActualProjected.insets = new java.awt.Insets(0, 3, 5, 2);
		add(getJLabelActualProjected(), constraintsJLabelActualProjected);

		java.awt.GridBagConstraints constraintsJLabelActualThreshold = new java.awt.GridBagConstraints();
		constraintsJLabelActualThreshold.gridx = 2; constraintsJLabelActualThreshold.gridy = 2;
		constraintsJLabelActualThreshold.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualThreshold.ipadx = 20;
		constraintsJLabelActualThreshold.ipady = 1;
		constraintsJLabelActualThreshold.insets = new java.awt.Insets(1, 3, 0, 2);
		add(getJLabelActualThreshold(), constraintsJLabelActualThreshold);

		java.awt.GridBagConstraints constraintsJLabelStopTime = new java.awt.GridBagConstraints();
		constraintsJLabelStopTime.gridx = 3; constraintsJLabelStopTime.gridy = 3;
		constraintsJLabelStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelStopTime.ipadx = 1;
		constraintsJLabelStopTime.ipady = 1;
		constraintsJLabelStopTime.insets = new java.awt.Insets(0, 2, 5, 22);
		add(getJLabelStopTime(), constraintsJLabelStopTime);

		java.awt.GridBagConstraints constraintsJLabelActualStopTime = new java.awt.GridBagConstraints();
		constraintsJLabelActualStopTime.gridx = 4; constraintsJLabelActualStopTime.gridy = 3;
		constraintsJLabelActualStopTime.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualStopTime.ipadx = 5;
		constraintsJLabelActualStopTime.ipady = 1;
		constraintsJLabelActualStopTime.insets = new java.awt.Insets(0, 3, 5, 10);
		add(getJLabelActualStopTime(), constraintsJLabelActualStopTime);

		java.awt.GridBagConstraints constraintsJLabelActualSecondaryLoad = new java.awt.GridBagConstraints();
		constraintsJLabelActualSecondaryLoad.gridx = 6; constraintsJLabelActualSecondaryLoad.gridy = 1;
		constraintsJLabelActualSecondaryLoad.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelActualSecondaryLoad.ipadx = 10;
		constraintsJLabelActualSecondaryLoad.ipady = 1;
		constraintsJLabelActualSecondaryLoad.insets = new java.awt.Insets(3, 2, 0, 14);
		add(getJLabelActualSecondaryLoad(), constraintsJLabelActualSecondaryLoad);

		java.awt.GridBagConstraints constraintsJLabelSecondaryLoad = new java.awt.GridBagConstraints();
		constraintsJLabelSecondaryLoad.gridx = 5; constraintsJLabelSecondaryLoad.gridy = 1;
		constraintsJLabelSecondaryLoad.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJLabelSecondaryLoad.ipadx = 5;
		constraintsJLabelSecondaryLoad.ipady = 1;
		constraintsJLabelSecondaryLoad.insets = new java.awt.Insets(3, 11, 0, 1);
		add(getJLabelSecondaryLoad(), constraintsJLabelSecondaryLoad);

		java.awt.GridBagConstraints constraintsJPanelPeak = new java.awt.GridBagConstraints();
		constraintsJPanelPeak.gridx = 5; constraintsJPanelPeak.gridy = 1;
		constraintsJPanelPeak.gridwidth = 2;
constraintsJPanelPeak.gridheight = 3;
		constraintsJPanelPeak.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelPeak.weightx = 1.0;
		constraintsJPanelPeak.weighty = 1.0;
		constraintsJPanelPeak.ipadx = 8;
		constraintsJPanelPeak.ipady = -9;
		constraintsJPanelPeak.insets = new java.awt.Insets(19, 11, 2, 1);
		add(getJPanelPeak(), constraintsJPanelPeak);

		java.awt.GridBagConstraints constraintsJScrollPaneTable = new java.awt.GridBagConstraints();
		constraintsJScrollPaneTable.gridx = 7; constraintsJScrollPaneTable.gridy = 1;
constraintsJScrollPaneTable.gridheight = 3;
		constraintsJScrollPaneTable.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJScrollPaneTable.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJScrollPaneTable.weightx = 1.0;
		constraintsJScrollPaneTable.weighty = 1.0;
		constraintsJScrollPaneTable.ipadx = 163;
		constraintsJScrollPaneTable.ipady = 33;
		constraintsJScrollPaneTable.insets = new java.awt.Insets(1, 2, 0, 1);
		add(getJScrollPaneTable(), constraintsJScrollPaneTable);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}

/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		javax.swing.JFrame frame = new javax.swing.JFrame();
		ControlAreaPanel aControlAreaPanel;
		aControlAreaPanel = new ControlAreaPanel();
		frame.setContentPane(aControlAreaPanel);
		frame.setSize(aControlAreaPanel.getSize());
		frame.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				System.exit(0);
			};
		});
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of javax.swing.JPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Insert the method's description here.
 * Creation date: (4/10/2001 4:22:45 PM)
 */
private void setGUIDefaults() 
{

	//Updating GUI components, so put the updates on the event thread
	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJLabelActualStartTime().setText("--:--:-- ----------");
			getJLabelActualStopTime().setText("--:--:-- ----------");

			getJLabelActualControl().setForeground( java.awt.Color.black);				
			getJLabelActualControl().setText( com.cannontech.common.util.CtiUtilities.STRING_NONE );

			//clear all data from our Trigger table
			getJTableTriggers().getSelectionModel().setSelectionInterval(-1,-1);
			getTableModel().clear();
		}
	});
	
}
/**
 * Insert the method's description here.
 * Creation date: (4/6/2001 2:32:36 PM)
 * @param controlArea com.cannontech.loadcontrol.data.LMControlArea
 */
public void setGUIValues(final com.cannontech.loadcontrol.data.LMControlArea controlArea) 
{
	if( controlArea == null )
		return;

	setGUIDefaults();
	
	//final java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
	
	final java.util.GregorianCalendar start = new java.util.GregorianCalendar();
	final java.util.GregorianCalendar stop = new java.util.GregorianCalendar();
	start.setTime( new java.util.Date() );
	stop.setTime( new java.util.Date() );

	if( controlArea.getDefDailyStartTime() != null && controlArea.getDefDailyStopTime() != null )
	{
		start.set( java.util.GregorianCalendar.SECOND, controlArea.getDefDailyStartTime().intValue() );
		stop.set( java.util.GregorianCalendar.SECOND, controlArea.getDefDailyStopTime().intValue() );		
	}
	else
	{
		Exception e = new Exception("*** LMControlArea has (null) DailyStartTimes in :");
		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	}
	
	//Updating GUI components, so put the updates on the event thread
	javax.swing.SwingUtilities.invokeLater( new Runnable()
	{
		public void run()
		{
			getJLabelActualStartTime().setText( new com.cannontech.clientutils.commonutils.ModifiedDate(start.getTime().getTime()).toString() );
			getJLabelActualStopTime().setText( new com.cannontech.clientutils.commonutils.ModifiedDate(stop.getTime().getTime()).toString() );

			//set the color of our state text
			getJLabelActualControl().setForeground(LMControlArea.getControlAreaStateColor(controlArea));			
				
			getJLabelActualControl().setText( (controlArea.getDisableFlag().booleanValue() ? "Disabled : " : "") +
								LMControlArea.getControlAreaStateString(controlArea.getControlAreaState().intValue()) );

			if( controlArea.getTriggerVector() != null && controlArea.getTriggerVector().size() > 0 )
			{
				for( int i = 0; i < controlArea.getTriggerVector().size(); i++ )
				{
					com.cannontech.loadcontrol.data.LMControlAreaTrigger trigger = (com.cannontech.loadcontrol.data.LMControlAreaTrigger)controlArea.getTriggerVector().get(i);					
					com.cannontech.database.data.lite.LitePoint pt = YukonSpringHook.getBean(PointDao.class).getLitePoint( trigger.getPointId().intValue() );
				  	ControlAreaRowData data = new ControlAreaRowData();
				  	data.setLitePoint(pt);
				  	data.setTrigger(trigger);
					
				  	getTableModel().addRow( data );
				}
			}
		}
	});
}
}
