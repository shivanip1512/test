/**
 * 
 */
package com.cannontech.core.dao;

import java.util.List;
import java.util.Map;

import com.cannontech.database.cache.DBChangeListener;
import com.cannontech.database.cache.DBChangeLiteListener;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.database.data.lite.LiteBaseline;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteCommand;
import com.cannontech.database.data.lite.LiteConfig;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteDeviceMeterNumber;
import com.cannontech.database.data.lite.LiteDeviceTypeCommand;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteGear;
import com.cannontech.database.data.lite.LiteGraphDefinition;
import com.cannontech.database.data.lite.LiteHolidaySchedule;
import com.cannontech.database.data.lite.LiteLMConstraint;
import com.cannontech.database.data.lite.LiteLMPAOExclusion;
import com.cannontech.database.data.lite.LiteLMProgScenario;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LitePointLimit;
import com.cannontech.database.data.lite.LiteSeasonSchedule;
import com.cannontech.database.data.lite.LiteSettlementConfig;
import com.cannontech.database.data.lite.LiteStateGroup;
import com.cannontech.database.data.lite.LiteTOUDay;
import com.cannontech.database.data.lite.LiteTOUSchedule;
import com.cannontech.database.data.lite.LiteTag;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.data.lite.LiteYukonImage;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonRole;
import com.cannontech.database.data.lite.LiteYukonRoleProperty;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.CTIDbChange;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.yukon.IDatabaseCache;

public class IDatabaseCacheAdapter implements IDatabaseCache {
    public DBChangeMsg[] createDBChangeMessages(CTIDbChange newItem, DbChangeType dbChangeType) {
        throw new UnsupportedOperationException();
    }

    public LiteContact getAContactByContactID(int contactID) {

        throw new UnsupportedOperationException();
    }

    public LiteContact getAContactByUserID(int userID) {

        throw new UnsupportedOperationException();
    }

    public LiteContactNotification getAContactNotifByNotifID(int contNotifyID) {

        throw new UnsupportedOperationException();
    }

    public LiteCustomer getACustomerByPrimaryContactID(int contactID) {

        throw new UnsupportedOperationException();
    }

    public LiteCustomer getACustomerByCustomerID(int customerID) {

        throw new UnsupportedOperationException();
    }

    public LiteYukonRole getARole(LiteYukonUser user, int roleID) {

        throw new UnsupportedOperationException();
    }

    public String getARolePropertyValue(LiteYukonUser user, int rolePropertyID) {

        throw new UnsupportedOperationException();
    }

    public List<LiteAlarmCategory> getAllAlarmCategories() {

        throw new UnsupportedOperationException();
    }

    public List<LiteBaseline> getAllBaselines() {

        throw new UnsupportedOperationException();
    }

    public List<LiteCICustomer> getAllCICustomers() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllCapControlFeeders() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllCapControlSubBuses() {

        throw new UnsupportedOperationException();
    }
    
    public List<LiteYukonPAObject> getAllCapControlSubStations() {

        throw new UnsupportedOperationException();
    }

    public List<LiteCommand> getAllCommands() {

        throw new UnsupportedOperationException();
    }

    public Map<Integer, LiteCommand> getAllCommandsMap() {

        throw new UnsupportedOperationException();
    }

    public List<LiteConfig> getAllConfigs() {

        throw new UnsupportedOperationException();
    }

    public List<LiteNotificationGroup> getAllContactNotificationGroups() {

        throw new UnsupportedOperationException();
    }

    public List<LiteNotificationGroup> getAllContactNotificationGroupsWithNone() {

        throw new UnsupportedOperationException();
    }

    public Map<Integer, LiteContactNotification> getAllContactNotifsMap() {

        throw new UnsupportedOperationException();
    }

    public List<LiteContact> getAllContacts() {

        throw new UnsupportedOperationException();
    }

    public List<String> getAllDMG_AlternateGroups() {

        throw new UnsupportedOperationException();
    }

    public List<String> getAllDMG_BillingGroups() {

        throw new UnsupportedOperationException();
    }

    public List<String> getAllDMG_CollectionGroups() {

        throw new UnsupportedOperationException();
    }

    public List<LiteDeviceMeterNumber> getAllDeviceMeterGroups() {

        throw new UnsupportedOperationException();
    }

    public List<LiteDeviceTypeCommand> getAllDeviceTypeCommands() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllDevices() {

        throw new UnsupportedOperationException();
    }

    public List<LiteEnergyCompany> getAllEnergyCompanies() {

        throw new UnsupportedOperationException();
    }

    public List<LiteGear> getAllGears() {

        throw new UnsupportedOperationException();
    }

    public List<LiteGraphDefinition> getAllGraphDefinitions() {

        throw new UnsupportedOperationException();
    }

    public List<LiteHolidaySchedule> getAllHolidaySchedules() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllLMControlAreas() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllLMGroups() {

        throw new UnsupportedOperationException();
    }

    public List<LiteLMPAOExclusion> getAllLMPAOExclusions() {

        throw new UnsupportedOperationException();
    }

    public List<LiteLMConstraint> getAllLMProgramConstraints() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllLMPrograms() {

        throw new UnsupportedOperationException();
    }
    
    public List<LiteYukonPAObject> getAllLMDirectPrograms() {

        throw new UnsupportedOperationException();
    }

    public List<LiteLMProgScenario> getAllLMScenarioProgs() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllLMScenarios() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllLoadManagement() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllMCTs() {

        throw new UnsupportedOperationException();
    }

    public Map<Integer, LiteYukonPAObject> getAllPAOsMap() {

        throw new UnsupportedOperationException();
    }

    public List<LitePointLimit> getAllPointLimits() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllPorts() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllRoutes() {

        throw new UnsupportedOperationException();
    }

    public List<LiteSeasonSchedule> getAllSeasonSchedules() {

        throw new UnsupportedOperationException();
    }


    public Map<Integer, LiteStateGroup> getAllStateGroupMap() {

        throw new UnsupportedOperationException();
    }

    public List<LitePoint> getAllSystemPoints() {

        throw new UnsupportedOperationException();
    }

    public List<LiteTOUDay> getAllTOUDays() {

        throw new UnsupportedOperationException();
    }

    public List<LiteTOUSchedule> getAllTOUSchedules() {

        throw new UnsupportedOperationException();
    }

    public List<LiteTag> getAllTags() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllUnusedCCDevices() {

        throw new UnsupportedOperationException();
    }

    public Map<Integer, LiteYukonUser> getAllUsersMap() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonGroup> getAllYukonGroups() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonImage> getAllYukonImages() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonPAObject> getAllYukonPAObjects() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonRoleProperty> getAllYukonRoleProperties() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonRole> getAllYukonRoles() {

        throw new UnsupportedOperationException();
    }

    public List<LiteYukonUser> getAllYukonUsers() {

        throw new UnsupportedOperationException();
    }

    public LiteContact getContactsByEmail(String email) {

        throw new UnsupportedOperationException();
    }

    public LiteContact[] getContactsByFirstName(String firstName, boolean partialMatch) {

        throw new UnsupportedOperationException();
    }

    public LiteContact[] getContactsByLastName(String lastName, boolean partialMatch) {

        throw new UnsupportedOperationException();
    }

    public LiteContact[] getContactsByPhoneNumber(String phone, boolean partialMatch) {

        throw new UnsupportedOperationException();
    }

    public List getDevicesByCommPort(int portId) {

        throw new UnsupportedOperationException();
    }

    public List getDevicesByDeviceAddress(Integer masterAddress, Integer slaveAddress) {

        throw new UnsupportedOperationException();
    }

    public Map<LiteYukonGroup, Map<LiteYukonRole, Map<LiteYukonRoleProperty, String>>> getYukonGroupRolePropertyMap() {

        throw new UnsupportedOperationException();
    }

    public Map<LiteYukonGroup, List<LiteYukonUser>> getYukonGroupUserMap() {

        throw new UnsupportedOperationException();
    }

    public Map<LiteYukonUser, List<LiteYukonGroup>> getYukonUserGroupMap() {

        throw new UnsupportedOperationException();
    }

    @Override
    public LiteEnergyCompany getALiteEnergyCompanyByUserID( LiteYukonUser liteYukonUser) {
    	throw new UnsupportedOperationException();
    }
    
    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg) {

        throw new UnsupportedOperationException();
    }

    public LiteBase handleDBChangeMessage(DBChangeMsg dbChangeMsg, boolean noObjectNeeded) {
        
        throw new UnsupportedOperationException();
    }
    
    public void releaseAllAlarmCategories() {

        
    }

    public void releaseAllBaselines() {

        
    }

    public void releaseAllCache() {

        
    }

    public void releaseAllCommands() {

        
    }

    public void releaseAllConfigs() {

        
    }

    public void releaseAllContacts() {

        
    }

    public void releaseAllCustomers() {

        
    }

    public void releaseAllDeviceMeterGroups() {

        
    }

    public void releaseAllDeviceTypeCommands() {

        
    }

    public void releaseAllGraphDefinitions() {

        
    }

    public void releaseAllHolidaySchedules() {

        
    }

    public void releaseAllLMPAOExclusions() {

        
    }

    public void releaseAllLMProgramConstraints() {

        
    }

    public void releaseAllLMScenarios() {

        
    }

    public void releaseAllNotificationGroups() {

        
    }

    public void releaseAllSeasonSchedules() {

        
    }

    public void releaseAllSettlementConfigs() {

        
    }

    public void releaseAllStateGroups() {

        
    }

    public void releaseAllTOUDays() {

        
    }

    public void releaseAllTOUSchedules() {

        
    }

    public void releaseAllTags() {

        
    }

    public void releaseAllYukonImages() {

        
    }

    public void releaseAllYukonPAObjects() {

        
    }

    public void releaseAllYukonUsers() {

        
    }

    public void releaseUserContactMap() {

        
    }

    public void releaseUserRoleMap() {

        
    }

    public void releaseUserRolePropertyValueMap() {

        
    }

    public void removeDBChangeListener(DBChangeListener listener) {

        
    }

    public void removeDBChangeLiteListener(DBChangeLiteListener listener) {

        
    }

    public void setDatabaseAlias(String newAlias) {

        
    }
}