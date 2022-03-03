package com.cannontech.web.dev;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.cannontech.common.pao.PaoType;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationRequest;
import com.cannontech.dr.ecobee.message.ZeusAuthenticationResponse;
import com.cannontech.dr.ecobee.message.ZeusGroup;
import com.cannontech.dr.ecobee.message.ZeusGroupResponse;
import com.cannontech.dr.ecobee.message.ZeusThermostat;
import com.cannontech.dr.ecobee.message.ZeusThermostatState;
import com.cannontech.dr.ecobee.message.ZeusThermostatsDeletionResponse;
import com.cannontech.dr.ecobee.message.ZeusThermostatsResponse;
import com.cannontech.stars.dr.enrollment.dao.EnrollmentDao;
import com.cannontech.stars.dr.hardware.dao.LmHardwareBaseDao;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MockZeusResponseFactory {

    @Autowired private IDatabaseCache serverDatabaseCache;
    @Autowired private EnrollmentDao enrollementDao;
    @Autowired private LmHardwareBaseDao lmHardwareBaseDao;
    @Autowired private ZeusEcobeeDataConfiguration zeusEcobeeDataConfiguration;
    
    private Cache<String, ZeusAuthenticationResponse> mockEcobeeAuthTokenResponseCache = CacheBuilder.newBuilder()
            .expireAfterWrite(1440, TimeUnit.MINUTES).build();
    private static final String mockResponseCacheKey = "mockResponseCacheKey";
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZoneUTC();

    public ZeusAuthenticationResponse login(ZeusAuthenticationRequest request) {
        String authToken = UUID.randomUUID().toString();
        String refreshToken = UUID.randomUUID().toString();
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        String expiryTimestamp = formatter.print(afterTime);
        ZeusAuthenticationResponse response = new ZeusAuthenticationResponse(authToken, refreshToken, expiryTimestamp);
        mockEcobeeAuthTokenResponseCache.put(mockResponseCacheKey, response);
        return response;
    }

    public ZeusAuthenticationResponse refresh(String mockRefreshToken) {
        mockEcobeeAuthTokenResponseCache.invalidateAll();
        String authToken = UUID.randomUUID().toString();
        DateTime afterTime = DateTime.now(DateTimeZone.UTC).plusHours(1);
        String expiryTimestamp = formatter.print(afterTime);
        ZeusAuthenticationResponse response = new ZeusAuthenticationResponse(authToken, mockRefreshToken, expiryTimestamp);
        mockEcobeeAuthTokenResponseCache.put(mockResponseCacheKey, response);
        return response;
    }

    public boolean isInvalidRefreshToken(String mockRefreshToken) {
        return !mockRefreshToken.equals(mockEcobeeAuthTokenResponseCache.getIfPresent(mockResponseCacheKey).getRefreshToken());
    }

    public ZeusThermostatsResponse retrieveThermostats(List<String> thermostatGroupIDs) {
        ZeusThermostat thermostat = new ZeusThermostat();
        thermostat.setSerialNumber(thermostatGroupIDs.get(0));
        thermostat.setState(ZeusThermostatState.ENROLLED);
        List<ZeusThermostat> thermostats = new ArrayList<ZeusThermostat>();
        thermostats.add(thermostat);
        ZeusThermostatsResponse response = new ZeusThermostatsResponse();
        response.setThermostats(thermostats);
        return response;
    }
    
    public ZeusThermostatsDeletionResponse deleteThermostats(List<String> thermostatGroupIDs) {
        ZeusThermostatsDeletionResponse response = new ZeusThermostatsDeletionResponse();
        response.setDeletedThermostatsCount(thermostatGroupIDs.size());
        return response;
    }

    public ZeusGroupResponse retrieveGroups(String programId) {

        List<ZeusGroup> groups = new ArrayList<ZeusGroup>();
        List<LiteYukonPAObject> paoObjects = serverDatabaseCache.getAllLoadManagement().stream()
                .filter(pao -> pao.getPaoType() == PaoType.LM_GROUP_ECOBEE)
                .collect(Collectors.toList());

        // Considering i89uUYUuioyhyu36hsidch9s8NUYGUA group as a parent group of other groups.
        paoObjects.forEach(pao -> {

            ZeusGroup group = new ZeusGroup();
            group.setGroupId(String.valueOf(pao.getLiteID()));
            group.setName(String.valueOf(pao.getLiteID()));
            if (!groups.isEmpty()) {
                group.setParentGroupId("i89uUYUuioyhyu36hsidch9s8NUYGUA");
            }
            group.setProgramId(programId);
            groups.add(group);
        });

        if (zeusEcobeeDataConfiguration.getGenerateDiscrepency() == 1) {
            // MISSING_GROUP discrepancy
            if (groups.size() >= 2 && groups.get(1) != null) {
                groups.remove(1);
            }

            // EXTRANEOUS_GROUP discrepancy
            ZeusGroup group = new ZeusGroup();
            group.setGroupId(String.valueOf("8000"));
            group.setName("EcobeeGroupDecrepency");
            if (!groups.isEmpty()) {
                group.setParentGroupId("i89uUYUuioyhyu36hsidch9s8NUYGUA");
            }
            groups.add(group);
        }
        ZeusGroupResponse response = new ZeusGroupResponse();
        groups.get(0).setGroupId("i89uUYUuioyhyu36hsidch9s8NUYGUA");
        response.setGroups(groups);
        return response;
    }
    
    public ZeusThermostatsResponse getThermostatsInGroup(String groupId) {

        Set<Integer> inventoryIds = enrollementDao.getActiveEnrolledInventoryIdsForGroupIds(Arrays.asList(Integer.parseInt(groupId)));
        List<String> serialNumber = lmHardwareBaseDao.getSerialNumberForInventoryIds(inventoryIds);
        List<ZeusThermostat> thermostats = new ArrayList<ZeusThermostat>();
        serialNumber.stream().forEach(srNo -> {
            ZeusThermostat thermostat = new ZeusThermostat();
            thermostat.setSerialNumber(srNo);
            thermostat.setState(ZeusThermostatState.ENROLLED);
            thermostats.add(thermostat);

        });
        ZeusThermostatsResponse response = new ZeusThermostatsResponse();
        if (zeusEcobeeDataConfiguration.getGenerateDiscrepency() == 1) {
            // MISLOCATED_DEVICE discrepancy
            if (!thermostats.isEmpty() && thermostats.get(0) != null) {
                List<String> groups = new ArrayList<String>();
                groups.add("9999");
                thermostats.get(0).setZeusGroups(groups);
            }

            // MISSING_DEVICE discrepancy
            if (thermostats.size() >= 2 && thermostats.get(1) != null) {
                thermostats.remove(1);
            }
            // EXTRANEOUS_DEVICE discrepancy
            ZeusThermostat descrepencythermostat = new ZeusThermostat();
            descrepencythermostat.setSerialNumber("9000");
            descrepencythermostat.setState(ZeusThermostatState.ENROLLED);
            thermostats.add(descrepencythermostat);
        }
        response.setThermostats(thermostats);
        return response;
    }

    public ResponseEntity<Object> retrievePaginatedGroups(String programId, Integer pageNumber) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(1);
        headers.add("x-total-count", "21");
        ZeusGroupResponse response = new ZeusGroupResponse();
        List<ZeusGroup> groups = new ArrayList<ZeusGroup>();
        int maxSize = pageNumber == 1 ? 1 : 20;
        for (int i = 1; i <= maxSize; i++) {
            ZeusGroup group = new ZeusGroup();
            group.setGroupId(Integer.toString(pageNumber * 20 + i));
            group.setName(group.getGroupId());
            groups.add(group);
        }
        response.setGroups(groups);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
        return responseEntity;
    }

    public ResponseEntity<Object> getPaginatedThermostatsInGroup(String thermostatGroupID, Integer pageNumber) {
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>(1);
        headers.add("x-total-count", "105");
        int maxSize = pageNumber == 1 ? 5 : 100;
        ZeusThermostatsResponse response = new ZeusThermostatsResponse();
        List<ZeusThermostat> thermostats = new ArrayList<ZeusThermostat>();
        for (int i = 1; i <= maxSize; i++) {
            ZeusThermostat thermostat = new ZeusThermostat();
            thermostat.setSerialNumber(Integer.toString(pageNumber * 100 + i));
            thermostat.setState(ZeusThermostatState.ENROLLED);
            thermostats.add(thermostat);
        }
        response.setThermostats(thermostats);
        ResponseEntity<Object> responseEntity = new ResponseEntity<>(response, headers, HttpStatus.OK);
        return responseEntity;
    }
}