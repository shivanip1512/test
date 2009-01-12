package com.cannontech.cc.daohibe;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventDaoImpl extends YukonBaseHibernateDao implements
    EconomicEventDao {
    private EconomicEventParticipantDao economicEventParticipantDao;

    public EconomicEventDaoImpl() {
        super();
    }

    public void save(EconomicEvent object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEvent object) {
        economicEventParticipantDao.deleteForEvent(object);
        getHibernateTemplate().delete(object);
    }

    public EconomicEvent getForId(Integer id) {
        return (EconomicEvent) getHibernateTemplate().get(EconomicEvent.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<EconomicEvent> getAllForProgram(Program program) {
        String query = 
            "from EconomicEvent ce where ce.program = ?";
        return getHibernateTemplate().find(query, program);
    }
    
    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        String query = 
            "select ee from EconomicEvent ee " +
            "where ee.program.programType.energyCompanyId = ?";
        return getHibernateTemplate().find(query, energyCompany.getEnergyCompanyID());
    }

    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer) {
        String query = 
            "select eep.event from EconomicEventParticipant eep " +
            "where eep.customer = ?";
        return getHibernateTemplate().find(query, customer);
    }

    public EconomicEvent getChildEvent(EconomicEvent event) {
        String query = 
            "select ee from EconomicEvent ee " +
            "where ee.initialEvent = ?";
        List results = getHibernateTemplate().find(query, event);
        if (results.size() > 1) {
            throw new IncorrectResultSizeDataAccessException(1, results.size());
        }
        if (results.size() == 0) {
            return null;
        }
        return (EconomicEvent) results.get(0);
    }
    
    @Required
    public void setEconomicEventParticipantDao(
            EconomicEventParticipantDao economicEventParticipantDao) {
        this.economicEventParticipantDao = economicEventParticipantDao;
    }

}
