package com.cannontech.dr.program.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.DisplayablePaoBase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoTag;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.impl.PaoIdentifierRowMapper;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.dr.program.dao.ProgramDao;

public class ProgramDaoImpl implements ProgramDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private PaoDefinitionDao paoDefinitionDao;

    private final static PaoIdentifierRowMapper paoIdentifierRowMapper = new PaoIdentifierRowMapper();

    private final static YukonRowMapper<DisplayablePao> programRowMapper =
        new YukonRowMapper<DisplayablePao>() {
        @Override
        public DisplayablePao mapRow(YukonResultSet rs)
                throws SQLException {
        	int paobjectId = rs.getInt("paObjectId");
        	PaoType paoType = rs.getEnum("type", PaoType.class);
            PaoIdentifier paoId = new PaoIdentifier(paobjectId, paoType);
            DisplayablePao retVal = new DisplayablePaoBase(paoId,
                                                           rs.getString("paoName"));
            return retVal;
        }};

    @Override
    public DisplayablePao getProgram(int programId) {
    	Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
    	
    	SqlStatementBuilder sql = new SqlStatementBuilder();
    	sql.append("SELECT paObjectId, paoName, type FROM yukonPAObject");
        sql.append("WHERE type").in(paoTypes);
        sql.append("AND paObjectId").eq(programId);
    	
        return yukonJdbcTemplate.queryForObject(sql, programRowMapper);
    }

    @Override
    public List<PaoIdentifier> getAllProgramPaoIdentifiers() {
        Set<PaoType> paoTypes = paoDefinitionDao.getPaoTypesThatSupportTag(PaoTag.LM_PROGRAM);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT paObjectId, type FROM yukonPAObject");
        sql.append("WHERE type").in(paoTypes);
        
        List<PaoIdentifier> paoIdentifiers =  yukonJdbcTemplate.query(sql, paoIdentifierRowMapper);
        
        return paoIdentifiers;
    }
    
}
