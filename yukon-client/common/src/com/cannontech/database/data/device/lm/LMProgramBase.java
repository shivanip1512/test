package com.cannontech.database.data.device.lm;

/**
 * Insert the type's description here.
 * Creation date: (12/6/00 3:54:11 PM)
 * @author: 
 */
import java.util.Vector;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.device.lm.DeviceListItem;
import com.cannontech.database.db.device.lm.LMControlAreaProgram;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;
import com.cannontech.database.db.device.lm.LMProgramControlWindow;

public abstract class LMProgramBase extends com.cannontech.database.data.pao.YukonPAObject implements com.cannontech.common.editor.EditorPanel
{
	com.cannontech.database.db.device.lm.LMProgram program = null;

	// should only contain LMProgramControlWindow objects
	private Vector<LMProgramControlWindow> lmProgramControlWindowVector = null;

	//used to store a list of data for each program. 
	// This should only contain DBPersistant items that implement DeviceListItem !!!!
	private Vector<DeviceListItem> lmProgramStorageVector = null;

	public static final String OPSTATE_AUTOMATIC = "Automatic";
	public static final String OPSTATE_MANUALONLY = "ManualOnly";
	public static final String OPSTATE_TIMED = "Timed";
/**
 * LMProgramBase constructor comment.
 */
public LMProgramBase() {
	super();
}
/**
 * This method was created in VisualAge.
 */
public void add() throws java.sql.SQLException 
{
	if( getPAObjectID() == null ) {
        PaoDao paoDao = DaoFactory.getPaoDao();
        setPAObjectID(paoDao.getNextPaoId());
    }

    super.add();
	getProgram().add();

	for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
		lmProgramControlWindow.add();
	}
}
/**
 * This method was created in VisualAge.
 */
public void delete() throws java.sql.SQLException
{
	//delete all the program windows
	LMProgramControlWindow.deleteAllProgramControlWindows( getPAObjectID(), getDbConnection() );


	delete("DynamicLMProgram", "DeviceID", getPAObjectID() );
	delete(LMControlAreaProgram.TABLE_NAME, "LMProgramDeviceID", getPAObjectID() );
	delete(LMControlScenarioProgram.TABLE_NAME, "ProgramID", getPAObjectID() );
	
	
	getProgram().delete();
	super.delete();
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:14:54 PM)
 * @return java.util.Vector
 */
public Vector<LMProgramControlWindow> getLmProgramControlWindowVector() 
{
	if( lmProgramControlWindowVector == null )
		lmProgramControlWindowVector = new Vector<LMProgramControlWindow>(2);
	
	return lmProgramControlWindowVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:16:22 PM)
 * @return java.util.Vector
 */
public Vector<DeviceListItem> getLmProgramStorageVector()
{
	if (lmProgramStorageVector == null)
		lmProgramStorageVector = new Vector<DeviceListItem>(10) {
			public void add(int i, DeviceListItem o) {
				super.add(i, o);
			}

			public boolean add(DeviceListItem o) {
				return super.add(o);
			}

			public void addElement(DeviceListItem o) {
				super.addElement(o);
			}
		};

    return lmProgramStorageVector;
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/00 3:57:41 PM)
 * @return com.cannontech.database.db.device.lm.LMProgram
 */
public com.cannontech.database.db.device.lm.LMProgram getProgram() 
{
	if( program == null )
		program = new com.cannontech.database.db.device.lm.LMProgram();
		
	return program;
}
/**
 * This method was created in VisualAge.
 */
public void retrieve() throws java.sql.SQLException
{
	super.retrieve();
	getProgram().retrieve();

	//get all the LMProgramControlWindow for this LMProgramBase
	LMProgramControlWindow[] windows = LMProgramControlWindow.getAllLMProgramControlWindows( getPAObjectID(), getDbConnection() );
	
	for( int i = 0; i < windows.length; i++ )
		getLmProgramControlWindowVector().add( windows[i] );
}
/**
 * Insert the method's description here.
 * Creation date: (1/4/00 3:32:03 PM)
 * @param conn java.sql.Connection
 */
public void setDbConnection(java.sql.Connection conn) 
{
	super.setDbConnection(conn);
	getProgram().setDbConnection(conn);

	for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
		lmProgramControlWindow.setDbConnection(conn);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:14:54 PM)
 * @param newLmProgramControlWindowVector java.util.Vector
 */
public void setLmProgramControlWindowVector(Vector<LMProgramControlWindow> newLmProgramControlWindowVector) {
	lmProgramControlWindowVector = newLmProgramControlWindowVector;
}
/**
 * Insert the method's description here.
 * Creation date: (3/16/2001 12:16:22 PM)
 * @param newLmControlAreaProgramVector java.util.Vector
 */
public void setLmProgramStorageVector(Vector<DeviceListItem> newlmProgramStorageVector) 
{
	lmProgramStorageVector = newlmProgramStorageVector;
}
/**
 * Insert the method's description here.
 * Creation date: (9/12/2001 10:25:35 AM)
 */
public void setName( String name )
{
	getYukonPAObject().setPaoName( name );
}
/**
 * This method was created in VisualAge.
 * @param deviceID java.lang.Integer
 */
public void setPAObjectID(Integer paoID)
{
	super.setPAObjectID(paoID);
	getProgram().setDeviceID(paoID);

	for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
		lmProgramControlWindow.setDeviceID(paoID);
	}
}
/**
 * Insert the method's description here.
 * Creation date: (3/6/2002 10:18:26 AM)
 * @param category java.lang.String
 */
public void setPAOType(String paoType)
{
	getYukonPAObject().setType(paoType);
}
/**
 * Insert the method's description here.
 * Creation date: (12/6/00 3:57:41 PM)
 * @param newProgram com.cannontech.database.db.device.lm.LMProgram
 */
public void setProgram(com.cannontech.database.db.device.lm.LMProgram newProgram) {
	program = newProgram;
}
/**
 * This method was created in VisualAge.
 */
public void update() throws java.sql.SQLException
{
	super.update();
	getProgram().update();

	//delete all the program windows
	LMProgramControlWindow.deleteAllProgramControlWindows( getPAObjectID(), getDbConnection() );
	
	for (LMProgramControlWindow lmProgramControlWindow : getLmProgramControlWindowVector()) {
		lmProgramControlWindow.add();
	}
}
}
