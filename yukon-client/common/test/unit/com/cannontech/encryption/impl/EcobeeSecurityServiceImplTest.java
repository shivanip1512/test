package com.cannontech.encryption.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import com.cannontech.common.events.helper.EventLogHelper;
import com.cannontech.common.exception.EcobeePGPException;
import com.cannontech.database.db.security.EncryptionKey;
import com.cannontech.encryption.EncryptedRouteDao;
import com.cannontech.encryption.EncryptionKeyType;

public class EcobeeSecurityServiceImplTest {

    private EcobeeSecurityServiceImpl mockEcobeeSecurityServiceImpl;
    private EncryptedRouteDao mockEncryptedRouteDao;
    private EventLogHelper mockEventLogHelper;

    @Before
    public void setup() {
        mockEncryptedRouteDao = EasyMock.createMock(EncryptedRouteDao.class);
        mockEventLogHelper = EasyMock.createMock(EventLogHelper.class);
        mockEcobeeSecurityServiceImpl = new EcobeeSecurityServiceImpl();
        ReflectionTestUtils.setField(mockEcobeeSecurityServiceImpl, "encryptedRouteDao", mockEncryptedRouteDao);
        ReflectionTestUtils.setField(mockEcobeeSecurityServiceImpl, "eventLogHelper", mockEventLogHelper);
    }
    
    @Test
    public void test_generateEcobeePGPKeyPair() {
        assertNotNull(mockEcobeeSecurityServiceImpl.generateEcobeePGPKeyPair());
    }

    @Test
    public void test_getEcobeeKeyPairCreationTime_Present() {
        EncryptionKey encryptionKey = new EncryptionKey();
        Instant timestamp = Instant.now();
        encryptionKey.setTimestamp(timestamp);
        EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee)).andReturn(Optional.ofNullable(encryptionKey));
        EasyMock.replay(mockEncryptedRouteDao);
        assertEquals(timestamp, mockEcobeeSecurityServiceImpl.getEcobeeKeyPairCreationTime());
    }

    @Test(expected = NoSuchElementException.class)
    public void test_getEcobeeKeyPairCreationTime_NotPresent() {
        EncryptionKey encryptionKey = new EncryptionKey();
        EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee)).andReturn(Optional.ofNullable(encryptionKey));
        EasyMock.replay(mockEncryptedRouteDao);
        mockEcobeeSecurityServiceImpl.getEcobeeKeyPairCreationTime();
    }

    @Test(expected = EcobeePGPException.class)
    public void test_decryptEcobeeFile_PrimaryKey_NotPresent() {
        InputStream stubInputStream = new ByteArrayInputStream( "Dummy data".getBytes() );
        EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee)).andReturn(Optional.empty());
        EasyMock.replay(mockEncryptedRouteDao);
        mockEcobeeSecurityServiceImpl.decryptEcobeeFile(stubInputStream);
    }

    @Test(expected = EcobeePGPException.class)
    public void test_decryptEcobeeFile_Fail() {
        Capture<String> yukonService = new Capture<>();
        Capture<String> type = new Capture<>();
        EncryptionKey encryptionKey = new EncryptionKey();
        encryptionKey.setPrivateKey("de87aaa5e01fd1af0f0dd265a65a65b851510ac3557f44d553727c4d6d257f");
        InputStream stubInputStream = new ByteArrayInputStream( "Dummy data".getBytes() );
        EasyMock.expect(mockEncryptedRouteDao.getEncryptionKey(EncryptionKeyType.Ecobee)).andReturn(Optional.of(encryptionKey));
        EasyMock.replay(mockEncryptedRouteDao);
        mockEventLogHelper.decryptionFailedEventLog(EasyMock.capture(yukonService),EasyMock.capture(type));
        EasyMock.expectLastCall().once();
        EasyMock.replay(mockEventLogHelper);
        mockEcobeeSecurityServiceImpl.decryptEcobeeFile(stubInputStream);
    }
}
