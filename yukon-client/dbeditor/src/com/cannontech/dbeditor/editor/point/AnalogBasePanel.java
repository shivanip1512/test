package com.cannontech.dbeditor.editor.point;

/**
 * This type was created in VisualAge.
 */

import java.awt.Dimension;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.border.EtchedBorder;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteUnitMeasure;
import com.cannontech.database.data.point.AnalogPoint;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.yukon.IDatabaseCache;

public class AnalogBasePanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener, com.klg.jclass.util.value.JCValueListener
{
	private javax.swing.JComboBox ivjUnitOfMeasureComboBox = null;
	private javax.swing.JLabel ivjUnitOfMeasureLabel = null;
	private javax.swing.JComboBox ivjArchiveIntervalComboBox = null;
	private javax.swing.JLabel ivjArchiveIntervalLabel = null;
	private javax.swing.JComboBox ivjArchiveTypeComboBox = null;
	private javax.swing.JLabel ivjArchiveTypeLabel = null;
	private javax.swing.JLabel ivjJLabelDecimalPositions = null;
    private javax.swing.JLabel jLabelMeterDials = null;
	private com.klg.jclass.field.JCSpinField ivjDecimalPlacesSpinner = null;
    private com.klg.jclass.field.JCSpinField meterDialsSpinner = null;
	private javax.swing.JPanel ivjJPanelArchive = null;
	private javax.swing.JPanel ivjJPanelHolder = null;
    private JComboBox stateGroupComboBox = null;
    private JLabel stateGroupLabel = null;
    
/**
 * Constructor
 */
public AnalogBasePanel() {
	super();
	initialize();
}
/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) {
	if (e.getSource() == getArchiveTypeComboBox()) 
		connEtoC1(e);
	if (e.getSource() == getUnitOfMeasureComboBox()) 
		connEtoC4(e);
	if (e.getSource() == getArchiveIntervalComboBox()) 
		connEtoC5(e);
    if (e.getSource() == getStateGroupComboBox()) 
        connEtoC5(e);
}
/**
 * Comment
 */
public void archiveTypeComboBox_ActionPerformed(java.awt.event.ActionEvent actionEvent) 
{
	fireInputUpdate();

	if( ((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_NONE) ||
			((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_ON_CHANGE) ||
			((String)getArchiveTypeComboBox().getSelectedItem()).equalsIgnoreCase(PointTypes.ARCHIVE_ON_UPDATE) )
	{
		getArchiveIntervalLabel().setEnabled(false);
		getArchiveIntervalComboBox().setEnabled(false);
	}
	else
	{
		getArchiveIntervalLabel().setEnabled(true);
		getArchiveIntervalComboBox().setEnabled(true);
		getArchiveIntervalComboBox().setSelectedItem("5 minute");
		
		if("On Timer Or Update".equalsIgnoreCase(((String)getArchiveTypeComboBox().getSelectedItem())))
			getArchiveIntervalComboBox().setSelectedItem("Daily");
	}

	return;
}
/**
 * connEtoC1:  (UnitOfMeasureComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
	try {
		this.archiveTypeComboBox_ActionPerformed(arg1);
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC4:  (ArchiveTypeComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}
/**
 * connEtoC5:  (ArchiveIntervalComboBox.action.actionPerformed(java.awt.event.ActionEvent) --> AnalogBasePanel.fireInputUpdate()V)
 * @param arg1 java.awt.event.ActionEvent
 */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
	try {
		this.fireInputUpdate();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * Return the stateGroupLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getStateGroupLabel() {
    if (stateGroupLabel == null) {
        try {
            stateGroupLabel = new javax.swing.JLabel();
            stateGroupLabel.setName("StateGroupLabel");
            stateGroupLabel.setText("State Group:");
            stateGroupLabel.setMinimumSize(new Dimension(85, 22));
            stateGroupLabel.setPreferredSize(new Dimension(85, 22));
            stateGroupLabel.setFont(new java.awt.Font("dialog", 0, 14));
         } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return stateGroupLabel;
}

/**
 * Return the StateGroupComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getStateGroupComboBox() {
    if (stateGroupComboBox == null) {
        try {
            stateGroupComboBox = new javax.swing.JComboBox();
            stateGroupComboBox.setName("StateGroupComboBox");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return stateGroupComboBox;
}

/**
 * Return the ArchiveIntervalComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getArchiveIntervalComboBox() {
	if (ivjArchiveIntervalComboBox == null) {
		try {
			ivjArchiveIntervalComboBox = new javax.swing.JComboBox();
			ivjArchiveIntervalComboBox.setName("ArchiveIntervalComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalComboBox;
}
/**
 * Return the ArchiveIntervalLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getArchiveIntervalLabel() {
	if (ivjArchiveIntervalLabel == null) {
		try {
			ivjArchiveIntervalLabel = new javax.swing.JLabel();
			ivjArchiveIntervalLabel.setName("ArchiveIntervalLabel");
			ivjArchiveIntervalLabel.setText("Interval:");
			ivjArchiveIntervalLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveIntervalLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveIntervalLabel.setMinimumSize(new java.awt.Dimension(78, 16));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjArchiveIntervalLabel;
}
/**
 * Return the ArchiveTypeComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getArchiveTypeComboBox() {
	if (ivjArchiveTypeComboBox == null) {
		try {
			ivjArchiveTypeComboBox = new javax.swing.JComboBox();
			ivjArchiveTypeComboBox.setName("ArchiveTypeComboBox");
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeComboBox;
}
/**
 * Return the ArchiveTypeLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getArchiveTypeLabel() {
	if (ivjArchiveTypeLabel == null) {
		try {
			ivjArchiveTypeLabel = new javax.swing.JLabel();
			ivjArchiveTypeLabel.setName("ArchiveTypeLabel");
			ivjArchiveTypeLabel.setText("Data Type:");
			ivjArchiveTypeLabel.setMaximumSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setPreferredSize(new java.awt.Dimension(78, 16));
			ivjArchiveTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjArchiveTypeLabel.setMinimumSize(new java.awt.Dimension(78, 16));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjArchiveTypeLabel;
}

private com.klg.jclass.field.JCSpinField getDecimalPlacesSpinner() {
	if (ivjDecimalPlacesSpinner == null) {
		try {
			ivjDecimalPlacesSpinner = new com.klg.jclass.field.JCSpinField();
			ivjDecimalPlacesSpinner.setName("DecimalPlacesSpinner");
			ivjDecimalPlacesSpinner.setBackground(java.awt.Color.white);
			ivjDecimalPlacesSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			ivjDecimalPlacesSpinner.setPreferredSize(new java.awt.Dimension(35,20));
			ivjDecimalPlacesSpinner.setMinimumSize(new java.awt.Dimension(35,20));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjDecimalPlacesSpinner;
}

private com.klg.jclass.field.JCSpinField getMeterDialsSpinner() {
    if (meterDialsSpinner == null) {
        try {
            meterDialsSpinner = new com.klg.jclass.field.JCSpinField();
            meterDialsSpinner.setName("MeterDialsSpinner");
            meterDialsSpinner.setBackground(java.awt.Color.white);
            meterDialsSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(10), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(0)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
            meterDialsSpinner.setPreferredSize(new java.awt.Dimension(35,20));
            meterDialsSpinner.setMinimumSize(new java.awt.Dimension(35,20));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return meterDialsSpinner;
}

private javax.swing.JLabel getJLabelDecimalPositions() {
	if (ivjJLabelDecimalPositions == null) {
		try {
           ivjJLabelDecimalPositions = new javax.swing.JLabel();
			ivjJLabelDecimalPositions.setName("JLabelDecimalPositions");
			ivjJLabelDecimalPositions.setFont(new java.awt.Font("dialog", 0, 14));
			ivjJLabelDecimalPositions.setText("Decimal Places:");
			ivjJLabelDecimalPositions.setMinimumSize(new Dimension(110,22));
            ivjJLabelDecimalPositions.setPreferredSize(new Dimension(110,22));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJLabelDecimalPositions;
}

private javax.swing.JLabel getJLabelMeterDials() {
    if (jLabelMeterDials == null) {
        try {
            jLabelMeterDials = new javax.swing.JLabel();
            jLabelMeterDials.setName("JLabelMeterDials");
            jLabelMeterDials.setFont(new java.awt.Font("dialog", 0, 14));
            jLabelMeterDials.setText("Meter Dials:");
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return jLabelMeterDials;
}

private javax.swing.JPanel getJPanelArchive() {
	if (ivjJPanelArchive == null) {
		try {
			com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
			ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
			ivjLocalBorder.setTitleFont(new java.awt.Font("Arial", 1, 14));
			ivjLocalBorder.setTitle("Archive");
			ivjJPanelArchive = new javax.swing.JPanel();
			ivjJPanelArchive.setName("JPanelArchive");
			ivjJPanelArchive.setBorder(ivjLocalBorder);
			ivjJPanelArchive.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsArchiveTypeLabel = new java.awt.GridBagConstraints();
			constraintsArchiveTypeLabel.gridx = 1; constraintsArchiveTypeLabel.gridy = 1;
			constraintsArchiveTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeLabel.ipadx = -5;
			constraintsArchiveTypeLabel.insets = new java.awt.Insets(3, 14, 6, 1);
			getJPanelArchive().add(getArchiveTypeLabel(), constraintsArchiveTypeLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalLabel = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalLabel.gridx = 1; constraintsArchiveIntervalLabel.gridy = 2;
			constraintsArchiveIntervalLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalLabel.ipadx = -5;
			constraintsArchiveIntervalLabel.insets = new java.awt.Insets(5, 14, 10, 1);
			getJPanelArchive().add(getArchiveIntervalLabel(), constraintsArchiveIntervalLabel);

			java.awt.GridBagConstraints constraintsArchiveIntervalComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveIntervalComboBox.gridx = 2; constraintsArchiveIntervalComboBox.gridy = 2;
			constraintsArchiveIntervalComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveIntervalComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveIntervalComboBox.weightx = 1.0;
			constraintsArchiveIntervalComboBox.ipadx = 10;
			constraintsArchiveIntervalComboBox.insets = new java.awt.Insets(2, 2, 6, 8);
			getJPanelArchive().add(getArchiveIntervalComboBox(), constraintsArchiveIntervalComboBox);

			java.awt.GridBagConstraints constraintsArchiveTypeComboBox = new java.awt.GridBagConstraints();
			constraintsArchiveTypeComboBox.gridx = 2; constraintsArchiveTypeComboBox.gridy = 1;
			constraintsArchiveTypeComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsArchiveTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
			constraintsArchiveTypeComboBox.weightx = 1.0;
			constraintsArchiveTypeComboBox.ipadx = 10;
			constraintsArchiveTypeComboBox.insets = new java.awt.Insets(0, 2, 2, 8);
			getJPanelArchive().add(getArchiveTypeComboBox(), constraintsArchiveTypeComboBox);
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelArchive;
}
/**
 * Return the JPanelHolder property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelHolder() {
	if (ivjJPanelHolder == null) {
		try {
			ivjJPanelHolder = new javax.swing.JPanel();
			ivjJPanelHolder.setName("JPanelHolder");
			ivjJPanelHolder.setLayout(new java.awt.GridBagLayout());

			java.awt.GridBagConstraints constraintsUnitOfMeasureLabel = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureLabel.gridx = 0; constraintsUnitOfMeasureLabel.gridy = 0;
			constraintsUnitOfMeasureLabel.anchor = java.awt.GridBagConstraints.WEST;
			constraintsUnitOfMeasureLabel.insets = new java.awt.Insets(5, 5, 5, 5);
			getJPanelHolder().add(getUnitOfMeasureLabel(), constraintsUnitOfMeasureLabel);

			java.awt.GridBagConstraints constraintsUnitOfMeasureComboBox = new java.awt.GridBagConstraints();
			constraintsUnitOfMeasureComboBox.gridx = 1; constraintsUnitOfMeasureComboBox.gridy = 0;
			constraintsUnitOfMeasureComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
			constraintsUnitOfMeasureComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsUnitOfMeasureComboBox.gridwidth = 3;
			constraintsUnitOfMeasureComboBox.weightx = 1.0;
			constraintsUnitOfMeasureComboBox.insets = new java.awt.Insets(5,5,5,5);
			getJPanelHolder().add(getUnitOfMeasureComboBox(), constraintsUnitOfMeasureComboBox);

			java.awt.GridBagConstraints constraintsJLabelDecimalPositions = new java.awt.GridBagConstraints();
            constraintsJLabelDecimalPositions.gridx = 0; constraintsJLabelDecimalPositions.gridy = 1;
            constraintsJLabelDecimalPositions.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDecimalPositions.insets = new java.awt.Insets(5,5,5,5);
			getJPanelHolder().add(getJLabelDecimalPositions(), constraintsJLabelDecimalPositions);

			java.awt.GridBagConstraints constraintsDecimalPlacesSpinner = new java.awt.GridBagConstraints();
			constraintsDecimalPlacesSpinner.gridx = 1; constraintsDecimalPlacesSpinner.gridy = 1;
			constraintsDecimalPlacesSpinner.anchor = java.awt.GridBagConstraints.WEST;
			constraintsDecimalPlacesSpinner.insets = new java.awt.Insets(5,5,5,5);
			getJPanelHolder().add(getDecimalPlacesSpinner(), constraintsDecimalPlacesSpinner);
            
            java.awt.GridBagConstraints constraintJLabelMeterDials = new java.awt.GridBagConstraints();
            constraintJLabelMeterDials.gridx = 2; constraintJLabelMeterDials.gridy = 1;
            constraintJLabelMeterDials.anchor = java.awt.GridBagConstraints.WEST;
            constraintJLabelMeterDials.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getJLabelMeterDials(), constraintJLabelMeterDials);

            java.awt.GridBagConstraints constraintsMeterDialsSpinner = new java.awt.GridBagConstraints();
            constraintsMeterDialsSpinner.gridx = 3; constraintsMeterDialsSpinner.gridy = 1;
            constraintsMeterDialsSpinner.anchor = java.awt.GridBagConstraints.WEST;
            constraintsMeterDialsSpinner.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getMeterDialsSpinner(), constraintsMeterDialsSpinner);
            
            java.awt.GridBagConstraints constraintsStateGroupLabel = new java.awt.GridBagConstraints();
            constraintsStateGroupLabel.gridx =0; constraintsStateGroupLabel.gridy =2 ;
            constraintsStateGroupLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsStateGroupLabel.insets = new java.awt.Insets(5,5,5,5);
            getJPanelHolder().add(getStateGroupLabel(), constraintsStateGroupLabel);
            
            java.awt.GridBagConstraints constraintsStateGroupComboBox = new java.awt.GridBagConstraints();
            constraintsStateGroupComboBox.gridx = 1; constraintsStateGroupComboBox.gridy = 2;
            constraintsStateGroupComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsStateGroupComboBox.gridwidth = 3;
            constraintsStateGroupComboBox.insets = new java.awt.Insets(5,5,5,5);
            constraintsStateGroupComboBox.fill = java.awt.GridBagConstraints.HORIZONTAL;
            getJPanelHolder().add(getStateGroupComboBox(), constraintsStateGroupComboBox);
            
            
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelHolder;
}
/**
 * Return the UnitOfMeasureComboBox property value.
 * @return javax.swing.JComboBox
 */
private javax.swing.JComboBox getUnitOfMeasureComboBox() {
	if (ivjUnitOfMeasureComboBox == null) {
		try {
			ivjUnitOfMeasureComboBox = new javax.swing.JComboBox();
			ivjUnitOfMeasureComboBox.setName("UnitOfMeasureComboBox");
			ivjUnitOfMeasureComboBox.setPreferredSize(new java.awt.Dimension(126, 24));
			ivjUnitOfMeasureComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureComboBox.setMinimumSize(new java.awt.Dimension(90, 24));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureComboBox;
}
/**
 * Return the UnitOfMeasureLabel property value.
 * @return javax.swing.JLabel
 */
private javax.swing.JLabel getUnitOfMeasureLabel() {
	if (ivjUnitOfMeasureLabel == null) {
		try {
			ivjUnitOfMeasureLabel = new javax.swing.JLabel();
			ivjUnitOfMeasureLabel.setName("UnitOfMeasureLabel");
			ivjUnitOfMeasureLabel.setText("Unit of Measure:");
			ivjUnitOfMeasureLabel.setMaximumSize(new java.awt.Dimension(110, 22));
			ivjUnitOfMeasureLabel.setPreferredSize(new java.awt.Dimension(110, 22));
			ivjUnitOfMeasureLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjUnitOfMeasureLabel.setMinimumSize(new java.awt.Dimension(110, 22));
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjUnitOfMeasureLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {
	//Assume that commonObject is an instance of com.cannontech.database.data.point.AnalogPoint
	AnalogPoint point = (AnalogPoint) val;

	int uOfMeasureID = ((LiteUnitMeasure)getUnitOfMeasureComboBox().getSelectedItem()).getUomID();
	
	point.getPointUnit().setUomID( new Integer(uOfMeasureID) );
	if(getArchiveTypeComboBox().getSelectedItem().toString().compareTo("On Timer Or Update") == 0)
		point.getPoint().setArchiveType(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE);
	else
		point.getPoint().setArchiveType((String)getArchiveTypeComboBox().getSelectedItem());
	point.getPoint().setArchiveInterval(CtiUtilities.getIntervalComboBoxSecondsValue(getArchiveIntervalComboBox()));
	
	point.getPointUnit().setDecimalPlaces( new Integer(((Number)getDecimalPlacesSpinner().getValue()).intValue() ) );
    point.getPointUnit().setMeterDials( new Integer(((Number)getMeterDialsSpinner().getValue()).intValue() ) );
    
    LiteStateGroup stateGroup = (LiteStateGroup) getStateGroupComboBox().getSelectedItem();

    point.getPoint().setStateGroupID( new Integer(stateGroup.getStateGroupID()) );
    
	return point;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
private void initConnections() throws java.lang.Exception {

	getDecimalPlacesSpinner().addValueListener(this);
    getMeterDialsSpinner().addValueListener(this);
	getStateGroupComboBox().addActionListener(this);
	getArchiveTypeComboBox().addActionListener(this);
	getUnitOfMeasureComboBox().addActionListener(this);
	getArchiveIntervalComboBox().addActionListener(this);
}
/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("AnalogBasePanel");
		setPreferredSize(new java.awt.Dimension(300, 102));
		setLayout(new java.awt.GridBagLayout());
		setSize(386, 177);
		setMinimumSize(new java.awt.Dimension(0, 0));

		java.awt.GridBagConstraints constraintsJPanelArchive = new java.awt.GridBagConstraints();
		constraintsJPanelArchive.gridx = 1; constraintsJPanelArchive.gridy = 2;
		constraintsJPanelArchive.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelArchive.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelArchive.weightx = 1.0;
		constraintsJPanelArchive.weighty = 1.0;
		constraintsJPanelArchive.ipadx = 136;
		constraintsJPanelArchive.ipady = 8;
		constraintsJPanelArchive.insets = new java.awt.Insets(6, 4, 18, 2);
		add(getJPanelArchive(), constraintsJPanelArchive);

		java.awt.GridBagConstraints constraintsJPanelHolder = new java.awt.GridBagConstraints();
		constraintsJPanelHolder.gridx = 1; constraintsJPanelHolder.gridy = 1;
		constraintsJPanelHolder.fill = java.awt.GridBagConstraints.BOTH;
		constraintsJPanelHolder.anchor = java.awt.GridBagConstraints.WEST;
		constraintsJPanelHolder.weightx = 1.0;
		constraintsJPanelHolder.weighty = 1.0;
		constraintsJPanelHolder.insets = new java.awt.Insets(2, 4, 6, 2);
		add(getJPanelHolder(), constraintsJPanelHolder);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}

	//Put a border around the Analog section of panel
	javax.swing.border.TitledBorder border = new javax.swing.border.TitledBorder("Analog Summary");
	border.setTitleFont(new java.awt.Font("dialog", java.awt.Font.BOLD, 14));
	setBorder(border);

	//Load the unit of measure combo box with default possible values
    List<LiteUnitMeasure> unitMeasures = 
        DaoFactory.getUnitMeasureDao().getLiteUnitMeasures();
    for (LiteUnitMeasure lum : unitMeasures) {
        getUnitOfMeasureComboBox().addItem(lum);
    }

	//Load the Archive Type combo box with default possible values
	getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_NONE);
	getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_CHANGE);
	getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_TIMER);
	getArchiveTypeComboBox().addItem(PointTypes.ARCHIVE_ON_UPDATE);
	getArchiveTypeComboBox().addItem("On Timer Or Update"); // PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE

	//Load the Archive Interval combo box with default possible values
	getArchiveIntervalComboBox().addItem("1 second");
	getArchiveIntervalComboBox().addItem("2 second");
	getArchiveIntervalComboBox().addItem("5 second");
	getArchiveIntervalComboBox().addItem("10 second");
	getArchiveIntervalComboBox().addItem("15 second");
	getArchiveIntervalComboBox().addItem("30 second");
	getArchiveIntervalComboBox().addItem("1 minute");
	getArchiveIntervalComboBox().addItem("2 minute");
	getArchiveIntervalComboBox().addItem("3 minute");
	getArchiveIntervalComboBox().addItem("5 minute");
	getArchiveIntervalComboBox().addItem("10 minute");
	getArchiveIntervalComboBox().addItem("15 minute");
	getArchiveIntervalComboBox().addItem("30 minute");
	getArchiveIntervalComboBox().addItem("1 hour");
	getArchiveIntervalComboBox().addItem("2 hour");
	getArchiveIntervalComboBox().addItem("6 hour");
	getArchiveIntervalComboBox().addItem("12 hour");
	//getArchiveIntervalComboBox().addItem("1 day");
	getArchiveIntervalComboBox().addItem("Daily");
	getArchiveIntervalComboBox().addItem("Weekly");
	getArchiveIntervalComboBox().addItem("Monthly");
	getArchiveIntervalComboBox().setSelectedItem("5 minute");
}
/**
 * Insert the method's description here.
 * Creation date: (5/1/2001 9:11:36 AM)
 * @return boolean
 */
public boolean isInputValid() 
{	
	return true;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		java.awt.Frame frame;
		try {
			Class aFrameClass = Class.forName("com.ibm.uvm.abt.edit.TestFrame");
			frame = (java.awt.Frame)aFrameClass.newInstance();
		} catch (java.lang.Throwable ivjExc) {
			frame = new java.awt.Frame();
		}
		AnalogBasePanel aAnalogBasePanel;
		aAnalogBasePanel = new AnalogBasePanel();
		frame.add("Center", aAnalogBasePanel);
		frame.setSize(aAnalogBasePanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
	//Assume that defaultObject is an AnalogPoint
	AnalogPoint point = (AnalogPoint) val;

	int uOfMeasureID = point.getPointUnit().getUomID().intValue();
	String archiveType = point.getPoint().getArchiveType();
	if(archiveType.compareTo(PointTypes.ARCHIVE_ON_TIMER_OR_UPDATE) == 0)
		archiveType = "On Timer Or Update";
	Integer archiveInteger = point.getPoint().getArchiveInterval();

	getDecimalPlacesSpinner().setValue( point.getPointUnit().getDecimalPlaces() );
    getMeterDialsSpinner().setValue( point.getPointUnit().getMeterDials() );
	getArchiveIntervalLabel().setEnabled(false);
	getArchiveIntervalComboBox().setEnabled(false);
	for(int i=0;i<getUnitOfMeasureComboBox().getModel().getSize();i++)
	{
		if( ((LiteUnitMeasure)getUnitOfMeasureComboBox().getItemAt(i)).getUomID()
			 == uOfMeasureID )
		{
			getUnitOfMeasureComboBox().setSelectedIndex(i);
			break;
		}
	}

	for(int i=0;i<getArchiveTypeComboBox().getModel().getSize();i++)
	{
		if( ((String)getArchiveTypeComboBox().getItemAt(i)).equalsIgnoreCase(archiveType) )
		{
			getArchiveTypeComboBox().setSelectedIndex(i);
			if( getArchiveIntervalComboBox().isEnabled() )
				CtiUtilities.setIntervalComboBoxSelectedItem(getArchiveIntervalComboBox(),archiveInteger.intValue());
			break;
		}
	}
    
    // load and set stategroups
    int stateGroupID = point.getPoint().getStateGroupID().intValue();
    
    //Load all the state groups
    IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    synchronized(cache)
    {
        LiteStateGroup[] allStateGroups = DaoFactory.getStateDao().getAllStateGroups();

        //Load the state table combo box
        for(int i=0;i<allStateGroups.length;i++)
        {
            LiteStateGroup grp = (LiteStateGroup)allStateGroups[i];

           getStateGroupComboBox().addItem( grp );
            if( grp.getStateGroupID() == stateGroupID )
                getStateGroupComboBox().setSelectedItem( grp );
        }
    }
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) 
{
	if ((arg1.getSource() == getDecimalPlacesSpinner()) || (arg1.getSource() == getMeterDialsSpinner())) 
		this.fireInputUpdate();
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) 
{
}
}
