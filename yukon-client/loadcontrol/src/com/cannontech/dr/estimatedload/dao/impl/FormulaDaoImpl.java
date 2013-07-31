package com.cannontech.dr.estimatedload.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.RowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.dr.estimatedload.Formula;
import com.cannontech.dr.estimatedload.FormulaFunction;
import com.cannontech.dr.estimatedload.FormulaInput;
import com.cannontech.dr.estimatedload.FormulaLookupTable;
import com.cannontech.dr.estimatedload.dao.FormulaDao;
import com.cannontech.dr.estimatedload.enumeration.FormulaCalculationType;
import com.cannontech.dr.estimatedload.enumeration.FormulaInputType;
import com.cannontech.dr.estimatedload.enumeration.FormulaType;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.Ordering;

public class FormulaDaoImpl implements FormulaDao {
    private final Logger log = YukonLogManager.getLogger(FormulaDaoImpl.class);
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    private static SimpleTableAccessTemplate<Formula> formulaInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaFunction> functionInsertTemplate;
    private static SimpleTableAccessTemplate<FormulaLookupTable> lookupTableInsertTemplate;
    
    private static FieldMapper<Formula> baseFormulaFieldMapper;
    private static FieldMapper<FormulaFunction> functionFieldMapper;
    private static FieldMapper<FormulaLookupTable> lookupTableFieldMapper;
    
    private static YukonRowMapper<Formula.Builder> baseFormulaRowMapper;
    private static YukonRowMapper<FormulaFunction> functionRowMapper;
    private static YukonRowMapper<FormulaLookupTable.Builder> baseLookupTableRowMapper;
//    private static YukonRowMapper<Formula> assignmentMapper;

    private static final String formulaTableName = "Formula";
    private static final String functionTableName = "FormulaFunction";
    private static final String lookupTableName = "FormulaLookupTable";
    private static final String tableEntryTableName = "FormulaLookupEntry";
    //private static final String assignmentTableName = "FormulaAssignmentMap";

    @Override
    public List<Formula> getAllFormulas() {
        List<Formula> formulas = new ArrayList<>();

        SqlStatementBuilder sql = getFormulaQueryBase();
        List<Formula.Builder> formulaBuilders = yukonJdbcTemplate.query(sql, baseFormulaRowMapper);

        for (Formula.Builder formulaBuilder : formulaBuilders) {
            formulaBuilder.setFunctions(getFunctions(formulaBuilder.getFormulaId()));
            formulaBuilder.setTables(getLookupTables(formulaBuilder.getFormulaId()));
            formulas.add(formulaBuilder.build());
        }

        return ImmutableList.copyOf(formulas);
    }

    // First-level CRUD operations (Formula objects)
    @Override
    public Formula getFormulaById(int formulaId) {
        SqlStatementBuilder sql = getFormulaQueryBase();
        sql.append("WHERE FormulaId").eq(formulaId);
        
        Formula.Builder formulaBuilder = yukonJdbcTemplate.queryForObject(sql, baseFormulaRowMapper);
        formulaBuilder.setFunctions(getFunctions(formulaId));
        formulaBuilder.setTables(getLookupTables(formulaId));
        
        return formulaBuilder.build();
    }
    
    private SqlStatementBuilder getFormulaQueryBase() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormulaId, Name, FormulaType, CalculationType, FunctionIntercept FROM");
        sql.append(formulaTableName);
        return sql;
    }

    @Override
    public int saveFormula(Formula formula) {
        int formulaId = formulaInsertTemplate.save(formula);

        for (FormulaFunction function : formula.getFunctions()) {
            functionInsertTemplate.save(function);
        }
        for (FormulaLookupTable table : formula.getTables()) {
            int tableId = lookupTableInsertTemplate.save(table);
            saveTableEntries(tableId, table.getEntries());
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
        sql.append("WHERE FormulaId").eq(formulaId);
        
        yukonJdbcTemplate.update(sql);
        if(log.isDebugEnabled()) {
            log.debug("Deleted estimated load reduction formula id: " + formulaId);
        }
    }

    @Override
    public void deleteFormula(Formula formula) {
        deleteFormulaById(formula.getFormulaId());
    }

    // Second-level CRUD operations (FormulaFunction and FormulaLookupTable objects).
    private ImmutableList<FormulaFunction> getFunctions(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormulaFunctionId, FormulaId, Name, InputType,");
        sql.append(       "InputMin, InputMax, InputPointId, Quadratic, Linear");
        sql.append("FROM").append(functionTableName);
        sql.append("WHERE FormulaId").eq(formulaId);
        
        return ImmutableList.copyOf(yukonJdbcTemplate.query(sql, functionRowMapper));
    }

    private ImmutableList<FormulaLookupTable> getLookupTables(int formulaId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormulaLookupTableId");
        sql.append("FROM").append(lookupTableName);
        sql.append("WHERE FormulaId").eq(formulaId);
        List<Integer> tableIds = yukonJdbcTemplate.query(sql, RowMapper.INTEGER);
        
        List<FormulaLookupTable> tables = new ArrayList<>();
        for (Integer tableId : tableIds) {
            tables.add(getLookupTable(formulaId, tableId));
        }
        
        return ImmutableList.copyOf(tables);
    }

    private FormulaLookupTable getLookupTable(int formulaId, int lookupTableId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormulaLookupTableId, FormulaId, Name, InputType, InputMin, InputMax, InputPointId");
        sql.append("FROM").append(lookupTableName);
        sql.append("WHERE FormulaId").eq(formulaId);
        sql.append("AND FormulaLookupTableId").eq(lookupTableId);
        
        FormulaLookupTable.Builder builder = yukonJdbcTemplate.queryForObject(sql, baseLookupTableRowMapper);
        builder.setEntries(getTableEntries(lookupTableId));
        
        return builder.build();
    }

    // Third-level CRUD operations (FormulaLookupTable entries).
    private void saveTableEntries(int lookupTableId, Map<Double, Double> entries) {
        deleteTableEntries(lookupTableId);
        insertTableEntries(lookupTableId, entries);
    }

    private void deleteTableEntries(int lookupTableId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM").append(tableEntryTableName);
        sql.append("WHERE FormulaLookupTableId").eq(lookupTableId);
        
        yukonJdbcTemplate.update(sql);
    }

    private void insertTableEntries(int lookupTableId, Map<Double, Double> entries) {
        for (Map.Entry<Double, Double> entry : entries.entrySet()) {
            insertTableEntry(lookupTableId, entry);
        }
    }

    private void insertTableEntry(int lookupTableId, Map.Entry<Double, Double> entry) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        int entryId = nextValueHelper.getNextValue("FormulaLookupEntry"); 
        sql.append("INSERT INTO").append(tableEntryTableName);
        sql.append("(FormulaLookupEntryId, FormulaLookupTableId, Key, Value)");
        sql.values(entryId, lookupTableId, entry.getKey(), entry.getValue());
        
        yukonJdbcTemplate.update(sql);
    }

    private ImmutableMap<Double, Double> getTableEntries(int lookupTableId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormulaLookupEntryId, FormulaLookupTableId, Key, Value FROM");
        sql.append(tableEntryTableName);
        sql.append("WHERE FormulaLookupTableId").eq(lookupTableId);
        sql.append("ORDER BY Key ASC");
        
        LookupTableEntryMapper entryMapper = new LookupTableEntryMapper();
        yukonJdbcTemplate.query(sql, entryMapper);
        
        return entryMapper.entries.build();
    }


    static {
        // Setting up row mappers.
        baseFormulaRowMapper = new YukonRowMapper<Formula.Builder>() {
            @Override
            public Formula.Builder mapRow(YukonResultSet rs) throws SQLException {
                Formula.Builder formulaBuilder = new Formula.Builder(
                        rs.getInt("FormulaId"),
                        rs.getString("Name"),
                        FormulaType.valueOf(rs.getString("FormulaType")),
                        FormulaCalculationType.valueOf(rs.getString("CalculationType")),
                        rs.getDouble("FunctionIntercept"));
                return formulaBuilder;
            }
        };

        baseLookupTableRowMapper = new YukonRowMapper<FormulaLookupTable.Builder>() {
            @Override
            public FormulaLookupTable.Builder mapRow(YukonResultSet rs) throws SQLException {
                FormulaInput formulaInput = mapFormulaInput(rs);

                FormulaLookupTable.Builder tableBuilder = new FormulaLookupTable.Builder(
                        rs.getInt("FormulaLookukpTableId"),
                        rs.getInt("FormulaId"),
                        rs.getString("Name"),
                        formulaInput);
                return tableBuilder;
            }


        };

        functionRowMapper = new YukonRowMapper<FormulaFunction>() {
            @Override
            public FormulaFunction mapRow(YukonResultSet rs) throws SQLException {
                FormulaInput formulaInput = mapFormulaInput(rs);
                
                return new FormulaFunction(rs.getInt("FormulaFunctionId"), rs.getInt("FormulaId"), rs.getString("Name"),
                        formulaInput, rs.getDouble("Quadratic"), rs.getDouble("Linear"));
            }
        };

        // Setting up field mappers for use with SimpleTableAccessTemplate.
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
                parameterHolder.addValue("FormulaId", function.getFormulaId());
                parameterHolder.addValue("Name", function.getName());
                parameterHolder.addValue("InputType", function.getInput().getInputType());
                parameterHolder.addValue("InputPointId", function.getInput().getPointId());
                parameterHolder.addValue("InputMin", function.getInput().getMin());
                parameterHolder.addValue("InputMax", function.getInput().getMax());
                parameterHolder.addValue("Quadratic", function.getQuadratic());
                parameterHolder.addValue("Linear", function.getLinear());
            }
            
        };

        lookupTableFieldMapper = new FieldMapper<FormulaLookupTable>() {
            @Override
            public Number getPrimaryKey(FormulaLookupTable lookupTable) {
                return lookupTable.getLookupTableId();
            }
            @Override
            public void setPrimaryKey(FormulaLookupTable lookupTable, int value) {
                // Do nothing because FormulaLookupTable is immutable.
            }
            @Override
            public void extractValues(MapSqlParameterSource parameterHolder, FormulaLookupTable lookupTable) {
                parameterHolder.addValue("FormulaId", lookupTable.getFormulaId());
                parameterHolder.addValue("Name", lookupTable.getName());
                parameterHolder.addValue("InputType", lookupTable.getInput().getInputType());
                parameterHolder.addValue("InputPointId", lookupTable.getInput().getPointId());
            }
        };
    }

    private static FormulaInput mapFormulaInput(YukonResultSet rs) throws SQLException {
        FormulaInput input = new FormulaInput(FormulaInputType.valueOf(rs.getString("InputType")),
                rs.getDouble("InputMin"), rs.getDouble("InputMax"), rs.getInt("InputPointId"));
        return input;
    }

    private class LookupTableEntryMapper implements YukonRowCallbackHandler {
        ImmutableSortedMap.Builder<Double, Double> entries = new ImmutableSortedMap.Builder<Double, Double>(Ordering.natural());
        
        @Override
        public void processRow(YukonResultSet rs) throws SQLException {
            entries.put(rs.getDouble("Key"), rs.getDouble("Value"));
        }
    }

    // Setting up SimpleTableAccessTemplate objects.
    @PostConstruct
    public void init() {
        formulaInsertTemplate = new SimpleTableAccessTemplate<Formula>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(formulaTableName)
                .setFieldMapper(baseFormulaFieldMapper)
                .setPrimaryKeyField("FormulaId")
                .setPrimaryKeyValidOver(0);
        functionInsertTemplate = new SimpleTableAccessTemplate<FormulaFunction>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(functionTableName)
                .setFieldMapper(functionFieldMapper)
                .setPrimaryKeyField("FormulaFunctionId")
                .setPrimaryKeyValidOver(0);
        lookupTableInsertTemplate = new SimpleTableAccessTemplate<FormulaLookupTable>(yukonJdbcTemplate, nextValueHelper)
                .setTableName(lookupTableName)
                .setFieldMapper(lookupTableFieldMapper)
                .setPrimaryKeyField("FormulaLookupTableId")
                .setPrimaryKeyValidOver(0);
    }
}
