package com.cannontech.dbeditor.editor.contact;

import java.awt.Dimension;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.CaretEvent;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.customer.CICustomerPointData;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class CICustomerPointDataPanel extends JPanel implements java.awt.event.ActionListener, javax.swing.event.CaretListener 

{
     private JLabel actualValueLabel;
    private JLabel txtValueLabel;
    private JComboBox<LitePoint> pointComboBox;
    private JLabel pointLabel;
    private JLabel deviceLabel;
    private JLabel typeLabel;
    private JComboBox<LiteYukonPAObject> deviceComboBox;
    private JComboBox typeComboBox;
    private JTextField textFieldLabel;
    private JLabel basLbl;
    private String type;
    private CICustomerPointPanel parent;
    private JButton removeButton = null;

    /**
     * Constructor
     */
    public CICustomerPointDataPanel(CICustomerPointData pd, CICustomerPointPanel parentPanel) {
        
        super();
        initialize();
        initDeviceComboBox(getJComboDevice());
        initConnections();
        parent = parentPanel;
    }
    
    /**
     * Constructor
     */
    public CICustomerPointDataPanel(CICustomerPointPanel parentPanel) {
        
        super();
        initialize();
        initConnections();
        parent = parentPanel;
    }

    
    private void initialize() 
    {
        try {
            com.cannontech.common.gui.util.TitleBorder ivjLocalBorder2;
            ivjLocalBorder2 = new com.cannontech.common.gui.util.TitleBorder();
            ivjLocalBorder2.setTitle("Point Type");
            this.setBorder(ivjLocalBorder2);
            this.setLayout(new java.awt.GridBagLayout());
            this.setMaximumSize(new Dimension( 400, 140));

            java.awt.GridBagConstraints constraintsRemoveButton = new java.awt.GridBagConstraints();
            constraintsRemoveButton.gridx = 2; constraintsRemoveButton.gridy = 3;
            
            constraintsRemoveButton.gridwidth = 1;
            constraintsRemoveButton.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getRemoveButton(), constraintsRemoveButton);
            
            java.awt.GridBagConstraints constraintsTypeLabel = new java.awt.GridBagConstraints();
            constraintsTypeLabel.gridx = 0; constraintsTypeLabel.gridy = 3;
            constraintsTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsTypeLabel.gridwidth = 1;
            constraintsTypeLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJLabelType(), constraintsTypeLabel);
            
            java.awt.GridBagConstraints constraintsTypeComboBox = new java.awt.GridBagConstraints();
            constraintsTypeComboBox.gridx = 1; constraintsTypeComboBox.gridy = 3;
            constraintsTypeComboBox.anchor = java.awt.GridBagConstraints.WEST;
            constraintsTypeComboBox.gridwidth = 1;
            constraintsTypeComboBox.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJComboBoxType(), constraintsTypeComboBox);

            java.awt.GridBagConstraints constraintsJComboBoxPointBase = new java.awt.GridBagConstraints();
            constraintsJComboBoxPointBase.gridx = 1; constraintsJComboBoxPointBase.gridy = 2;
            constraintsJComboBoxPointBase.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboBoxPointBase.gridwidth = 2;
            constraintsJComboBoxPointBase.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJComboBoxPoint(), constraintsJComboBoxPointBase);

            java.awt.GridBagConstraints constraintsJLabelPointBase = new java.awt.GridBagConstraints();
            constraintsJLabelPointBase.gridx = 0; constraintsJLabelPointBase.gridy = 2;
            constraintsJLabelPointBase.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelPointBase.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJLabelPoint(), constraintsJLabelPointBase);

            java.awt.GridBagConstraints constraintsJLabelDeviceBase = new java.awt.GridBagConstraints();
            constraintsJLabelDeviceBase.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelDeviceBase.gridx = 0; constraintsJLabelDeviceBase.gridy = 1;
            constraintsJLabelDeviceBase.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJLabelDevice(), constraintsJLabelDeviceBase);

            java.awt.GridBagConstraints constraintsJComboDeviceBase = new java.awt.GridBagConstraints();
            constraintsJComboDeviceBase.gridx = 1; constraintsJComboDeviceBase.gridy = 1;
            constraintsJComboDeviceBase.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJComboDeviceBase.gridwidth = 2;
            constraintsJComboDeviceBase.weightx = 1.0;
            constraintsJComboDeviceBase.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJComboDevice(), constraintsJComboDeviceBase);

            java.awt.GridBagConstraints constraintsJTextFieldBaseLabel = new java.awt.GridBagConstraints();
            constraintsJTextFieldBaseLabel.gridx = 1; constraintsJTextFieldBaseLabel.gridy = 0;
            constraintsJTextFieldBaseLabel.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJTextFieldBaseLabel.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJTextFieldLabel(), constraintsJTextFieldBaseLabel);

            java.awt.GridBagConstraints constraintsJLabelBasLbl = new java.awt.GridBagConstraints();
            constraintsJLabelBasLbl.gridx = 0; constraintsJLabelBasLbl.gridy = 0;
            constraintsJLabelBasLbl.anchor = java.awt.GridBagConstraints.WEST;
            constraintsJLabelBasLbl.insets = new java.awt.Insets(5, 5, 5, 5);
            this.add(getJLabelBasLbl(), constraintsJLabelBasLbl);
            
            initDeviceComboBox(getJComboDevice());
            initConnections();
            getJComboDevice().setSelectedIndex(0);
            
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    private void initDeviceComboBox( javax.swing.JComboBox<LiteYukonPAObject> comboBox )
    {
        if( comboBox == null ) return;

        comboBox.removeAllItems();
        setDeviceComboBoxes( comboBox );
    }

    //TODO: This is inefficient
    private void setDeviceComboBoxes( final javax.swing.JComboBox<LiteYukonPAObject> comboBox )
    {     
        if( comboBox == null ) return;
        
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).
            getLitePointsBy(Lists.newArrayList(PointType.Analog, PointType.CalcAnalog));
        
            //ensures uniqueness and ordering by name
            TreeSet<LiteYukonPAObject> paoSet = new TreeSet<LiteYukonPAObject>(LiteComparators.liteStringComparator );

            for(LitePoint litePoint : points) {
                //use the validPt boolean to see if this point is worthy
                if( isPointValid(litePoint) )
                {
                    LiteYukonPAObject liteDevice = YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO( litePoint.getPaobjectID() );

                    if (liteDevice.getPaoType().getPaoClass().isCore())
                        paoSet.add( liteDevice );
                }
            }

            //add the unique ordered elements to the combo box
            Iterator<LiteYukonPAObject> it = paoSet.iterator();
            while( it.hasNext() )
                comboBox.addItem( it.next() );
    }
    
    /**
     * Sets the point selctions to the given ID
     * 
     */
    public void setPointComboBox( int ptId, JComboBox<LitePoint> pointCombo, JComboBox<LiteYukonPAObject> deviceCombo ) 
    {   
        LitePoint litePoint = YukonSpringHook.getBean(PointDao.class).getLitePoint( ptId );

        if( litePoint == null )
        {
            deviceCombo.setSelectedItem( PaoUtils.LITEPAOBJECT_NONE );
        }
        else
        {
            deviceCombo.setSelectedItem( YukonSpringHook.getBean(PaoDao.class).getLiteYukonPAO(litePoint.getPaobjectID()) );
            pointCombo.setSelectedItem( litePoint );
        }
        
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
    
    /**
     * Return the JLabelActualValue111 property value.
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelActualValue() {
        if (actualValueLabel == null) {
            try {
                actualValueLabel = new javax.swing.JLabel();
                actualValueLabel.setName("JLabelActualValueBase");
                actualValueLabel.setFont(new java.awt.Font("Arial", 1, 14));
                actualValueLabel.setText("----");
                actualValueLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
                actualValueLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
                actualValueLabel.setVisible( false );
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return actualValueLabel;
    }
    
    /**
     * Return the JLabelTxtValue111 property value.
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelTxtValue() {
        if (txtValueLabel == null) {
            try {
                txtValueLabel = new javax.swing.JLabel();
                txtValueLabel.setName("JLabelTxtValueBase");
                txtValueLabel.setFont(new java.awt.Font("dialog", 0, 14));
                txtValueLabel.setText("Value:");
                txtValueLabel.setVisible( false );
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return txtValueLabel;
    }
    
    /**
     * Return the JComboBoxPoint111 property value.
     * @return javax.swing.JComboBox
     */
    public javax.swing.JComboBox<LitePoint> getJComboBoxPoint() {
        if (pointComboBox == null) {
            try {
                pointComboBox = new javax.swing.JComboBox<LitePoint>();
                pointComboBox.setName("JComboBoxPointBase");
                pointComboBox.setEnabled( false );
                pointComboBox.setMinimumSize(new Dimension( 225, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return pointComboBox;
    }
    
    /**
     * 
     * @return javax.swing.JComboBox
     */
    public javax.swing.JComboBox getJComboBoxType() {
        if (typeComboBox == null) {
            try {
                typeComboBox = new javax.swing.JComboBox();
                typeComboBox.setName("JComboBoxType");
                
                typeComboBox.setMinimumSize(new Dimension( 225, 20));
                typeComboBox.setEditable(true);
                
                CICustomerPointType[] names = CICustomerPointType.values();
                for (int i = 0; i < names.length; i++)
                {
                    typeComboBox.addItem(names[i].toString());
                }
                typeComboBox.setSelectedIndex(0);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return typeComboBox;
    }
    
    /**
     * Return the JLabelState111 property value.
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelPoint() {
        if (pointLabel == null) {
            try {
                pointLabel = new javax.swing.JLabel();
                pointLabel.setName("JLabelPointBase");
                pointLabel.setFont(new java.awt.Font("dialog", 0, 14));
                pointLabel.setText("Point:");
                
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return pointLabel;
    }
    
    /**
     * 
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelType() {
        if (typeLabel == null) {
            try {
                typeLabel = new javax.swing.JLabel();
                typeLabel.setName("JLabelType");
                typeLabel.setFont(new java.awt.Font("dialog", 0, 14));
                typeLabel.setText("Type:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return typeLabel;
    }
    
    /**
     * Return the JLabelState21 property value.
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelDevice() {
        if (deviceLabel == null) {
            try {
                deviceLabel = new javax.swing.JLabel();
                deviceLabel.setName("JLabelDeviceBase");
                deviceLabel.setFont(new java.awt.Font("dialog", 0, 14));
                deviceLabel.setText("Device:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return deviceLabel;
    }
    
    /**
     * Return the JComboDevice111 property value.
     * @return javax.swing.JComboBox
     */
    public javax.swing.JComboBox<LiteYukonPAObject> getJComboDevice() {
        if (deviceComboBox == null) {
            try {
                deviceComboBox = new javax.swing.JComboBox<LiteYukonPAObject>();
                deviceComboBox.setName("JComboDeviceBase");
                deviceComboBox.setMinimumSize(new Dimension( 225, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return deviceComboBox;
    }
    
    /**
     * Return the JTextField111 property value.
     * @return javax.swing.JTextField
     */
    public javax.swing.JTextField getJTextFieldLabel() {
        if (textFieldLabel == null) 
        {
            try 
            {
                textFieldLabel = new javax.swing.JTextField();
                textFieldLabel.setName("JTextFieldBaseLabel");
                textFieldLabel.setText("Demand");
                textFieldLabel.setMinimumSize(new Dimension( 225, 20));
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return textFieldLabel;
    }
    
    /**
     * Return the JLabelBasLbl property value.
     * @return javax.swing.JLabel
     */
    public javax.swing.JLabel getJLabelBasLbl() 
    {
        if (basLbl == null) {
            try 
            {
                basLbl = new javax.swing.JLabel();
                basLbl.setName("JLabelBasLbl");
                basLbl.setFont(new java.awt.Font("dialog", 0, 14));
                basLbl.setText("Label:");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return basLbl;
    }
    
    private JButton getRemoveButton()
    {
        if (removeButton == null) {
            try {
                removeButton = new javax.swing.JButton();
                removeButton.setName("ButtonPanel");
                removeButton.setText("Remove");
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return removeButton;
    }

    
    public String getType()
    {
        return type;
    }
    
    public void setType(String value)
    {
        type = value;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(Throwable exception) 
    {

        /* Uncomment the following lines to print uncaught exceptions to stdout */
        CTILogger.info("--------- UNCAUGHT EXCEPTION ---------");
        CTILogger.error( exception.getMessage(), exception );;
    }
    
    public void jComboBoxPoint_ActionPerformed( JComboBox<LitePoint> pointCombo )
    {
        if( pointCombo == null ) return;

        LitePoint litePoint = (LitePoint)pointCombo.getSelectedItem();
        if( litePoint != null )
        {
        }
    }

    /**
     * Populates the given pointComboBox with the valid points from the selected
     * device in the deviceComboBox
     * 
     */
    public void jComboBoxDevice_ActionPerformed( JComboBox<LiteYukonPAObject> deviceCombo, JComboBox<LitePoint> pointCombo ) 
    {
        if( deviceCombo == null || pointCombo == null ) return;
        int deviceID = 0;
        LiteYukonPAObject pao = (LiteYukonPAObject)deviceCombo.getSelectedItem();
        if (pao == null)
        {
            return;
        }else
        {
            deviceID = pao.getYukonID();   
        }
        pointCombo.removeAllItems();

        //if the (none) object is selected, just return
        pointCombo.setEnabled( deviceCombo.getSelectedItem() != PaoUtils.LITEPAOBJECT_NONE );

        if( deviceCombo.getSelectedItem() == PaoUtils.LITEPAOBJECT_NONE )
        {
            return;
        }

        //add the point to the pointCombo if the point is valid
        List<LitePoint> litePts = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceID);
        Collections.sort(litePts, LiteComparators.liteStringComparator);
        for (LitePoint point : litePts) {
            if( isPointValid(point) ) {
                pointCombo.addItem(point);
            }
        }

        return;
    }

    /**
     * Method to handle events for the ActionListener interface.
     * @param e java.awt.event.ActionEvent
     */
    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        if (e.getSource() == getJComboDevice()) 
        {
            jComboBoxDevice_ActionPerformed(getJComboDevice(), getJComboBoxPoint());
        } else if (e.getSource() == getJComboBoxPoint()) 
        {
            jComboBoxPoint_ActionPerformed(getJComboBoxPoint());
        }else if (e.getSource() == getRemoveButton())
        {
            removeButton_actionPerformed();
        }
        
    }
    
    private void removeButton_actionPerformed()
    {
        parent.removeButton_ActionPerformed(this);
    }

    @Override
    public void caretUpdate(CaretEvent e) 
    {
        parent.fireInputUpdate();
    }
    
    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections()
    {
        try 
        {
            getJTextFieldLabel().addCaretListener(this);
            getJComboDevice().addActionListener(this);
            getJComboBoxPoint().addActionListener(this);
            getRemoveButton().addActionListener(this);
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }
}