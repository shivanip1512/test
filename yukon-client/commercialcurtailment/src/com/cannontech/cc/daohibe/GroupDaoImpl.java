package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.GroupDao;
import com.cannontech.cc.model.Group;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class GroupDaoImpl extends YukonBaseHibernateDao implements GroupDao {
    AvailableProgramGroupDaoImpl programGroupDao;
    GroupCustomerNotifDaoImpl groupCustomerNotifDao;

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

    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(Group object) {
        getHibernateTemplate().lock(object, LockMode.NONE);
        programGroupDao.deleteFor(object);
        groupCustomerNotifDao.deleteFor(object);
        getHibernateTemplate().delete(object);
    }

    public void setGroupCustomerNotifDao(GroupCustomerNotifDaoImpl groupCustomerNotifDao) {
        this.groupCustomerNotifDao = groupCustomerNotifDao;
    }

    public void setProgramGroupDao(AvailableProgramGroupDaoImpl programGroupDao) {
        this.programGroupDao = programGroupDao;
    }

}
