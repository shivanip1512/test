/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.database.data.device.lm;

import com.cannontech.database.data.pao.YukonPAObject;
import java.util.Vector;
import com.cannontech.database.db.device.lm.LMControlScenarioProgram;

/**
 * @author jdayton
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class LMScenario extends YukonPAObject implements com.cannontech.database.db.CTIDbChange, com.cannontech.common.editor.EditorPanel
{
	
	private Vector allThePrograms; 
	
	public LMScenario()
	{
		super();
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_CAT_LMSCENARIO );
	}
	
	
	public void add() throws java.sql.SQLException 
	{
		if( getScenarioID() == null )
			setScenarioID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );

		super.add();
		
		//add all the wee programs to the database
		if(getAllThePrograms() != null)
		{
			for(int j = 0; j < getAllThePrograms().size(); j++)
			{
				((LMControlScenarioProgram)getAllThePrograms().elementAt(j)).add();
			}
		}
	}
	
	/**
	 * delete method comment.
	 */
	public void delete() throws java.sql.SQLException 
	{
		
		//delete all the wee programs from the database
		if(getAllThePrograms() != null)
		{
			for(int j = 0; j < getAllThePrograms().size(); j++)
			{
				((LMControlScenarioProgram)getAllThePrograms().elementAt(j)).delete();
			}
		}
		super.delete();
		
	
	}

	
	public Integer getScenarioID()
	{
		return getPAObjectID();
	}
	
	public String getScenarioName()
	{
		return getPAOName();
	}
	
	public Vector getAllThePrograms()
	{
		return allThePrograms;
	}
	
	
	
	
	public com.cannontech.message.dispatch.message.DBChangeMsg[] getDBChangeMsgs( int typeOfChange )
	{
		com.cannontech.message.dispatch.message.DBChangeMsg[] msgs =
		{
			new com.cannontech.message.dispatch.message.DBChangeMsg(
				getScenarioID().intValue(),
				com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_LMSCENARIO_DB,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMSCENARIO,
				com.cannontech.message.dispatch.message.DBChangeMsg.CAT_LMSCENARIO,
				typeOfChange)
		};


		return msgs;
	}
	
	public void initialize()
	{
	}
	
	
	public void setScenarioID(Integer newID)
	{
		setPAObjectID(newID);
		
		if(getAllThePrograms() != null)
		{
			for(int j = 0; j < getAllThePrograms().size(); j++)
			{
				((LMControlScenarioProgram)getAllThePrograms().elementAt(j)).setScenarioID(newID);
			}
		}
	}
	
	public void setScenarioName(String newName)
	{
		this.setPAOName(newName);
	}
	
	public void setDbConnection(java.sql.Connection conn) 
	{
		super.setDbConnection(conn);

		// do all the programs for this scenario
		if(getAllThePrograms() != null)
		{
			for(int j = 0; j < getAllThePrograms().size(); j++)
			{
				((LMControlScenarioProgram)getAllThePrograms().elementAt(j)).setDbConnection(conn);
			}
		}
			
		
	}
}
