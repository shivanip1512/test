package com.cannontech.common.pao.notes.service;

import com.cannontech.common.pao.notes.model.PaoNote;
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

}
