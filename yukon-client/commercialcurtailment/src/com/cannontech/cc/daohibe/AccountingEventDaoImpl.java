package com.cannontech.cc.daohibe;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.cc.dao.AccountingEventDao;
import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class AccountingEventDaoImpl extends YukonBaseHibernateDao implements
    AccountingEventDao {
    private AccountingEventParticipantDao accountingEventParticipantDao;

    public AccountingEventDaoImpl() {
        super();
    }

    public void save(AccountingEvent object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(AccountingEvent event) {
        accountingEventParticipantDao.deleteForEvent(event);
        getHibernateTemplate().delete(event);
    }

    public AccountingEvent getForId(Integer id) {
        return (AccountingEvent) getHibernateTemplate().get(AccountingEvent.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<AccountingEvent> getAllForProgram(Program program) {
        String query = 
            "from AccountingEvent ae where ae.program = ?";
        return getHibernateTemplate().find(query, program);
    }
    
    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        String query = 
            "select ae from AccountingEvent ae " +
            "where ae.program.programType.energyCompanyId = ?";
        return getHibernateTemplate().find(query, energyCompany.getEnergyCompanyID());
    }

    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer) {
        String query = 
            "select aep.event from AccountingEventParticipant aep " +
            "where aep.customer = ?";
        return getHibernateTemplate().find(query, customer);
    }
    
    @Required
    public void setAccountingEventParticipantDao(AccountingEventParticipantDao accountingEventParticipantDao) {
        this.accountingEventParticipantDao = accountingEventParticipantDao;
    }

}
