package com.cannontech.dbeditor.wizard.point.lm;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.dbeditor.wizard.point.PointAccumulatorSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointAnalogSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointPhysicalSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointStatusPhysicalSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointStatusSettingsPanel;
import com.cannontech.dbeditor.wizard.point.PointWizardPanel;

public class LMPointWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private PointAccumulatorSettingsPanel pointAccumulatorSettingsPanel;
	private PointAnalogSettingsPanel pointAnalogSettingsPanel;
	private PointPhysicalSettingsPanel pointPhysicalSettingsPanel;
	private PointSettingsPanel pointSettingsPanel;
	private LMPointTypePanel lmPointTypePanel;
	private PointStatusSettingsPanel pointStatusSettingsPanel;
	private PointStatusPhysicalSettingsPanel pointStatusPhysicalSettingsPanel;
	private Integer deviceID;
/**
 * PointWizardPanel constructor comment.
 */
public LMPointWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (6/28/2001 9:33:09 AM)
 * @param selectedDevice com.cannontech.database.db.DBPersistent
 */
public LMPointWizardPanel(Integer id)
{
	super();
	deviceID = id;
	
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
 * Insert the method's description here.
 * Creation date: (6/6/2001 8:20:56 AM)
 * @return java.lang.Object
 */
public Integer getDeviceID()
{
	return deviceID;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Point Setup";
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.point.PointTypePanel
 */
protected LMPointTypePanel getLMPointTypePanel() {

	if( lmPointTypePanel == null )
		lmPointTypePanel = new LMPointTypePanel();
	
	return lmPointTypePanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
		return getLMPointTypePanel();
	}
	else if( currentInputPanel == getLMPointTypePanel() )
	{
		getPointSettingsPanel().setValueLM( null, getDeviceID() );
			
		return getPointSettingsPanel();
	}
	else if( currentInputPanel == getPointSettingsPanel() )
	{
		//Depending on the type selected in PointTypePanel
		int type = getLMPointTypePanel().getSelectedType();

		switch( type )
		{
			case PointTypes.ANALOG_POINT:
				return getPointAnalogSettingsPanel();

			case PointTypes.STATUS_POINT:
				getPointStatusSettingsPanel().setValue(null);
				return getPointStatusSettingsPanel();

			default:
				throw new Error( getClass() + "::"+ "getNextInputPanel() - Unrecognized point type:  " + type );
		}
	}
	else if( currentInputPanel == getPointAnalogSettingsPanel() )
	{
		getPointPhysicalSettingsPanel().reinitialize(getPointSettingsPanel().getPointDeviceID(), getLMPointTypePanel().getSelectedType());
		return getPointPhysicalSettingsPanel();
	}
	else if( currentInputPanel == getPointStatusSettingsPanel() )
	{
		getPointStatusPhysicalSettingsPanel().reinitialize(getPointSettingsPanel().getPointDeviceID(), getLMPointTypePanel().getSelectedType());
		return getPointStatusPhysicalSettingsPanel();
	}
	else
		throw new Error( getClass() + "::" + "getNextInputPanel() - Unable to determine the next DataInputPanel");
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
 * isLastInputPanel method comment.
 */
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) {
	return (
				currentPanel == getPointStatusPhysicalSettingsPanel() ||
				currentPanel == getPointPhysicalSettingsPanel()
			);
}

/**
 * This method was created in VisualAge.
 * @param newValue com.cannontech.dbeditor.wizard.point.PointStatusPhysicalSettingsPanel
 */
public void setPointStatusPhysicalSettingsPanel(PointStatusPhysicalSettingsPanel newValue) {
	this.pointStatusPhysicalSettingsPanel = newValue;
}
}
