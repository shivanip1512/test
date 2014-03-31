package com.cannontech.database.model;

/**
 * This type was created in VisualAge.
 */
import java.awt.Cursor;
import java.awt.Frame;
import java.util.concurrent.Executor;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.tree.TreePath;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.CustomerDao;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.spring.YukonSpringHook;


/* *******
 * This model is good now, it was slow.
 * 
 * 
*/
public class CICustomerTreeModel extends DbBackgroundTreeModel implements FrameAware {
private SwingWorker<Object, LiteCICustomer> worker;
private Frame frame;
/**
 * CICustomerTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public CICustomerTreeModel() {
	super( new DBTreeNode("CI Customers") );
}

@Override
public void setParentFrame(Frame frame) {
    this.frame = frame;
}

/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
@Override
public boolean insertTreeObject( LiteBase lb ) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode rootNode = (DBTreeNode) getRoot();
		
	if( lb instanceof LiteContact )
	{
		int contactID = ((LiteContact)lb).getContactID();

		LiteCICustomer ownerCst = YukonSpringHook.getBean(ContactDao.class).getOwnerCICustomer( contactID );

		rootNode = findLiteObject( null, ownerCst );

		if( rootNode != null )
		{

			//this will force us to reload ALL the Contacts for this CICustomer
			rootNode.setWillHaveChildren(true);
			TreePath rootPath = new TreePath( rootNode );
			treePathWillExpand( rootPath );

			updateTreeNodeStructure( rootNode );

			return true;
		}

	}
	else if ( lb instanceof LiteCICustomer )
	{
		DBTreeNode node = new DBTreeNode(lb);

		//add all new tree nodes to the top, for now
		int[] indexes = { 0 };
		
		rootNode.insert( node, indexes[0] );
		
		nodesWereInserted(rootNode,indexes );

		node.setWillHaveChildren(true);
		
		reload();

		return true;
	}

	return false;
}

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
@Override
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.CUSTOMER_CI
		  		 || liteType == com.cannontech.database.data.lite.LiteTypes.CONTACT );
}

@Override
public boolean isTreePrimaryForObject(LiteBase liteBase) {
    return liteBase.getLiteType() == com.cannontech.database.data.lite.LiteTypes.CUSTOMER_CI;
}

@Override
public synchronized void treePathWillExpand(javax.swing.tree.TreePath path)
{
	//Watch out, this reloads the contacts every TIME!!!
	DBTreeNode node = (DBTreeNode)path.getLastPathComponent();

	if( node.willHaveChildren() &&
		 node.getUserObject() instanceof LiteCICustomer )
	{
		LiteCICustomer ciCust = (LiteCICustomer)node.getUserObject();
		
		node.removeAllChildren();
		
		int primaryContactId = ciCust.getPrimaryContactID();
	    LiteContact primaryContact = YukonSpringHook.getBean(ContactDao.class).getContact(primaryContactId);
	    if (primaryContact != null) {
		    node.add(new DBTreeNode(primaryContact));
		}
		
		for (LiteContact contact : ciCust.getAdditionalContacts()) {
            node.add( new DBTreeNode(contact) );
        }
	}

	node.setWillHaveChildren(false);
}

/**
 * Insert the method's description here.
 * Creation date: (4/17/2002 1:58:45 PM)
 * @param lite com.cannontech.database.data.lite.LiteBase
 */
@Override
public boolean updateTreeObject(LiteBase lb) 
{
	if( lb == null || !isLiteTypeSupported(lb.getLiteType()) )
		return false;

	DBTreeNode node = findLiteObject( null, lb );

	if( node != null )
	{
		//slightyly different from the SUPER
		node.setWillHaveChildren( true );
		treePathWillExpand( new TreePath(node) );
		nodeStructureChanged( node );

		return true;			
	}

	return false;
}
/**
 * This method was created in VisualAge.
 */
@Override
public void doUpdate(final Runnable onCompletion) {
	frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    // if we have a worker, cancel it
    if (worker != null) {
        worker.cancel(false);
    }
    
    final DBTreeNode rootNode = (DBTreeNode) getRoot();
    rootNode.removeAllChildren();
    nodeStructureChanged(rootNode);

    final CustomerDao customerDao = YukonSpringHook.getBean(CustomerDao.class);
    
    final ProgressMonitor monitor = new ProgressMonitor(frame, "Loading contents", "---", 0, 0);

    worker = new SwingWorker<Object, LiteCICustomer>() {
        private int count = 0;
        private volatile int customerCount = 0;
        @Override
        protected Object doInBackground() throws Exception {
            customerCount = customerDao.getAllCiCustomerCount();
            customerDao.callbackWithAllCiCustomers(new SimpleCallback<LiteCICustomer>() {
                @Override
                public void handle(LiteCICustomer item) {
                    publish(item);
                }
            });
            return null;
        };
        
        @Override
        protected void process(java.util.List<LiteCICustomer> chunks) {
            // if the user canceled, there isn't much we can do about the background thread
            // but we can stop adding stuff to the model (and tying up memory)
            if (monitor.isCanceled() || isCancelled()) return;
            
            for (LiteCICustomer customer : chunks) {
                DBTreeNode custNode = new DBTreeNode(customer);
                rootNode.add( custNode );

                custNode.setWillHaveChildren(true);

                count++;
            }
            reload();
            
            // update monitor here on the event dispatch thread
            monitor.setMaximum(customerCount);
            monitor.setNote(count + " out of " + customerCount);
            monitor.setProgress(count);
            frame.setCursor(Cursor.getDefaultCursor());

        };
        
        @Override
        protected void done() {
            // this happens automatically, but if we cancel via the worker.cancel() method
            // we want to make sure the dialog goes away
            monitor.close();
            frame.setCursor(Cursor.getDefaultCursor());
            onCompletion.run();
        }
    };
    
    Executor executor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    executor.execute(worker);
}

}
