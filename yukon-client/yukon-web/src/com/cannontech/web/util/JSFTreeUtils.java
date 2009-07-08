package com.cannontech.web.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.myfaces.custom.tree2.TreeNode;
import org.apache.myfaces.custom.tree2.TreeNodeBase;

import com.cannontech.database.data.lite.LitePoint;

public class JSFTreeUtils {
    
    /**
     * Function that will create a 3-lvel tree from 2-level tree if the number of leaves in
     * 2-level tree is > then parameter passed
     */
    @SuppressWarnings("unchecked")
    public static TreeNode splitTree (TreeNode root, int leavesPerLevel,  String leafCat) {
    	List<Object> allPaos = root.getChildren();
    	List<Object> removeList = new ArrayList<Object>(10);
    	List<Object> addList = new ArrayList<Object>(10);
    	TreeNode pao = null;
    	TreeNode newPAO = null;
    	for (Iterator<Object> iter = allPaos.iterator(); iter.hasNext();) {
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
							if (j == level) {
								catName += point.getDescription();
							}
							if  (j == (level + leavesPerLevel) - 1 ) {
								catName += " - "+point.getDescription();
							}
							category.getChildren().add( new TreeNodeBase (point.getType(), point.getDescription(), point.getIdentifier(), true));
							removeList.add(point);
						}
					}
					
					category.setDescription(catName);
					if (category.getChildCount() > 0) {
						newPAO.getChildren().add(category);
					}
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
    
	/**
	 * Function that will create a tree from the node and the list - basically attach points to the node
	 * @param points
	 * @param root
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public static TreeNode createTreeFromPointList(TreeSet<LitePoint> points, TreeNode root){
        for (Iterator<LitePoint> iter = points.iterator(); iter.hasNext();) {
            LitePoint litePoint = iter.next();
            
            TreeNodeBase leaf = new TreeNodeBase("points", litePoint.getPointName(), String.valueOf(litePoint.getPointID()), true);
            root.getChildren().add(leaf);
        }
        return root;
    }

}
