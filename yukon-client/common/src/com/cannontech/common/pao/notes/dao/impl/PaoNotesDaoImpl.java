package com.cannontech.common.pao.notes.dao.impl;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.PagingResultSetExtractor;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotesDaoImpl implements PaoNotesDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    
    private final YukonRowMapper<PaoNote> paoNotesRowMapper = new YukonRowMapper<PaoNote>() {

        @Override
        public PaoNote mapRow(YukonResultSet rs) throws SQLException {
            PaoNote row = new PaoNote();
            row.setNoteId(rs.getInt("NoteId"));
            row.setPaObjectId(rs.getInt("PaObjectId"));
            row.setNoteText(rs.getString("NoteText"));
            row.setStatus(rs.getString("Status").charAt(0));
            row.setCreatorUserName(rs.getString("CreatorUserName"));
            row.setCreationDate(rs.getInstant("CreationDate"));
            row.setEditorUserName(rs.getString("EditorUserName"));
            row.setEditDate(rs.getInstant("EditDate"));
            return row;
        }
        
    };
    
    @Override
    public void create(int paoId, String text, LiteYukonUser user) {
    }

    @Override
    public void edit(int noteId, String text, LiteYukonUser user) {
    }

    @Override
    public void delete(int noteId) {
    }

    @Override
    public List<PaoNote> findMostRecentNotes(int paoId, int numOfNotes) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();

        sql.append("SELECT NoteId, PaObjectId, NoteText, Status, CreatorUserName, CreationDate, EditorUserName, EditDate");
        sql.append("FROM PaoNote");
        sql.append("WHERE PaObjectId").eq_k(paoId);
        sql.append("ORDER BY COALESCE(EditDate, CreationDate) DESC");
        
        return yukonJdbcTemplate.queryForLimitedResults(sql, paoNotesRowMapper, numOfNotes);
    }

    @Override
    public SearchResults<PaoNote> findAllNotesByPaoId(int paoId) {
        PaoNotesFilter filter = new PaoNotesFilter();
        filter.setPaoIds(Collections.singleton((Integer)paoId));
        return findAllNotesByFilter(filter,
                                    null,
                                    null,
                                    null);
    }

    @Override
    public SearchResults<PaoNote> findAllNotesByFilter(PaoNotesFilter filter,
                                                          SortBy sortBy,
                                                          Direction direction,
                                                          PagingParameters paging) {
        
        if (sortBy == null) {
            sortBy = SortBy.DATE;
        }
        if (direction == null) {
            direction = Direction.asc;
        }
        
        SqlStatementBuilder sql = getAllNotesSql(filter);
        
        sql.append("ORDER BY").append(sortBy.getDbString()).append(direction);
        
        int start = paging.getStartIndex();
        int count = paging.getItemsPerPage();
        
        PagingResultSetExtractor<PaoNote> rse = new PagingResultSetExtractor<>(start, count, paoNotesRowMapper);
        yukonJdbcTemplate.query(sql, rse);

        SearchResults<PaoNote> searchResults = new SearchResults<>();
        searchResults.setBounds(start, count, getAllNotesByFilterCount(filter));
        searchResults.setResultList(rse.getResultList());
        
        return searchResults;
    }
    
    private int getAllNotesByFilterCount(PaoNotesFilter filter) {
        SqlStatementBuilder sql = getAllNotesSql(filter);
        return yukonJdbcTemplate.queryForInt(sql);
    }
    
    private SqlStatementBuilder getAllNotesSql(PaoNotesFilter filter) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT NoteId, PaObjectId, NoteText, Status, CreatorUserName, CreationDate, EditorUserName, EditDate");
        sql.append("FROM PaoNote");
        sql.append("WHERE 1=1");
        if (filter.getPaoIds() != null) {
            sql.append("AND PaObjectId").in(filter.getPaoIds());
        }
        if (StringUtils.isNotEmpty(filter.getText())) {
            sql.append("AND NoteText").contains(filter.getText());
        }
        if (filter.getUser() != null) {
            sql.append("AND CreatorUserName").eq(filter.getUser());
        }
        if (filter.getStartDate() != null) {
            sql.append("CreationDate").gt(filter.getStartDate());
        }
        if (filter.getEndDate() != null) {
            sql.append("CreationDate").lte(filter.getEndDate());
        }
        
        return sql;
    }

}
