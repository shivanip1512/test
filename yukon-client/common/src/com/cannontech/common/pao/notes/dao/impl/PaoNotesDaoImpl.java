package com.cannontech.common.pao.notes.dao.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.common.util.Range;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotesDaoImpl implements PaoNotesDao {

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
    public List<PaoNotesDao> findMostRecentNotes(int paoId, int numOfNotes) {
        return null;
    }

    @Override
    public SearchResults<PaoNotesDao> findAllNotesByPaoId(int paoId) {
        return null;
    }

    @Override
    public SearchResults<PaoNotesDao> findAllNotesByPaoId(Set<Integer> paoIds, String text,
                                                          Range<Date> dateRange,
                                                          LiteYukonUser creator) {
        return null;
    }

}
