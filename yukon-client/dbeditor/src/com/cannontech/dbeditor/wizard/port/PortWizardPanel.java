package com.cannontech.dbeditor.wizard.port;

/**
 * This type was created in VisualAge.
 */
import com.cannontech.common.gui.util.DataInputPanel;

public class PortWizardPanel extends com.cannontech.common.wizard.WizardPanel {
	private PortTypeQuestionPanelA portTypeQuestionPanelA;
	private LocalPortTypeQuestionPanel localPortTypeQuestionPanel;
	private TcpTypeQuestionPanel tcpTypeQuestionPanel;
	private SimpleLocalPortSettingsPanel simpleLocalPortSettingsPanel;
	private SimpleTerminalServerSettingsPanel simpleTerminalServerSettingsPanel;
	private SimpleDialupModemPanel simpleDialupModemPanel;
	private PooledPortListPanel pooledPortListPanel;
	
/**
 * PortWizardPanel constructor comment.
 */
public PortWizardPanel() {
	super();
}
/**
 * Insert the method's description here.
 * Creation date: (5/4/2001 11:11:28 AM)
 * @return java.awt.Dimension
 */
public java.awt.Dimension getActualSize() 
{
	setPreferredSize( new java.awt.Dimension(430, 480) );

	return getPreferredSize();
}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
protected String getHeaderText() {
	return "Comm Channel Setup";
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.LocalPortTypeQuestionPanel
 */
protected LocalPortTypeQuestionPanel getLocalPortTypeQuestionPanel() {
	if( localPortTypeQuestionPanel == null )
		localPortTypeQuestionPanel = new LocalPortTypeQuestionPanel();
		
	return localPortTypeQuestionPanel;
}
/**
 * getNextInputPanel method comment.
 */
protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) {

	if( currentInputPanel == null )
	{
        getPortTypeQuestionPanelA().setFirstFocus();
        return getPortTypeQuestionPanelA();
	}
	else if( currentInputPanel == getPortTypeQuestionPanelA() )
	{
		if (((PortTypeQuestionPanelA) currentInputPanel).isLocalSerialPort() )
		{
            getLocalPortTypeQuestionPanel().setFirstFocus();
            return getLocalPortTypeQuestionPanel();
		}
		else if (((PortTypeQuestionPanelA) currentInputPanel).isTCPTerminalServerPort() ||
		         ((PortTypeQuestionPanelA) currentInputPanel).isUDPTerminalServerPort()) 
		{
		    if (((PortTypeQuestionPanelA) currentInputPanel).isTCPTerminalServerPort())
		    {
		        getSimpleTerminalServerSettingsPanel().setAsTCP();
		    }
		    else
		    {
		        getSimpleTerminalServerSettingsPanel().setAsUDP();
		    }
		    
		    getSimpleTerminalServerSettingsPanel().setFirstFocus();
            return getSimpleTerminalServerSettingsPanel();
        }
		else
		{
		    getTcpTypeQuestionPanel().setFirstFocus();
		    return getTcpTypeQuestionPanel();
		}
	}
	else if( currentInputPanel == getLocalPortTypeQuestionPanel() )
	{
		//set some items to show or not
		getSimpleLocalPortSettingsPanel().setDisplayItems(getLocalPortTypeQuestionPanel().isDialoutPool());
        
        getSimpleLocalPortSettingsPanel().setFirstFocus();

		return getSimpleLocalPortSettingsPanel();
	}
	else if( currentInputPanel == getSimpleLocalPortSettingsPanel() || 
		     currentInputPanel == getSimpleTerminalServerSettingsPanel() )
	{
		if( getPortTypeQuestionPanelA().isLocalSerialPort() )
		{
	
			if( getLocalPortTypeQuestionPanel().isDialup()
				 || getLocalPortTypeQuestionPanel().isDialBack() )
			{
				getSimpleDialupModemPanel().setFirstFocus();
                return getSimpleDialupModemPanel();
			}
			else if( getLocalPortTypeQuestionPanel().isDialoutPool() )
			{
				 return getPooledPortListPanel();
			}
		}
	}


	throw new Error("PortWizardPanel::getNextInputPanel - Cannot determine next DataInputPanel, currentInputPanel is " + currentInputPanel );
	
	
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.PortTypeQuestionPanelA
 */
protected PortTypeQuestionPanelA getPortTypeQuestionPanelA() {
	if( portTypeQuestionPanelA == null )
		portTypeQuestionPanelA = new PortTypeQuestionPanelA();
		
	return portTypeQuestionPanelA;
}
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.SimpleDialupModemPanel
 */
protected SimpleDialupModemPanel getSimpleDialupModemPanel() {
	if( simpleDialupModemPanel == null )
		simpleDialupModemPanel = new SimpleDialupModemPanel();
		
	return simpleDialupModemPanel;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.SimpleDialupModemPanel
 */
protected PooledPortListPanel getPooledPortListPanel() {
	if( pooledPortListPanel == null )
		pooledPortListPanel = new PooledPortListPanel();
		
	return pooledPortListPanel;
}

/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.SimpleLocalPortSettingsPanel
 */
protected SimpleLocalPortSettingsPanel getSimpleLocalPortSettingsPanel() {
	if( simpleLocalPortSettingsPanel == null )
		simpleLocalPortSettingsPanel = new SimpleLocalPortSettingsPanel();
		
	return simpleLocalPortSettingsPanel;
} 
/**
 * This method was created in VisualAge.
 * @return com.cannontech.dbeditor.wizard.port.SimpleTerminalServerSettingsPanel
 */
protected SimpleTerminalServerSettingsPanel getSimpleTerminalServerSettingsPanel() {
	if( simpleTerminalServerSettingsPanel == null )
		simpleTerminalServerSettingsPanel = new SimpleTerminalServerSettingsPanel();
	
	return simpleTerminalServerSettingsPanel;
}

protected TcpTypeQuestionPanel getTcpTypeQuestionPanel() {
    if( tcpTypeQuestionPanel == null )
        tcpTypeQuestionPanel = new TcpTypeQuestionPanel();
    
    return tcpTypeQuestionPanel;
}

/**
 * This method was created in VisualAge.
 * @return boolean
 * @param panel com.cannontech.common.gui.util.DataInputPanel
 */
protected boolean isLastInputPanel(DataInputPanel panel) {

	return( (panel == getSimpleDialupModemPanel())
				|| (panel == getSimpleLocalPortSettingsPanel() &&
					 getPortTypeQuestionPanelA().isLocalSerialPort() &&
					 !getLocalPortTypeQuestionPanel().isDialup() &&
					 !getLocalPortTypeQuestionPanel().isDialoutPool() &&
					 !getLocalPortTypeQuestionPanel().isDialBack() )
					 
				|| (panel == getSimpleTerminalServerSettingsPanel() &&
					 !getPortTypeQuestionPanelA().isLocalSerialPort())
				|| (panel == getPooledPortListPanel())
				|| (panel == getTcpTypeQuestionPanel())
			);	
}


}
