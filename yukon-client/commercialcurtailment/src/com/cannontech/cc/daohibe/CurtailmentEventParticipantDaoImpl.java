package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.CurtailmentEventParticipantDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventParticipant;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class CurtailmentEventParticipantDaoImpl extends YukonBaseHibernateDao implements
    CurtailmentEventParticipantDao {

    @SuppressWarnings("unchecked")
    public List<CurtailmentEventParticipant> getForEvent(CurtailmentEvent event) {
        String query = "select cep from CurtailmentEventParticipant cep " +
            "inner join fetch cep.customer " +
            "inner join fetch cep.event " +
            "where cep.event = ?";
        return getHibernateTemplate().find(query, event);
    }

    public void deleteForEvent(CurtailmentEvent event) {
        List<CurtailmentEventParticipant> particips = getForEvent(event);
        for (CurtailmentEventParticipant partic : particips) {
            delete(partic);
        }
    }

    public void save(CurtailmentEventParticipant object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(CurtailmentEventParticipant object) {
        getHibernateTemplate().delete(object);
    }

    public CurtailmentEventParticipant getForId(Integer id) {
        return (CurtailmentEventParticipant) 
            getHibernateTemplate().get(CurtailmentEventParticipant.class, id);
    }

}
