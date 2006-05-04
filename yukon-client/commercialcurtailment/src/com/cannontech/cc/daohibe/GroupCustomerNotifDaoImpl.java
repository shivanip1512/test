package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;

import com.cannontech.cc.dao.GroupCustomerNotifDao;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.GroupCustomerNotif;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class GroupCustomerNotifDaoImpl extends YukonBaseHibernateDao implements
        GroupCustomerNotifDao {

    @SuppressWarnings("unchecked")
    public List<GroupCustomerNotif> getAllForGroup(Group group) {
        getHibernateTemplate().lock(group, LockMode.NONE);
        return getHibernateTemplate().find("from GroupCustomerNotif notif" +
                " inner join fetch notif.customer" +
                " where notif.group = ?", group);
    }

    public void save(GroupCustomerNotif object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(GroupCustomerNotif object) {
        getHibernateTemplate().delete(object);
    }

    public GroupCustomerNotif getForId(Integer id) {
        return (GroupCustomerNotif) getHibernateTemplate().get(GroupCustomerNotif.class, id);
    }

    public void saveNotifsForGroup(Group group, List<GroupCustomerNotif> required) {
        List<GroupCustomerNotif> existing = getAllForGroup(group);
        for (GroupCustomerNotif notif : required) {
            //save(notif); // <-- the old way
            // For the first time, I'm using merge instead of save/update
            // the difference is that this won't place notif (and its object
            // graph) in the session when its done. This is useful when
            // other DAO calls that are wrapped in the same transaction
            // attempt to save/update an entity that is logicaly the same
            // as one in this notif's object graph, but is a seperate
            // physical reference.
            getHibernateTemplate().merge(notif);
            existing.remove(notif);
        }
        for (GroupCustomerNotif notif : existing) {
            delete(notif);
        }
    }

}
