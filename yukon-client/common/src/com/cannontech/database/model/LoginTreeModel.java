package com.cannontech.database.model;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.concurrent.Executor;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.UserUtils;

/**
 * This type was created in VisualAge.
 */
public class LoginTreeModel extends DbBackgroundTreeModel implements FrameAware 
{
private Frame frame;
private SwingWorker<Object, LiteYukonUser> worker;
/**
 * MeterTreeModel constructor comment.
 * @param root javax.swing.tree.TreeNode
 */
public LoginTreeModel() 
{
	super( new DBTreeNode("Users") );
}

@Override
    public void setParentFrame(Frame frame) {
        this.frame = frame;
    }

/**
 * Insert the method's description here.
 * Creation date: (4/22/2002 2:05:03 PM)
 * @return com.cannontech.database.data.lite.LiteBase[]
 */
public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.YUKON_USER );
}
/**
 * This method was created in VisualAge.
 */
public synchronized void doUpdate(final Runnable onCompletion) {
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    
    // if we have a worker, cancel it
    if (worker != null) {
        worker.cancel(false);
    }
    
    final DBTreeNode rootNode = (DBTreeNode) getRoot();
    rootNode.removeAllChildren();
    nodeStructureChanged(rootNode);

    final ProgressMonitor monitor = new ProgressMonitor(frame, "Loading contents", "", 0, 0);

    final YukonUserDao yukonUserDao = DaoFactory.getYukonUserDao();
    worker = new SwingWorker<Object, LiteYukonUser>() {
        private int count = 0;
        private volatile int userCount = 0;
        protected Object doInBackground() throws Exception {
            userCount = yukonUserDao.getAllYukonUserCount();
            yukonUserDao.callbackWithAllYukonUsers(new SimpleCallback<LiteYukonUser>() {
                public void handle(LiteYukonUser item) {
                    publish(item);
                }
            });
            return null;
        };
        
        protected void process(java.util.List<LiteYukonUser> chunks) {
            // if the user canceled, there isn't much we can do about the background thread
            // but we can stop adding stuff to the model (and tying up memory)
            if (monitor.isCanceled() || isCancelled()) return;
            
            for (LiteYukonUser user : chunks) {
                DBTreeNode userNode = new DBTreeNode(user);

                userNode.setIsSystemReserved(user.getUserID() < 0 );
                if (user.getUserID() > UserUtils.USER_DEFAULT_ID) {
                    rootNode.add( userNode );
                    
                }
                count++;
            }
            reload();
            
            // update monitor here on the event dispatch thread
            monitor.setMaximum(userCount);
            monitor.setNote(count + " out of " + userCount);
            monitor.setProgress(count);
            frame.setCursor(null);
        };
        
        @Override
        protected void done() {
            // this happens automatically, but if we cancel via the worker.cancel() method
            // we want to make sure the dialog goes away
            monitor.close();
            frame.setCursor(null);
            onCompletion.run();
        }
    };
    
    Executor executor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    executor.execute(worker);
    
}
}
