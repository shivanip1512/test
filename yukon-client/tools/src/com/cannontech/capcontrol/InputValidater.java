/*
 * Created on May 24, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.capcontrol;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.cannontech.clientutils.CTILogger;


/**
 * @author ASolberg
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class InputValidater
{
	public int from;
	public int to;
	public String route;
	public String banksize;
	public String manufacturer;
	public String switchType;
	public String conType;
	private final String TABLE_NAME = "DeviceCBC";
	public int getCBCCount = 0;
	public JFrame parent;
	
	public InputValidater(JFrame parentvar, int fromvar, int tovar, String routevar, String banksizevar, String manufacturervar, String switchTypevar, String conTypevar)
	{
		parent = parentvar;
		from = fromvar;
		to = tovar;
		route = routevar;
		banksize = banksizevar;
		manufacturer = manufacturervar;
		switchType = switchTypevar;
		conType = conTypevar;
	}
	
	public boolean checkRange() throws java.sql.SQLException
	{
		java.sql.Connection conn = 
			com.cannontech.database.PoolManager.getInstance().getConnection(
			com.cannontech.common.util.CtiUtilities.getDatabaseAlias() );
			
		java.sql.Statement stmt = null;
		java.sql.ResultSet rset = null;
		java.util.Vector devices = new java.util.Vector(5);
		String sql =
				"select * " +
				"from " + TABLE_NAME +
				" where SERIALNUMBER >= " + from +
				" and SERIALNUMBER <= " + to;
		try
		{
			stmt = conn.createStatement();
			rset = stmt.executeQuery( sql.toString() );
			
			while( rset.next() )
			{
				devices.add( rset.getString(1) );
			}
		}
		catch( java.sql.SQLException e )
		{
			CTILogger.error( e.getMessage(), e );
		}
		finally
		{
			try
			{
				if( stmt != null ) stmt.close();
				if( conn != null ) conn.close();
			}
			catch( java.sql.SQLException e2 )
			{
				CTILogger.error( e2.getMessage(), e2 );//something is up
			}
		}
		
		if( devices.size() > 0 )
		{
			JOptionPane.showMessageDialog(this.getParent(), "Some of the numbers in the specified serial range are used.", "Serial Range Conflict", JOptionPane.WARNING_MESSAGE);
			return false;
		}
		else if( conType.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]) )
		{
				
			if(new Integer(from).toString().length() != 9 || !new Integer(from).toString().startsWith("7", 0))
			{
				JOptionPane.showMessageDialog(this.getParent(), "A 7000 series CBC needs a nine digit serial number that begins with a 7", "Finish settings first", JOptionPane.WARNING_MESSAGE);
	
				return false;
			}
			if(new Integer(to).toString().length() != 9 || !new Integer(to).toString().startsWith("7", 0))
			{
				JOptionPane.showMessageDialog(this.getParent(), "A 7000 series CBC needs a nine digit serial number that begins with a 7", "Finish settings first", JOptionPane.WARNING_MESSAGE);
	
				return false;
			}
		}
		
		return true;
	}
	
	public JFrame getParent(){
		return parent;
	}
	
	public boolean checkRoute()
	{
		return true;
	}
	
	public boolean checkBankSize()
	{
		return true;
	}
	
	public boolean checkManufacturer()
	{
		return true;
	}
	
	public boolean checkSwitchType()
	{
		return true;
	}
	
	public boolean checkConType()
	{
		return true;
	}
	
	public int getFrom(){
		return from;
	}
	
	public int getTo(){
		return to;
	}
	
	public String getRoute(){
		return route;
	}
	
	public String getBankSize(){
		return banksize;
	}
	
	public String getSwitchMan(){
		return manufacturer;
	}
	
	public String getSwitchType(){
		return switchType;
	}
	
	public String getControllerType(){
		return conType;
	}
	
	public int getCBCCount(){
		return ((to - from)+1);
	}
}