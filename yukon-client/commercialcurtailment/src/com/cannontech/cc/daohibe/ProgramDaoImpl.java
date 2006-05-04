package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class ProgramDaoImpl extends YukonBaseHibernateDao implements ProgramDao {

    public ProgramDaoImpl() {
        super();
    }

    @SuppressWarnings("unchecked")
    public List<Program> getProgramsForEnergyCompany(Integer energyCompanyId) {
        String string = 
            "select p from Program p inner join fetch p.programType where energyCompanyId = ?";
        List programsToReturn = getHibernateTemplate().find(string, energyCompanyId);
        return programsToReturn;
    }

    public Program getForId(Integer id) {
        Program programToReturn = (Program)getHibernateTemplate().get(Program.class, id);
        return programToReturn;
    }

    public void save(Program object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(Program object) {
        getHibernateTemplate().delete(object);
    }

    @SuppressWarnings("unchecked")
    public List<Program> getProgramsForType(ProgramType programType) {
        return getHibernateTemplate().find("select p from Program as p where p.programType = ?", programType);
    }

}
