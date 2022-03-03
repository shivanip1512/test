package com.cannontech.services.rfn.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.easymock.Capture;
import org.easymock.CaptureType;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.amr.errors.dao.DeviceError;
import com.cannontech.amr.rfn.dao.RfnDeviceDao;
import com.cannontech.common.device.programming.dao.MeterProgrammingDao;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest;
import com.cannontech.common.device.programming.message.MeterProgramStatusArchiveRequest.Source;
import com.cannontech.common.device.programming.model.MeterProgram;
import com.cannontech.common.device.programming.model.MeterProgramSource;
import com.cannontech.common.device.programming.model.MeterProgramStatus;
import com.cannontech.common.device.programming.model.ProgrammingStatus;
import com.cannontech.common.rfn.message.RfnIdentifier;
import com.cannontech.core.dao.NotFoundException;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.Table;

public class MeterProgramStatusArchiveRequestListenerTest {

    //  Constants used to form up the MeterProgramStatus and MeterProgramStatusArchiveRequest
    private static final int DEVICE_ID = 42;
    private static final Instant EXISTING_TIMESTAMP = Instant.parse("2020-01-20T12:34:56");
    private static final Instant NEW_TIMESTAMP = EXISTING_TIMESTAMP.plus(Duration.standardSeconds(1));
    private static final Instant TIMED_OUT_TIMESTAMP = NEW_TIMESTAMP.plus(Duration.standardHours(1)); 
    private static final RfnIdentifier RFN_IDENTIFIER = new RfnIdentifier("serial", "manufacturer", "model");
    private static final Source SOURCE = Source.PORTER;

    private static final String INSUFFICIENT_FIRMWARE_CONFIG_ID = "X00000000-0000-0000-0000-000000000000";
    private static final String UNKNOWN_CONFIG_ID = "N00000000-0000-0000-0000-000000000000";
    private static final UUID YUKON_GUID = UUID.fromString("EE8358B0-92B7-4603-A148-A06E5489D4C7");
    private static final String YUKON_CONFIG_ID = "R" + YUKON_GUID;

    @Nested
    public static class TestUploadingIdle {

        private MeterProgramStatusArchiveRequestListener l = new MeterProgramStatusArchiveRequestListener();
        
        //  Variable to capture any status update 
        private Capture<MeterProgramStatus> updatedStatus = new Capture<>(CaptureType.ALL);
    
        @Test
        public void test_idleIgnoredWithinTimeout() {
            
            var m = new MeterProgramStatusArchiveRequest();

            m.setConfigurationId(UNKNOWN_CONFIG_ID);
            m.setError(DeviceError.SUCCESS);
            m.setRfnIdentifier(RFN_IDENTIFIER);
            m.setSource(Source.SM_STATUS_ARCHIVE);
            m.setStatus(ProgrammingStatus.IDLE);
            m.setTimestamp(NEW_TIMESTAMP);

            l.process(m, "just testing");

            assertFalse(updatedStatus.hasCaptured(), "Unexpected state update");
        }
        
        @Test
        public void test_idleAcceptedAfterTimeout() {

            
            var m = new MeterProgramStatusArchiveRequest();
            
            m.setConfigurationId(UNKNOWN_CONFIG_ID);
            m.setError(DeviceError.SUCCESS);
            m.setRfnIdentifier(RFN_IDENTIFIER);
            m.setSource(Source.SM_STATUS_ARCHIVE);
            m.setStatus(ProgrammingStatus.IDLE);
            m.setTimestamp(TIMED_OUT_TIMESTAMP);

            l.process(m, "just testing");

            assertTrue(updatedStatus.hasCaptured(), "State updated");
            assertEquals(updatedStatus.getValues().size(), 1, "State updated once");
            assertEquals(updatedStatus.getValue(),
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.MISMATCHED, TIMED_OUT_TIMESTAMP),
                    "State update matches");
        }
        
        @BeforeEach
        public void init() {
            initializeMockRfnDeviceDao(l);
            initializeMockMeterProgrammingDao(l, updatedStatus,
                    () -> {
                        return createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.UPLOADING, EXISTING_TIMESTAMP);
                    },
                    () -> {
                        var mp = new MeterProgram();
                        mp.setGuid(YUKON_GUID);
                        return mp;
                    });
        }
    }
    @Nested
    public static class TestAllStatusUpdates {
    
        private MeterProgramStatusArchiveRequestListener l = new MeterProgramStatusArchiveRequestListener();

        //  Variable to capture any status update 
        private Capture<MeterProgramStatus> updatedStatus = new Capture<>(CaptureType.ALL);
    
        //  ExistingState is a helper class that encapsulates a meter's assigned and reported programming state
        public States state;
        public Messages message;
        public Optional<MeterProgramStatus> expectedUpdate;
    
        public static Collection<Object[]> existingStates() {
            return getExpectedUpdates().cellSet().stream()
                    .map(c -> new Object[] { c.getRowKey(), c.getColumnKey(), c.getValue() })
                    .collect(Collectors.toList());
        }

        @ParameterizedTest
        @MethodSource("existingStates")
        public void test_newMessage(ArgumentsAccessor argumentsAccessor) {
            state = (States) argumentsAccessor.get(0);
            message = (Messages) argumentsAccessor.get(1);
            expectedUpdate = (Optional<MeterProgramStatus>) argumentsAccessor.get(2);
            init();
            var archiveRequest = message.getMessageWithTimestamp(NEW_TIMESTAMP);
            
            l.process(archiveRequest, "just testing");

            expectedUpdate.ifPresentOrElse(
                expected -> {
                    assertTrue(updatedStatus.hasCaptured(), "State updated");
                    assertEquals(updatedStatus.getValues().size(), 1, "State updated once");
                    assertEquals(expected, updatedStatus.getValue(), "State update matches");
                },
                () ->
                    assertFalse(updatedStatus.hasCaptured(), "Unexpected state update: " + updatedStatus));
        }

        @ParameterizedTest
        @MethodSource("existingStates")
        public void test_oldMessage(ArgumentsAccessor argumentsAccessor) {
            state = (States) argumentsAccessor.get(0);
            message = (Messages) argumentsAccessor.get(1);
            expectedUpdate = (Optional<MeterProgramStatus>) argumentsAccessor.get(2);
            init();
            var archiveRequest = message.getMessageWithTimestamp(EXISTING_TIMESTAMP);
            
            l.process(archiveRequest, "just testing");

            //  Only accept "old" messages if:
            //    1. we've never heard from the device yet AND 
            //    2. this is a success/idle notification
            if (state == States.UNREPORTED && archiveRequest.getStatus() == ProgrammingStatus.IDLE) {
                assertTrue(updatedStatus.hasCaptured(), "State updated");
            } else {
                assertFalse(updatedStatus.hasCaptured(), "Unexpected state update: " + updatedStatus);
            }
        }

        //  Rows - states from state machine
        private enum States {
            UNREPORTED(
                    null,
                    null,
                    null),
            UNASSIGNED_IDLE(
                    null,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.IDLE),
            INSUFFICIENT(
                    null,
                    INSUFFICIENT_FIRMWARE_CONFIG_ID,
                    ProgrammingStatus.FAILED),
            ASSIGNED_CANCELED(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.CANCELED),
            ASSIGNED_CONFIRMING(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.CONFIRMING),
            ASSIGNED_FAILED(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.FAILED),
            ASSIGNED_INITIATING(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.INITIATING),
            ASSIGNED_MISMATCHED(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.MISMATCHED),
            ASSIGNED_UPLOADING(
                    YUKON_GUID,
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.UPLOADING),
            ASSIGNED_IDLE(
                    YUKON_GUID,
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.IDLE);

            private UUID assignedGuid;
            private String configurationId;
            private ProgrammingStatus status;

            private States(UUID assignedGuid, String configurationId, ProgrammingStatus status) {
                this.assignedGuid = assignedGuid;
                this.configurationId = configurationId;
                this.status = status;
            }

            public IAnswer<MeterProgram> getMeterProgramAnswer() throws NotFoundException {
                return () -> {
                    if (assignedGuid == null) {
                        throw new NotFoundException("No meter program assigned");
                    }
                    var meterProgram = new MeterProgram();
                    meterProgram.setGuid(assignedGuid);
                    return meterProgram;
                };
            }

            public IAnswer<MeterProgramStatus> getMeterProgramStatusAnswer() throws NotFoundException {
                return () -> {
                    if (configurationId == null || status == null) {
                        throw new NotFoundException("No meter program status entry");
                    }

                    return createMeterProgramStatus(configurationId, status, EXISTING_TIMESTAMP);
                };
            }
        };

        //  Columns - Messages sent for all test cases
        private enum Messages {
            UNKNOWN_IDLE(
                    UNKNOWN_CONFIG_ID,
                    ProgrammingStatus.IDLE),
            INSUFFICIENT_IDLE(
                    INSUFFICIENT_FIRMWARE_CONFIG_ID,
                    ProgrammingStatus.IDLE),
            YUKON_CANCELED(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.CANCELED),
            YUKON_CONFIRMING(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.CONFIRMING),
            YUKON_FAILED(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.FAILED),
            YUKON_IDLE(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.IDLE),
            YUKON_INITIATING(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.INITIATING),
            YUKON_UPLOADING(
                    YUKON_CONFIG_ID,
                    ProgrammingStatus.UPLOADING);

            private String configurationId;
            private ProgrammingStatus status;

            private Messages(String configurationId, ProgrammingStatus status) {
                this.configurationId = configurationId;
                this.status = status;
            }

            public MeterProgramStatusArchiveRequest getMessageWithTimestamp(Instant timestamp) {
                final var deviceError =
                        status == ProgrammingStatus.FAILED
                            ? DeviceError.CATASTROPHIC_FAILURE
                            : DeviceError.SUCCESS;

                var m = new MeterProgramStatusArchiveRequest();
                m.setConfigurationId(configurationId);
                m.setError(deviceError);
                m.setRfnIdentifier(RFN_IDENTIFIER);
                m.setSource(SOURCE);
                m.setStatus(status);
                m.setTimestamp(timestamp);
                return m;
            }
        };

        private static Table<States, Messages, Optional<MeterProgramStatus>> getExpectedUpdates() {

            var builder = ImmutableTable.<States, Messages, Optional<MeterProgramStatus>>builder();

            var __ = Optional.<MeterProgramStatus>empty();
            var unknownCanceled = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.CANCELED, NEW_TIMESTAMP));
            var unknownConfirming = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.CONFIRMING, NEW_TIMESTAMP));
            var unknownIdle = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.IDLE, NEW_TIMESTAMP));
            var unknownInitiating = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.INITIATING, NEW_TIMESTAMP));
            var unknownMismatch = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.MISMATCHED, NEW_TIMESTAMP));
            var unknownFailed = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.FAILED, NEW_TIMESTAMP, DeviceError.CATASTROPHIC_FAILURE));
            var unknownUploading = Optional.of(
                    createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.UPLOADING, NEW_TIMESTAMP));
            
            var yukonCanceled = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.CANCELED, NEW_TIMESTAMP));
            var yukonConfirming = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.CONFIRMING, NEW_TIMESTAMP));
            var yukonFailed = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.FAILED, NEW_TIMESTAMP, DeviceError.CATASTROPHIC_FAILURE));
            var yukonIdle = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.IDLE, NEW_TIMESTAMP));
            var yukonInitiating = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.INITIATING, NEW_TIMESTAMP));
            var yukonUploading = Optional.of(
                    createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.UPLOADING, NEW_TIMESTAMP));
            var insufficientFirmware = Optional.of(
                    createMeterProgramStatus(INSUFFICIENT_FIRMWARE_CONFIG_ID, ProgrammingStatus.FAILED, NEW_TIMESTAMP));
            
            builder.put(States.UNREPORTED, Messages.UNKNOWN_IDLE, unknownIdle);
            builder.put(States.UNREPORTED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.UNREPORTED, Messages.YUKON_CANCELED, __);
            builder.put(States.UNREPORTED, Messages.YUKON_CONFIRMING, __);
            builder.put(States.UNREPORTED, Messages.YUKON_FAILED, __);
            builder.put(States.UNREPORTED, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.UNREPORTED, Messages.YUKON_INITIATING, __);
            builder.put(States.UNREPORTED, Messages.YUKON_UPLOADING, __);

            builder.put(States.INSUFFICIENT, Messages.UNKNOWN_IDLE, unknownIdle);
            builder.put(States.INSUFFICIENT, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.INSUFFICIENT, Messages.YUKON_CANCELED, __);
            builder.put(States.INSUFFICIENT, Messages.YUKON_CONFIRMING, __);
            builder.put(States.INSUFFICIENT, Messages.YUKON_FAILED, __);
            builder.put(States.INSUFFICIENT, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.INSUFFICIENT, Messages.YUKON_INITIATING, __);
            builder.put(States.INSUFFICIENT, Messages.YUKON_UPLOADING, __);

            builder.put(States.UNASSIGNED_IDLE, Messages.UNKNOWN_IDLE, unknownIdle);
            builder.put(States.UNASSIGNED_IDLE, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_CANCELED, __);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_CONFIRMING, __);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_FAILED, __);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_INITIATING, __);
            builder.put(States.UNASSIGNED_IDLE, Messages.YUKON_UPLOADING, __);

            builder.put(States.ASSIGNED_CANCELED, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_CANCELED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_FAILED, unknownFailed);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_CANCELED, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_CONFIRMING, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_FAILED, unknownFailed);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_CONFIRMING, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_FAILED, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_FAILED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_FAILED, unknownFailed);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_FAILED, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_INITIATING, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_INITIATING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_FAILED, unknownFailed);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_INITIATING, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_MISMATCHED, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_FAILED, unknownFailed);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_MISMATCHED, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_UPLOADING, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_UPLOADING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_CANCELED, unknownCanceled);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_CONFIRMING, unknownConfirming);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_FAILED, __);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_INITIATING, unknownInitiating);
            builder.put(States.ASSIGNED_UPLOADING, Messages.YUKON_UPLOADING, unknownUploading);

            builder.put(States.ASSIGNED_IDLE, Messages.UNKNOWN_IDLE, unknownMismatch);
            builder.put(States.ASSIGNED_IDLE, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_CANCELED, yukonCanceled);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_CONFIRMING, yukonConfirming);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_FAILED, yukonFailed);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_IDLE, yukonIdle);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_INITIATING, yukonInitiating);
            builder.put(States.ASSIGNED_IDLE, Messages.YUKON_UPLOADING, yukonUploading);

            var table = builder.build();

            var columnKeys = table.columnKeySet();
            table.rowMap().forEach((rowKey, rowTable) ->
                columnKeys.forEach(columnKey ->
                    assertTrue(rowTable.containsKey(columnKey), "Missing entry at\n" + rowKey + "\n" + columnKey)));

            return table;
        }

        public void init() {
            initializeMockRfnDeviceDao(l);
            initializeMockMeterProgrammingDao(l, updatedStatus,
                    state.getMeterProgramStatusAnswer(),
                    state.getMeterProgramAnswer());
        }

    }

    private static void initializeMockRfnDeviceDao(MeterProgramStatusArchiveRequestListener l) {
        var rfnDeviceDao = EasyMock.createNiceMock(RfnDeviceDao.class);

        EasyMock.expect(rfnDeviceDao.getDeviceIdForRfnIdentifier(EasyMock.anyObject()))
            .andReturn(DEVICE_ID)
            .atLeastOnce();

        ReflectionTestUtils.setField(l, "rfnDeviceDao", rfnDeviceDao);

        EasyMock.replay(rfnDeviceDao);
    }

    private static void initializeMockMeterProgrammingDao(MeterProgramStatusArchiveRequestListener l, 
            Capture<MeterProgramStatus> updatedStatus, IAnswer<MeterProgramStatus> meterProgramStatusAnswer, 
            IAnswer<MeterProgram> meterProgramAnswer) {
        var meterProgrammingDao = EasyMock.createMock(MeterProgrammingDao.class);

        EasyMock.expect(meterProgrammingDao.getMeterProgramStatus(DEVICE_ID))
            .andAnswer(meterProgramStatusAnswer)
            .times(1, 2);

        meterProgrammingDao.createMeterProgramStatus(EasyMock.capture(updatedStatus));
        EasyMock.expectLastCall()
            .times(0, 1);

        meterProgrammingDao.updateMeterProgramStatus(EasyMock.capture(updatedStatus));
        EasyMock.expectLastCall()
            .times(0, 1);

        EasyMock.expect(meterProgrammingDao.getProgramByDeviceId(DEVICE_ID))
            .andAnswer(meterProgramAnswer)
            .times(0, 1);

        ReflectionTestUtils.setField(l, "meterProgrammingDao", meterProgrammingDao);

        EasyMock.replay(meterProgrammingDao);
    }

    private static MeterProgramStatus createMeterProgramStatus(String configurationId, ProgrammingStatus status, Instant timestamp) {
        return createMeterProgramStatus(configurationId, status, timestamp, DeviceError.SUCCESS);
    }
    
    private static MeterProgramStatus createMeterProgramStatus(String configurationId, ProgrammingStatus status, Instant timestamp, 
            DeviceError error) {
        var mps = new MeterProgramStatus();

        mps.setDeviceId(DEVICE_ID);
        mps.setError(error);
        mps.setLastUpdate(timestamp);
        mps.setReportedGuid(UUID.fromString(configurationId.substring(1)));
        mps.setSource(MeterProgramSource.getByPrefix(configurationId.substring(0, 1)));
        mps.setStatus(status);

        return mps;
    }
}
