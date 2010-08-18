package com.cannontech.common.events.service.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.log4j.Logger;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.bulk.filter.SqlFragmentUiFilter;
import com.cannontech.common.bulk.filter.UiFilter;
import com.cannontech.common.bulk.filter.service.FilterService;
import com.cannontech.common.bulk.filter.service.UiFilterList;
import com.cannontech.common.bulk.mapper.ObjectMappingException;
import com.cannontech.common.bulk.mapper.PassThroughMapper;
import com.cannontech.common.config.ConfigurationSource;
import com.cannontech.common.device.DeviceRequestType;
import com.cannontech.common.events.Arg;
import com.cannontech.common.events.YukonEventLog;
import com.cannontech.common.events.dao.EventLogDao;
import com.cannontech.common.events.loggers.ArgEnum;
import com.cannontech.common.events.model.ArgumentColumn;
import com.cannontech.common.events.model.EventCategory;
import com.cannontech.common.events.model.EventLog;
import com.cannontech.common.events.model.EventParameter;
import com.cannontech.common.events.model.MappedEventLog;
import com.cannontech.common.events.service.EventLogService;
import com.cannontech.common.events.service.mappers.LiteYukonUserToNameMapper;
import com.cannontech.common.exception.BadConfigurationException;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.search.SearchResult;
import com.cannontech.common.util.ObjectMapper;
import com.cannontech.common.util.SqlBuilder;
import com.cannontech.core.service.DateFormattingService;
import com.cannontech.core.service.DateFormattingService.DateFormatEnum;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.point.PointType;
import com.cannontech.user.YukonUserContext;
import com.google.common.base.Function;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableList.Builder;

@Service
public class EventLogServiceImpl implements EventLogService {
    private static final Logger log = YukonLogManager.getLogger(EventLogServiceImpl.class);

    private ConfigurationSource configurationSource;
    private DateFormattingService dateFormattingService;
    private EventLogDao eventLogDao;
    private FilterService filterService;

    private Map<Method, MethodLogDetail> methodLogDetailLookup = Maps.newHashMap();
    private Map<String, MethodLogDetail> methodLogDetailCatalog = Maps.newHashMap();

    private ImmutableList<String> excludedEventLogPaths; 
    
    @PostConstruct
    public void setupExcludedEventLogPaths() {
        // Gets the value from the cparm if it exists
        String excludedEventLogPathsStr = configurationSource.getString("EVENT_LOG_EXCLUSION_LIST");

        // Builds up the list of excluded event log paths.
        Builder<String> excludedEventLogPathsBuilder = ImmutableList.builder();
        if (excludedEventLogPathsStr != null) {
            String[] excludedEventLogPathArray = StringUtils.split(excludedEventLogPathsStr, ",");

            for (String excludedEventLogPathStr : excludedEventLogPathArray) {
                excludedEventLogPathsBuilder.add(excludedEventLogPathStr.trim());
            }
        }
        
        excludedEventLogPaths = excludedEventLogPathsBuilder.build();
    }
    
    private static class ArgumentMapper<T> {
        public static <TT> ArgumentMapper<TT> create(Class<TT> javaType, int sqlType) {
            return new ArgumentMapper<TT>(javaType, sqlType, new PassThroughMapper<TT>());
        }
        
        public static <TT> ArgumentMapper<TT> create(Class<TT> javaType, int sqlType, ObjectMapper<? super TT, ? extends Object> objectMapper) {
            return new ArgumentMapper<TT>(javaType, sqlType, objectMapper);
        }
        
        public static <TT extends Enum<TT>> ArgumentMapper<TT> createForEnum(Class<TT> javaType) {
            return new ArgumentMapper<TT>(javaType, Types.VARCHAR, new ObjectMapper<TT, String>() {
                
                public String map(TT from) throws ObjectMappingException {
                    return from.name();
                }
            });
        }
        
        private ArgumentMapper(Class<T> javaType, int sqlType, ObjectMapper<? super T, ? extends Object> objectMapper) {
            this.javaType = javaType;
            this.sqlType = sqlType;
            this.objectMapper = objectMapper;
        }
        
        public final Class<T> javaType;
        public final int sqlType;
        public final ObjectMapper<? super T, ?> objectMapper;
    }
    
    private List<ArgumentMapper<?>> argumentMappers;
    {
        // This area defines what arguments can be used in the interface methods.
        // The argumentMappers list is processed in order, by doing an instanceof
        // check on the Java type specified (primitive types are handled by their
        // wrapper class. When a match is found a column is searched
        // for that exactly matches the SQL type specified (so, adding a Types.REAL, won't
        // do anything unless a Types.REAL has been added as one of the database columns).
        // Refer to the EventLogDaoImpl to see which columns are actually supported.
        Builder<ArgumentMapper<?>> builder = ImmutableList.builder();
        builder.add(ArgumentMapper.create(Number.class, Types.NUMERIC));
        builder.add(ArgumentMapper.create(String.class, Types.VARCHAR));
        builder.add(ArgumentMapper.create(Boolean.class, Types.VARCHAR));
        builder.add(ArgumentMapper.create(Date.class, Types.TIMESTAMP));
        builder.add(ArgumentMapper.create(LiteYukonUser.class, Types.VARCHAR, new LiteYukonUserToNameMapper()));
        builder.add(ArgumentMapper.createForEnum(PaoType.class));
        builder.add(ArgumentMapper.createForEnum(PointType.class));
        builder.add(ArgumentMapper.createForEnum(DeviceRequestType.class));
        builder.add(ArgumentMapper.create(ReadableInstant.class, Types.TIMESTAMP, new ObjectMapper<ReadableInstant, Date>() {
            public Date map(ReadableInstant from) throws ObjectMappingException {
                return new Instant(from).toDate();
            }
        }));
        argumentMappers = builder.build();
    }

    @Override
    public void setupLoggerForMethod(final Method method) throws BadConfigurationException {
        MethodLogDetail methodLogDetail = new MethodLogDetail();
        
        YukonEventLog annotation = AnnotationUtils.getAnnotation(method, YukonEventLog.class);

        methodLogDetail.setTransactionality(annotation.transactionality());
        
        String categoryStr = annotation.category();
        if (categoryStr == null) {
            throw new BadConfigurationException("Could not find EventCategory for: " + method);
        }
        
        EventCategory category = EventCategory.createCategory(categoryStr);
        methodLogDetail.setEventCategory(category);
        methodLogDetail.setMethodName(method.getName());

        final Class<?>[] parameterTypes = method.getParameterTypes();
        
        final List<ArgumentColumn> argumentColumns = eventLogDao.getArgumentColumns();
        
        ListMultimap<Integer,ArgumentColumn> availableArguments = ArrayListMultimap.create();
        for (ArgumentColumn argumentColumn : argumentColumns) {
            availableArguments.put(argumentColumn.getSqlType(), argumentColumn);
        }
        final List<ArgumentColumn> choosenColumns = Lists.newArrayListWithExpectedSize(parameterTypes.length);
        final List<ArgumentMapper<?>> choosenMappers = Lists.newArrayListWithExpectedSize(parameterTypes.length);
        
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> parameterType = parameterTypes[i];
            Annotation[] parameterTypeAnnotations = parameterAnnotations[i];
        
            // Check for Arg annotation and set the parameter name if it exists
            ArgEnum parameterName = null;
            for (Annotation parameterAnnotation : parameterTypeAnnotations) {
                if (parameterAnnotation instanceof Arg) {
                    Arg argAnnotation = (Arg) parameterAnnotation;
                    parameterName = argAnnotation.value();
                }
            }

            boolean foundArgumentColumn = false;
            Class<?> parameterTypeToCompare = parameterType;
            if (parameterType.isPrimitive()) {
                // treat primitives as wrappers
                parameterTypeToCompare = ClassUtils.primitiveToWrapper(parameterType);
            }
            for (ArgumentMapper<?> argumentMapper : argumentMappers) {
                if (argumentMapper.javaType.isAssignableFrom(parameterTypeToCompare)) {
                    // this mapper handles the declared argument, now check if there are 
                    // columns left to store it
                    List<ArgumentColumn> availableArgumentsForType = availableArguments.get(argumentMapper.sqlType);
                    if (availableArgumentsForType.isEmpty()) continue;
                    
                    // remove column from available list
                    ArgumentColumn argumentColumn = availableArgumentsForType.remove(0);
                    choosenColumns.add(argumentColumn);
                    choosenMappers.add(argumentMapper);
                    
                    // Get the parameter number from the list of argument columns 
                    int paramNumber = 0;
                    for (;paramNumber < argumentColumns.size(); paramNumber++) {
                        if (argumentColumns.get(paramNumber).equals(argumentColumn)) {
                            paramNumber++;
                            break;
                        }
                    }
                    
                    EventParameter eventParameter;
                    if (parameterName != null) {
                        eventParameter = EventParameter.createNamed(parameterName.toString(), argumentMapper.javaType);
                    } else {
                        eventParameter = EventParameter.createDefault(paramNumber, argumentMapper.javaType);
                    }
                    methodLogDetail.addColomnAndParameterMapping(argumentColumn, eventParameter);
                    
                    foundArgumentColumn = true;
                    break;
                }
            }
            if (!foundArgumentColumn) {
                throw new BadConfigurationException("Unable to map event log method to database: " + method);
            }
        }
        
        // Create the mapper that will be used to convert the method arguments
        // to an array of arguments for the SQL, this mapper will get called
        // each time the method is invoked.
        // In the following, the length of the first array will equal the number of arguments in the method
        // and the length of the second array will equal the number of ArgumentColumns defined for the system.
        ObjectMapper<Object[], Object[]> argumentValueMapper = new ObjectMapper<Object[], Object[]>() {
            @Override
            public Object[] map(Object[] methodArguments) throws ObjectMappingException {
                Map<ArgumentColumn, Object> columnToValue = Maps.newHashMapWithExpectedSize(methodArguments.length);
                
                for (int i = 0; i < methodArguments.length; i++) {
                    Object methodArgumentValue = methodArguments[i];
                    ArgumentMapper<?> argumentMapper = choosenMappers.get(i);
                    ArgumentColumn dbColumn = choosenColumns.get(i);
                    
                    Object value = getValueWithMapper(methodArgumentValue, argumentMapper);
                    columnToValue.put(dbColumn, value);
                }
                
                Object[] dbArguments = new Object[argumentColumns.size()];
                for (int i = 0; i < argumentColumns.size(); i++) {
                    ArgumentColumn argumentColumn = argumentColumns.get(i);
                    // the following works for the unmapped columns because get will just return null
                    dbArguments[i] = columnToValue.get(argumentColumn);
                }
                return dbArguments;
            }
            
            @Override
            public String toString() {
                String result = Arrays.toString(parameterTypes) + " to " + choosenColumns;
                return result;
            }
        };
        methodLogDetail.setValueMapper(argumentValueMapper); 
        
        // Checks to see if the event log is in the exclusion list.
        for (String excludedEventLogPath : excludedEventLogPaths) {
            if (methodLogDetail.getFullPath().startsWith(excludedEventLogPath)) {
                methodLogDetail.setLogging(false);
                log.debug(methodLogDetail.getFullPath()+" was added to the logging exclusion list because of the ["+
                          excludedEventLogPath+"] entry being excluded through the master.cfg");
            }
        }
        log.debug("Created mapping: " + methodLogDetail);
        
        methodLogDetailLookup.put(method, methodLogDetail);
        methodLogDetailCatalog.put(methodLogDetail.getEventType(), methodLogDetail);
    }
    
    /**
     * This method doesn't do much work, but it is broken out to preserve
     * the clean generic types of the ArgumentMapper.
     * @param <T>
     * @param methodArgumentValue
     * @param argumentMapper
     * @return
     */
    protected <T> Object getValueWithMapper(Object methodArgumentValue, ArgumentMapper<T> argumentMapper) {
        T value = argumentMapper.javaType.cast(methodArgumentValue);
        Object result = argumentMapper.objectMapper.map(value);
        return result;
    }

    @Override
    public ListMultimap<EventCategory, String> getEventLogTypeMultiMap() {
        Collection<MethodLogDetail> methodLogDetails = methodLogDetailLookup.values();

        ListMultimap<EventCategory, String> eventLogTypes = ArrayListMultimap.create();
        
        for (MethodLogDetail methodLogDetail : methodLogDetails) {
            eventLogTypes.put(methodLogDetail.getEventCategory(), methodLogDetail.getMethodName());
        }
        
        return eventLogTypes;
    }

    @Override
    public MethodLogDetail getDetailForEventType(String eventType) {
        return methodLogDetailCatalog.get(eventType);
    }
    
    @Override
    public MethodLogDetail getDetailForMethod(Method method) {
        return methodLogDetailLookup.get(method);
    }
    
    public List<MappedEventLog> findAllByCategories(Iterable<EventCategory> eventCategory, ReadableInstant startDate, ReadableInstant stopDate) {
        List<EventLog> eventLogs = eventLogDao.findAllByCategories(eventCategory, startDate, stopDate);
        
        List<MappedEventLog> result = mapEventLogParameters(eventLogs);
        return result;
    }
    
    private List<MappedEventLog> mapEventLogParameters(List<EventLog> eventLogs) {
        return Lists.transform(eventLogs, new Function<EventLog, MappedEventLog> () {
            public MappedEventLog apply(EventLog from) {
                return mapEventLogParameters(from);
            }
            
        });
    }
    
    private MappedEventLog mapEventLogParameters(EventLog eventLog) {
        MappedEventLog mappedEventLog = new MappedEventLog();
        mappedEventLog.setEventLog(eventLog);
        
        MethodLogDetail methodLogDetail = getDetailForEventType(eventLog.getEventType());
        
        Object[] values = eventLog.getArguments();
        List<ArgumentColumn> columns = eventLogDao.getArgumentColumns();
        Validate.isTrue(values.length == columns.size());
        
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            ArgumentColumn column = columns.get(i);
            EventParameter eventParameter = methodLogDetail.getColumnToParameterMapping().get(column);
            mappedEventLog.getParameterMap().put(eventParameter, value);
        }
        return mappedEventLog;
    }
    
    @Override
    public SearchResult<EventLog> 
                getFilteredPagedSearchResultByCategories(Iterable<EventCategory> eventCategories, 
                                                         ReadableInstant startDate, 
                                                         ReadableInstant stopDate, 
                                                         Integer start, 
                                                         Integer pageCount,
                                                         String filterText,
                                                         YukonUserContext userContext) {
        
        // There was no filter text supplied.  Return the whole list of event logs for the
        // supplied time period time period.
        if (StringUtils.isBlank(filterText)) {
            return eventLogDao.getPagedSearchResultByCategories(eventCategories, startDate, 
                                                                stopDate, start, pageCount);
        }

        SearchResult<EventLog> pagedSearchResultByCategories = 
            eventLogDao.getFilteredPagedSearchResultByCategories(eventCategories, 
                                                                 startDate, 
                                                                 stopDate, 
                                                                 start, 
                                                                 pageCount,
                                                                 filterText);
        
        return pagedSearchResultByCategories;
        
    }
    
    @Override
    public SearchResult<EventLog> getFilteredPagedSearchResultByType(String eventLogType,
                                                                     ReadableInstant startDate,
                                                                     ReadableInstant stopDate,
                                                                     int startIndex,
                                                                     int itemsPerPage,
                                                                     UiFilter<EventLog> eventLogSqlFilters,
                                                                     YukonUserContext userContext) {
        
        // Build up the Event Log Type filter and Date Range Filter from the supplied
        // eventLogType and start and stop dates
        List<UiFilter<EventLog>> filters = Lists.newArrayList();
        filters.add(new EventLogTypeFilter(eventLogType));
        filters.add(new EventLogDateRangeFilter(startDate, stopDate));
        if (eventLogSqlFilters != null) {
            filters.add(eventLogSqlFilters);
        }
        UiFilter<EventLog> filter = UiFilterList.wrap(filters);
        
        // Process search results
        SearchResult<EventLog> searchResult =
            filterService.filter(filter, null, startIndex, itemsPerPage, 
                                 eventLogDao.getEventLogRowMapper());

        return searchResult;
    }
    
    @Override
    public List<List<String>> getDataGridRow(SearchResult<EventLog> searchResult, 
                                             YukonUserContext userContext) {
        
        List<EventLog> resultList = searchResult.getResultList();
        
        List<List<String>> dataGrid = Lists.newArrayList();
        for (EventLog eventLog : resultList) {
            DateFormatEnum dateDisplayFormat = DateFormatEnum.BOTH;
            
            List<String> dataRow = Lists.newArrayList();
            
            dataRow.add(eventLog.getEventType());
            dataRow.add(dateFormattingService.format(eventLog.getDateTime(), dateDisplayFormat, userContext));

            for (Object argument : eventLog.getArguments()) {
                if (argument == null) { continue;}
                
                if (argument instanceof Date) {
                    dataRow.add(dateFormattingService.format(argument, dateDisplayFormat, userContext));
                } else {
                    dataRow.add(argument.toString());
                }
            }
            dataGrid.add(dataRow);
        }
        return dataGrid;
    }
    
    
    // UI Filters
    private static class EventLogTypeFilter extends SqlFragmentUiFilter<EventLog> {

        String eventLogType;
        
        public EventLogTypeFilter(String eventLogType) {
            this.eventLogType = eventLogType;
        }
        
        @Override
        protected void getSqlFragment(SqlBuilder sql) {
            sql.append("EventType").eq(eventLogType);
        }
    };

    private static class EventLogDateRangeFilter extends SqlFragmentUiFilter<EventLog> {

        private Instant startDate;
        private Instant stopDate;
        
        public EventLogDateRangeFilter(ReadableInstant startDate,
                                       ReadableInstant stopDate) {
            this.startDate = startDate.toInstant();
            this.stopDate = stopDate.toInstant();
        }
        
        @Override
        protected void getSqlFragment(SqlBuilder sql) {
            sql.append("EventTime").gte(startDate).append(" AND ");
            sql.append("EventTime").lte(stopDate);
        }
    };
    
    @Autowired
    public void setConfigurationSource(ConfigurationSource configurationSource) {
        this.configurationSource = configurationSource;
    }

    @Autowired
    public void setDateFormattingService(DateFormattingService dateFormattingService) {
        this.dateFormattingService = dateFormattingService;
    }
    
    @Autowired
    public void setEventLogDao(EventLogDao eventLogDao) {
        this.eventLogDao = eventLogDao;
    }
    
    @Autowired
    public void setFilterService(FilterService filterService) {
        this.filterService = filterService;
    }
    
}