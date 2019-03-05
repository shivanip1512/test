package com.cannontech.util;

import static org.junit.Assert.fail;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.cannontech.amr.archivedValueExporter.model.ArchivedValuesExportFormatType;
import com.cannontech.amr.archivedValueExporter.model.AttributeField;
import com.cannontech.amr.archivedValueExporter.model.DataExportDelimiter;
import com.cannontech.amr.archivedValueExporter.model.DataSelection;
import com.cannontech.amr.archivedValueExporter.model.FieldType;
import com.cannontech.amr.archivedValueExporter.model.MissingAttribute;
import com.cannontech.amr.archivedValueExporter.model.PadSide;
import com.cannontech.amr.archivedValueExporter.model.ReadingPattern;
import com.cannontech.amr.archivedValueExporter.model.TimestampPattern;
import com.cannontech.amr.archivedValueExporter.model.YukonRoundingMode;
import com.cannontech.amr.archivedValueExporter.model.dataRange.DataRangeType;
import com.cannontech.amr.device.ProfileAttributeChannel;
import com.cannontech.amr.disconnect.model.DisconnectDeviceState;
import com.cannontech.amr.errors.model.SpecificDeviceErrorDescription;
import com.cannontech.amr.meter.dao.impl.MeterDisplayFieldEnum;
import com.cannontech.amr.meter.model.PointSortField;
import com.cannontech.amr.meter.search.model.MeterSearchField;
import com.cannontech.amr.meter.search.model.MspSearchField;
import com.cannontech.amr.phaseDetect.data.DetectedPhase;
import com.cannontech.amr.porterResponseMonitor.model.PorterResponseMonitorMatchStyle;
import com.cannontech.amr.rfn.message.demandReset.RfnMeterDemandResetReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectConfirmationReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectInitialReplyType;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectState;
import com.cannontech.amr.rfn.message.disconnect.RfnMeterDisconnectStatusType;
import com.cannontech.amr.rfn.message.event.Direction;
import com.cannontech.amr.rfn.message.event.RfnConditionDataType;
import com.cannontech.amr.rfn.message.event.RfnConditionType;
import com.cannontech.amr.rfn.message.read.ChannelDataStatus;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingDataReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingReplyType;
import com.cannontech.amr.rfn.message.read.RfnMeterReadingType;
import com.cannontech.amr.rfn.model.CalculationData;
import com.cannontech.amr.rfn.model.RfnInvalidValues;
import com.cannontech.amr.rfn.service.pointmapping.SiPrefix;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoEnabledFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduleGroupRequestExecutionDaoPendingFilter;
import com.cannontech.amr.scheduledGroupRequestExecution.dao.ScheduledGroupRequestExecutionStatus;
import com.cannontech.amr.statusPointMonitoring.model.StatusPointMonitorStateType;
import com.cannontech.cc.model.EconomicEventParticipantSelection.SelectionState;
import com.cannontech.cc.model.ProgramParameterKey;
import com.cannontech.cc.service.CurtailmentEventAction;
import com.cannontech.cc.service.CurtailmentEventState;
import com.cannontech.cc.service.EconomicEventAction;
import com.cannontech.cc.service.EconomicEventState;
import com.cannontech.cc.service.NotificationReason;
import com.cannontech.cc.service.NotificationState;
import com.cannontech.cc.service.NotificationStatus;
import com.cannontech.clientutils.ClientApplicationRememberMe;
import com.cannontech.clientutils.CommonUtils;
import com.cannontech.clientutils.tags.TagUtils;
import com.cannontech.common.alert.model.AlertType;
import com.cannontech.common.bulk.collection.device.model.DeviceCollectionType;
import com.cannontech.common.bulk.collection.device.model.StrategyType;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionByField;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionById;
import com.cannontech.common.bulk.collection.device.persistable.DeviceCollectionDbType;
import com.cannontech.common.bulk.collection.inventory.InventoryCollectionType;
import com.cannontech.common.bulk.field.processor.BlankHandlingEnum;
import com.cannontech.common.bulk.mapper.ObjectMapperFactory.FileMapperEnum;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.bulk.mapper.StringToIntegerMapper;
import com.cannontech.common.bulk.model.AdaStatus;
import com.cannontech.common.bulk.model.PointImportParameters;
import com.cannontech.common.bulk.model.ReadType;
import com.cannontech.common.bulk.service.BulkImportType;
import com.cannontech.common.bulk.service.FdrImportAction;
import com.cannontech.common.chart.model.ChartInterval;
import com.cannontech.common.chart.model.GraphType;
import com.cannontech.common.config.MasterConfigBoolean;
import com.cannontech.common.config.MasterConfigDeprecatedKey;
import com.cannontech.common.config.MasterConfigDouble;
import com.cannontech.common.config.MasterConfigInteger;
import com.cannontech.common.config.MasterConfigString;
import com.cannontech.common.constants.SelectionListCategory;
import com.cannontech.common.constants.YukonDefinition;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.constants.YukonSelectionListOrder;
import com.cannontech.common.csvImport.ImportAction;
import com.cannontech.common.databaseMigration.bean.data.ElementCategoryEnum;
import com.cannontech.common.databaseMigration.bean.database.ReferenceTypeEnum;
import com.cannontech.common.databaseMigration.model.ExportTypeEnum;
import com.cannontech.common.device.commands.CommandRequestExecutionStatus;
import com.cannontech.common.device.commands.CommandRequestType;
import com.cannontech.common.device.commands.CommandRequestUnsupportedType;
import com.cannontech.common.device.commands.impl.CommandRequestExecutionDefaults;
import com.cannontech.common.device.config.model.DeviceConfigCategory;
import com.cannontech.common.device.config.model.DeviceConfigCategoryItem;
import com.cannontech.common.device.config.model.DeviceConfiguration;
import com.cannontech.common.device.config.model.jaxb.EnumOption;
import com.cannontech.common.device.groups.editor.dao.SystemGroupEnum;
import com.cannontech.common.device.groups.service.FixedDeviceGroups;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.device.peakReport.model.PeakReportRunType;
import com.cannontech.common.dynamicBilling.ReadingType;
import com.cannontech.common.events.loggers.ArgEnum;
import com.cannontech.common.events.model.EventSource;
import com.cannontech.common.fdr.FdrDirection;
import com.cannontech.common.fdr.FdrInterfaceOption;
import com.cannontech.common.fdr.FdrInterfaceType;
import com.cannontech.common.fdr.FdrOptionType;
import com.cannontech.common.fileExportHistory.ExportHistoryEntry;
import com.cannontech.common.fileExportHistory.FileExportType;
import com.cannontech.common.inventory.HardwareClass;
import com.cannontech.common.inventory.HardwareType;
import com.cannontech.common.model.ContactNotificationMethodType;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.model.PaoPropertyName;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoInfo;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.attribute.model.AttributeGroup;
import com.cannontech.common.pao.dao.impl.CompletePaoMetaData;
import com.cannontech.common.pao.definition.loader.jaxb.ComponentTypeType;
import com.cannontech.common.pao.definition.loader.jaxb.ControlTypeType;
import com.cannontech.common.pao.definition.loader.jaxb.UpdateTypeType;
import com.cannontech.common.pao.definition.model.PaoData.OptionalField;
import com.cannontech.common.pao.definition.model.PaoMultiPointIdentifier;
import com.cannontech.common.pao.definition.model.PaoPointValue;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.pao.definition.model.PaoTypePointIdentifier;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.service.PaoSelectionService.PaoSelectorType;
import com.cannontech.common.pao.service.PaoSelectionUtil;
import com.cannontech.common.point.AccumulatorType;
import com.cannontech.common.point.PointQuality;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.common.rfn.message.RfnMessageClass;
import com.cannontech.common.rfn.message.metadata.CommStatusType;
import com.cannontech.common.rfn.message.metadata.RfnMetadata;
import com.cannontech.common.rfn.message.metadata.RfnMetadataReplyType;
import com.cannontech.common.survey.model.QuestionType;
import com.cannontech.common.temperature.CelsiusTemperature;
import com.cannontech.common.temperature.FahrenheitTemperature;
import com.cannontech.common.temperature.TemperatureUnit;
import com.cannontech.common.token.Token;
import com.cannontech.common.token.TokenType;
import com.cannontech.common.user.UserAuthenticationInfo;
import com.cannontech.common.userpage.model.SiteMapCategory;
import com.cannontech.common.userpage.model.SiteModule;
import com.cannontech.common.userpage.model.UserPage;
import com.cannontech.common.userpage.model.UserSubscription;
import com.cannontech.common.userpage.model.UserSubscription.SubscriptionType;
import com.cannontech.common.util.BinaryPrefix;
import com.cannontech.common.util.DatedObject;
import com.cannontech.common.util.EnabledStatus;
import com.cannontech.common.util.MatchStyle;
import com.cannontech.common.util.MonthYear;
import com.cannontech.common.util.Range;
import com.cannontech.common.util.StringUtils;
import com.cannontech.common.util.TimeZoneFormat;
import com.cannontech.common.util.TransactionExecutor.ExecutorTransactionality;
import com.cannontech.common.validation.model.RphTag;
import com.cannontech.common.weather.GeographicCoordinate;
import com.cannontech.common.weather.WeatherLocation;
import com.cannontech.common.weather.WeatherObservation;
import com.cannontech.common.weather.WeatherStation;
import com.cannontech.core.authentication.model.AuthType;
import com.cannontech.core.authentication.model.AuthenticationCategory;
import com.cannontech.core.authentication.model.PasswordPolicyError;
import com.cannontech.core.authorization.support.AllowDeny;
import com.cannontech.core.authorization.support.AuthorizationResponse;
import com.cannontech.core.authorization.support.Permission;
import com.cannontech.core.dao.PersistedSystemValueKey;
import com.cannontech.core.dao.RawPointHistoryDao.Mode;
import com.cannontech.core.dao.RawPointHistoryDao.Order;
import com.cannontech.core.dao.impl.LiteYukonUserMapper;
import com.cannontech.core.image.model.YukonImage;
import com.cannontech.core.roleproperties.SerialNumberValidation;
import com.cannontech.core.roleproperties.YukonRoleCategory;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.core.service.DateFormattingService.DateOnlyMode;
import com.cannontech.core.service.DateFormattingService.MidnightMode;
import com.cannontech.core.service.DateFormattingService.PeriodFormatEnum;
import com.cannontech.core.service.PointFormattingService.Format;
import com.cannontech.core.substation.model.SubstationRowMapper;
import com.cannontech.core.users.model.PreferenceShowHide;
import com.cannontech.core.users.model.UserPreferenceName;
import com.cannontech.database.DatabaseTypes;
import com.cannontech.database.DateRowMapper;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.LongRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate.CascadeMode;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.TransactionType;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonRowMapperAdapter;
import com.cannontech.database.data.customer.CustomerFactory;
import com.cannontech.database.data.lite.LiteFactory;
import com.cannontech.database.data.lite.LiteTypes;
import com.cannontech.database.data.pao.CapControlType;
import com.cannontech.database.data.pao.PAOFactory;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.data.pao.ZoneType;
import com.cannontech.database.data.point.AnalogControlType;
import com.cannontech.database.data.point.ControlStateType;
import com.cannontech.database.data.point.PointArchiveInterval;
import com.cannontech.database.data.point.PointFactory;
import com.cannontech.database.data.point.PointLogicalGroups;
import com.cannontech.database.data.point.PointType;
import com.cannontech.database.data.point.PointTypes;
import com.cannontech.database.data.point.StatusControlType;
import com.cannontech.database.data.schedule.script.ScriptParameters;
import com.cannontech.database.data.state.StateFactory;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSettingName;
import com.cannontech.database.db.capcontrol.CommReportingPercentageSettingType;
import com.cannontech.database.db.capcontrol.PeaksTargetType;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingName;
import com.cannontech.database.db.capcontrol.PowerFactorCorrectionSettingType;
import com.cannontech.database.db.capcontrol.VoltViolationType;
import com.cannontech.database.db.capcontrol.VoltageViolationSettingType;
import com.cannontech.database.db.customer.CICustomerPointType;
import com.cannontech.database.db.graph.GraphRenderers;
import com.cannontech.database.db.macro.MacroTypes;
import com.cannontech.database.db.point.stategroup.Disconnect410State;
import com.cannontech.database.db.point.stategroup.RfnDemandResetState;
import com.cannontech.database.db.point.stategroup.RfnDisconnectStatusState;
import com.cannontech.database.db.point.stategroup.TrueFalse;
import com.cannontech.database.db.port.PortTerminalServer.EncodingType;
import com.cannontech.database.db.state.StateGroupUtils;
import com.cannontech.database.db.version.CTIDatabase;
import com.cannontech.database.dbchange.ChangeSequenceStrategyEnum;
import com.cannontech.dr.ThermostatRampRateValues;
import com.cannontech.dr.assetavailability.AllRelayCommunicationTimes;
import com.cannontech.dr.assetavailability.InventoryRelayAppliances;
import com.cannontech.dr.ecobee.dao.EcobeeQueryCount;
import com.cannontech.dr.ecobee.dao.EcobeeQueryType;
import com.cannontech.dr.ecobee.message.AuthenticationRequest;
import com.cannontech.dr.ecobee.message.AuthenticationResponse;
import com.cannontech.dr.ecobee.message.CreateSetRequest;
import com.cannontech.dr.ecobee.message.DrRestoreRequest;
import com.cannontech.dr.ecobee.message.EcobeeReportJob;
import com.cannontech.dr.ecobee.message.MoveDeviceRequest;
import com.cannontech.dr.ecobee.message.MoveSetRequest;
import com.cannontech.dr.ecobee.message.RegisterDeviceRequest;
import com.cannontech.dr.ecobee.message.RuntimeReportJobResponse;
import com.cannontech.dr.ecobee.message.StandardResponse;
import com.cannontech.dr.ecobee.message.partial.DemandResponseRef;
import com.cannontech.dr.ecobee.message.partial.Status;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReading;
import com.cannontech.dr.ecobee.model.EcobeeDeviceReadings;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyCategory;
import com.cannontech.dr.ecobee.model.EcobeeDiscrepancyType;
import com.cannontech.dr.ecobee.model.EcobeeDutyCycleDrParameters;
import com.cannontech.dr.ecobee.model.EcobeeQueryStatistics;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedDeviceDiscrepancy;
import com.cannontech.dr.ecobee.model.discrepancy.EcobeeMislocatedSetDiscrepancy;
import com.cannontech.dr.model.PerformanceVerificationAverageReports;
import com.cannontech.dr.model.PerformanceVerificationEventMessage;
import com.cannontech.dr.model.PerformanceVerificationEventMessageStats;
import com.cannontech.dr.model.PerformanceVerificationEventStats;
import com.cannontech.dr.model.PerformanceVerificationMessageStatus;
import com.cannontech.dr.rfn.message.archive.RfnLcrReadingType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastDataReplyType;
import com.cannontech.dr.rfn.message.unicast.RfnExpressComUnicastReplyType;
import com.cannontech.dr.rfn.model.PqrEvent;
import com.cannontech.dr.rfn.model.PqrEventType;
import com.cannontech.dr.rfn.model.PqrResponseType;
import com.cannontech.dr.rfn.model.RfnLcr6700RelayMap;
import com.cannontech.dr.rfn.model.RfnLcrPointDataMap;
import com.cannontech.dr.rfn.model.RfnLcrReadSimulatorDeviceParameters;
import com.cannontech.dr.rfn.model.RfnLcrRelayDataMap;
import com.cannontech.dr.rfn.model.RfnLcrTlvPointDataType;
import com.cannontech.i18n.MessageCodeGenerator;
import com.cannontech.stars.dr.optout.util.OptOutUtil;
import com.cannontech.stars.dr.program.service.HardwareEnrollmentInfo;
import com.cannontech.stars.util.SettlementConfigFuncs;
import com.cannontech.system.GlobalSettingCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.OnOff;
import com.cannontech.web.PageEditMode;
import com.cannontech.web.input.DatePropertyEditorFactory.BlankMode;
import com.cannontech.web.input.EnumPropertyEditorTest.PlainEnum;
import com.cannontech.web.input.validate.RequiredValidator;
import com.google.common.base.Joiner;

/**
 * Tests objects that we claim are immutable to assert they remain immutable.
 *
 * This only tests a few qualities of classes to determine if they are immutable. Although, we cannot
 * comprehensively determine if the objects are immutable (maybe its possible, but not trivial) which means if
 * an object passes this test it's possible the object is still mutable.
 */
public class SimpleImmutabilityTest {

    @Test
    public void test_common() {
        // @formatter:off
        assertImmutability(
            AccumulatorType.class,
            AdaStatus.class,
            AlertType.class,
            ArgEnum.class,
            AttributeGroup.class,
            BinaryPrefix.class,
            BlankHandlingEnum.class,
            BulkImportType.class,
            CelsiusTemperature.class,
            ChartInterval.class,
            CommStatusType.class,
            CommandRequestExecutionDefaults.class,
            CommandRequestExecutionStatus.class,
            CommandRequestType.class,
            CommandRequestUnsupportedType.class,
            CompletePaoMetaData.class,
            ComponentTypeType.class,
            ContactNotificationMethodType.class,
            ControlTypeType.class,
            DatedObject.class,
            DeviceCollectionByField.class,
            DeviceCollectionById.class,
            DeviceCollectionDbType.class,
            DeviceCollectionType.class,
            DeviceConfigCategory.class,
            DeviceConfigCategoryItem.class,
            DeviceConfiguration.class,
            ElementCategoryEnum.class,
            EnabledStatus.class,
            EnumOption.class,
            EventSource.class,
            ExecutorTransactionality.class,
            ExportHistoryEntry.class,
            ExportTypeEnum.class,
            FahrenheitTemperature.class,
            FdrDirection.class,
            FdrImportAction.class,
            FdrInterfaceOption.class,
            FdrInterfaceType.class,
            FdrOptionType.class,
            FileExportType.class,
            FileMapperEnum.class,
            FixedDeviceGroups.class,
            GraphType.class,
            HardwareClass.class,
            HardwareType.class,
            ImportAction.class,
            InventoryCollectionType.class,
            MasterConfigBoolean.class,
            MasterConfigDeprecatedKey.class,
            MasterConfigDouble.class,
            MasterConfigInteger.class,
            MasterConfigString.class,
            MatchStyle.class,
            MonthYear.class,
            OptionalField.class,
            PagingParameters.class,
            PaoCategory.class,
            PaoClass.class,
            PaoIdentifier.class,
            PaoInfo.class,
            PaoMultiPointIdentifier.class,
            PaoPointValue.class,
            PaoPropertyName.class,
            PaoSelectionUtil.class,
            PaoSelectorType.class,
            PaoTag.class,
            PaoType.class,
            PaoTypePointIdentifier.class,
            PassThroughMapper.class,
            PeakReportRunType.class,
            PointIdentifier.class,
            PointImportParameters.class,
            PointQuality.class,
            PqrEvent.class,
            PqrEventType.class,
            PqrResponseType.class,
            QuestionType.class,
            Range.class,
            ReadType.class,
            ReadingType.class,
            ReferenceTypeEnum.class,
            RfnIdentifier.class,
            RfnLcrTlvPointDataType.class,
            RfnMessageClass.class,
            RfnMetadata.class,
            RfnMetadataReplyType.class,
            RphTag.class,
            SelectionListCategory.class,
            SimpleDevice.class,
            SiteMapCategory.class,
            SiteModule.class,
            StringToIntegerMapper.class,
            StringUtils.class,
            SubscriptionType.class,
            SystemGroupEnum.class,
            TemperatureUnit.class,
            TimeZoneFormat.class,
            Token.class,
            TokenType.class,
            UpdateTypeType.class,
            UserAuthenticationInfo.class,
            UserPage.class,
            UserSubscription.class,
            YukonDefinition.class,
            YukonSelectionListEnum.class,
            YukonSelectionListOrder.class
        );
         // @formatter:on
    }

    @Test
    public void test_database() {
         // @formatter:off
        assertImmutability(
            AnalogControlType.class,
            CICustomerPointType.class,
            CTIDatabase.class,
            CapControlType.class,
            CascadeMode.class,
            ChangeSequenceStrategyEnum.class,
            CommReportingPercentageSettingName.class,
            CommReportingPercentageSettingType.class,
            ControlStateType.class,
            CustomerFactory.class,
            DatabaseTypes.class,
            DateRowMapper.class,
//            DeviceTypesFuncs.class,   // The switch statements in here are converting to non-final PaoType values, need to investigate more.
            Disconnect410State.class,
            EncodingType.class,
            GraphRenderers.class,
            IntegerRowMapper.class,
            LiteFactory.class,
            LiteTypes.class,
            LongRowMapper.class,
            MacroTypes.class,
            PAOFactory.class,
            PAOGroups.class,
            PeaksTargetType.class,
            PointArchiveInterval.class,
            PointFactory.class,
            PointLogicalGroups.class,
            PointType.class,
            PointTypes.class,
            PowerFactorCorrectionSettingName.class,
            PowerFactorCorrectionSettingType.class,
            RfnDemandResetState.class,
            RfnDisconnectStatusState.class,
            ScriptParameters.class,
            SqlUtils.class,
            StateFactory.class,
            StateGroupUtils.class,
            StatusControlType.class,
            TransactionType.class,
            TrueFalse.class,
            VoltViolationType.class,
            VoltageViolationSettingType.class,
            YNBoolean.class,
            YukonRowMapperAdapter.class,
            ZoneType.class
        );
         // @formatter:on
    }

    @Test
    public void test_dr() {
        // @formatter:off
        assertImmutability(
            AllRelayCommunicationTimes.class,
            AuthenticationRequest.class,
            AuthenticationResponse.class,
            CreateSetRequest.class,
            DemandResponseRef.class,
            DrRestoreRequest.class,
            EcobeeDeviceReading.class,
            EcobeeDeviceReadings.class,
            EcobeeDiscrepancyCategory.class,
            EcobeeDiscrepancyType.class,
            EcobeeDutyCycleDrParameters.class,
            EcobeeMislocatedDeviceDiscrepancy.class,
            EcobeeMislocatedSetDiscrepancy.class,
            EcobeeQueryCount.class,
            EcobeeQueryStatistics.class,
            EcobeeQueryType.class,
            EcobeeReportJob.class,
            InventoryRelayAppliances.class,
            MoveDeviceRequest.class,
            MoveSetRequest.class,
            PerformanceVerificationAverageReports.class,
            PerformanceVerificationEventMessage.class,
            PerformanceVerificationEventMessageStats.class,
            PerformanceVerificationEventStats.class,
            PerformanceVerificationMessageStatus.class,
            RegisterDeviceRequest.class,
            RfnExpressComUnicastDataReplyType.class,
            RfnExpressComUnicastReplyType.class,
            RfnLcrPointDataMap.class,
            RfnLcrReadSimulatorDeviceParameters.class,
            RfnLcrReadingType.class,
            RfnLcrRelayDataMap.class,
            RuntimeReportJobResponse.class,
            StandardResponse.class,
            Status.class,
            ThermostatRampRateValues.class,
            RfnLcrTlvPointDataType.class,
            RfnLcr6700RelayMap.class
        );
         // @formatter:on
    }

    @Test
    public void test_web() {
        // @formatter:off
        assertImmutability(
            BlankMode.class,
            PageEditMode.class,
            PlainEnum.class,
            RequiredValidator.class
        );
         // @formatter:on
    }

    @Test
    public void test_loadcontrol() {
        // @formatter:off
        assertImmutability(
            GeographicCoordinate.class,
            WeatherLocation.class,
            WeatherObservation.class,
            WeatherStation.class
        );
         // @formatter:on
    }

    @Test
    public void test_cc() {
        // @formatter:off
        assertImmutability(
            CurtailmentEventState.class,
            EconomicEventState.class,
            NotificationStatus.class,
            ProgramParameterKey.class,
            SelectionState.class
        );
         // @formatter:on
    }

    @Test
    public void test_clientutils() {
        // @formatter:off
        assertImmutability(
            ClientApplicationRememberMe.class,
            CommonUtils.class,
            TagUtils.class
        );
         // @formatter:on
    }

    @Test
    public void test_core() {
        // @formatter:off
        assertImmutability(
            AllowDeny.class,
            AuthType.class,
            AuthenticationCategory.class,
            AuthorizationResponse.class,
            Range.class,
            DateFormatEnum.class,
            DateOnlyMode.class,
            Format.class,
            LiteYukonUserMapper.class,
            MidnightMode.class,
            Mode.class,
            Order.class,
            PasswordPolicyError.class,
            PeriodFormatEnum.class,
            Permission.class,
            PersistedSystemValueKey.class,
            PreferenceShowHide.class,
            SerialNumberValidation.class,
            SubstationRowMapper.class,
            UserPreferenceName.class,
            YukonImage.class,
            YukonRoleCategory.class,
            YukonRoleProperty.class
        );
         // @formatter:on
    }

    @Test
    public void test_amr() {
        // @formatter:off
        assertImmutability(
            ArchivedValuesExportFormatType.class,
            AttributeField.class,
            CalculationData.class,
            ChannelDataStatus.class,
            DataExportDelimiter.class,
            DataRangeType.class,
            DataSelection.class,
            DetectedPhase.class,
            Direction.class,
            DisconnectDeviceState.class,
            FieldType.class,
            MeterDisplayFieldEnum.class,
            MeterSearchField.class,
            MissingAttribute.class,
            MspSearchField.class,
            PadSide.class,
            PointSortField.class,
            PorterResponseMonitorMatchStyle.class,
            ProfileAttributeChannel.class,
            ReadingPattern.class,
            RfnConditionDataType.class,
            RfnConditionType.class,
            RfnInvalidValues.class,
            RfnMeterDemandResetReplyType.class,
            RfnMeterDisconnectConfirmationReplyType.class,
            RfnMeterDisconnectInitialReplyType.class,
            RfnMeterDisconnectState.class,
            RfnMeterDisconnectStatusType.class,
            RfnMeterReadingDataReplyType.class,
            RfnMeterReadingReplyType.class,
            RfnMeterReadingType.class,
            ScheduleGroupRequestExecutionDaoEnabledFilter.class,
            ScheduleGroupRequestExecutionDaoPendingFilter.class,
            ScheduledGroupRequestExecutionStatus.class,
            SiPrefix.class,
            SpecificDeviceErrorDescription.class,
            StatusPointMonitorStateType.class,
            StrategyType.class,
            TimestampPattern.class,
            YukonRoundingMode.class
        );
         // @formatter:on
    }

    @Test
    public void test_enums() {
        // @formatter:off
        assertImmutability(
            CurtailmentEventAction.class,
            EconomicEventAction.class,
            NotificationReason.class,
            NotificationState.class,
            Phase.class
        );
         // @formatter:on
    }

    @Test
    public void test_system() {
        // @formatter:off
        assertImmutability(
            GlobalSettingCategory.class,
            GlobalSettingType.class,
            OnOff.class
        );
         // @formatter:on
    }

    @Test
    public void test_misc() throws Exception {
        // @formatter:off
        assertImmutability(
            HardwareEnrollmentInfo.class,
            MessageCodeGenerator.class,
            OptOutUtil.class,
            SettlementConfigFuncs.class
            );
        // @formatter:on
    }

    private void assertImmutability(Class<?>... classes) {
        BeanInfo beanInfo;
        for (Class<?> aClass : classes) {
            try {
                beanInfo = Introspector.getBeanInfo(aClass);
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            List<String> failureMessages = new ArrayList<>();
            if (!aClass.isEnum() && !Modifier.isFinal(aClass.getModifiers())) {
                failureMessages.add(aClass.getSimpleName() + " is not final.");
            }
            for (PropertyDescriptor property : propertyDescriptors) {
                if (property.getWriteMethod() != null) {
                    failureMessages.add("Setter method: " + property.getWriteMethod().getName());
                }
            }
            for (Field field : aClass.getDeclaredFields()) {
                if (!Modifier.isFinal(field.getModifiers())) {
                    failureMessages.add("Non final field: " + field.getType().getSimpleName() + " " + field.getName());
                }
            }
            if (!failureMessages.isEmpty()) {
                String reasons = Joiner.on(", ").join(failureMessages);
                fail(aClass.getSimpleName() + " is not immutable for the following reasons: " + reasons);
            }
        }
    }
}
