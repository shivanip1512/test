package com.cannontech.message.dispatch;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.vsj.CollectableStreamer;

public class ClientConnection extends com.cannontech.message.util.ClientConnection {
/**
 * ClientConnection constructor comment.
 */
public ClientConnection() {
	super();
}
/**
 * ClientConnection constructor comment.
 * @param host java.lang.String
 * @param port int
 */
public ClientConnection(String host, int port) {
	super(host, port);
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/00 9:59:56 AM)
 * @param o java.lang.Object
 */
public void doHandleMessage(Object o)
{
	// Only instances of com.cannontech.message.dispatch.message.Command should
	// get here and it should have a ARE_YOU_THERE operation ... see handleMessage
	// echo it back so vangogh doesn't time out on us
	write(o);
}
/**
 * Insert the method's description here.
 * Creation date: (8/10/00 9:59:09 AM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean handleMessage(Object o) {
   return ( o instanceof com.cannontech.message.dispatch.message.Command &&
	      ((com.cannontech.message.dispatch.message.Command) o).getOperation() 
	      					== com.cannontech.message.dispatch.message.Command.ARE_YOU_THERE );
}
/**
 * This method was created in VisualAge.
 */
protected void registerMappings(CollectableStreamer polystreamer) {
	super.registerMappings(polystreamer);

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();
	
	for( int i = 0; i < mappings.length; i++ )
	{
		polystreamer.register( mappings[i] );
	}

//	polystreamer.register()
}
}
