package com.cannontech.dr.estimatedload.dao.impl;

import java.beans.PropertyEditor;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.Logger;
import org.joda.time.LocalTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.LMGearDao;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.estimatedload.ApplianceCategoryAssignment;
import com.cannontech.dr.estimatedload.EstimatedLoadException;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.dr.estimatedload.GearAssignment;
import com.cannontech.dr.estimatedload.NoAppCatFormulaException;
import com.cannontech.dr.estimatedload.NoGearFormulaException;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.dao.FormulaNotFoundException;
import com.cannontech.loadcontrol.data.LMProgramDirectGear;
import com.cannontech.stars.dr.appliance.dao.ApplianceCategoryDao;
import com.cannontech.stars.dr.appliance.model.ApplianceCategory;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class FormulaDaoImpl implements FormulaDao {
    private final Logger log = YukonLogManager.getLogger(FormulaDaoImpl.class);
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private ApplianceCategoryDao applianceCategoryDao;
    @Autowired private LMGearDao gearDao;

    private static SimpleTableAccessTemplate<Formula> formulaInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaFunction> functionInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaLookupTable<?>> lookupTableInsertTemplate;

    private static FieldMapper<Formula> baseFormulaFieldMapper;
    private static FieldMapper<FormulaFunction> functionFieldMapper;
    private static FieldMapper<FormulaLookupTable<?>> lookupTableFieldMapper;

    private static final String formulaTableName = "EstimatedLoadFormula";
    private static final String functionTableName = "EstimatedLoadFunction";
    private static final String lookupTableName = "EstimatedLoadLookupTable";
    private static final String tableEntryTableName = "EstimatedLoadTableEntry";
    private static final String assignmentTableName = "EstimatedLoadFormulaAssignment";
    private static ChunkingSqlTemplate chunkingTemplate;

    @Override
    public List<Formula> getAllFormulas() {
        Map<Integer, ImmutableList<FormulaFunction>> functions = getFunctions(null);
        Map<Integer, ImmutableList<FormulaLookupTable<Double>>> lookupTables = getLookupTables(null);
        Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> timeLookupTables = getTimeLookupTables(null);
        SqlStatementBuilder sql = getFormulaQuery(null);

        FormulaRowMapper formulaRowMapper = new FormulaRowMapper(functions, lookupTables, timeLookupTables);

        return yukonJdbcTemplate.query(sql, formulaRowMapper);
    }

    // First-level CRUD operations (Formula objects)
    @Override
    public Formula getFormulaById(int formulaId) {
        SqlStatementBuilder whereClause = new SqlStatementBuilder();
        whereClause.append("WHERE EstimatedLoadFormulaId").eq(formulaId);

        SqlStatementBuilder sql = getFormulaQuery(whereClause);
        Map<Integer, ImmutableList<FormulaFunction>> functions = getFunctions(whereClause);
        Map<Integer, ImmutableList<FormulaLookupTable<Double>>> lookupTables = getLookupTables(whereClause);
        Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> timeLookupTables = getTimeLookupTables(whereClause);

        FormulaRowMapper formulaRowMapper = new FormulaRowMapper(functions, lookupTables, timeLookupTables);
        return yukonJdbcTemplate.queryForLimitedResults(sql, formulaRowMapper, 1).get(0);
    }

    @Override
    public boolean isFormulaNameInUse(Integer formulaId, String name) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM EstimatedLoadFormula WHERE UPPER(Name) = UPPER(").appendArgument(name).append(")");
        // If formulaId is null a new formula is being created, check all other formula names for uniqueness.
        if (formulaId != null) {
            // If formulaId is not null then an existing formula is being re-saved, check all other ids to ensure
            // the possibly modified name is not already used.
            sql.append("AND EstimatedLoadFormulaId").neq(formulaId);
        }
        int count = yukonJdbcTemplate.queryForInt(sql);
        return count > 0;
    }

    /** Returns the base query used for retrieving formula objects.  If no whereClause is supplied,
     * the query provided will select all Formula objects.
     * 
     * @param whereClause Appended to the base query to select a smaller subset of Formula objects.  
     */
    private SqlStatementBuilder getFormulaQuery(SqlFragmentSource whereClause) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadFormulaId, Name, FormulaType, CalculationType, FunctionIntercept FROM");
        sql.append(formulaTableName);
        if (whereClause != null) {
            sql.append(whereClause);
        }
        return sql;
    }

    @Override
    public int saveFormula(Formula formula) {
        int formulaId = formulaInsertTemplate.save(formula);

        deleteFunctionsByFormulaId(formulaId);
        deleteLookupTablesByFormulaId(formulaId);

        if (formula.getFunctions() != null) {
            for (FormulaFunction function : formula.getFunctions()) {
                functionInsertTemplate.insert(function.withFormulaId(formulaId));
            }
        }
        if (formula.getTables() != null) {
            for (FormulaLookupTable<Double> table : formula.getTables()) {
                int tableId = lookupTableInsertTemplate.insert(table.withFormulaId(formulaId));
                saveTableEntries(tableId, table.getInput(), table.getEntries());
            }
        }
        if (formula.getTimeTables() != null) {
            for (FormulaLookupTable<LocalTime> table : formula.getTimeTables()) {
                int tableId = lookupTableInsertTemplate.insert(table.withFormulaId(formulaId));
                saveTableEntries(tableId, table.getInput(), table.getEntries());
            }
        }
        if(log.isDebugEnabled()) {
            log.debug("Saved estimated load reduction formula id: " + formulaId);
        }

        return formulaId;
    }

    @Override
    public void deleteFormulaById(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(formulaTableName);
        sql.append("WHERE EstimatedLoadFormulaId").eq(formulaId);

        yukonJdbcTemplate.update(sql);
        if(log.isDebugEnabled()) {
            log.debug("Deleted estimated load reduction formula id: " + formulaId);
        }
    }

    // Second-level CRUD operations (FormulaFunction and FormulaLookupTable objects).
    /** Returns a map of formula id to list of FormulaFunction objects that belong to that formula.
     * If no whereClause is supplied, the query provided will select all FormulaFunction objects.
     * 
     * @param whereClause Appended to the base query to select a smaller subset of FormulaFunction objects.
     */
    private Map<Integer, ImmutableList<FormulaFunction>> getFunctions(SqlFragmentSource whereClause) {
        Map<Integer, ImmutableList<FormulaFunction>> functionsByFormulaId = new HashMap<>();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadFunctionId, EstimatedLoadFormulaId, Name, InputType,");
        sql.append(       "InputMin, InputMax, InputPointId, Quadratic, Linear");
        sql.append("FROM").append(functionTableName);
        if (whereClause != null) {
            sql.append(whereClause);
        }

        FunctionRowHandler functionRowMapper = new FunctionRowHandler();
        yukonJdbcTemplate.query(sql, functionRowMapper);

        for (Integer formulaId : functionRowMapper.functionsByFormulaId.keySet()) {
            Collection<FormulaFunction> functions = functionRowMapper.functionsByFormulaId.get(formulaId);
            functionsByFormulaId.put(formulaId, ImmutableList.copyOf(functions));
        }

        return functionsByFormulaId;
    }

    /** Deletes all formula functions that belong to a given formula id. */
    private void deleteFunctionsByFormulaId(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(functionTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);

        yukonJdbcTemplate.update(sql);
    }

    /** Returns a Map of FormulaId to a List of its lookup tables for all Double input types.
     * If no whereClause is supplied, the query provided will select all FormulaFunction objects.
     * 
     * @param whereClause Appended to the base query to select a smaller subset of FormulaFunction objects. 
     */
    private Map<Integer, ImmutableList<FormulaLookupTable<Double>>> getLookupTables(SqlFragmentSource whereClause) {
        SqlStatementBuilder entriesWhereClause;
        entriesWhereClause = new SqlStatementBuilder();
        entriesWhereClause.append("WHERE EstimatedLoadLookupTableId IN ");
        entriesWhereClause.append("(SELECT EstimatedLoadLookupTableId FROM EstimatedLoadLookupTable ");
        if (whereClause != null) {
            entriesWhereClause.append(whereClause);
            entriesWhereClause.append("AND InputType").neq_k(InputType.TIME_LOOKUP).append(")"); 
        } else {
            entriesWhereClause.append("WHERE InputType").neq_k(InputType.TIME_LOOKUP).append(")");
        }
        Map<Integer, Map<String, Double>> tableEntries = getTableEntries(entriesWhereClause);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadLookupTableId, EstimatedLoadFormulaId,");
        sql.append(       "Name, InputType, InputMin, InputMax, InputPointId");
        sql.append("FROM").append(lookupTableName);
        if (whereClause != null) {
            sql.append(whereClause);
            sql.append("AND InputType").neq_k(InputType.TIME_LOOKUP);
        } else {
            sql.append("WHERE InputType").neq_k(InputType.TIME_LOOKUP);
        }

        LookupTableRowHandler lookupTableRowMapper = new LookupTableRowHandler(tableEntries);
        yukonJdbcTemplate.query(sql, lookupTableRowMapper);

        Map<Integer, ImmutableList<FormulaLookupTable<Double>>> tablesByFormulaId = new HashMap<>();
        for (Integer formulaId : lookupTableRowMapper.lookupTablesByFormulaId.keySet()) {
            Collection<FormulaLookupTable<Double>> tables = lookupTableRowMapper.lookupTablesByFormulaId.get(formulaId);
            tablesByFormulaId.put(formulaId, ImmutableList.copyOf(tables));
        }

        return tablesByFormulaId;
    }

    /** Returns a Map of FormulaId to a List of its lookup tables for all LocalTime input types.
     * If no whereClause is supplied, the query provided will select all FormulaFunction objects.
     * 
     * @param whereClause Appended to the base query to select a smaller subset of FormulaFunction objects.
     */
    private Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> getTimeLookupTables(SqlFragmentSource whereClause) {
        SqlStatementBuilder entriesWhereClause;
        entriesWhereClause = new SqlStatementBuilder();
        entriesWhereClause.append("WHERE EstimatedLoadLookupTableId IN ");
        entriesWhereClause.append("(SELECT EstimatedLoadLookupTableId FROM EstimatedLoadLookupTable ");
        if (whereClause != null) {
            entriesWhereClause.append(whereClause);
            entriesWhereClause.append("AND InputType").eq_k(InputType.TIME_LOOKUP).append(")"); 
        } else {
            entriesWhereClause.append("WHERE InputType").eq_k(InputType.TIME_LOOKUP).append(")");
        }
        Map<Integer, Map<String, Double>> tableEntries = getTableEntries(entriesWhereClause);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadLookupTableId, EstimatedLoadFormulaId,");
        sql.append(       "Name, InputType, InputMin, InputMax, InputPointId");
        sql.append("FROM").append(lookupTableName);
        if (whereClause != null) {
            sql.append(whereClause);
            sql.append("AND InputType").eq_k(InputType.TIME_LOOKUP);
        } else {
            sql.append("WHERE InputType").eq_k(InputType.TIME_LOOKUP);
        }

        LookupTableRowHandler lookupTableRowMapper = new LookupTableRowHandler(tableEntries);
        yukonJdbcTemplate.query(sql, lookupTableRowMapper);

        Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> tablesByFormulaId = new HashMap<>();
        for (Integer formulaId : lookupTableRowMapper.timeLookupTablesByFormulaId.keySet()) {
            Collection<FormulaLookupTable<LocalTime>> tables = lookupTableRowMapper.timeLookupTablesByFormulaId.get(formulaId);
            tablesByFormulaId.put(formulaId, ImmutableList.copyOf(tables));
        }

        return tablesByFormulaId;
    }

    /** Deletes all lookup tables that belong to a given formula id. */
    private void deleteLookupTablesByFormulaId(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(lookupTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);

        yukonJdbcTemplate.update(sql);
    }

    // Third-level CRUD operations (FormulaLookupTable entries).
    /** Saves all table entries for a FormulaLookupTable object.  First deletes all existing entries
     * and then saves the new entry values.
     * 
     * @param tableId The id of the formula lookup table to which the entries belong.
     * @param formulaInput The FormulaInput object associated with the lookup table and its entries.
     * @param entries The map of key, value pairs that comprise the actual lookup table entries.
     */
    private void saveTableEntries(Integer tableId, FormulaInput<?> formulaInput, Map<?, Double> entries) {
        deleteTableEntries(tableId);
        insertTableEntries(tableId, formulaInput, entries);
    }

    /** Removes all existing lookup table entries for a given lookup table id. */
    private void deleteTableEntries(int lookupTableId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(tableEntryTableName);
        sql.append("WHERE EstimatedLoadLookupTableId").eq(lookupTableId);

        yukonJdbcTemplate.update(sql);
    }

    /** Inserts the list of lookup table entries for a given lookup table id and its associated input object. */
    private void insertTableEntries(int lookupTableId, FormulaInput<?> formulaInput, Map<?, Double> entries) {
        for (Map.Entry<?, Double> entry : entries.entrySet()) {
            insertTableEntry(lookupTableId, formulaInput, entry);
        }
    }

    /** Inserts a single lookup table entry for a given lookup table id and input. */
    private void insertTableEntry(int lookupTableId, FormulaInput<?> formulaInput, Map.Entry<?, Double> entry) {
        PropertyEditor typeConverter = formulaInput.getInputType().makeTypeConverter();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int entryId = nextValueHelper.getNextValue("EstimatedLoadTableEntry"); 
        sql.append("INSERT INTO").append(tableEntryTableName);
        sql.append("(EstimatedLoadTableEntryId, EstimatedLoadLookupTableId, EntryKey, EntryValue)");
        typeConverter.setValue(entry.getKey());
        sql.values(entryId, lookupTableId, typeConverter.getAsText(), entry.getValue());
        
        yukonJdbcTemplate.update(sql);
    }

    /** Retrieves all lookup table entries as a map of lookup table id to a map of entry key/value pairs.
     * By default all table entries will be returned unless a where clause is specified.
     * 
     * @param entriesWhereClause Appended to the base query to select a smaller subset of lookup table entries.
     */
    private Map<Integer, Map<String, Double>> getTableEntries(SqlFragmentSource entriesWhereClause) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadLookupTableId, EntryKey, EntryValue");
        sql.append("FROM").append(tableEntryTableName);
        if (entriesWhereClause != null) {
            sql.append(entriesWhereClause);
        }
        LookupTableEntryRowHandler entryMapper = new LookupTableEntryRowHandler();
        yukonJdbcTemplate.query(sql, entryMapper);
        
        return entryMapper.entriesByTableId;
    }


    // Setting up field mappers for use with SimpleTableAccessTemplate.
    static {
        baseFormulaFieldMapper = new FieldMapper<Formula>() {
            @Override
            public Number getPrimaryKey(Formula formula) {
                return formula.getFormulaId();
            }
            @Override
            public void setPrimaryKey(Formula formula, int value) {
                // Do nothing because Formula is immutable. 
            }
            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, Formula formula) {
                parameterHolder.addValue("Name", formula.getName());
                parameterHolder.addValue("FormulaType", formula.getType());
                parameterHolder.addValue("CalculationType", formula.getCalculationType());
                parameterHolder.addValue("FunctionIntercept", formula.getFunctionIntercept());
            }
        };

        functionFieldMapper = new FieldMapper<FormulaFunction>() {
            @Override
            public Number getPrimaryKey(FormulaFunction function) {
                return function.getFunctionId();
            }
            @Override
            public void setPrimaryKey(FormulaFunction function, int value) {
                // Do nothing because Formula is immutable. 
            }
            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, FormulaFunction function) {
                PropertyEditor typeConverter = function.getInput().getInputType().makeTypeConverter();
                parameterHolder.addValue("EstimatedLoadFormulaId", function.getFormulaId());
                parameterHolder.addValue("Name", function.getName());
                parameterHolder.addValue("InputType", function.getInput().getInputType());
                parameterHolder.addValue("InputPointId", function.getInput().getPointId());
                typeConverter.setValue(function.getInput().getMin());
                parameterHolder.addValue("InputMin", typeConverter.getAsText());
                typeConverter.setValue(function.getInput().getMax());
                parameterHolder.addValue("InputMax", typeConverter.getAsText());
                parameterHolder.addValue("Quadratic", function.getQuadratic());
                parameterHolder.addValue("Linear", function.getLinear());
            }
            
        };

        lookupTableFieldMapper = new FieldMapper<FormulaLookupTable<?>>() {
            @Override
            public Number getPrimaryKey(FormulaLookupTable<?> lookupTable) {
                return lookupTable.getLookupTableId();
            }
            @Override
            public void setPrimaryKey(FormulaLookupTable<?> lookupTable, int value) {
                // Do nothing because FormulaLookupTable is immutable.
            }
            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, FormulaLookupTable<?> lookupTable) {
                PropertyEditor typeConverter = lookupTable.getInput().getInputType().makeTypeConverter();
                parameterHolder.addValue("EstimatedLoadFormulaId", lookupTable.getFormulaId());
                parameterHolder.addValue("Name", lookupTable.getName());
                parameterHolder.addValue("InputType", lookupTable.getInput().getInputType());
                typeConverter.setValue(lookupTable.getInput().getMin());
                parameterHolder.addValue("InputMin", typeConverter.getAsText());
                typeConverter.setValue(lookupTable.getInput().getMax());
                parameterHolder.addValue("InputMax", typeConverter.getAsText());
                parameterHolder.addValue("InputPointId", lookupTable.getInput().getPointId());
            }
        };
    }

    // Setting up row mappers and callback handlers.
    private static class FormulaRowMapper implements YukonRowMapper<Formula> {
        //Inputs
        Map<Integer, ImmutableList<FormulaLookupTable<Double>>> lookupTablesByFormulaId;
        Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> timeLookupTablesByFormulaId;
        Map<Integer, ImmutableList<FormulaFunction>> functionsByFormulaId;
        
        public FormulaRowMapper(
                Map<Integer, ImmutableList<FormulaFunction>> functionsByFormulaId,
                Map<Integer, ImmutableList<FormulaLookupTable<Double>>> lookupTablesByFormulaId,
                Map<Integer, ImmutableList<FormulaLookupTable<LocalTime>>> timeLookupTablesByFormulaId) {
                
            this.functionsByFormulaId = functionsByFormulaId;
            this.lookupTablesByFormulaId = lookupTablesByFormulaId;
            this.timeLookupTablesByFormulaId = timeLookupTablesByFormulaId;
        }
        
        @Override
        public Formula mapRow(YukonResultSet rs) throws SQLException {
            Integer formulaId = rs.getInt("EstimatedLoadFormulaId");
            return new Formula(
                    formulaId,
                    rs.getString("Name"),
                    rs.getEnum("FormulaType", Formula.Type.class),
                    rs.getEnum("CalculationType", Formula.CalculationType.class),
                    rs.getDouble("FunctionIntercept"),
                    functionsByFormulaId.get(formulaId),
                    lookupTablesByFormulaId.get(formulaId),
                    timeLookupTablesByFormulaId.get(formulaId));
        }
    }

    private static class FunctionRowHandler implements YukonRowCallbackHandler {
        Multimap<Integer, FormulaFunction> functionsByFormulaId = HashMultimap.create();
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            InputType inputType = rs.getEnum("InputType", FormulaInput.InputType.class);
            FormulaInput<Double> formulaInput = mapFormulaInput(rs, inputType, Double.class);
            
            Integer formulaId = rs.getInt("EstimatedLoadFormulaId");
            functionsByFormulaId.put(formulaId, new FormulaFunction(
                    rs.getInt("EstimatedLoadFunctionId"), 
                    formulaId, 
                    rs.getString("Name"),
                    formulaInput, 
                    rs.getDouble("Quadratic"), 
                    rs.getDouble("Linear")));
        }
    }

    private static class LookupTableRowHandler implements YukonRowCallbackHandler {
        Multimap<Integer, FormulaLookupTable<Double>> lookupTablesByFormulaId = HashMultimap.create();
        Multimap<Integer, FormulaLookupTable<LocalTime>> timeLookupTablesByFormulaId = HashMultimap.create();
        Map<Integer, Map<String, Double>> entriesByTableId;

        public LookupTableRowHandler(Map<Integer, Map<String, Double>> entriesByTableId) {
            this.entriesByTableId = entriesByTableId;
        }

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            InputType inputType = rs.getEnum("InputType", FormulaInput.InputType.class);

            Integer tableId = rs.getInt("EstimatedLoadLookupTableId");
            Integer formulaId = rs.getInt("EstimatedLoadFormulaId");
            if (inputType == InputType.TIME_LOOKUP) {
                FormulaInput<LocalTime> formulaInput = mapFormulaInput(rs, inputType, LocalTime.class);
                PropertyEditor typeConverter = formulaInput.getInputType().makeTypeConverter();
                ImmutableMap.Builder<LocalTime, Double> entries = ImmutableMap.builder();
                for (Map.Entry<String, Double> entry : entriesByTableId.get(tableId).entrySet()) {
                    typeConverter.setAsText(entry.getKey());
                    entries.put((LocalTime) typeConverter.getValue(), entry.getValue());
                }
                FormulaLookupTable<LocalTime> table = new FormulaLookupTable<LocalTime>(
                        tableId,
                        formulaId,
                        rs.getString("Name"),
                        formulaInput,
                        entries.build());
                timeLookupTablesByFormulaId.put(formulaId, table);
            } else {
                FormulaInput<Double> formulaInput = mapFormulaInput(rs, inputType, Double.class);
                PropertyEditor typeConverter = formulaInput.getInputType().makeTypeConverter();
                ImmutableMap.Builder<Double, Double> entries = ImmutableMap.builder();
                for (Map.Entry<String, Double> entry : entriesByTableId.get(tableId).entrySet()) {
                    typeConverter.setAsText(entry.getKey());
                    entries.put((Double) typeConverter.getValue(), entry.getValue());
                }
                FormulaLookupTable<Double> table = new FormulaLookupTable<Double>(
                        tableId,
                        formulaId,
                        rs.getString("Name"),
                        formulaInput,
                        entries.build());
                lookupTablesByFormulaId.put(formulaId, table);
            }
        }
    }

    private static class LookupTableEntryRowHandler implements YukonRowCallbackHandler {
        Map<Integer, Map<String, Double>> entriesByTableId = new HashMap<>();
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            Integer lookupTableId = rs.getInt("EstimatedLoadLookupTableId");
            Map<String, Double> entries = entriesByTableId.get(lookupTableId);
            if (entries == null) {
                entries = new HashMap<>();
                entriesByTableId.put(lookupTableId, entries);
            }
            entries.put(rs.getString("EntryKey"), rs.getDouble("EntryValue"));
        }
    }

    private static <T extends Comparable<? super T>> FormulaInput<T> mapFormulaInput(YukonResultSet rs,
            InputType inputType, Class<T> klass) throws SQLException {
        PropertyEditor typeConverter = inputType.makeTypeConverter();
        typeConverter.setAsText(rs.getString("InputMin"));
        Object inputMin = typeConverter.getValue();
        typeConverter.setAsText(rs.getString("InputMax"));
        Object inputMax = typeConverter.getValue();

        return new FormulaInput<T>(inputType, klass.cast(inputMin), klass.cast(inputMax),
                rs.getNullableInt("InputPointId"));
    }

    // Setting up SimpleTableAccessTemplate objects.
    @PostConstruct
    public void init() {
        formulaInsertTemplate = new SimpleTableAccessTemplate<Formula>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(formulaTableName)
                .setFieldMapper(baseFormulaFieldMapper)
                .setPrimaryKeyField("EstimatedLoadFormulaId")
                .setPrimaryKeyValidOver(0);
        functionInsertTemplate = new SimpleTableAccessTemplate<FormulaFunction>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(functionTableName)
                .setFieldMapper(functionFieldMapper)
                .setPrimaryKeyField("EstimatedLoadFunctionId")
                .setPrimaryKeyValidOver(0);
        lookupTableInsertTemplate = new SimpleTableAccessTemplate<FormulaLookupTable<?>>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(lookupTableName)
                .setFieldMapper(lookupTableFieldMapper)
                .setPrimaryKeyField("EstimatedLoadLookupTableId")
                .setPrimaryKeyValidOver(0);
        chunkingTemplate = new ChunkingSqlTemplate(yukonJdbcTemplate);
    }

    /** Tests for the existence of a given formula by looking for existence of a formula id. */ 
    private boolean doesFormulaExist(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT 1 FROM").append(formulaTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);

        try {
            yukonJdbcTemplate.queryForInt(sql);
            return true;
        } catch(EmptyResultDataAccessException e) {
            return false;
        }
    }
    
    @Override
    public void saveAppCategoryAssignmentsForId(int formulaId, List<Integer> appCategoryAssignments) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(assignmentTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);
        yukonJdbcTemplate.update(sql);

        for (Integer assignId : appCategoryAssignments) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO").append(assignmentTableName);
            sql.append("(FormulaAssignmentId, EstimatedLoadFormulaId, ApplianceCategoryId)");
            sql.values(nextValueHelper.getNextValue("EstimatedLoadFormulaAssignment"),formulaId, assignId);
            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    public void unassignAppCategory(int appCategoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(assignmentTableName);
        sql.append("WHERE ApplianceCategoryId").eq(appCategoryId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void unassignGear(int gearId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(assignmentTableName);
        sql.append("WHERE GearId").eq(gearId);
        yukonJdbcTemplate.update(sql);
    }

    @Override
    public void saveAppCategoryAssignmentForId(int formulaId, int appCategoryId) {
        if (!doesFormulaExist(formulaId)) {
            throw new FormulaNotFoundException("Formula id# "+formulaId+" does not exist. Cannot assign appliance category");
        }
        unassignAppCategory(appCategoryId);
        List<Integer> assignments = getAppCategoryAssignmentIds(formulaId);
        assignments.add(appCategoryId);
        saveAppCategoryAssignmentsForId(formulaId, assignments);
    }

    @Override
    public List<Integer> getAppCategoryAssignmentIds(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT ApplianceCategoryId FROM").append(assignmentTableName);
        sql.append("WHERE EstimatedLoadFormulaId").eq(formulaId);
        sql.append("AND ApplianceCategoryId IS NOT NULL");

        List<Integer> applianceCategoryIds = yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);

        return applianceCategoryIds;
    }

    @Override
    @Transactional
    public void saveGearAssignmentsForId(int formulaId, List<Integer> gearIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(assignmentTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);
        yukonJdbcTemplate.update(sql);

        for (Integer assignId : gearIds) {
            sql = new SqlStatementBuilder();
            sql.append("INSERT INTO").append(assignmentTableName);
            sql.append("(FormulaAssignmentId, EstimatedLoadFormulaId, GearId)");
            sql.values(nextValueHelper.getNextValue("EstimatedLoadFormulaAssignment"),formulaId, assignId);

            yukonJdbcTemplate.update(sql);
        }
    }

    @Override
    @Transactional
    public void saveGearAssignmentForId(int formulaId, int gearId) {
        if (!doesFormulaExist(formulaId)) {
            throw new FormulaNotFoundException("Formula id# "+formulaId+" does not exist. Cannot assign gear");
        }
        unassignGear(gearId);
        List<Integer> assignments = getGearAssignmentIds(formulaId);
        assignments.add(gearId);
        saveGearAssignmentsForId(formulaId, assignments);
    }

    @Override
    public List<Integer> getGearAssignmentIds(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT GearId FROM").append(assignmentTableName);
        sql.append("WHERE EstimatedLoadFormulaId").eq(formulaId);
        sql.append("AND GearId IS NOT NULL");

        List<Integer> gearIds = yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);

        return gearIds;
    }

    @Override
    public List<GearAssignment> getAssignmentsForGears(Iterable<Integer> gearIds) {
        final Map<Integer, LMProgramDirectGear> gears = gearDao.getByGearIds(gearIds);

        final Map<Integer, GearAssignment> assignmentMap = new HashMap<>();
        for (LMProgramDirectGear gear : gears.values()) {
            assignmentMap.put(gear.getGearId(), new GearAssignment(gear, null));
        }

        chunkingTemplate.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT GearId, EstimatedLoadFormulaId FROM").append(assignmentTableName);
                sql.append("WHERE GearId").in(subList);
                return sql;
            }
        },
        gearIds,
        new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int gearId = rs.getInt("GearId");
                int formulaIdOrNull = rs.getInt("EstimatedLoadFormulaId");
                Integer formulaId = rs.wasNull() ? null : formulaIdOrNull;
                assignmentMap.put(gearId, new GearAssignment(gears.get(gearId), formulaId));
            }
        });

        return new ArrayList<>(assignmentMap.values());
    }

    @Override
    public GearAssignment getAssignmentForGear(int gearId) {
        return getAssignmentsForGears(Collections.singleton(gearId)).get(0);
    }

    /**
     * Returns Map&lt;AppCatId, FormulaId(nullable)&gt;
     */
    @Override
    public List<ApplianceCategoryAssignment> getAssignmentsForApplianceCategories(Iterable<Integer> appCategoryIds) {
        final Map<Integer, ApplianceCategory> appCats = applianceCategoryDao.getByApplianceCategoryIds(appCategoryIds);

        final Map<Integer, ApplianceCategoryAssignment> assignmentMap = new HashMap<>();
        for (ApplianceCategory appCat : appCats.values()) {
            assignmentMap.put(appCat.getApplianceCategoryId(), new ApplianceCategoryAssignment(appCat, null));
        }

        chunkingTemplate.query(new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT ApplianceCategoryId, EstimatedLoadFormulaId FROM").append(assignmentTableName);
                sql.append("WHERE ApplianceCategoryId").in(subList);
                return sql;
            }
        },
        appCategoryIds,
        new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                int appCatId = rs.getInt("ApplianceCategoryId");
                int formulaIdOrNull = rs.getInt("EstimatedLoadFormulaId");
                Integer formulaId = rs.wasNull() ? null : formulaIdOrNull;

                assignmentMap.put(appCatId, new ApplianceCategoryAssignment(appCats.get(appCatId), formulaId));
            }
        });

        return new ArrayList<>(assignmentMap.values());
    }

    @Override
    public ApplianceCategoryAssignment getAssignmentForApplianceCategory(int appCategoryId) {
        return getAssignmentsForApplianceCategories(Collections.singleton(appCategoryId)).get(0);
    }

    @Override
    public boolean hasFormulaInputPoints(int paoId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM").append("YukonPaobject ypo");
        sql.append("JOIN Point p ON p.PAObjectId = ypo.PAObjectId");
        sql.append("JOIN EstimatedLoadFunction elf ON p.PointId = elf.InputPointId");
        sql.append("WHERE ypo.PAObjectId").eq(paoId);

        if (yukonJdbcTemplate.queryForInt(sql) != 0) {
            return true;
        }

        sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*) FROM").append("YukonPaobject ypo");
        sql.append("JOIN Point p ON p.PAObjectId = ypo.PAObjectId");
        sql.append("JOIN EstimatedLoadLookupTable ellt ON p.PointId = ellt.InputPointId");
        sql.append("WHERE ypo.PAObjectId").eq(paoId);

        return yukonJdbcTemplate.queryForInt(sql) != 0;
    }

    @Override
    public Formula getFormulaForApplianceCategory(int appCategoryId) throws EstimatedLoadException {
        ApplianceCategoryAssignment assignment = getAssignmentForApplianceCategory(appCategoryId);
        if (assignment.getFormulaId() == null) {
            throw new NoAppCatFormulaException(appCategoryId);
        } else {
            try {
                return getFormulaById(assignment.getFormulaId());
            } catch (DataAccessException e) {
                throw new NoAppCatFormulaException(appCategoryId);
            }
        }
    }

    @Override
    public Formula getFormulaForGear(int gearId) throws EstimatedLoadException {
        GearAssignment assignment = getAssignmentForGear(gearId);
        if (assignment.getFormulaId() == null) {
            throw new NoGearFormulaException(gearId);
        } else {
            try {
                return getFormulaById(assignment.getFormulaId());
            } catch (DataAccessException e) {
                throw new NoGearFormulaException(gearId);
            }
        }
    }
}
