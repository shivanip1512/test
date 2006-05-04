package com.cannontech.cc.daohibe;

import java.util.List;

import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.hibernate.YukonBaseHibernateDao;

public class ProgramParameterDaoImpl extends YukonBaseHibernateDao implements
    ProgramParameterDao {

    public void save(ProgramParameter object) {
        getHibernateTemplate().saveOrUpdate(object);
    }

    public void delete(ProgramParameter object) {
        getHibernateTemplate().delete(object);
    }

    public ProgramParameter getForId(Integer id) {
        return (ProgramParameter) getHibernateTemplate().get(ProgramParameter.class, id);
    }
    
    public ProgramParameter getFor(String parameterKey, Program program) 
    throws UnknownParameterException {
        String query = 
            "select pp from ProgramParameter pp " +
            "where pp.program = ? " +
            "and pp.parameterKey= ?";
        List list = getHibernateTemplate().find(query, new Object[] {program, parameterKey});
        if (list.size() != 1) {
            throw new UnknownParameterException(parameterKey);
        }
        return (ProgramParameter) list.get(0);
    }
    
    @SuppressWarnings("unchecked")
    public List<ProgramParameter> getAllForProgram(Program program) {
        String query = 
            "select pp from ProgramParameter pp " +
            "where pp.program = ?";
        return getHibernateTemplate().find(query, program);
    }

}
