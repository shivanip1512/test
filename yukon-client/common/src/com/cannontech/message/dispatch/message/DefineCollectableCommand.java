package com.cannontech.message.dispatch.message;

/**
 * This type was created in VisualAge.
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableCommand extends com.cannontech.message.util.DefineCollectableMessage implements com.roguewave.vsj.DefineCollectable {
	private static final int classId = 1530;
/**
 * DefineCollectableCommand constructor comment.
 */
public DefineCollectableCommand() {
	super();
}
/**
 * create method comment.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
	return new Command();
}
/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() {
	return new Comparator() {
	  public int compare(Object x, Object y) {
			if( x == y )
				return 0;
			else
				return -1;
	  }
	};
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	return classId;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId() {
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	return Command.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.restoreGuts( obj, vstr, polystr );

	Command cmd = (Command) obj;
	
	cmd.setOperation( vstr.extractInt() );
	cmd.setOpString( (String) vstr.restoreObject(SimpleMappings.CString));

	int count = vstr.extractInt(); //get the vector element count
	
	for( int i = 0; i < count; i++ )
	{
		cmd.getOpArgList().addElement( new Integer(vstr.extractInt()) );		
	}
	
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts(obj, vstr, polystr );

	Command cmd = (Command) obj;

	vstr.insertInt( cmd.getOperation() );
	vstr.saveObject( cmd.getOpString(), SimpleMappings.CString );
//	vstr.saveObject( cmd.getOpArgList(), polystr );

	int count = cmd.getOpArgList().size();	
	vstr.insertInt( count ); //send the vector element count

	for( int i = 0; i < count; i++ )
	{
		vstr.insertInt( Integer.parseInt(cmd.getOpArgList().elementAt( i ).toString()) );
	}

}
}
