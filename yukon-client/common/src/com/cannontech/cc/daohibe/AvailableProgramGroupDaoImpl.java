package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;

import com.cannontech.cc.dao.AvailableProgramGroupDao;
import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class AvailableProgramGroupDaoImpl extends YukonBaseHibernateDao implements AvailableProgramGroupDao {

    public AvailableProgramGroupDaoImpl() {
        super();
    }

    public void save(AvailableProgramGroup object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(AvailableProgramGroup object) {
        getHibernateTemplate().delete(object);
    }
    
    public AvailableProgramGroup getForId(Integer id) {
        return (AvailableProgramGroup) getHibernateTemplate().get(AvailableProgramGroup.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<AvailableProgramGroup> getAllForProgram(Program program) {
        // the following is a bit odd, but it solves a problem where the AvailableProgramGroup
        // objects would refer internally to a different Program than the one passed in
        getHibernateTemplate().lock(program, LockMode.NONE);
        return getHibernateTemplate().find("from AvailableProgramGroup where program = ?", program);
    }

    public void deleteFor(Group object) {
        getHibernateTemplate().bulkUpdate("delete AvailableProgramGroup apg " +
                                          "where apg.group = ?", object);
    }

    public void deleteFor(Program object) {
        getHibernateTemplate().bulkUpdate("delete AvailableProgramGroup apg " +
                                          "where apg.program = ?", object);
    }

}
