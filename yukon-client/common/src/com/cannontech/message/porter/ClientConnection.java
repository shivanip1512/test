package com.cannontech.message.porter;

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
 * This method was created in VisualAge.
 */
protected void registerMappings(CollectableStreamer polystreamer) {
	super.registerMappings(polystreamer);

	com.roguewave.vsj.DefineCollectable[] mappings = CollectableMappings.getMappings();
	
	for( int i = 0; i < mappings.length; i++ )
	{
		polystreamer.register( mappings[i] );
	}
}
}
