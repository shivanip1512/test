package com.cannontech.amr.device.search.model;

import com.cannontech.common.util.SqlFragmentCollection;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;

/**
 * This class should be used to make groups of filters.
 * 
 * For example, if you want something like:
 * 
 * WHERE (itemA AND (ItemB OR itemC))
 * 
 * you could use:
 * 
 * CompositeFilterBy.and(
 *      FilterBy(something for itemA),
 *      CompositeFilterBy.or(
 *          FilterBy(something for itemB),
 *          FilterBy(something for itemC)
 * );
 * 
 * @author macourtois
 *
 */
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

    /**
     * Create a composite of filters
     * 
     * @param joiner OR or AND joiner for the filters
     * @param filters list of filters to join
     */
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

    /**
     * Utility method to create an AND composite filter
     * 
     * @param filters the list of filters to join
     * @return the composite filter
     */
    public static FilterBy and(FilterBy... filters) {
        return new CompositeFilterBy(Joiner.AND, filters);
    }

    /**
     * Utility method to create an OR composite filter
     * 
     * @param filters the list of filters to join
     * @return the composite filter
     */
    public static FilterBy or(FilterBy... filters) {
        return new CompositeFilterBy(Joiner.OR, filters);
    }
}
