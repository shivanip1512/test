package com.cannontech.mbean;

import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;

//import org.jboss.naming.NonSerializableFactory;

/**
 * @author rneuharth
 *
 * Base class for MBeans implemented by CTI.
 */
public abstract class CTIMBeanBase implements CTIMBeanBaseMBean
{
	private String jndiName;
	private boolean started;

	/**
	 * Constructor for CTIMBeanBase.
	 */
	public CTIMBeanBase() 
	{
		super();
System.out.println("------------created...");
		
	}

	public String getJndiName() 
	{
		return jndiName;
	}

	public void setJndiName(String jndiName) throws NamingException 
	{
System.out.println("------------setJndiName..." );
		
		String oldName = this.jndiName;
		this.jndiName = jndiName;
		if (started) 
		{
			unbind(oldName);
			try 
			{
				rebind();
			} 
			catch (Exception e) 
			{
				NamingException ne =
					new NamingException("Failed to update jndiName");
				ne.setRootCause(e);
				throw ne;
			}
		}
	}
	
	public void start() throws Exception 
	{
System.out.println("------------start...");
		
		started = true;
		rebind();
	}

	public void stop() 
	{
System.out.println("------------stop...");
		
		started = false;
		unbind(jndiName);
	}

	private void rebind() throws NamingException 
	{
System.out.println("------------rebind...");

		InitialContext rootCtx = new InitialContext();
		Name fullName = rootCtx.getNameParser("").parse(jndiName);
		System.out.println("fullName=" + fullName);

		//JBOSS SPECIFIC CODE!!!!
//		NonSerializableFactory.rebind(fullName, this, true);
	}

	private void unbind(String jndiName) 
	{
System.out.println("------------unbind...");
		
		try 
		{
			InitialContext rootCtx = new InitialContext();
			rootCtx.unbind(jndiName);
			
			//JBOSS SPECIFIC CODE!!!!
//			NonSerializableFactory.unbind(jndiName);
		}
		catch (NamingException e) 
		{
			e.printStackTrace();
		}
	}

}
