package com.cannontech.cc.service;

import java.util.Collection;
import java.util.Set;

import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.core.dao.LMDao;
import com.cannontech.core.dao.LMDirectCustomerListDao;
import com.cannontech.database.data.lite.LiteYukonPAObject;

public class CustomerLMProgramService {
    private LMDao lmDao;
    private LMDirectCustomerListDao lmDirectCustomerListDao;

    public CustomerLMProgramService() {
        super();
    }

    public Set<LiteYukonPAObject> getProgramsForCustomer(CICustomerStub customer) {
        Set<LiteYukonPAObject> programPaosForCustomer = 
            lmDirectCustomerListDao.getLMProgramPaosForCustomer(customer.getId());
        return programPaosForCustomer;
    }
    
    public Set<LiteYukonPAObject> getAvailableProgramsForCustomer(CICustomerStub customer) {
        Set<LiteYukonPAObject> programPaosForCustomer = 
            lmDirectCustomerListDao.getLMProgramPaosForCustomer(customer.getId());
        Set<LiteYukonPAObject> allPrograms = 
            lmDao.getAllProgramsForCommercialCurtailment();
        allPrograms.removeAll(programPaosForCustomer);

        return allPrograms;
    }

    public void saveProgramList(CICustomerStub customer, Collection<LiteYukonPAObject> assignedProgramList) {
        lmDirectCustomerListDao.setLMProgramPaosForCustomer(customer.getId(), assignedProgramList);
    }

    public LMDao getLmDao() {
        return lmDao;
    }

    public void setLmDao(LMDao lmDao) {
        this.lmDao = lmDao;
    }

    public LMDirectCustomerListDao getLmDirectCustomerListDao() {
        return lmDirectCustomerListDao;
    }

    public void setLmDirectCustomerListDao(LMDirectCustomerListDao lmDirectCustomerListDao) {
        this.lmDirectCustomerListDao = lmDirectCustomerListDao;
    }

}
