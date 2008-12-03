package com.cannontech.stars.dr.enrollment.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.account.dao.impl.CustomerAccountDaoImpl;
import com.cannontech.stars.dr.account.model.CustomerAccount;
import com.cannontech.stars.dr.appliance.dao.impl.ApplianceDaoImpl;
import com.cannontech.stars.dr.enrollment.dao.impl.EnrollmentDaoImpl;
import com.cannontech.stars.dr.hardware.dao.impl.LMHardwareControlGroupDaoImpl;
import com.cannontech.stars.dr.hardware.model.LMHardwareConfiguration;
import com.cannontech.stars.dr.program.model.ProgramEnrollmentResultEnum;
import com.cannontech.stars.dr.program.service.ProgramEnrollment;
import com.cannontech.stars.dr.program.service.impl.ProgramEnrollmentServiceImpl;
import com.cannontech.user.YukonUserContext;

public class EnrollmentHelperEndpointServiceMock extends EnrollmentHelperServiceImpl {

    ApplianceDaoMock applianceDao;
    CustomerAccountDaoMock customerAccountDao;
    EnrollmentDaoMock enrollmentDao;
    ProgramEnrollmentServiceMock programEnrollmentService;
    LmHardwareControlGroupDaoMock lmHardwareControlGroupDao;

    public EnrollmentHelperEndpointServiceMock() throws Exception {
        setUp();
    }
    
    public void setUp() throws Exception {
        applianceDao = new ApplianceDaoMock();
        super.setApplianceDao(applianceDao);
        customerAccountDao = new CustomerAccountDaoMock();
        super.setCustomerAccountDao(customerAccountDao);
        enrollmentDao = new EnrollmentDaoMock();
        super.setEnrollmentDao(enrollmentDao);
        programEnrollmentService = new ProgramEnrollmentServiceMock();
        super.setProgramEnrollmentService(programEnrollmentService);
        lmHardwareControlGroupDao = new LmHardwareControlGroupDaoMock();
        super.setLmHardwareControlGroupDao(lmHardwareControlGroupDao);
    }
    
    public ProgramEnrollment getEnrollmentOne(){
        return new ProgramEnrollment(10, 20, true, 30, 40, 50, 1);
    }
    public ProgramEnrollment getEnrollmentTwo(){
        return new ProgramEnrollment(11, 0, true, 31, 41, 51, 0);
    }
    public ProgramEnrollment getEnrollmentThree(){
        return new ProgramEnrollment(11, 0, true, 31, 43, 52, 1);
    }
    public ProgramEnrollment getEnrollmentFour(){
        return new ProgramEnrollment(11, 0, true, 32, 41, 51, 0);
    }
        
    private class ApplianceDaoMock extends ApplianceDaoImpl{
        @Override
        public void updateApplianceKW(int applianceId, float applianceKW) {}
    }
    
    private class CustomerAccountDaoMock extends CustomerAccountDaoImpl{
        @Override
        public CustomerAccount getByAccountNumber(final String accountNumber, final LiteYukonUser user) {
            CustomerAccount customerAccount = new CustomerAccount();
            int accountId = Integer.parseInt(accountNumber);
            customerAccount.setAccountId(accountId);
            return customerAccount;
        }
    }

    public class EnrollmentDaoMock extends EnrollmentDaoImpl{
        @Override
        public List<ProgramEnrollment> getActiveEnrollmentsByAccountId(int accountId) {
            List<ProgramEnrollment> currentEnrollments = new ArrayList<ProgramEnrollment>(); 
            

            currentEnrollments.add(getEnrollmentOne());
            currentEnrollments.add(getEnrollmentTwo());
            currentEnrollments.add(getEnrollmentThree());
            currentEnrollments.add(getEnrollmentFour());
            return currentEnrollments;
        }
    }

    private class ProgramEnrollmentServiceMock extends ProgramEnrollmentServiceImpl{
        @Override
        public ProgramEnrollmentResultEnum applyEnrollmentRequests(CustomerAccount customerAccount, 
                                                                   List<ProgramEnrollment> requests, 
                                                                   YukonUserContext yukonUserContext) {
            return null;
        }
    }
    
    private class LmHardwareControlGroupDaoMock extends LMHardwareControlGroupDaoImpl {
        @Override
        public List<LMHardwareConfiguration> getOldConfigDataByInventoryIdAndGroupId(int inventoryId, 
                                                                                     int lmGroupId) {
            List<LMHardwareConfiguration> lmHardwareConfigs = new ArrayList<LMHardwareConfiguration>();
            LMHardwareConfiguration lmHardwareConfig = new LMHardwareConfiguration();
            lmHardwareConfigs.add(lmHardwareConfig);
            return lmHardwareConfigs;
        }
    }
}

