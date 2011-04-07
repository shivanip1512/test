package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.SqlStatement;
import com.cannontech.database.Transaction;
import com.cannontech.database.TransactionException;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.device.DeviceCarrierSettings;
import com.cannontech.database.db.device.DeviceDirectCommSettings;
import com.cannontech.database.db.device.DeviceRoutes;

/*
 */
public class LiteYukonPAObject extends LiteBase implements YukonPao
{
	// a place holder for a LiteYukonPAObject used to show a dummy element
	public static final LiteYukonPAObject LITEPAOBJECT_SYSTEM = new LiteYukonPAObject
	(
		0,
		"System Device",
		PaoType.SYSTEM,
		CtiUtilities.STRING_NONE,
        CtiUtilities.STRING_NONE
	);
	
	// a place holder for a LiteYukonPAObject, mostly used in option lists
	// that allow the user not to choose a LitYukonPAObject
	public static final LiteYukonPAObject LITEPAOBJECT_NONE = new LiteYukonPAObject
	(
		0,
		CtiUtilities.STRING_NONE,
		PaoType.SYSTEM,
		CtiUtilities.STRING_NONE,
        CtiUtilities.STRING_NONE
	);

	//private int yukonID = com.cannontech.database.data.pao.PAOGroups.INVALID;
	private int category = PAOGroups.INVALID;
	private String paoName = null;
	private PaoType paoType = null;
	private int paoClass = PAOGroups.INVALID;
	private String paoDescription = null;
	private String disableFlag = null;
	//portID is only for devices that belong to a port
	private int portID = PAOGroups.INVALID;

	//routeIDID is only for devices that have a route, (deviceRoutes)
	private int routeID = PAOGroups.INVALID;
	//address is only for devices that have a physical address (deviceCarrierStatistics)
	private int address = PAOGroups.INVALID;
	
/**
 * Use this constructor ONLY when the full LiteYukonPaobject will be loaded before using the object.
 *  For example: Constructing a LiteYukonPaobject to call dbPersistent.retrieve() for is okay.
 * The paoType MUST be set (either explicitly or by a retrieve()) for correct usage of 
 * 	this object (required by implements YukonPao). 
 * @param paoID
 */
public LiteYukonPAObject(int paoId) 
{
	super();

	setLiteID( paoId );
	setLiteType( LiteTypes.YUKON_PAOBJECT );
}

/**
 * @param paoId
 * @param paoName
 * @param paoType - paoCategory and paoClass will also be loaded from paoType
 * @param paoDescription
 * @param disableFlag
 */
public LiteYukonPAObject(int paoId, String paoName, PaoType paoType, String paoDescription, String disableFlag) 
{
	this(paoId);

	setPaoName(paoName);
	setCategory(paoType.getPaoCategory().getCategoryId());
	setPaoClass(paoType.getPaoClass().getPaoClassId());
	setPaoType(paoType);
	setPaoDescription(paoDescription);
    setDisableFlag(disableFlag);
}

public LiteYukonPAObject(int paoId, String paoName, PaoCategory paoCategory, PaoClass paoClass, PaoType paoType, 
		String paoDescription, String disableFlag) 
{
	this(paoId);

	setPaoName(paoName);
	setCategory(paoCategory.getCategoryId());
	setPaoClass(paoClass.getPaoClassId());
	setPaoType(paoType);
	setPaoDescription(paoDescription);
    setDisableFlag(disableFlag);
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getCategory() {
	return category;
}

/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getPaoClass() {
	return paoClass;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 11:31:46 AM)
 * @return java.lang.String
 */
public java.lang.String getPaoName() {
	return paoName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 10:22:08 AM)
 * @return int
 */
public int getPortID() {
	return portID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @return int
 */
public int getType() {
        return paoType.getDeviceTypeId();
}
/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 12:07:44 PM)
 * @return int
 */
public int getYukonID() {
	return getLiteID();
}

public String getDisableFlag() {
    return disableFlag;
}

/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:21:01 PM)
 * @param dbalias java.lang.String
 */
public void retrieve(String dbalias)
{
	try
	{
		SqlStatement stat = new SqlStatement(
				"select Category, PAOName, Type, PAOClass, Description, DisableFlag "
					+ "from YukonPAObject where PAObjectID = "
					+ getLiteID(), dbalias);

		stat.execute();

		if (stat.getRowCount() <= 0)
			throw new IllegalStateException(
				"Unable to find the PAObject with PAOid = " + getLiteID());

		Object[] objs = stat.getRow(0);
		String category = objs[0].toString();
		setCategory(PAOGroups.getCategory(category));
		setPaoName(objs[1].toString());
		setType(PAOGroups.getPAOType(category, objs[2].toString()));
		setPaoClass(PAOGroups.getPAOClass(category, objs[3].toString()));
		setPaoDescription(objs[4].toString());
        setDisableFlag(objs[5].toString());
	}
	catch (Exception e)
	{
		CTILogger.error(e.getMessage(), e);
	}
	
    try
    {
        DeviceDirectCommSettings d = new DeviceDirectCommSettings( new Integer(getLiteID()) );
        Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, d);

        d = (DeviceDirectCommSettings)t.execute();

        if( d.getPortID() != null )
            setPortID( d.getPortID().intValue() );
    }
    catch( TransactionException e )
    {
        CTILogger.error( e.getMessage(), e );
    }
    try
    {
        DeviceCarrierSettings d = new DeviceCarrierSettings( new Integer(getLiteID()) );
        Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, d);

        d = (DeviceCarrierSettings)t.execute();

        if( d.getAddress() != null )
            setAddress( d.getAddress().intValue() );
    }
    catch( TransactionException e )
    {
        CTILogger.error( e.getMessage(), e );
    }
    try
    {
        DeviceRoutes d = new DeviceRoutes();
        d.setDeviceID(new Integer(getLiteID()) );
        Transaction t = Transaction.createTransaction(Transaction.RETRIEVE, d);

        d = (DeviceRoutes)t.execute();

        if( d.getRouteID() != null )
            setRouteID( d.getRouteID().intValue() );
    }
    catch( TransactionException e )
    {
        CTILogger.error( e.getMessage(), e );
    }
}	

/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newCategory int
 */
public void setCategory(int newCategory) {
	category = newCategory;
}

/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newPaoClass int
 */
public void setPaoClass(int newPaoClass) {
	paoClass = newPaoClass;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 11:31:46 AM)
 * @param newPaoName java.lang.String
 */
public void setPaoName(java.lang.String newPaoName) {
	paoName = newPaoName;
}
/**
 * Insert the method's description here.
 * Creation date: (10/10/2001 10:22:08 AM)
 * @param newPortID int
 */
public void setPortID(int newPortID) {
	portID = newPortID;
}
/**
 * Insert the method's description here.
 * Creation date: (9/28/2001 4:57:42 PM)
 * @param newType int
 */
public void setType(int newType) {
    paoType = newType == PAOGroups.INVALID ? null : PaoType.getForId(newType);
}

public void setPaoType(PaoType paoType) {
	this.paoType = paoType;
}

/**
 * Insert the method's description here.
 * Creation date: (9/27/2001 12:07:44 PM)
 * @param newYukonID int
 */
public void setYukonID(int newYukonID) 
{
	setLiteID( newYukonID );
}
/**
 * This method was created by Cannon Technologies Inc.
 */
public String toString() 
{
	return getPaoName();
}
	/**
	 * Returns the paoDescription.
	 * @return String
	 */
	public String getPaoDescription() {
		return paoDescription;
	}

	/**
	 * Sets the paoDescription.
	 * @param paoDescription The paoDescription to set
	 */
	public void setPaoDescription(String paoDescription) {
		this.paoDescription = paoDescription;
	}
    
    public void setDisableFlag( String flag_ ) {
        this.disableFlag = flag_;
    }

	/**
	 * @return
	 */
	public int getAddress()
	{
		return address;
	}

	/**
	 * @return
	 */
	public int getRouteID()
	{
		return routeID;
	}

	/**
	 * @param string
	 */
	public void setAddress(int i)
	{
		address = i;
	}

	/**
	 * @param i
	 */
	public void setRouteID(int i)
	{
		routeID = i;
	}

    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(getLiteID(), paoType);
    }
}
