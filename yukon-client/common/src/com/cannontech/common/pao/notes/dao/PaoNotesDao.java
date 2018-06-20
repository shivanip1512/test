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
    
    /**
     * Add a new PaoNote to the PaoNotes table. Includes NoteId, PaObjectId, NoteText, Status, 
     * CreatorUserName, and CreationDate. Excludes EditorUserName and EditDate
     */
    int create(PaoNote note, LiteYukonUser user);
    
    /**
     * Update the note's NoteText, Status ('E'), EditorUserName, and EditDate.
     */
    int edit(PaoNote note, LiteYukonUser user);
    
    /**
     * Delete the note by noteId. This will not remove the
     * note from the database. Instead it will update the
     * status of the note to 'D' as well as the EditDate and
     * EditUsername to reflect the deletion event instead of
     * the last edit.
     */
    int delete(int noteId, LiteYukonUser user);
    
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
