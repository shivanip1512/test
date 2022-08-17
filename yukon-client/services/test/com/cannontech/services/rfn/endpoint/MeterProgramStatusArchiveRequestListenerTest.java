package com.cannontech.services.rfn.endpoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
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

@RunWith(Parameterized.class)
public class MeterProgramStatusArchiveRequestListenerTest {

    private MeterProgramStatusArchiveRequestListener l = new MeterProgramStatusArchiveRequestListener();

    //  ExistingState is a helper class that encapsulates a meter's assigned and reported programming state
    @Parameter(0)
    public States state;
    @Parameter(1)
    public Messages message;
    @Parameter(2)
    public Optional<MeterProgramStatus> expectedUpdate;

    //  Constants used to form up the MeterProgramStatus and MeterProgramStatusArchiveRequest
    private static final int DEVICE_ID = 42;
    private static final Instant EXISTING_TIMESTAMP = Instant.parse("2020-01-20T12:34:56");
    private static final Instant NEW_TIMESTAMP = EXISTING_TIMESTAMP.plus(Duration.standardSeconds(1));
    private static final RfnIdentifier RFN_IDENTIFIER = new RfnIdentifier("serial", "manufacturer", "model");
    private static final Source SOURCE = Source.PORTER;

    private static final String INSUFFICIENT_FIRMWARE_CONFIG_ID = "X00000000-0000-0000-0000-000000000000";
    private static final String UNKNOWN_CONFIG_ID = "N00000000-0000-0000-0000-000000000000";
    private static final UUID YUKON_GUID = UUID.fromString("EE8358B0-92B7-4603-A148-A06E5489D4C7");
    private static final String YUKON_CONFIG_ID = "R" + YUKON_GUID;

    //  Variable to capture any status update 
    private Capture<MeterProgramStatus> updatedStatus = new Capture<>(CaptureType.ALL);

    @Parameters(name="state {0} msg {1}")
    public static Collection<Object[]> existingStates() {
        return getExpectedUpdates().cellSet().stream()
                .map(c -> new Object[] { c.getRowKey(), c.getColumnKey(), c.getValue() })
                .collect(Collectors.toList());
    }

    @Test
    public void test_newMessage() {
        var archiveRequest = message.getMessageWithTimestamp(NEW_TIMESTAMP);
        
        l.process(archiveRequest, "just testing");

        expectedUpdate.ifPresentOrElse(
            expected -> {
                assertTrue("State updated", updatedStatus.hasCaptured());
                assertEquals("State updated once", updatedStatus.getValues().size(), 1);
                assertEquals("State update matches", expected, updatedStatus.getValue());
            },
            () ->
                assertFalse("Unexpected state update: " + updatedStatus, updatedStatus.hasCaptured()));
    }

    @Test
    public void test_oldMessage() {
        var archiveRequest = message.getMessageWithTimestamp(EXISTING_TIMESTAMP);
        
        l.process(archiveRequest, "just testing");

        //  Only accept "old" messages if:
        //    1. we've never heard from the device yet AND 
        //    2. this is a success/idle notification
        if (state == States.UNREPORTED && archiveRequest.getStatus() == ProgrammingStatus.IDLE) {
            assertTrue("State updated", updatedStatus.hasCaptured());
        } else {
            assertFalse("Unexpected state update: " + updatedStatus, updatedStatus.hasCaptured());
        }
    }

    //  Rows - states from state machine
    private enum States {
        UNREPORTED(
                null,
                null,
                null),
        UNASSIGNED(
                null,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.IDLE),
        INSUFFICIENT_FIRMWARE(
                null,
                INSUFFICIENT_FIRMWARE_CONFIG_ID,
                ProgrammingStatus.FAILED),
        CANCELED(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.CANCELED),
        CONFIRMING(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.CONFIRMING),
        FAILED(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.FAILED),
        INITIATING(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.INITIATING),
        MISMATCHED(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.MISMATCHED),
        UPLOADING(
                YUKON_GUID,
                UNKNOWN_CONFIG_ID,
                ProgrammingStatus.UPLOADING),
        IDLE(
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
        OLD_FIRMWARE_IDLE(
                YUKON_CONFIG_ID,
                ProgrammingStatus.CANCELED),
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

        Optional<MeterProgramStatus> __ = Optional.empty();
        var unknownIdle = Optional.of(
                createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.IDLE, NEW_TIMESTAMP));
        var unknownMismatch = Optional.of(
                createMeterProgramStatus(UNKNOWN_CONFIG_ID, ProgrammingStatus.MISMATCHED, NEW_TIMESTAMP));
        var yukonCanceled = Optional.of(
                createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.CANCELED, NEW_TIMESTAMP));
        var yukonConfirming = Optional.of(
                createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.CONFIRMING, NEW_TIMESTAMP));
        var yukonFailed = Optional.of(
                createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.FAILED, NEW_TIMESTAMP, DeviceError.CATASTROPHIC_FAILURE));
        var yukonIdle = Optional.of(
                createMeterProgramStatus(YUKON_CONFIG_ID, ProgrammingStatus.IDLE, NEW_TIMESTAMP));
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
        builder.put(States.UNREPORTED, Messages.YUKON_UPLOADING, __);

        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.UNKNOWN_IDLE, unknownIdle);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.YUKON_CANCELED, __);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.YUKON_CONFIRMING, __);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.YUKON_FAILED, __);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.INSUFFICIENT_FIRMWARE, Messages.YUKON_UPLOADING, __);

        builder.put(States.UNASSIGNED, Messages.UNKNOWN_IDLE, unknownIdle);
        builder.put(States.UNASSIGNED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.UNASSIGNED, Messages.YUKON_CANCELED, __);
        builder.put(States.UNASSIGNED, Messages.YUKON_CONFIRMING, __);
        builder.put(States.UNASSIGNED, Messages.YUKON_FAILED, __);
        builder.put(States.UNASSIGNED, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.UNASSIGNED, Messages.YUKON_UPLOADING, __);

        builder.put(States.CANCELED, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.CANCELED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.CANCELED, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.CANCELED, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.CANCELED, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.CANCELED, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.CANCELED, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.CONFIRMING, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.CONFIRMING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.CONFIRMING, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.CONFIRMING, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.CONFIRMING, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.CONFIRMING, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.CONFIRMING, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.FAILED, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.FAILED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.FAILED, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.FAILED, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.FAILED, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.FAILED, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.FAILED, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.INITIATING, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.INITIATING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.INITIATING, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.INITIATING, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.INITIATING, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.INITIATING, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.INITIATING, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.MISMATCHED, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.MISMATCHED, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.MISMATCHED, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.MISMATCHED, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.MISMATCHED, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.MISMATCHED, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.MISMATCHED, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.UPLOADING, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.UPLOADING, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.UPLOADING, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.UPLOADING, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.UPLOADING, Messages.YUKON_FAILED, __);
        builder.put(States.UPLOADING, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.UPLOADING, Messages.YUKON_UPLOADING, yukonUploading);

        builder.put(States.IDLE, Messages.UNKNOWN_IDLE, unknownMismatch);
        builder.put(States.IDLE, Messages.INSUFFICIENT_IDLE, insufficientFirmware);
        builder.put(States.IDLE, Messages.YUKON_CANCELED, yukonCanceled);
        builder.put(States.IDLE, Messages.YUKON_CONFIRMING, yukonConfirming);
        builder.put(States.IDLE, Messages.YUKON_FAILED, yukonFailed);
        builder.put(States.IDLE, Messages.YUKON_IDLE, yukonIdle);
        builder.put(States.IDLE, Messages.YUKON_UPLOADING, yukonUploading);

        var table = builder.build();

        var columnKeys = table.columnKeySet();
        table.rowMap().forEach((rowKey, rowTable) ->
            columnKeys.forEach(columnKey ->
                assertTrue("Missing entry at\n" + rowKey + "\n" + columnKey, rowTable.containsKey(columnKey))));

        return table;
    }

    @Before
    public void init() {
        initializeMockRfnDeviceDao();
        initializeMockMeterProgrammingDao();
    }

    private void initializeMockRfnDeviceDao() {
        var rfnDeviceDao = EasyMock.createNiceMock(RfnDeviceDao.class);

        EasyMock.expect(rfnDeviceDao.getDeviceIdForRfnIdentifier(EasyMock.anyObject()))
            .andReturn(DEVICE_ID)
            .atLeastOnce();

        ReflectionTestUtils.setField(l, "rfnDeviceDao", rfnDeviceDao);

        EasyMock.replay(rfnDeviceDao);
    }

    private void initializeMockMeterProgrammingDao() {
        var meterProgrammingDao = EasyMock.createMock(MeterProgrammingDao.class);

        EasyMock.expect(meterProgrammingDao.getMeterProgramStatus(DEVICE_ID))
            .andAnswer(state.getMeterProgramStatusAnswer())
            .times(1, 2);

        meterProgrammingDao.createMeterProgramStatus(EasyMock.capture(updatedStatus));
        EasyMock.expectLastCall()
            .times(0, 1);

        meterProgrammingDao.updateMeterProgramStatus(EasyMock.capture(updatedStatus));
        EasyMock.expectLastCall()
            .times(0, 1);

        meterProgrammingDao.updateMeterProgramStatusToInitiating(DEVICE_ID, NEW_TIMESTAMP);
        EasyMock.expectLastCall()
            .andDelegateTo(new Object() {
                @SuppressWarnings("unused")
                void updateMeterProgramStatusToInitiating(int deviceId, Instant lastUpdate) throws Throwable {
                    var mps = state.getMeterProgramStatusAnswer().answer();
                    mps.setLastUpdate(lastUpdate);
                    updatedStatus.setValue(mps);
                }
            })
            .times(0, 1);

        EasyMock.expect(meterProgrammingDao.getProgramByDeviceId(DEVICE_ID))
            .andAnswer(state.getMeterProgramAnswer())
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
