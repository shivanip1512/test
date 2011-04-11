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
