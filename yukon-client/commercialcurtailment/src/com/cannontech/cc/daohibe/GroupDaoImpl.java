package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.Group;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class GroupDaoImpl extends YukonBaseHibernateDao implements GroupDao {

    public GroupDaoImpl() {
        super();
    }

    @SuppressWarnings("unchecked")
    public List<Group> getGroupsForEnergyCompany(int energyCompanyId) {
        return getHibernateTemplate().find("from Group where energyCompanyId = ?", energyCompanyId);
    }

    public Group getForId(Integer id) {
        return (Group) getHibernateTemplate().get(Group.class, id);
    }

    public void save(Group object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(Group object) {
        getHibernateTemplate().delete(object);
    }

}
