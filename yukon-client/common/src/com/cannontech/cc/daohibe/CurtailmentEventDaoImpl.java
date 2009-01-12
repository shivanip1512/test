package com.cannontech.cc.daohibe;

import java.util.List;

import org.springframework.beans.factory.annotation.Required;

import com.cannontech.cc.dao.CurtailmentEventDao;
import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class CurtailmentEventDaoImpl extends YukonBaseHibernateDao implements
    CurtailmentEventDao {
    private CurtailmentEventParticipantDao curtailmentEventParticipantDao;

    public CurtailmentEventDaoImpl() {
        super();
    }

    public void save(CurtailmentEvent object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(CurtailmentEvent object) {
        curtailmentEventParticipantDao.deleteForEvent(object);
        getHibernateTemplate().delete(object);
    }

    public CurtailmentEvent getForId(Integer id) {
        return (CurtailmentEvent) getHibernateTemplate().get(CurtailmentEvent.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<CurtailmentEvent> getAllForProgram(Program program) {
        String query = 
            "from CurtailmentEvent ce where ce.program = ?";
        return getHibernateTemplate().find(query, program);
    }
    
    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForEnergyCompany(LiteEnergyCompany energyCompany) {
        String query = 
            "select ce from CurtailmentEvent ce " +
            "where ce.program.programType.energyCompanyId = ?";
        return getHibernateTemplate().find(query, energyCompany.getEnergyCompanyID());
    }

    @SuppressWarnings("unchecked")
    public List<BaseEvent> getAllForCustomer(CICustomerStub customer) {
        String query = 
            "select cep.event from CurtailmentEventParticipant cep " +
            "where cep.customer = ?";
        return getHibernateTemplate().find(query, customer);
    }

    @Required
    public void setCurtailmentEventParticipantDao(
            CurtailmentEventParticipantDao curtailmentEventParticipantDao) {
        this.curtailmentEventParticipantDao = curtailmentEventParticipantDao;
    }

}
