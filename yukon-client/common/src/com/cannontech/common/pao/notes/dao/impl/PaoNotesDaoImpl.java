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
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.incrementer.NextValueHelper;

public class PaoNotesDaoImpl implements PaoNotesDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;

    
    private final YukonRowMapper<PaoNote> paoNotesRowMapper = new YukonRowMapper<PaoNote>() {

        @Override
        public PaoNote mapRow(YukonResultSet rs) throws SQLException {
            PaoNote row = new PaoNote();
            row.setNoteId(rs.getInt("NoteId"));
            row.setPaoId(rs.getInt("PAObjectId"));
            row.setNoteText(rs.getString("NoteText"));
            row.setStatus(rs.getEnum("Status", PaoNoteStatus.class));
            row.setCreateUserName(rs.getString("CreateUsername"));
            row.setCreateDate(rs.getInstant("CreateDate"));
            row.setEditUserName(rs.getString("EditUserName"));
            row.setEditDate(rs.getInstant("EditDate"));
            LiteYukonPAObject yukonPao = new LiteYukonPAObject(rs.getInt("PAObjectId"),
                                                               rs.getString("PAOName"),
                                                               rs.getEnum("Type", PaoType.class),
                                                               rs.getString("Description"),
                                                               rs.getString("DisableFlag"));
            row.setLiteYukonPAObject(yukonPao);
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
        sink.addValue("CreateUsername", note.getCreateUserName());
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
        sink.addValue("EditUsername", user.getUsername());
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
        sink.addValue("EditUsername", user.getUsername());
        sink.addValue("EditDate", new Instant());
        updateSql.append("WHERE NoteId").eq(noteId);
        
        yukonJdbcTemplate.update(updateSql);
        return noteId;
    }

    @Override
    public List<PaoNote> findMostRecentNotes(int paoId, int numOfNotes) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT NoteId, PAObjectId, NoteText, Status, CreateUserName, CreateDate, EditUserName, EditDate");
        sql.append("FROM PaoNote");
        sql.append("WHERE PAObjectId").eq(paoId);
        sql.append("AND Status").neq_k(PaoNoteStatus.DELETED);
        sql.append("ORDER BY COALESCE(EditDate, CreateDate) DESC");
        
        return yukonJdbcTemplate.queryForLimitedResults(sql, paoNotesRowMapper, numOfNotes);
    }

    @Override
    public SearchResults<PaoNote> getAllNotesByPaoId(int paoId) {
        PaoNotesFilter filter = new PaoNotesFilter();
        filter.setPaoIds(Collections.singleton((Integer)paoId));
        return getAllNotesByFilter(filter,
                                    null,
                                    null,
                                    null);
    }

    @Override
    public SearchResults<PaoNote> getAllNotesByFilter(PaoNotesFilter filter,
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
        sql.append("SELECT pn.NoteId, pn.PaObjectId, pn.NoteText, pn.Status, pn.CreateUserName, pn.CreateDate, pn.EditUserName, pn.EditDate, ypo.PAOName, ypo.Type, ypo.Description, ypo.DisableFlag");
        sql.append(getAllNotesSql(filter));
        sql.append("ORDER BY").append(getOrderBy(sortBy, direction));
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<PaoNote> rse = new PagingResultSetExtractor<>(start, count, paoNotesRowMapper);
        yukonJdbcTemplate.query(sql, rse);

        SearchResults<PaoNote> searchResults = new SearchResults<>();
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
        if (filter.getUser() != null) {
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
