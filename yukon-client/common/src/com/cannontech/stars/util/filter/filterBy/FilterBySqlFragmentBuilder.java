package com.cannontech.stars.util.filter.filterBy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy;
import com.cannontech.stars.util.filter.JoinTable;
import com.cannontech.stars.util.filter.OrderBy;
import com.cannontech.stars.util.filter.DirectionAwareOrderBy.Direction;

public class FilterBySqlFragmentBuilder {
    private String select;
    private String join;
    private String where;
    private String orderBy;
    private String direction;
    private List<Object> arguments;
    
    public FilterBySqlFragmentBuilder withSelect(String select) {
        this.select = select;
        return this;
    }
    
    public FilterBySqlFragmentBuilder withWhere(Collection<FilterBy> filterBys) {
        Map<Class<?>, Collection<FilterBy>> filterTypeMap = createFilterTypeMap(filterBys);

        SqlStatementBuilder sqlANDBuilder = new SqlStatementBuilder();
        List<Object> sqlParameterList = new ArrayList<Object>();

        boolean needsAND = false;

        for (Collection<FilterBy> types : filterTypeMap.values()) {
            SqlStatementBuilder sqlORBuilder = new SqlStatementBuilder();

            boolean needsOR = false;

            for (FilterBy filterBySql : types) {
                String whereSql = filterBySql.getSql();

                if (needsOR) {
                    sqlORBuilder.append(" OR ");
                }

                sqlORBuilder.append("(")
                .append(whereSql)
                .append(")");

                List<Object> parameterValues = filterBySql.getParameterValues();
                sqlParameterList.addAll(parameterValues);

                needsOR = true;
            }

            String whereSql = sqlORBuilder.toString();

            if (needsAND) {
                sqlANDBuilder.append(" AND ");
            }

            sqlANDBuilder.append("(")
            .append(whereSql)
            .append(")");

            needsAND = true;
        }

        this.where = sqlANDBuilder.toString();
        this.arguments = sqlParameterList;

        return this;
    }
    
    public FilterBySqlFragmentBuilder withJoin(Collection<FilterBy> filterBys) {
        Map<Class<?>, Collection<FilterBy>> filterTypeMap = createFilterTypeMap(filterBys);

        Set<JoinTable> joinTables = new LinkedHashSet<JoinTable>();

        for (Collection<FilterBy> types : filterTypeMap.values()) {
            for (FilterBy filterBySql : types) {
                addAllJoinTables(joinTables, filterBySql.getJoinTables());
            }
        }

        SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();

        for (final JoinTable joinTable : joinTables) {
            String joinSql = joinTable.getSql();
            sqlBuilder.append(joinSql);
        }

        this.join = sqlBuilder.toString();
        
        return this;
    }

    public FilterBySqlFragmentBuilder withDirectionAwareOrderBy(
            DirectionAwareOrderBy directionAwareOrderBy) {
        withOrderBy(directionAwareOrderBy.getOrderBy());
        withDirection(directionAwareOrderBy.getDirection());
        return this;
    }
    
    public FilterBySqlFragmentBuilder withDirection(Direction direction) {
        this.direction = direction.toString();
        return this;
    }
    

    public FilterBySqlFragmentBuilder withOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy.getSql();
        return this;
    }
    
    public SqlFragmentSource build() {
        final SqlStatementBuilder sqlBuilder = new SqlStatementBuilder();
        
        boolean isEmptySelectSql = StringUtils.isBlank(select);
        if (!isEmptySelectSql) {
            sqlBuilder.append(select);
        }
        
        boolean isEmptyJoinSql = StringUtils.isBlank(join);
        if (!isEmptyJoinSql) {
            sqlBuilder.append(join);
        }
        
        boolean isEmptyWhereSql = StringUtils.isBlank(where);
        if (!isEmptyWhereSql) {
            sqlBuilder.append(" WHERE ");
            sqlBuilder.append(where);
        }
        
        boolean isEmptyOrderBy = StringUtils.isBlank(orderBy);
        if (!isEmptyOrderBy) {
            sqlBuilder.append(" ORDER BY ");
            sqlBuilder.append(orderBy);
        }
        
        boolean isEmptyDirection = StringUtils.isBlank(direction);
        if (!isEmptyDirection) {
            sqlBuilder.append(" ");
            sqlBuilder.append(direction);
        }
        
        // this class doesn't take advantage of the fact that SqlStatementBuilder is a SqlFragmentSource
        SimpleSqlFragment holder = new SimpleSqlFragment() {
            @Override
            public List<Object> getArgumentList() {
                return arguments;
            }
            @Override
            public Object[] getArguments() {
                return arguments.toArray();
            }
            @Override
            public String getSql() {
                return sqlBuilder.getSql();
            }
        };
        return holder; 
    }
    
    private <R> Map<Class<?>, Collection<R>> createFilterTypeMap(Collection<R> filterBys) {
        Map<Class<?>, Collection<R>> resultMap = new HashMap<Class<?>, Collection<R>>();

        for (final R filterBy : filterBys) {
            Class<?> key = filterBy.getClass();

            Collection<R> value = resultMap.get(key);
            if (value == null) {
                value = new HashSet<R>();
                resultMap.put(key, value);
            }

            value.add(filterBy);
        }

        return resultMap;
    }
    
    private void addAllJoinTables(Collection<JoinTable> joinTables, Collection<JoinTable> filterByJoinTables) {
        for (final JoinTable filterByJoinTable : filterByJoinTables) {
            addAllJoinTables(joinTables, filterByJoinTable);
        }
    }

    private void addAllJoinTables(Collection<JoinTable> joinTables, JoinTable filterByJoinTable) {
        JoinTable dep = filterByJoinTable.getDependency();
        if (dep != null) {
            addAllJoinTables(joinTables, dep);
        }
        joinTables.add(filterByJoinTable);
    }

}
