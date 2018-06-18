package com.cannontech.common.pao.notes.dao;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonUser;

public interface PaoNotesDao {
    
    void create(int paoId, String text, LiteYukonUser user);
    
    void edit(int noteId, String text, LiteYukonUser user);
    
    void delete(int noteId);
    
    /**
     * @param paoId
     * @param numOfNotes - this method will return up to this many notes
     * @return
     */
    List<PaoNotesDao> findMostRecentNotes(int paoId, int numOfNotes);
    
    //TODO consult with UI developer about sorting the results in SQL possiby
    SearchResults<PaoNotesDao> findAllNotesByPaoId(int paoId);
    
    /**
     * 
     * @param paoIds
     * @param text
     * @param dateRange
     * @param creator
     * @return
     */
    SearchResults<PaoNotesDao> findAllNotesByPaoId(Set<Integer> paoIds,
                                                   String text,
                                                   Range<Date> dateRange,
                                                   LiteYukonUser creator);

}
