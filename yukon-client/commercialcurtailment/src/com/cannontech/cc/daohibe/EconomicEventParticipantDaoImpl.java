package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import com.cannontech.cc.dao.EconomicEventParticipantDao;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventParticipantDaoImpl extends YukonBaseHibernateDao implements
    EconomicEventParticipantDao {

    @SuppressWarnings("unchecked")
    public List<EconomicEventParticipant> getForEvent(EconomicEvent event) {
        String query = "select eep from EconomicEventParticipant eep " +
            "inner join fetch eep.customer " +
            "inner join fetch eep.event " +
            "where eep.event = ?";
        return getHibernateTemplate().find(query, event);
    }
    
    public EconomicEventParticipant getForCustomerAndEvent(CICustomerStub customer, EconomicEvent event) {
        String query = "select eep from EconomicEventParticipant eep " +
            "where eep.event = ? and eep.customer = ?";
        Object[] args = new Object[] {event, customer};
        List find = getHibernateTemplate().find(query, args);
        if (find.size() != 1) {
            throw new IncorrectResultSizeDataAccessException("Unable to get EconomicEventParticipant " +
                    "for customer and event", 1, find.size());
        }
        return (EconomicEventParticipant) find.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public List<EconomicEventParticipant> getForCustomer(CICustomerStub customer) {
        String query = "select eep from EconomicEventParticipant eep " +
        "where eep.customer = ?";
        return getHibernateTemplate().find(query, customer);
    }

    public void deleteForEvent(EconomicEvent event) {
        getHibernateTemplate().lock(event, LockMode.NONE);
        List<EconomicEventParticipant> particips = getForEvent(event);
        for (EconomicEventParticipant partic : particips) {
            delete(partic);
        }
    }

    public void save(EconomicEventParticipant object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEventParticipant object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventParticipant getForId(Integer id) {
        return (EconomicEventParticipant) 
            getHibernateTemplate().get(EconomicEventParticipant.class, id);
    }

}
