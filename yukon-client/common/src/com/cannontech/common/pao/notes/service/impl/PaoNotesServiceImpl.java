package com.cannontech.common.pao.notes.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.pao.notes.dao.PaoNotesDao.SortBy;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.search.result.model.PaoNotesSearchResult;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.common.search.result.SearchResults;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotesServiceImpl implements PaoNotesService {
    
    @Autowired private PaoNotesDao paoNotesDao;
    @Autowired private DeviceGroupService deviceGroupService;
    
    @Override
    public int create(PaoNote note, LiteYukonUser user) {
        return paoNotesDao.create(note, user);
    }

    @Override
    public int edit(PaoNote note, LiteYukonUser user) {
        return paoNotesDao.edit(note, user);
    }

    @Override
    public void delete(int noteId) {
        paoNotesDao.delete(noteId);
    }
    
    @Override
    public SearchResults<PaoNotesSearchResult> getAllNotesByFilter(PaoNotesFilter filter, 
                                                      SortBy sortBy, 
                                                      Direction direction, 
                                                      PagingParameters paging) {
        
        if (filter.getDeviceGroups() != null) {
            filter.setPaoIds(deviceGroupService.getDeviceIds(filter.getDeviceGroups()));
        }
        return paoNotesDao.getAllNotesByFilter(filter, sortBy, direction, paging);
    }

    @Override 
    public SearchResults<PaoNotesSearchResult> getAllNotesByPaoId(int paoId) {
        return paoNotesDao.getAllNotesByPaoId(paoId);
    }
    
    @Override
    public List<PaoNotesSearchResult> findMostRecentNotes(int paoId, int numOfNotes) {
        return paoNotesDao.findMostRecentNotes(paoId, numOfNotes);
    }

    @Override
    public boolean hasNotes(int paoId) {
        return !paoNotesDao.getAllNotesByPaoId(paoId).getResultList().isEmpty();
    }

}
