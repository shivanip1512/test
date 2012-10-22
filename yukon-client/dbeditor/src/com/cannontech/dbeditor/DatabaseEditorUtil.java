package com.cannontech.dbeditor;

import java.awt.Container;
import java.util.Set;
import java.util.concurrent.Executor;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.springframework.dao.DataAccessException;

import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.Attribute;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.MessageEvent;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.device.CCUBase;
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
    private static final PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
    private static final DeviceDao deviceDao = YukonSpringHook.getBean("deviceDao", DeviceDao.class);
    private static final AttributeService attributeService = YukonSpringHook.getBean("attributeService", AttributeService.class);
    private static final DBPersistentDao dbPersistentDao = YukonSpringHook.getBean("dbPersistentDao", DBPersistentDao.class);
    private static final PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
    private static final DeviceConfigurationDao configurationDao = YukonSpringHook.getBean("deviceConfigurationDao", DeviceConfigurationDao.class);
    
    private DatabaseEditorUtil() {
        
    }
    
    /**
     * Checks that the device config assigned to the specified device is valid. If it is not, the
     * config is removed from that device.
     * @return True if a config was removed, otherwise false.
     * @throws InvalidDeviceTypeException if the config cannot be unassigned due to the device type.
     */
    public static boolean unassignDeviceConfigIfInvalid(int paoId) throws InvalidDeviceTypeException {
        YukonDevice device = deviceDao.getYukonDevice(paoId);
        
        ConfigurationBase config = configurationDao.findConfigurationForDevice(device);
        if(config != null) {
            PaoTag configTag = config.getType().getSupportedDeviceTag();
            boolean configSupported = paoDefinitionDao.isTagSupported(device.getPaoIdentifier().getPaoType(), configTag);
            if(!configSupported) {
                configurationDao.unassignConfig(device);
                return true;
            }
        }
        return false;
    }
    
    public static boolean isDisconnectCollarCompatible(final Object object){
        if (object instanceof YukonPAObject) {
            YukonPAObject yukonPaobject = (YukonPAObject)object;
            PaoType paoType = PaoType.getForDbString(yukonPaobject.getPAOType());
            return paoDefinitionDao.isTagSupported(paoType, PaoTag.DISCONNECT_COLLAR_COMPATIBLE);
        } else {
            return false;
        }
    }
    
    public static void updateDisconnectStatus(final DatabaseEditor dbEditor, final JComponent c, final Object object) {
        threadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                JCheckBox checkBox = DatabaseEditorUtil.findJComponent(c, "JCheckBoxEnableDisconnect", JCheckBox.class);
                JTextField textField = DatabaseEditorUtil.findJComponent(c, "JTextFieldDisconnectAddress", JTextField.class);
                
                if (checkBox == null || textField == null) return;
                
                final YukonPAObject paoObject = (YukonPAObject) object;
                SimpleDevice device = deviceDao.getYukonDevice(paoObject.getPAObjectID());
                
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
            @Override
            public void run() {
                dbEditor.viewMenuRefreshAction();
            }
        });
    }
    
    public static DBPersistent getDisconnectStatusPointForDevice(final SimpleDevice device) {
        final Attribute att = BuiltInAttribute.DISCONNECT_STATUS;
        AttributeService attributeService = YukonSpringHook.getBean(AttributeService.class);
        LitePoint litePoint = attributeService.getPointForAttribute(device, att);
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
        if (expectedType.isInstance(c) && 
            c.getName() != null && c.getName().equals(name)) {
            return expectedType.cast(c);
        }
        return null;
    }
    
    public static boolean showUpdateRouteName(final Object object) {
        return (object instanceof RepeaterBase || object instanceof CCUBase);
    }
    
    public static void updateRouteName(final DatabaseEditor dbEditor, final JComponent c, final DBPersistent object) {
        threadExecutor.execute(new Runnable() {
            @Override
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
                        @Override
                        public void run() {
                            int result = JOptionPane.showConfirmDialog(
                                                                       CtiUtilities.getParentFrame(c), 
                                                                       message,
                                                                       title,
                                                                       JOptionPane.YES_NO_OPTION,
                                                                       JOptionPane.INFORMATION_MESSAGE);
                            if (result == JOptionPane.YES_OPTION) {
                                threadExecutor.execute(new Runnable () {
                                    @Override
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
    
    /**
     * Returns true if yukonPaobject supports at least one of the paoTags, otherwise false.
     */
    public static boolean isTagSupported(final YukonPAObject yukonPAObject, Set<PaoTag> paoTags){
        PaoType paoType = PaoType.getForDbString(yukonPAObject.getPAOType());
        for (PaoTag paoTag : paoTags) {
            if (paoDefinitionDao.isTagSupported(paoType, paoTag)) {
                return true;
            }
        }
        return false;
    }
}