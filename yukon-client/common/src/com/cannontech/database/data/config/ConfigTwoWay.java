/*
 * Created on Dec 17, 2003
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.config;

import com.cannontech.database.db.config.MCTConfig;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class ConfigTwoWay extends com.cannontech.database.db.DBPersistent implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	private MCTConfig config = null;
	public final static Integer SERIES_200_TYPE = new Integer(2);
	public final static Integer SERIES_300_TYPE = new Integer(3);
	public final static Integer SERIES_400_TYPE = new Integer(4);
	
	public final static String MODE_NONE = "none";
	public final static String MODE_PEAKOFFPEAK = "peakoffpeak";
	public final static String MODE_MINMAX = "minmax";
	
	public final static Integer NONVALUE = new Integer(-1);
	public final static Double NOVALUE = new Double(-1);
	
	
	public final static Integer THREEWIRE = new Integer(3);
	public final static Integer TWOWIRE = new Integer(2);

/**
 * Baseline constructor comment.
 */
public ConfigTwoWay() {
	super();
}
/**
 * Baseline constructor comment.
 */
public ConfigTwoWay(Integer id)
{
	super();

	setConfigID(id);
}
/**
 * Baseline constructor comment.
 */
public ConfigTwoWay(Integer id, String name)
{
	super();

	setConfigID(id);
	setConfigName(name);
}

public ConfigTwoWay(Integer conID, String name, Integer type, String mode, Integer wire1, Double KE1, Integer wire2, Double KE2, Integer wire3, Double KE3 )
{
	super();

	getConfig().setConfigID(conID);
	getConfig().setConfigName(name);
	getConfig().setConfigType(type);
	getConfig().setConfigMode(mode);
	getConfig().setMCTWire1(wire1);
	getConfig().setKe1(KE1);
	getConfig().setMCTWire2(wire2);
	getConfig().setKe2(KE2);
	getConfig().setMCTWire3(wire3);
	getConfig().setKe3(KE3);
	
	}

public void add() throws java.sql.SQLException 
{
	if(getConfigID() == null)
		setConfigID(MCTConfig.getNextConfigID(getDbConnection()));
	getConfig().add();
	
}


public void delete() throws java.sql.SQLException 
{
	//com.cannontech.database.db.baseline.Baseline.deleteAllBaselines(getConfig().getConfigID(), getDbConnection());
	
	getConfig().delete();	
}


public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
{
	com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
	{
		new com.cannontech.message.dispatch.message.DBChangeMsg(
					getConfigID().intValue(),
					com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_CONFIG_DB,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CONFIG,
					com.cannontech.message.dispatch.message.DBChangeMsg.CAT_CONFIG,
					typeOfChange)
	};


	return msgs;
}

public MCTConfig getConfig()
{
	if (config == null)
		config = new MCTConfig();

	return config;
}


public Integer getConfigID()
{
	return getConfig().getConfigID();
}


public String getConfigName()
{
	return getConfig().getConfigName();
}

public Integer getConfigType()
{
	return getConfig().getConfigType();
}

public String getConfigMode()
{
	return getConfig().getConfigMode();
}

public Integer getMCTWire1()
{
	return getConfig().getMCTWire1();
}

public Double getKe1()
{
	return getConfig().getKe1();
}

public Integer getMCTWire2()
{
	if(getConfig().getConfigType().compareTo(SERIES_200_TYPE) == 0)
		return NONVALUE;
	return getConfig().getMCTWire2();
}

public Double getKe2()
{
	if(getConfig().getConfigType().compareTo(SERIES_200_TYPE) == 0)
		return NOVALUE;
	return getConfig().getKe2();
}

public Integer getMCTWire3()
{
	if(getConfig().getConfigType().compareTo(SERIES_200_TYPE) == 0)
		return NONVALUE;
	return getConfig().getMCTWire3();
}

public Double getKe3()
{
	if(getConfig().getConfigType().compareTo(SERIES_200_TYPE) == 0)
		return NOVALUE;
	return getConfig().getKe3();
}

public void retrieve() throws java.sql.SQLException 
{
	
	getConfig().retrieve();

}

public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);

	getConfig().setDbConnection(conn);

}


public void setConfigID( Integer newID )
{
	getConfig().setConfigID( newID );

}

public void setConfigName( String newName )
{
	getConfig().setConfigName( newName );	
}

public void setConfigType( Integer type)
{
	getConfig().setConfigType(type);
}

public void setConfigMode(String mode)
{
	getConfig().setConfigMode(mode);
}

public void setMCTWire1(Integer wire1)
{
	getConfig().setMCTWire1(wire1);
}

public void setKe1(Double ke1)
{
	getConfig().setKe1(ke1);
}

public void setMCTWire2(Integer wire2)
{
	getConfig().setMCTWire2(wire2);
}

public void setKe2(Double ke2)
{
	getConfig().setKe2(ke2);
}

public void setMCTWire3(Integer wire3)
{
	getConfig().setMCTWire3(wire3);
}

public void setKe3(Double ke3)
{
	getConfig().setKe3(ke3);
}

public String toString()
{
	return getConfigName();
}


public void update() throws java.sql.SQLException
{
	getConfig().update();
	
}

public final static boolean inUseByMCT(Integer configID) throws java.sql.SQLException 
{	
	return inUseByMCT(configID, com.cannontech.common.util.CtiUtilities.getDatabaseAlias());
}
/**
 * This method was created in VisualAge.
 * @param pointID java.lang.Integer
 */
public final static boolean inUseByMCT(Integer conID, String databaseAlias) throws java.sql.SQLException 
{
	com.cannontech.database.SqlStatement stmt =
		new com.cannontech.database.SqlStatement("SELECT MctID FROM MCTConfigMapping WHERE ConfigID=" + conID,
													databaseAlias );

	try
	{
		stmt.execute();
		return (stmt.getRowCount() > 0 );
	}
	catch( Exception e )
	{
		return false;
	}
}

}