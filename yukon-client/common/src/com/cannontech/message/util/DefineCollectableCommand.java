package com.cannontech.message.util;

/**
 * This type was created in VisualAge.
 */
import java.util.ArrayList;
import java.util.List;

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
    List<Integer> opArgList = new ArrayList<Integer>(count);
	
	for( int i = 0; i < count; i++ ) {
        opArgList.add(vstr.extractInt());
	}
	
    cmd.setOpArgList(opArgList);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	super.saveGuts(obj, vstr, polystr );

	Command cmd = (Command) obj;

	vstr.insertInt( cmd.getOperation() );
	vstr.saveObject( cmd.getOpString(), SimpleMappings.CString );

    List<Integer> opArgList = cmd.getOpArgList();
	vstr.insertInt(opArgList.size()); //send the vector element count

    for (Integer arg : opArgList) {
        vstr.insertInt(arg);
    }
}
}
