package com.cannontech.web.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.database.cache.functions.PAOFuncs;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class JSFTreeUtils {

    public static TreeNode createPAOTreeFromPointList(Set points, TreeNode root) {
        TreeNode rootData = root;

        HashMap deviceMap = new HashMap(100);

        for (Iterator iter = points.iterator(); iter.hasNext();) {
            LitePoint litePoint = (LitePoint) iter.next();
			LiteYukonPAObject device = PAOFuncs.getLiteYukonPAO(litePoint.getPaobjectID());            

            TreeNodeBase newParent = null;
            TreeNodeBase leaf = new TreeNodeBase("points",
                                                 litePoint.getPointName(),
                                                 String.valueOf(litePoint.getPointID()),
                                                 true);
            if (!deviceMap.containsKey(device)) {
                newParent = new TreeNodeBase( // type, description, leaf
                                             "paos",
                                             device.getPaoName(),
                                             String.valueOf(litePoint.getPaobjectID()),
                                             false);

                deviceMap.put(device, newParent);
                rootData.getChildren().add(newParent);

            } else {
                // don't create a device
                // just attach a point in the root data for this device
                // get the old tree node
                newParent = (TreeNodeBase) deviceMap.get(device);
            }
            newParent.getChildren().add(leaf);

        }
        Collections.sort(rootData.getChildren(), JSFComparators.treeNodeDescriptionComparator);
        return rootData;
    }
    
    
	//function that will create a tree from the node and the list - basically attach points to the node
	public static TreeNode createTreeFromPointList(Set points, TreeNode root){
        for (Iterator iter = points.iterator(); iter.hasNext();) {
            LitePoint litePoint = (LitePoint) iter.next();
            
            TreeNodeBase leaf = new TreeNodeBase("points",
                                                 litePoint.getPointName(),
                                                 String.valueOf(litePoint.getPointID()),
                                                 true);
            root.getChildren().add(leaf);
        }
        return root;
    }

}
