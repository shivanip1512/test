package com.cannontech.dbeditor.wizard.customer;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:21:12 PM)
 * @author: 
 */
public class CustomerWizardPanel extends com.cannontech.common.wizard.WizardPanel 
{
	private CustomerTypePanel customerTypePanel;
	private CustomerBasePanel customerBasePanel;
	private AddressPanel customerAddressPanel;
	private CustomerContactPanel customerContactPanel;

	/**
	 * CustomerWizardPanel constructor comment.
	 */
	public CustomerWizardPanel() {
		super();
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
	 * Creation date: (3/27/2001 11:21:54 AM)
	 * @return com.cannontech.dbeditor.wizard.device.customer.CICustomerBasePanel
	 */
	public CustomerTypePanel getCustomerTypePanel() 
	{
		if( customerTypePanel == null )
			customerTypePanel = new CustomerTypePanel();
			
		return customerTypePanel;
	}
		
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 11:21:54 AM)
	 * @return com.cannontech.dbeditor.wizard.device.customer.CICustomerBasePanel
	 */
	public CustomerBasePanel getCustomerBasePanel() 
	{
		if( customerBasePanel == null )
			customerBasePanel = new CustomerBasePanel();
			
		return customerBasePanel;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 12:41:08 PM)
	 * @return com.cannontech.dbeditor.wizard.device.customer.CustomerAddressPanel
	 */
	public AddressPanel getCustomerAddressPanel() 
	{
		if( customerAddressPanel == null )
			customerAddressPanel = new AddressPanel();
			
		return customerAddressPanel;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/27/2001 3:47:18 PM)
	 * @return com.cannontech.dbeditor.wizard.device.customer.CustomerContactPanel
	 */
	public CustomerContactPanel getCustomerContactPanel() 
	{
		if( customerContactPanel == null )
			customerContactPanel = new CustomerContactPanel();
			
		return customerContactPanel;
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	protected String getHeaderText() {
		return "Customer Setup";
	}
	/**
	 * This method was created in VisualAge.
	 * @return java.awt.Dimension
	 */
	public java.awt.Dimension getMinimumSize() {
		return getPreferredSize();
	}
	/**
	 * This method was created in VisualAge.
	 * @return com.cannontech.common.gui.util.InputPanel
	 * @param currentInputPanel com.cannontech.common.gui.util.InputPanel
	 */
	protected com.cannontech.common.gui.util.DataInputPanel getNextInputPanel(com.cannontech.common.gui.util.DataInputPanel currentInputPanel) 
	{
		//until we add more customer types to the wizard, knock out the customer type panel
		//force customertype to Commercial/Industrial
		if( currentInputPanel == null )
		{
			getCustomerBasePanel().setCustomerType( com.cannontech.database.data.customer.CustomerTypes.CUSTOMER_CI );
            getCustomerBasePanel().setFirstFocus();
			return getCustomerBasePanel();
		}
		/*else if( currentInputPanel == getCustomerTypePanel() )
		{
			//set the type of customer first
			getCustomerBasePanel().setCustomerType( getCustomerTypePanel().getSelectedCustomerType() );
			return getCustomerBasePanel();
		}*/
		else if( currentInputPanel == getCustomerBasePanel() )
		{
			getCustomerAddressPanel().setFirstFocus();
            return getCustomerAddressPanel();
		}
		else if( currentInputPanel == getCustomerAddressPanel() )
		{
			getCustomerContactPanel().setFirstFocus();
            return getCustomerContactPanel();
		}
	
		return null;
	}
	/**
	 * This method was created in VisualAge.
	 * @return boolean
	 * @param currentPanel com.cannontech.common.gui.util.DataInputPanel
	 */
	protected boolean isLastInputPanel(com.cannontech.common.gui.util.DataInputPanel currentPanel) 
	{
		return (currentPanel == getCustomerContactPanel() );
	}
}
