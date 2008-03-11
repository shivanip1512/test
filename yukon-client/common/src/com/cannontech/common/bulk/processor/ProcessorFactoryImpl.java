package com.cannontech.common.bulk.processor;

import java.util.Collection;
import java.util.Collections;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataRetrievalFailureException;

import com.cannontech.common.bulk.collection.EditableDevice;
import com.cannontech.common.device.YukonDevice;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.model.ConfigurationBase;
import com.cannontech.common.device.definition.dao.DeviceDefinitionDao;
import com.cannontech.common.device.definition.model.DeviceDefinition;
import com.cannontech.common.device.definition.service.DeviceDefinitionService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.PersistenceException;

/**
 * Default implementation of ProcessorFactory
 */
public class ProcessorFactoryImpl implements ProcessorFactory {

    private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao = null;
    private DeviceConfigurationDao deviceConfigurationDao = null;
    private DeviceDefinitionService deviceDefinitionService = null;
    private DeviceDefinitionDao deviceDefinitionDao = null;
    private DeviceDao deviceDao = null;

    public void setDeviceGroupMemberEditorDao(
            DeviceGroupMemberEditorDao deviceGroupMemberEditorDao) {
        this.deviceGroupMemberEditorDao = deviceGroupMemberEditorDao;
    }

    public void setDeviceConfigurationDao(
            DeviceConfigurationDao deviceConfigurationDao) {
        this.deviceConfigurationDao = deviceConfigurationDao;
    }

    public void setDeviceDefinitionService(
            DeviceDefinitionService deviceDefinitionService) {
        this.deviceDefinitionService = deviceDefinitionService;
    }

    public void setDeviceDefinitionDao(DeviceDefinitionDao deviceDefinitionDao) {
        this.deviceDefinitionDao = deviceDefinitionDao;
    }

    public void setDeviceDao(DeviceDao deviceDao) {
        this.deviceDao = deviceDao;
    }

    public Processor<YukonDevice> createAddYukonDeviceToGroupProcessor(
            final StoredDeviceGroup group) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {
                deviceGroupMemberEditorDao.addDevices(group, devices);
            }
        };
    }

    public Processor<YukonDevice> createAssignConfigurationToYukonDeviceProcessor(
            final ConfigurationBase configuration) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {
                deviceConfigurationDao.assignConfigToDevices(configuration,
                                                             devices);
            }
        };
    }

    public Processor<YukonDevice> createDeleteDeviceProcessor() {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {

                for (YukonDevice device : devices) {

                    try {
                        deviceDao.removeDevice(device);
                    } catch (PersistenceException e) {
                        throw new ProcessingException("Could not delete device",
                                                      e);
                    }
                }
            }
        };
    }

    public Processor<YukonDevice> createEnableDisableProcessor(
            final boolean enable) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {

                for (YukonDevice device : devices) {

                    try {
                        if (enable) {
                            deviceDao.enableDevice(device);
                        } else {
                            deviceDao.disableDevice(device);
                        }
                    } catch (DataAccessException e) {
                        throw new ProcessingException("Could not " + ((enable) ? "enable"
                                                              : "disable") + " device with id: " + device.getDeviceId(),
                                                      e);
                    }
                }
            }
        };
    }

    public Processor<YukonDevice> createUpdateRouteProcessor(final int routeId) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {

                for (YukonDevice device : devices) {
                    System.out.println("Updated route to: " + routeId + " for device: " + device.getDeviceId());
                    // TODO update route
                }
            }
        };
    }

    public Processor<YukonDevice> createChangeTypeProcessor(final int type) {

        return new Processor<YukonDevice>() {

            public void process(YukonDevice device) throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<YukonDevice> devices)
                    throws ProcessingException {

                DeviceDefinition deviceDefinition = deviceDefinitionDao.getDeviceDefinition(type);

                for (YukonDevice device : devices) {

                    try {
                        deviceDefinitionService.changeDeviceType(device,
                                                                 deviceDefinition);
                    } catch (DataRetrievalFailureException e) {
                        throw new ProcessingException("Could not find device with id: " + device.getDeviceId(),
                                                      e);
                    } catch (PersistenceException e) {
                        throw new ProcessingException("Could not change device type for device with id: " + device.getDeviceId(),
                                                      e);
                    }
                }
            }
        };
    }

    public Processor<EditableDevice> createEditDeviceProcessor() {
        return new Processor<EditableDevice>() {

            public void process(EditableDevice device)
                    throws ProcessingException {
                process(Collections.singletonList(device));
            }

            public void process(Collection<EditableDevice> devices)
                    throws ProcessingException {

                for (EditableDevice device : devices) {
                    System.out.println("Edited device: " + device.getName());
                    // TODO edit device
                }
            }
        };
    }
}
