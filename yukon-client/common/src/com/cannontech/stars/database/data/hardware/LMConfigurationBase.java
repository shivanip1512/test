/*
 * Created on Jun 23, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.database.data.hardware;

import java.sql.SQLException;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.inventory.HardwareConfigType;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.util.CommandExecutionException;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.db.hardware.LMConfigurationExpressCom;
import com.cannontech.stars.database.db.hardware.LMConfigurationSA205;
import com.cannontech.stars.database.db.hardware.LMConfigurationSA305;
import com.cannontech.stars.database.db.hardware.LMConfigurationSASimple;
import com.cannontech.stars.database.db.hardware.LMConfigurationVersaCom;

/**
 * @author yao
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMConfigurationBase extends DBPersistent {
	
	private com.cannontech.stars.database.db.hardware.LMConfigurationBase lmConfigBase_ = null;
	private LMConfigurationSASimple simple_ = null;
	private LMConfigurationSA205 sa205_ = null;
	private LMConfigurationSA305 sa305_ = null;
	private LMConfigurationExpressCom xcom_ = null;
	private LMConfigurationVersaCom vcom_ = null;
	
	public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getLMConfigurationBase().setDbConnection( conn );
		if (getExpressCom() != null)
			getExpressCom().setDbConnection( conn );
		else if (getVersaCom() != null)
			getVersaCom().setDbConnection( conn );
		else if (getSA205() != null)
			getSA205().setDbConnection( conn );
		else if (getSA305() != null)
			getSA305().setDbConnection( conn );
		else if (getSASimple() != null)
			getSASimple().setDbConnection( conn );
	}
	
	public void setConfigurationID(Integer configID) {
		getLMConfigurationBase().setConfigurationID( configID );
		if (getExpressCom() != null)
			getExpressCom().setConfigurationID( configID );
		else if (getVersaCom() != null)
			getVersaCom().setConfigurationID( configID );
		else if (getSA205() != null)
			getSA205().setConfigurationID( configID );
		else if (getSA305() != null)
			getSA305().setConfigurationID( configID );
		else if (getSASimple() != null)
			getSASimple().setConfigurationID( configID );
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	public void add() throws SQLException {
		getLMConfigurationBase().add();
		
		if (getExpressCom() != null) {
			getExpressCom().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getExpressCom().add();
		}
		else if (getVersaCom() != null) {
			getVersaCom().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getVersaCom().add();
		}
		else if (getSA205() != null) {
			getSA205().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getSA205().add();
		}
		else if (getSA305() != null) {
			getSA305().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
			getSA305().add();
		}
		else if (getSASimple() != null) {
			getSASimple().setConfigurationID( getLMConfigurationBase().getConfigurationID() );
		}
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	public void delete() throws SQLException {
		// Delete from tables of each hardware type
		delete( LMConfigurationExpressCom.TABLE_NAME, "ConfigurationID", getLMConfigurationBase().getConfigurationID() );
		delete( LMConfigurationVersaCom.TABLE_NAME, "ConfigurationID", getLMConfigurationBase().getConfigurationID() );
		delete( LMConfigurationSA205.TABLE_NAME, "ConfigurationID", getLMConfigurationBase().getConfigurationID() );
		delete( LMConfigurationSA305.TABLE_NAME, "ConfigurationID", getLMConfigurationBase().getConfigurationID() );
		delete( LMConfigurationSASimple.TABLE_NAME, "ConfigurationID", getLMConfigurationBase().getConfigurationID() );
		
		getLMConfigurationBase().delete();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	public void retrieve() throws SQLException {
		getLMConfigurationBase().retrieve();
		
		if (getExpressCom() != null)
			getExpressCom().retrieve();
		else if (getVersaCom() != null)
			getVersaCom().retrieve();
		else if (getSA205() != null)
			getSA205().retrieve();
		else if (getSA305() != null)
			getSA305().retrieve();
		else if (getSASimple() != null)
			getSASimple().retrieve();
	}

	/* (non-Javadoc)
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	public void update() throws SQLException {
		getLMConfigurationBase().update();
		
		if (getExpressCom() != null)
			getExpressCom().update();
		else if (getVersaCom() != null)
			getVersaCom().update();
		if (getSA205() != null)
			getSA205().update();
		else if (getSA305() != null)
			getSA305().update();
		else if (getSASimple() != null)
			getSASimple().update();
	}
	
	public static LMConfigurationBase getLMConfiguration(int configID, int hwTypeID) {
		LMConfigurationBase config = new LMConfigurationBase();
		config.getLMConfigurationBase().setConfigurationID( new Integer(configID) );
		
		try {
		    YukonListDao yukonListDao = YukonSpringHook.getBean("yukonListDao", YukonListDao.class);
		    HardwareType type = HardwareType.valueOf(yukonListDao.getYukonListEntry(hwTypeID).getYukonDefID());
		    HardwareConfigType configType = type.getHardwareConfigType();
			if (configType == HardwareConfigType.EXPRESSCOM) {
				String sql = "SELECT ConfigurationID FROM LMConfigurationExpressCom WHERE ConfigurationID = " + configID;
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() > 0) {
					LMConfigurationExpressCom xcom = new LMConfigurationExpressCom();
					xcom.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
					config.setExpressCom( xcom );
				}
			}
			else if (configType == HardwareConfigType.VERSACOM) {
				String sql = "SELECT ConfigurationID FROM LMConfigurationVersaCom WHERE ConfigurationID = " + configID;
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() > 0) {
					LMConfigurationVersaCom vcom = new LMConfigurationVersaCom();
					vcom.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
					config.setVersaCom( vcom );
				}
			}
			else if (configType == HardwareConfigType.SA205) {
				String sql = "SELECT ConfigurationID FROM LMConfigurationSA205 WHERE ConfigurationID = " + configID;
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() > 0) {
					LMConfigurationSA205 sa205 = new LMConfigurationSA205();
					sa205.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
					config.setSA205( sa205 );
				}
			}
			else if (configType == HardwareConfigType.SA305) {
				String sql = "SELECT ConfigurationID FROM LMConfigurationSA305 WHERE ConfigurationID = " + configID;
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() > 0) {
					LMConfigurationSA305 sa305 = new LMConfigurationSA305();
					sa305.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
					config.setSA305( sa305 );
				}
			}
			else if (configType == HardwareConfigType.SA_SIMPLE) {
				String sql = "SELECT ConfigurationID FROM LMConfigurationSASimple WHERE ConfigurationID = " + configID;
				SqlStatement stmt = new SqlStatement( sql, CtiUtilities.getDatabaseAlias() );
				stmt.execute();
				
				if (stmt.getRowCount() > 0) {
					LMConfigurationSASimple simple = new LMConfigurationSASimple();
					simple.setConfigurationID( config.getLMConfigurationBase().getConfigurationID() );
					config.setSASimple( simple );
				}
			}
		}
		catch (CommandExecutionException e) {
			CTILogger.error( e.getMessage(), e );
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
	
	public com.cannontech.stars.database.db.hardware.LMConfigurationBase getLMConfigurationBase() {
		if (lmConfigBase_ == null)
			lmConfigBase_ = new com.cannontech.stars.database.db.hardware.LMConfigurationBase();
		return lmConfigBase_;
	}
	
	public com.cannontech.stars.database.db.hardware.LMConfigurationSA205 getSA205() {
		return sa205_;
	}
	
	public com.cannontech.stars.database.db.hardware.LMConfigurationSA305 getSA305() {
		return sa305_;
	}
	
	public void setLMConfigurationBase(com.cannontech.stars.database.db.hardware.LMConfigurationBase lmConfigBase) {
		lmConfigBase_ = lmConfigBase;
	}
	
	public void setSA205(com.cannontech.stars.database.db.hardware.LMConfigurationSA205 sa205) {
		sa205_ = sa205;
	}
	
	public void setSA305(com.cannontech.stars.database.db.hardware.LMConfigurationSA305 sa305) {
		sa305_ = sa305;
	}

	/**
	 * @return
	 */
	public com.cannontech.stars.database.db.hardware.LMConfigurationVersaCom getVersaCom() {
		return vcom_;
	}

	/**
	 * @return
	 */
	public com.cannontech.stars.database.db.hardware.LMConfigurationExpressCom getExpressCom() {
		return xcom_;
	}

	/**
	 * @param com
	 */
	public void setVersaCom(com.cannontech.stars.database.db.hardware.LMConfigurationVersaCom vcom) {
		vcom_ = vcom;
	}

	/**
	 * @param com
	 */
	public void setExpressCom(com.cannontech.stars.database.db.hardware.LMConfigurationExpressCom xcom) {
		xcom_ = xcom;
	}

	/**
	 * @return
	 */
	public LMConfigurationSASimple getSASimple() {
		return simple_;
	}

	/**
	 * @param simple
	 */
	public void setSASimple(LMConfigurationSASimple simple) {
		simple_ = simple;
	}

}
