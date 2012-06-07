package com.cannontech.stars.util.filter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.processor.CollectionProcessor;
import com.cannontech.common.bulk.processor.ProcessingException;
import com.cannontech.common.bulk.processor.Processor;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.util.MappingList;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.core.dao.PersistenceException;
import com.cannontech.database.IntegerRowMapper;
import com.cannontech.database.data.lite.LiteBase;
import com.cannontech.stars.util.DateRangeFilterWrapper;
import com.cannontech.stars.util.FilterWrapper;
import com.cannontech.stars.util.filter.filterBy.FilterBy;
import com.cannontech.stars.util.filter.filterBy.FilterByFactory;
import com.cannontech.stars.util.filter.filterBy.FilterBySqlFragmentBuilder;

public abstract class AbstractFilter<E extends LiteBase> implements Filter<E> {
    private final ParameterizedRowMapper<Integer> idRowMapper = new IntegerRowMapper();
    private FilterByFactory filterByFactory;
    private JdbcTemplate jdbcTemplate;
    private int batchSize;
    
    protected abstract String getSelectCountSql();
    
    protected abstract String getSelectIdSql();

    protected abstract List<E> getList(List<Integer> ids);
    
    @Override
    @Transactional(readOnly = true)
    public int getFilterCount(List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
            Date startDate, Date stopDate) throws PersistenceException {
        
        //Add Dates into any WorkOrderStatus FilterWrapper if they exist.
        addDateRange(filterWrapperList, startDate, stopDate);
        
        return getFilterCount(energyCompanyIdList, filterWrapperList);
    }
    
    @Override
    @Transactional(readOnly = true)
    public int getFilterCount(List<Integer> energyCompanyIdList, 
            List<FilterWrapper> filterWrapperList) throws PersistenceException {

        Collection<FilterBy> filterBys = filterByFactory.createFilterBys(filterWrapperList);
        applyFilterBySpecificSettings(energyCompanyIdList, filterBys);
        
        SqlFragmentSource sqlFragmentHolder = new FilterBySqlFragmentBuilder()
            .withSelect(getSelectCountSql())
            .withJoin(filterBys)
            .withWhere(filterBys)
            .build();
        
        String sql = sqlFragmentHolder.getSql();
        Object[] args = sqlFragmentHolder.getArguments();
        int count = 0;
        try {
            count = jdbcTemplate.queryForInt(sql, args);
        // Oracle seems to throw SQLSyntaxErrorException whereas MS-SQL throws SQLException, 
        // that is the root of discrepancy in the Spring SQL error exception translator to throw
        // BadSqlGrammarException vs. DataIntegrityViolationException            
        } catch (BadSqlGrammarException e){
            throw new PersistenceException(e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
        return count;
    }
    
    @Override
    @Transactional(readOnly = true)
    public void filter(final int fromIndex, final int toIndex, final Processor<E> processor, 
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy, Date startDate, Date stopDate) throws PersistenceException {
        
        //Add Dates into any WorkOrderStatus FilterWrapper if they exist.
        addDateRange(filterWrapperList, startDate, stopDate);

        filter(fromIndex, toIndex, processor, energyCompanyIdList, filterWrapperList, orderBy);
    }   
    
    @Override
    @Transactional(readOnly = true)
    public void filter(final int fromIndex, final int toIndex, final Processor<E> processor, 
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy) throws PersistenceException {
        
        filterForInteger(fromIndex, toIndex, new CollectionProcessor<Integer>() {
            @Override
            public void process(Collection<Integer> objectCollection) throws ProcessingException {
                List<E> list = getList(new ArrayList<Integer>(objectCollection));
                processor.process(list);
            }
        }, energyCompanyIdList, filterWrapperList, orderBy);
    }

    private void filterForInteger(final int fromIndex, final int toIndex, final Processor<Integer> processor, 
            List<Integer> energyCompanyIdList, List<FilterWrapper> filterWrapperList,
                DirectionAwareOrderBy orderBy) throws PersistenceException {
        
        Collection<FilterBy> filterBys = filterByFactory.createFilterBys(filterWrapperList);
        applyFilterBySpecificSettings(energyCompanyIdList, filterBys);
        
        SqlFragmentSource sqlFragmentHolder = new FilterBySqlFragmentBuilder()
            .withSelect(getSelectIdSql())
            .withJoin(filterBys)
            .withWhere(filterBys)
            .withDirectionAwareOrderBy(orderBy)
            .build();

        String sql = sqlFragmentHolder.getSql();
        Object[] args = sqlFragmentHolder.getArguments();
        
        try {
            jdbcTemplate.query(sql, args, new ResultSetExtractor() {
                @Override
                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {

                    List<Integer> list = new ArrayList<Integer>(batchSize);

                    // Adjusted to zero-based counting to work similar to SearchBasedSimpleCollection
                    int rowNum = -1;
                    while (rs.next()) {
                        rowNum++;

                        if (rowNum < fromIndex) continue;
                        if (rowNum >= toIndex) break;

                        Integer id = idRowMapper.mapRow(rs, rowNum);
                        list.add(id);

                        if (list.size() >= batchSize) {
                            processor.process(list);
                            list = new ArrayList<Integer>(batchSize);
                        }
                    }

                    if (list.size() > 0) {
                        processor.process(list);
                    }

                    return null;
                }
            });
            // Oracle seems to throw SQLSyntaxErrorException whereas MS-SQL throws SQLException, 
            // that is the root of discrepancy in the Spring SQL error exception translator to throw
            // BadSqlGrammarException vs. DataIntegrityViolationException            
        } catch (BadSqlGrammarException e){
            throw new PersistenceException(e.getMessage(), e);
        } catch (DataIntegrityViolationException e) {
            throw new PersistenceException(e.getMessage(), e);
        }
    }
    
    protected void applyFilterBySpecificSettings(List<Integer> energyCompanyIdList, 
            Collection<FilterBy> filterBys) {
        // Can be overridden to add more interesting Filter specific FilterBy's.
    }
    
    private void addDateRange(List<FilterWrapper> filterWrapperList, 
            Date startDate, Date stopDate) {

        Date updatedStartDate = (startDate != null) ? startDate : new Date(0);
        Date updatedStopDate = (stopDate != null) ? stopDate : new Date();
        
        List<FilterWrapper> needsUpdatingList = new ArrayList<FilterWrapper>(2);
        
        for (final FilterWrapper filterWrapper : filterWrapperList) {
            if (Integer.parseInt(filterWrapper.getFilterTypeID()) == YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS) {
                needsUpdatingList.add(filterWrapper);
            }
        }
        
        filterWrapperList.removeAll(needsUpdatingList);
        
        for (final FilterWrapper filterWrapper : needsUpdatingList) {
            DateRangeFilterWrapper dateRangeFilterWrapper = new DateRangeFilterWrapper(filterWrapper);
            dateRangeFilterWrapper.setStartDate(updatedStartDate);
            dateRangeFilterWrapper.setStopDate(updatedStopDate);
            filterWrapperList.add(dateRangeFilterWrapper);
        }
    }
    
    protected <K> List<E> createSortedList(final List<K> sortedIds, final Map<K,E> map) {
        return new MappingList<K,E>(sortedIds, new ObjectMapper<K,E>() {
            @Override
            public E map(K from) throws ObjectMappingException {
                return map.get(from);
            }
        });
    }
    
    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Autowired
    public void setFilterByFactory(FilterByFactory filterByFactory) {
        this.filterByFactory = filterByFactory;
    }

}
