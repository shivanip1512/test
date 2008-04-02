/*
 * Created on Dec 27, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.analysis.data.stars;

import com.cannontech.database.data.lite.LiteAddress;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.stars.LiteInventoryBase;
import com.cannontech.database.data.lite.stars.LiteWorkOrderBase;

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
        private LiteContact ecContact;
        private LiteAddress address;
        private LiteAddress ecAddress;
        
        public LiteContact getContact() {
            return contact;
        }
        
        public void setContact(LiteContact contact) {
            this.contact = contact;
        }
        
        public LiteContact getEcContact() {
            return ecContact;
        }
        
        public void setEcContact(LiteContact ecContact) {
            this.ecContact = ecContact;
        }
        
        public LiteAddress getAddress() {
            return address;
        }
        
        public void setAddress(LiteAddress address) {
            this.address = address;
        }
        
        public LiteAddress getEcAddress() {
            return ecAddress;
        }
        
        public void setEcAddress(LiteAddress ecAddress) {
            this.ecAddress = ecAddress;
        }
        
    }

}
