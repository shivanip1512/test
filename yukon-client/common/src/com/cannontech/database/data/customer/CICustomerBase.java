package com.cannontech.database.data.customer;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
import com.cannontech.database.db.company.EnergyCompany;
import com.cannontech.database.db.customer.Address;
import com.cannontech.database.db.customer.CustomerBaseLine;
import com.cannontech.database.db.customer.CustomerBaseLinePoint;

public class CICustomerBase extends Customer implements com.cannontech.common.editor.EditorPanel, IAddress
{
	private com.cannontech.database.db.customer.CICustomerBase ciCustomerBase = null;

	private Address customerAddress = null;
	private CustomerBaseLine customerBaseLine = null;
	private CustomerBaseLinePoint customerBaseLinePoint = null;

	//currently, 1 customer may only belong to 1 EnergyCompany. This is null
	// if the customer is not owned by an EnergyCompany
	private EnergyCompany energyCompany = null;


	//----------------------------------------------------------------------------------
	//--------------- TODO:  NO LONGER, MUST ADD A MAPPING TABLE FOR METERS-------------
	//----------------------------------------------------------------------------------
	//contains com.cannontech.database.db.pao.PAOowner
	private java.util.Vector meterVector = null;
	
	
	/**
	 * CICustomerBase constructor comment.
	 */
	public CICustomerBase() 
	{
		super( new Integer(CustomerTypes.CUSTOMER_CI) );
	}
	
	
	/**
	 * This method was created in VisualAge.
	 * @return CICustomerBase[]
	 * @param stateGroup java.lang.Integer
	 */
	public static final CICustomerBase[] getAllAvailableCICustomers(String databaseAlias) throws java.sql.SQLException
	{
		java.util.ArrayList tmpList = new java.util.ArrayList(30);
		java.sql.Connection conn = null;
		java.sql.PreparedStatement pstmt = null;
		java.sql.ResultSet rset = null;
	
		
	
		//get all the unused customers for Curtailment	
		String sql = "select CustomerID, CompanyName " +
						 "from " + 
						 com.cannontech.database.db.customer.CICustomerBase.TABLE_NAME + 
						 " where CustomerID not in " +
						 "(select CustomerID " + 
						 "from lmprogramcurtailcustomerlist)";
	
		try
		{		
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(databaseAlias);
	
			if( conn == null )
			{
				throw new IllegalStateException("Error getting database connection.");
			}
			else
			{
				pstmt = conn.prepareStatement(sql.toString());
				
				rset = pstmt.executeQuery();							
		
				while( rset.next() )
				{
					CICustomerBase customer = new CICustomerBase();
					
					customer.setCustomerID( new Integer(rset.getInt("CustomerID")) );
					customer.getCiCustomerBase().setCompanyName( rset.getString("CompanyName") );
					//customer.set( rset.getString("type") );
	
					tmpList.add( customer );
				}
						
			}		
		}
		catch( java.sql.SQLException e )
		{
			com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( pstmt != null ) pstmt.close();
				if( conn != null ) conn.close();
			} 
			catch( java.sql.SQLException e2 )
			{
				com.cannontech.clientutils.CTILogger.error( e2.getMessage(), e2 );//something is up
			}	
		}
	
	
		CICustomerBase retVal[] = new CICustomerBase[ tmpList.size() ];
		tmpList.toArray( retVal );
		
		return retVal;
	}
	
	/**
	 * 
	 * This method was created in VisualAge.
	 */
	public void add() throws java.sql.SQLException 
	{
		super.add();

		getAddress().add();
		
		getCiCustomerBase().setMainAddressID( getAddress().getAddressID() );
		
		getCiCustomerBase().add();
		getCustomerBaseLine().add();
		
		if( getCustomerBaseLinePoint().getPointID() != null )
			getCustomerBaseLinePoint().add();


		if( getEnergyCompany() != null )
		{
			Object addValues[] = 
			{ 
				getEnergyCompany().getEnergyCompanyID(), 
				getCustomerID()
			};
	
			//just add the bridge value to the EnergyCompanyCustomerList table
			// showing that this customer belongs to the EnergyCompany
			add("EnergyCompanyCustomerList", addValues);		
		}
		
		
		setDbConnection(null);
	}
	

	/**
	 * This method was created in VisualAge.
	 */
	public void delete() throws java.sql.SQLException
	{
		delete(
			com.cannontech.database.db.device.lm.LMEnergyExchangeCustomerList.TABLE_NAME,
			"CustomerID",
			getCustomerID() );
	
		//just delete the bridge value to the EnergyCompanyCustomerList table
		delete("EnergyCompanyCustomerList", "CustomerID", getCustomerID() );		
		
	
		delete("LMEnergyExchangeHourlyCustomer", "CustomerID", getCustomerID() );
		delete("LMEnergyExchangeCustomerReply", "CustomerID", getCustomerID() );
		delete("LMCurtailCustomerActivity", "CustomerID", getCustomerID() );
			


		getCustomerBaseLine().delete();
		getCustomerBaseLinePoint().delete();

		
		//delete("CustomerAddress", "AddressID", getCiCustomerBase().getAddressID() );
		getAddress().setAddressID( 
				getCiCustomerBase().getMainAddressID() );
//					com.cannontech.database.db.customer.CICustomerBase.getCICustomerAddressID(
//							getCustomerID(), getDbConnection()) );
	
		getCiCustomerBase().delete();
	
		getAddress().delete();
		
	
		super.delete();
	
		setDbConnection(null);
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 4:27:13 PM)
	 * @return com.cannontech.database.db.customer.CICustomerBase
	 */
	public com.cannontech.database.db.customer.CICustomerBase getCiCustomerBase() 
	{
		if( ciCustomerBase == null )
			ciCustomerBase = new com.cannontech.database.db.customer.CICustomerBase();
	
		return ciCustomerBase;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2001 2:47:56 PM)
	 * @return com.cannontech.database.db.device.customer.CustomerAddress
	 */
	public Address getAddress() 
	{
		if( customerAddress == null )
			customerAddress = new Address();
			
		return customerAddress;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 3:34:40 PM)
	 * @return com.cannontech.database.db.device.customer.CustomerBaseLine
	 */
	public CustomerBaseLine getCustomerBaseLine() 
	{
		if( customerBaseLine == null )
			customerBaseLine = new CustomerBaseLine();
	
		return customerBaseLine;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 3:34:40 PM)
	 * @return com.cannontech.database.db.device.customer.CustomerBaseLine
	 */
	public CustomerBaseLinePoint getCustomerBaseLinePoint() 
	{
		if( customerBaseLinePoint == null )
			customerBaseLinePoint = new CustomerBaseLinePoint();

		return customerBaseLinePoint;
	}

	

	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2002 4:32:41 PM)
	 * @return com.cannontech.database.db.company.EnergyCompany
	 */
	public com.cannontech.database.db.company.EnergyCompany getEnergyCompany() {
		return energyCompany;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/16/2001 12:47:02 PM)
	 * @return java.util.Vector
	 */
	public java.util.Vector getMeterVector() 
	{
		if( meterVector == null )
			meterVector = new java.util.Vector(10);
	
		return meterVector;
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void retrieve() throws java.sql.SQLException
	{
		super.retrieve();
	
		getCiCustomerBase().retrieve();
		
		
		getCustomerBaseLine().retrieve();
		getCustomerBaseLinePoint().retrieve();

	
		getAddress().setAddressID( getCiCustomerBase().getMainAddressID() );
		getAddress().retrieve();
	
	
		try
		{
	//----------------------------------------------------------------------------------
	//--------------- TODO:  NO LONGER, MUST ADD A MAPPING TABLE FOR METERS-------------
	//----------------------------------------------------------------------------------
			com.cannontech.database.db.pao.PAOowner[] meters = com.cannontech.database.db.pao.PAOowner.getAllPAOownerChildren( 
					getCustomerID(), getDbConnection() );
			for( int i = 0; i < meters.length; i++ )
			{
				meters[i].setDbConnection(getDbConnection());
				getMeterVector().addElement( meters[i] );
			}
		}
		catch(java.sql.SQLException e )
		{
			//not necessarily an error
		}
	
	/**************  BEGIN SUPREME HACK!!!!! ***********************************/
		//if an EnergyCompany exists, gets its values
		String[] cols = { "EnergyCompanyID", "CustomerID" };
		String[] keys = { "CustomerID" };
		Object[] vals = { getCustomerID() };
		Object[] rets = retrieve( cols,
										 "EnergyCompanyCustomerList",
										 keys,
										 vals );
	
		if( rets.length > 0 )
		{
			EnergyCompany ec = new EnergyCompany();
			ec.setEnergyCompanyID( (Integer)rets[0] );
			ec.setDbConnection( getDbConnection() );
	
			//get the data
			energyCompany = ec;
			getEnergyCompany().retrieve();
		}
	/**************  END SUPREME HACK!!!!! ***********************************/
	
		setDbConnection(null);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/24/2001 4:27:13 PM)
	 * @param newCiCustomerBase com.cannontech.database.db.customer.CICustomerBase
	 */
	public void setCiCustomerBase(com.cannontech.database.db.customer.CICustomerBase newCiCustomerBase) {
		ciCustomerBase = newCiCustomerBase;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2001 2:47:56 PM)
	 * @param newCustomerAddress com.cannontech.database.db.customer.CustomerAddress
	 */
	public void setCustomerAddress(com.cannontech.database.db.customer.Address newCustomerAddress) {
		customerAddress = newCustomerAddress;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 3:34:40 PM)
	 * @param newCustomerBaseLine com.cannontech.database.db.customer.CustomerBaseLine
	 */
	public void setCustomerBaseLine(com.cannontech.database.db.customer.CustomerBaseLine newCustomerBaseLine) {
		customerBaseLine = newCustomerBaseLine;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/18/2001 3:34:40 PM)
	 * @param newCustomerBaseLine com.cannontech.database.db.customer.CustomerBaseLine
	 */
	public void setCustomerBaseLinePoint(CustomerBaseLinePoint newCustomerBaseLinePoint) {
		customerBaseLinePoint = newCustomerBaseLinePoint;
	}

	/**
	 * This method was created in VisualAge.
	 * @param deviceID java.lang.Integer
	 */
	public void setCustomerID(Integer custID) 
	{
		super.setCustomerID( custID );
		getCiCustomerBase().setCustomerID( custID );
		getCustomerBaseLine().setCustomerID(custID);
		getCustomerBaseLinePoint().setCustomerID( custID );
	}
	
	/**
	 * Insert the method's description here.
	 * Creation date: (1/4/00 3:32:03 PM)
	 * @param conn java.sql.Connection
	 */
	public void setDbConnection(java.sql.Connection conn)
	{
		super.setDbConnection(conn);
		getCiCustomerBase().setDbConnection(conn);
		getAddress().setDbConnection(conn);	
		getCustomerBaseLine().setDbConnection(conn);
		getCustomerBaseLinePoint().setDbConnection( conn );

	
	//----------------------------------------------------------------------------------
	//--------------- TODO:  NO LONGER, MUST ADD A MAPPING TABLE FOR METERS-------------
	//----------------------------------------------------------------------------------
		for (int i = 0; i < getMeterVector().size(); i++)
			((com.cannontech.database.db.DBPersistent) getMeterVector().elementAt(i)).setDbConnection(conn);
	
		if( getEnergyCompany() != null )
			getEnergyCompany().setDbConnection( conn );
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (3/26/2002 4:32:41 PM)
	 * @param newEnergyCompany com.cannontech.database.db.company.EnergyCompany
	 */
	public void setEnergyCompany(com.cannontech.database.db.company.EnergyCompany newEnergyCompany) {
		energyCompany = newEnergyCompany;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (10/16/2001 12:47:02 PM)
	 * @param newMeterVector java.util.Vector
	 */
	public void setMeterVector(java.util.Vector newMeterVector) {
		meterVector = newMeterVector;
	}
	/**
	 * This method was created in VisualAge.
	 */
	public void update() throws java.sql.SQLException 
	{
		getAddress().setAddressID(getCiCustomerBase().getMainAddressID());
		getAddress().update();

		
		super.update();
		getCiCustomerBase().update();
	
		getCustomerBaseLine().update();
		
		if( getCustomerBaseLinePoint().getPointID() != null )
			getCustomerBaseLinePoint().update();
	
	
	//----------------------------------------------------------------------------------
	//--------------- TODO:  NO LONGER, MUST ADD A MAPPING TABLE FOR METERS-------------
	//----------------------------------------------------------------------------------
		// delete all the ownership of meters for this customer
		com.cannontech.database.db.pao.PAOowner.deleteAllPAOowners( 
			getCustomerID(), getDbConnection() );

		// add all the current selected meters for this customer
		for (int i = 0; i < getMeterVector().size(); i++)
			 ((com.cannontech.database.db.pao.PAOowner) getMeterVector().elementAt(i)).add();

	
		//just delete the bridge value to the EnergyCompanyCustomerList table
		delete("EnergyCompanyCustomerList", "CustomerID", getCustomerID() );
	
		//add a new EnergyCompany if needed
		if( getEnergyCompany() != null )
		{
			Object addValues[] = 
			{ 
				getEnergyCompany().getEnergyCompanyID(), 
				getCustomerID()
			};
	
			//just add the bridge value to the EnergyCompanyCustomerList table
			// showing that this customer belongs to the EnergyCompany
			add(
				com.cannontech.database.db.web.EnergyCompanyCustomerList.tableName,
				addValues);
		}
			 
		setDbConnection(null);
	}
	
	/**
	 * This method was created in VisualAge.
	 * @return java.lang.String
	 */
	public String toString() 
	{
		return getCiCustomerBase().getCompanyName();
	}

}
