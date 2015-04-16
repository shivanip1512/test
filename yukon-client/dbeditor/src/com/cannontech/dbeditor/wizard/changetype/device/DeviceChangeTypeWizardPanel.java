package com.cannontech.dbeditor.wizard.changetype.device;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.editor.EditorInputValidationException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.wizard.CancelInsertException;
import com.cannontech.common.wizard.WizardPanel;
import com.cannontech.database.data.device.DeviceBase;

public class DeviceChangeTypeWizardPanel extends WizardPanel
{
	private com.cannontech.database.db.DBPersistent changeObject = null;
	private DeviceChngTypesPanel deviceTypesPanel;
	private RfnOptionPanel rfnOptionPanel;
	private MctOptionPanel mctOptionPanel;

/**
 * DeviceWizardPanel constructor comment.
 */
public DeviceChangeTypeWizardPanel(com.cannontech.database.db.DBPersistent objectToChange) 
{
	super();
	setChangeObject(objectToChange);
	
   if( getChangeObject() instanceof DeviceBase ) {
   	
		getDeviceTypesPanel().setCurrentDevice( 
				(DeviceBase)getChangeObject() );   	
   }
    	  	

}

/**
 * Insert the method's description here.
 * Creation date: (6/7/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(410, 480) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 */
public com.cannontech.database.db.DBPersistent getChangeObject() {
		
	return changeObject;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.device.DeviceNameAddressPanel
 */
protected DeviceChngTypesPanel getDeviceTypesPanel() {
	if( deviceTypesPanel == null )
		deviceTypesPanel = new DeviceChngTypesPanel();
		
	return deviceTypesPanel;
}

protected RfnOptionPanel getRfnOptionPanel() {
    if( rfnOptionPanel == null )
        rfnOptionPanel = new RfnOptionPanel();
        
    return rfnOptionPanel;
}
protected MctOptionPanel getMctOptionPanel() {
    if( mctOptionPanel == null )
        mctOptionPanel = new MctOptionPanel();
        
    return mctOptionPanel;
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
@Override
protected String getHeaderText() {
	return "Change Device Type";
}
/**
 * This method was created in VisualAge.
 * @return java.awt.Dimension
 */
@Override
public java.awt.Dimension getMinimumSize() {
	return getPreferredSize();
}
/**
 * getNextInputPanel method comment.
 */
@Override
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(
    com.cannontech.common.gui.util.DataInputPanel currentInputPanel)
{

    if (currentInputPanel == null) 
    {
    	return getDeviceTypesPanel();
    }
    else if(currentInputPanel == getDeviceTypesPanel() && getDeviceTypesPanel().displayRfnOptions)
    {
        return getRfnOptionPanel();
    }
    else if(currentInputPanel == getDeviceTypesPanel() && getDeviceTypesPanel().displayMctOptions)
    {
        return getMctOptionPanel();
    }
    else
        throw new Error(getClass() + "::" + "getNextInputPanel() - Could not determine next DataInputPanel");
}
/**
 * isLastInputPanel method comment.
 */
@Override
protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel)
{
    if( currentPanel == getDeviceTypesPanel() && !getDeviceTypesPanel().displayRfnOptions && !getDeviceTypesPanel().displayMctOptions)
    {
    	return true;
    }
    else if(currentPanel == getRfnOptionPanel() || currentPanel == getMctOptionPanel())
    {
        return true;
    }
    else
    {
        return false;
    }
}
  /**
 * This method was created in VisualAge.
 */
public void setChangeObject(com.cannontech.database.db.DBPersistent newObject) 
{
 	try 
 	{ 
	 	newObject.setDbConnection(com.cannontech.database.PoolManager.getInstance().getConnection(com.cannontech.common.util.CtiUtilities.getDatabaseAlias()));
	 	changeObject = newObject;
	 	changeObject.retrieve();
 	}
 	catch (java.sql.SQLException e) 
 	{
	 	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
 	}
 	finally
 	{
	 	if ( changeObject != null ) 
	 	{
	 		try
	 		{
	 			changeObject.getDbConnection().close();
		 		changeObject.setDbConnection(null);
	 		}
	 		catch (java.sql.SQLException e)
	 		{
		 		com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	 		}
	 	}
 	}
 } 

@Override
public Object getValue(Object o) throws CancelInsertException {
    ChangeDeviceTypeInfo info = null;
    if (o != null) {
        if (getDeviceTypesPanel().displayRfnOptions) {
            try {
                info = (ChangeDeviceTypeInfo) rfnOptionPanel.getValue(o);
                return deviceTypesPanel.getValue(info);
            } catch (EditorInputValidationException e) {
                throw new Error(e);
            }
        } else if (getDeviceTypesPanel().displayMctOptions) {
            try {
                mctOptionPanel.setPaoType((PaoType) getDeviceTypesPanel().getValue(null));
                info = (ChangeDeviceTypeInfo) mctOptionPanel.getValue(o);
                return deviceTypesPanel.getValue(info);
            } catch (EditorInputValidationException e) {
                throw new Error(e);
            }
        }
    }

    return deviceTypesPanel.getValue(o);
}

}
