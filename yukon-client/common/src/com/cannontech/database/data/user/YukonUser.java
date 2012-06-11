package com.cannontech.database.data.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;

import org.apache.log4j.Logger;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.NativeIntVector;
import com.cannontech.core.authorization.dao.PaoPermissionDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.contact.Contact;
import com.cannontech.database.db.user.YukonGroup;
import com.cannontech.database.db.web.EnergyCompanyOperatorLoginList;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.roles.YukonGroupRoleDefs;
import com.cannontech.spring.YukonSpringHook;

/*** 
 * @author alauinger
 */
public class YukonUser extends DBPersistent implements CTIDbChange, EditorPanel
{	
    private static final Logger log = YukonLogManager.getLogger(YukonUser.class);
    private static final Integer[] immutableUserIds = new Integer[] {
        -1, // Admin
        -2, // Yukon
        -100 // DefaultCTI
    };
    
    private com.cannontech.database.db.user.YukonUser yukonUser;
	private Vector<YukonGroup> yukonGroups; //type = com.cannontech.database.db.user.YukonGroup
	private NativeIntVector yukonUserOwnedPAOs; //type = com.cannontech.database.db.user.UserPAOwner
	private EnergyCompanyOperatorLoginList company;
	/**
	 * adds any default group roles for a new user
	 *  these group DB objects only needs their GroupIDs set for now
	 */		
	public YukonUser() 
	{
		super();

		YukonGroup group = new YukonGroup( new Integer(YukonGroupRoleDefs.GRP_YUKON) );
		getYukonGroups().add( group );
	}
	
	@Override
    public void setDbConnection(java.sql.Connection conn) {
		super.setDbConnection( conn );
		getYukonUser().setDbConnection( conn );
		
		//This is not null if this login has been linked to an energy company
		if(company != null)
			company.setDbConnection( conn );
		
	}

	public Integer getID()
	{
		return getUserID();
	}
	
	/**
	 * This method was created in VisualAge.
	 * @param pointID java.lang.Integer
	 */
	public final static boolean isUsedByContact(int loginID_, String databaseAlias) 
	{
		com.cannontech.database.SqlStatement stmt =
			new com.cannontech.database.SqlStatement(
				"SELECT LoginID FROM " + 
				Contact.TABLE_NAME + 
				" WHERE LoginID=" + loginID_,
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
	
	
	/**
	 * @see com.cannontech.database.db.DBPersistent#add()
	 */
	@Override
    public void add() throws SQLException 
	{
		if( getYukonUser().getUserID() == null )
			setUserID(
				com.cannontech.database.db.user.YukonUser.getNextUserID(getDbConnection()) );

		getYukonUser().add();

		
		for (int i = 0; i < getYukonGroups().size(); i++) 
		{
			Object[] addValues = 
			{
				getYukonUser().getUserID(), 
				getYukonGroups().get(i).getGroupID()
			};

			add( YukonGroup.TBL_YUKON_USER_GROUP, addValues );
		}

		//This is not null if this login has been linked to an energy company
		if(company != null)
		{
			company.setOperatorLoginID(getUserID());
			company.add();
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#delete()
	 */
	@Override
    public void delete() throws SQLException 
	{
		delete( YukonGroup.TBL_YUKON_USER_GROUP, "UserID", getYukonUser().getUserID() );
        
		YukonUserDao yukonUserDao =
		    YukonSpringHook.getBean("yukonUserDao", YukonUserDao.class);
		yukonUserDao.removeUserFromEventBase(getYukonUser().getUserID());

		delete( EnergyCompanyOperatorLoginList.tableName, "OperatorLoginID", getYukonUser().getUserID() );
        @SuppressWarnings("unchecked") PaoPermissionDao<LiteYukonUser> paoPermissionDao =
            YukonSpringHook.getBean("userPaoPermissionDao", PaoPermissionDao.class);
        paoPermissionDao.removeAllPermissions(getUserID());
        
		getYukonUser().delete();
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#retrieve()
	 */
	@Override
    public void retrieve() throws SQLException 
	{
		getYukonUser().retrieve();
		
		//add the role groups this user belongs to
		YukonGroup[] groups = YukonGroup.getYukonGroups(getUserID(), getDbConnection() );
						
		getYukonGroups().clear();
		for( int i = 0; i < groups.length; i++ ) {
			getYukonGroups().add( groups[i] );
		}
	}

	/**
	 * @see com.cannontech.database.db.DBPersistent#update()
	 */
	@Override
    public void update() throws SQLException {

		setUserID( getYukonUser().getUserID() );

		getYukonUser().update();
		
		//remove then add all the groups this user belongs to
		delete( YukonGroup.TBL_YUKON_USER_GROUP, "UserID", getYukonUser().getUserID() );
		for (int i = 0; i < getYukonGroups().size(); i++) 
		{
			Object[] addValues = 
			{
				getYukonUser().getUserID(), 
				getYukonGroups().get(i).getGroupID()
			};
			add( YukonGroup.TBL_YUKON_USER_GROUP, addValues );
		}
				
		//This is not null if this login has been linked to an energy company
		if(company != null)
		{
			this.delete( "EnergyCompanyOperatorLoginList", "OperatorLoginID", company.getOperatorLoginID() );
			
			if(company.getEnergyCompanyID() != null)
				company.add();
		}
	}
	
	public static void deleteOperatorLogin(Integer userID) {
	    checkForValidUserId(userID);
	    
		Connection conn = null;
		
		try {
			conn = com.cannontech.database.PoolManager.getInstance().getConnection(
					com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			
			YukonUser user = new YukonUser();
			user.setUserID( userID );
			user.setDbConnection( conn );
			
			user.delete( "EnergyCompanyOperatorLoginList", "OperatorLoginID", userID );
			user.delete( "OperatorLoginGraphList", "OperatorLoginID", userID );
			user.delete();
		} catch (java.sql.SQLException e) {
		    log.error(e);
		} finally {
			SqlUtils.close(conn);
		}
	}
	
	private static void checkForValidUserId(Integer userId) {
	    for (final Integer immutableUserId : immutableUserIds) {
	        if (userId.equals(immutableUserId)) 
	            throw new UnsupportedOperationException("Deletion of userid: " + userId + " not supported.");
	    }
	}

	/**
	 * Returns the yukonGroups.
	 * @return Vector
	 */
	public Vector<YukonGroup> getYukonGroups() {
		if (yukonGroups == null)
			yukonGroups = new Vector<YukonGroup>();
		return yukonGroups;
	}

	/**
	 * Returns the yukonUser.
	 * @return com.cannontech.database.db.user.YukonUser
	 */
	public com.cannontech.database.db.user.YukonUser getYukonUser() {
		if(yukonUser == null)
			yukonUser = new com.cannontech.database.db.user.YukonUser();
		return yukonUser;
	}

	/**
	 * Sets the yukonGroups.
	 * @param yukonGroups The yukonGroups to set
	 */
	public void setYukonGroups(Vector<YukonGroup> yukonGroups) {
		this.yukonGroups = yukonGroups;
	}
	
	public void setUserOwnedPAOs(NativeIntVector yukonPAOs) 
	{
		yukonUserOwnedPAOs = yukonPAOs;
	}

	/**
	 * Sets the yukonUser.
	 * @param yukonUser The yukonUser to set
	 */
	public void setYukonUser(
		com.cannontech.database.db.user.YukonUser yukonUser) {
		this.yukonUser = yukonUser;
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public Integer getUserID() {
		return getYukonUser().getUserID();
	}

	/**
	 * Returns the userID.
	 * @return Integer
	 */
	public void setUserID( Integer userID_ ) {
		getYukonUser().setUserID( userID_ );
	}

	
	@Override
    public String toString()
	{
		return getYukonUser().getUsername();
	}


	/**
	 * @see com.cannontech.database.db.CTIDbChange#getDBChangeMsgs(int)
	 */
	public DBChangeMsg[] getDBChangeMsgs(DbChangeType dbChangeType) {
		
	    DBChangeMsg[] msgs = {
	            new DBChangeMsg(
	                            getYukonUser().getUserID().intValue(),
	                            DBChangeMsg.CHANGE_YUKON_USER_DB,
	                            DBChangeMsg.CAT_YUKON_USER,
	                            DBChangeMsg.CAT_YUKON_USER,
	                            dbChangeType)
		};

		return msgs;
	}

	public NativeIntVector getUserOwnedPAOs()
	{
		if( yukonUserOwnedPAOs == null )
			yukonUserOwnedPAOs = new NativeIntVector(10);
		
		return yukonUserOwnedPAOs;
	}
	
	public void setEnergyCompany(EnergyCompanyOperatorLoginList ceo)
	{
		company = ceo;
	}

}
