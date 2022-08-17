package com.cannontech.services.mock;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cannontech.amr.meter.model.SimpleMeter;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.MethodNotImplementedException;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

/**
 * This stub has concrete implementations of all interface methods, but all of them throw a 
 * MethodNotImplementedException. This class can be extended by testing mocks that need to 
 * implement specific methods. 
 */
public class StubServerDatabaseCache implements IDatabaseCache {

    @Override
    public DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllCapControlFeeders() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllCapControlSubBuses() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllCapControlSubStations() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteCICustomer> getAllCICustomers() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, SimpleMeter> getAllMeters() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllDevices() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllMCTs() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteGraphDefinition> getAllGraphDefinitions() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteHolidaySchedule> getAllHolidaySchedules() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteBaseline> getAllBaselines() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteSeasonSchedule> getAllSeasonSchedules() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteTOUSchedule> getAllTOUSchedules() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteCommand> getAllCommands() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteConfig> getAllConfigs() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteLMConstraint> getAllLMProgramConstraints() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteLMProgScenario> getAllLMScenarioProgs() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllLMScenarios() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteLMPAOExclusion> getAllLMPAOExclusions() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllLMPrograms() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllLMControlAreas() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllLMGroups() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<String, LiteYukonPAObject> getAllLMGroupsMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllLoadManagement() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteNotificationGroup> getAllContactNotificationGroups() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LitePoint> getAllSystemPoints() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteYukonPAObject> getAllPaosMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteContactNotification> getAllContactNotifsMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LitePointLimit> getAllPointLimits() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllPorts() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllRoutes() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteYukonPAObject> getAllRoutesMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonPAObject> getAllYukonPAObjects() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonGroup> getAllYukonGroups() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonRole> getAllYukonRoles() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteYukonRoleProperty> getAllYukonRoleProperties() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonGroupRolePropertyMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<LiteDeviceTypeCommand> getAllDeviceTypeCommands() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteDeviceTypeCommand> getAllDeviceTypeCommandsMap() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteDeviceTypeCommand getDeviceTypeCommand(int deviceCommandId) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public void releaseAllCache() {
    }

    @Override
    public void releaseAllContacts() {
    }

    @Override
    public void releaseAllMeters() {
    }

    @Override
    public void releaseAllYukonImages() {
    }

    @Override
    public void releaseAllGraphDefinitions() {
    }

    @Override
    public void releaseAllHolidaySchedules() {
    }

    @Override
    public void releaseAllBaselines() {
    }

    @Override
    public void releaseAllSeasonSchedules() {
    }

    @Override
    public void releaseAllCommands() {
    }

    @Override
    public void releaseAllTOUSchedules() {
    }

    @Override
    public void releaseAllConfigs() {
    }

    @Override
    public void releaseAllLMProgramConstraints() {
    }

    @Override
    public void releaseAllLMScenarios() {
    }

    @Override
    public void releaseAllLMPAOExclusions() {
    }

    @Override
    public void releaseAllNotificationGroups() {
    }

    @Override
    public void releaseAllCustomers() {
    }

    @Override
    public void releaseAllYukonPAObjects() {
    }

    @Override
    public void releaseAllDeviceTypeCommands() {
    }

    @Override
    public LiteContact getAContactByUserID(int userID) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteContact getAContactByContactID(int contactID) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteContactNotification getContactNotification(int contactNotificationId) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteCustomer getACustomerByPrimaryContactID(int contactID) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public LiteCustomer getCustomer(int customerId) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public void releaseUserContactMap() {
    }

    @Override
    public List<Integer> getDevicesByCommPort(int portId) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public List<Integer> getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Map<Integer, LiteYukonImage> getImages() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

    @Override
    public Set<PaoType> getAllPaoTypes() {
        throw new MethodNotImplementedException("Method not implemented yet.");
    }

}
