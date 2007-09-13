package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcOperations;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.JdbcTemplateHelper;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.point.PointBase;

/**
 * This type was created in VisualAge.
 */
public class CapControlSubBus extends CapControlYukonPAOBase implements com.cannontech.common.editor.EditorPanel
{
	public static final String ENABLE_OPSTATE = "subEnabled";
    public static final String DISABLE_OPSTATE = "subDisabled";
    public static final String ENABLE_OVUVSTATE = "subOVUVEnabled";
    public static final String DISABLE_OVUVSTATE = "subOVUVDisabled";

    private com.cannontech.database.db.capcontrol.CapControlSubstationBus capControlSubstationBus = null;

	//contains objects of type com.cannontech.database.db.capcontrol.CCFeederSubAssignment
	private ArrayList ccFeederList = null;
/**
 */
public CapControlSubBus() {
	super();
	setPAOCategory( PAOGroups.STRING_CAT_CAPCONTROL );
	setPAOClass( PAOGroups.STRING_CAT_CAPCONTROL );
}
/**
 */
public CapControlSubBus(Integer subBusID) 
{
	this();
	setCapControlPAOID( subBusID );
}

/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getCapControlPAOID() == null ) {
        PaoDao paoDao = DaoFactory.getPaoDao();
		setCapControlPAOID(paoDao.getNextPaoId());
    }

	super.add();
	getCapControlSubstationBus().add();
	
	for( int i = 0; i < getChildList().size(); i++ )
	{
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getChildList().get(i)).add();
	}
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{

	//remove all the associations of feeders to this SubstationBus
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
					getCapControlPAOID(), null, getDbConnection() );


	//Delete from all dynamic SubBus cap control tables here
	delete("DynamicCCSubstationBus", "SubstationBusID", getCapControlPAOID() );
    delete("ccsubareaassignment", "SubstationBusID", getCapControlPAOID() );
    //delete all the points that belog to this sub
	//there should be a constraint on pointid in point table
    deleteAllPoints();
    //delete(Point.TABLE_NAME, Point.SETTER_COLUMNS[2], getCapControlPAOID());
	
    getCapControlSubstationBus().delete();

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
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @return com.cannontech.database.db.capcontrol.CapControlSubstationBus
 */
public com.cannontech.database.db.capcontrol.CapControlSubstationBus getCapControlSubstationBus() 
{
	if( capControlSubstationBus == null )
		capControlSubstationBus = new com.cannontech.database.db.capcontrol.CapControlSubstationBus();

	return capControlSubstationBus;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 *
 */
public ArrayList getChildList() 
{
	if( ccFeederList == null )
		ccFeederList = new ArrayList(16);

	return ccFeederList;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	
	getCapControlSubstationBus().retrieve();
	
	ccFeederList = com.cannontech.database.db.capcontrol.CCFeederSubAssignment.getCCFeedersOnSub(
		getPAObjectID(), getDbConnection() );
}
/**
 * This method was created in VisualAge.
 */
public void setCapControlPAOID(Integer subBusID) 
{
	super.setPAObjectID( subBusID );
	getCapControlSubstationBus().setSubstationBusID( subBusID );
	
	for( int i = 0; i < getChildList().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getChildList().get(i)).setSubstationBusID( subBusID );
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @param newCapControlSubstationBus com.cannontech.database.db.capcontrol.CapControlSubstationBus
 */
public void setCapControlSubstationBus(com.cannontech.database.db.capcontrol.CapControlSubstationBus newCapControlSubstationBus) {
	capControlSubstationBus = newCapControlSubstationBus;
}
/**
 * Insert the method's description here.
 * Creation date: (11/9/2001 3:00:46 PM)
 * @param newCcFeederListVector java.util.Vector
 */
public void setCcFeederList(ArrayList newCcFeederListVector) {
	ccFeederList = newCcFeederListVector;
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn)
{
	super.setDbConnection( conn );
	getCapControlSubstationBus().setDbConnection(conn);
	
	for (int i = 0; i < getChildList().size(); i++)
		 ((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getChildList().get(i)).setDbConnection(conn);
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	
	getCapControlSubstationBus().update();
	
	com.cannontech.database.db.capcontrol.CCFeederSubAssignment.deleteCCFeedersFromSubList( 
			getCapControlPAOID(), null, getDbConnection() );

	for( int i = 0; i < getChildList().size(); i++ )
		((com.cannontech.database.db.capcontrol.CCFeederSubAssignment) getChildList().get(i)).add();
}

public static List<Integer> getAllUnassignedBuses () {
    SqlStatementBuilder allSubs = new SqlStatementBuilder();
    allSubs.append("select paobjectid from yukonpaobject where type like 'CCSUBBUS' ");
    allSubs.append("and ");
    allSubs.append("paobjectid not in (select substationbusid from ccsubareaassignment)");
    JdbcOperations yukonTemplate = JdbcTemplateHelper.getYukonTemplate();
    return yukonTemplate.queryForList(allSubs.toString(), Integer.class);

}
}
