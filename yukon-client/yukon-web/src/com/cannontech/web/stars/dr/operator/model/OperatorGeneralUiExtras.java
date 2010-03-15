package com.cannontech.web.stars.dr.operator.model;

public class OperatorGeneralUiExtras {

	private boolean hasOddsForControlRole;
    private boolean notifyOddsForControl;
    private String notes;
    private String accountSiteNotes;
    private boolean usePrimaryAddressForBilling;
    
    public boolean isHasOddsForControlRole() {
		return hasOddsForControlRole;
	}
    public void setHasOddsForControlRole(boolean hasOddsForControlRole) {
		this.hasOddsForControlRole = hasOddsForControlRole;
	}
    
	public boolean isNotifyOddsForControl() {
		return notifyOddsForControl;
	}
	public void setNotifyOddsForControl(boolean notifyOddsForControl) {
		this.notifyOddsForControl = notifyOddsForControl;
	}
    
    public String getNotes() {
		return notes;
	}
    public void setNotes(String notes) {
		this.notes = notes;
	}
	
	public String getAccountSiteNotes() {
		return accountSiteNotes;
	}
    
    public void setAccountSiteNotes(String accountSiteNotes) {
		this.accountSiteNotes = accountSiteNotes;
	}
    
    public boolean isUsePrimaryAddressForBilling() {
		return usePrimaryAddressForBilling;
	}
    
    public void setUsePrimaryAddressForBilling(boolean usePrimaryAddressForBilling) {
		this.usePrimaryAddressForBilling = usePrimaryAddressForBilling;
	}
}
