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
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.db.NestedDBPersistent;

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
		getYukonPAObject().setType( com.cannontech.database.data.pao.PAOGroups.STRING_LM_SCENARIO[0] );
	}
	
	
	public void add() throws java.sql.SQLException 
	{
		if( getScenarioID() == null )
		{
			setScenarioID( com.cannontech.database.db.pao.YukonPAObject.getNextYukonPAObjectID() );
			if(getAllThePrograms() != null)
			{
				for(int j = 0; j < getAllThePrograms().size(); j++)
				{
					((LMControlScenarioProgram)getAllThePrograms().elementAt(j)).setScenarioID(getScenarioID());
				}
			}
		}
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
		retrieve();
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
		if(allThePrograms == null)
			allThePrograms = new Vector();
		return allThePrograms;
	}
	
	
	public void retrieve() throws java.sql.SQLException
	{
		super.retrieve();

		//retrieve all the programs for this scenario
		java.util.Vector progs = LMControlScenarioProgram.getAllProgramsForAScenario( getPAObjectID(), getDbConnection() );
		for( int i = 0; i < progs.size(); i++ )
			getAllThePrograms().add( progs.elementAt(i) );

			
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
	
	public void setAllThePrograms(Vector newVector)
	{
		allThePrograms = newVector;
	}
	/**
	* This method was created in VisualAge.
	*/
   public void update() throws java.sql.SQLException
   {
	   super.update();

	   //grab all the previous program entries for this scenario
	   java.util.Vector oldProgs = LMControlScenarioProgram.getAllProgramsForAScenario(getPAObjectID(), getDbConnection());
	
	   //unleash the power of the NestedDBPersistent
	   Vector comparedPrograms = CtiUtilities.NestedDBPersistentComparator(oldProgs, getAllThePrograms());

	   //throw the programs into the Db
	   for( int i = 0; i < comparedPrograms.size(); i++ )
	   {
		   ((LMControlScenarioProgram)comparedPrograms.elementAt(i)).setScenarioID( getPAObjectID() );
		   ((NestedDBPersistent)comparedPrograms.elementAt(i)).executeNestedOp();

	   }
   }
	
   public static Integer getDefaultGearID(Integer programID)
   {
		java.sql.Connection conn = null;
	
		conn = com.cannontech.database.PoolManager.getInstance().getConnection("yukon");

		Integer id = new Integer(0);
		
		try
		{
			id = com.cannontech.database.db.device.lm.LMProgramDirectGear.getDefaultGearID(programID, conn);
			conn.close();
		}
		catch (java.sql.SQLException e2)
		{
			e2.printStackTrace(); //something is up
		}
	return id;	
   }
	
}
