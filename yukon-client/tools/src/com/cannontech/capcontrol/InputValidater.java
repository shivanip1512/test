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
	public boolean command = false;
	
	public InputValidater(JFrame parentvar, boolean debug, int fromvar, int tovar, String routevar,String conTypevar, String banksizevar, String manufacturervar, String switchTypevar)
	{
		parent = parentvar;
		command = debug;
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
			if(command)
			{
				CTILogger.error("Some of the numbers in the specified serial range are used.");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Some of the numbers in the specified serial range are used.", "Serial Range Validation Failed", JOptionPane.WARNING_MESSAGE);
			}			
			return false;
		}
		else if( conType.equalsIgnoreCase(com.cannontech.database.data.pao.PAOGroups.STRING_CBC_7010[0]) )
		{
				
			if(new Integer(from).toString().length() != 9 || !new Integer(from).toString().startsWith("7", 0))
			{
				if(command)
				{
					CTILogger.error("A 7000 series CBC needs a nine digit serial number that begins with a 7");
				}else
				{
					JOptionPane.showMessageDialog(this.getParent(), "A 7000 series CBC needs a nine digit serial number that begins with a 7", "Serial Range Validation Failed", JOptionPane.WARNING_MESSAGE);
				}
	
				return false;
			}
			if(new Integer(to).toString().length() != 9 || !new Integer(to).toString().startsWith("7", 0))
			{
				JOptionPane.showMessageDialog(this.getParent(), "A 7000 series CBC needs a nine digit serial number that begins with a 7", "Serial Range Validation Failed", JOptionPane.WARNING_MESSAGE);
	
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
		if(true)
		{
			return true;
		}else
		{
			if(command)
			{
				CTILogger.error("Route Validatoin Failed");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Route Validatoin Failed", "Route Validation Failed", JOptionPane.WARNING_MESSAGE);
			}
			
			return false;
		}
		
	}
	
	public boolean checkBankSize()
	{
		if(true)
		{
			return true;
		}else
		{
			if(command)
			{
				CTILogger.error("Bank Size Validatoin Failed");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Bank Size Validatoin Failed", "Bank Size Validation Failed", JOptionPane.WARNING_MESSAGE);
			}
			
			return false;
		}
		
	}
	
	public boolean checkManufacturer()
	{
		if(true)
		{
			return true;
		}else
		{
			if(command)
			{
				CTILogger.error("Manufacturer Validatoin Failed");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Manufacturer Validatoin Failed", "Manufacturer Validation Failed", JOptionPane.WARNING_MESSAGE);
			}
			
			return false;
		}
		
	}
	
	public boolean checkSwitchType()
	{
		if(true)
		{
			return true;
		}else
		{
			if(command)
			{
				CTILogger.error("Switch Type Validatoin Failed");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Switch Type Validatoin Failed", "Switch Type Validation Failed", JOptionPane.WARNING_MESSAGE);
			}
			
			return false;
		}
		
	}
	
	public boolean checkConType()
	{
		if(true)
		{
			return true;
		}else
		{
			if(command)
			{
				CTILogger.error("Controller Type Validatoin Failed");
			}else
			{
				JOptionPane.showMessageDialog(this.getParent(), "Controller Type Validatoin Failed", "Controller Type Validation Failed", JOptionPane.WARNING_MESSAGE);
			}
			
			return false;
		}
		
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