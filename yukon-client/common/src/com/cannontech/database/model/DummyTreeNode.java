package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (7/5/2001 2:57:20 PM)
 * @author: 
 */
public class DummyTreeNode extends DBTreeNode
{
/**
 * DeviceNodePointType constructor comment.
 */
public DummyTreeNode(Object userObject) {
	super(userObject);
}
/**
 * Insert the method's description here.
 * Creation date: (11/17/00 4:28:38 PM)
 * @return boolean
 */
public boolean equals(Object o)
{
	return( o != null
			  && o instanceof DummyTreeNode
			  && ((DummyTreeNode)o).toString().equals(this.toString()) );
}
}
