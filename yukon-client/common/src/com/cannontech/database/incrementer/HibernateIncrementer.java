package com.cannontech.database.incrementer;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.type.Type;

import com.cannontech.spring.YukonSpringHook;

public class HibernateIncrementer extends Object implements IdentifierGenerator {

    private NextValueHelper nextValueHelper = null;

    public HibernateIncrementer() {
    }

    public Serializable generate(SessionImplementor session, Object object)
            throws HibernateException {
        if (nextValueHelper == null) {
            nextValueHelper = (NextValueHelper) YukonSpringHook.getBean("nextValueHelper");
        }
        return nextValueHelper.getNextValue(object);
    }

}
