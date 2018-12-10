package com.cannontech.dbeditor.editor.contact;


import java.awt.Color;
import java.awt.Dimension;
import java.util.ListIterator;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.customer.CICustomerBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.customer.CICustomerPointData;

public class CICustomerPointPanel extends com.cannontech.common.gui.util.DataInputPanel implements java.awt.event.ActionListener {
    private javax.swing.JPanel ivjJPanelScroller = null;
	private javax.swing.JScrollPane ivjJScrollPane = null;
    private JButton addButton = null;
    private java.util.Vector pointPanelVector = new Vector(4);

/**
 * Constructor
 */
public CICustomerPointPanel() {
	super();
	initialize();
}


/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void actionPerformed(java.awt.event.ActionEvent e) 
{
    if(e.getSource() == getAddButton())
    {
        addButton_ActionPerformed(e);
    }
    
	fireInputUpdate();
}

/**
 * Method to handle events for the ActionListener interface.
 * @param e java.awt.event.ActionEvent
 */
public void addButton_ActionPerformed(java.awt.event.ActionEvent e) 
{
            addDefaultPanel();
            getJScrollPane().invalidate();
            getJScrollPane().doLayout();
            getJScrollPane().repaint();
            getJScrollPane().revalidate();
}

public void removeButton_ActionPerformed(CICustomerPointDataPanel p) 
{
    
    getJPanelScroller().remove(p);
    pointPanelVector.remove(p);
    getJPanelScroller().invalidate();
    getJPanelScroller().doLayout();
    getJPanelScroller().repaint();
    getJPanelScroller().revalidate();
    
    fireInputUpdate();
}

/**
 * Test to see if the given LitePoint is valid
 * 
 */
private boolean isPointValid( LitePoint lPoint )
{
    return
        lPoint != null && 
        (lPoint.getPointTypeEnum() == PointType.Analog
        || lPoint.getPointTypeEnum() == PointType.CalcAnalog);
    
}

private CICustomerPointData createPointData( JComboBox pointCombo, JTextField labelField, String type )
{
	CICustomerPointData ciPtData = new CICustomerPointData();
	
	if( labelField.getText() != null && labelField.getText().length() > 0 )
		ciPtData.setOptionalLabel( labelField.getText() );

	LitePoint litePoint = (LitePoint)pointCombo.getSelectedItem();
	ciPtData.setPointID( new Integer(litePoint.getPointID()) );

	ciPtData.setType( type );
	
	return ciPtData;
}

private JButton getAddButton()
{
    if (addButton == null) {
        try {
            addButton = new javax.swing.JButton();
            addButton.setName("ButtonPanel");
            addButton.setText("Add");
            addButton.setPreferredSize(new Dimension( 60, 25 ));
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    return addButton;
}


/**
 * Return the JPanelScroller property value.
 * @return javax.swing.JPanel
 */
private javax.swing.JPanel getJPanelScroller() {
	if (ivjJPanelScroller == null) {
		try {
			ivjJPanelScroller = new javax.swing.JPanel();
			ivjJPanelScroller.setName("JPanelScroller");
            BoxLayout boxLayout = new BoxLayout(getJPanelScroller(), BoxLayout.Y_AXIS);
			ivjJPanelScroller.setLayout(boxLayout);
            ivjJPanelScroller.setBackground(Color.GRAY);
			ivjJPanelScroller.setBounds(0, 0, 160, 120);

		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJPanelScroller;
}

/**
 * Return the JScrollPane property value.
 * @return javax.swing.JScrollPane
 */
private javax.swing.JScrollPane getJScrollPane() {
	if (ivjJScrollPane == null) {
		try {
			ivjJScrollPane = new javax.swing.JScrollPane();
			ivjJScrollPane.setName("JScrollPane");
			ivjJScrollPane.setPreferredSize(new java.awt.Dimension(163, 600));
			ivjJScrollPane.setMaximumSize(new java.awt.Dimension(163, 32767));
            ivjJScrollPane.getVerticalScrollBar().setUnitIncrement(30);
			getJScrollPane().setViewportView(getJPanelScroller());
		} catch (java.lang.Throwable ivjExc) {
			handleException(ivjExc);
		}
	}
	return ivjJScrollPane;
}

/**
 * getValue method comment.
 */
public Object getValue(Object o) 
{
	CICustomerBase ciCust = (CICustomerBase)o;
	Vector tempVect = new Vector(8);		
    
	ListIterator iter = pointPanelVector.listIterator();
    
    while(iter.hasNext())
    {
        CICustomerPointDataPanel pointPanel = (CICustomerPointDataPanel) iter.next();
        CICustomerPointData pointData = createPointData(pointPanel.getJComboBoxPoint(), pointPanel.getJTextFieldLabel(), pointPanel.getJComboBoxType().getSelectedItem().toString());
        tempVect.add(pointData);
    }
    
    CICustomerPointData[] ciCustPointDatas = new CICustomerPointData[ tempVect.size() ];
    ciCust.setCiCustomerPointData( (CICustomerPointData[])tempVect.toArray(ciCustPointDatas) );
    
	return ciCust;
}

/**
 * Called whenever the part throws an exception.
 * @param exception java.lang.Throwable
 */
private void handleException(Throwable exception) {

	/* Uncomment the following lines to print uncaught exceptions to stdout */
	CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
	CTILogger.error( exception.getMessage(), exception );
}



/**
 * Initialize the class.
 */
private void initialize() {
	try {
		setName("CustomerAddressPanel");
		setToolTipText("");
		setLayout(new java.awt.GridBagLayout());
		setSize(416, 348);
        
        java.awt.GridBagConstraints constraintsAddButton = new java.awt.GridBagConstraints();
        constraintsAddButton.gridx = 0; constraintsAddButton.gridy = 0;
        constraintsAddButton.ipadx = 2;
        constraintsAddButton.ipady = 2;
        constraintsAddButton.insets = new java.awt.Insets(4,4,4,4);
        add(getAddButton(),  constraintsAddButton);
        
        java.awt.GridBagConstraints constraintsScrollerPane = new java.awt.GridBagConstraints();
        constraintsScrollerPane.gridx = 0; constraintsScrollerPane.gridy = 1;
        constraintsScrollerPane.weightx = 1.0;
        constraintsScrollerPane.weighty = 1.0;
        constraintsScrollerPane.fill = java.awt.GridBagConstraints.BOTH;
        constraintsScrollerPane.ipadx = 2;
        constraintsScrollerPane.ipady = 2;
        constraintsScrollerPane.insets = new java.awt.Insets(4,4,4,4);
		add(getJScrollPane(), constraintsScrollerPane);
        
        getAddButton().addActionListener(this);
        
	} catch (java.lang.Throwable ivjExc) {
		handleException(ivjExc);
	}
}

/**
 * This method was created in VisualAge.
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
		javax.swing.JFrame frame = new javax.swing.JFrame();
		CICustomerPointPanel aCICustomerPointPanel;
		aCICustomerPointPanel = new CICustomerPointPanel();
		frame.setContentPane(aCICustomerPointPanel);
		frame.setSize(aCICustomerPointPanel.getSize());
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
		exception.printStackTrace(System.out);
	}
}

private void addDefaultPanel()
{
    CICustomerPointDataPanel pointPanel = new CICustomerPointDataPanel(this);

    getJPanelScroller().add(pointPanel);
    getJPanelScroller().revalidate();
    pointPanelVector.add(pointPanel);
    
}

private void addNewPanel(CICustomerPointData pd)
{
    CICustomerPointDataPanel pointPanel = new CICustomerPointDataPanel(pd, this);
    
    pointPanel.setPointComboBox( pd.getPointID().intValue(), pointPanel.getJComboBoxPoint(), pointPanel.getJComboDevice() );
    pointPanel.getJTextFieldLabel().setText( pd.getOptionalLabel() );
    pointPanel.getJComboBoxType().setSelectedItem(pd.getType());
    pointPanel.setType(pd.getType());
    getJPanelScroller().add(pointPanel);
    
    pointPanelVector.add(pointPanel);
    
}

/**
 * setValue method comment.
 */
public void setValue(Object o) 
{
	if( o == null || !(o instanceof CICustomerBase) )
		return;

	CICustomerBase ciCust = (CICustomerBase)o;
	
	for( int i = 0; i < ciCust.getCiCustomerPointData().length; i++ )
	{
		CICustomerPointData ciCustPtData = ciCust.getCiCustomerPointData()[i];
        addNewPanel(ciCustPtData);
	}
}
}