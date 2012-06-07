/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.stars;

import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.stars.database.data.lite.LiteInventoryBase;
import com.cannontech.stars.database.data.lite.LiteWorkOrderBase;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class WorkOrder {
	private AdditionalInformation additionalInformation;
	private LiteWorkOrderBase liteWorkOrderBase = null;
//	private ArrayList<LiteInventoryBase> liteInventories = null;
	private LiteInventoryBase liteInventoryBase = null;
	
	public WorkOrder() {
	}

	public WorkOrder(LiteWorkOrderBase liteWorkOrderBase_) {
		this.liteWorkOrderBase = liteWorkOrderBase_;
	}

	public WorkOrder(LiteWorkOrderBase liteWorkOrderBase_, LiteInventoryBase liteInventoryBase_) {
		this.liteWorkOrderBase = liteWorkOrderBase_;
		this.liteInventoryBase = liteInventoryBase_;
	}

	public LiteWorkOrderBase getLiteWorkOrderBase() {
		return liteWorkOrderBase;
	}

	public void setLiteWorkOrderBase(LiteWorkOrderBase liteWorkOrderBase) {
		this.liteWorkOrderBase = liteWorkOrderBase;
	}

	public LiteInventoryBase getLiteInventoryBase() {
		return liteInventoryBase;
	}

	public void setLiteInventoryBase(LiteInventoryBase liteInventoryBase) {
		this.liteInventoryBase = liteInventoryBase;
	}
	
    public AdditionalInformation getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(AdditionalInformation additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public static class AdditionalInformation {
        private LiteContact contact;
        private LiteContact energyCompanyContact;
        private LiteAddress address;
        private LiteAddress energyCompanyAddress;
        
        public LiteContact getContact() {
            return contact;
        }
        
        public void setContact(LiteContact contact) {
            this.contact = contact;
        }
        
        public LiteContact getEnergyCompanyContact() {
            return energyCompanyContact;
        }
        
        public void setEnergyCompanyContact(LiteContact energyCompanyContact) {
            this.energyCompanyContact = energyCompanyContact;
        }
        
        public LiteAddress getAddress() {
            return address;
        }
        
        public void setAddress(LiteAddress address) {
            this.address = address;
        }
        
        public LiteAddress getEnergyCompanyAddress() {
            return energyCompanyAddress;
        }
        
        public void setEnergyCompanyAddress(LiteAddress energyCompanyAddress) {
            this.energyCompanyAddress = energyCompanyAddress;
        }
        
    }

}
