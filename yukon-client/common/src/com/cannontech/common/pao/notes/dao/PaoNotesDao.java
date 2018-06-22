package com.cannontech.common.pao.notes.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PaoNotesDao {
    
    public enum SortBy {
        NOTE_TEXT("NoteText"),
        CREATE_DATE("CreateDate"),
        CREATE_USERNAME("CreateUsername"),
        EDIT_USERNAME("EditUsername"),
        EDIT_DATE("EditDate"),
        PAO_NAME("PAOName"),
        PAO_TYPE("Type");
        
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

    List<PaoNotesSearchResult> findMostRecentNotes(int paoId, int numOfNotes);
    
    SearchResults<PaoNotesSearchResult> getAllNotesByPaoId(int paoId);
    /**
     * @param sortBy is nullable default sort will be by Device Name, then Last Date.
     * @param direction is nullable
     * @param paging is nullable
     */
    SearchResults<PaoNotesSearchResult> getAllNotesByFilter(PaoNotesFilter filter, SortBy sortBy,
                                                Direction direction, PagingParameters paging);

}
