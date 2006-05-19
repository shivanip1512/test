package com.cannontech.cc.daohibe;

import java.util.List;

import org.hibernate.LockMode;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.cc.dao.ProgramDao;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class ProgramDaoImpl extends YukonBaseHibernateDao implements ProgramDao {
    ProgramParameterDaoImpl programParameterDao;
    AvailableProgramGroupDaoImpl programGroupDao;

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

    @Transactional(propagation=Propagation.MANDATORY)
    public void delete(Program object) {
        getHibernateTemplate().lock(object, LockMode.NONE);
        programGroupDao.deleteFor(object);
        programParameterDao.deleteAllForProgram(object);
        getHibernateTemplate().delete(object);
    }

    @SuppressWarnings("unchecked")
    public List<Program> getProgramsForType(ProgramType programType) {
        return getHibernateTemplate().find("select p from Program as p where p.programType = ?", programType);
    }

    public void setProgramGroupDao(AvailableProgramGroupDaoImpl programGroupDao) {
        this.programGroupDao = programGroupDao;
    }

    public void setProgramParameterDao(ProgramParameterDaoImpl programParameterDao) {
        this.programParameterDao = programParameterDao;
    }

}
