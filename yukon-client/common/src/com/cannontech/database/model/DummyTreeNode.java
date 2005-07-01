package com.cannontech.database.model;

/**
 * Insert the type's description here.
 * Creation date: (7/5/2001 2:57:20 PM)
 * @author: 
 */
public class DummyTreeNode extends DBTreeNode
{
	/**
	 * Compares the String value of the UserObject inside the node.
	 *  
	 */
	public static java.util.Comparator comparator = new java.util.Comparator()
	{
		public int compare(Object o1, Object o2)
		{
			if( ((DummyTreeNode)o1).getUserObject() == null
				|| ((DummyTreeNode)o2).getUserObject() == null )
				return 0;

			String thisName = ((DummyTreeNode)o1).getUserObject().toString();
			String anotherName = ((DummyTreeNode)o2).getUserObject().toString();

			return( thisName.compareToIgnoreCase(anotherName) );			
		}

		public boolean equals(Object obj)
		{
			return false;
		}
	};


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
