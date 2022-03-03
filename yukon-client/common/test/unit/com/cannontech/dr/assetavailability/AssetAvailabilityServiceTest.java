package com.cannontech.dr.assetavailability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.MutableDateTime;
import org.junit.jupiter.api.Test;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.dr.assetavailability.service.AssetAvailabilityService;
import com.google.common.collect.Sets;

public class AssetAvailabilityServiceTest {

    @Test
    public void getAssetAvailabilityWithAppliance_ByDrGroupPaoIdentifier() {
        Instant now = getNow();

        // All the data this method asserts against is set up here
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState(false, now);
        AssetAvailabilityCombinedStatus[] filters = { AssetAvailabilityCombinedStatus.ACTIVE };
        // 107 = area containing all inventory
        PaoIdentifier paoIdentifier = new PaoIdentifier(107, PaoType.LM_CONTROL_AREA);

        SearchResults<ApplianceAssetAvailabilityDetails> result = assetAvailabilityService.getAssetAvailabilityWithAppliance(
                paoIdentifier,
                PagingParameters.EVERYTHING, filters, null, null);
        List<ApplianceAssetAvailabilityDetails> assetAvailabilityDetails = result.getResultList();

        testAssetAvailabilityDetailsWithAppliance(assetAvailabilityDetails.get(0));
    }

    @Test
    public void getAssetAvailability_ByInventoryIds() {
        Instant now = getNow();

        // All the data this method asserts against is set up here
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState(false, now);

        Set<Integer> expectedInventoryIds = Sets.newHashSet(1, 2, 3, 4, 5, 6, 7, 8);
        Map<Integer, SimpleAssetAvailability> aaMap = assetAvailabilityService.getAssetAvailability(expectedInventoryIds);

        assertTrue(aaMap.keySet().equals(expectedInventoryIds), "Inventory id map mismatch.");

        testSimpleAssetAvailability_OneWay(aaMap.get(1), now);
        testSimpleAssetAvailability_OneWay_OptedOut(aaMap.get(2), now);
        testSimpleAssetAvailability_TwoWay_Active(aaMap.get(3), now);
        testSimpleAssetAvailability_TwoWay_Inactive(aaMap.get(4), now);
        testSimpleAssetAvailability_TwoWay_Unavailable(aaMap.get(5), now);
        testSimpleAssetAvailability_TwoWay_Active_OptedOut(aaMap.get(6), now);
        testSimpleAssetAvailability_TwoWay_Inactive_OptedOut(aaMap.get(7), now);
        testSimpleAssetAvailability_TwoWay_Unavailable_OptedOut(aaMap.get(8), now);
    }

    @Test
    public void getAssetAvailability_ByInventoryId() {
        Instant now = getNow();

        // All the data this method asserts against is set up here
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState(false, now);

        testSimpleAssetAvailability_OneWay(assetAvailabilityService.getAssetAvailability(1), now);
        testSimpleAssetAvailability_OneWay_OptedOut(assetAvailabilityService.getAssetAvailability(2), now);
        testSimpleAssetAvailability_TwoWay_Active(assetAvailabilityService.getAssetAvailability(3), now);
        testSimpleAssetAvailability_TwoWay_Inactive(assetAvailabilityService.getAssetAvailability(4), now);
        testSimpleAssetAvailability_TwoWay_Unavailable(assetAvailabilityService.getAssetAvailability(5), now);
        testSimpleAssetAvailability_TwoWay_Active_OptedOut(assetAvailabilityService.getAssetAvailability(6), now);
        testSimpleAssetAvailability_TwoWay_Inactive_OptedOut(assetAvailabilityService.getAssetAvailability(7), now);
        testSimpleAssetAvailability_TwoWay_Unavailable_OptedOut(assetAvailabilityService.getAssetAvailability(8), now);
    }

    @Test
    public void getApplianceAssetAvailability_ByDrGroupPaoIdentifier() {
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState();

        PaoIdentifier controlArea = new PaoIdentifier(107, PaoType.LM_CONTROL_AREA);
        ApplianceAssetAvailabilitySummary applianceSummary = assetAvailabilityService.getApplianceAssetAvailability(controlArea);

        testApplianceAssetAvailabilitySummary(applianceSummary);
    }

    @Test
    public void getAssetAvailabilityFromDrGroup() {
        PaoIdentifier drPaoIdentifier = new PaoIdentifier(107, PaoType.LM_CONTROL_AREA); // 107 = area containing all inventory

        // All the data this method asserts against is set up here
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState();

        AssetAvailabilitySummary aaSummary = assetAvailabilityService.getAssetAvailabilityFromDrGroup(drPaoIdentifier);

        testAssetAvailabilitySummary(aaSummary);
    }

    private void testApplianceAssetAvailabilitySummary(ApplianceAssetAvailabilitySummary applianceSummary) {
        // All appliances
        Set<Integer> expectedAllAppliances = Sets.newHashSet(10011, 10021, 10031, 10032, 10041, 10051, 10061, 10071, 10081);
        assertTrue(applianceSummary.getAll().equals(expectedAllAppliances),
                "One or more appliance ids missing. " + applianceSummary.getAll());

        // Opted-out appliances
        Set<Integer> expectedOptedOut = Sets.newHashSet(10021, 10061, 10071, 10081);
        assertTrue(applianceSummary.getOptedOut().containsAll(expectedOptedOut), "Opted-out appliance ids mismatch.");
        assertEquals(4, applianceSummary.getOptedOutSize(), "Incorrect number of opted-out appliances.");

        // Active appliances
        Set<Integer> expectedActive = Sets.newHashSet(10011, 10032); // 10021 and 10061 are also active, but opted-out
        assertTrue(applianceSummary.getActive().equals(expectedActive),
                "Active appliance ids mismatch: " + applianceSummary.getActive());
        assertEquals(2, applianceSummary.getActiveSize(), "Incorrect number of active appliances."); // exclude opted-out
        assertEquals(2, applianceSummary.getActiveSize(false), "Incorrect number of active appliances."); // exclude opted-out
        assertEquals(2, applianceSummary.getActiveSize(true), "Incorrect number of active appliances."); // include opted-out

        // Inactive appliances
        Set<Integer> expectedInactive = Sets.newHashSet(10031, 10041); // 10071 is inactive, but opted out
        assertTrue(applianceSummary.getInactive().equals(expectedInactive), "Inactive appliance ids mismatch.");
        assertEquals(2, applianceSummary.getInactiveSize(), "Incorrect number of inactive appliances"); // exclude opted-out
        assertEquals(2, applianceSummary.getInactiveSize(false), "Incorrect number of inactive appliances."); // exclude opted-out
        assertEquals(2, applianceSummary.getInactiveSize(true), "Incorrect number of inactive appliances."); // include opted-out

        // Unavailable appliances
        Set<Integer> expectedUnavailable = Sets.newHashSet(10051); // 10081 is unavailable, but opted out
        assertTrue(applianceSummary.getUnavailable().equals(expectedUnavailable), "Unavailable appliance ids mismatch.");
        assertEquals(1, applianceSummary.getUnavailableSize(), "Incorrect number of unavailable appliances"); // exclude opted-out
        assertEquals(1, applianceSummary.getUnavailableSize(false), "Incorrect number of unavailable appliances"); // exclude
                                                                                                                   // opted-out
        assertEquals(5, applianceSummary.getUnavailableSize(true), "Incorrect number of unavailable appliances"); // include
                                                                                                                  // opted-out
    }

    private void testAssetAvailabilitySummary(AssetAvailabilitySummary aaSummary) {

        // Opted-out inventory
        assertEquals(4, aaSummary.getOptedOutSize().intValue(), "Incorrect number of opted-out inventory.");

        // Active inventory
        assertEquals(2, aaSummary.getActiveSize().intValue(), "Incorrect number of active inventory."); // exclude opted-out

        // Inactive inventory
        assertEquals(1, aaSummary.getInactiveSize().intValue(), "Incorrect number of incactive inventory"); // exclude opted-out

        // Unavailable inventory
        assertEquals(1, aaSummary.getUnavailableSize().intValue(), "Incorrect number of unavailable inventory"); // exclude
                                                                                                                 // opted-out
    }

    private void testAssetAvailabilityDetailsWithAppliance(ApplianceAssetAvailabilityDetails availability) {
        assertEquals(AssetAvailabilityCombinedStatus.ACTIVE, availability.getAvailability(), "Incorrect A.A. status.");
        assertNull(availability.getLastComm(), "Incorrect communication time.");
        assertNull(availability.getLastRun(), "Incorrect last non-zero run time.");
        assertNotNull(availability.getSerialNumber(), "Incorrect serial number");
        assertNotNull(availability.getType(), "Incorrect type");
        assertNotNull(availability.getAppliances(), "Incorrect appliance");

    }

    // Test inventory 1 - one-way
    private void testSimpleAssetAvailability_OneWay(SimpleAssetAvailability availability, Instant now) {
        assertEquals(1, availability.getInventoryId(), "Inventory id mismatch.");
        assertFalse(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.ACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.ACTIVE, availability.getCombinedStatus(), "Incorrect A.A. combined status.");
        assertNull(availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime1 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10011, appRuntime1.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime1.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 2 - one-way, opted-out
    private void testSimpleAssetAvailability_OneWay_OptedOut(SimpleAssetAvailability availability, Instant now) {
        assertEquals(2, availability.getInventoryId(), "Inventory id mismatch.");
        assertTrue(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.ACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.OPTED_OUT, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertNull(availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime2 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10021, appRuntime2.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime2.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 3 - two-way, active
    private void testSimpleAssetAvailability_TwoWay_Active(SimpleAssetAvailability availability, Instant now) {
        Instant tenMinutesAgo = now.minus(Duration.standardMinutes(10));

        assertEquals(3, availability.getInventoryId(), "Inventory id mismatch.");
        assertFalse(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.ACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.ACTIVE, availability.getCombinedStatus(), "Incorrect A.A. combined status.");
        assertEquals(tenMinutesAgo, availability.getLastCommunicationTime(), "Incorrect communication time.");
        Set<ApplianceWithRuntime> applianceRuntimeSet = availability.getApplianceRuntimes();
        assertEquals(2, applianceRuntimeSet.size(), "Incorrect number of appliances.");
        ApplianceWithRuntime relay1ApplianceRuntime = new ApplianceWithRuntime(10031, null);
        ApplianceWithRuntime relay2ApplianceRuntime = new ApplianceWithRuntime(10032, tenMinutesAgo);
        assertTrue(applianceRuntimeSet.contains(relay1ApplianceRuntime), "Incorrect appliance/runtime values.");
        assertTrue(applianceRuntimeSet.contains(relay2ApplianceRuntime), "Incorrect appliance/runtime values.");
    }

    // Test inventory 4 - two-way, inactive
    private void testSimpleAssetAvailability_TwoWay_Inactive(SimpleAssetAvailability availability, Instant now) {
        Instant tenMinutesAgo = now.minus(Duration.standardMinutes(10));

        assertEquals(4, availability.getInventoryId(), "Inventory id mismatch.");
        assertFalse(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.INACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.INACTIVE, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertEquals(tenMinutesAgo, availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime4 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10041, appRuntime4.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime4.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 5 - two-way, unavailable
    private void testSimpleAssetAvailability_TwoWay_Unavailable(SimpleAssetAvailability availability, Instant now) {
        assertEquals(5, availability.getInventoryId(), "Inventory id mismatch.");
        assertFalse(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.UNAVAILABLE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.UNAVAILABLE, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertNull(availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime5 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10051, appRuntime5.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime5.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 6 - two-way, active, opted-out
    private void testSimpleAssetAvailability_TwoWay_Active_OptedOut(SimpleAssetAvailability availability, Instant now) {
        Instant tenMinutesAgo = now.minus(Duration.standardMinutes(10));

        assertEquals(6, availability.getInventoryId(), "Inventory id mismatch.");
        assertTrue(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.ACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.OPTED_OUT, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertEquals(tenMinutesAgo, availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime6 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10061, appRuntime6.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertEquals(tenMinutesAgo, appRuntime6.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 7 - two-way, inactive, opted-out
    private void testSimpleAssetAvailability_TwoWay_Inactive_OptedOut(SimpleAssetAvailability availability, Instant now) {
        Instant tenMinutesAgo = now.minus(Duration.standardMinutes(10));

        assertEquals(7, availability.getInventoryId(), "Inventory id mismatch.");
        assertTrue(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.INACTIVE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.OPTED_OUT, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertEquals(tenMinutesAgo, availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime7 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10071, appRuntime7.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime7.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    // Test inventory 8 - two-way, unavailable, opted-out
    private void testSimpleAssetAvailability_TwoWay_Unavailable_OptedOut(SimpleAssetAvailability availability, Instant now) {
        assertEquals(8, availability.getInventoryId(), "Inventory id mismatch.");
        assertTrue(availability.isOptedOut(), "Incorrect opt-out value.");
        assertEquals(AssetAvailabilityStatus.UNAVAILABLE, availability.getStatus(), "Incorrect A.A. status.");
        assertEquals(AssetAvailabilityCombinedStatus.OPTED_OUT, availability.getCombinedStatus(),
                "Incorrect A.A. combined status.");
        assertNull(availability.getLastCommunicationTime(), "Incorrect communication time.");
        assertEquals(1, availability.getApplianceRuntimes().size(), "Incorrect number of appliances.");
        ApplianceWithRuntime appRuntime8 = availability.getApplianceRuntimes().iterator().next();
        assertEquals(10081, appRuntime8.getApplianceCategoryId(), "Incorrect appliance category id.");
        assertNull(appRuntime8.getLastNonZeroRuntime(), "Incorrect last non-zero run time.");
    }

    /*
     * Gets the current date/time Instant, with millis rounded off.
     */
    private Instant getNow() {
        MutableDateTime now = MutableDateTime.now();
        now.setMillisOfSecond(0);
        return new Instant(now);
    }

    /*
     * Creates an AssetAvailabilityService and wires in all required mock daos and services with the following data
     * set up:
     * 
     * - One-way (inventory=1, device=0, relay 1=appliance 10011)
     * - Opted-out one-way (inventory=2, device=0, relay 1=appliance 10021)
     * - Two-way active (inventory=3, device=1003, relay 1=appliance 10031, relay 2=appliance 10032)
     * - Two-way inactive (inventory=4, device=1004, relay 3=appliance 10041)
     * - Two-way unavailable (inventory=5, device=1005, relay 4=appliance 10051)
     * - Two-way opted-out active (inventory=6, device=1006, relay 1=appliance 10061)
     * - Two-way opted-out inactive (inventory=7, device=1007, relay 1=appliance 10071)
     * - Two-way opted-out unavailable (inventory=8, device=1008, relay 1=appliance 10081)
     * 
     * - Load Group containing one-way devices (id 100)
     * - Program containing one-way Load Group (id 102)
     * - Control area containing one-way Load Group (id 103)
     * 
     * - Load Group containing two-way devices (id 101)
     * - Program containing two-way Load Group (id 104)
     * - Control Area containing two-way Load Group (id 105)
     * 
     * - Program containing both Load Groups (id 106)
     * - Control Area containing both Load Groups (id 107)
     * 
     * - relay runtime attributes and points for each two-way device (pointId = deviceId + 00 + relay)
     */
    private AssetAvailabilityService buildServiceWithOneInEachState() {
        return buildServiceWithOneInEachState(false, getNow());
    }

    private AssetAvailabilityService buildServiceWithOneInEachState(boolean databaseUpToDate, Instant now) {
        Instant tenMinutesAgo = now.minus(Duration.standardMinutes(10));

        AssetAvailabilityServiceBuilder builder = new AssetAvailabilityServiceBuilder();
        builder.withCommunicationHours(1)
                .withRuntimeHours(2)
                // group with one-way devices
                .withLoadGroupInventory(100, new Integer[] { 1, 2 })
                // group with two-way devices
                .withLoadGroupInventory(101, new Integer[] { 3, 4, 5, 6, 7, 8 })
                // opted-out inventory
                .withOptedOutInventory(2, 6, 7, 8)
                // inventory to device mapping
                .withInventoryToDeviceMapping(1, 0)
                .withInventoryToDeviceMapping(2, 0)
                .withInventoryToDeviceMapping(3, 1003)
                .withInventoryToDeviceMapping(4, 1004)
                .withInventoryToDeviceMapping(5, 1005)
                .withInventoryToDeviceMapping(6, 1006)
                .withInventoryToDeviceMapping(7, 1007)
                .withInventoryToDeviceMapping(8, 1008)
                // device->relay->appliance mapping
                .withInventoryRelayApplianceMapping(1, 1, 10011)
                .withInventoryRelayApplianceMapping(2, 1, 10021)
                .withInventoryRelayApplianceMapping(3, new Integer[][] { { 1, 10031 }, { 2, 10032 } })
                .withInventoryRelayApplianceMapping(4, 3, 10041)
                .withInventoryRelayApplianceMapping(5, 4, 10051)
                .withInventoryRelayApplianceMapping(6, 1, 10061)
                .withInventoryRelayApplianceMapping(7, 1, 10071)
                .withInventoryRelayApplianceMapping(8, 1, 10081)
                // one-way program and area
                .withDrGroupToLoadGroupIds(new PaoIdentifier(102, PaoType.LM_DIRECT_PROGRAM), 100)
                .withDrGroupToLoadGroupIds(new PaoIdentifier(103, PaoType.LM_CONTROL_AREA), 100)
                // two-way program and area
                .withDrGroupToLoadGroupIds(new PaoIdentifier(104, PaoType.LM_CONTROL_AREA), 101)
                .withDrGroupToLoadGroupIds(new PaoIdentifier(105, PaoType.LM_CONTROL_AREA), 101)
                // everything program and area
                .withDrGroupToLoadGroupIds(new PaoIdentifier(106, PaoType.LM_CONTROL_AREA), 100, 101)
                .withDrGroupToLoadGroupIds(new PaoIdentifier(107, PaoType.LM_CONTROL_AREA), 100, 101)
                // rph and dynamic data
                // run time for inventory 3
                .withData(1003, tenMinutesAgo, null, tenMinutesAgo, null, null)
                // comm data for inventory 4
                .withData(1004, tenMinutesAgo, null, null, null, null)
                // no data for inventory 5 (it's unavailable) - entry exists, but all nulls
                .withData(1005, null, null, null, null, null)
                // run time for inventory 6
                .withData(1006, tenMinutesAgo, tenMinutesAgo, null, null, null)
                // comm data for inventory 7
                .withData(1007, tenMinutesAgo, null, null, null, null);
        // no data for inventory 8 (it's unavailable) - entry does not exist
        return builder.build();
    }

    @Test
    public void getAssetAvailability_ByDrGroupPaoIdentifier() {
        Instant now = getNow();

        // All the data this method asserts against is set up here
        AssetAvailabilityService assetAvailabilityService = buildServiceWithOneInEachState(false, now);
        AssetAvailabilityCombinedStatus[] filters = { AssetAvailabilityCombinedStatus.ACTIVE };
        // 107 = area containing all inventory
        PaoIdentifier paoIdentifier = new PaoIdentifier(107, PaoType.LM_CONTROL_AREA);

        SearchResults<AssetAvailabilityDetails> result = assetAvailabilityService.getAssetAvailabilityDetails(null, paoIdentifier,
                PagingParameters.EVERYTHING, filters, null, null, Direction.asc, null);
        List<AssetAvailabilityDetails> assetAvailabilityDetails = result.getResultList();

        testAssetAvailabilityDetails(assetAvailabilityDetails.get(0));
    }

    private void testAssetAvailabilityDetails(AssetAvailabilityDetails availability) {
        assertEquals(AssetAvailabilityCombinedStatus.ACTIVE, availability.getAvailability(), "Incorrect A.A. status.");
        assertNull(availability.getLastComm(), "Incorrect communication time.");
        assertNull(availability.getLastRun(), "Incorrect last non-zero run time.");
        assertNotNull(availability.getSerialNumber(), "Incorrect serial number");
        assertNotNull(availability.getType(), "Incorrect type");
        assertNotNull(availability.getDeviceId(), "Incorrect DeviceID");
        assertNotNull(availability.getInventoryId(), "Incorrect InventoryID");
    }
}
