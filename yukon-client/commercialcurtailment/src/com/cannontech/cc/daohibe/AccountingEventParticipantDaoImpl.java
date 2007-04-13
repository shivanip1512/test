package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;

import com.cannontech.cc.dao.AccountingEventParticipantDao;
import com.cannontech.cc.model.AccountingEvent;
import com.cannontech.cc.model.AccountingEventParticipant;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class AccountingEventParticipantDaoImpl extends YukonBaseHibernateDao implements
    AccountingEventParticipantDao {

    @SuppressWarnings("unchecked")
    public List<AccountingEventParticipant> getForEvent(AccountingEvent event) {
        getHibernateTemplate().lock(event, LockMode.NONE);
        String query = "select aep from AccountingEventParticipant aep " +
            "inner join fetch aep.customer " +
            "inner join fetch aep.event " +
            "where aep.event = ?";
        return getHibernateTemplate().find(query, event);
    }

    public void deleteForEvent(AccountingEvent event) {
        getHibernateTemplate().lock(event, LockMode.NONE);
        List<AccountingEventParticipant> forEvent = getForEvent(event);
        getHibernateTemplate().deleteAll(forEvent);
    }

    public void save(AccountingEventParticipant object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(AccountingEventParticipant participant) {
        getHibernateTemplate().delete(participant);
    }

    public AccountingEventParticipant getForId(Integer id) {
        return (AccountingEventParticipant) 
            getHibernateTemplate().get(AccountingEventParticipant.class, id);
    }
    
}
