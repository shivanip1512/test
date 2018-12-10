package com.cannontech.dbeditor.wizard.point;

import java.util.List;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class PointPhysicalSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener {
	private javax.swing.JLabel ivjPointOffsetLabel = null;
	private javax.swing.JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private java.util.Vector usedPointOffsetsVector = null;
	private javax.swing.JLabel ivjUsedPointOffsetLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointPhysicalSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointPhysicalSettingsPanel.physicalPointOffsetCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.physicalPointOffsetCheckBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC2:  (PointOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> PointPhysicalSettingsPanel.pointOffsetSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(com.klg.jclass.util.value.JCValueEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.pointOffsetSpinner_ValueChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return javax.swing.JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new javax.swing.JCheckBox();
			ivjPhysicalPointOffsetCheckBox.setName("PhysicalPointOffsetCheckBox");
			ivjPhysicalPointOffsetCheckBox.setSelected(true);
			ivjPhysicalPointOffsetCheckBox.setText("Physical Point Offset");
			ivjPhysicalPointOffsetCheckBox.setActionCommand("PhysicalPointOffsetCheckBox");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPhysicalPointOffsetCheckBox;
}
/**
 * Return the PointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new javax.swing.JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetLabel.setText("Point Offset:");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetLabel;
}
/**
 * Return the PointOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getPointOffsetSpinner() {
	if (ivjPointOffsetSpinner == null) {
		try {
			ivjPointOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjPointOffsetSpinner.setName("PointOffsetSpinner");
			ivjPointOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjPointOffsetSpinner.setBackground(java.awt.Color.white);
			ivjPointOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}
			ivjPointOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjPointOffsetSpinner;
}
/**
 * Return the InvalidPointOffsetLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new javax.swing.JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
			ivjUsedPointOffsetLabel.setPreferredSize(new java.awt.Dimension(180, 20));
			ivjUsedPointOffsetLabel.setFont(new java.awt.Font("dialog.bold", 1, 14));
			ivjUsedPointOffsetLabel.setMinimumSize(new java.awt.Dimension(180, 20));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjUsedPointOffsetLabel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.Object
 * @param val java.lang.Object
 */
public Object getValue(Object val) {

	Integer pointOffset = null;
	
	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	if( pointOffsetSpinVal instanceof Long )
		pointOffset = new Integer( ((Long)pointOffsetSpinVal).intValue() );
	else if( pointOffsetSpinVal instanceof Integer )
		pointOffset = new Integer( ((Integer)pointOffsetSpinVal).intValue() );
	
	//Assuming either a REAL AnalogPoint or an AccumulatorPoint
	if( val instanceof com.cannontech.database.data.point.AnalogPoint )
	{
		if ( (getUsedPointOffsetLabel().getText()) == "" )
			((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPointOffset(pointOffset);
		else
			((com.cannontech.database.data.point.AnalogPoint) val).getPoint().setPointOffset(null);
	}
	else
	if( val instanceof com.cannontech.database.data.point.AccumulatorPoint )
	{
		if ( (getUsedPointOffsetLabel().getText()) == "" )
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPointOffset(pointOffset);
		else
			((com.cannontech.database.data.point.AccumulatorPoint) val).getPoint().setPointOffset(null);
	}

	
	return val;	
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	// com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	// com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getPointOffsetSpinner().addValueListener(this);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
	try {
		// user code begin {1}
		// user code end
		setName("PointPhysicalSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(379, 193);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 0; constraintsPointOffsetLabel.gridy = 1;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 0; constraintsPhysicalPointOffsetCheckBox.gridy = 0;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 3;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 0; constraintsUsedPointOffsetLabel.gridy = 2;
		constraintsUsedPointOffsetLabel.gridwidth = 2;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 1;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 8, 5, 0);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC1(e);
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
		PointPhysicalSettingsPanel aPointPhysicalSettingsPanel;
		aPointPhysicalSettingsPanel = new PointPhysicalSettingsPanel();
		frame.getContentPane().add("Center", aPointPhysicalSettingsPanel);
		frame.setSize(aPointPhysicalSettingsPanel.getSize());
		frame.setVisible(true);
	} catch (Throwable exception) {
		System.err.println("Exception occurred in main() of com.cannontech.common.gui.util.DataInputPanel");
		com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
	}
}
/**
 * Comment
 */
public void physicalPointOffsetCheckBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) {
	if ( getPhysicalPointOffsetCheckBox().isSelected() )
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(Integer.MAX_VALUE), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue(new Integer(1));
		int temp = 2;
		while( getUsedPointOffsetLabel().getText() != "" )
		{
			getPointOffsetSpinner().setValue(new Integer(temp));
			temp++;
		}
		getPointOffsetLabel().setEnabled(true);
	}
	else
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue(new Integer(0));
		getPointOffsetLabel().setEnabled(false);
	}

	revalidate();
	repaint();
	return;
}

public boolean isInputValid() {
    if (!getPhysicalPointOffsetCheckBox().isSelected()) return true;
    Object value = this.getPointOffsetSpinner().getValue();

        if (value instanceof Number) {
            Number numValue = (Number) value;
            return !this.isPointOffsetInUse(numValue.intValue());
        }

    return false;
}

    /**
     * Helper method to determine if the pointOffset is already in use
     * @param pointOffset - Offset to check
     * @return - True if offset is used by another point
     */
    private boolean isPointOffsetInUse(int pointOffset) {

        if (this.usedPointOffsetsVector != null && this.usedPointOffsetsVector.size() > 0) {

            for (Object point : this.usedPointOffsetsVector) {

                if (((LitePoint) point).getPointOffset() == pointOffset) {
                    return true;
                }
            }
        }

        return false;

    }

/**
 * Comment
 */
public void pointOffsetSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	if(usedPointOffsetsVector != null)
	{
		getUsedPointOffsetLabel().setText("");
		if (usedPointOffsetsVector.size() > 0)
		{
			for (int i=0; i<usedPointOffsetsVector.size(); i++)
			{
				Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
				if( pointOffsetSpinVal instanceof Long )
				{
					if( ((Long)pointOffsetSpinVal).longValue() != 0 &&
						(((Long)pointOffsetSpinVal).longValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset()) )
					{
						getUsedPointOffsetLabel().setText("Used by " + ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointName() );
						break;
					}
				}
				else if( pointOffsetSpinVal instanceof Integer )
				{
					if( ((Integer)pointOffsetSpinVal).intValue() != 0 &&
						(((Integer)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset()) )
					{
						getUsedPointOffsetLabel().setText("Used by " + ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointName() );
						break;
					}
				}
			}
			revalidate();
			repaint();
		}
	}
    this.fireInputUpdate();
	return;
}
public void reinitialize(Integer pointDeviceID, PointType pointType) {

	getUsedPointOffsetLabel().setText("");
	
    List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(pointDeviceID);
    usedPointOffsetsVector = new java.util.Vector(points.size());
    for (LitePoint point : points) {
        if(pointType == point.getPointTypeEnum()) {
            usedPointOffsetsVector.add(point);
        }
    }

	getPointOffsetSpinner().setValue(new Integer(1));
	if (usedPointOffsetsVector.size() > 0)
	{
		for (int i=0; i<usedPointOffsetsVector.size(); i++)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			if( pointOffsetSpinVal instanceof Long )
			{
				if( ((Long)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset() )
					getPointOffsetSpinner().setValue(new Integer(((Long)pointOffsetSpinVal).intValue() + 1) );
				else
					break;
			}
			else if( pointOffsetSpinVal instanceof Integer )
			{
				if( ((Integer)pointOffsetSpinVal).intValue() == ((com.cannontech.database.data.lite.LitePoint)usedPointOffsetsVector.elementAt(i)).getPointOffset() )
				{
					getPointOffsetSpinner().setValue(new Integer(((Integer) pointOffsetSpinVal).intValue() + 1));
					i = -1;
				}
			}
		}
	}
	revalidate();
	repaint();
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}

public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    javax.swing.SwingUtilities.invokeLater( new Runnable() 
        { 
        public void run() 
            { 
            getPhysicalPointOffsetCheckBox().requestFocus();
        } 
    });    
}

/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getPointOffsetSpinner()) 
		connEtoC2(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
}
