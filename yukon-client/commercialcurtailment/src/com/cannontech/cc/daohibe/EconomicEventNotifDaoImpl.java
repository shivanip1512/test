package com.cannontech.cc.daohibe;

import java.util.Date;
import java.util.List;

import com.cannontech.cc.dao.EconomicEventNotifDao;
import com.cannontech.cc.model.EconomicEvent;
import com.cannontech.cc.model.EconomicEventNotif;
import com.cannontech.cc.model.EconomicEventParticipant;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class EconomicEventNotifDaoImpl extends YukonBaseHibernateDao
    implements EconomicEventNotifDao {

    public void save(EconomicEventNotif object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(EconomicEventNotif object) {
        getHibernateTemplate().delete(object);
    }

    public EconomicEventNotif getForId(Integer id) {
        return (EconomicEventNotif) getHibernateTemplate().get(EconomicEventNotif.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<EconomicEventNotif> getForEvent(EconomicEvent event) {
        String query = "select cen from EconomicEventNotif cen " +
                "inner join fetch cen.participant p " +
                "inner join fetch p.event " +
                "where p.event = ?";
        return getHibernateTemplate().find(query, event);
    }
    
    public void deleteForEvent(EconomicEvent event) {
        // this is a pretty lazy approach, maybe I should just use SQL...
        List<EconomicEventNotif> list = getForEvent(event);
        for (EconomicEventNotif notif : list) {
            delete(notif);
        }
    }

    public void deleteForParticipant(EconomicEventParticipant participant) {
        String query = "delete from EconomicEventNotif een " +
            "where een.participant = ? ";
        getHibernateTemplate().bulkUpdate(query, participant);
    }
    
    @SuppressWarnings("unchecked")
    public List<EconomicEventNotif> getScheduledNotifs() {
        //use my def of now in case DB time is different
        String query = "select cen from EconomicEventNotif cen " +
                "inner join fetch cen.participant p " +
                "inner join fetch p.event e " +
                "where cen.state = ? " +
                "  and cen.notificationTime <= ?";
        Object[] args = {NotificationState.SCHEDULED, new Date()};
        return getHibernateTemplate().find(query, args);
    }

    @SuppressWarnings("unchecked")
    public List<EconomicEventNotif> getForEventAndReason(EconomicEvent event, NotificationReason reason) {
        String query = "select een from EconomicEventNotif een " +
                "inner join fetch een.participant p " +
                "inner join fetch p.event " +
                "where p.event = ? " +
                "  and een.reason = ?";
        return getHibernateTemplate().find(query, new Object[]{event, reason});
    }

    @SuppressWarnings("unchecked")
    public List<EconomicEventNotif> getForParticipant(EconomicEventParticipant participant) {
        String query = "select een from EconomicEventNotif een " +
            "where een.participant = ? ";
        return getHibernateTemplate().find(query, participant);
    }

}
