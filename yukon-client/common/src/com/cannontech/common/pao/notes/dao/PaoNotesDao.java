package com.cannontech.common.pao.notes.dao;

import java.util.List;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.YukonPao;
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
     * Delete the note with the given noteId from the PaoNote table.
     */
    void delete(int noteId);

    List<PaoNotesSearchResult> findMostRecentNotes(int paoId, int numOfNotes);
    
    SearchResults<PaoNotesSearchResult> getAllNotesByPaoId(int paoId);
    /**
     * @param sortBy is nullable default sort will be by Device Name, then Last Date.
     * @param direction is nullable
     * @param paging is nullable
     */
    SearchResults<PaoNotesSearchResult> getAllNotesByFilter(PaoNotesFilter filter, SortBy sortBy,
                                                Direction direction, PagingParameters paging);

    PaoNote getNote(int noteId);
    
    /**
     * This method returns the count of notes for the pao object.
     * 
     * @param paoId
     * @return note count for the pao object
     */
    int getNoteCount(int paoId);

    /**
     * This method returns a list of YukonPao implementations that were passed into the method and
     * have notes associated with them.
     * 
     * @param list of YukonPao implementations
     * @return list of YukonPao implementations that have notes associated with them
     */
    <T extends YukonPao> List<T> getPaosWithNotes(List<T> paos);

    /**
     * This method returns a list of paoIds that were passed into the method and have notes
     * associated with them.
     * 
     * @param list of paoIds
     * @return list of paoIds that have notes associated with them
     */
    List<Integer> getPaoIdsWithNotes(List<Integer> paoIds);

}
