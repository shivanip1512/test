package com.cannontech.cc.daohibe;

import java.util.Date;
import java.util.List;

import com.cannontech.cc.dao.CurtailmentEventNotifDao;
import com.cannontech.cc.model.CurtailmentEvent;
import com.cannontech.cc.model.CurtailmentEventNotif;
import com.cannontech.enums.NotificationReason;
import com.cannontech.enums.NotificationState;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class CurtailmentEventNotifDaoImpl extends YukonBaseHibernateDao
    implements CurtailmentEventNotifDao {

    public void save(CurtailmentEventNotif object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(CurtailmentEventNotif object) {
        getHibernateTemplate().delete(object);
    }

    public CurtailmentEventNotif getForId(Integer id) {
        return (CurtailmentEventNotif) getHibernateTemplate().get(CurtailmentEventNotif.class, id);
    }
    
    @SuppressWarnings("unchecked")
    public List<CurtailmentEventNotif> getForEvent(CurtailmentEvent event) {
        String query = "select cen from CurtailmentEventNotif cen " +
                "inner join fetch cen.participant p " +
                "inner join fetch p.event " +
                "where p.event = ?";
        return getHibernateTemplate().find(query, event);
    }
    
    public void deleteForEvent(CurtailmentEvent event) {
        // this is a pretty lazy approach, maybe I should just use SQL...
        List<CurtailmentEventNotif> list = getForEvent(event);
        for (CurtailmentEventNotif notif : list) {
            delete(notif);
        }
    }

    @SuppressWarnings("unchecked")
    public List<CurtailmentEventNotif> getScheduledNotifs() {
        //use my def of now in case DB time is different
        String query = "select cen from CurtailmentEventNotif cen " +
                "inner join fetch cen.participant p " +
                "inner join fetch p.event e " +
                "where cen.state = ? " +
                "  and cen.notificationTime <= ?";
        Object[] args = {NotificationState.SCHEDULED, new Date()};
        return getHibernateTemplate().find(query, args);
    }

    @SuppressWarnings("unchecked")
    public List<CurtailmentEventNotif> getForEventAndReason(CurtailmentEvent event, NotificationReason reason) {
        String query = "select cen from CurtailmentEventNotif cen " +
                "inner join fetch cen.participant p " +
                "inner join fetch p.event " +
                "where p.event = ? " +
                "  and cen.reason = ?";
        return getHibernateTemplate().find(query, new Object[]{event, reason});
    }

}
