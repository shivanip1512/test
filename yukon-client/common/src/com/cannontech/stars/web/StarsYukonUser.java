package com.cannontech.stars.web;

import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class StarsYukonUser {

	private LiteYukonUser yukonUser = null;
	private int energyCompanyID = -1;
	private LiteCustomer customer = null;
	
	private StarsYukonUser(LiteYukonUser user) {
		yukonUser = user;
	}
	
	public static StarsYukonUser newInstance(LiteYukonUser user) throws InstantiationException {
		StarsYukonUser starsUser = new StarsYukonUser(user);
		starsUser.init();
		
		return starsUser;
	}
	
	public int getUserID() {
		return yukonUser.getUserID();
	}
	
	public LiteYukonUser getYukonUser() {
		return yukonUser;
	}
	
	public LiteCustomer getCustomer() {
		return customer;
	}
	
	public int getEnergyCompanyID() {
		return energyCompanyID;
	}
	
	private void init() throws InstantiationException {
	    RolePropertyDao rolePropertyDao = YukonSpringHook.getBean(RolePropertyDao.class);
	    if (rolePropertyDao.checkRole(YukonRole.OPERATOR_ADMINISTRATOR, this.getYukonUser())) {
			EnergyCompany energyCompany = YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(getYukonUser());
			if (energyCompany == null) {
                throw new InstantiationException( "Cannot find the energy company for user id = " + getYukonUser().getUserID() );
            }
			
			energyCompanyID = energyCompany.getId();
		}
		else if (rolePropertyDao.checkRole(YukonRole.RESIDENTIAL_CUSTOMER, this.getYukonUser())) {
			LiteContact liteContact = YukonSpringHook.getBean(YukonUserDao.class).getLiteContact( getUserID() );
			if (liteContact == null) {
                throw new InstantiationException( "Cannot find contact information for user id = " + getYukonUser().getUserID() );
            }
			customer = YukonSpringHook.getBean(ContactDao.class).getCustomer( liteContact.getContactID() );
			if (customer == null) {
                throw new InstantiationException( "Cannot find customer information for user id = " + getYukonUser().getUserID() );
            }
			
			energyCompanyID = customer.getEnergyCompanyID();
		}
	}

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((customer == null) ? 0 : customer.hashCode());
        result = prime * result + energyCompanyID;
        result = prime * result + ((yukonUser == null) ? 0
                : yukonUser.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StarsYukonUser other = (StarsYukonUser) obj;
        if (customer == null) {
            if (other.customer != null) {
                return false;
            }
        } else if (!customer.equals(other.customer)) {
            return false;
        }
        if (energyCompanyID != other.energyCompanyID) {
            return false;
        }
        if (yukonUser == null) {
            if (other.yukonUser != null) {
                return false;
            }
        } else if (!yukonUser.equals(other.yukonUser)) {
            return false;
        }
        return true;
    }
    
}
