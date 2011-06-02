package com.cannontech.common.util;

import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

public class SqlFragmentCollection implements SqlFragmentSource {
    private String joiner;
    private List<String> sql = Lists.newArrayList();
    private List<Object> arguments = Lists.newArrayList();
    private final boolean wrap;
    
    public static SqlFragmentCollection newAndCollection() {
        return new SqlFragmentCollection(" AND ", true);
    }
    
    public static SqlFragmentCollection newOrCollection() {
        return new SqlFragmentCollection(" OR ", true);
    }
    
    /**
     * Create a SqlFragmentCollection with any joiner string. Normally the newAndCollection
     * and newOrCollection will be used, but this is available for advanced uses.
     * 
     * If one exactly one fragment is added to the collection, it will simply be returned as is.
     * If two or more fragments are added to the collection, they will be wrapped in parentheses
     * if the wrap parameter is set. This is useful for boolean logic clauses where the joiner is 
     * " AND ", but isn't desirable for comma-separated lists (like a UPDATE clause) where
     * the joiner is ", ".
     * 
     * @param joiner the string to place between each fragment, spaces are not added automatically
     * @param wrap controls whether output may be wrapped in parentheses
     */
    public SqlFragmentCollection(String joiner, boolean wrap) {
        this.joiner = joiner;
        this.wrap = wrap;
    }

    public List<Object> getArgumentList() {
        return Collections.unmodifiableList(arguments);
    }
    
    @Override
    public Object[] getArguments() {
        return arguments.toArray();
    }

    @Override
    public String getSql() {
        String result = StringUtils.join(sql, joiner);
        if (wrap && sql.size() > 1) {
            result = "(" + result + ")";
        }
        return result;
    }
    
    @Override
    public String toString() {
        return joiner + sql;
    }
    
    public SqlFragmentCollection add(SqlFragmentSource fragment) {
        sql.add(fragment.getSql());
        arguments.addAll(fragment.getArgumentList());
        return this;
    }

    public SqlFragmentCollection addSimpleFragment(String fragment) {
        sql.add(fragment);
        return this;
    }

    public boolean isEmpty() {
        return sql.isEmpty();
    }
    
}
