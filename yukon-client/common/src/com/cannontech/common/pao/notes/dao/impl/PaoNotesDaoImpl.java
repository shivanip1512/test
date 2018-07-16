package com.cannontech.common.pao.notes.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.Instant;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.notes.PaoNoteStatus;
import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoNotesDaoImpl implements PaoNotesDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    
    private final YukonRowMapper<PaoNotesSearchResult> paoNotesRowMapper = new YukonRowMapper<PaoNotesSearchResult>() {

        @Override
        public PaoNotesSearchResult mapRow(YukonResultSet rs) throws SQLException {
            PaoNote paoNote = new PaoNote();
            paoNote.setNoteId(rs.getInt("NoteId"));
            paoNote.setPaoId(rs.getInt("PAObjectId"));
            paoNote.setNoteText(rs.getString("NoteText"));
            paoNote.setStatus(rs.getEnum("Status", PaoNoteStatus.class));
            paoNote.setCreateUserName(rs.getString("CreateUserName"));
            paoNote.setCreateDate(rs.getInstant("CreateDate"));
            paoNote.setEditUserName(rs.getString("EditUserName"));
            paoNote.setEditDate(rs.getInstant("EditDate"));
            
            PaoNotesSearchResult row = new PaoNotesSearchResult();
            row.setPaoNote(paoNote);
            
            if (rs.hasColumn("PAOName")) {
                row.setPaoName(rs.getString("PAOName"));
            }
            
            if (rs.hasColumn("Type")) {
                row.setPaoType(rs.getEnum("Type", PaoType.class));
            }
            
            return row;
        }
        
    };
    
    private int getNextPaoNoteId() {
        return nextValueHelper.getNextValue("PaoNote");
    }
    
    @Override
    public int create(PaoNote note, LiteYukonUser user) {
        int noteId = getNextPaoNoteId();
        
        SqlStatementBuilder insertSql = new SqlStatementBuilder();
        SqlParameterSink sink = insertSql.insertInto("PaoNote");
        sink.addValue("NoteId", noteId);
        sink.addValue("PaObjectId", note.getPaoId());
        sink.addValue("NoteText", note.getNoteText());
        sink.addValue("Status", PaoNoteStatus.CREATED);
        sink.addValue("CreateUserName", user.getUsername());
        sink.addValue("CreateDate", new Instant());
        yukonJdbcTemplate.update(insertSql);
        
        return noteId;
    }

    @Override
    public int edit(PaoNote note, LiteYukonUser user) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink sink = updateSql.update("PaoNote");
        sink.addValue("NoteText", note.getNoteText());
        sink.addValue("Status", PaoNoteStatus.EDITED);
        sink.addValue("EditUserName", user.getUsername());
        sink.addValue("EditDate", new Instant());
        updateSql.append("WHERE NoteId").eq(note.getNoteId());
        
        yukonJdbcTemplate.update(updateSql);
        return note.getNoteId();
    }

    @Override
    public int delete(int noteId, LiteYukonUser user) {
        SqlStatementBuilder updateSql = new SqlStatementBuilder();
        SqlParameterSink sink = updateSql.update("PaoNote");
        sink.addValue("Status", PaoNoteStatus.DELETED);
        sink.addValue("EditUserName", user.getUsername());
        sink.addValue("EditDate", new Instant());
        updateSql.append("WHERE NoteId").eq(noteId);
        
        yukonJdbcTemplate.update(updateSql);
        return noteId;
    }

    @Override
    public List<PaoNotesSearchResult> findMostRecentNotes(int paoId, int numOfNotes) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT NoteId, PAObjectId, NoteText, Status, CreateUserName, CreateDate, EditUserName, EditDate");
        sql.append("FROM PaoNote");
        sql.append("WHERE PAObjectId").eq(paoId);
        sql.append("AND Status").neq_k(PaoNoteStatus.DELETED);
        sql.append("ORDER BY COALESCE(EditDate, CreateDate) DESC");
        
        return yukonJdbcTemplate.queryForLimitedResults(sql, paoNotesRowMapper, numOfNotes);
    }

    @Override
    public SearchResults<PaoNotesSearchResult> getAllNotesByPaoId(int paoId) {
        PaoNotesFilter filter = new PaoNotesFilter();
        filter.setPaoIds(Collections.singleton((Integer)paoId));
        return getAllNotesByFilter(filter,
                                    SortBy.CREATE_DATE,
                                    Direction.desc,
                                    PagingParameters.EVERYTHING);
    }

    @Override
    public SearchResults<PaoNotesSearchResult> getAllNotesByFilter(PaoNotesFilter filter,
                                                          SortBy sortBy,
                                                          Direction direction,
                                                          PagingParameters paging) {
        
        if (direction == null) {
            direction = Direction.desc;
        }
        if (paging == null) {
            paging = PagingParameters.of(25, 1);
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT pn.NoteId, pn.PaObjectId, pn.NoteText, pn.Status, pn.CreateUserName, pn.CreateDate, pn.EditUserName, pn.EditDate, ypo.PAOName, ypo.Type");
        sql.append(getAllNotesSql(filter));
        sql.append("ORDER BY").append(getOrderBy(sortBy, direction));
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<PaoNotesSearchResult> rse = new PagingResultSetExtractor<>(start, count, paoNotesRowMapper);
        yukonJdbcTemplate.query(sql, rse);

        SearchResults<PaoNotesSearchResult> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getAllNotesByFilterCount(filter));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }
        
    private SqlStatementBuilder getOrderBy(SortBy sortBy, Direction direction) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        if (sortBy == null) {
            sql.append("PAOName ASC, COALESCE(EditDate, CreateDate)").append(direction);
        } else {
            sql.append(sortBy.getDbString()).append(direction);
        }
        return sql;
    }
    
    private int getAllNotesByFilterCount(PaoNotesFilter filter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append(getAllNotesSql(filter));
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    private SqlStatementBuilder getAllNotesSql(PaoNotesFilter filter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM PaoNote pn");
        sql.append("JOIN YukonPaobject ypo ON pn.PaobjectId = ypo.PAObjectID");
        sql.append("WHERE 1=1");
        if (filter.getPaoIds() != null) {
            sql.append("AND pn.PaObjectId").in(filter.getPaoIds());
        }
        if (StringUtils.isNotEmpty(filter.getText())) {
            sql.append("AND UPPER(pn.NoteText)").contains(filter.getText().toUpperCase());
        }
        if (StringUtils.isNotEmpty(filter.getUser())) {
            sql.append("AND pn.CreateUserName").eq(filter.getUser());
        }
        if (filter.getStartDate() != null) {
            sql.append("AND pn.CreateDate").gt(filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            sql.append("AND pn.CreateDate").lte(filter.getEndDate());
        }
        sql.append("AND pn.Status").neq_k(PaoNoteStatus.DELETED);
        return sql;
    }
}
