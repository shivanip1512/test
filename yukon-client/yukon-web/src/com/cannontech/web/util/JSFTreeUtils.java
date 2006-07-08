package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;

public class JSFTreeUtils {

    public static TreeNode createPAOTreeFromPointList(Collection points, TreeNode root, LiteYukonUser user) {
        TreeNode rootData = root;

        HashMap deviceMap = new HashMap(100);

        for (Iterator iter = points.iterator(); iter.hasNext();) {
            LitePoint litePoint = (LitePoint) iter.next();
			LiteYukonPAObject device = DaoFactory.getPaoDao().getLiteYukonPAO(litePoint.getPaobjectID());            
			
			if (device != null) {
	            TreeNodeBase newParent = null;
	            TreeNodeBase leaf = new TreeNodeBase("points",
	                                                 litePoint.getPointName(),
	                                                 String.valueOf(litePoint.getPointID()),
	                                                 true);
	            boolean userHasAccessPAO = DaoFactory.getAuthDao().userHasAccessPAO(user, device.getLiteID());
				if ((!deviceMap.containsKey(device)) && userHasAccessPAO){
	               
					
	            	newParent = new TreeNodeBase( // type, description, leaf
	                                             "paos",
	                                             device.getPaoName(),
	                                             String.valueOf(litePoint.getPaobjectID()),
	                                             false);
	
	                deviceMap.put(device, newParent);
	                rootData.getChildren().add(newParent);
	
	            } 
	            if (userHasAccessPAO) {
	                // don't create a device
	                // just attach a point in the root data for this device
	                // get the old tree node
	                newParent = (TreeNodeBase) deviceMap.get(device);
	            }
        
				if (newParent != null)
	            	newParent.getChildren().add(leaf);

			}
        }
        splitTree(rootData, 100, "sublevels");
        Collections.sort(rootData.getChildren(), JSFComparators.treeNodeDescriptionComparator);        
        return rootData;
    }
    
    //function that will create a 3-lvel tree from 2-level tree if the number of leaves in
    //2-level tree is > then parameter passed
    public static TreeNode splitTree (TreeNode root, int leavesPerLevel,  String leafCat) {
    	//see if the parent level is saturated
    	List allPaos = root.getChildren();
    	List removeList = new ArrayList (10);
    	List addList = new ArrayList (10);
    	
    	TreeNode pao = null;
    	TreeNode newPAO = null;
    	for (Iterator iter = allPaos.iterator(); iter.hasNext();) {
			pao = (TreeNodeBase) iter.next();			
			newPAO = new TreeNodeBase (pao.getType(), pao.getDescription(), pao.getIdentifier(), false);
			if (pao.getChildCount() > leavesPerLevel) {
				for (int i=0; i < leavesPerLevel; i++) {
					int level = i * leavesPerLevel;
					TreeNodeBase category = new TreeNodeBase(leafCat, "", "" + level, false);
					String catName = "";
					for (int j=level; j < level + leavesPerLevel; j++) {
						if (j < pao.getChildCount()) {	
							TreeNodeBase point = (TreeNodeBase) pao.getChildren().get(j);	
								if (j == level)
									catName += point.getDescription();
								if  (j == (level + leavesPerLevel) - 1 )
									catName += " - "+point.getDescription();
							category.getChildren().add( new TreeNodeBase (point.getType(), 
																			point.getDescription(), 
																			point.getIdentifier(), true));
							removeList.add(point);
						
						}
					}
					
					category.setDescription(catName);
					if (category.getChildCount() > 0)
						newPAO.getChildren().add(category);
					
				}
		
				}
			
			if (newPAO.getChildCount() > 0) {
				removeList.add(pao);
				addList.add(newPAO);
	    	}
    	}
    	root.getChildren().removeAll(removeList);
    	root.getChildren().addAll(addList);
    	return root;
    	
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
