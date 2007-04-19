package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;
import com.cannontech.database.db.point.Point;

/**
 * This type was created in VisualAge.
 */
@SuppressWarnings("serial")
public class CapControlFeeder extends CapControlYukonPAOBase implements com.cannontech.common.editor.EditorPanel
{
	public static final String ENABLE_OPSTATE = "feederEnabled";
    public static final String DISABLE_OPSTATE = "feederDisabled";
    
    private com.cannontech.database.db.capcontrol.CapControlFeeder capControlFeeder = null;
	private ArrayList ccBankListVector = null;

	//private com.cannontech.database.db.capcontrol.CapControlStrategy cbcStrategy = null;

/**
 */
public CapControlFeeder() {
	super();

	setPAOCategory( PAOGroups.STRING_CAT_CAPCONTROL );
	setPAOClass( PAOGroups.STRING_CAT_CAPCONTROL );
}
/**
 */
public CapControlFeeder(Integer feedID) 
{
	this();
	setCapControlPAOID( feedID );
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getPAObjectID() == null ){
        PaoDao paoDao = DaoFactory.getPaoDao();   
        setCapControlPAOID(paoDao.getNextPaoId());   
    }

    super.add();
	
	getCapControlFeeder().add();
	
	for( int i = 0; i < getChildList().size(); i++ )
	{
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getChildList().get(i)).add();
	}
	
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{

	//remove all the associations of CapBanks to this Feeder
	com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList( 
					getCapControlPAOID(), null, getDbConnection() );

	//remove all the associations of this Feeder to any SubBus
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
					null, getCapControlPAOID(), getDbConnection() );

	//Delete from all dynamic Feeder cap control tables here
	delete("DynamicCCFeeder", "FeederID", getCapControlPAOID() );
    deleteAllPoints();
    //delete(Point.TABLE_NAME, Point.SETTER_COLUMNS[2], getCapControlPAOID());

	getCapControlFeeder().delete();

	super.delete();
}
private void deleteAllPoints() throws SQLException {
    List<LitePoint> litePointsByPaObjectId = DaoFactory.getPointDao().getLitePointsByPaObjectId(getCapControlPAOID());
    for (LitePoint point : litePointsByPaObjectId) {
        PointBase pointPers = (PointBase) LiteFactory.convertLiteToDBPers(point);
        pointPers.setDbConnection(getDbConnection());
        pointPers.delete();
    }
}
/**
 * Strategy object used for control
 * 
 */
//public com.cannontech.database.db.capcontrol.CapControlStrategy getCBCStrategy() 
//{
//	if( cbcStrategy == null )
//		cbcStrategy = new com.cannontech.database.db.capcontrol.CapControlStrategy();
//
//	return cbcStrategy;
//}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @return com.cannontech.database.db.capcontrol.CapControlFeeder
 */
public com.cannontech.database.db.capcontrol.CapControlFeeder getCapControlFeeder() 
{
	if( capControlFeeder == null )
		capControlFeeder = new com.cannontech.database.db.capcontrol.CapControlFeeder();

	return capControlFeeder;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @return java.util.Vector
 */
public ArrayList getChildList() 
{
	if( ccBankListVector == null )
		ccBankListVector = new ArrayList(16);
		
	return ccBankListVector;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	
	getCapControlFeeder().retrieve();
	
	ccBankListVector = com.cannontech.database.db.capcontrol.CCFeederBankList.getCapBanksOnFeederList(
		getCapControlPAOID(), getDbConnection() );
	
//	getCBCStrategy().setStrategyID( getCapControlFeeder().getStrategyID() );
//	getCBCStrategy().retrieve();
}

/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @param newCapControlFeeder com.cannontech.database.db.capcontrol.CapControlFeeder
 */
public void setCapControlFeeder(com.cannontech.database.db.capcontrol.CapControlFeeder newCapControlFeeder) {
	capControlFeeder = newCapControlFeeder;
}
/**
 * This method was created in VisualAge.
 */
public void setCapControlPAOID(Integer feedID)
{
	super.setPAObjectID( feedID );
	getCapControlFeeder().setFeederID( feedID );
	
	for( int i = 0; i < getChildList().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getChildList().get(i)).setFeederID( feedID );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 6:50:18 PM)
 * @param newCcBankListVector java.util.Vector
 */
public void setCcBankList(ArrayList newCcBankListVector) {
	ccBankListVector = newCcBankListVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection( conn );
	getCapControlFeeder().setDbConnection(conn);
//	getCBCStrategy().setDbConnection(conn);
	
	for (int i = 0; i < getChildList().size(); i++)
		 ((com.cannontech.database.db.capcontrol.CCFeederBankList) getChildList().get(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	
	getCapControlFeeder().update();
//	getCBCStrategy().update();
	
	com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList( 
			getCapControlPAOID(), null, getDbConnection() );

	for( int i = 0; i < getChildList().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederBankList) getChildList().get(i)).add();
}




}
