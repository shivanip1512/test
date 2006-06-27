package com.cannontech.cc.daohibe;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.cannontech.cc.dao.ProgramParameterDao;
import com.cannontech.cc.dao.UnknownParameterException;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramParameterKey;
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
    
    public ProgramParameter getFor(Program program, ProgramParameterKey parameterKey) 
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
    
    public void deleteAllForProgram(Program program) {
        getHibernateTemplate().bulkUpdate("delete ProgramParameter pp " +
                "where pp.program = ?", program);
    }
    
    public String getParameterValue(Program program, ProgramParameterKey key) {
        String result = null;
        try {
            ProgramParameter parameter;
            parameter = getFor(program, key);
            if (!StringUtils.isBlank(parameter.getParameterValue())) {
                result = parameter.getParameterValue();
            }
        } catch (UnknownParameterException e) {
        }
        if (result == null) {
            // get default
            result = key.getDefaultValue();
        }
        return result;
    }
    
    public int getParameterValueInt(Program program, ProgramParameterKey key) {
        return Integer.parseInt(getParameterValue(program, key));
    }
    
    public float getParameterValueFloat(Program program, ProgramParameterKey key) {
        return Float.parseFloat(getParameterValue(program, key));
    }


}
