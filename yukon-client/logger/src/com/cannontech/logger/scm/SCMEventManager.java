package com.cannontech.logger.scm;

/*
 * SCMEventManager.java	1.0 (5 May 2000)
 */

import java.util.*;

public class SCMEventManager extends Vector
{
	private static SCMEventManager h = null;


private SCMEventManager()
{
	super();
}
public void addSCMEventListener(SCMEventListener l)
{
	if (!contains(l)) 
		add(l);
}
public void dispatchSCMEvent(int eventID)
{

	SCMEvent event = new SCMEvent(eventID);
	
	for(int i=0; i < size(); i++)
	{
		Object obj = get(i);

		if (null == obj) 
			continue;

		((SCMEventListener)obj).handleSCMEvent(event);
	}

}
public static SCMEventManager getInstance()
{
	if(null == h)
	{
		h = new SCMEventManager();
	}
	
	return h;
}
public void removeSCMEventListener(SCMEventListener l)
{
	remove(l);
}
}
