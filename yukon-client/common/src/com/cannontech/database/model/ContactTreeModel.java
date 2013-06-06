package com.cannontech.database.model;

import java.awt.Cursor;
import java.awt.Frame;
import java.util.concurrent.Executor;

import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;

import com.cannontech.common.util.SimpleCallback;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.spring.YukonSpringHook;


/**
 * This type was created in VisualAge.
 */
public class ContactTreeModel extends DbBackgroundTreeModel implements FrameAware
{
private SwingWorker<Object, LiteContact> worker;
private Frame frame;

public ContactTreeModel() {
	super( new DBTreeNode("Contacts") );
}

@Override
public void setParentFrame(Frame frame) {
    this.frame = frame;
}

public boolean isLiteTypeSupported( int liteType )
{
	return ( liteType == com.cannontech.database.data.lite.LiteTypes.CONTACT );
}

public void doUpdate(final Runnable onCompletion) {
    frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    // if we have a worker, cancel it
    if (worker != null) {
        worker.cancel(false);
    }
    
    final DBTreeNode rootNode = (DBTreeNode) getRoot();
    rootNode.removeAllChildren();
    nodeStructureChanged(rootNode);

    final ProgressMonitor monitor = new ProgressMonitor(frame, "Loading contents", "", 0, 0);

    final ContactDao contactDao = YukonSpringHook.getBean(ContactDao.class);
    worker = new SwingWorker<Object, LiteContact>() {
        private int count = 0;
        private volatile int contactCount = 0;
        protected Object doInBackground() throws Exception {
            contactCount = contactDao.getAllContactCount();
            contactDao.callbackWithAllContacts(new SimpleCallback<LiteContact>() {
                public void handle(LiteContact item) {
                    publish(item);
                }
            });
            return null;
        };
        
        protected void process(java.util.List<LiteContact> chunks) {
            // if the user canceled, there isn't much we can do about the background thread
            // but we can stop adding stuff to the model (and tying up memory)
            if (monitor.isCanceled() || isCancelled()) return;
            
            for (LiteContact contact : chunks) {
                DBTreeNode userNode = new DBTreeNode(contact);

                rootNode.add( userNode );
                count++;
            }
            reload();
            
            // update monitor here on the event dispatch thread
            monitor.setMaximum(contactCount);
            monitor.setNote(count + " out of " + contactCount);
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
