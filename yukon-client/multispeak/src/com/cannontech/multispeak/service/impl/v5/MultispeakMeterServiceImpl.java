package com.cannontech.multispeak.service.impl.v5;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.cannontech.amr.deviceread.dao.DeviceAttributeReadCallback;
import com.cannontech.amr.deviceread.dao.DeviceAttributeReadService;
import com.cannontech.amr.deviceread.dao.WaitableDeviceAttributeReadCallback;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.MeterDao;
import com.cannontech.amr.meter.model.PlcMeter;
import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.amr.meter.model.YukonMeter;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.model.RfnMeter;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectCallback;
import com.cannontech.amr.rfn.service.RfnMeterDisconnectService;
import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService;
import com.cannontech.common.bulk.service.ChangeDeviceTypeService.ChangeDeviceTypeInfo;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigHelper;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.device.commands.CommandRequestDevice;
import com.cannontech.common.device.commands.CommandRequestDeviceExecutor;
import com.cannontech.common.device.commands.impl.CommandCallbackBase;
import com.cannontech.common.device.creation.DeviceCreationException;
import com.cannontech.common.device.creation.DeviceCreationService;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupEditorDao;
import com.cannontech.common.device.groups.editor.dao.DeviceGroupMemberEditorDao;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.editor.model.StoredDeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.service.CommandCompletionCallbackAdapter;
import com.cannontech.common.device.service.DeviceUpdateService;
import com.cannontech.common.events.loggers.MultispeakEventLogService;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.exception.InsufficientMultiSpeakDataException;
import com.cannontech.common.model.Route;
import com.cannontech.common.model.Substation;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.PaoUtils;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.model.RfnDevice;
import com.cannontech.core.dao.DeviceDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PointDao;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.PointValueHolder;
import com.cannontech.core.dynamic.PointValueQualityHolder;
import com.cannontech.core.roleproperties.MspPaoNameAliasEnum;
import com.cannontech.core.roleproperties.MultispeakMeterLookupFieldEnum;
import com.cannontech.core.substation.dao.SubstationDao;
import com.cannontech.core.substation.dao.SubstationToRouteMappingDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.OutageStatus;
import com.cannontech.database.db.point.stategroup.PointStateHelper;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfCDStateChange;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfEndDeviceState;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfFormattedBlock;
import com.cannontech.msp.beans.v5.commonarrays.ArrayOfMeterReading;
import com.cannontech.msp.beans.v5.commonarrays.ObjectFactory;
import com.cannontech.msp.beans.v5.commontypes.ErrorObject;
import com.cannontech.msp.beans.v5.commontypes.ExtensionsItem;
import com.cannontech.msp.beans.v5.commontypes.ExtensionsList;
import com.cannontech.msp.beans.v5.commontypes.MeterID;
import com.cannontech.msp.beans.v5.commontypes.ObjectRef;
import com.cannontech.msp.beans.v5.enumerations.EndDeviceStateKind;
import com.cannontech.msp.beans.v5.enumerations.EndDeviceStateType;
import com.cannontech.msp.beans.v5.enumerations.LoadActionCode;
import com.cannontech.msp.beans.v5.enumerations.LoadActionCodeKind;
import com.cannontech.msp.beans.v5.enumerations.RCDStateKind;
import com.cannontech.msp.beans.v5.enumerations.ServiceKind;
import com.cannontech.msp.beans.v5.multispeak.CDDeviceIdentifier;
import com.cannontech.msp.beans.v5.multispeak.CDStateChange;
import com.cannontech.msp.beans.v5.multispeak.ConnectDisconnectEvent;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeter;
import com.cannontech.msp.beans.v5.multispeak.ElectricMeterExchange;
import com.cannontech.msp.beans.v5.multispeak.ElectricServicePoint;
import com.cannontech.msp.beans.v5.multispeak.EndDeviceState;
import com.cannontech.msp.beans.v5.multispeak.FormattedBlock;
import com.cannontech.msp.beans.v5.multispeak.MeterGroup;
import com.cannontech.msp.beans.v5.multispeak.MeterReading;
import com.cannontech.msp.beans.v5.multispeak.ObjectDeletion;
import com.cannontech.msp.beans.v5.multispeak.ServiceLocation;
import com.cannontech.msp.beans.v5.not_server.CDStatesChangedNotification;
import com.cannontech.msp.beans.v5.not_server.EndDeviceStatesNotification;
import com.cannontech.msp.beans.v5.not_server.FormattedBlockNotification;
import com.cannontech.msp.beans.v5.not_server.MeterReadingsNotification;
import com.cannontech.multispeak.block.v5.Block;
import com.cannontech.multispeak.client.MultispeakDefines;
import com.cannontech.multispeak.client.MultispeakVendor;
import com.cannontech.multispeak.client.core.v5.NOTClient;
import com.cannontech.multispeak.client.v5.MultispeakFuncs;
import com.cannontech.multispeak.dao.v5.FormattedBlockProcessingService;
import com.cannontech.multispeak.dao.v5.FormattedBlockUpdater;
import com.cannontech.multispeak.dao.v5.FormattedBlockUpdaterChain;
import com.cannontech.multispeak.dao.v5.MeterReadProcessingService;
import com.cannontech.multispeak.dao.v5.MeterReadUpdater;
import com.cannontech.multispeak.dao.v5.MeterReadUpdaterChain;
import com.cannontech.multispeak.dao.v5.MspMeterDao;
import com.cannontech.multispeak.dao.v5.MspObjectDao;
import com.cannontech.multispeak.data.v5.MspErrorObjectException;
import com.cannontech.multispeak.data.v5.MspLoadActionCode;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceClientException;
import com.cannontech.multispeak.exceptions.MultispeakWebServiceException;
import com.cannontech.multispeak.service.MultispeakMeterServiceBase;
import com.cannontech.multispeak.service.v5.MultispeakMeterService;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.user.UserUtils;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.BasicServerConnection;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSetMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;

public class MultispeakMeterServiceImpl extends MultispeakMeterServiceBase implements MultispeakMeterService {

    private static final Logger log = YukonLogManager.getLogger(MultispeakMeterServiceImpl.class);

    private BasicServerConnection porterConnection;

    @Autowired private AttributeService attributeService;
    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private ChangeDeviceTypeService changeDeviceTypeService;
    @Autowired private CommandRequestDeviceExecutor commandRequestDeviceExecutor;
    @Autowired private ConfigurationSource configurationSource;
    @Autowired private DeviceAttributeReadService deviceAttributeReadService;
    @Autowired private DeviceCreationService deviceCreationService;
    @Autowired private DeviceDao deviceDao;
    @Autowired private DeviceGroupEditorDao deviceGroupEditorDao;
    @Autowired private DeviceGroupMemberEditorDao deviceGroupMemberEditorDao;
    @Autowired private DeviceUpdateService deviceUpdateService;
    @Autowired private GlobalSettingDao globalSettingDao;
    @Autowired private MeterDao meterDao;
    @Autowired private MspMeterDao mspMeterDao;
    @Autowired private MspObjectDao mspObjectDao;
    @Autowired private MultispeakEventLogService multispeakEventLogService;
    @Autowired private MultispeakFuncs multispeakFuncs;
    @Autowired private SubstationDao substationDao;
    @Autowired private SubstationToRouteMappingDao substationToRouteMappingDao;
    @Autowired private PointDao pointDao;
    @Autowired private PaoDefinitionDao paoDefinitionDao;
    @Autowired private RfnDeviceDao rfnDeviceDao;
    @Autowired private RfnMeterDisconnectService rfnMeterDisconnectService;
    @Autowired private TransactionTemplate transactionTemplate;
    @Autowired private MeterReadProcessingService meterReadProcessingService;
    @Autowired private NOTClient notClient;
    @Autowired private ObjectFactory objectFactory;

    private static final String EXTENSION_DEVICE_TEMPLATE_STRING = "AMRMeterType";

    private ImmutableSet<EndDeviceStateKind> supportedEndDeviceStateTypes;
    private ImmutableSetMultimap<EndDeviceStateKind, Integer> outageConfig;

    // Strings to represent MultiSpeak method calls, generally used for logging.
    private static final String METER_CHANGED_STRING = "MetersChangedNotification";
    private static final String METER_CREATED_STRING = "MetersCreatedNotification";
    private static final String METER_DELETED_STRING = "MetersDeletedNotification";
    private static final String METER_EXCHANGED_STRING = "MetersExchangedNotification";
    private static final String METER_INSTALLED_STRING = "MetersInstalledNotification";
    private static final String METER_UNINSTALL_STRING = "MetersUninstalledNotification";
    private static final String SERV_LOC_CHANGED_STRING = "ServiceLocationsChangedNotification";

    /**
     * Get the static instance of Multispeak (this) object.
     */
    @PostConstruct
    public void initialize() throws Exception {
        log.info("New MSP instance created");

        ConfigurationSource configurationSource = MasterConfigHelper.getConfiguration();
        Builder<EndDeviceStateKind, Integer> builder = ImmutableMultimap.builder();
        builder.putAll(EndDeviceStateKind.OUTAGED, 20, 57, 72);
        builder.putAll(EndDeviceStateKind.STARTING_UP, 1, 17, 74, 0); // TODO Not sure for replacement of
                                                                      // Restoration(in MSP 3) with
                                                                      // STARTING_UP. Please confirm
        ImmutableMultimap<EndDeviceStateKind, Integer> systemDefault = builder.build();

        supportedEndDeviceStateTypes =
            ImmutableSet.of(EndDeviceStateKind.OUTAGED, EndDeviceStateKind.NO_RESPONSE, EndDeviceStateKind.STARTING_UP,
                EndDeviceStateKind.IN_SERVICE, EndDeviceStateKind.DEFECTIVE, EndDeviceStateKind.OUTOF_SERVICE,
                EndDeviceStateKind.SHUTTING_DOWN);

        SetMultimap<EndDeviceStateKind, Integer> outageConfigTemp = HashMultimap.create(systemDefault);
        for (EndDeviceStateKind deviceState : supportedEndDeviceStateTypes) {
            String valueStr =
                configurationSource.getString("MSP_OUTAGE_EVENT_TYPE_CONFIG_" + deviceState.value().toUpperCase());
            if (valueStr != null) {
                int[] errorCodes =
                    com.cannontech.common.util.StringUtils.parseIntStringAfterRemovingWhitespace(valueStr);
                List<Integer> errorCodeList = Arrays.asList(ArrayUtils.toObject(errorCodes));
                outageConfigTemp.values().removeAll(errorCodeList);
                outageConfigTemp.putAll(deviceState, errorCodeList);
            }
        }

        outageConfig = ImmutableSetMultimap.copyOf(outageConfigTemp);
        log.info("outage event configuation: " + outageConfig);
    }

    @Override
    public List<ErrorObject> metersCreated(MultispeakVendor mspVendor, List<ElectricMeter> electricCreatedMeters)
            throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (final ElectricMeter mspMeter : electricCreatedMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_CREATED_STRING);
                        try {
                            checkForExistingMeterAndUpdate(mspMeter, mspVendor, METER_CREATED_STRING);
                        } catch (NotFoundException e) { // and NEW meter
                            addNewMeter(mspMeter, mspVendor, METER_CREATED_STRING);
                        }
                        // TODO Decide if the meter shall be enabled or disabled when created
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getDisplayString(), METER_CREATED_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Exception: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_CREATED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_CREATED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Error: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_CREATED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_CREATED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            }
        }// end for

        return errorObjects;
    }

    @Override
    public List<ErrorObject> metersDeleted(MultispeakVendor mspVendor, List<ObjectDeletion> electricMeters) {
        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        for (ObjectDeletion mspMeter : electricMeters) {
            // Lookup meter in Yukon by msp meter number
            YukonMeter meter;
            try {
                meter = getMeterByMeterNumber(mspMeter.getObjectRef().getPrimaryIdentifierValue());

                deviceDao.removeDevice(meter);
                multispeakEventLogService.removeDevice(meter.getMeterNumber(), meter, METER_DELETED_STRING,
                    mspVendor.getCompanyName());

            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(mspMeter.getObjectRef().getPrimaryIdentifierValue(),
                    METER_DELETED_STRING, mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(mspMeter.getObjectRef().getValue(), "MeterNumber",
                        "ObjectDeletion", METER_DELETED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_DELETED_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            }
        }

        return errorObjects;
    }

    @Override
    public List<ErrorObject> metersUninstalled(MultispeakVendor mspVendor, List<ElectricMeter> electricUninstalledMeters)
            throws MultispeakWebServiceException {
        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        for (ElectricMeter mspMeter : electricUninstalledMeters) {
            // Lookup meter in Yukon by msp meter number
            YukonMeter meter;
            try {
                meter = getMeterByMeterNumber(mspMeter);

                // Added meter to Inventory
                addToGroup(meter, SystemGroupEnum.INVENTORY, METER_UNINSTALL_STRING, mspVendor);
                if (!meter.isDisabled()) {// enabled
                    meter.setDisabled(true); // update local object reference
                    deviceDao.disableDevice(meter);
                    multispeakEventLogService.disableDevice(meter.getMeterNumber(), meter, METER_UNINSTALL_STRING,
                        mspVendor.getCompanyName());
                }
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(mspMeter.getPrimaryIdentifier().getValue(),
                    METER_UNINSTALL_STRING, mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(mspMeter.getPrimaryIdentifier().getValue().trim(),
                        "MeterNumber", "ElectricMeter", METER_UNINSTALL_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_UNINSTALL_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            }
        }

        return errorObjects;
    }

    @Override
    public List<ErrorObject> metersInstalled(final MultispeakVendor mspVendor, List<ElectricMeter> addMeters)
            throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (final ElectricMeter mspMeter : addMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_INSTALLED_STRING);

                        YukonMeter newMeter;
                        try {
                            newMeter = checkForExistingMeterAndUpdate(mspMeter, mspVendor, METER_INSTALLED_STRING);
                        } catch (NotFoundException e) { // and NEW meter
                            // TODO - need to decide if "installed" meter doesn't already exist, should it be
                            // created or should exception be thrown?
                            newMeter = addNewMeter(mspMeter, mspVendor, METER_INSTALLED_STRING);
                        }

                        removeFromGroup(newMeter, SystemGroupEnum.INVENTORY, METER_INSTALLED_STRING, mspVendor);

                        // TODO Consider moving this stuff out of this transaction....requesting
                        // serviceLocaiton could fail and shouldn't roll back the rest of this.
                        ServiceLocation mspServiceLocation =
                            mspObjectDao.getMspServiceLocation(mspMeter.getPrimaryIdentifier().getValue(), mspVendor);

                        // update the billing group from CIS billingCyle
                        if (mspServiceLocation != null) {
                            // TODO we are getting billingCycle from ElectricMeter instead of
                            // serviceLocation.Is it correct way to get this information.
                            String billingCycle = mspMeter.getBillingCycle();
                            updateBillingCyle(billingCycle, newMeter.getMeterNumber(), newMeter,
                                METER_INSTALLED_STRING, mspVendor);
                            // TODO is it necessary to make call for CB server to get the information related
                            // to alt group
                            updateAltGroup(mspServiceLocation, newMeter.getMeterNumber(), newMeter,
                                METER_INSTALLED_STRING, mspVendor);
                        } else {
                            multispeakEventLogService.objectNotFoundByVendor(
                                mspMeter.getPrimaryIdentifier().getValue(), "GetServiceLocationsByMeterIDs",
                                METER_INSTALLED_STRING, mspVendor.getCompanyName());
                        }

                        // Must complete route locate after meter is enabled
                        verifyAndUpdateSubstationGroupAndRoute(newMeter, mspVendor, mspMeter, mspServiceLocation,
                            METER_INSTALLED_STRING);
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getDisplayString(), METER_INSTALLED_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Exception: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_INSTALLED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_INSTALLED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Error: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_INSTALLED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_INSTALLED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            }
        }// end for

        return errorObjects;
    }

    @Override
    public List<ErrorObject> metersExchanged(final MultispeakVendor mspVendor,
            List<ElectricMeterExchange> exchangeMeters) throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (final ElectricMeterExchange electricMeterExchange : exchangeMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        if (electricMeterExchange.getExistingMeter() != null
                            && electricMeterExchange.getReplacementMeter() != null) {
                            YukonMeter existingMeter = null;

                            try {
                                existingMeter = getMeterByMeterNumber(electricMeterExchange.getExistingMeter());
                            } catch (NotFoundException e) {
                                multispeakEventLogService.meterNotFound(
                                    electricMeterExchange.getExistingMeter().getPrimaryIdentifier().getValue(),
                                    METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                                ErrorObject err =
                                    mspObjectDao.getNotFoundErrorObject(
                                        electricMeterExchange.getExistingMeter().getPrimaryIdentifier().getValue().trim(),
                                        "MeterNumber", "ElectricMeter", METER_EXCHANGED_STRING,
                                        mspVendor.getCompanyName());
                                errorObjects.add(err);
                                multispeakEventLogService.errorObject(err.getDisplayString(), METER_EXCHANGED_STRING,
                                    mspVendor.getCompanyName());
                                log.error(e);
                            }
                            if (existingMeter != null) {
                                try {
                                    validateMspMeter(electricMeterExchange.getReplacementMeter(), mspVendor,
                                        METER_EXCHANGED_STRING);
                                    YukonMeter replacementMeter;

                                    try {
                                        replacementMeter =
                                            checkForExistingMeterAndUpdate(electricMeterExchange.getReplacementMeter(),
                                                mspVendor, METER_EXCHANGED_STRING);
                                    } catch (NotFoundException e) { // and NEW meter
                                        replacementMeter =
                                            addNewMeter(electricMeterExchange.getReplacementMeter(), mspVendor,
                                                METER_EXCHANGED_STRING);
                                    }

                                    addToGroup(existingMeter, SystemGroupEnum.INVENTORY, METER_EXCHANGED_STRING,
                                        mspVendor);
                                    removeFromGroup(replacementMeter, SystemGroupEnum.INVENTORY,
                                        METER_EXCHANGED_STRING, mspVendor);

                                    if (!existingMeter.isDisabled()) {// enabled
                                        existingMeter.setDisabled(true); // update local object reference
                                        deviceDao.disableDevice(existingMeter);
                                        multispeakEventLogService.disableDevice(existingMeter.getMeterNumber(),
                                            existingMeter, METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                                    }

                                    // TODO Consider moving this stuff out of this transaction....requesting
                                    // serviceLocaiton could fail and shouldn't roll back the rest of this.
                                    ServiceLocation mspServiceLocation =
                                        mspObjectDao.getMspServiceLocation(
                                            electricMeterExchange.getReplacementMeter().getPrimaryIdentifier().getValue(),
                                            mspVendor);

                                    // update the billing group from CIS billingCyle
                                    if (mspServiceLocation != null) {
                                        // TODO we are getting billingCycle from ElectricMeter instead of
                                        // serviceLocation.Is it correct way to get this information.
                                        String billingCycle =
                                            electricMeterExchange.getReplacementMeter().getBillingCycle();
                                        updateBillingCyle(billingCycle, replacementMeter.getMeterNumber(),
                                            replacementMeter, METER_EXCHANGED_STRING, mspVendor);
                                        // TODO is it necessary to make call for CB server to get the
                                        // information related to alt group
                                        updateAltGroup(mspServiceLocation, replacementMeter.getMeterNumber(),
                                            replacementMeter, METER_EXCHANGED_STRING, mspVendor);
                                    } else {
                                        multispeakEventLogService.objectNotFoundByVendor(
                                            electricMeterExchange.getReplacementMeter().getPrimaryIdentifier().getValue(),
                                            "GetServiceLocationsByMeterIDs", METER_EXCHANGED_STRING,
                                            mspVendor.getCompanyName());
                                    }

                                    // Must complete route locate after meter is enabled
                                    verifyAndUpdateSubstationGroupAndRoute(replacementMeter, mspVendor,
                                        electricMeterExchange.getReplacementMeter(), mspServiceLocation,
                                        METER_EXCHANGED_STRING);
                                } catch (MspErrorObjectException e) {
                                    errorObjects.add(e.getErrorObject());
                                    multispeakEventLogService.errorObject(e.getErrorObject().getDisplayString(),
                                        METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                                    log.error(e);
                                }
                            }
                        } else {
                            // TODO Is it necessary to sent the error object if both meters are not
                            // present.(Used referableID in error object)
                            // As Not sure which objectID could be used in error object
                            ErrorObject err =
                                mspObjectDao.getErrorObject(electricMeterExchange.getReferableID(),
                                    "Requires both meters(existingMeter and replacementMeter)", "ElectricMeter",
                                    METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                            errorObjects.add(err);
                            multispeakEventLogService.errorObject(err.getDisplayString(), METER_EXCHANGED_STRING,
                                mspVendor.getCompanyName());

                        }
                    }
                });
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(electricMeterExchange.getReferableID(), "X Exception: (ReferableID:"
                        + electricMeterExchange.getReferableID() + ")-" + ex.getMessage(), "ElectricMeterExchange",
                        METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_EXCHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(electricMeterExchange.getReferableID(), "X Error: (ReferableID:"
                        + electricMeterExchange.getReferableID() + ")-" + ex.getMessage(), "ElectricMeterExchange",
                        METER_EXCHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_EXCHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            }
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> metersChanged(final MultispeakVendor mspVendor, List<ElectricMeter> changedMeters)
            throws MultispeakWebServiceException {
        final List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (final ElectricMeter mspMeter : changedMeters) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {

                        validateMspMeter(mspMeter, mspVendor, METER_CHANGED_STRING);

                        try {
                            YukonMeter meterToChange = getMeterByMeterNumber(mspMeter);
                            YukonMeter templateMeter =
                                getYukonMeterForTemplate(mspMeter, mspVendor, false, METER_CHANGED_STRING); // throws
                                                                                                            // MspErrorObjectException
                            if (templateMeter == null) {
                                // If no template found, just use this meter as the template (meaning, same
                                // meter, no type changes).
                                templateMeter = meterToChange;
                            }
                            meterToChange =
                                updateExistingMeter(mspMeter, meterToChange, templateMeter, METER_CHANGED_STRING,
                                    mspVendor, false);

                            // using null for mspServiceLocation. See comments in
                            // getSubstationNameFromMspMeter(...)
                            verifyAndUpdateSubstationGroupAndRoute(meterToChange, mspVendor, mspMeter, null,
                                SERV_LOC_CHANGED_STRING);
                        } catch (NotFoundException e) {
                            multispeakEventLogService.meterNotFound(mspMeter.getPrimaryIdentifier().getValue(),
                                METER_CHANGED_STRING, mspVendor.getCompanyName());
                            ErrorObject err =
                                mspObjectDao.getNotFoundErrorObject(mspMeter.getObjectGUID(), "MeterNumber: "
                                    + mspMeter.getPrimaryIdentifier().getValue(), "ElectricMeter",
                                    METER_CHANGED_STRING, mspVendor.getCompanyName());
                            errorObjects.add(err);
                            multispeakEventLogService.errorObject(err.getDisplayString(), METER_CHANGED_STRING,
                                mspVendor.getCompanyName());
                            log.error(e);
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getDisplayString(), METER_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Exception: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "X Error: (MeterNo:"
                        + mspMeter.getPrimaryIdentifier().getValue() + ")-" + ex.getMessage(), "ElectricMeter",
                        METER_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), METER_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            }
        }// end for

        return errorObjects;
    }

    /**
     * Add a new meter to Yukon.
     * 
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name.
     */
    private YukonMeter addNewMeter(ElectricMeter mspMeterToAdd, MultispeakVendor mspVendor, String mspMethod)
            throws MspErrorObjectException {

        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeterToAdd, mspVendor, true, mspMethod);

        String newPaoName = getPaoNameFromMspMeter(mspMeterToAdd, mspVendor);
        // If PaoName already exists, a uniqueness value will be added.
        newPaoName = getNewUniquePaoName(newPaoName);

        SimpleDevice newDevice;

        // Create PLC or RFN Device object with defaults
        try {
            if (templateMeter.getPaoType().isRfn()) {

                String serialNumber = mspMeterToAdd.getCommunicationsAddress().getValue().trim();
                RfnIdentifier newMeterRfnIdentifier =
                    buildNewMeterRfnIdentifier((RfnMeter) templateMeter, serialNumber);

                // Use Model and Manufacturer from template
                newDevice =
                    deviceCreationService.createRfnDeviceByTemplate(templateMeter.getName(), newPaoName,
                        newMeterRfnIdentifier, true);

            } else if (templateMeter.getPaoType().isPlc()) {
                // CREATE DEVICE - new device is automatically added to template's device groups
                // TODO create new method here that takes a loaded template...since we already have one!
                newDevice = deviceCreationService.createDeviceByTemplate(templateMeter.getName(), newPaoName, true);
            } else {
                // return errorObject for any other type.
                ErrorObject errorObject =
                    mspObjectDao.getErrorObject(mspMeterToAdd.getObjectGUID(), "Error: Invalid template type ["
                        + templateMeter.getPaoType() + "].", "ElectricMeter", mspMethod, mspVendor.getCompanyName());
                throw new MspErrorObjectException(errorObject);
            }
        } catch (DeviceCreationException | BadConfigurationException e) {
            log.error(e);
            ErrorObject errorObject =
                mspObjectDao.getErrorObject(mspMeterToAdd.getObjectGUID(), "Error: " + e.getMessage(), "ElectricMeter",
                    mspMethod, mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        YukonMeter newMeter = meterDao.getForId(newDevice.getDeviceId());
        systemLog(mspMethod, "New Meter created: " + newMeter.toString(), mspVendor);
        multispeakEventLogService.meterCreated(newMeter.getMeterNumber(), newMeter, mspMethod,
            mspVendor.getCompanyName());

        // update default values of newMeter
        newMeter = updateExistingMeter(mspMeterToAdd, newMeter, templateMeter, mspMethod, mspVendor, true);
        return newMeter;
    }

    /**
     * Returns an rfnIdentifier representing the serialNumber and manufacturer/model from templateMeter.
     * If templateMeter has blank values, then we will attempt to parse the manufacturer/model values from the
     * template name.
     * This is useful if the templateName matches the "standard" *RfnTemplate_manufacturer_model naming
     * convention
     * 
     * @param templateMeter
     * @param serialNumber
     * @return
     */
    private RfnIdentifier buildNewMeterRfnIdentifier(RfnMeter templateMeter, String serialNumber) {

        String manufacturer = templateMeter.getRfnIdentifier().getSensorManufacturer();
        String model = templateMeter.getRfnIdentifier().getSensorModel();

        if (StringUtils.isBlank(manufacturer) || StringUtils.isBlank(model)) {
            // if either is empty, attempt to parse from the templateMeter.paoName
            String templatePrefix =
                configurationSource.getString(MasterConfigString.RFN_METER_TEMPLATE_PREFIX, "*RfnTemplate_");

            // Format is *RfnTemplate_manufacturer_model
            String nameToStripRfnIdentifierFrom = templateMeter.getName();
            String rfnIdentifierPart = StringUtils.removeStart(nameToStripRfnIdentifierFrom, templatePrefix);
            String[] manufacturerModel = StringUtils.split(rfnIdentifierPart, "_");

            if (manufacturerModel.length == 2) {
                manufacturer = manufacturerModel[0];
                model = manufacturerModel[1];
            } else {
                // TODO - make better error object
                throw new MspErrorObjectException(new ErrorObject());
            }
        }

        return new RfnIdentifier(serialNumber, manufacturer, model);
    }

    /**
     * Check for existing meter in system and update if found.
     * 
     * @throws NotFoundException if existing meter is not found in system.
     * @throws MspErrorObjectException when templateName is not a valid YukonPaobject Name. Or if changeType
     *         cannot be processed.
     */
    private YukonMeter checkForExistingMeterAndUpdate(ElectricMeter mspMeter, MultispeakVendor mspVendor,
            String mspMethod) throws NotFoundException, MspErrorObjectException {

        YukonMeter meter = null;
        MultispeakMeterLookupFieldEnum multispeakMeterLookupFieldEnum = multispeakFuncs.getMeterLookupField();

        switch (multispeakMeterLookupFieldEnum) {
        case METER_NUMBER:
            meter = getMeterByMeterNumber(mspMeter);
            break;
        case ADDRESS:
            meter = getMeterBySerialNumberOrAddress(mspMeter);
            break;
        case DEVICE_NAME:
            meter = getMeterByPaoName(mspMeter, mspVendor);
            break;
        case AUTO_METER_NUMBER_FIRST:
            try { // Lookup by MeterNo
                meter = getMeterByMeterNumber(mspMeter);
            } catch (NotFoundException e) { // Doesn't exist by MeterNumber
                try { // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspMeter);
                } catch (NotFoundException e2) { // Doesn't exist by Address
                    meter = getMeterByPaoName(mspMeter, mspVendor);
                    // Not Found Exception thrown in the end if never found
                }
            }
            break;
        case AUTO_DEVICE_NAME_FIRST:
        default:
            try { // Lookup by Device Name
                meter = getMeterByPaoName(mspMeter, mspVendor);
            } catch (NotFoundException e) { // Doesn't exist by Device name
                try { // Lookup by Meter Number
                    meter = getMeterByMeterNumber(mspMeter);
                } catch (NotFoundException e2) {
                    // Lookup by Address
                    meter = getMeterBySerialNumberOrAddress(mspMeter);
                    // Not Found Exception thrown in the end if never found
                }
            }
            break;
        }

        multispeakEventLogService.meterFound(meter.getMeterNumber(), meter, mspMethod, mspVendor.getCompanyName());

        // load (and validate) template exists
        YukonMeter templateMeter = getYukonMeterForTemplate(mspMeter, mspVendor, false, mspMethod); // throws
                                                                                                    // MspErrorObjectException
        if (templateMeter == null) {
            // If no template found, just use this meter as the template (meaning, same meter, no type
            // changes).
            templateMeter = meter;
        }

        meter = updateExistingMeter(mspMeter, meter, templateMeter, mspMethod, mspVendor, true);
        return meter;
    }

    /**
     * Helper method to update an existing meter with data associated with mspMeter.
     * Updates the following (if different):
     * - If existingMeter is enabled, we just warn...do not give up (anymore! 201406 change)
     * - Attempt to change the deviceType, throws MspErrorObjectException if cannot be completed.
     * - Attempt to change the Meter Number.
     * - Attempt to change the Serial Number or (Carrier) Address.
     * - Attempt to change the PaoName. -
     * - Loads MspServiceLocation from meterNumber.
     * - Updates BillingCycle
     * - Updates Alt Group (DEMCO special)
     * - Removes from /Meters/Flags/Inventory/
     * - Enables meter
     * - Attempt to update CIS Substation Group and Routing information
     * 
     * @return Returns the updated existingMeter object (in case of major paoType change)
     */
    private YukonMeter updateExistingMeter(ElectricMeter mspMeter, YukonMeter existingMeter, YukonMeter templateMeter,
            String mspMethod, MultispeakVendor mspVendor, boolean enable) throws MspErrorObjectException {

        YukonMeter originalCopy = existingMeter;
        String newSerialOrAddress = mspMeter.getCommunicationsAddress().getValue().trim(); // this should be
                                                                                           // the
                                                                                           // sensorSerialNumber

        existingMeter = verifyAndUpdateType(templateMeter, existingMeter, newSerialOrAddress, mspMethod, mspVendor);

        String newMeterNumber = mspMeter.getPrimaryIdentifier().getValue().trim();
        verifyAndUpdateMeterNumber(newMeterNumber, existingMeter, mspMethod, mspVendor);

        verifyAndUpdateAddressOrSerial(newSerialOrAddress, templateMeter, existingMeter, mspMethod, mspVendor);

        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        verifyAndUpdatePaoName(newPaoName, existingMeter, mspMethod, mspVendor);

        // Enable Meter and update applicable fields.
        if (enable) {
            if (existingMeter.isDisabled()) {
                deviceDao.enableDevice(existingMeter);
                existingMeter.setDisabled(false); // update local object with new status
                multispeakEventLogService.enableDevice(existingMeter.getMeterNumber(), existingMeter, mspMethod,
                    mspVendor.getCompanyName());
            } else {
                log.info("Meter (" + existingMeter.toString() + ") - currently enabled, continuing with processing...");
            }
        }

        systemLog(mspMethod, "Original:" + originalCopy.toString() + " New:" + existingMeter.toString(), mspVendor);
        // TODO Perform DBChange for Pao here instead? Need to make sure the above methods no longer push the
        // db change msg too...
        return existingMeter;
    }

    /**
     * Check if the deviceType of meter is different than the deviceType of the template meter
     * If different types of meters, then the deviceType will be changed for meter.
     * If the types are not compatible, a MspErrorObjectException will be thrown.
     * 
     * @param templateMeter - the meter to compare to, this is the type of meter the calling system thinks we
     *        should have
     * @param existingMeter - the meter to update
     * @param serialOrAddress
     * @param mspMethod
     * @param mspVendor
     * @throws MspErrorObjectException when error changing the type
     * @return Returns the updated existingMeter object (in case of major paoType change)
     */
    private YukonMeter verifyAndUpdateType(YukonMeter templateMeter, YukonMeter existingMeter, String serialOrAddress,
            String mspMethod, MultispeakVendor mspVendor) throws MspErrorObjectException {
        PaoType originalType = existingMeter.getPaoType();
        if (templateMeter.getPaoType() != originalType) {
            // PROBLEM, types do not match!
            // Attempt to change type
            try {
                ChangeDeviceTypeService.ChangeDeviceTypeInfo changeInfo;
                if (templateMeter.getPaoType().isRfn()) {
                    RfnIdentifier rfnIdentifier = buildNewMeterRfnIdentifier((RfnMeter) templateMeter, serialOrAddress);
                    changeInfo = new ChangeDeviceTypeInfo(rfnIdentifier);
                } else {
                    changeInfo = new ChangeDeviceTypeInfo(Integer.parseInt(serialOrAddress), 0); // route will
                                                                                                 // be updated
                                                                                                 // later as
                                                                                                 // part of
                                                                                                 // route
                                                                                                 // locate
                }

                changeDeviceTypeService.changeDeviceType(new SimpleDevice(existingMeter), templateMeter.getPaoType(),
                    changeInfo);
                existingMeter = meterDao.getForId(existingMeter.getDeviceId()); // reload the meter in case
                                                                                // we've changed base classes

                // Extra logging to SystemLog, can be removed with completion of MultiSpeak EventLogs
                systemLog(mspMethod, "MeterNumber (" + existingMeter.getMeterNumber() + ") - Changed DeviceType from:"
                    + originalType + " to:" + templateMeter.getPaoIdentifier().getPaoType() + ").", mspVendor);
                multispeakEventLogService.deviceTypeUpdated(originalType, existingMeter, mspMethod,
                    mspVendor.getCompanyName());
            } catch (ProcessingException | NumberFormatException e) {
                ErrorObject errorObject =
                    mspObjectDao.getErrorObject(existingMeter.getMeterNumber(), "Error: " + e.getMessage(),
                        "ElectricMeter", "ChangeDeviceType", mspVendor.getCompanyName());
                // return errorObject; couldn't save the change type
                throw new MspErrorObjectException(errorObject);
            }
        }
        return existingMeter;
    }

    /**
     * Check if paoName of meter is different than new paoName.
     * If different, paoName is updated.
     */
    private void verifyAndUpdatePaoName(String newPaoName, YukonMeter existingMeter, String mspMethod,
            MultispeakVendor mspVendor) {
        String originalName = existingMeter.getName();
        if (!originalName.equalsIgnoreCase(newPaoName)) {
            // UPDATE PAONAME
            // Shouldn't fail, if PaoName already exists, a uniqueness value will be added.
            newPaoName = getNewUniquePaoName(newPaoName);
            deviceDao.changeName(existingMeter, newPaoName);
            existingMeter.setName(newPaoName); // update local object with new name.
            multispeakEventLogService.paoNameUpdated(originalName, existingMeter, mspMethod, mspVendor.getCompanyName());
        }
    }

    /**
     * Check if meterNumber of meter is different than new meterNumber.
     * If different, meterNumber is updated.
     */
    private void verifyAndUpdateMeterNumber(String newMeterNumber, YukonMeter existingMeter, String mspMethod,
            MultispeakVendor mspVendor) {
        String originalMeterNumber = existingMeter.getMeterNumber();
        if (!originalMeterNumber.equalsIgnoreCase(newMeterNumber)) {
            // UPDATE METER NUMBER
            // Shouldn't fail, if Meter Number already exists, we end up with duplicates
            deviceDao.changeMeterNumber(existingMeter, newMeterNumber);
            existingMeter.setMeterNumber(newMeterNumber); // update local object with new meter number.
            multispeakEventLogService.meterNumberUpdated(originalMeterNumber, existingMeter, mspMethod,
                mspVendor.getCompanyName());
        }
    }

    /**
     * For Rfn, check if rfnIdentifier of meter is different than new serialNumber (and model/manufacturer of
     * templateMeter).
     * If different, rfnIdentifier is updated.
     * For Plc, check if address of meter is different than new address.
     * If different, address is updated.
     */
    private void verifyAndUpdateAddressOrSerial(String newSerialOrAddress, YukonMeter templateMeter,
            YukonMeter existingMeter, String mspMethod, MultispeakVendor mspVendor) {
        if (existingMeter.getPaoType().isRfn()) {
            RfnIdentifier newMeterRfnIdentifier =
                buildNewMeterRfnIdentifier((RfnMeter) templateMeter, newSerialOrAddress);

            // Check if different first, then update only if change needed
            if (!((RfnMeter) existingMeter).getRfnIdentifier().getSensorSerialNumber().equals(
                newMeterRfnIdentifier.getSensorSerialNumber())) {
                String originalSerialNumber = ((RfnMeter) existingMeter).getRfnIdentifier().getSensorSerialNumber();
                RfnDevice deviceToUpdate = new RfnDevice(existingMeter.getName(), existingMeter, newMeterRfnIdentifier);
                rfnDeviceDao.updateDevice(deviceToUpdate);
                existingMeter =
                    new RfnMeter(existingMeter, newMeterRfnIdentifier, existingMeter.getMeterNumber(),
                        existingMeter.getName(), existingMeter.isDisabled()); // update local object with new
                                                                              // RfnIdentifier
                multispeakEventLogService.serialNumberOrAddressUpdated(originalSerialNumber, existingMeter, mspMethod,
                    mspVendor.getCompanyName());
                // UPDATE SERIAL NUMBER (model, manufacturer)
                // MAY FAIL IF RFNIdentifier ALREADY EXISTS FOR ANOTHER METER
            }

        } else if (templateMeter.getPaoType().isMct()) {
            // Check if different first, then update only if change needed
            if (!existingMeter.getSerialOrAddress().equals(newSerialOrAddress)) {
                String originalAddress = existingMeter.getSerialOrAddress();
                // UPDATE CARRIER ADDRESS
                // WILL NOT FAIL IF ADDRESS IS ALREADY IN USE BY ANOTHER DEVICE!
                deviceUpdateService.changeAddress(existingMeter, Integer.valueOf(newSerialOrAddress));
                ((PlcMeter) existingMeter).setAddress(newSerialOrAddress); // update local object with new
                                                                           // address.
                multispeakEventLogService.serialNumberOrAddressUpdated(originalAddress, existingMeter, mspMethod,
                    mspVendor.getCompanyName());
            }
        }
    }

    /**
     * Update the (CIS) Substation Group.
     * If changed, update route (perform route locate).
     * If substationName is blank, do nothing.
     * 
     * @param meter - the meter to modify
     * @param mspVendor
     * @param mspMeter - the multispeak meter to process (if null, most likely will not change substation and
     *        routing info)
     *        - See {@link #getSubstationNameFromMspObjects(ElectricMeter, ServiceLocation, MultispeakVendor)}
     * @param mspServiceLocation - the multispeak serviceLocation to process (will be lazy loaded if needed)
     *        - See {@link #getSubstationNameFromMspObjects(ElectricMeter, ServiceLocation, MultispeakVendor)}
     */
    private void verifyAndUpdateSubstationGroupAndRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor,
            ElectricMeter mspMeter, ServiceLocation mspServiceLocation, String mspMethod) {

        String meterNumber = meterToUpdate.getMeterNumber();

        // Verify substation name
        String substationName = getSubstationNameFromMspObjects(mspMeter, mspServiceLocation, mspVendor);
        if (StringUtils.isBlank(substationName)) {
            // change logging here.
            // No route updates made (if PLC). No substation group updates.
            multispeakEventLogService.substationNotFound("", meterNumber, mspMethod, mspVendor.getCompanyName());
            systemLog(mspMethod, "MeterNumber(" + meterNumber
                + ") - substation name not provided, route locate and substation assignement not completed.", mspVendor);
        } else {

            // update the substation group
            boolean addedToGroup =
                updateSubstationGroup(substationName, meterNumber, meterToUpdate, mspMethod, mspVendor);

            if (meterToUpdate.getPaoType().isPlc()) {
                if (addedToGroup || ((PlcMeter) meterToUpdate).getRouteId() == 0) {
                    // If the substation changed, we should attempt to update the route info too.
                    // Update route (_after_ meter is enabled).
                    verifyAndUpdateRoute(meterToUpdate, mspVendor, substationName, mspMethod);
                }
            }
        }
    }

    /**
     * Checks if substationName is in Yukon.
     * If not found, or if no routes associated with substation, then return with no processing.
     * If found, and routes exist, assigns (first) route to meter.
     * Then initiates route locate.
     * If only one route found, assigns to meter but locate is skipped.
     * Returns with no processing for non PLC types.
     * 
     * @param meterToUpdate- the meter to update routing for
     * @param mspVendor
     * @param substationName - the substationName to lookup routeMappings for
     * @param meterNumber - for logging
     * @param mspMethod
     */
    private void verifyAndUpdateRoute(YukonMeter meterToUpdate, MultispeakVendor mspVendor, String substationName,
            String mspMethod) {

        // not valid for RFN meter types
        if (!(meterToUpdate instanceof PlcMeter)) {
            return;
        }

        String meterNumber = meterToUpdate.getMeterNumber();

        try {
            // get routes
            Substation substation = substationDao.getByName(substationName);
            List<Route> routes = substationToRouteMappingDao.getRoutesBySubstationId(substation.getId());

            if (routes.isEmpty()) {
                multispeakEventLogService.routeNotFound(substationName, ((PlcMeter) meterToUpdate).getRoute(),
                    meterNumber, mspMethod, mspVendor.getCompanyName());
                systemLog(mspMethod, "MeterNumber(" + meterNumber + ") - No Routes for substation (" + substationName
                    + "), using route from existing device.", mspVendor);
            } else { // routes exist

                // initally set route to first sub mapping
                int originalRouteId = ((PlcMeter) meterToUpdate).getRouteId();
                Route initialRoute = routes.get(0);

                if (originalRouteId != initialRoute.getId()) {
                    deviceUpdateService.changeRoute(meterToUpdate, initialRoute.getId());
                    ((PlcMeter) meterToUpdate).setRouteId(initialRoute.getId());
                    ((PlcMeter) meterToUpdate).setRoute(initialRoute.getName()); // update local object with
                                                                                 // initiate route

                    if (routes.size() == 1) { // no need to run route discovery if we only have one route.
                        multispeakEventLogService.routeUpdated(initialRoute.getName(), meterNumber, mspMethod,
                            mspVendor.getCompanyName());
                        systemLog(
                            mspMethod,
                            "MeterNumber("
                                + meterNumber
                                + ") - Only one route associated with substation, assigned route and skipping route discovery.",
                            mspVendor);
                    }
                }

                if (routes.size() > 1) { // run route discovery
                    LiteYukonUser liteYukonUser = UserUtils.getYukonUser();

                    ImmutableList<Integer> routeIds = PaoUtils.asPaoIdList(routes);
                    ImmutableList<String> routeNames = PaoUtils.asPaoNames(routes);

                    multispeakEventLogService.routeUpdatedByDiscovery(initialRoute.getName(), meterNumber,
                        StringUtils.join(routeNames, ","), mspMethod, mspVendor.getCompanyName());
                    deviceUpdateService.routeDiscovery(meterToUpdate, routeIds, liteYukonUser);
                    systemLog(mspMethod, "MeterNumber(" + meterNumber + ") - Route discovery started on: "
                        + StringUtils.join(routeNames, ",") + ".", mspVendor);
                }
            }

        } catch (NotFoundException e) { // bad sub name
            multispeakEventLogService.substationNotFound(substationName, meterNumber, mspMethod,
                mspVendor.getCompanyName());
            systemLog(mspMethod, "MeterNumber(" + meterNumber + ") - substation name " + substationName
                + " not found in Yukon, no route changes will occur.", mspVendor);
            log.warn(e);
        }
    }

    /**
     * Helper method to return a Meter object for PaoName PaoName is looked up
     * based on PaoNameAlias
     */
    private YukonMeter getMeterByPaoName(ElectricMeter mspMeter, MultispeakVendor mspVendor) {
        String paoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
        return meterDao.getForPaoName(paoName);
    }

    /**
     * Helper method to return a PLC Meter object for Address
     * Address to look up is from mspMeter.communicationsAddress.value
     */
    private YukonMeter getMeterBySerialNumberOrAddress(ElectricMeter mspMeter) {
        String mspAddress = mspMeter.getCommunicationsAddress().getValue().trim();
        return mspMeterDao.getForSerialNumberOrAddress(mspAddress);
    }

    /**
     * Helper method to return a Meter object for Meter Number
     */
    private YukonMeter getMeterByMeterNumber(ElectricMeter mspMeter) throws NotFoundException {
        String mspMeterNo = mspMeter.getPrimaryIdentifier().getValue().trim();
        return meterDao.getForMeterNumber(mspMeterNo);
    }

    /**
     * Helper method to return a Meter object for Meter Number
     */
    private YukonMeter getMeterByMeterNumber(String mspMeter) throws NotFoundException {
        return meterDao.getForMeterNumber(mspMeter);
    }

    @Override
    public List<ErrorObject> serviceLocationsChanged(final MultispeakVendor mspVendor,
            List<ServiceLocation> serviceLocations) throws MultispeakWebServiceException {
        final ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();

        for (final ServiceLocation mspServiceLocation : serviceLocations) {
            try {
                transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                    @Override
                    protected void doInTransactionWithoutResult(TransactionStatus status) {
                        boolean isMeterFound = false;
                        if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
                            if (mspServiceLocation.getElectricServicePoints() != null) {
                                for (ElectricServicePoint servicePoint : mspServiceLocation.getElectricServicePoints().getElectricServicePoint()) {
                                    YukonMeter meter = null;
                                    // TODO probably need to update code after confirmation on actual meter
                                    // field population (parent or child field)

                                    if (servicePoint.getElectricMeterID() != null) {
                                        try {
                                            meter =
                                                getMeterByMeterNumber(servicePoint.getElectricMeterID().getObjectGUID());
                                            isMeterFound = true;
                                        } catch (NotFoundException e) {
                                            if (servicePoint.getElectricMeterID().getPrimaryIdentifier() != null) {
                                                try {
                                                    meter =
                                                        getMeterByMeterNumber(servicePoint.getElectricMeterID().getPrimaryIdentifier().getValue());
                                                    isMeterFound = true;
                                                } catch (NotFoundException e1) {
                                                    multispeakEventLogService.meterNotFound(
                                                        servicePoint.getElectricMeterID().getObjectGUID(),
                                                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                                    ErrorObject err =
                                                        mspObjectDao.getNotFoundErrorObject(
                                                            servicePoint.getElectricMeterID().getObjectGUID(),
                                                            "MeterNumber", "ServiceLocation", SERV_LOC_CHANGED_STRING,
                                                            mspVendor.getCompanyName());
                                                    errorObjects.add(err);
                                                    multispeakEventLogService.errorObject(err.getDisplayString(),
                                                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                                    log.error(e);

                                                }
                                            }
                                        }
                                    }

                                    if (!isMeterFound && servicePoint.getElectricMeter() != null) {
                                        try {
                                            meter = getMeterByMeterNumber(servicePoint.getElectricMeter());
                                        } catch (NotFoundException e) {
                                            multispeakEventLogService.meterNotFound(
                                                servicePoint.getElectricMeter().getPrimaryIdentifier().getValue(),
                                                SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                            ErrorObject err =
                                                mspObjectDao.getNotFoundErrorObject(
                                                    servicePoint.getElectricMeter().getPrimaryIdentifier().getValue(),
                                                    "MeterNumber", "ServiceLocation", SERV_LOC_CHANGED_STRING,
                                                    mspVendor.getCompanyName());
                                            errorObjects.add(err);
                                            multispeakEventLogService.errorObject(err.getDisplayString(),
                                                SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                            log.error(e);
                                        }
                                    }

                                    if (meter != null) {
                                        // update the billing group from request
                                        String billingCycle = servicePoint.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter,
                                            SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter,
                                            SERV_LOC_CHANGED_STRING, mspVendor);

                                        // using null for mspMeter. See comments in
                                        // getSubstationNameFromMspMeter(...)
                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, null,
                                            mspServiceLocation, SERV_LOC_CHANGED_STRING);
                                    }
                                    // TODO if above both optional fields (ElectricMeterID/ElectricMeter ) are
                                    // not present in ServiceLocation then should we need to send any error
                                    // message.
                                }
                            }
                        } else {
                            // Must get meters from MSP CB call to process.
                            List<ElectricMeter> mspMeters =
                                mspObjectDao.getMspMetersByServiceLocation(mspServiceLocation, mspVendor);

                            if (!mspMeters.isEmpty()) {

                                for (ElectricMeter mspMeter : mspMeters) {
                                    try {
                                        YukonMeter meter = getMeterByMeterNumber(mspMeter);

                                        // MeterNumber should not have changed, nor communication
                                        // address...only paoName possibly
                                        String newPaoName = getPaoNameFromMspMeter(mspMeter, mspVendor);
                                        verifyAndUpdatePaoName(newPaoName, meter, SERV_LOC_CHANGED_STRING, mspVendor);

                                        String billingCycle = mspMeter.getBillingCycle();
                                        updateBillingCyle(billingCycle, meter.getMeterNumber(), meter,
                                            SERV_LOC_CHANGED_STRING, mspVendor);
                                        updateAltGroup(mspServiceLocation, meter.getMeterNumber(), meter,
                                            SERV_LOC_CHANGED_STRING, mspVendor);

                                        verifyAndUpdateSubstationGroupAndRoute(meter, mspVendor, mspMeter,
                                            mspServiceLocation, SERV_LOC_CHANGED_STRING);
                                    } catch (NotFoundException e) {
                                        multispeakEventLogService.meterNotFound(
                                            mspMeter.getPrimaryIdentifier().getValue(), SERV_LOC_CHANGED_STRING,
                                            mspVendor.getCompanyName());
                                        ErrorObject err =
                                            mspObjectDao.getNotFoundErrorObject(
                                                mspMeter.getPrimaryIdentifier().getValue(), "MeterNumber",
                                                "ElectricMeter", SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        errorObjects.add(err);
                                        multispeakEventLogService.errorObject(err.getDisplayString(),
                                            SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                        log.error(e);
                                    }
                                }
                            } else {
                                multispeakEventLogService.objectNotFoundByVendor(mspServiceLocation.getObjectGUID(),
                                    "GetMetersByServiceLocationIDs", SERV_LOC_CHANGED_STRING,
                                    mspVendor.getCompanyName());
                                ErrorObject err =
                                    mspObjectDao.getErrorObject(
                                        mspServiceLocation.getObjectGUID(),
                                        paoAlias.getDisplayName() + " ServiceLocation("
                                            + mspServiceLocation.getObjectGUID()
                                            + ") - No meters returned from vendor for location.", "ServiceLocation",
                                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                                errorObjects.add(err);
                                multispeakEventLogService.errorObject(err.getDisplayString(), SERV_LOC_CHANGED_STRING,
                                    mspVendor.getCompanyName());
                            }
                        }
                    };
                });
            } catch (MspErrorObjectException e) {
                errorObjects.add(e.getErrorObject());
                multispeakEventLogService.errorObject(e.getErrorObject().getDisplayString(), SERV_LOC_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(e);
            } catch (RuntimeException ex) {
                // Transactional code threw application exception -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspServiceLocation.getObjectGUID(), "X Exception: (ServiceLocationID:"
                        + mspServiceLocation.getObjectGUID() + ")-" + ex.getMessage(), "ServiceLocation",
                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), SERV_LOC_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            } catch (Error ex) {
                // Transactional code threw error -> rollback
                ErrorObject err =
                    mspObjectDao.getErrorObject(mspServiceLocation.getObjectGUID(), "X Error: (ServiceLocationID:"
                        + mspServiceLocation.getObjectGUID() + ")-" + ex.getMessage(), "ServiceLocation",
                        SERV_LOC_CHANGED_STRING, mspVendor.getCompanyName());
                errorObjects.add(err);
                multispeakEventLogService.errorObject(err.getDisplayString(), SERV_LOC_CHANGED_STRING,
                    mspVendor.getCompanyName());
                log.error(ex);
            }
        }
        return errorObjects;
    }

    /**
     * Helper method to load extension value from extensionItems for
     * extensionName
     */
    private String getExtensionValue(ExtensionsList extensionsList, String extensionName, String defaultValue) {
        log.debug("Attempting to load extension value for key:" + extensionName);

        if (extensionsList != null) {
            for (ExtensionsItem eItem : extensionsList.getExtensionsItem()) {
                String extName = eItem.getExtName();
                if (extName.equalsIgnoreCase(extensionName)) {
                    return eItem.getExtValue().getValue();
                }
            }
        }
        log.warn("Extension " + extensionName + " key was not found. Returning default value: " + defaultValue);
        return defaultValue;
    }

    /**
     * Returns a Yukon PaoName (template) to model new meters after. If no value
     * is provided in the mspMeter object, then the defaultTemplateName is
     * returned.
     */
    private String getMeterTemplate(ElectricMeter mspMeter, String defaultTemplateName) {

        if (StringUtils.isNotBlank(mspMeter.getAMIDeviceType())) {
            return mspMeter.getAMIDeviceType();
        }

        return getExtensionValue(mspMeter.getExtensionsList(), EXTENSION_DEVICE_TEMPLATE_STRING, defaultTemplateName);
    }

    /**
     * Returns the value of the paoName alias extension field from Meter object.
     * If no value is provided in the Meter object, then null is returned. NOTE:
     * meterno - this extension will return mspMeter.primaryIdentifier directly.
     */
    private String getExtensionValue(ElectricMeter mspMeter) {

        boolean usesExtension = multispeakFuncs.usesPaoNameAliasExtension();

        if (usesExtension) {
            String extensionName = multispeakFuncs.getPaoNameAliasExtension();
            if (extensionName.equalsIgnoreCase("meterno")) { // specific field
                return mspMeter.getPrimaryIdentifier().getValue();
            } else { // use extensions
                return getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
            }
        }
        return null;
    }

    /**
     * Updates an alternate device grouping.
     * The exact parent group to update is configured by MSP_ALTGROUP_EXTENSION.
     * This functionality was added specifically for DEMCO.
     */
    @Override
    public boolean updateAltGroup(ServiceLocation mspServiceLocation, String meterNumber, YukonDevice yukonDevice,
            String mspMethod, MultispeakVendor mspVendor) {
        boolean updateAltGroup = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_ALTGROUP_EXTENSION);
        if (updateAltGroup) {
            String extensionName = configurationSource.getString(MasterConfigString.MSP_ALTGROUP_EXTENSION, "altGroup");
            String altGroup = getExtensionValue(mspServiceLocation.getExtensionsList(), extensionName, null);
            if (!StringUtils.isBlank(altGroup)) {

                // Remove from all alt group membership groups
                DeviceGroup altGroupDeviceGroup = deviceGroupEditorDao.getSystemGroup(SystemGroupEnum.ALTERNATE);
                StoredDeviceGroup deviceGroupParent = deviceGroupEditorDao.getStoredGroup(altGroupDeviceGroup);
                return updatePrefixGroup(altGroup, meterNumber, yukonDevice, mspMethod, mspVendor, deviceGroupParent);
            }
        }
        return false;
    }

    /**
     * Helper method to remove meter from systemGroup
     */
    private void removeFromGroup(YukonMeter meter, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        int numAffected =
            deviceGroupMemberEditorDao.removeDevices((StoredDeviceGroup) deviceGroup, Collections.singletonList(meter));
        if (numAffected > 0) {
            String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
            multispeakEventLogService.removeMeterFromGroup(meter.getMeterNumber(), basePath, mspMethod,
                mspVendor.getCompanyName());
        }
    }

    /**
     * Helper method to add meter to systemGroup
     */
    private void addToGroup(YukonMeter meter, SystemGroupEnum systemGroup, String mspMethod, MultispeakVendor mspVendor) {

        DeviceGroup deviceGroup = deviceGroupEditorDao.getSystemGroup(systemGroup);
        int numAffected = deviceGroupMemberEditorDao.addDevice((StoredDeviceGroup) deviceGroup, meter);
        if (numAffected > 0) {
            String basePath = deviceGroupEditorDao.getFullPath(systemGroup);
            multispeakEventLogService.addMeterToGroup(meter.getMeterNumber(), basePath, mspMethod,
                mspVendor.getCompanyName());
        }
    }

    /**
     * Helper method to add meterNos to systemGroup
     */
    private List<ErrorObject> addToGroup(List<String> meterNos, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return addToGroupAndDisable(meterNos, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to add meterNos to systemGroup
     * 
     * @param disable - when true, the meter will be disabled. Else no change.
     */
    private List<ErrorObject> addToGroupAndDisable(List<String> meterNos, SystemGroupEnum systemGroup,
            String mspMethod, MultispeakVendor mspVendor, boolean disable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (String meterNumber : meterNos) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (disable && !meter.isDisabled()) {
                    deviceDao.disableDevice(meter);
                }
                addToGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID", "addToGroup",
                        mspVendor.getCompanyName(), e.getMessage());
                errorObjects.add(err);
                log.error(e);
            }
        }
        return errorObjects;
    }

    /**
     * Returns the PaoName alias value for the MultiSpeak mspMeter object.
     * 
     * @throws InsufficientMultiSpeakDataException - when paoName not found (null)
     */
    private String getPaoNameFromMspMeter(ElectricMeter mspMeter, MultispeakVendor mspVendor) {

        String paoName = null;
        final MspPaoNameAliasEnum paoAlias = multispeakFuncs.getPaoNameAlias();
        if (paoAlias == MspPaoNameAliasEnum.ACCOUNT_NUMBER) {
            if (mspMeter.getUtilityInfo() != null && mspMeter.getUtilityInfo().getAccountID() != null
                && mspMeter.getUtilityInfo().getAccountID().getPrimaryIdentifier() != null
                && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getAccountID().getPrimaryIdentifier().getValue())) {
                paoName = mspMeter.getUtilityInfo().getAccountID().getPrimaryIdentifier().getValue();
                // TODO should I return objectID or primaryIndentifier as objectID is required field here
                if (paoName == null) {
                    if (mspMeter.getUtilityInfo().getAccountID() != null) {
                        paoName = mspMeter.getUtilityInfo().getAccountID().getObjectGUID();
                    }
                }
            }
        } else if (paoAlias == MspPaoNameAliasEnum.SERVICE_LOCATION) {
            if (mspMeter.getUtilityInfo() != null
                && mspMeter.getUtilityInfo().getServiceLocationID() != null
                && mspMeter.getUtilityInfo().getServiceLocationID().getPrimaryIdentifier() != null
                && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getServiceLocationID().getPrimaryIdentifier().getValue())) {
                paoName = mspMeter.getUtilityInfo().getServiceLocationID().getPrimaryIdentifier().getValue();
                // TODO should I return objectID or primaryIndentifier as objectID is required field here
                if (paoName == null) {
                    if (mspMeter.getUtilityInfo().getServiceLocationID() != null) {
                        paoName = mspMeter.getUtilityInfo().getServiceLocationID().getObjectGUID();
                    }
                }
            }
        } else if (paoAlias == MspPaoNameAliasEnum.CUSTOMER_ID) {
            if (mspMeter.getUtilityInfo() != null && mspMeter.getUtilityInfo().getCustomerID() != null
                && mspMeter.getUtilityInfo().getCustomerID().getPrimaryIdentifier() != null
                && StringUtils.isNotBlank(mspMeter.getUtilityInfo().getCustomerID().getPrimaryIdentifier().getValue())) {
                paoName = mspMeter.getUtilityInfo().getCustomerID().getPrimaryIdentifier().getValue();
                // TODO should I return objectID or primaryIndentifier as objectID is required field here
                if (paoName == null) {
                    if (mspMeter.getUtilityInfo().getCustomerID() != null) {
                        paoName = mspMeter.getUtilityInfo().getCustomerID().getObjectGUID();
                    }
                }

            }
        } else if (paoAlias == MspPaoNameAliasEnum.EA_LOCATION) {
            if (mspMeter.getElectricLocationFields() != null
                && mspMeter.getElectricLocationFields().getNetworkModelRef() != null
                && mspMeter.getElectricLocationFields().getNetworkModelRef().getValue() != null
                && StringUtils.isNotBlank(mspMeter.getElectricLocationFields().getNetworkModelRef().getPrimaryIdentifierValue())) {
                paoName = mspMeter.getElectricLocationFields().getNetworkModelRef().getPrimaryIdentifierValue();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.GRID_LOCATION) {
            if (mspMeter.getAssetLocation() != null
                && StringUtils.isNotBlank(mspMeter.getAssetLocation().getGridLocation())) {
                paoName = mspMeter.getAssetLocation().getGridLocation();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.METER_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getPrimaryIdentifier().getValue())) {
                paoName = mspMeter.getPrimaryIdentifier().getValue();
            }
        } else if (paoAlias == MspPaoNameAliasEnum.POLE_NUMBER) {
            if (StringUtils.isNotBlank(mspMeter.getPrimaryIdentifier().getValue())) {
                // TODO pole number is now present in Electric meter so probably we do not require to make CB
                // call for pole no
                // ServiceLocation mspServiceLocation =
                // mspObjectDao.getMspServiceLocation(mspMeter.getPrimaryIdentifier().getValue(), mspVendor);
                if (mspMeter.getElectricLocationFields() != null
                    && StringUtils.isNotBlank(mspMeter.getElectricLocationFields().getPoleNumber())) {
                    paoName = mspMeter.getElectricLocationFields().getPoleNumber();
                }
            }
        }

        if (paoName == null) {
            throw new InsufficientMultiSpeakDataException(
                "Message does not contain sufficient data for Yukon Device Name.");
        }

        String extensionValue = getExtensionValue(mspMeter);
        return multispeakFuncs.buildAliasWithQuantifier(paoName, extensionValue, mspVendor);

    }

    /**
     * Return the substation name of a Meter. If meter is null, return empty
     * string. If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm is set, then attempt
     * to return from mspMeter extensions. If mspMeter returns nothing, then
     * call mspVendor to load the related ServiceLocation. Attempt to return
     * from mspServiceLocation extensions. If ServiceLocation extensions return
     * nothing, then return null. If MSP_ENABLE_SUBSTATIONNAME_EXTENSION cparm
     * is NOT set, use normal loading from Meter object. Normal loading: If the
     * Meter does not contain a substation name in its utility info, return
     * null.
     * 
     * @param ServiceLocation mspServiceLocation - will be lazy loaded (if
     *        needed) and null passed in.
     * @return String substationName
     */
    private String getSubstationNameFromMspObjects(ElectricMeter mspMeter, ServiceLocation mspServiceLocation,
            MultispeakVendor mspVendor) {

        boolean useExtension = configurationSource.getBoolean(MasterConfigBoolean.MSP_ENABLE_SUBSTATIONNAME_EXTENSION);
        if (useExtension) { // custom for DEMCO/SEDC integration
            String extensionName =
                configurationSource.getString(MasterConfigString.MSP_SUBSTATIONNAME_EXTENSION, "readPath");
            String extensionValue;

            if (mspMeter != null) {
                log.debug("Attempting to load extension value for substation name from multispeak _meter_.");
                extensionValue = getExtensionValue(mspMeter.getExtensionsList(), extensionName, null);
                if (StringUtils.isNotBlank(extensionValue)) {
                    return extensionValue;
                }

                log.debug("Not found in meter. Attempting to load extension value for substation name from multispeak _serviceLocation_.");
                if (mspServiceLocation == null) {
                    log.debug("Calling CB to load ServiceLocation for Meter.");
                    mspServiceLocation =
                        mspObjectDao.getMspServiceLocation(mspMeter.getPrimaryIdentifier().getValue(), mspVendor);
                }
            }

            if (mspServiceLocation != null) {
                extensionValue = getExtensionValue(mspServiceLocation.getExtensionsList(), extensionName, null);
                if (StringUtils.isNotBlank(extensionValue)) {
                    return extensionValue;
                }
            }

            log.debug("Extension value for substation name NOT found for meter or service location, returning empty substationName.");
            return "";

        } else { // Normal loading
            if (mspMeter == null || mspMeter.getElectricLocationFields() == null
                || mspMeter.getElectricLocationFields().getSubstationRef() == null
                || StringUtils.isBlank(mspMeter.getElectricLocationFields().getSubstationRef().getSubstationName())) {
                return null;
            } else {
                return mspMeter.getElectricLocationFields().getSubstationRef().getSubstationName();
            }
        }
    }

    /**
     * Returns a YukonMeter object, looked up by a templateName provided by mspMeter.
     * If templateName not found on mspMeter, then the vendor's default templateName will be used.
     * If useDefault is true, then the default template name will be used if getAMIDeviceType not provided,
     * otherwise return null.
     * 
     * @throws MspErrorObjectException when meter not found in Yukon by templateName provided
     */
    private YukonMeter getYukonMeterForTemplate(ElectricMeter mspMeter, MultispeakVendor mspVendor, boolean useDefault,
            String mspMethod) throws MspErrorObjectException {

        String defaultTemplateName = useDefault ? mspVendor.getTemplateNameDefault() : null;
        String templateName = getMeterTemplate(mspMeter, defaultTemplateName);
        if (templateName != null) {
            YukonMeter templateMeter;
            try {
                templateMeter = meterDao.getForPaoName(templateName);
            } catch (NotFoundException e) {
                // template not found...now what? ERROR?
                ErrorObject err =
                    mspObjectDao.getErrorObject(
                        mspMeter.getObjectGUID(),
                        "Error: Meter ("
                            + mspMeter.getPrimaryIdentifier().getValue()
                            + ") - does not contain a valid template meter: Template["
                            + templateName
                            + "]. Processing could not be completed, returning ErrorObject to calling vendor for processing.",
                        "ElectricMeter", mspMethod, mspVendor.getCompanyName());
                log.error(e);
                throw new MspErrorObjectException(err);
            }
            return templateMeter;
        }
        return null;
    }

    /**
     * Returns ErrorObject when mspMeter is not valid. Returns null when mspMeter is valid.
     * Validates
     * 1) mspMeter.primaryIdentifier field is not blank.
     * 2) mspMeter.communicationsAddress is not null AND
     * communicationsAddress.Nameplate.communicationsAddress.value is not blank
     */
    private void validateMspMeter(ElectricMeter mspMeter, MultispeakVendor mspVendor, String method)
            throws MspErrorObjectException {

        // Check for valid MeterNo
        if (StringUtils.isBlank(mspMeter.getPrimaryIdentifier().getValue())) {

            ErrorObject errorObject =
                mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(),
                    "Error: MeterNo is invalid (empty or null).  No updates were made.", "ElectricMeter", method,
                    mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }

        // Check for valid communication address (PLC meters)
        if (mspMeter.getCommunicationsAddress() == null
            || StringUtils.isBlank(mspMeter.getCommunicationsAddress().getValue())) {

            ErrorObject errorObject =
                mspObjectDao.getErrorObject(mspMeter.getPrimaryIdentifier().getValue(), "Error: MeterNumber("
                    + mspMeter.getPrimaryIdentifier().getValue() + ") - SerialNumber "
                    + "nor communication Address are invalid.  No updates were made.", "ElectricMeter", method,
                    mspVendor.getCompanyName());
            throw new MspErrorObjectException(errorObject);
        }
    }

    /**
     * Returns an "unused" paoName for newPaoName.
     * If a meter already exists with newPaoName, then a numeric incremented value will be appended to the
     * newPaoName.
     */
    private String getNewUniquePaoName(String newPaoName) {

        int retryCount = 0;
        String tempPaoName = newPaoName;
        boolean found = false;
        do {
            try {
                if (retryCount > 0) {
                    tempPaoName = newPaoName + " (" + retryCount + ")";
                }
                // Try to find if a meter already exists with this paoName
                meterDao.getForPaoName(tempPaoName);
                retryCount++;
            } catch (NotFoundException e) {
                // this is good!
                found = true;
            }
        } while (!found);

        return tempPaoName;
    }

    /**
     * Extra SystemLog logging that will be completely removed upon completion of MultiSpeak EventLogs.
     * Only use this method if you intend for the logging to be removed with EventLogs completion.
     */
    @Deprecated
    private void systemLog(String mspMethod, String description, MultispeakVendor mspVendor) {
        mspObjectDao.logMSPActivity(mspMethod, description, mspVendor.getCompanyName());
    }

    @Override
    public ImmutableSet<EndDeviceStateKind> getsupportedEndDeviceStateTypes() {
        return supportedEndDeviceStateTypes;
    }

    @Override
    public ImmutableSetMultimap<EndDeviceStateKind, Integer> getOutageConfig() {
        return outageConfig;
    }

    @Override
    public synchronized List<ErrorObject> odEvent(final MultispeakVendor mspVendor, List<String> meterNumbers,
            final String transactionId, final String responseUrl) throws MultispeakWebServiceException {

        if (StringUtils.isBlank(responseUrl)) { // no need to go through all the work if we have no one to
                                                // respond to.
            throw new MultispeakWebServiceException("OMS vendor unknown.  Please contact Yukon administrator"
                + " to set the Multispeak Vendor Role Property value in Yukon.");
        }

        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException("Connection to 'Yukon Port Control Service' "
                + "is not valid.  Please contact your Yukon Administrator.");
        }

        log.info("Received " + meterNumbers.size() + " Meter(s) for Outage Verification Testing from "
            + mspVendor.getCompanyName());
        multispeakEventLogService.initiateODEventRequest(meterNumbers.size(), "InitiateEndDevicePings",
            mspVendor.getCompanyName());

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        List<YukonMeter> rfnPaosToPing = Lists.newArrayList();
        List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();

        ListMultimap<String, YukonMeter> meterNumberToMeterMap =
            meterDao.getMetersMapForMeterNumbers(Lists.newArrayList(meterNumbers));// Why new here
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        for (String meterNumber : meterNumbers) {
            List<YukonMeter> meters = meterNumberToMeterMap.get(meterNumber); // this will most likely be size
                                                                              // 1
            for (YukonMeter meter : meters) {
                if (meter == null) {
                    multispeakEventLogService.meterNotFound(meterNumber, "InitiateEndDevicePings",
                        mspVendor.getCompanyName());
                    ErrorObject err =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "ODEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
                }
                if (excludeDisabled && meter.isDisabled()) {
                    log.debug("Meter " + meter.getMeterNumber() + " is disabled, skipping.");
                    continue;
                }
                // TODO validate is OD supported meter ???
                if (meter instanceof RfnMeter) {
                    rfnPaosToPing.add(meter);
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId,
                        "InitiateEndDevicePings", mspVendor.getCompanyName());
                    continue;
                }
                // Assume plc if we made it this far, validate meter can receive porter command requests and
                // command string exists, then perform action
                boolean supportsPing =
                    paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(),
                        PaoTag.PORTER_COMMAND_REQUESTS);
                if (supportsPing) { // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(SimpleDevice.of(meter.getPaoIdentifier()));
                    request.setCommandCallback(new CommandCallbackBase("ping"));
                    plcCommandRequests.add(request);
                    multispeakEventLogService.initiateODEvent(meterNumber, meter, transactionId,
                        "InitiateEndDevicePings", mspVendor.getCompanyName());
                } else {
                    ErrorObject err =
                        mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                            + ") - Meter cannot receive requests from porter. ", "Meter", "ODEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                }
            }
        }
        // perform read attribute(?) on list of meters
        doRfnOutagePing(rfnPaosToPing, mspVendor, transactionId, responseUrl);
        // perform plc action on list of commandRequests
        doPlcOutagePing(plcCommandRequests, mspVendor, transactionId, responseUrl);
        return errorObjects;

    }

    /**
     * Performs the RFN meter outage analysis.
     * If MSP_RFN_PING_FORCE_CHANNEL_READ setting is set to
     * true = perform a real time attribute read using Outage_Status attribute.
     * Callback will initiate a EndDeviceStatesNotification on receivedLastValue or receivedError.
     * false = use the last known value of Outage_Status to determine odEvent state.
     * Returns immediately, does not wait for a response.
     */

    private void doRfnOutagePing(final List<YukonMeter> meters, final MultispeakVendor mspVendor,
            final String transactionId, final String responseUrl) {

        boolean isChannelReadForPing = globalSettingDao.getBoolean(GlobalSettingType.MSP_RFN_PING_FORCE_CHANNEL_READ);

        if (isChannelReadForPing) {
            DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

                @Override
                public void complete() {
                    log.debug("deviceAttributeReadCallback.complete for odEvent");
                }

                @Override
                public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                    log.debug("deviceAttributeReadCallback.receivedLastValue for odEvent");
                }

                @Override
                public void receivedLastValue(PaoIdentifier pao, String value) { // success
                    log.debug("deviceAttributeReadCallback.receivedLastValue for odEvent");

                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId()); // can we get this from meters?
                    if (meters.contains(yukonMeter)) { // meter may have already been handled and removed by
                                                       // receivedError
                        Date now = new Date(); // may need to get this from the callback, but for now "now"
                                               // will do.
                        EndDeviceStateType deviceStateType = new EndDeviceStateType();
                        deviceStateType.setValue(getForStatusCode(0));
                        EndDeviceState endDeviceState = buildEndDeviceStates(yukonMeter, deviceStateType, now, value);
                        sendEndDeviceStatesNotification(yukonMeter, mspVendor, transactionId, responseUrl,
                            endDeviceState);
                    }
                }

                @Override
                public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) { // failure
                    log.warn("deviceAttributeReadCallback.receivedError for odEvent: " + pao + ": " + error);

                    YukonMeter yukonMeter = meterDao.getForId(pao.getPaoId()); // can we get this from meters?
                    meters.remove(yukonMeter);
                    Date now = new Date(); // may need to get this from the callback, but for now "now" will
                                           // do.
                    EndDeviceStateType deviceStateType = new EndDeviceStateType();
                    deviceStateType.setValue(getForStatusCode(error.getErrorCode()));
                    EndDeviceState endDeviceState =
                        buildEndDeviceStates(yukonMeter, deviceStateType, now, error.toString());
                    sendEndDeviceStatesNotification(yukonMeter, mspVendor, transactionId, responseUrl, endDeviceState);
                }

                @Override
                public void receivedException(SpecificDeviceErrorDescription error) {
                    log.warn("deviceAttributeReadCallback.receivedException in odEvent callback: " + error);
                }

            };
            deviceAttributeReadService.initiateRead(meters, Sets.newHashSet(BuiltInAttribute.OUTAGE_STATUS), callback,
                DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, UserUtils.getYukonUser());
        } else { // save network expense by just returning latest known value.

            BiMap<LitePoint, PaoIdentifier> pointsToPaos =
                attributeService.getPoints(meters, BuiltInAttribute.OUTAGE_STATUS).inverse();
            Set<Integer> pointIds = Sets.newHashSet(Iterables.transform(pointsToPaos.keySet(), LitePoint.ID_FUNCTION));
            Set<? extends PointValueQualityHolder> pointValues = asyncDynamicDataSource.getPointValues(pointIds);

            final ImmutableMap<Integer, LitePoint> pointLookup =
                Maps.uniqueIndex(pointsToPaos.keySet(), LitePoint.ID_FUNCTION);
            final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup = PaoUtils.indexYukonPaos(meters);
            // need to send unkonwn or something if we don't have a point value.
            for (PointValueQualityHolder pointValue : pointValues) {
                Integer pointId = pointValue.getId();
                LitePoint litePoint = pointLookup.get(pointId);
                PaoIdentifier paoIdentifier = pointsToPaos.get(litePoint);
                YukonMeter yukonMeter = meterLookup.get(paoIdentifier);

                OutageStatus outageStatus = PointStateHelper.decodeRawState(OutageStatus.class, pointValue.getValue());
                EndDeviceStateType deviceStateType = new EndDeviceStateType();
                EndDeviceStateKind deviceState;

                switch (outageStatus) {
                case GOOD:
                    deviceState = EndDeviceStateKind.IN_SERVICE;
                    break;
                case BAD:
                    deviceState = EndDeviceStateKind.OUTAGED;
                    break;
                default:
                    deviceState = EndDeviceStateKind.NO_RESPONSE;
                    break;
                }
                deviceStateType.setValue(deviceState);
                EndDeviceState endDeviceState =
                    buildEndDeviceStates(yukonMeter, deviceStateType, pointValue.getPointDataTimeStamp(),
                        "Last Known Status");
                sendEndDeviceStatesNotification(yukonMeter, mspVendor, transactionId, responseUrl, endDeviceState);
            }
        }
    }

    /**
     * Performs the PLC meter outage ping.
     * Returns immediately, does not wait for a response.
     * Callback will initiate a EndDeviceStatesNotification on receivedLastResultString.
     */
    private void doPlcOutagePing(List<CommandRequestDevice> plcCommandRequests, final MultispeakVendor mspVendor,
            final String transactionId, final String responseUrl) {

        YukonUserContext yukonUserContext = YukonUserContext.system;
        CommandCompletionCallbackAdapter<CommandRequestDevice> callback =
            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

                @Override
                public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    log.warn("receivedIntermediateError for odEvent " + error.getDescription());
                }

                @Override
                public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                    log.debug("receivedIntermediateResultString for odEvent " + value);
                }

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    log.debug("receivedValue for odEvent" + value);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    log.debug("receivedLastResultString for odEvent " + value);
                    SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());

                    Date now = new Date(); // may need to get this from the porter Return Message, but for now
                                           // "now" will do.
                    EndDeviceStateType deviceStateType = new EndDeviceStateType();
                    deviceStateType.setValue(getForStatusCode(0));
                    EndDeviceState endDeviceState = buildEndDeviceStates(yukonMeter, deviceStateType, now, value);
                    sendEndDeviceStatesNotification(yukonMeter, mspVendor, transactionId, responseUrl, endDeviceState);
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    log.warn("receivedLastError for odEvent " + error.getDescription());
                    SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());

                    Date now = new Date(); // may need to get this from the porter Return Message, but for now
                                           // "now" will do.
                    EndDeviceStateType deviceStateType = new EndDeviceStateType();
                    deviceStateType.setValue(getForStatusCode(error.getErrorCode()));
                    EndDeviceState endDeviceState =
                        buildEndDeviceStates(yukonMeter, deviceStateType, now, error.getPorter());
                    sendEndDeviceStatesNotification(yukonMeter, mspVendor, transactionId, responseUrl, endDeviceState);
                }

                @Override
                public void complete() {
                    log.debug("complete for odEvent");
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    log.warn("processingExceptionOccured for odEvent " + reason);
                }
            };

        commandRequestDeviceExecutor.execute(plcCommandRequests, callback,
            DeviceRequestType.MULTISPEAK_OUTAGE_DETECTION_PING_COMMAND, yukonUserContext.getYukonUser());
    }

    private EndDeviceStateKind getForStatusCode(int statusCode) {
        for (EndDeviceStateKind deviceState : supportedEndDeviceStateTypes) {
            if (outageConfig.get(deviceState).contains(statusCode)) {
                return deviceState;
            }
        }
        return EndDeviceStateKind.NO_RESPONSE;
    }

    private void sendEndDeviceStatesNotification(SimpleMeter meter, MultispeakVendor mspVendor, String transactionId,
            String responseUrl, EndDeviceState endDeviceState) {
        try {
            EndDeviceStatesNotification endDeviceStatesNotification = new EndDeviceStatesNotification();
            ArrayOfEndDeviceState arrayOfEndDeviceState = new ArrayOfEndDeviceState();
            List<EndDeviceState> endDeviceStateList = arrayOfEndDeviceState.getEndDeviceState();
            endDeviceStateList.add(endDeviceState);

            endDeviceStatesNotification.setArrayOfEndDeviceState(arrayOfEndDeviceState);
            endDeviceStatesNotification.setTransactionID(transactionId);

            log.info("Sending EndDeviceStatesNotification (" + responseUrl + "): Meter Number " + meter.toString()
                + " EndDeviceStateType: " + endDeviceState.getDeviceState().getValue());

            notClient.endDeviceStatesNotification(mspVendor, responseUrl, endDeviceStatesNotification);

            List<ErrorObject> errObjects = new ArrayList<>();
            errObjects = multispeakFuncs.getErrorObjectsFromResponse();

            multispeakEventLogService.notificationResponse("EndDeviceStatesNotification", transactionId,
                endDeviceState.getReferableID(), endDeviceState.getDeviceState().getValue().toString(),
                CollectionUtils.size(errObjects), responseUrl);

            if (CollectionUtils.isNotEmpty(errObjects)) {
                multispeakFuncs.logErrorObjects(responseUrl, "EndDeviceStatesNotification", errObjects);
            }

        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + responseUrl + " - InitiateEndDevicePings (" + mspVendor.getCompanyName()
                + ")");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }

    }

    private EndDeviceState buildEndDeviceStates(SimpleMeter yukonMeter, EndDeviceStateType deviceStateType,
            Date timestamp, String resultString) {

        EndDeviceState endDeviceState = null;
        endDeviceState = new EndDeviceState();
        String meterNumber = yukonMeter.getMeterNumber();
        ObjectRef objectRef = new ObjectRef();

        objectRef.setNoun(new QName("http://www.multispeak.org/V5.0/commonTypes", "objectRef", "com"));
        objectRef.setPrimaryIdentifierValue(meterNumber);
        objectRef.setValue(meterNumber);
        objectRef.setRegisteredName("Eaton");
        objectRef.setSystemName("Yukon");

        endDeviceState.setDeviceReference(objectRef);
        endDeviceState.setDeviceState(deviceStateType);

        return endDeviceState;
    }

    @Override
    public RCDStateKind CDMeterState(final MultispeakVendor mspVendor, final YukonMeter meter)
            throws MultispeakWebServiceException {

        log.info("Received " + meter.getMeterNumber() + " for CDDeviceStates from " + mspVendor.getCompanyName());
        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException(
                "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        List<YukonMeter> allPaosToRead = Collections.singletonList(meter);
        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.DISCONNECT_STATUS);

        LACWaitableDeviceAttributeReadCallback waitableCallback =
            new LACWaitableDeviceAttributeReadCallback(mspVendor.getRequestMessageTimeout()) {

                @Override
                public void complete() {
                    super.complete();
                    // do we need to do anything here once we received all of the data?
                    log.debug("deviceAttributeReadCallback.complete for cdEvent");
                }

                @Override
                public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                    // the following is expensive but unavoidable until PointData is
                    // changed
                    PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                    Set<BuiltInAttribute> thisAttribute =
                        attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(),
                            attributes);
                    if (thisAttribute.contains(BuiltInAttribute.DISCONNECT_STATUS)) {
                        setRCDStateKind(multispeakFuncs.getRCDStateKind(meter, value));
                    } else {
                        return;
                    }
                }

                @Override
                public void receivedLastValue(PaoIdentifier pao, String value) {
                    log.debug("deviceAttributeReadCallback.receivedLastValue for cdEvent");
                }

                @Override
                public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                    // do we need to send something to the foreign system here?
                    log.warn("received error for " + pao + ": " + error);
                }

                @Override
                public void receivedException(SpecificDeviceErrorDescription error) {
                    log.warn("received exception in meterReadEvent callback: " + error);
                }

            };
        multispeakEventLogService.initiateMeterRead(meter.getMeterNumber(), meter, "N/A", "GetCDDeviceStates",
            mspVendor.getCompanyName());
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, waitableCallback,
            DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());
        try {
            waitableCallback.waitForCompletion();
        } catch (InterruptedException e) { /* Ignore */}
        return waitableCallback.getRCDStateKind();
    }

    /**
     * Wrapper class for holding attribute read response value.
     */
    private abstract class LACWaitableDeviceAttributeReadCallback extends WaitableDeviceAttributeReadCallback {
        private RCDStateKind rcdStateKind = RCDStateKind.UNKNOWN;

        public LACWaitableDeviceAttributeReadCallback(long timeoutInMillis) {
            super(timeoutInMillis);
        }

        public void setRCDStateKind(RCDStateKind rcdStateKind) {
            this.rcdStateKind = rcdStateKind;
        }

        public RCDStateKind getRCDStateKind() {
            return rcdStateKind;
        }
    };

    @Override
    public synchronized List<ErrorObject> cdEvent(final MultispeakVendor mspVendor,
            List<ConnectDisconnectEvent> cdEvents, final String transactionId, final String responseUrl)
            throws MultispeakWebServiceException {

        if (!porterConnection.isValid()) {
            throw new MultispeakWebServiceException(
                "Connection to 'Yukon Port Control Service' is not valid.  Please contact your Yukon Administrator.");
        }

        log.info("Received " + cdEvents.size() + " Meter(s) for Connect/Disconnect from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateCDRequest(cdEvents.size(), "InitiateConnectDisconnect",
            mspVendor.getCompanyName());

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        List<CommandRequestDevice> plcCommandRequests = Lists.newArrayList();

        for (ConnectDisconnectEvent cdEvent : cdEvents) {
            final String meterNumber = getMeterNumberFromCDEvent(cdEvent);

            try {
                YukonMeter meter = mspMeterDao.getMeterForMeterNumber(meterNumber);
                MspLoadActionCode mspLoadActionCode =
                    MspLoadActionCode.getForLoadActionCode(cdEvent.getLoadActionCode().getValue());

                // validate is CD supported meter
                if (!mspMeterDao.isCDSupportedMeter(meterNumber)) {
                    ErrorObject err =
                        mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                            + ") - Invalid Yukon Connect/Disconnect Meter.", "Meter", "CDEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
                }

                // check for rf disconnect meter type and perform action
                boolean isRfnDisconnect =
                    paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(), PaoTag.DISCONNECT_RFN);
                if (isRfnDisconnect) {
                    RfnMeter rfnMeter = (RfnMeter) meter;
                    multispeakEventLogService.initiateCD(meter.getMeterNumber(), meter, mspLoadActionCode.toString(),
                        transactionId, "InitiateConnectDisconnect", mspVendor.getCompanyName());
                    doRfnConnectDisconnect(rfnMeter,
                        mspLoadActionCode.getRfnMeterDisconnectStatusType(configurationSource), mspVendor,
                        transactionId, responseUrl);
                    continue;
                }

                // Assume plc if we made it this far, validate meter can receive porter command requests and
                // command string exists, then perform action
                boolean canInitiatePorterRequest =
                    paoDefinitionDao.isTagSupported(meter.getPaoIdentifier().getPaoType(),
                        PaoTag.PORTER_COMMAND_REQUESTS);
                if (!canInitiatePorterRequest || StringUtils.isBlank(mspLoadActionCode.getPlcCommandString())) {
                    ErrorObject err =
                        mspObjectDao.getErrorObject(meterNumber, "MeterNumber (" + meterNumber
                            + ") - Meter cannot receive requests from porter or no control command exists. "
                            + "LoadActionCode=" + cdEvent.getLoadActionCode(), "Meter", "CDEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                } else { // build up a list of plc command requests (to be sent later)
                    CommandRequestDevice request = new CommandRequestDevice();
                    request.setDevice(new SimpleDevice(meter));
                    request.setCommandCallback(new CommandCallbackBase(mspLoadActionCode.getPlcCommandString()));
                    plcCommandRequests.add(request);
                    multispeakEventLogService.initiateCD(meter.getMeterNumber(), meter, mspLoadActionCode.toString(),
                        (cdEvent.getCDReasonCode() == null ? "unknown" : cdEvent.getCDReasonCode().getValue() != null
                            ? cdEvent.getCDReasonCode().getValue().value() : "unknown"), "InitiateConnectDisconnect",
                        mspVendor.getCompanyName());
                }
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, "InitiateConnectDisconnect",
                    mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "Meter", "CDEvent",
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(e);
            }

        }

        // perform plc action on list of commandRequests
        doPlcConnectDisconnect(plcCommandRequests, mspVendor, transactionId, responseUrl);

        return errorObjects;
    }

    /**
     * Performs the PLC meter disconnect.
     * Returns immediately, does not wait for a response.
     * Callback will initiate a cdEventNotification on receivedValue.
     */
    private void doPlcConnectDisconnect(List<CommandRequestDevice> plcCommandRequests,
            final MultispeakVendor mspVendor, final String transactionId, final String responseUrl) {

        YukonUserContext yukonUserContext = YukonUserContext.system;

        CommandCompletionCallbackAdapter<CommandRequestDevice> callback =
            new CommandCompletionCallbackAdapter<CommandRequestDevice>() {

                @Override
                public void receivedIntermediateError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    log.warn("receivedIntermediateError for cdEvent " + error.getDescription());
                }

                @Override
                public void receivedIntermediateResultString(CommandRequestDevice command, String value) {
                    log.debug("receivedIntermediateResultString for cdEvent " + value);
                }

                @Override
                public void receivedValue(CommandRequestDevice command, PointValueHolder value) {
                    log.debug("receivedValue for cdEvent" + value);
                    Disconnect410State state =
                        Disconnect410State.getByRawState(new Double(value.getValue()).intValue());
                    MspLoadActionCode mspLoadActionCode = MspLoadActionCode.getForPlcState(state);
                    SimpleMeter yukonMeter = meterDao.getSimpleMeterForId(command.getDevice().getDeviceId());
                    sendCDEventNotification(yukonMeter, mspLoadActionCode.getLoadActionCode(), mspVendor,
                        transactionId, responseUrl);
                }

                @Override
                public void receivedLastResultString(CommandRequestDevice command, String value) {
                    log.debug("receivedLastResultString for cdEvent " + value);
                }

                @Override
                public void receivedLastError(CommandRequestDevice command, SpecificDeviceErrorDescription error) {
                    log.warn("receivedLastError for cdEvent " + error.getDescription());
                }

                @Override
                public void complete() {
                    log.debug("complete for cdEvent");
                }

                @Override
                public void processingExceptionOccured(String reason) {
                    log.warn("processingExceptionOccured for cdEvent " + reason);
                }
            };

        commandRequestDeviceExecutor.execute(plcCommandRequests, callback,
            DeviceRequestType.MULTISPEAK_CONNECT_DISCONNECT, yukonUserContext.getYukonUser());
    }

    /**
     * Performs the RFN meter disconnect.
     * Returns immediately, does not wait for a response.
     * Callback will initiate a cdEventNotification on success or error.
     */
    private void doRfnConnectDisconnect(final RfnMeter meter, RfnMeterDisconnectStatusType action,
            final MultispeakVendor mspVendor, final String transactionId, final String responseUrl) {

        RfnMeterDisconnectCallback rfnCallback = new RfnMeterDisconnectCallback() {

            @Override
            public void receivedSuccess(RfnMeterDisconnectState state, PointValueQualityHolder pointData) {
                log.debug("rfn receivedSuccess for cdEvent " + state);
                MspLoadActionCode mspLoadActionCode =
                    MspLoadActionCode.getForRfnState(RfnDisconnectStatusState.getForNmState(state));
                sendCDEventNotification(meter, mspLoadActionCode.getLoadActionCode(), mspVendor, transactionId,
                    responseUrl);
            }

            @Override
            public void receivedError(MessageSourceResolvable message, RfnMeterDisconnectState state) {
                log.warn("rfn receivedError for cdEvent " + message);
                sendCDEventNotification(meter, LoadActionCodeKind.UNKNOWN, mspVendor, transactionId, responseUrl);
            }

            @Override
            public void processingExceptionOccured(MessageSourceResolvable message) {
                log.warn("rfn processingExceptionOccured for cdEvent " + message);
            }

            @Override
            public void complete() {
                log.debug("rfn complete for cdEvent");
            }
        };

        rfnMeterDisconnectService.send(meter, action, rfnCallback);
    }

    /**
     * Returns meterNumber from ConnectDisconnectEvent object. Tries to load
     * from CDEvent's meterNo, then meterId (SEDC specific), then objectId.
     * 
     * @param cdEvent
     * @return
     */
    private String getMeterNumberFromCDEvent(ConnectDisconnectEvent cdEvent) {
        String meterNumber =
            cdEvent.getCDDeviceIdentifier() == null ? null : cdEvent.getCDDeviceIdentifier().getMeterID() != null
                ? cdEvent.getCDDeviceIdentifier().getMeterID().getMeterName() : null;

        // Try to load MeterNumber from another element
        if (StringUtils.isBlank(meterNumber)) {
            // TODO In MSP 3 MeterNO and MeterID both are present in request, Customer SEDC is using MeterID
            // but it is not present in msp 5.0
            if (StringUtils.isBlank(meterNumber)) { // this is only necessary for old integrations; moving
                                                    // forward, objectId will be a unique identifier of the
                                                    // cdevent.
                meterNumber = cdEvent.getReferableID();
            }
        }
        if (meterNumber != null) {
            meterNumber = meterNumber.trim();
        }
        return meterNumber;
    }

    /**
     * Initiates a CDStatesChangedNotification message to vendor.
     * 
     * @param yukonMeter - meter
     * @param loadActionCode - loadActionCode
     * @param mspVendor - msp vendor that made the inital request
     * @param transactionId - the token provided from the initial request
     */
    private void sendCDEventNotification(SimpleMeter yukonMeter, LoadActionCodeKind loadActionCodeKind,
            MultispeakVendor mspVendor, String transactionId, String responseUrl) {

        log.info("Sending CDStatesChangedNotification (" + responseUrl + "): Meter Number "
            + yukonMeter.getMeterNumber() + " Code:" + loadActionCodeKind);
        try {
            CDStatesChangedNotification cdStatesChangedNotification = new CDStatesChangedNotification();
            ArrayOfCDStateChange arrayOfCDStateChange = objectFactory.createArrayOfCDStateChange();
            List<CDStateChange> cdStateChange = arrayOfCDStateChange.getCDStateChange();
            CDStateChange stateChange = new CDStateChange();
            CDDeviceIdentifier cdDeviceIdentifier = new CDDeviceIdentifier();
            MeterID meterID = new MeterID();
            meterID.setMeterName(yukonMeter.getMeterNumber());
            meterID.setRegisteredName(MultispeakDefines.REGISTERED_NAME);
            meterID.setServiceType(ServiceKind.ELECTRIC);
            meterID.setSystemName(MultispeakDefines.MSP_APPNAME_YUKON);
            cdDeviceIdentifier.setMeterID(meterID);
            stateChange.setCDDeviceIdentifier(cdDeviceIdentifier);
            LoadActionCode loadActionCode = new LoadActionCode();
            loadActionCode.setValue(loadActionCodeKind);
            stateChange.setStateChange(loadActionCode);
            cdStateChange.add(stateChange);
            cdStatesChangedNotification.setArrayOfCDStateChange(arrayOfCDStateChange);
            cdStatesChangedNotification.setTransactionID(transactionId);
            notClient.cdStatesChangedNotification(mspVendor, responseUrl, cdStatesChangedNotification);
            multispeakEventLogService.notificationResponse("CDStatesChangedNotification", transactionId,
                yukonMeter.getMeterNumber(), loadActionCodeKind.value(), -1, responseUrl);

        } catch (MultispeakWebServiceClientException e) {
            log.error("TargetService: " + responseUrl + " - initiateConnectDisconnect (" + mspVendor.getCompanyName()
                + ")");
            log.error("MultispeakWebServiceClientException: " + e.getMessage());
        }
    }

    @Required
    public void setPorterConnection(BasicServerConnection porterConnection) {
        this.porterConnection = porterConnection;
    }

    @Override
    public synchronized List<ErrorObject> meterReadEvent(final MultispeakVendor mspVendor, List<MeterID> meterIDs,
            final String transactionID, final String responseUrl) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

        log.info("Received " + mspMeters.size() + " Meter(s) for MeterReading from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterReadRequest(mspMeters.size(), "InitiateMeterReadingsByMeterIDs",
            mspVendor.getCompanyName());
        List<YukonMeter> allPaosToRead = Lists.newArrayListWithCapacity(mspMeters.size());
        ListMultimap<String, YukonMeter> meterNumberToMeterMap =
            meterDao.getMetersMapForMeterNumbers(Lists.newArrayList(mspMeters));
        boolean excludeDisabled = globalSettingDao.getBoolean(GlobalSettingType.MSP_EXCLUDE_DISABLED_METERS);

        for (String meterNumber : mspMeters) {
            List<YukonMeter> meters = meterNumberToMeterMap.get(meterNumber); // this will most likely be size
                                                                              // 1
            for (YukonMeter meter : meters) {
                if (meter == null) {
                    multispeakEventLogService.meterNotFound(meterNumber, "InitiateMeterReadingsByMeterIDs",
                        mspVendor.getCompanyName());
                    ErrorObject err =
                        mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID", "MeterReadEvent",
                            mspVendor.getCompanyName());
                    errorObjects.add(err);
                    continue;
                }

                if (excludeDisabled && meter.isDisabled()) {
                    log.debug("Meter " + meter.getMeterNumber() + " is disabled, skipping.");
                    continue;
                }

                allPaosToRead.add(meter);
                multispeakEventLogService.initiateMeterRead(meterNumber, meter, transactionID,
                    "InitiateMeterReadingsByMeterIDs", mspVendor.getCompanyName());
            }
        }

        final ImmutableMap<PaoIdentifier, YukonMeter> meterLookup = PaoUtils.indexYukonPaos(allPaosToRead);

        final EnumSet<BuiltInAttribute> attributes = EnumSet.of(BuiltInAttribute.USAGE, BuiltInAttribute.PEAK_DEMAND);

        final ConcurrentMap<PaoIdentifier, MeterReadUpdater> updaterMap =
            new MapMaker().concurrencyLevel(2).initialCapacity(mspMeters.size()).makeMap();

        DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

            @Override
            public void complete() {
                // do we need to do anything here once we received all of the data?
                log.debug("deviceAttributeReadCallback.complete for meterReadEvent");
            }

            @Override
            public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                // the following is expensive but unavoidable until PointData is changed
                PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                Set<BuiltInAttribute> thisAttribute =
                    attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(), attributes);

                if (log.isDebugEnabled()) {
                    log.debug("deviceAttributeReadCallback.receivedValue for meterReadEvent: "
                        + paoPointIdentifier.toString() + " - " + value.getPointDataTimeStamp() + " - "
                        + value.getValue());
                }

                if (thisAttribute.isEmpty()) {
                    log.debug("data received but no attribute found for point");
                    return;
                }
                for (BuiltInAttribute attribute : thisAttribute) {
                    log.debug("data received for some attribute: " + thisAttribute.toString());

                    // Get a new updater object for the current value
                    MeterReadUpdater meterReadUpdater =
                        meterReadProcessingService.buildMeterReadUpdater(attribute, value);
                    // if the map is empty, place the updater into it
                    MeterReadUpdater oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                    while (oldValue != null) {
                        // looks like the map was not empty, combine the existing updater with the
                        // new one and then place it back in the map, but we must be careful
                        // that someone hasn't changed the map out from under us (thus the while loop)
                        MeterReadUpdaterChain chain = new MeterReadUpdaterChain(oldValue, meterReadUpdater);
                        boolean success = updaterMap.replace(pao, oldValue, chain);
                        if (success)
                            break;
                        oldValue = updaterMap.putIfAbsent(pao, meterReadUpdater);
                    }
                }
            }

            /**
             * The unfortunate part is that this method is going to fire off a readingChangeNotification for
             * each
             * set of attributes that happened to be able to be collected using the same command
             * (as derived by MeterReadCommandGenerationService.getMinimalCommandSet(...))
             */
            @Override
            public void receivedLastValue(PaoIdentifier pao, String value) {
                YukonMeter meter = meterLookup.get(pao);
                MeterReading meterRead = meterReadProcessingService.createMeterRead(meter);

                // because we were so careful about putting updater or updater chains into the
                // map, we know we can safely remove it and generate a MeterRead from it
                // whenever we want; but this happens to be a perfect time
                MeterReadUpdater updater = updaterMap.remove(pao);
                if (updater != null) {
                    updater.update(meterRead);

                    try {
                        log.info("Sending MeterReadingsNotification (" + responseUrl + "): Meter Number "
                            + meterRead.getReferableID());
                        final MeterReadingsNotification meterReadingsNotification = new MeterReadingsNotification();
                        ArrayOfMeterReading arrayOfMeterReading = objectFactory.createArrayOfMeterReading();
                        arrayOfMeterReading.getMeterReading().add(meterRead);
                        meterReadingsNotification.setArrayOfMeterReading(arrayOfMeterReading);

                        meterReadingsNotification.setTransactionID(transactionID);
                        log.info("Sending MeterReadingsNotification (" + responseUrl + "): Meter Number "
                            + meterRead.getReferableID());
                        notClient.meterReadingsNotification(mspVendor, responseUrl, meterReadingsNotification);

                        List<ErrorObject> errObjects = new ArrayList<>();
                        errObjects = multispeakFuncs.getErrorObjectsFromResponse();
                        multispeakEventLogService.notificationResponse("MeterReadingsNotification", transactionID,
                            meterRead.getReferableID(), meterRead.getMeterID().getServiceType().toString(),
                            CollectionUtils.size(errObjects), responseUrl);
                        if (CollectionUtils.isNotEmpty(errObjects)) {
                            multispeakFuncs.logErrorObjects(responseUrl, "MeterReadingsNotification", errObjects);
                        }

                    } catch (MultispeakWebServiceClientException e) {
                        log.warn("caught exception in receivedValue of meterReadEvent", e);
                    }
                } else {
                    log.info("No matching attribute to point mappings identified. No notification message triggered.");
                }
            }

            @Override
            public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                // do we need to send something to the foreign system here?
                log.warn("received error for " + pao + ": " + error);
            }

            @Override
            public void receivedException(SpecificDeviceErrorDescription error) {
                log.warn("received exception in meterReadEvent callback: " + error);
            }
        };
        deviceAttributeReadService.initiateRead(allPaosToRead, attributes, callback,
            DeviceRequestType.MULTISPEAK_METER_READ_EVENT, UserUtils.getYukonUser());

        return errorObjects;
    }

    @Override
    public synchronized List<ErrorObject> blockMeterReadEvent(final MultispeakVendor mspVendor, List<MeterID> meterIDs,
            final FormattedBlockProcessingService<Block> blockProcessingService, final String transactionId,
            final String responseUrl) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        log.info("Received " + meterIDs.size() + " for BlockMeterReading from " + mspVendor.getCompanyName());
        multispeakEventLogService.initiateMeterReadRequest(1, "InitiateMeterReadingsByReadingTypeCodes",
            mspVendor.getCompanyName());

        final EnumSet<BuiltInAttribute> attributes = blockProcessingService.getAttributeSet();

        // retain only those that ARE readable.
        // profile attributes are not readable right now, which means that the LoadBlock specifically, will
        // not get many new values.
        // this is a change from before where we built up a command to retrieve at least the last 6 profile
        // reads. Oh well...
        // a solution could be implemented a new command for collected the "latest" profile reads available.
        attributes.retainAll(attributeService.getReadableAttributes());

        for (MeterID meterID : meterIDs) {
            String meter = meterID.getMeterName();
            YukonMeter paoToRead = mspMeterDao.getMeterForMeterNumber(meter);
            multispeakEventLogService.initiateMeterRead(meter, paoToRead, transactionId,
                "InitiateMeterReadingsByReadingTypeCodes", mspVendor.getCompanyName());

            final ConcurrentMap<PaoIdentifier, FormattedBlockUpdater<Block>> updaterMap =
                new MapMaker().concurrencyLevel(2).initialCapacity(1).makeMap();

            DeviceAttributeReadCallback callback = new DeviceAttributeReadCallback() {

                /**
                 * Because we only have one meterNumber that is being processed, we will wait until all reads
                 * are
                 * returned to fire off the formattedBlockNotification.
                 * This allows us to group them all together into one Block, which is desired.
                 * This is different than the meterReadEvent which may have multiple meterNumbers it is
                 * processing and
                 * therefore fires notifications for each read that comes in (basically using this same layout
                 * of code, only
                 * implemented in receivedLastValue instead)
                 */
                @Override
                public void complete() {

                    Block block = blockProcessingService.createBlock(paoToRead);

                    // because we were so careful about putting updater or updater chains into the
                    // map, we know we can safely remove it and generate a MeterRead from it
                    // whenever we want; but this happens to be a perfect time
                    FormattedBlockUpdater<Block> updater = updaterMap.remove(paoToRead.getPaoIdentifier());
                    if (updater != null) {
                        updater.update(block);
                    } else {
                        log.warn("no data updates for meter. notification will contain no readings");
                    }

                    final FormattedBlockNotification formattedBlockNotification = new FormattedBlockNotification();
                    ArrayOfFormattedBlock arrayOfFormattedBlock = objectFactory.createArrayOfFormattedBlock();
                    FormattedBlock formattedBlock = blockProcessingService.createMspFormattedBlock(block);
                    formattedBlockNotification.setTransactionID(transactionId);
                    arrayOfFormattedBlock.getFormattedBlock().add(formattedBlock);
                    formattedBlockNotification.setArrayOfFormattedBlock(arrayOfFormattedBlock);
                    try {
                        notClient.formattedBlockNotification(mspVendor, responseUrl, formattedBlockNotification);

                        List<ErrorObject> errObjects = new ArrayList<>();
                        errObjects = multispeakFuncs.getErrorObjectsFromResponse();

                        multispeakEventLogService.notificationResponse("FormattedBlockNotification", transactionId,
                            block.getObjectId(), "", CollectionUtils.size(errObjects), responseUrl);
                        if (CollectionUtils.isNotEmpty(errObjects)) {
                            multispeakFuncs.logErrorObjects(responseUrl, "FormattedBlockNotification", errObjects);
                        }

                    } catch (MultispeakWebServiceClientException e) {
                        log.warn("caught exception in receivedValue of formattedBlockEvent", e);
                    }
                }

                @Override
                public void receivedValue(PaoIdentifier pao, PointValueHolder value) {
                    // the following is expensive but unavoidable until PointData is changed
                    PaoPointIdentifier paoPointIdentifier = pointDao.getPaoPointIdentifier(value.getId());
                    Set<BuiltInAttribute> thisAttribute =
                        attributeService.findAttributesForPoint(paoPointIdentifier.getPaoTypePointIdentifier(),
                            attributes);
                    if (thisAttribute.isEmpty()) {
                        return;
                    }

                    // Get a new updater object for the current value
                    for (BuiltInAttribute attribute : thisAttribute) {
                        FormattedBlockUpdater<Block> formattedBlockUpdater =
                            blockProcessingService.buildFormattedBlockUpdater(attribute, value);

                        // if the map is empty, place the updater into it
                        FormattedBlockUpdater<Block> oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);

                        while (oldValue != null) {
                            // looks like the map was not empty, combine the existing updater with the
                            // new one and then place it back in the map, but we must be careful
                            // that someone hasn't changed the map out from under us (thus the while loop)
                            FormattedBlockUpdaterChain<Block> chain =
                                new FormattedBlockUpdaterChain<Block>(oldValue, formattedBlockUpdater);
                            boolean success = updaterMap.replace(pao, oldValue, chain);
                            if (success)
                                break;
                            oldValue = updaterMap.putIfAbsent(pao, formattedBlockUpdater);
                        }
                    }
                }

                @Override
                public void receivedLastValue(PaoIdentifier pao, String value) {
                    log.debug("deviceAttributeReadCallback.receivedLastValue for formattedBlockEvent");
                }

                @Override
                public void receivedError(PaoIdentifier pao, SpecificDeviceErrorDescription error) {
                    // do we need to send something to the foreign system here?
                    log.warn("received error for " + pao + ": " + error);
                }

                @Override
                public void receivedException(SpecificDeviceErrorDescription error) {
                    log.warn("received exception in FormattedBlockEvent callback: " + error);
                }
            };

            multispeakEventLogService.initiateMeterRead(meter, paoToRead, transactionId,
                "InitiateMeterReadingsByReadingTypeCodes", mspVendor.getCompanyName());
            deviceAttributeReadService.initiateRead(Collections.singleton(paoToRead), attributes, callback,
                DeviceRequestType.MULTISPEAK_FORMATTED_BLOCK_READ_EVENT, UserUtils.getYukonUser());
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> initiateEndDeviceEventMonitoring(MultispeakVendor mspVendor, List<MeterID> meterIDs) {

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

        return addToGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "InitiateEndDeviceEventMonitoring", mspVendor);
    }

    @Override
    public List<ErrorObject> cancelEndDeviceEventMonitoring(MultispeakVendor mspVendor, String meterID) {

        List<String> mspMeters = new ArrayList<String>();
        mspMeters.add(meterID);
        return removeFromGroup(mspMeters, SystemGroupEnum.USAGE_MONITORING, "CancelEndDeviceEventMonitoring", mspVendor);
    }

    /**
     * Helper method to remove meterNos from systemGroup
     */
    private List<ErrorObject> removeFromGroup(List<String> meterIDs, SystemGroupEnum systemGroup, String mspMethod,
            MultispeakVendor mspVendor) {
        return removeFromGroupAndEnable(meterIDs, systemGroup, mspMethod, mspVendor, false);
    }

    /**
     * Helper method to remove meterNos from systemGroup
     * 
     * @param disable - when true, the meter will be enabled. Else no change.
     */
    private List<ErrorObject> removeFromGroupAndEnable(List<String> meterIDs, SystemGroupEnum systemGroup,
            String mspMethod, MultispeakVendor mspVendor, boolean enable) {

        ArrayList<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (String meterNumber : meterIDs) {
            try {
                YukonMeter meter = meterDao.getForMeterNumber(meterNumber);
                if (enable && meter.isDisabled()) {
                    deviceDao.enableDevice(meter);
                }
                removeFromGroup(meter, systemGroup, mspMethod, mspVendor);
            } catch (NotFoundException e) {
                multispeakEventLogService.meterNotFound(meterNumber, mspMethod, mspVendor.getCompanyName());
                ErrorObject err =
                    mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID", "RemoveFromGroup",
                        mspVendor.getCompanyName());
                errorObjects.add(err);
                log.error(e);
            }
        }

        return errorObjects;
    }

    @Override
    public List<ErrorObject> setDisconnectedStatus(MultispeakVendor mspVendor, List<MeterID> meterIDs) {
        boolean disable = globalSettingDao.getBoolean(GlobalSettingType.MSP_DISABLE_DISCONNECT_STATUS);

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

        return addToGroupAndDisable(mspMeters, SystemGroupEnum.DISCONNECTED_STATUS, "SetDisconnectedStatus", mspVendor,
            disable);
    }

    @Override
    public List<ErrorObject> clearDisconnectedStatus(MultispeakVendor mspVendor, List<MeterID> meterIDs) {
        // For the cancel method, the MSP_DISABLE_DISCONNECT_STATUS setting
        // shall be reversed to "undo" the disable.
        boolean enable = globalSettingDao.getBoolean(GlobalSettingType.MSP_DISABLE_DISCONNECT_STATUS);

        List<String> mspMeters = meterIDs.stream().map(meterID -> meterID.getMeterName()).collect(Collectors.toList());

        return removeFromGroupAndEnable(mspMeters, SystemGroupEnum.DISCONNECTED_STATUS, "CancelDisconnectedStatus",
            mspVendor, enable);
    }

    @Override
    public ErrorObject deleteMeterGroups(String groupName, MultispeakVendor mspVendor) {
        ErrorObject errorObject = null;
        try {

            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
            deviceGroupEditorDao.removeGroup(storedGroup);
        } catch (NotFoundException e) {
            errorObject =
                mspObjectDao.getNotFoundErrorObject(groupName, "meterGroupId", "meterGroupID", "deleteGroup",
                    mspVendor.getCompanyName());
            return errorObject;
        }
        return errorObject;
    }

    @Override
    public List<ErrorObject> removeMetersFromGroup(String groupName, List<MeterID> meterIDs, MultispeakVendor mspVendor) {
        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();
        List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();

        try {
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, false);
            String meterNumber = null;
            if (meterIDs != null) {
                for (MeterID meterID : meterIDs) {
                    try {
                        meterNumber = meterID.getMeterName();
                        SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                        yukonDevices.add(yukonDevice);
                    } catch (EmptyResultDataAccessException e) {
                        String exceptionMessage = "Unknown meter number " + meterNumber;
                        ErrorObject errorObject =
                            mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID",
                                "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                        errorObjects.add(errorObject);
                        log.error(e);
                    } catch (IncorrectResultSizeDataAccessException e) {
                        String exceptionMessage = "Duplicate meters were found for this meter number  " + meterNumber;
                        ErrorObject errorObject =
                            mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID",
                                "removeMetersFromGroup", mspVendor.getCompanyName(), exceptionMessage);
                        errorObjects.add(errorObject);
                        log.error(e);
                    }
                }
            }

            deviceGroupMemberEditorDao.removeDevices(storedGroup, yukonDevices);
            multispeakEventLogService.removeMetersFromGroup(yukonDevices.size(), storedGroup.getFullName(),
                "RemoveMetersFromMeterGroup", mspVendor.getCompanyName());
        } catch (NotFoundException e) {
            ErrorObject errorObject =
                mspObjectDao.getNotFoundErrorObject(groupName, "GroupName", "meterGroupID", "removeMetersFromGroup",
                    mspVendor.getCompanyName());
            errorObjects.add(errorObject);
            log.error(e);
        }
        return errorObjects;
    }

    @Override
    public List<ErrorObject> addMetersToGroup(List<MeterGroup> meterGroups, String mspMethod, MultispeakVendor mspVendor) {

        List<ErrorObject> errorObjects = new ArrayList<ErrorObject>();

        for (MeterGroup meterGroup : meterGroups) {
            // Convert MeterNumbers to YukonDevices
            List<SimpleDevice> yukonDevices = new ArrayList<SimpleDevice>();
            if (meterGroup.getMeterIDs() != null) {
                String meterNumber = null;
                for (MeterID meterID : meterGroup.getMeterIDs().getMeterID()) {
                    try {
                        meterNumber = meterID.getMeterName();
                        SimpleDevice yukonDevice = deviceDao.getYukonDeviceObjectByMeterNumber(meterNumber);
                        yukonDevices.add(yukonDevice);
                    } catch (EmptyResultDataAccessException e) {
                        String exceptionMessage = "Unknown meter number " + meterNumber;
                        ErrorObject errorObject =
                            mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID",
                                "addMetersToGroup", mspVendor.getCompanyName(), exceptionMessage);
                        errorObjects.add(errorObject);
                        log.error(e);
                    } catch (IncorrectResultSizeDataAccessException e) {
                        String exceptionMessage = "Duplicate meters were found for this meter number  " + meterNumber;
                        ErrorObject errorObject =
                            mspObjectDao.getNotFoundErrorObject(meterNumber, "MeterNumber", "MeterID",
                                "addMetersToGroup", mspVendor.getCompanyName(), exceptionMessage);
                        errorObjects.add(errorObject);
                        log.error(e);
                    }
                }

            }

            String groupName = meterGroup.getGroupName();
            StoredDeviceGroup storedGroup = deviceGroupEditorDao.getStoredGroup(groupName, true);
            deviceGroupMemberEditorDao.addDevices(storedGroup, yukonDevices);
            multispeakEventLogService.addMetersToGroup(yukonDevices.size(), storedGroup.getFullName(), mspMethod,
                mspVendor.getCompanyName());
        }
        return errorObjects;
    }
}