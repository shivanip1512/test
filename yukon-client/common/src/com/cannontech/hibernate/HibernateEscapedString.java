package com.cannontech.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

import com.cannontech.common.util.CtiUtilities;

/**
 * Stores Strings in the Yukon standard where null types are
 * represented by the string "(none)".
 * 
 * @author Erik Visser, Chess-iT B.V.
 * @author Mika Goeckel, cyber:con gmbh
 *
 */
public class HibernateEscapedString implements UserType {

    private static final String MARK_EMPTY = CtiUtilities.STRING_NONE;
    private static final int[] TYPES = { Types.VARCHAR };

    public int[] sqlTypes() {
        return TYPES;
    }

    public Class returnedClass() {
        return String.class;
    }

    public boolean equals(Object x, Object y) {
        if (x == y)
            return true;
        if (x == null || y == null)
            return false;
        return x.equals(y);
    }

    public Object deepCopy(Object x) {
        return x;
    }

    public boolean isMutable() {
        return false;
    }

    public Object nullSafeGet(ResultSet rs, String[] names, Object owner)
        throws HibernateException, SQLException {
        String dbValue = (String) Hibernate.STRING.nullSafeGet(rs, names[0]);
        if (dbValue != null) {
            return unescape(dbValue);
        } else {
            return null;
        }
    }

    public void nullSafeSet(PreparedStatement st, Object value, int index)
        throws HibernateException, SQLException {

        if (value != null) {
            String v = escape((String) value);
            Hibernate.STRING.nullSafeSet(st, v, index);
        } else {
            Hibernate.STRING.nullSafeSet(st, value, index);
        }
    }

    public Object assemble(Serializable arg0, Object arg1) throws HibernateException {
        return deepCopy(arg0);
    }

    public Serializable disassemble(Object value) {
        return (Serializable) deepCopy(value);
    }

    /**
     * Escape a string by quoting the string.
     */
    private String escape(String string) {
        if ((string == null) || (string.length() == 0)) {
            return MARK_EMPTY;
        } else {
            return string;
        }
    }

    /**
     * Unescape by removing the quotes
     */
    private Object unescape(String string) throws HibernateException {
        if ((string == null) || (MARK_EMPTY.equals(string))) {
            return "";
        }
        return string;
    }

    /* (non-Javadoc)
     *  (at) see org (dot) hibernate.usertype.UserType#hashCode(java.lang.Object)
     */
    public int hashCode(Object arg0) throws HibernateException {
        return arg0.hashCode();
    }

    /* (non-Javadoc)
     *  (at) see org (dot) hibernate.usertype.UserType#replace(java.lang.Object,
     java.lang.Object, java.lang.Object)
     */
    public Object replace(Object arg0, Object arg1, Object arg2) throws HibernateException {
        return deepCopy(arg0);
    }
}