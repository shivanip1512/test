package com.cannontech.dbeditor;

import java.awt.Container;
import java.util.concurrent.Executor;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.attribute.model.Attribute;
import com.cannontech.common.device.attribute.model.BuiltInAttribute;
import com.cannontech.common.device.attribute.service.AttributeService;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.PointTemplate;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.CCUBase;
import com.cannontech.database.data.device.MCT410CL;
import com.cannontech.database.data.device.MCT410FL;
import com.cannontech.database.data.device.MCT410GL;
import com.cannontech.database.data.device.MCT410IL;
import com.cannontech.database.data.device.RepeaterBase;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.RouteTypes;
import com.cannontech.database.data.pao.YukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public final class DatabaseEditorUtil {
    private static final Executor threadExecutor = YukonSpringHook.getBean("globalScheduledExecutor", Executor.class);
    private static final DeviceDefinitionDao deviceDefinitionDao = YukonSpringHook.getBean("deviceDefinitionDao", DeviceDefinitionDao.class);
    private static final DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao", DeviceDao.class);
    private static final AttributeService attributeService = YukonSpringHook.getBean("attributeService", AttributeService.class);
    private static final PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
    private static final DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
    private static final PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    
    private DatabaseEditorUtil() {
        
    }
    
    public static boolean isMCT410Series(final Object object) {
        return ((object instanceof MCT410CL) ||
                (object instanceof MCT410FL) ||
                (object instanceof MCT410GL) ||
                (object instanceof MCT410IL));
    }
    
    public static void updateDisconnectStatus(final DatabaseEditor dbEditor, final JComponent c, final Object object) {
        threadExecutor.execute(new Runnable() {
            public void run() {
                JCheckBox checkBox = DatabaseEditorUtil.findJComponent(c, "JCheckBoxEnableDisconnect", JCheckBox.class);
                JTextField textField = DatabaseEditorUtil.findJComponent(c, "JTextFieldDisconnectAddress", JTextField.class);
                
                if (checkBox == null || textField == null) return;
                
                final YukonPAObject paoObject = (YukonPAObject) object;
                YukonDevice device = deviceDao.getYukonDevice(paoObject.getPAObjectID());
                
                if (checkBox.isSelected()) {
                    
                    if (!attributeService.pointExistsForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS)) {
                        try {
                            attributeService.createPointForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS);
                            DBPersistent point = DatabaseEditorUtil.getDisconnectStatusPointForDevice(device);
                            String messageString = point + " inserted successfully into the database.";
                            dbEditor.fireMessage(new MessageEvent( this, messageString) );
                            DatabaseEditorUtil.doViewMenuRefreshAction(dbEditor);
                        } catch (DataAccessException e) {
                            dbEditor.fireMessage(new MessageEvent(this, e.getMessage()));
                        }
                    }
                    
                } else {
                    
                    if (attributeService.pointExistsForAttribute(device, BuiltInAttribute.DISCONNECT_STATUS)) {
                        DBPersistent point = DatabaseEditorUtil.getDisconnectStatusPointForDevice(device);
                        try {
                            dbPersistentDao.performDBChange(point, Transaction.DELETE);
                            dbEditor.fireMessage(new MessageEvent(this, point + " deleted successfully from the database."));
                            DatabaseEditorUtil.doViewMenuRefreshAction(dbEditor);
                        } catch (PersistenceException e) {
                            dbEditor.fireMessage(new MessageEvent(this, e.getMessage()));
                        }
                    }
                    
                }
                
            }
        });
    }
    
    public static void doViewMenuRefreshAction(final DatabaseEditor dbEditor) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dbEditor.viewMenuRefreshAction();
            }
        });
    }
    
    public static DBPersistent getDisconnectStatusPointForDevice(final YukonDevice device) {
        final Attribute att = BuiltInAttribute.DISCONNECT_STATUS;
        PointTemplate template = deviceDefinitionDao.getPointTemplateForAttribute(device, att);
        LitePoint litePoint = pointDao.getLitePointIdByDeviceId_Offset_PointType(device.getDeviceId(), template.getOffset(), template.getType());
        DBPersistent point = dbPersistentDao.retrieveDBPersistent(litePoint);
        return point;
    }
    
    public static <E> E findJComponent(final Container c, final String name, final Class<E> expectedType) {
        for (int x = 0; x < c.getComponentCount(); x++) {
            JComponent comp = (JComponent) c.getComponent(x);
            if ((comp instanceof JPanel) || (comp instanceof JTabbedPane)) {
                E result = findJComponent(comp, name, expectedType);
                if (result != null) return result;
            }
            E result = matchJComponent(comp, name, expectedType);
            if (result != null) return result;
        }
        return null;
    }
    
    public static <E> E matchJComponent(final JComponent c, final String name, final Class<E> expectedType) {
        if (expectedType.isInstance(c) && c.getName().equals(name)) {
            return expectedType.cast(c);
        }
        return null;
    }
    
    public static boolean showUpdateRouteName(final Object object) {
        return (object instanceof RepeaterBase || object instanceof CCUBase);
    }
    
    public static void updateRouteName(final DatabaseEditor dbEditor, final JComponent c, final DBPersistent object) {
        threadExecutor.execute(new Runnable() {
            public void run() {
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
                                        DBPersistent routePAObject = dbPersistentDao.retrieveDBPersistent(liteRoutePAObject);
                                        ((YukonPAObject) routePAObject).setPAOName(paoObj.getPAOName());
                                        try {
                                            dbPersistentDao.performDBChange(routePAObject, Transaction.UPDATE);
                                            String messageString = object + " updated successfully in the database.";
                                            dbEditor.fireMessage(new MessageEvent( this, messageString) );
                                            DatabaseEditorUtil.doViewMenuRefreshAction(dbEditor);
                                        } catch (PersistenceException e) {
                                            dbEditor.fireMessage(new MessageEvent(this, e.getMessage()));
                                        }
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
