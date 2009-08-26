package com.cannontech.database.data.lite;

import com.cannontech.clientutils.CTILogger;
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
    private final static long serialVersionUID = 1L;

    // a place holder for a LiteYukonPAObject used to show a dummy element
	public static final LiteYukonPAObject LITEPAOBJECT_SYSTEM = new LiteYukonPAObject
	(
		new PaoIdentifier(0, null),
		"System Device",
		CtiUtilities.STRING_NONE,
        CtiUtilities.STRING_NONE
	);

	// a place holder for a LiteYukonPAObject, mostly used in option lists
	// that allow the user not to choose a LitYukonPAObject
	public static final LiteYukonPAObject LITEPAOBJECT_NONE = new LiteYukonPAObject
	(
	        new PaoIdentifier(0, null),
		CtiUtilities.STRING_NONE,
		CtiUtilities.STRING_NONE,
        CtiUtilities.STRING_NONE
	);

	private PaoIdentifier paoIdentifier;
	private String paoName = null;
	private String paoDescription = null;
	private String disableFlag = null;
	//portID is only for devices that belong to a port
	private int portID = PAOGroups.INVALID;

	//routeIDID is only for devices that have a route, (deviceRoutes)
	private int routeID = PAOGroups.INVALID;
	//address is only for devices that have a physical address (deviceCarrierStatistics)
	private int address = PAOGroups.INVALID;

	@Deprecated
    public LiteYukonPAObject(int paoId)
    {
	    this(paoId, null);
    }

    @Deprecated
    public LiteYukonPAObject(int paoId, String paoName)
    {
        super();

        paoIdentifier = new PaoIdentifier(paoId, null);
        super.setLiteID(paoIdentifier.getPaoId());

        setLiteType(LiteTypes.YUKON_PAOBJECT);
        this.paoName = paoName;
    }

    public LiteYukonPAObject(PaoIdentifier paoIdentifier)
    {
        super();

        this.paoIdentifier = paoIdentifier;
        super.setLiteID(paoIdentifier.getPaoId());
        setLiteType(LiteTypes.YUKON_PAOBJECT);
    }

    public LiteYukonPAObject(PaoIdentifier paoIdentifier, String paoName)
    {
        this(paoIdentifier);
        this.paoName = paoName;
    }

    public LiteYukonPAObject(PaoIdentifier paoIdentifier, String name,
            String paoDescription, String disableFlag)
    {
        this(paoIdentifier, name);
        this.paoDescription = paoDescription;
        this.disableFlag = disableFlag;
    }

    public PaoIdentifier getPaoIdentifier() {
        return paoIdentifier;
    }

    @Deprecated
    public int getCategory() {
    	return paoIdentifier.getPaoType().getPaoCategory().ordinal();
    }

    @Deprecated
    public int getPaoClass() {
    	return paoIdentifier.getPaoType().getPaoClass().getPaoClassId();
    }

    public String getPaoName() {
    	return paoName;
    }

    public int getPortID() {
    	return portID;
    }

    @Deprecated
    public int getType() {
    	return paoIdentifier.getPaoType().getDeviceTypeId();
    }

    public int getYukonID() {
    	return getLiteID();
    }

    public String getDisableFlag() {
        return disableFlag;
    }

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
    		PaoType paoType = PaoType.getForId(PAOGroups.getPAOType(category, objs[2].toString()));
    		paoIdentifier = new PaoIdentifier(paoIdentifier.getPaoId(),
    		                                  paoType);
            paoName = objs[1].toString();
    		paoDescription = objs[4].toString();
            disableFlag = objs[5].toString();
    	}
    	catch (Exception e)
    	{
    		CTILogger.error(e.getMessage(), e);
    	}

        try
        {
            DeviceDirectCommSettings d = new DeviceDirectCommSettings( new Integer(getLiteID()) );
            Transaction<DeviceDirectCommSettings> t = Transaction.createTransaction(Transaction.RETRIEVE, d);

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
            Transaction<DeviceCarrierSettings> t = Transaction.createTransaction(Transaction.RETRIEVE, d);

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
            Transaction<DeviceRoutes> t = Transaction.createTransaction(Transaction.RETRIEVE, d);

            d = (DeviceRoutes)t.execute();

            if( d.getRouteID() != null )
                setRouteID( d.getRouteID().intValue() );
        }
        catch( TransactionException e )
        {
            CTILogger.error( e.getMessage(), e );
        }
    }

    public void setPaoName(String paoName) {
    	this.paoName = paoName;
    }

    public void setPortID(int newPortID) {
    	portID = newPortID;
    }

    @Deprecated
    @Override
    public void setLiteID(int paoId)
    {
        super.setLiteID(paoId);
        paoIdentifier = new PaoIdentifier(paoId,
                                          paoIdentifier == null ? null : paoIdentifier.getPaoType());
    }

    public String toString()
    {
    	return paoName;
    }

	public String getPaoDescription() {
		return paoDescription;
	}

	public void setPaoDescription(String paoDescription) {
		this.paoDescription = paoDescription;
	}

    public void setDisableFlag(String disableFlag) {
        this.disableFlag = disableFlag;
    }

	public int getAddress()
	{
		return address;
	}

	public int getRouteID()
	{
		return routeID;
	}

	public void setAddress(int address)
	{
		this.address = address;
	}

	public void setRouteID(int routeID)
	{
		this.routeID = routeID;
	}
}
