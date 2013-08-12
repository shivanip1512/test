package com.cannontech.dr.estimatedload.dao.impl;

import java.beans.PropertyEditor;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaInput.InputType;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

public class FormulaDaoImpl implements FormulaDao {
    private final Logger log = YukonLogManager.getLogger(FormulaDaoImpl.class);
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private static SimpleTableAccessTemplate<Formula> formulaInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaFunction> functionInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaLookupTable<Object>> lookupTableInsertTemplate;

    private static FieldMapper<Formula> baseFormulaFieldMapper;
    private static FieldMapper<FormulaFunction> functionFieldMapper;
    private static FieldMapper<FormulaLookupTable<Object>> lookupTableFieldMapper;

    private static final String formulaTableName = "EstimatedLoadFormula";
    private static final String functionTableName = "EstimatedLoadFunction";
    private static final String lookupTableName = "EstimatedLoadLookupTable";
    private static final String tableEntryTableName = "EstimatedLoadTableEntry";
    //private static final String assignmentTableName = "EstimatedLoadFormulaAssignment";

    @Override
    public List<Formula> getAllFormulas() {
        Map<Integer, ImmutableList<FormulaLookupTable<Object>>> lookupTables = getLookupTables(null);
        Map<Integer, ImmutableList<FormulaFunction>> functions = getFunctions(null);
        SqlStatementBuilder sql = getFormulaQuery(null);

        FormulaRowMapper formulaRowMapper = new FormulaRowMapper(lookupTables, functions);

        return yukonJdbcTemplate.query(sql, formulaRowMapper);
    }

    // First-level CRUD operations (Formula objects)
    @Override
    public Formula getFormulaById(int formulaId) {
        SqlFragment whereClause = new SqlFragment("WHERE EstimatedLoadFormulaId = ?", formulaId);

        SqlStatementBuilder sql = getFormulaQuery(whereClause);
        Map<Integer, ImmutableList<FormulaLookupTable<Object>>> lookupTables = getLookupTables(whereClause);
        Map<Integer, ImmutableList<FormulaFunction>> functions = getFunctions(whereClause);

        FormulaRowMapper formulaRowMapper = new FormulaRowMapper(lookupTables, functions);
        return yukonJdbcTemplate.queryForLimitedResults(sql, formulaRowMapper, 1).get(0);
    }

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
            for (FormulaLookupTable<Object> table : formula.getTables()) {
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
    
    private void deleteFunctionsByFormulaId(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(functionTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);

        yukonJdbcTemplate.update(sql);
    }

    /** Returns a Map of FormulaId to a List of its lookup tables. */
    private Map<Integer, ImmutableList<FormulaLookupTable<Object>>> getLookupTables(SqlFragmentSource whereClause) {
        SqlFragment entriesWhereClause;
        if (whereClause != null) {
             entriesWhereClause = new SqlFragment("WHERE EstimatedLoadLookupTableId IN "
                    + "(SELECT EstimatedLoadLookupTableId FROM EstimatedLoadLookupTable " + whereClause.getSql() + ")", 
                    whereClause.getArguments());
        } else {
            entriesWhereClause = null;
        }
        Map<Integer, Map<String, Double>> tableEntries = getTableEntries(entriesWhereClause);

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadLookupTableId, EstimatedLoadFormulaId,");
        sql.append(       "Name, InputType, InputMin, InputMax, InputPointId");
        sql.append("FROM").append(lookupTableName);
        if (whereClause != null) {
            sql.append(whereClause);
        }

        LookupTableRowHandler lookupTableRowMapper = new LookupTableRowHandler(tableEntries);
        yukonJdbcTemplate.query(sql, lookupTableRowMapper);

        Map<Integer, ImmutableList<FormulaLookupTable<Object>>> tablesByFormulaId = new HashMap<>();
        for (Integer formulaId : lookupTableRowMapper.lookupTablesByFormulaId.keySet()) {
            Collection<FormulaLookupTable<Object>> tables = lookupTableRowMapper.lookupTablesByFormulaId.get(formulaId);
            tablesByFormulaId.put(formulaId, ImmutableList.copyOf(tables));
        }

        return tablesByFormulaId;
    }

    private void deleteLookupTablesByFormulaId(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(lookupTableName);
        sql.append("WHERE EstimatedLoadFormulaId ").eq(formulaId);

        yukonJdbcTemplate.update(sql);
    }

    // Third-level CRUD operations (FormulaLookupTable entries).
    private void saveTableEntries(Integer tableId, FormulaInput<Object> formulaInput, Map<Object, Double> entries) {
        deleteTableEntries(tableId);
        insertTableEntries(tableId, formulaInput, entries);
    }

    private void deleteTableEntries(int lookupTableId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(tableEntryTableName);
        sql.append("WHERE EstimatedLoadLookupTableId").eq(lookupTableId);

        yukonJdbcTemplate.update(sql);
    }

    private void insertTableEntries(int lookupTableId, FormulaInput<Object> formulaInput, Map<Object, Double> entries) {
        for (Map.Entry<Object, Double> entry : entries.entrySet()) {
            insertTableEntry(lookupTableId, formulaInput, entry);
        }
    }

    private void insertTableEntry(int lookupTableId, FormulaInput<Object> formulaInput, Map.Entry<Object, Double> entry) {
        PropertyEditor typeConverter = formulaInput.getInputType().makeTypeConverter();
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int entryId = nextValueHelper.getNextValue("EstimatedLoadTableEntry"); 
        sql.append("INSERT INTO").append(tableEntryTableName);
        sql.append("(EstimatedLoadTableEntryId, EstimatedLoadLookupTableId, EntryKey, EntryValue)");
        typeConverter.setValue(entry.getKey());
        sql.values(entryId, lookupTableId, typeConverter.getAsText(), entry.getValue());
        
        yukonJdbcTemplate.update(sql);
    }

    private Map<Integer, Map<String, Double>> getTableEntries(SqlFragmentSource whereClause) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT EstimatedLoadLookupTableId, EntryKey, EntryValue");
        sql.append("FROM").append(tableEntryTableName);
        if (whereClause != null) {
            sql.append(whereClause);
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
                parameterHolder.addValue("FormulaType", formula.getFormulaType());
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

        lookupTableFieldMapper = new FieldMapper<FormulaLookupTable<Object>>() {
            @Override
            public Number getPrimaryKey(FormulaLookupTable<Object> lookupTable) {
                return lookupTable.getLookupTableId();
            }
            @Override
            public void setPrimaryKey(FormulaLookupTable<Object> lookupTable, int value) {
                // Do nothing because FormulaLookupTable is immutable.
            }
            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, FormulaLookupTable<Object> lookupTable) {
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
        Map<Integer, ImmutableList<FormulaLookupTable<Object>>> lookupTablesByFormulaId;
        Map<Integer, ImmutableList<FormulaFunction>> functionsByFormulaId;
        
        public FormulaRowMapper(
                Map<Integer, ImmutableList<FormulaLookupTable<Object>>> lookupTablesByFormulaId,
                Map<Integer, ImmutableList<FormulaFunction>> functionsByFormulaId) {
            this.lookupTablesByFormulaId = lookupTablesByFormulaId;
            this.functionsByFormulaId = functionsByFormulaId;
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
                    lookupTablesByFormulaId.get(formulaId));
        }
    }

    private static class FunctionRowHandler implements YukonRowCallbackHandler {
        Multimap<Integer, FormulaFunction> functionsByFormulaId = HashMultimap.create();
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            FormulaInput<Double> formulaInput = mapFormulaInput(rs).castAsDouble();
            
            Integer formulaId = rs.getInt("EstimatedLoadFormulaId");
            functionsByFormulaId.put(formulaId, new FormulaFunction(
                    rs.getInt("EstimatedLoadFunctionId"), 
                    formulaId, 
                    rs.getString("Name"),
                    formulaInput, 
                    rs.getDouble("Quadratic"), 
                    rs.getDouble("Linear")));
        }
    };

    private static class LookupTableRowHandler implements YukonRowCallbackHandler {
        Multimap<Integer, FormulaLookupTable<Object>> lookupTablesByFormulaId = HashMultimap.create();
        Map<Integer, Map<String, Double>> entriesByTableId;

        public LookupTableRowHandler(Map<Integer, Map<String, Double>> entriesByTableId) {
            this.entriesByTableId = entriesByTableId;
        }

        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            FormulaInput<Object> formulaInput = mapFormulaInput(rs);
            PropertyEditor typeConverter = formulaInput.getInputType().makeTypeConverter();

            Integer tableId = rs.getInt("EstimatedLoadLookupTableId");
            ImmutableMap.Builder<Object, Double> entries = ImmutableMap.builder();
            for (Map.Entry<String, Double> entry : entriesByTableId.get(tableId).entrySet()) {
                typeConverter.setAsText(entry.getKey());
                entries.put(typeConverter.getValue(), entry.getValue());
            }

            Integer formulaId = rs.getInt("EstimatedLoadFormulaId");
            FormulaLookupTable<Object> table = new FormulaLookupTable<Object>(
                    tableId,
                    formulaId,
                    rs.getString("Name"),
                    formulaInput,
                    entries.build());

            lookupTablesByFormulaId.put(formulaId, table);
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

    private static FormulaInput<Object> mapFormulaInput(YukonResultSet rs) throws SQLException {
        InputType inputType = rs.getEnum("InputType", FormulaInput.InputType.class);
        PropertyEditor typeConverter = inputType.makeTypeConverter();
        typeConverter.setAsText(rs.getString("InputMin"));
        Object inputMin = typeConverter.getValue();
        typeConverter.setAsText(rs.getString("InputMax"));
        Object inputMax = typeConverter.getValue();

        return new FormulaInput<Object>(inputType, inputMin, inputMax, rs.getNullableInt("InputPointId"));
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
        lookupTableInsertTemplate = new SimpleTableAccessTemplate<FormulaLookupTable<Object>>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(lookupTableName)
                .setFieldMapper(lookupTableFieldMapper)
                .setPrimaryKeyField("EstimatedLoadLookupTableId")
                .setPrimaryKeyValidOver(0);
    }
}
