/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.stars.hardware;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.stars.hardware.LMConfigurationSA205;
import com.cannontech.database.db.stars.hardware.LMConfigurationSA305;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationBase extends DBPersistent {
	
	private com.cannontech.database.db.stars.hardware.LMConfigurationBase lmConfigBase_ = null;
	private com.cannontech.database.db.stars.hardware.LMConfigurationSA205 sa205_ = null;
	private com.cannontech.database.db.stars.hardware.LMConfigurationSA305 sa305_ = null;

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getLMConfigurationBase().add();
		
		if (getSA205() != null) {
			getSA205().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getSA205().add();
		}
		else if (getSA305() != null) {
			getSA305().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getSA305().add();
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		if (getSA205() != null)
			getSA205().delete();
		else if (getSA305() != null)
			getSA305().delete();
		
		getLMConfigurationBase().delete();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMConfigurationBase().retrieve();
		
		if (getSA205() != null)
			getSA205().retrieve();
		else if (getSA305() != null)
			getSA305().retrieve();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMConfigurationBase().update();
		
		if (getSA205() != null)
			getSA205().update();
		else if (getSA305() != null)
			getSA305().update();
	}
	
	public static LMConfigurationBase getLMConfiguration(int configID, int hwTypeDefID) {
		LMConfigurationBase config = new LMConfigurationBase();
		config.getLMConfigurationBase().setConfigurationID( new Integer(configID) );
		
		if (hwTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205) {
			LMConfigurationSA205 sa205 = new LMConfigurationSA205();
			sa205.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
			config.setSA205( sa205 );
		}
		else if (hwTypeDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_SA205) {
			LMConfigurationSA305 sa305 = new LMConfigurationSA305();
			sa305.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
			config.setSA305( sa305 );
		}
		
		try {
			config = (LMConfigurationBase) Transaction.createTransaction( Transaction.RETRIEVE, config ).execute();
		}
		catch (TransactionException e) {
			CTILogger.error( e.getMessage(), e );
			return null;
		}
		
		return config;
	}
	
	public com.cannontech.database.db.stars.hardware.LMConfigurationBase getLMConfigurationBase() {
		if (lmConfigBase_ == null)
			lmConfigBase_ = new com.cannontech.database.db.stars.hardware.LMConfigurationBase();
		return lmConfigBase_;
	}
	
	public com.cannontech.database.db.stars.hardware.LMConfigurationSA205 getSA205() {
		return sa205_;
	}
	
	public com.cannontech.database.db.stars.hardware.LMConfigurationSA305 getSA305() {
		return sa305_;
	}
	
	public void setLMConfigurationBase(com.cannontech.database.db.stars.hardware.LMConfigurationBase lmConfigBase) {
		lmConfigBase_ = lmConfigBase;
	}
	
	public void setSA205(com.cannontech.database.db.stars.hardware.LMConfigurationSA205 sa205) {
		sa205_ = sa205;
	}
	
	public void setSA305(com.cannontech.database.db.stars.hardware.LMConfigurationSA305 sa305) {
		sa305_ = sa305;
	}

}
