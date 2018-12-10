package com.cannontech.dbeditor.wizard.point;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;

public class PointWizardPanel extends com.cannontech.common.wizard.WizardPanel
{
	// ALL THESE VALUES BELOW MUST NOT BE INITIALIZED TO NULL FOR ANY WIZARDPANEL,
	// BECAUSE OF THE CALL TO THE SUPER CONSTRUCTOR
	private PointAccumulatorSettingsPanel pointAccumulatorSettingsPanel;
	private PointAnalogSettingsPanel pointAnalogSettingsPanel;
	private PointPhysicalSettingsPanel pointPhysicalSettingsPanel;
	private PointSettingsPanel pointSettingsPanel;
	private PointIDSettingsPanel pointIDSettingsPanel;
	private PointTypePanel pointTypePanel;
	private PointStatusSettingsPanel pointStatusSettingsPanel;
	private PointStatusPhysicalSettingsPanel pointStatusPhysicalSettingsPanel;
	private PointCalcBaseSettingsPanel pointCalcBaseSettingsPanel;

	private Integer initialPAOId;

/**
 * PointWizardPanel constructor comment.
 */
public PointWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/5/2001 5:18:07 PM)
 * @param selectedDevice com.cannontech.database.db.DBPersistent
 */
public PointWizardPanel(Integer id)
{
	this();
	initialPAOId = id;
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 480) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Point Setup";
}
/**
 * Insert the method's description here.
 * Creation date: (3/4/2002 2:04:39 PM)
 * @return java.lang.Integer
 */
public java.lang.Integer getInitialPAOId() {
	return initialPAOId;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
	com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{
	if (currentInputPanel == null)
	{

        getPointTypePanel().setFirstFocus();
        return getPointTypePanel();
	}
	else if (currentInputPanel == getPointTypePanel())
	{
		getPointSettingsPanel().setValueCore( null, getInitialPAOId() );
        getPointSettingsPanel().setFirstFocus();
		return getPointSettingsPanel();
	}
	else if ((currentInputPanel == getPointSettingsPanel()) || (currentInputPanel == getPointIDSettingsPanel()))
	{
		//Depending on the type selected in PointTypePanel
		switch (getPointTypePanel().getPointType())
		{
			case Analog :
                getPointAnalogSettingsPanel().setFirstFocus();
				return getPointAnalogSettingsPanel();

			case Status :
			case CalcStatus :
				getPointStatusSettingsPanel().setValue(null);
                getPointStatusSettingsPanel().setFirstFocus();
				return getPointStatusSettingsPanel();

			case PulseAccumulator :
			case DemandAccumulator :
                getPointAccumulatorSettingsPanel().setFirstFocus();
				return getPointAccumulatorSettingsPanel();

			case CalcAnalog :
				getPointCalcBaseSettingsPanel().setValue(null);
                getPointCalcBaseSettingsPanel().setFirstFocus();
				return getPointCalcBaseSettingsPanel();
				
			default :
				throw new Error(
					getClass()
						+ "::"
						+ "getNextInputPanel() - Unrecognized point type:  "
						+ getPointTypePanel().getPointType().getPointTypeString());
		}
	}
	else if (currentInputPanel == getPointAnalogSettingsPanel())
	{
		getPointPhysicalSettingsPanel().reinitialize(
			getPointSettingsPanel().getPointDeviceID(),
			getPointTypePanel().getPointType());
        getPointPhysicalSettingsPanel().setFirstFocus();
		return getPointPhysicalSettingsPanel();
	}
	else if (currentInputPanel == getPointAccumulatorSettingsPanel())
	{
		//set the correct accumlator point type
		getPointTypePanel().setPointType(getPointAccumulatorSettingsPanel().getAccumulatorPointType());

		getPointPhysicalSettingsPanel().reinitialize(
			getPointSettingsPanel().getPointDeviceID(),
			getPointTypePanel().getPointType() );
		getPointPhysicalSettingsPanel().setFirstFocus();
		return getPointPhysicalSettingsPanel();
	}
	else if (currentInputPanel == getPointStatusSettingsPanel())
	{
		
		if(getPointTypePanel().getPointType() == PointType.CalcStatus)
		{
			getPointCalcBaseSettingsPanel().setValue(null);
            getPointCalcBaseSettingsPanel().setFirstFocus();
			return getPointCalcBaseSettingsPanel();
		}
				
		getPointStatusPhysicalSettingsPanel().reinitialize(
			getPointSettingsPanel().getPointDeviceID(),
			getPointTypePanel().getPointType());
        getPointStatusPhysicalSettingsPanel().setFirstFocus();
		return getPointStatusPhysicalSettingsPanel();
	}
	else
		throw new Error(getClass() + "::" + "getNextInputPanel() - Unable to determine the next DataInputPanel");

}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointAccumulatorSettingsPanel
 */
protected PointAccumulatorSettingsPanel getPointAccumulatorSettingsPanel() {
	if( pointAccumulatorSettingsPanel == null )
		pointAccumulatorSettingsPanel = new PointAccumulatorSettingsPanel();
		
	return pointAccumulatorSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointAnalogSettingsPanel
 */
protected PointAnalogSettingsPanel getPointAnalogSettingsPanel() {
	if( pointAnalogSettingsPanel == null )
		pointAnalogSettingsPanel = new PointAnalogSettingsPanel();
		
	return pointAnalogSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointCalcBaseSettingsPanel
 */
protected PointCalcBaseSettingsPanel getPointCalcBaseSettingsPanel() {
	if( pointCalcBaseSettingsPanel == null )
		pointCalcBaseSettingsPanel = new PointCalcBaseSettingsPanel();
		
	return pointCalcBaseSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointSettingsPanel
 */
protected PointIDSettingsPanel getPointIDSettingsPanel() {
	if( pointIDSettingsPanel == null )
		pointIDSettingsPanel = new PointIDSettingsPanel();
		
	return pointIDSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointPhysicalSettingsPanel
 */
protected PointPhysicalSettingsPanel getPointPhysicalSettingsPanel() {
	if( pointPhysicalSettingsPanel == null )
		pointPhysicalSettingsPanel = new PointPhysicalSettingsPanel();
		
	return pointPhysicalSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointSettingsPanel
 */
protected PointSettingsPanel getPointSettingsPanel() {
	if( pointSettingsPanel == null )
		pointSettingsPanel = new PointSettingsPanel();
		
	return pointSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointStatusPhysicalSettingsPanel
 */
protected PointStatusPhysicalSettingsPanel getPointStatusPhysicalSettingsPanel() {
	
	if( pointStatusPhysicalSettingsPanel == null )
		pointStatusPhysicalSettingsPanel = new PointStatusPhysicalSettingsPanel();
		
	return pointStatusPhysicalSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointStatusSettingsPanel
 */
protected PointStatusSettingsPanel getPointStatusSettingsPanel() {
	
	if( pointStatusSettingsPanel == null )
		pointStatusSettingsPanel = new PointStatusSettingsPanel();
		
	return pointStatusSettingsPanel;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointTypePanel
 */
protected PointTypePanel getPointTypePanel() {

	if( pointTypePanel == null )
		pointTypePanel = new PointTypePanel(this);
	
	return pointTypePanel;
}
/**
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	return (	currentPanel == getPointStatusPhysicalSettingsPanel() ||
						currentPanel == getPointPhysicalSettingsPanel() ||
						currentPanel == getPointCalcBaseSettingsPanel() );
}

}
