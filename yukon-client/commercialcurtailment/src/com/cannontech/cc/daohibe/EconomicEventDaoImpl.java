package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.EconomicEventDao;
import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.Program;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventDaoImpl extends YukonBaseHibernateDao implements
    EconomicEventDao {

    public EconomicEventDaoImpl() {
        super();
    }

    public void save(EconomicEvent object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEvent object) {
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

}
