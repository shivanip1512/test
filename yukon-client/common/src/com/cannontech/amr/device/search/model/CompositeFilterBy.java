package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

public class CompositeFilterBy implements FilterBy {
    public enum Joiner {
        AND {
            @Override
            public SqlFragmentCollection getNewSqlFragmentCollection() {
                return SqlFragmentCollection.newAndCollection();
            }
        },
        OR{
            @Override
            public SqlFragmentCollection getNewSqlFragmentCollection() {
                return SqlFragmentCollection.newOrCollection();
            }
        };
        
        public abstract SqlFragmentCollection getNewSqlFragmentCollection();
    }

    private FilterBy [] filters;
    private Joiner joiner;

    public CompositeFilterBy(Joiner joiner, FilterBy... filters) {
        this.joiner = joiner;
        this.filters = filters;
    }

    @Override
    public boolean hasValue() {
        for(FilterBy filterBy : filters) {
            if(!filterBy.hasValue()) {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public SqlFragmentSource getWhereClauseFragment() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        SqlFragmentCollection whereClause = joiner.getNewSqlFragmentCollection();
        for(int i = 0; i < filters.length; i++) {
            whereClause.add(filters[i].getWhereClauseFragment());
        }
        
        sql.append("(").append(whereClause).append(")");
        
        return sql;
    }

    @Override
    public String toSearchString() {
        StringBuilder stringBuilder = new StringBuilder();
        
        stringBuilder.append("(");
        for(int i = 0; i < filters.length; i++) {
            stringBuilder.append(filters[i].toSearchString());
            if(i < filters.length - 1) {
                stringBuilder.append(" ").append(joiner.name()).append(" ");
            }
        }
        stringBuilder.append(")");
        
        return stringBuilder.toString();
    }

    public static FilterBy and(FilterBy... filters) {
        return new CompositeFilterBy(Joiner.AND, filters);
    }

    public static FilterBy or(FilterBy... filters) {
        return new CompositeFilterBy(Joiner.OR, filters);
    }
}
