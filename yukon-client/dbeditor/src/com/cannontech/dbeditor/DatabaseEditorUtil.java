package com.cannontech.dbeditor;

import java.util.concurrent.Executor;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.CCUBase;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public final class DatabaseEditorUtil {
    private static final Executor threadExecutor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    
    private DatabaseEditorUtil() {
        
    }
    
    public static boolean showUpdateRouteName(final Object o) {
        return (o instanceof RepeaterBase || o instanceof CCUBase);
    }
    
    public static void updateRouteName(final JComponent c, final DBPersistent object) {
        threadExecutor.execute(new Runnable() {
            public void run() {
                final PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
                final YukonPAObject paoObj = (YukonPAObject) object;

                int id = paoObj.getPAObjectID();
                String oldName = paoDao.getLiteYukonPAO(id).getPaoName();

                if (oldName.equals(paoObj.getPAOName())) return;
                
                try {
                    final LiteYukonPAObject liteRoutePAObject = paoDao.getLiteYukonPAObject(
                                                                                            oldName,
                                                                                            PAOGroups.CAT_ROUTE,
                                                                                            PAOGroups.CLASS_ROUTE,
                                                                                            RouteTypes.ROUTE_CCU);

                    final String title = "Update Route Name Confirmation";
                    final String message = "Update Route name " + oldName + " to " + paoObj.getPAOName();
                    
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            int result = JOptionPane.showConfirmDialog(
                                                                       CtiUtilities.getParentFrame(c), 
                                                                       message,
                                                                       title,
                                                                       JOptionPane.YES_NO_OPTION,
                                                                       JOptionPane.INFORMATION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                threadExecutor.execute(new Runnable () {
                                    public void run() {
                                        DBPersistentDao dbDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
                                        DBPersistent routePAObject = dbDao.retrieveDBPersistent(liteRoutePAObject);
                                        ((YukonPAObject) routePAObject).setPAOName(paoObj.getPAOName());
                                        dbDao.performDBChange(routePAObject, Transaction.UPDATE);
                                    }
                                });
                            }
                        }
                    });
                    
                } catch (NotFoundException ignore) {
                    javax.swing.JOptionPane.showConfirmDialog(
                                                              CtiUtilities.getParentFrame(c),
                                                              "No identical route names were found for " + oldName + ".  No Route Names were updated.",
                                                              "Information",
                                                              JOptionPane.DEFAULT_OPTION,
                                                              JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
    }

}
