package com.cannontech.esub.editor.element;

import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.model.DeviceTreeModel;

/**
 * Creation date: (1/11/2002 11:43:34 AM)
 * @author: alauinger
 */
public class DeviceSelectionPanel extends JPanel implements TreeWillExpandListener {
    private JTree ivjDevicePointTree = null;
    private JScrollPane ivjJScrollPane1 = null;
    private DeviceTreeModel deviceTreeModel = null;
    
    /**
     * PointSelectionPanel constructor comment.
     */
    public DeviceSelectionPanel() {
        super();
        initialize();
    }
    /**
     * connEtoM1:  (PointSelectionPanel.initialize() --> DeviceTreeModel.update()V)
     */
    private void connEtoM1() {
        try {
            getDeviceTreeModel().update();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    /**
     * connPtoP1SetTarget:  (DeviceTreeModel.this <--> DevicePointTree.model)
     */
    private void connPtoP1SetTarget() {
        try {
            getDevicePointTree().setModel(getDeviceTreeModel());
            getDevicePointTree().addTreeWillExpandListener(this);
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }
    
    /**
     * Return the DevicePointTree property value.
     * @return javax.swing.JTree
     */
    private javax.swing.JTree getDevicePointTree() {
        if (ivjDevicePointTree == null) {
            try {
                ivjDevicePointTree = new javax.swing.JTree();
                ivjDevicePointTree.setName("DevicePointTree");
                ivjDevicePointTree.setModel(null);
                ivjDevicePointTree.setLargeModel(true);
                ivjDevicePointTree.setCellRenderer(new com.cannontech.common.gui.util.CtiTreeCellRenderer());
                ivjDevicePointTree.setBounds(0, 0, 317, 337);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjDevicePointTree;
    }
    
    /**
     * Return the DeviceTreeModel property value.
     * @return DeviceTreeModel
     */
    private DeviceTreeModel getDeviceTreeModel() {
        if (deviceTreeModel == null) {
            try {
                deviceTreeModel = new DeviceTreeModel(false);
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return deviceTreeModel;
    }
    
    /**
     * Creation date: (1/14/2002 12:38:10 PM)
     * @return javax.swing.JTree
     */
    javax.swing.JTree getIvjDevicePointTree() {
        return ivjDevicePointTree;
    }
    
    /**
     * Return the JScrollPane1 property value.
     * @return javax.swing.JScrollPane
     */
    private javax.swing.JScrollPane getJScrollPane1() {
        if (ivjJScrollPane1 == null) {
            try {
                com.cannontech.common.gui.util.TitleBorder ivjLocalBorder;
                ivjLocalBorder = new com.cannontech.common.gui.util.TitleBorder();
                ivjLocalBorder.setTitle("Select a point");
                ivjJScrollPane1 = new javax.swing.JScrollPane();
                ivjJScrollPane1.setName("JScrollPane1");
                ivjJScrollPane1.setBorder(ivjLocalBorder);
                getJScrollPane1().setViewportView(getDevicePointTree());
            } catch (java.lang.Throwable ivjExc) {
                handleException(ivjExc);
            }
        }
        return ivjJScrollPane1;
    }
    
    /**
     * Returns the selected point.
     * @return com.cannontech.database.data.lite.LitePoint
     */
    public LitePoint getSelectedPoint() {
        javax.swing.tree.TreePath path = getDevicePointTree().getSelectionPath();
        return getSelectedPoint(path);
            
    }
    
    /**
     * Returns the selected point.
     * @return LiteYukonPAObject
     */
    public LiteYukonPAObject getSelectedDevice() {
        TreePath path = getDevicePointTree().getSelectionPath();
        return getSelectedDevice(path);
            
    }
    
    /**
     * Returns all the selected points
     * @return
     */
    public LitePoint[] getSelectedPoints() {
        TreePath[] paths = getDevicePointTree().getSelectionPaths();
        if(paths == null) {
            return new LitePoint[0];
        }
        
        ArrayList<LitePoint> selectedPoints = new ArrayList<LitePoint>(paths.length);
        for(int i = 0; i < paths.length; i++) {
            LitePoint pt = getSelectedPoint(paths[i]);
            if(pt != null) {
                selectedPoints.add(pt);
            }
        }
        
        LitePoint[] retPts = new LitePoint[selectedPoints.size()];
        selectedPoints.toArray(retPts);
        return retPts;
    }
    
    /**
     * Return the selected point at the end of the given path
     * @param path
     * @return
     */
    private LitePoint getSelectedPoint(TreePath path) {
        if( path != null )  {
            javax.swing.tree.DefaultMutableTreeNode node = (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
            Object selected = node.getUserObject();
            if( selected instanceof LitePoint ) {
                return (LitePoint) node.getUserObject();
            }
        }
        return null;
    }
    
    /**
     * Return the selected point at the end of the given path
     * @param path
     * @return
     */
    private LiteYukonPAObject getSelectedDevice(TreePath path) {
        if( path != null )  {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            Object selected = node.getUserObject();
            if( selected instanceof LiteYukonPAObject ) {
                return (LiteYukonPAObject) node.getUserObject();
            }
        }
        return null;
    }
    
    /**
     * Called whenever the part throws an exception.
     * @param exception java.lang.Throwable
     */
    private void handleException(java.lang.Throwable exception) {
        CTILogger.error(exception.getMessage(), exception);
    }
    
    /**
     * Initializes connections
     * @exception java.lang.Exception The exception description.
     */
    private void initConnections() throws java.lang.Exception {
        connPtoP1SetTarget();
    }

    /**
     * Initialize the class.
     */
    private void initialize() {
        try {
            setName("PointSelectionPanel");
            setLayout(new java.awt.BorderLayout());
            setSize(361, 141);
            add(getJScrollPane1(), "Center");
            initConnections();
            connEtoM1();
        } catch (java.lang.Throwable ivjExc) {
            handleException(ivjExc);
        }
    }

    /**
     * Creation date: (12/18/2001 4:36:53 PM)
     * @param path javax.swing.tree.TreePath
     * @param point com.cannontech.database.data.lite.LitePoint
     */
    public boolean selectPoint(LitePoint point) {
        return selectPoint(point, true);
    }
    
    private boolean selectPoint(LitePoint point, boolean clear) {
        if(clear) {
            getDevicePointTree().clearSelection();
        }
        TreePath rootPath = new TreePath( getDeviceTreeModel().getRoot() );
        TreePath paoPath = findPAO(rootPath, point.getPaobjectID());
        
        if( paoPath != null ) {
            getDevicePointTree().expandPath(paoPath);
            return selectPoint(paoPath, point);
        }
    
        return false;   
    }
    
    /**
     * Creation date: (12/18/2001 4:36:53 PM)
     * @param path javax.swing.tree.TreePath
     * @param point com.cannontech.database.data.lite.LitePoint
     */
    private boolean selectPoint(TreePath path, LitePoint point) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    
        Object o = node.getUserObject();
        if( o instanceof LitePoint && ((LitePoint) o).getPointID() == point.getPointID()) {
            getDevicePointTree().getSelectionModel().addSelectionPath( path );
            getDevicePointTree().scrollPathToVisible(path);
            return true;
        }
        else 
        if( node.isLeaf() ) {
                return false;
        }
        else {
            for( int i = 0; i < node.getChildCount(); i++ )
            {
                Object nextPathObjs[] = new Object[path.getPath().length +1];
    
                System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );
    
                nextPathObjs[path.getPath().length] = node.getChildAt(i);
                
                TreePath nextPath = new TreePath(nextPathObjs);
                
                if( selectPoint(nextPath,point) )
                    return true;    
            }
            return false;
        }
    }
    
    public void selectPoints(LitePoint[] points) {
        getDevicePointTree().clearSelection();
        for(int i = 0; i < points.length; i++) {
            selectPoint(points[i], false);  
        }
    }
    
    private TreePath findPAO(TreePath path, int paoID) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
    
        Object o = node.getUserObject();
        if( o instanceof LiteYukonPAObject && ((LiteYukonPAObject) o).getYukonId() == paoID) {
            //getDevicePointTree().getSelectionModel().addSelectionPath( path );
            return path;
        }
        else 
        if( node.isLeaf() ) {
            return null;
        }
        else {
            for( int i = 0; i < node.getChildCount(); i++ )
            {
                Object nextPathObjs[] = new Object[path.getPath().length +1];
    
                System.arraycopy( path.getPath(), 0, nextPathObjs, 0, path.getPath().length );
    
                nextPathObjs[path.getPath().length] = node.getChildAt(i);
                
                TreePath nextPath = new TreePath(nextPathObjs);
                
                if( (nextPath = findPAO(nextPath, paoID)) != null)
                    return nextPath;
            }
    
            return null;
        }           
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(TreeExpansionEvent)
     */
    public void treeWillCollapse(TreeExpansionEvent event)
        throws ExpandVetoException {            
    }

    /**
     * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(TreeExpansionEvent)
     */
    public void treeWillExpand(TreeExpansionEvent event)
        throws ExpandVetoException {
            getDeviceTreeModel().treePathWillExpand(event.getPath());
    }
    
    /**
     * refresh the point tree while maintaining, if possible, any selected points.
     */
    public void refresh() {
        LitePoint[] lp = getSelectedPoints();
        getDeviceTreeModel().update();
        if(lp != null)
            selectPoints(lp);
    }
}
