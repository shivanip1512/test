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
 * This method was created in VisualAge.
 */

private void initStylesForTextPane(javax.swing.JTextPane textPane)
{
   //Initialize some styles.
   javax.swing.text.Style def =
	  javax.swing.text.StyleContext.getDefaultStyleContext().getStyle(javax.swing.text.StyleContext.DEFAULT_STYLE);

	javax.swing.text.Style regular = textPane.addStyle(TEXT_REGULAR, def);
   javax.swing.text.StyleConstants.setFontSize(regular, 14);
   
	javax.swing.text.Style s = textPane.addStyle(com.cannontech.common.gui.util.Colors.BLACK_STR_ID.toLowerCase(), regular);
	javax.swing.text.StyleConstants.setForeground(s, java.awt.Color.cyan);

   s = textPane.addStyle( TEXT_RED, regular);
   javax.swing.text.StyleConstants.setForeground(s, java.awt.Color.red);   
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
/**
 * 
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private static void getBuilderData() {
/*V1.1
**start of data**
	D0CB838494G88G88GE7F954ACGGGGGGGGGGGG8CGGGE2F5E9ECE4E5F2A0E4E1F4E155DC8FD8D457153786937F35559493A246E0A416983BF1DB1B3ADD3A35FC6C2EEDCD573626F1DB5BF5DB5A30DFED63266C56346EFA99E0C081A5C2949443A0E2B0C1C4C5A504C47E8C8AB2D1A2A3E2CA2B83730011474CE4668183826E39FF5F1BE186A49B77E379BC5EF7FF671EF36EBD777F3D6F8D4A4A8BCB0A57E5A1941F04585F39F804222FA3B47F03D766F0EC1A9425C71A3FFC2057D0CDFDA204
	7300A7C7C669B3D0CCE28A4443A19E7DCC14FEBFBCE722FFCB4C3AG8F816CEC29C36859CB8DFB47377352BEEEA719445F99CA03F0B7D084E0B80C13307D8F0732993E0E611C62F5882D6944E96421DC0615C33C89A898A822D3CD3F13D81ED1D14E62ED7343D05CF75E5425E3BD9A1D1CD23A28ECB18B8D6859788DDD58568212CFACE30578A2A0DC78841410F886424A261E2E77BB9C3DA59D963B5DE1D7240E6E9259E131F640334BA117F47559AAAAEAE9245AA5D91AACF6C9863926EE2C7B27C3A9C497C259
	905FC4301793303DE0F8DE81948DC44B725E1B15AC2F2DF71EC5C1AF5A937684A80B41C64BD0EFD34B925C7707649C6277B73331FF0300978194893499885D0E529B008E127AAE597F9604556DCA2D233757E12FF6BAEBE42B6B8307D51261392282B88C3BAAF5B8DCD604E81D068E957B494FF8C4651E1E349C75C944E9DE3FAB2EAEC31B3E5228CB766313CD91253A725B5497CD37D51FD05BBF8FE85BF95B3B7DF95B3E0E343615879D89C9FEEC2FFA6118AE0869895158DEFB9B57F5C7A22E6B55709C4F5A81
	45FB891E56236234AD7E87E99BB81E8B3CE9BD3CED346E66F9B1A5AEC499A7CDFE7D9092D52D0B6921F9585E236605360BF78F60BED5A4C7690DC0EDC08AD0B050CA20E819370D017DA76E31EDB4D9DCCACD1FCD36CAAEC4E5CB3377C1A8F9941725416ED6AC768E29456232D95AE5490DD81AF732F15E5B3D645EA7ED3683ECDC146C124B22589CF650EE691594496526E9AEE63F0653C09F43DC332DD782833DCE4475DE494E04D0363815EBCE2BC511549414FFBD9B7B325F52829AC181BCD33FF49F44BE0F01
	72EF87CA6B65FE78815157A83920C3D7D4189C0E1EBE279917843575623AFA1574ED83BC1783D1DD56DC0C05C1FC355033B3E72FC60F51560133EFF18D723EE1CB4463663ADEDE573FED65F5EDCCFF8A953FF049EFDDE30A6D25F50C6C3EED5ECDF462FC51789F93DA194C3ADE7FED7EFFC354C6D6A2371170A96B5FAD6A6A8707F0DD95C1F90DC0D6BB7731B147C05BEC0D45AD71D6FA45858DCC322A9E27E96B8E60F1D56A176C0AD1F2CAF6ABBC38D99BB19D5829365FCE07E8DBD7896E0476DE8B8D4CAD7056
	8338CDDA9445E5EB6FD324E6C9D1EC76AE15FF0B58F27735BB552E42EDF59D406309E3G5A3BC8AF9F58CB7A015DAE11CC8B9146371336AA49D2AFE43A5651E7D7A82E9C44755DA38D323E62B928A7EDDD6E437D0E4EE7ABAD29388CAEDBAF3882F7DD344E0E5BF1469DBE9FE39A868C4F4BE1902E3273B6B9D0405B641121A5E84D4DC21D3F31B064A923AE4BC15BD711C3ED13B4CF36DC7FF9F710BE43F0B74DEBDFD586043E53AAED8329F9D8CFBD68EF83D29C3C8D7CBAB7C7534FD86557BAE4193AD278F863
	A12C5BADFD5CA7417004BBF423705B3F9C5A6B43EBE1BE247C178EE19F4A1A31006677F747308F49DA6136255F62A9F18FC0BB282838C472CD5782C3F7F0B9F17A398E3E96889E45E50946F120B8877777775379DAA038A78CEDDEFAA0603AA64DC97DDC6AD47DEDF8002F852E9930CDA77026B9391F5A5F500EEB55EE372343C606D4D64657723475C131FDF93BA06C34395CCA83F4988F77C7EE9E4EAF9EF3994C46023BF7FC675CE027BA6EC4B8470EC1F4DE8C3A0B7D91692473E23E409FA7F84A47AACE674B
	0544FFB8EE865EAD979F079F4E4775A13B2374B3014200D683ED845A6666FE884FBB0DA618679AE03D6732DB64E62F8E4C4A7E11A10176B9967F0BA10977B988699C71882973ACA5CADF84C4B14B8846CC9037AAFCFC1EFD378A4219908F830A845A84348DA82BAFCADF8AE486D200C200A201B682ED83B274C7692B00DA7B392E25C4178271E020D5C09B00620032862274E5C0E6A085A898E895508620B820AC8F7001DABDDCD758A8EEF4B89F5A3A4236F1086DF2CCE1D8A4CB1B5577694FB3DAB2747B57EFE6
	BEEA5570A3192F7C251F49FCDD9A009FA66A6897449729BE7C9C9666E930CC244E0BB8CC3F0FFE40F4A8G0E672657D97F60985E6F9D8C109E37E75C1F778460A7A15E6FC373834F21CF667B1FC3A3727D4F21FF157F9A45C107F6C549794F650FBF477EDD3E3FB9B601703EF4874F0332E550512740FA49037BDF34186F06A19E8C9489345EC36B1F0E67FFBD006B39939620E02A59A9F52068C1AC6B9961322590B782D90102004201284D8F0A71D8F949E4E917E4936C94914B4B156CBD82B7F73BA4F737C336
	B25E724A3D0247E63DE6855630DC76238FD559A61763B6CCE992175D102372C048256005AF637D72E40660D5F7A8FD9612A34A5BF771F59F1EAA4D6D2624E77A24E139E37C1DA6DF9DEA6E380E458D3EE9545273B41B3BF6EB4A60F052CCD05E6F9B3246641326207C7EA3B93EFC58335A2D304A216EE7FAFE15231645BB8553E3A575C6E7EC495243653E53C6FC585C814516B117CC7C46A15C9EFEF1B7CB53BF520668DE2094C24AFF6006BF7EDC41AF5C670F9FA6782FD67A632F927CDD577C71572A7A4F7863
	EF92FCF4569FFF2B60277A65A75E617C9FDE77474F967CBBFE7945023F5EEF7E2B84FF33DF3ED970DF754B6F967CF3AD7E78434027EB5FCB4D3EEBDF4C1F877CF011E6FF23B6CD649D1AA696426DC0F49D32658EBFC34A9A0E521B0679995249DBF84FAD5605A51681252262224BE62D31F4194828C45706E543A9CCBEE8182F8D5B0749DA88629B00368C73352167A8DF9B2ED836901926B57ADD9BEEB61F52A50E303578083AB60C687BC3B23539F8045B3482FF518846560DE87D055BBFECD3D18990657F3797
	9F0F4B473C782523DABE4EEFE812165F6E45CF52485332FD371017ED7BF7A3107C55F7822EFBE3D819D2C65939CFCB81BAB27B0F49ECBD5E99E0BDCEC7D0D27E8388179F4F51D5235C37A397346B6E48BD27F429816CFB185D10D15537454237DF3F41F5EF84BE455A76F1AC63AE47CA5F63D83D402A2FF1EC1A405A4EF0EC35404E1D6558D60155882CE89A47F635F26C16403EFC1DE3A182CBEE6158FA016D97D84AE7C41E85D6A5B03B40BC821B5E0CB16C3F151F61EDBA738CEE533190C753E11C184E7BC9D7
	5DA17F75250E3A081F29DEE96436CA27F33DA89E6B6D0678B4201069FC4D764A18FD48C4C4655070A713FBE752F2F4FFD3B602733FF2BA5D5F448B7CBD02C7EAF0BA5EF48F7371E6AD700A9F607B1EF585385D4F03788A20E820CDC05B013203783E2779F0A91AE05FE3F6B8CD303813B54BB136379879E467385FCA667DEA55571FC42B9FFCB5BED03FAA8E227D494442D8F38141E979EE9662728D83AF84A892E883D0AC901A414B97FA3818162F4EEAD32A95455251CDF6323CF0ADB6E9C00571620E16AD8FE9
	4B16B5030D1DAC2C1CB1F6FFC073746D23F8CCEB823E8C94843488A8D264671707A75CE756F97C6EB3E93E3EF9C31B2FCDAC9FDBD918786058FCD1B94BBE2DDCB6CBD74C422A00F2E75E534A19D93AEE968E87143BF0CDABB70F258BE7616A4079BC2315DB4752EDE6E1DCC0394A33DA391407E83ADC9616BE94C86E22171C1125EBE5219CD06E5CE12D5CB416AE18059181653EF6DDAB9749522DE7E1CCC03934962D5CB616AEEDA61BEFE686124B7512ABE3696AD95896D06E0C171C13258BE2E1C8C0B937175C
	8A16EE8D8BB78414FB28194B45C21A6DC099335434C76039FC1637AC9D939FA2F308E9969D93F78ABC18605D9A1C0E15B9844771A060454E62F30339115BB74D0E525F1A6DEDCB19BDB65F540EC2661A1933FD6FF4C6899EB25B77CEC79F4F6FF4A20017B6075B2F98C14297351AE77BF02B0F5DCF13186F6F2B8DBA6E3DD090780CE0D3017FB3021F9778BFA308AD987F8CE0EBC160F3765F97E84F595999F2919E331DD097F301D682C583ED1C43476C99C78EA3B2E67B19BF6A2CDD926C02716D16426DF79786
	AE2B23F062BBB1CFA15EFB36BB9445510B75F3FC34905797ECF789C831878A310FDC362EEE05A6E5782CC23CAE1225CE8253334633E40D9B8765CA1C4B4F9F3F140A31EC089789ECEF9ABF13AC1B4BFD707D23014B3471287F7A7B59D17F7597F3F47CB31EADC78355AF4D5F0FEE70F28409BCEFF91717A3C4E09FD472F2048B4C330F4B4689AC5A41E5D3BE4B31C2A7174D921869BDAEEB9518BC42E5BD82FBDE1C6986896C4F57386C8601393339EC2C40B6E4F359B801F948FA5F78B9586BFE0EE3CB05EC3740
	92F3382CA2306633DCB6DAE0DF96329B8516A9E4E3847697A2DB76F958BFFF1EE3EFA1AE5BAA30B9C81CE18B6C72E1EEF71540FEFA174B2E95587BC2F603403EF71D4B9E19A7FCBF4AE54B857625D1AEEB94D8C18B77F330401A05EC0440D68959D582BB5D426DA64E9FAB1BBDFF2C6C910119042CA230E7C93A19908F91D856DD51A685B6DB64B9CE153D43E5D39E66D84630E81382DBCC76D5D608CF93584BFD988B06F804403ED3456F9E56894CD84375ED9518CC5AC1559720FE3F4067264F1E41E7DEB244E7
	82C5GC58B5EC2420B05F8CAB00CA1C075411C17F7064FABD8465F5CB0B3G9E6D034BFE52D19B6542C6329FBD18128260BBFD627E6E2929CFFA489E339B4AB5B7D80C99848B0378DA01651938BFB7899F8C45633B586285BC9EC46275A21E967FE94C3DD8FF60793D70D33191689E1F0E5919C501670783C5934FF985C5641E9166AA7320D322D8D191BE2FF7F4F63AA5B1379EAF4A90D8033B496136614BCAF6560E30EFBB9DF6A5E281F95F060CADB2BCFB9670737868D41CC6014CECD9005FDB5BD182D15474
	88CEFFAC4DB8C17935D48AB2188A905D3B9A9F21B6485A0E585FC166299040430128BD325DE6762C159359CB9A475E06C778F9893737044CED1B31ADE1EF6824EA2F52B91E3D64G65FB1D594BFDB4CA1F6AE52F0F4C07C50017BD4A6DC51CD26D450CCCD43EE4BF76F8792CE36CBDC866D0A76043425EFF1DD66D7DBAFB22720D674F952073B58DBF93FD7E0CF7FBFA0A4C432BA1DD3430EFB8235ABF18B31EFD2DEDFF7663C067B12F728E1379970540BEA1045B2BBD2B5AFBEBDCFBBBA7A8EFE5086ADFDA5E3E
	335E651DC36C57C3BA33305FF9CE357FA41A487EF8658D821D692CDE283D8132E6188778A2E1EFF139EA6F5A387612A6E8CF6BC2FC7D7BFB324E5888F80C30272FD06DC50FCEE4EFBC7FE6BD66EB0F2E918C0097BF466D1DFFD735775DF16DA5CFD03E3647FC471FB6E22F9BF0C558EB2F3CD7FBBBA730977E5818710058DB89F864E3FC3C7D07D16F71F646FB2A7D376F0EE7BFFD02763365B1BAB629763FC26CEF83BCF121980F5ED76D3DB9BC1E3D54896C15AD746DAFDFED46F9F43A24CE4965122C4D36A1C9
	6465FBE4DDD685B226055C977AFEBC1F35BBDCE433C36F1F8F75737BA48F241B873496E853C27A6E85CD733BAA1EA6EEA13D23CE8B0D529BC2E99ADA36BF555CCB59F89FA987D9D328DA36DB21F46CFBF9470E9DB42F8F64E019DE1B5D565B57CBCAC64B95CA5607B224770472F2F59E47FE2037A9C64BA03DAEE5BE5AFD59774E1827207C2E8C753E7A8398F93AB53C65693EF745EA8A18EF491D5E77465CE9E21F040772BB3F62B1F716183FDA70B70F39D3457CF502BF3F4A9FFF33A87B74C67C8EC53F64D2EC
	9D9619DED054D8DCCD8E5956B148FC76B8D9AB6231A0CE487D24911FBDF4BBDC36A1A853D8C9B1C691D95CE94A96713ABEEB9A2F2E77E85AA839038139390BE8DDE3AC8C1EAFC225570ABA9ABC0D65AE111BEC334B5655C55EB124771DDF0D575EF72ED8C45F87DA8FE14CA25E4756E0D5CC5712D4FCD6E217868C7C0A3364829430683055E6119DDD070B8D25450613270E713B0030816DBB7B653B4E79FD17905376C5F40D1B71B8FB3768218226631F7E24551131B0DF979FC0C7BD13B5BF2E5E5365A17A0E8E
	19EB599FAD57576B70B335712FE97C4EA27719D0E4FC71557802GB6E6B25D9BB5B6E2EFEFCF26BAFBAF293E5A7C6E3D7A0A4A4EEBD473366583D54FEA4764746C39226A7969072A1EDBA31353031AB57939266A694A1EE4FE2E2BFADE3E296AF9F412FA76DAD4BD768ED54F32C96A19EFD5754C6AD1753CC15E1D7223279AB6B3B228B94954B05995322AA745216A797E5D496567ED272A67A1172A27EAF4F2FA163BD5BD7AFED54F45C96AB9B6206AD9B57849753CB8246AB9BD226AD9BAC9BDAD232A1E2F45EB
	745C191C1E9F6BD4BDBFCAD475447465796AB15AFAA5BB7EF02068D4A9A8A9ABBF464E776B7A7979FE4662A8FD7DE23EC7FB6990EE977E3E9F6A6A331574A9B6392242ECD3E42906AD8F587D76E77B79FB425396737B6D7DD564BB92082F870ADD4C5FBBB9D84D6F015193D17A72A7785DFBC7B5FF3743798457B11F240D00789A200DC0E347412D677DE154BF2FA6293E4ACDD6FD751FE57762AB1A2FA4751D85B52F6ABB8BE1623D8371DE44AD012D2D926FAC0471B9F5D5E399041AD786CAB4EF8C143454DDB5
	B754D61BEEB657DE3DE2B255D4DF3DD95DFC73F2DD1D314E4856AA2633634857DF395A7051154BE67F9A68BB1F776E3F738422ED63761DG6F271099191677B7717C5B3175E13C8EAB4977C1A9CBE0AD88D43E045F7BEFC941E91BA0AEAF616777C94637A1EC315CE6B74E78BA5C61A23798GB6399CCED8428CD2143C654E6EC6DEA2539F5551E8AC8C24233ADD3984D93F520EDF77B3DB3A903FDB591B0A57AAF3A19F91C06B00E2003681E5BC9925BF82D48EE48232828D834D838A87DA8D3496E8935056A7F9
	B9F6549D9B279C6CE5AAF2A163325870158CBB83EB6C1F68DD0544255EE7E8D46E4604F2EF7844295C43C44E30547F391CAF15C1BAFAEFB807746D7A253E6FFE84935C3C54775D0FC3D53C4F5F82DE74D2FE2E18DF7D495ED9B97189653AA7ADC74B15D4C33E618977AD6FEE0217057B1677D1B146D581AFBC1C17F717777D7FD36C7E1F0525CF85BABF6E763E7FE769DAD9A887144B75FAAFE29ACB974C420800F24FD7F95D7F33F46BD99893D06E1578717C3B4D0F9C7563CAB2C624AD73756F5F90BCFB192FFF
	EB9238FF0FGEF66B25E6F7EBD856B94F7F2F408E0634236943CC7022D2282732AED08FCBE528C896DDD14FFB4B57059707154094F064F267A3F8F2DCC9DFB9FDA1D6A7FBE74E22A76BE14FD5F150A475FAE98B3DAC53A8F85F6C3E0ADC4A7462E5B2CCA37405BC83EB0DEAFE1039CEFA7799AA0D8AB7F3ECB035D605FF72562C5B494993601DC76E3F2BE5DE913E59A6FA3E9ECF6372488FF8C11BC5A1C96CD3E6F8A8C679B3FBF170056E42693D959E617EA695714085FE9BCCD5695DB20FE933F486FAF1EA013
	D2B6440B3F487B557CAAA45A60C24DF3C1557DFFAF21727F40C6EA0DDA26AC4D736355775F76F37755862D537C337CEE31DC54731FF2781D26C9E011627E37D5E0F7455DF104402EC9DCB6D2E04FC9DCF62D40CE0B3B726C880E8D6939EC3140FE22673265820B2962F6E78A6C4B87396CA201E566F25988016D54F1ECDB84EF434FA528757ACD4DF3FD427D2FE34BFD35C1572DEB7A717824605D835E5FA0FEF6341174715C272374C5CF73B572F36713F8DA44576F3F45C3BE1339C46A3C8D52F7BF4D4FF7F231
	A9F6067A43F16F9CFC6F48C2C087A6F147824FFB10764CBF0258DB8DF83430F7FE0FEA6FF9E9B2F7AA3E76626079EDAFFBED24DD2265D17A346562CCBCC335770AFEB2F7E43E76AA17D3BF2B7652495E2B9EF03330B763B5555EDB391339C3713597341CBEE3FB741BA4030E1F574D83DEB8D0B4D08CD062B35C7E963CA5E1ED676173EF22F17648A56D8E594ACEB64A0E3176D23FCF153F12F32FFB6C4067ECBD7B5567FF4CD1F53FF34F3A296C47792A1E678BD4BDF67D6474ECBD226A7970042AE759414969D1
	68DDAF592BDD5071BD7811E77877950A4E777B0A79715A6FA31669FC3F2F4072AAFF234E777B8AEF79DD893E5FD7F0F936A752CDF64DCEFF9F6647B52150E3D67C86A5A9A889FFB3D52120DD62B3AE086CE65F059948E7EB18ABBE8403C8AA79324C003F5902584E63642F6478F185E59EBFDEC17E990E0BBF8525B4DC2E2EB5B73454F9A756BE6F429F01F10D7E3524DE3EE2B6B4DD2DEB2E3BECD6509E714D170194406DC269DE157A77CB4E7CEA5D4FDA5C89C85F00F2701DE83364A24BDB49EA34A87CF7A3AE
	201DCE876CB3718DC983CAF413A0492D8C4A1289E5F5DE455F0E3B215855FC616CD6506BA6EC6984BE73B9D1EFF1F7ABF8317B2E89A55A71AFDC203D14ADDB6CDDA768AA7ADD8FBEG0C2F2EA2FD068C56105FCC12DF2C25C4E8497C5BBD057917E2D6A7A05D85348B7FB24D85CB876C0F9B50CE05570F47F91EAACA99601FE4A4003A0322783830A5E6D8120BCC77F89E1F1B9E1BFEC107104705CE101478CA2FC4243056F6DBDCE0CAF2B5A87897BB9C2EA3027719B714234FDF5C5F26C3712C109F23D11461F1
	AAC85FE96935490370E0A4DD480DB9D9D037309D303BBB9D2EDE481941A7E71CA58C14FC615B258B1E7E43C74400AA2A20BC6ACA90BD51A5B9FACFD4F7F642D2D8A8G3F027AC3C2BDCED36213E62FE77E6B17DE58518E6D400892FACBCB497FA7497F27707FA789F212A0A7CF01F33722F17FFC799EDEE652F8A0BFFBC8FEF07517DC009AD4FE7C1BEFBF79134A2DDB20F497D09279229CAAA1C3CDF5392F37DDF2BD38E2412154604DF3F09DB9F1D371E03D599E21911730016CA3B6771F2EC8784552849DCAEC
	C0FA3A2F02460BB7C750F8495E2981A5C00FA26D8E7F61FD0E01B4942CF477185608513DE31B950677094A45319C1274A0797F0DC93B8ACB99086CE1D142E35401051A1C6211B4FE6A653454BB27BA1653A96853F3EACE1318CF93C51E26D4CE5F9F6BD372BBD9D3AF27F55EB9CDE0391D0235EF9A1B53DD81FD9A9FA027095EB965031C1E1E00D00BD8667E9421F9EC915E181A4E7E33F7D7634DE28AF6B52B7F86BC853BDA0F37CFF1AE77CDCD1F3A46567E2E291953413183AD5934CC3D1CCE734EA97F34942F
	F1770E3D132C7E5CBF3F386226B1CB07F430966A166862A7E9G9F7412658F5E432D959AF59A0D19841DB1A68D778FB9D807F59DFEDF898223E550E73B23A72FB3F9019AF75EA20A48733D0D31B8E6467F35FC3A0E3D456273BDD32F329FD5AB9BF79E7E9B19D33083AD764E298ECD59017E297FB7B9DD611DD3CCD9A4B5FDE76B58DD6DFBE15BE6E487FC37ECE5A43DBF0B0E747D1D4CF79A48F955B72274395F60F7E1336A0F234077604D3D96D9D6ACF2CF355D0A6F44EB8695C97D766D7D862C5F4D53B0A67B
	AD42861C6F5B9A197F81D0CB878804E29691F09AGG6CD7GGD0CB818294G94G88G88GE7F954AC04E29691F09AGG6CD7GG8CGGGGGGGGGGGGGGGGGE2F5E9ECE4E5F2A0E4E1F4E1D0CB8586GGGG81G81GBAGGG2A9AGGGG
**end of data**/
}
}
