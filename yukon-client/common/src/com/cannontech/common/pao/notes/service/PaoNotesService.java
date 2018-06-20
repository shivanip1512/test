package com.cannontech.common.pao.notes.service;

import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.dao.PaoNotesDao.SortBy;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PaoNotesService {
    
    /**
     * Create a new note and return the new NoteId
     */
    int create(PaoNote note, LiteYukonUser user);
    
    /**
     * Create a new note and return the NoteId
     */
    int edit(PaoNote note, LiteYukonUser user);
    
    /**
     * "Delete" the PaoNote associated with the noteId. The note remains
     * in the database but it's status is now 'D'.
     */
    void delete(int noteId, LiteYukonUser user);
    /**
     * 
     * @param filter will be used to determine how to query for List of PaoIds, if deviceGroup is not null
     * deviceGroup will be used to obtain list of paoIds and set filter.paoIds. Otherwise if deviceGroup is null
     * paoIds should be explicitly set by the controller.
     * @param sortBy nullable defaults to sort by Device Name, then by Last Edit Date
     * @param direction nullable defaults to Desc
     * @param paging nullable Defaults to 25 pages per item and on the first page
     * @return
     */
    SearchResults<PaoNote> getAllNotesByFilter(PaoNotesFilter filter, SortBy sortBy,
                                               Direction direction, PagingParameters paging);
    
    
}
