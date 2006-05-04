package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.ProgramTypeDao;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class ProgramTypeDaoImpl extends YukonBaseHibernateDao implements
        ProgramTypeDao {

    public ProgramTypeDaoImpl() {
        super();
    }

    @SuppressWarnings("unchecked")
    public List<ProgramType> getAllProgramTypes(Integer energyCompanyId) {
        return getHibernateTemplate().find("from ProgramType where energyCompanyId = ?", energyCompanyId);
    }

    public ProgramType getForId(Integer id) {
        return (ProgramType) getHibernateTemplate().get(ProgramType.class, id);
    }

    public void save(ProgramType object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(ProgramType object) {
        getHibernateTemplate().delete(object);
    }

}
