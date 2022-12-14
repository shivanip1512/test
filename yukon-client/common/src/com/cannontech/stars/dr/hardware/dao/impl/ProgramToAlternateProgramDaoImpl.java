package com.cannontech.stars.dr.hardware.dao.impl;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.FieldMapper;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.stars.dr.hardware.dao.ProgramToAlternateProgramDao;
import com.cannontech.stars.dr.hardware.model.ProgramToAlternateProgram;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.collect.Lists;

public class ProgramToAlternateProgramDaoImpl implements ProgramToAlternateProgramDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    private SimpleTableAccessTemplate<ProgramToAlternateProgram> programToAlternateProgramTemplate;

    private FieldMapper<ProgramToAlternateProgram> programToSeasonalProgramFieldMapper =
        new FieldMapper<ProgramToAlternateProgram>() {
            @Override
            public void extractValues(MapSqlParameterSource p, ProgramToAlternateProgram programToSeasonalProgram) {
                p.addValue("SeasonalProgramId", programToSeasonalProgram.getAlternateProgramId());
            }

            @Override
            public Number getPrimaryKey(ProgramToAlternateProgram programToSeasonalProgram) {
                return programToSeasonalProgram.getAssignedProgramId();
            }

            @Override
            public void setPrimaryKey(ProgramToAlternateProgram programToSeasonalProgram, int newId) {
                programToSeasonalProgram.setAssignedProgramId(newId);
            }
        };

    // Row Mappers
    private static YukonRowMapper<ProgramToAlternateProgram> ProgramToAlternateProgramRowMapper =
        new YukonRowMapper<ProgramToAlternateProgram>() {

            @Override
            public ProgramToAlternateProgram mapRow(YukonResultSet rs) throws SQLException {
                ProgramToAlternateProgram programToAlternateProgram = new ProgramToAlternateProgram();

                programToAlternateProgram.setAssignedProgramId(rs.getInt("AssignedProgramId"));
                programToAlternateProgram.setAlternateProgramId(rs.getInt("SeasonalProgramId"));

                return programToAlternateProgram;
            }
        };

    private static YukonRowMapper<ProgramToAlternateProgram> ProgramToAlternateProgramParameterizedRowMapper =
        new YukonRowMapper<ProgramToAlternateProgram>() {
            @Override
            public ProgramToAlternateProgram mapRow(YukonResultSet rs) throws SQLException {
                ProgramToAlternateProgram programToAlternateProgram = new ProgramToAlternateProgram();

                programToAlternateProgram.setAssignedProgramId(rs.getInt("AssignedProgramId"));
                programToAlternateProgram.setAlternateProgramId(rs.getInt("SeasonalProgramId"));

                return programToAlternateProgram;
            }
        };

    @PostConstruct
    public void init() throws Exception {
        programToAlternateProgramTemplate = new SimpleTableAccessTemplate<>(yukonJdbcTemplate, nextValueHelper);
        programToAlternateProgramTemplate.setTableName("ProgramToSeasonalProgram");
        programToAlternateProgramTemplate.setPrimaryKeyField("AssignedProgramId");
        programToAlternateProgramTemplate.setFieldMapper(programToSeasonalProgramFieldMapper);
        programToAlternateProgramTemplate.setPrimaryKeyValidOver(0);
    }

    @Override
    public void save(final ProgramToAlternateProgram programToAlternateProgram) {
        try {
            programToAlternateProgramTemplate.insert(programToAlternateProgram);
        } catch (DataIntegrityViolationException e) {
            programToAlternateProgramTemplate.update(programToAlternateProgram);
        }
    }

    @Override
    public void delete(int assignedProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM ProgramToSeasonalProgram");
        sql.append("WHERE AssignedProgramId").eq(assignedProgramId);

        yukonJdbcTemplate.update(sql);
    }

    @Override
    public List<ProgramToAlternateProgram> getByAssignedProgramId(Iterable<Integer> assignedProgramIds) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM ProgramToSeasonalProgram");
        sql.append("WHERE AssignedProgramId").in(assignedProgramIds);

        return yukonJdbcTemplate.query(sql, ProgramToAlternateProgramRowMapper);
    }

    @Override
    public List<ProgramToAlternateProgram> getByEitherAssignedProgramIdOrSeasonalProgramId(Iterable<Integer> programIds) {

        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            @Override
            public SqlFragmentSource generate(List<Integer> subList) {
                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT *");
                sql.append("FROM ProgramToSeasonalProgram");
                sql.append("WHERE AssignedProgramId").in(subList);
                sql.append("  OR SeasonalProgramId").in(subList);
                return sql;
            }
        };

        List<ProgramToAlternateProgram> retVal =
            template.query(sqlGenerator, programIds, ProgramToAlternateProgramParameterizedRowMapper);

        return retVal;
    }

    @Override
    public List<ProgramToAlternateProgram> getAll() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM ProgramToSeasonalProgram");

        return yukonJdbcTemplate.query(sql, ProgramToAlternateProgramRowMapper);
    }

    @Override
    public List<Integer> getAllAlternateProgramIds() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT SeasonalProgramId");
        sql.append("FROM ProgramToSeasonalProgram");

        return yukonJdbcTemplate.query(sql, TypeRowMapper.INTEGER);
    }
    
    @Override
    public int getAssignedProgramIdBySeasonalProgramId(int SeasonalProgramId) {
        int result = -1;
        try {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT DISTINCT AssignedProgramId");
            sql.append("FROM ProgramToSeasonalProgram");
            sql.append("WHERE SeasonalProgramId").eq(SeasonalProgramId);
            result = yukonJdbcTemplate.queryForInt(sql);
        } catch (EmptyResultDataAccessException e) {
            // returns -1 in case the program to be deleted is not linked with any other program or is not an
            // alternate enrolled program for some other.
            return result;
        }
        return result;
    }

    @Override
    public int getTotalNumberOfDevicesInSeasonalOptOuts(YukonEnergyCompany yukonEnergyCompany,
            List<Integer> assignedProgramIds) {
        List<Integer> seasonalOptOutProgramIds = null;
        if (CollectionUtils.isEmpty(assignedProgramIds)) {
            seasonalOptOutProgramIds =
                Lists.transform(getAll(), ProgramToAlternateProgram.ALTERNATE_PROGRAM_IDS_FUNCTION);
        } else {
            List<ProgramToAlternateProgram> programToAlternateProgram =
                getByEitherAssignedProgramIdOrSeasonalProgramId(assignedProgramIds);
            seasonalOptOutProgramIds =
                Lists.transform(programToAlternateProgram, ProgramToAlternateProgram.ALTERNATE_PROGRAM_IDS_FUNCTION);
        }

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT (CA.AccountId)");
        sql.append("FROM CustomerAccount CA");
        sql.append("  JOIN ECToAccountMapping ECTAM ON CA.AccountId = ECTAM.AccountId");
        sql.append("  JOIN LMHardwareControlGroup LMHCG ON (CA.AccountId = LMHCG.AccountId  AND "
            + " LMHCG.GroupEnrollStart IS NOT NULL AND " + " LMHCG.GroupEnrollStop IS NULL)");
        sql.append("WHERE ECTAM.EnergyCompanyId").eq_k(yukonEnergyCompany.getEnergyCompanyId());
        sql.append("  AND LMHCG.ProgramId").in(seasonalOptOutProgramIds);

        return yukonJdbcTemplate.queryForInt(sql);
    }
}