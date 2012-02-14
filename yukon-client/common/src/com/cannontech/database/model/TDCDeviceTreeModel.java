package com.cannontech.database.model;

import java.util.Vector;

import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.yukon.IDatabaseCache;

/**
 * Insert the type's description here.
 * Creation date: (4/18/00 11:39:02 AM)
 * @author: 
 * @Version: <version>
 */
public class TDCDeviceTreeModel extends AbstractDeviceTreeModel
{

	
/**
 * NoEmptyDeviceTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public TDCDeviceTreeModel() 
{
	super( true, new DBTreeNode("Object Types") );
}
/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 4:11:23 PM)
 * @param deviceType int
 */
public boolean isDeviceValid( PaoCategory paoCategory, PaoClass paoClass, PaoType paoType )
{
	return true;
}

protected synchronized java.util.List getCacheList(IDatabaseCache cache ) 
{
    return cache.getAllYukonPAObjects();
}

// Override me if you want a sub class to do something different.
protected synchronized void runUpdate() 
{
	DBTreeNode rootNode = (DBTreeNode)getRoot();
	Vector typeList = new Vector(32);

	IDatabaseCache cache = DefaultDatabaseCache.getInstance();
	synchronized (cache)
	{
		java.util.List paos = getCacheList(cache);
		java.util.Collections.sort( paos, LiteComparators.litePaoTypeComparator );

		rootNode.removeAllChildren();
		
		int currType = Integer.MIN_VALUE;
		DummyTreeNode devTypeNode = null;
		for( int i = 0; i < paos.size(); i++ )
		{
			LiteYukonPAObject litPAO = (LiteYukonPAObject)paos.get(i);

    		if( currType != litPAO.getPaoType().getDeviceTypeId() ) {
    			devTypeNode = new DummyTreeNode(litPAO.getPaoType().getDbString());
    			typeList.add( devTypeNode );
    		}

    		DBTreeNode deviceNode = getNewNode(litPAO);
    		devTypeNode.add(deviceNode);
    		deviceNode.setWillHaveChildren(true);

    		currType = litPAO.getPaoType().getDeviceTypeId();

		} //for loop		
	} //synch

	//this list will be a fixed size with a controlled max value
	java.util.Collections.sort( typeList, DummyTreeNode.comparator);
	for( int i = 0; i < typeList.size(); i++ )
		rootNode.add( (DummyTreeNode)typeList.get(i) );

	reload();	
}

}
