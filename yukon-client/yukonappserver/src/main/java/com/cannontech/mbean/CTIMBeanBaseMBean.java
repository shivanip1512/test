package com.cannontech.mbean;

import javax.naming.NamingException;

/**
 * @author rneuharth
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface CTIMBeanBaseMBean {

	public String getJndiName(); 
	public void setJndiName(String jndiName) throws NamingException; 
	public void start() throws Exception; 
	public void stop();	

}
