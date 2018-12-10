package com.cannontech.dbeditor.wizard.point;

import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.spring.YukonSpringHook;

/**
 * This type was created in VisualAge.
 */

public class PointStatusPhysicalSettingsPanel extends com.cannontech.common.gui.util.DataInputPanel implements com.klg.jclass.util.value.JCValueListener, java.awt.event.ItemListener {
	private JLabel ivjControlOffsetLabel = null;
	private JLabel ivjPointOffsetLabel = null;
	private JComboBox<String> ivjControlTypeComboBox = null;
	private JLabel ivjControlTypeLabel = null;
	private JCheckBox ivjPhysicalPointOffsetCheckBox = null;
	private com.klg.jclass.field.JCSpinField ivjControlOffsetSpinner = null;
	private com.klg.jclass.field.JCSpinField ivjPointOffsetSpinner = null;
	private Vector<LitePoint> usedPointOffsetsVector = null;
	private JLabel ivjUsedPointOffsetLabel = null;
/**
 * Constructor
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public PointStatusPhysicalSettingsPanel() {
	super();
	initialize();
}
/**
 * connEtoC1:  (PhysicalPointOffsetCheckBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusPhysicalSettingsPanel.physicalPointOffsetCheckBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
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
 * connEtoC2:  (ControlTypeComboBox.item.itemStateChanged(java.awt.event.ItemEvent) --> PointStatusPhysicalSettingsPanel.controlTypeComboBox_ItemStateChanged(Ljava.awt.event.ItemEvent;)V)
 * @param arg1 java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ItemEvent arg1) {
	try {
		// user code begin {1}
		// user code end
		this.controlTypeComboBox_ItemStateChanged(arg1);
		// user code begin {2}
		// user code end
	} catch (java.lang.Throwable ivjExc) {
		// user code begin {3}
		// user code end
		handleException(ivjExc);
	}
}
/**
 * connEtoC3:  (PointOffsetSpinner.value.valueChanged(com.klg.jclass.util.value.JCValueEvent) --> PointStatusPhysicalSettingsPanel.pointOffsetSpinner_ValueChanged(Lcom.klg.jclass.util.value.JCValueEvent;)V)
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(com.klg.jclass.util.value.JCValueEvent arg1) {
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
 * Comment
 */
public void controlTypeComboBox_ItemStateChanged(java.awt.event.ItemEvent itemEvent) 
{
	boolean isControlTypeNone = getControlTypeComboBox().getSelectedItem().toString().equalsIgnoreCase(StatusControlType.NONE.getControlName());

	getControlOffsetLabel().setEnabled( ! isControlTypeNone );
	
	getControlOffsetSpinner().setEnabled( ! isControlTypeNone );

	revalidate();
	repaint();
	return;
}
/**
 * Return the ControlOffsetLabel property value.
 * @return JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JLabel getControlOffsetLabel() {
	if (ivjControlOffsetLabel == null) {
		try {
			ivjControlOffsetLabel = new JLabel();
			ivjControlOffsetLabel.setName("ControlOffsetLabel");
			ivjControlOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlOffsetLabel.setText("Control Offset:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlOffsetLabel;
}
/**
 * Return the ControlOffsetField property value.
 * @return com.klg.jclass.field.JCSpinField
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private com.klg.jclass.field.JCSpinField getControlOffsetSpinner() {
	if (ivjControlOffsetSpinner == null) {
		try {
			ivjControlOffsetSpinner = new com.klg.jclass.field.JCSpinField();
			ivjControlOffsetSpinner.setName("ControlOffsetSpinner");
			ivjControlOffsetSpinner.setPreferredSize(new java.awt.Dimension(55, 22));
			ivjControlOffsetSpinner.setBackground(java.awt.Color.white);
			ivjControlOffsetSpinner.setMinimumSize(new java.awt.Dimension(55, 22));
			// user code begin {1}

			ivjControlOffsetSpinner.setDataProperties(new com.klg.jclass.field.DataProperties(
				new com.klg.jclass.field.validate.JCIntegerValidator(null, 
					new Integer(0), new Integer(500), null, true, null, 
					new Integer(1), "#,##0.###;-#,##0.###", false, false, false, 
					null, new Integer(0)), new com.klg.jclass.util.value.MutableValueModel(
						java.lang.Integer.class, new Integer(1)), new com.klg.jclass.field.JCInvalidInfo(true, 2, 
							new java.awt.Color(0, 0, 0, 255), new java.awt.Color(255, 255, 255, 255))));

			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlOffsetSpinner;
}
/**
 * Return the ControlTypeComboBox property value.
 * @return JComboBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JComboBox<String> getControlTypeComboBox() {
	if (ivjControlTypeComboBox == null) {
		try {
			ivjControlTypeComboBox = new JComboBox<String>();
			ivjControlTypeComboBox.setName("ControlTypeComboBox");
			ivjControlTypeComboBox.setFont(new java.awt.Font("dialog", 0, 14));
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeComboBox;
}
/**
 * Return the ControlTypeLabel property value.
 * @return JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JLabel getControlTypeLabel() {
	if (ivjControlTypeLabel == null) {
		try {
			ivjControlTypeLabel = new JLabel();
			ivjControlTypeLabel.setName("ControlTypeLabel");
			ivjControlTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjControlTypeLabel.setText("Control Type:  ");
			// user code begin {1}
			// user code end
		} catch (java.lang.Throwable ivjExc) {
			// user code begin {2}
			// user code end
			handleException(ivjExc);
		}
	}
	return ivjControlTypeLabel;
}
/**
 * Return the PhysicalPointOffsetCheckBox property value.
 * @return JCheckBox
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JCheckBox getPhysicalPointOffsetCheckBox() {
	if (ivjPhysicalPointOffsetCheckBox == null) {
		try {
			ivjPhysicalPointOffsetCheckBox = new JCheckBox();
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
 * @return JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JLabel getPointOffsetLabel() {
	if (ivjPointOffsetLabel == null) {
		try {
			ivjPointOffsetLabel = new JLabel();
			ivjPointOffsetLabel.setName("PointOffsetLabel");
			ivjPointOffsetLabel.setFont(new java.awt.Font("dialog", 0, 14));
			ivjPointOffsetLabel.setText("Point Offset:  ");
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
 * @return JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private JLabel getUsedPointOffsetLabel() {
	if (ivjUsedPointOffsetLabel == null) {
		try {
			ivjUsedPointOffsetLabel = new JLabel();
			ivjUsedPointOffsetLabel.setName("UsedPointOffsetLabel");
			ivjUsedPointOffsetLabel.setText("Offset Used");
			ivjUsedPointOffsetLabel.setMaximumSize(new java.awt.Dimension(180, 20));
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
@Override
public Object getValue(Object val) 
{
	//Assuming val is a real status point
	com.cannontech.database.data.point.StatusPoint point = (com.cannontech.database.data.point.StatusPoint) val;

	Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
	Integer pointOffset = new Integer( ((Number)pointOffsetSpinVal).intValue() );

	Object controlOffsetSpinVal = getControlOffsetSpinner().getValue();
	Integer controlOffset = new Integer( ((Number)controlOffsetSpinVal).intValue() );
	
	String controlType = (String) getControlTypeComboBox().getSelectedItem();
	point.getPointStatusControl().setControlType(controlType);

	if ( (getUsedPointOffsetLabel().getText()) == "" )
		point.getPoint().setPointOffset( pointOffset );
	else
		point.getPoint().setPointOffset( null );

	if( point.getPointStatusControl().hasControl() ) {
		point.getPointStatusControl().setControlOffset(controlOffset);
	}

	return val;
}
/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	com.cannontech.clientutils.CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
}
/**
 * Initializes connections
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initConnections() throws java.lang.Exception {
	// user code begin {1}
	// user code end
	getPhysicalPointOffsetCheckBox().addItemListener(this);
	getControlTypeComboBox().addItemListener(this);
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
		setName("PointStatusPhysicalSettingsPanel");
		setPreferredSize(new java.awt.Dimension(350, 200));
		setLayout(new java.awt.GridBagLayout());
		setSize(367, 245);

		java.awt.GridBagConstraints constraintsPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsPointOffsetLabel.gridx = 0; constraintsPointOffsetLabel.gridy = 1;
		constraintsPointOffsetLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPointOffsetLabel(), constraintsPointOffsetLabel);

		java.awt.GridBagConstraints constraintsControlOffsetLabel = new java.awt.GridBagConstraints();
		constraintsControlOffsetLabel.gridx = 0; constraintsControlOffsetLabel.gridy = 3;
		constraintsControlOffsetLabel.fill = java.awt.GridBagConstraints.HORIZONTAL;
		constraintsControlOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlOffsetLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getControlOffsetLabel(), constraintsControlOffsetLabel);

		java.awt.GridBagConstraints constraintsPhysicalPointOffsetCheckBox = new java.awt.GridBagConstraints();
		constraintsPhysicalPointOffsetCheckBox.gridx = 0; constraintsPhysicalPointOffsetCheckBox.gridy = 0;
		constraintsPhysicalPointOffsetCheckBox.gridwidth = 2;
		constraintsPhysicalPointOffsetCheckBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPhysicalPointOffsetCheckBox.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getPhysicalPointOffsetCheckBox(), constraintsPhysicalPointOffsetCheckBox);

		java.awt.GridBagConstraints constraintsControlTypeLabel = new java.awt.GridBagConstraints();
		constraintsControlTypeLabel.gridx = 0; constraintsControlTypeLabel.gridy = 2;
		constraintsControlTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlTypeLabel.insets = new java.awt.Insets(5, 0, 5, 0);
		add(getControlTypeLabel(), constraintsControlTypeLabel);

		java.awt.GridBagConstraints constraintsControlTypeComboBox = new java.awt.GridBagConstraints();
		constraintsControlTypeComboBox.gridx = 1; constraintsControlTypeComboBox.gridy = 2;
		constraintsControlTypeComboBox.gridwidth = 2;
		constraintsControlTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlTypeComboBox.insets = new java.awt.Insets(5, 10, 5, 30);
		add(getControlTypeComboBox(), constraintsControlTypeComboBox);

		java.awt.GridBagConstraints constraintsPointOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsPointOffsetSpinner.gridx = 1; constraintsPointOffsetSpinner.gridy = 1;
		constraintsPointOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsPointOffsetSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getPointOffsetSpinner(), constraintsPointOffsetSpinner);

		java.awt.GridBagConstraints constraintsControlOffsetSpinner = new java.awt.GridBagConstraints();
		constraintsControlOffsetSpinner.gridx = 1; constraintsControlOffsetSpinner.gridy = 3;
		constraintsControlOffsetSpinner.anchor = java.awt.GridBagConstraints.WEST;
		constraintsControlOffsetSpinner.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getControlOffsetSpinner(), constraintsControlOffsetSpinner);

		java.awt.GridBagConstraints constraintsUsedPointOffsetLabel = new java.awt.GridBagConstraints();
		constraintsUsedPointOffsetLabel.gridx = 2; constraintsUsedPointOffsetLabel.gridy = 1;
		constraintsUsedPointOffsetLabel.anchor = java.awt.GridBagConstraints.WEST;
		constraintsUsedPointOffsetLabel.insets = new java.awt.Insets(5, 10, 5, 0);
		add(getUsedPointOffsetLabel(), constraintsUsedPointOffsetLabel);
		initConnections();
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
	// user code begin {2}
    getControlTypeComboBox().addItem(StatusControlType.NONE.getControlName());
    getControlTypeComboBox().addItem(StatusControlType.LATCH.getControlName());
    getControlTypeComboBox().addItem(StatusControlType.NORMAL.getControlName());
    getControlTypeComboBox().addItem(StatusControlType.PSEUDO.getControlName());
    getControlTypeComboBox().addItem(StatusControlType.SBOLATCH.getControlName());
    getControlTypeComboBox().addItem(StatusControlType.SBOPULSE.getControlName());

    getControlTypeComboBox().setSelectedItem(StatusControlType.NONE.getControlName());
	// user code end
}
/**
 * Method to handle events for the ItemListener interface.
 * @param e java.awt.event.ItemEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void itemStateChanged(java.awt.event.ItemEvent e) {
	// user code begin {1}
	// user code end
	if (e.getSource() == getPhysicalPointOffsetCheckBox()) 
		connEtoC1(e);
	if (e.getSource() == getControlTypeComboBox()) 
		connEtoC2(e);
	// user code begin {2}
	// user code end
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
	try {
		JFrame frame = new JFrame();
		PointStatusPhysicalSettingsPanel aPointStatusPhysicalSettingsPanel;
		aPointStatusPhysicalSettingsPanel = new PointStatusPhysicalSettingsPanel();
		frame.getContentPane().add("Center", aPointStatusPhysicalSettingsPanel);
		frame.setSize(aPointStatusPhysicalSettingsPanel.getSize());
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
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(1), new Integer(10000), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue( new Integer(1) );
		int temp = 2;
		while( getUsedPointOffsetLabel().getText() != "" )
		{
			getPointOffsetSpinner().setValue(new Integer(temp));
			temp++;
		}
		getPointOffsetLabel().setEnabled(true);
		getPointOffsetSpinner().setEnabled(true);
	}
	else
	if ( !getPhysicalPointOffsetCheckBox().isSelected() )
	{
		getPointOffsetSpinner().setValidator(new com.klg.jclass.field.validate.JCIntegerValidator(null, new Integer(0), new Integer(0), null, true, null, new Integer(1), "#,##0.###;-#,##0.###", false, false, false, null, new Integer(0)));
		getPointOffsetSpinner().setValue( new Integer(0) );
		getPointOffsetLabel().setEnabled(false);
		getPointOffsetSpinner().setEnabled(false);
	}

	revalidate();
	repaint();
	return;
}
/**
 * Comment
 */
public void pointOffsetSpinner_ValueChanged(com.klg.jclass.util.value.JCValueEvent arg1)
{
	if (usedPointOffsetsVector != null)
	{
		getUsedPointOffsetLabel().setText("");
		if (usedPointOffsetsVector.size() > 0)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			for (int i = 0; i < usedPointOffsetsVector.size(); i++)
			{
				if (pointOffsetSpinVal instanceof Long)
				{
					if (((Long) pointOffsetSpinVal).longValue() != 0
						&& (((Long) pointOffsetSpinVal).longValue()
							== usedPointOffsetsVector.elementAt(i).getPointOffset()))
					{
						getUsedPointOffsetLabel().setText(
							"Used by " + usedPointOffsetsVector.elementAt(i).getPointName());
						break;
					}
				}
				else if (pointOffsetSpinVal instanceof Integer)
				{
					if (((Integer) pointOffsetSpinVal).intValue() != 0
						&& (((Integer) pointOffsetSpinVal).intValue()
							== usedPointOffsetsVector.elementAt(i).getPointOffset()))
					{
						getUsedPointOffsetLabel().setText(
							"Used by " + usedPointOffsetsVector.elementAt(i).getPointName());
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
public void reinitialize(Integer pointDeviceID, PointType pointType)
{

	getUsedPointOffsetLabel().setText("");
	usedPointOffsetsVector = new Vector<LitePoint>();
	
    List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(pointDeviceID);
    for (LitePoint point : points) {
        if(point.getPointTypeEnum() == pointType) {
            usedPointOffsetsVector.add(point);
        }
    }       
   
	getPointOffsetSpinner().setValue(new Integer(1));
	if (usedPointOffsetsVector.size() > 0)
	{
		for (int i = 0; i < usedPointOffsetsVector.size(); i++)
		{
			Object pointOffsetSpinVal = getPointOffsetSpinner().getValue();
			if (pointOffsetSpinVal instanceof Long)
			{
				if (((Long) pointOffsetSpinVal).intValue()
					== usedPointOffsetsVector.elementAt(i).getPointOffset())
					getPointOffsetSpinner().setValue(new Integer(((Long) pointOffsetSpinVal).intValue() + 1));
				else
					break;
			}
			else if (pointOffsetSpinVal instanceof Integer)
			{
				if (((Integer) pointOffsetSpinVal).intValue()
					== usedPointOffsetsVector.elementAt(i).getPointOffset())
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

@Override
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
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
@Override
public void setValue(Object val) {
}

@Override
public void setFirstFocus() 
{
    // Make sure that when its time to display this panel, the focus starts in the top component
    SwingUtilities.invokeLater( new Runnable() 
        { 
        @Override
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
@Override
public void valueChanged(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	if (arg1.getSource() == getPointOffsetSpinner()) 
		connEtoC3(arg1);
	// user code begin {2}
	// user code end
}
/**
 * Method to handle events for the JCValueListener interface.
 * @param arg1 com.klg.jclass.util.value.JCValueEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
@Override
public void valueChanging(com.klg.jclass.util.value.JCValueEvent arg1) {
	// user code begin {1}
	// user code end
	// user code begin {2}
	// user code end
}
}
