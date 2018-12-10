package com.cannontech.dbeditor.wizard.changetype.point;

import java.util.List;

import com.cannontech.common.pao.PaoClass;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.db.point.Point;
import com.cannontech.database.db.point.PointAlarming;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This type was created in VisualAge.
 */

public class PointTypePanel extends com.cannontech.common.gui.util.DataInputPanel {
    
    private IDatabaseCache cache = DefaultDatabaseCache.getInstance();
    
    private PointType pointType = null;
    private javax.swing.JRadioButton ivjAnalogRadioButton = null;
    private javax.swing.JRadioButton ivjCalculatedRadioButton = null;
    private javax.swing.JLabel ivjSelectTypeLabel = null;
    private javax.swing.JRadioButton ivjStatusRadioButton = null;
    private javax.swing.JRadioButton ivjAccumulatorRadioButton = null;
    private javax.swing.JRadioButton ivjDemandAccumulatorRadioButton = null;
    private javax.swing.ButtonGroup ivjButtonGroup = null;
    IvjEventHandler ivjEventHandler = new IvjEventHandler();
    private javax.swing.JRadioButton ivjJRadioButtonAnalogOutput = null;
    private javax.swing.JRadioButton ivjJRadioButtonStatusOutput = null;

class IvjEventHandler implements java.awt.event.ActionListener {
        public void actionPerformed(java.awt.event.ActionEvent e) {
            if (e.getSource() == PointTypePanel.this.getJRadioButtonAnalogOutput()) 
                connEtoC1(e);
            if (e.getSource() == PointTypePanel.this.getJRadioButtonStatusOutput()) 
                connEtoC2(e);
            if (e.getSource() == PointTypePanel.this.getAnalogRadioButton()) 
                connEtoC3(e);
            if (e.getSource() == PointTypePanel.this.getStatusRadioButton()) 
                connEtoC4(e);
            if (e.getSource() == PointTypePanel.this.getAccumulatorRadioButton()) 
                connEtoC5(e);
            if (e.getSource() == PointTypePanel.this.getDemandAccumulatorRadioButton()) 
                connEtoC6(e);
            if (e.getSource() == PointTypePanel.this.getCalculatedRadioButton()) 
                connEtoC7(e);
        };
    };
public PointTypePanel() {
    super();
    initialize();
}
/**
 * Comment
 */
public void accumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    getJRadioButtonAnalogOutput().setVisible(false);
    getJRadioButtonStatusOutput().setVisible(false);
    
    pointType = PointType.PulseAccumulator;

    return;
}
/**
 * Comment
 */
public void analogRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    getJRadioButtonAnalogOutput().setVisible(false);
    getJRadioButtonStatusOutput().setVisible(false);
    
    pointType = PointType.Analog;
    
    return;
}
/**
 * Comment
 */
public void calculatedRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    pointType = PointType.CalcAnalog;
    
    getJRadioButtonAnalogOutput().setVisible(true);
    getJRadioButtonAnalogOutput().setSelected(true);
    getJRadioButtonStatusOutput().setVisible(true);
    getJRadioButtonStatusOutput().setSelected(false);
        
    return;
}
/**
 * connEtoC1:  (JRadioButtonAnalogOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonAnalogOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC1(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.jRadioButtonAnalogOutput_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC2:  (JRadioButtonStatusOutput.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.jRadioButtonStatusOutput_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC2(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.jRadioButtonStatusOutput_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC3:  (AnalogRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.analogRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC3(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.analogRadioButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC4:  (StatusRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.statusRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC4(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.statusRadioButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC5:  (AccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.accumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC5(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.accumulatorRadioButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC6:  (DemandAccumulatorRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.demandAccumulatorRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC6(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.demandAccumulatorRadioButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoC7:  (CalculatedRadioButton.action.actionPerformed(java.awt.event.ActionEvent) --> PointTypePanel.calculatedRadioButton_ActionPerformed(Ljava.awt.event.ActionEvent;)V)
 * @param arg1 java.awt.event.ActionEvent
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoC7(java.awt.event.ActionEvent arg1) {
    try {
        // user code begin {1}
        // user code end
        this.calculatedRadioButton_ActionPerformed(arg1);
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoM1:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM1() {
    try {
        // user code begin {1}
        // user code end
        getButtonGroup().add(getAccumulatorRadioButton());
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoM2:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM2() {
    try {
        // user code begin {1}
        // user code end
        getButtonGroup().add(getStatusRadioButton());
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoM3:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM3() {
    try {
        // user code begin {1}
        // user code end
        getButtonGroup().add(getCalculatedRadioButton());
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM4() {
    try {
        // user code begin {1}
        // user code end
        getButtonGroup().add(getAnalogRadioButton());
        // user code begin {2}
        // user code end
    } catch (java.lang.Throwable ivjExc) {
        // user code begin {3}
        // user code end
        handleException(ivjExc);
    }
}
/**
 * connEtoM4:  (PointTypePanel.initialize() --> ButtonGroup.add(Ljavax.swing.AbstractButton;)V)
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void connEtoM5() {
    try {
        // user code begin {1}
        // user code end
        getButtonGroup().add(getDemandAccumulatorRadioButton());
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
public void demandAccumulatorRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    getJRadioButtonAnalogOutput().setVisible(false);
    getJRadioButtonStatusOutput().setVisible(false);
    
    pointType = PointType.DemandAccumulator;

    return;
}
/**
 * Return the JRadioButton3 property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAccumulatorRadioButton() {
    if (ivjAccumulatorRadioButton == null) {
        try {
            ivjAccumulatorRadioButton = new javax.swing.JRadioButton();
            ivjAccumulatorRadioButton.setName("AccumulatorRadioButton");
            ivjAccumulatorRadioButton.setText("Pulse Accumulator");
            ivjAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(103, 27));
            ivjAccumulatorRadioButton.setActionCommand("Accumulator");
            ivjAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
            ivjAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(103, 27));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAccumulatorRadioButton;
}
/**
 * Return the AnalogRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getAnalogRadioButton() {
    if (ivjAnalogRadioButton == null) {
        try {
            ivjAnalogRadioButton = new javax.swing.JRadioButton();
            ivjAnalogRadioButton.setName("AnalogRadioButton");
            ivjAnalogRadioButton.setText("Analog");
            ivjAnalogRadioButton.setMaximumSize(new java.awt.Dimension(69, 27));
            ivjAnalogRadioButton.setActionCommand("Analog");
            ivjAnalogRadioButton.setSelected(true);
            ivjAnalogRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
            ivjAnalogRadioButton.setMinimumSize(new java.awt.Dimension(69, 27));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjAnalogRadioButton;
}

/**
 * Return the ButtonGroup property value.
 * @return javax.swing.ButtonGroup
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.ButtonGroup getButtonGroup() {
    if (ivjButtonGroup == null) {
        try {
            ivjButtonGroup = new javax.swing.ButtonGroup();
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjButtonGroup;
}
/**
 * Return the CalculatedRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButton getCalculatedRadioButton() {
    if (ivjCalculatedRadioButton == null) {
        try {
            ivjCalculatedRadioButton = new javax.swing.JRadioButton();
            ivjCalculatedRadioButton.setName("CalculatedRadioButton");
            ivjCalculatedRadioButton.setText("Calculated");
            ivjCalculatedRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
            ivjCalculatedRadioButton.setActionCommand("Calculated");
            ivjCalculatedRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
            ivjCalculatedRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjCalculatedRadioButton;
}
/**
 * Return the DemandAccumulatorRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getDemandAccumulatorRadioButton() {
    if (ivjDemandAccumulatorRadioButton == null) {
        try {
            ivjDemandAccumulatorRadioButton = new javax.swing.JRadioButton();
            ivjDemandAccumulatorRadioButton.setName("DemandAccumulatorRadioButton");
            ivjDemandAccumulatorRadioButton.setText("Demand Accumulator");
            ivjDemandAccumulatorRadioButton.setMaximumSize(new java.awt.Dimension(92, 27));
            ivjDemandAccumulatorRadioButton.setActionCommand("Calculated");
            ivjDemandAccumulatorRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
            ivjDemandAccumulatorRadioButton.setMinimumSize(new java.awt.Dimension(92, 27));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjDemandAccumulatorRadioButton;
}
/**
 * Return the JRadioButtonAnalogOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonAnalogOutput() {
    if (ivjJRadioButtonAnalogOutput == null) {
        try {
            ivjJRadioButtonAnalogOutput = new javax.swing.JRadioButton();
            ivjJRadioButtonAnalogOutput.setName("JRadioButtonAnalogOutput");
            ivjJRadioButtonAnalogOutput.setFont(new java.awt.Font("dialog", 0, 12));
            ivjJRadioButtonAnalogOutput.setText("Analog Output");
            // user code begin {1}
            ivjJRadioButtonAnalogOutput.setVisible(false);
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjJRadioButtonAnalogOutput;
}
/**
 * Return the JRadioButtonStatusOutput property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JRadioButton getJRadioButtonStatusOutput() {
    if (ivjJRadioButtonStatusOutput == null) {
        try {
            ivjJRadioButtonStatusOutput = new javax.swing.JRadioButton();
            ivjJRadioButtonStatusOutput.setName("JRadioButtonStatusOutput");
            ivjJRadioButtonStatusOutput.setFont(new java.awt.Font("dialog", 0, 12));
            ivjJRadioButtonStatusOutput.setText("Status Output");
            // user code begin {1}
            ivjJRadioButtonStatusOutput.setVisible(false);
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjJRadioButtonStatusOutput;
}
/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @return int
 */
public PointType getPointType()
{
    if (pointType == null)
    {
        if (getAnalogRadioButton().isSelected())
        {
            pointType = PointType.Analog;
        }
        else if (getStatusRadioButton().isSelected())
        {
            pointType = PointType.Status;
        }
        else if (getAccumulatorRadioButton().isSelected())
        {
            pointType = PointType.PulseAccumulator;
        }
        else if (getDemandAccumulatorRadioButton().isSelected())
        {
            pointType = PointType.DemandAccumulator;
        }
        else if (getCalculatedRadioButton().isSelected() && getJRadioButtonAnalogOutput().isSelected())
        {
            return PointType.CalcAnalog;
        }
        else if (getCalculatedRadioButton().isSelected() && getJRadioButtonStatusOutput().isSelected())
        {
            return PointType.CalcStatus;
        }
        else
            throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
    }

    return pointType;
}
/**
 * This method was created in VisualAge.
 * @return int
 */
private PointType getSelectedType()
{

    if (getAnalogRadioButton().isSelected())
    {
        return PointType.Analog;
    }
    else if (getStatusRadioButton().isSelected())
    {
        return PointType.Status;
    }
    else if (getAccumulatorRadioButton().isSelected())
    {
        return PointType.PulseAccumulator;
    }
    else if (getDemandAccumulatorRadioButton().isSelected())
    {
        return PointType.DemandAccumulator;
    }
    else if (getCalculatedRadioButton().isSelected() && getJRadioButtonAnalogOutput().isSelected())
    {
        return PointType.CalcAnalog;
    }
    else if (getCalculatedRadioButton().isSelected() && getJRadioButtonStatusOutput().isSelected())
    {
        return PointType.CalcStatus;
    }
    else
        throw new Error(getClass() + "::getSelectedType() - No radio button is selected");
}
/**
 * Return the SelectTypeLabel property value.
 * @return javax.swing.JLabel
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private javax.swing.JLabel getSelectTypeLabel() {
    if (ivjSelectTypeLabel == null) {
        try {
            ivjSelectTypeLabel = new javax.swing.JLabel();
            ivjSelectTypeLabel.setName("SelectTypeLabel");
            ivjSelectTypeLabel.setFont(new java.awt.Font("dialog", 0, 14));
            ivjSelectTypeLabel.setText("Select the type of point:");
            ivjSelectTypeLabel.setMaximumSize(new java.awt.Dimension(149, 19));
            ivjSelectTypeLabel.setMinimumSize(new java.awt.Dimension(149, 19));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjSelectTypeLabel;
}
/**
 * Return the StatusRadioButton property value.
 * @return javax.swing.JRadioButton
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
public javax.swing.JRadioButton getStatusRadioButton() {
    if (ivjStatusRadioButton == null) {
        try {
            ivjStatusRadioButton = new javax.swing.JRadioButton();
            ivjStatusRadioButton.setName("StatusRadioButton");
            ivjStatusRadioButton.setText("Status");
            ivjStatusRadioButton.setMaximumSize(new java.awt.Dimension(65, 27));
            ivjStatusRadioButton.setActionCommand("Status");
            ivjStatusRadioButton.setFont(new java.awt.Font("dialog", 0, 14));
            ivjStatusRadioButton.setMinimumSize(new java.awt.Dimension(65, 27));
            // user code begin {1}
            // user code end
        } catch (java.lang.Throwable ivjExc) {
            // user code begin {2}
            // user code end
            handleException(ivjExc);
        }
    }
    return ivjStatusRadioButton;
}
/**
* This method was created in VisualAge.
* @return java.lang.Object
* @param val java.lang.Object
*/
public Object getValue(Object val)
{
    PointType type = getSelectedType();

    if (val == null)
        return type;
    else
    {

        ((PointBase) val).getPoint().setPointTypeEnum(type);
        PointAlarming pointAlarming = ((PointBase) val).getPointAlarming();
        Point point = ((PointBase) val).getPoint();

        try
        {
            com.cannontech.database.Transaction t =
                com.cannontech.database.Transaction.createTransaction(
                    com.cannontech.database.Transaction.DELETE_PARTIAL,
                    ((com.cannontech.database.db.DBPersistent) val));

            val = t.execute();
        }
        catch (com.cannontech.database.TransactionException e)
        {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }

        val = com.cannontech.database.data.point.PointFactory.createPoint(type.getPointTypeId());

        ((com.cannontech.database.data.point.PointBase) val).setPoint(point);
        ((com.cannontech.database.data.point.PointBase) val).setPointAlarming(pointAlarming);
        ((com.cannontech.database.data.point.PointBase) val).setPointID(((com.cannontech.database.data.point.PointBase) val).getPoint().getPointID());

        if (val instanceof com.cannontech.database.data.point.StatusPoint)
             ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(new Integer(1));
        else if (val instanceof com.cannontech.database.data.point.AccumulatorPoint)
            ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
                new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));
        else if (val instanceof com.cannontech.database.data.point.CalculatedPoint)
        {
            if(type == PointType.CalcStatus)
                {
                    ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
                            new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_TWO_STATE_STATUS));
                }else
                    ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
                            new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));
        }
        else if (val instanceof com.cannontech.database.data.point.AnalogPoint)
            ((com.cannontech.database.data.point.PointBase) val).getPoint().setStateGroupID(
                new Integer(com.cannontech.database.db.state.StateGroupUtils.STATEGROUP_ANALOG));

        //resets the pointOffset to next available pointOffset for the new type
        java.util.Vector usedPointOffsetsVector = new java.util.Vector();

        //fill vector with points of same deviceID
        int deviceId = ((PointBase)val).getPoint().getPaoID();
        List<LitePoint> points = YukonSpringHook.getBean(PointDao.class).getLitePointsByPaObjectId(deviceId);
        for (LitePoint p : points) {
            if(type == p.getPointTypeEnum()) {
                usedPointOffsetsVector.add(p);
            }
        }        

        //search through vector to find next available pointOffset -- if vector is empty then pointoffset the same
        int pointOffset = 1;
        if (usedPointOffsetsVector.size() > 0)
        {
            for (int i = 0; i < usedPointOffsetsVector.size(); i++)
            {
                if (pointOffset == ((com.cannontech.database.data.lite.LitePoint) usedPointOffsetsVector.elementAt(i)).getPointOffset())
                {
                    pointOffset = pointOffset + 1;
                    i = -1;
                }

            }

        }
        ((com.cannontech.database.data.point.PointBase) val).getPoint().setPointOffset(new Integer(pointOffset));
        try
        {
            com.cannontech.database.Transaction t2 =
                com.cannontech.database.Transaction.createTransaction(
                    com.cannontech.database.Transaction.ADD_PARTIAL,
                    ((com.cannontech.database.db.DBPersistent) val));

            val = t2.execute();

        }
        catch (com.cannontech.database.TransactionException e)
        {
            com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
        }

        return val;
    }
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
    getJRadioButtonAnalogOutput().addActionListener(ivjEventHandler);
    getJRadioButtonStatusOutput().addActionListener(ivjEventHandler);
    getAnalogRadioButton().addActionListener(ivjEventHandler);
    getStatusRadioButton().addActionListener(ivjEventHandler);
    getAccumulatorRadioButton().addActionListener(ivjEventHandler);
    getDemandAccumulatorRadioButton().addActionListener(ivjEventHandler);
    getCalculatedRadioButton().addActionListener(ivjEventHandler);
}
/**
 * Initialize the class.
 */
/* WARNING: THIS METHOD WILL BE REGENERATED. */
private void initialize() {
    try {
        // user code begin {1}
        initConnections();
        connEtoM1();
        connEtoM2();
        connEtoM3();
        connEtoM4();
        connEtoM5();
        // user code end
        setName("PointTypePanel");
        setLayout(new java.awt.GridBagLayout());
        setSize(294, 222);

        java.awt.GridBagConstraints constraintsSelectTypeLabel = new java.awt.GridBagConstraints();
        constraintsSelectTypeLabel.gridx = 1; constraintsSelectTypeLabel.gridy = 1;
        constraintsSelectTypeLabel.anchor = java.awt.GridBagConstraints.WEST;
        constraintsSelectTypeLabel.ipadx = 31;
        constraintsSelectTypeLabel.insets = new java.awt.Insets(11, 57, 0, 57);
        add(getSelectTypeLabel(), constraintsSelectTypeLabel);

        java.awt.GridBagConstraints constraintsAnalogRadioButton = new java.awt.GridBagConstraints();
        constraintsAnalogRadioButton.gridx = 1; constraintsAnalogRadioButton.gridy = 2;
        constraintsAnalogRadioButton.anchor = java.awt.GridBagConstraints.WEST;
        constraintsAnalogRadioButton.insets = new java.awt.Insets(0, 77, 0, 148);
        add(getAnalogRadioButton(), constraintsAnalogRadioButton);

        java.awt.GridBagConstraints constraintsStatusRadioButton = new java.awt.GridBagConstraints();
        constraintsStatusRadioButton.gridx = 1; constraintsStatusRadioButton.gridy = 3;
        constraintsStatusRadioButton.anchor = java.awt.GridBagConstraints.WEST;
        constraintsStatusRadioButton.insets = new java.awt.Insets(0, 77, 0, 152);
        add(getStatusRadioButton(), constraintsStatusRadioButton);

        java.awt.GridBagConstraints constraintsAccumulatorRadioButton = new java.awt.GridBagConstraints();
        constraintsAccumulatorRadioButton.gridx = 1; constraintsAccumulatorRadioButton.gridy = 4;
        constraintsAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.WEST;
        constraintsAccumulatorRadioButton.ipadx = 39;
        constraintsAccumulatorRadioButton.insets = new java.awt.Insets(0, 77, 0, 75);
        add(getAccumulatorRadioButton(), constraintsAccumulatorRadioButton);

        java.awt.GridBagConstraints constraintsCalculatedRadioButton = new java.awt.GridBagConstraints();
        constraintsCalculatedRadioButton.gridx = 1; constraintsCalculatedRadioButton.gridy = 6;
        constraintsCalculatedRadioButton.anchor = java.awt.GridBagConstraints.WEST;
        constraintsCalculatedRadioButton.insets = new java.awt.Insets(0, 77, 0, 125);
        add(getCalculatedRadioButton(), constraintsCalculatedRadioButton);

        java.awt.GridBagConstraints constraintsDemandAccumulatorRadioButton = new java.awt.GridBagConstraints();
        constraintsDemandAccumulatorRadioButton.gridx = 1; constraintsDemandAccumulatorRadioButton.gridy = 5;
        constraintsDemandAccumulatorRadioButton.anchor = java.awt.GridBagConstraints.WEST;
        constraintsDemandAccumulatorRadioButton.ipadx = 68;
        constraintsDemandAccumulatorRadioButton.insets = new java.awt.Insets(0, 77, 0, 57);
        add(getDemandAccumulatorRadioButton(), constraintsDemandAccumulatorRadioButton);

        java.awt.GridBagConstraints constraintsJRadioButtonAnalogOutput = new java.awt.GridBagConstraints();
        constraintsJRadioButtonAnalogOutput.gridx = 1; constraintsJRadioButtonAnalogOutput.gridy = 7;
        constraintsJRadioButtonAnalogOutput.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJRadioButtonAnalogOutput.insets = new java.awt.Insets(0, 120, 0, 72);
        add(getJRadioButtonAnalogOutput(), constraintsJRadioButtonAnalogOutput);

        java.awt.GridBagConstraints constraintsJRadioButtonStatusOutput = new java.awt.GridBagConstraints();
        constraintsJRadioButtonStatusOutput.gridx = 1; constraintsJRadioButtonStatusOutput.gridy = 8;
        constraintsJRadioButtonStatusOutput.anchor = java.awt.GridBagConstraints.WEST;
        constraintsJRadioButtonStatusOutput.insets = new java.awt.Insets(0, 120, 9, 75);
        add(getJRadioButtonStatusOutput(), constraintsJRadioButtonStatusOutput);
        initConnections();
    } catch (java.lang.Throwable ivjExc) {
        handleException(ivjExc);
    }
    // user code begin {2}
    
        
    // user code end
}
/**
 * Comment
 */
public void jRadioButtonAnalogOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    pointType = PointType.CalcAnalog;
    getJRadioButtonStatusOutput().setSelected(false);
    
    return;
}
/**
 * Comment
 */
public void jRadioButtonStatusOutput_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    pointType = PointType.CalcStatus;
    getJRadioButtonAnalogOutput().setSelected(false);
    
    return;
}
/**
 * main entrypoint - starts the part when it is run as an application
 * @param args java.lang.String[]
 */
public static void main(java.lang.String[] args) {
    try {
        java.awt.Frame frame = new java.awt.Frame();
        PointTypePanel aPointTypePanel;
        aPointTypePanel = new PointTypePanel();
        frame.add("Center", aPointTypePanel);
        frame.setSize(aPointTypePanel.getSize());
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            };
        });
        frame.setVisible(true);
    } catch (Throwable exception) {
        System.err.println("Exception occurred in main() of com.ibm.uvm.abt.edit.DeletedClassView");
        com.cannontech.clientutils.CTILogger.error( exception.getMessage(), exception );;
    }
}

    /**
     * Sets buttons visible or not depending if point is off LMGroup.
     * @param val - the point.
     */
    public void setButtons(Object val) {
        
        int paoId = ((PointBase) val).getPoint().getPaoID();
        
        synchronized (cache) {
            
            LiteYukonPAObject pao = cache.getAllPaosMap().get(paoId);
            
            if (pao.getPaoType().getPaoClass() == PaoClass.GROUP) {
                getCalculatedRadioButton().setEnabled(false);
                getAccumulatorRadioButton().setEnabled(false);
                getCalculatedRadioButton().setVisible(false);
                getAccumulatorRadioButton().setVisible(false);
                getDemandAccumulatorRadioButton().setEnabled(false);
                getDemandAccumulatorRadioButton().setVisible(false);
            }
        }
    }

/**
 * Insert the method's description here.
 * Creation date: (4/30/2001 3:23:30 PM)
 * @param newPointType int
 */
public void setPointType(PointType newPointType) {
    pointType = newPointType;
}
/**
 * This method was created in VisualAge.
 * @param val java.lang.Object
 */
public void setValue(Object val) {
}
/**
 * Comment
 */
public void statusRadioButton_ActionPerformed(java.awt.event.ActionEvent actionEvent) {
    
    getJRadioButtonAnalogOutput().setVisible(false);
    getJRadioButtonStatusOutput().setVisible(false);
     
    pointType = PointType.Status;
    
    return;
}
}
