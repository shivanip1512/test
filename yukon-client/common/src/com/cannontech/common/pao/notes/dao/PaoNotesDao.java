package com.cannontech.common.pao.notes.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PaoNotesDao {
    
    public enum SortBy {
        PAO_ID("PaoId"),
        TEXT("Text"),
        DATE("Date"),
        USERNAME("Username"),;
        
        private SortBy(String dbString) {
            this.dbString = dbString;
        }
        
        private final String dbString;
        
        public String getDbString() {
            return dbString;
        }

    }
    
    void create(int paoId, String text, LiteYukonUser user);
    
    void edit(int noteId, String text, LiteYukonUser user);
    
    void delete(int noteId);
    
    /**
     * @param paoId
     * @param numOfNotes - this method will return up to this many notes
     * @return
     */
    List<PaoNote> findMostRecentNotes(int paoId, int numOfNotes);
    
    //TODO consult with UI developer about sorting the results in SQL possiby
    SearchResults<PaoNote> findAllNotesByPaoId(int paoId);
    
    SearchResults<PaoNote> findAllNotesByFilter(PaoNotesFilter filter, SortBy sortBy,
                                                Direction direction, PagingParameters paging);

}
