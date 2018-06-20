package com.cannontech.common.pao.notes.service.impl;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.model.Direction;
import com.cannontech.common.model.PagingParameters;
import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.pao.notes.dao.PaoNotesDao.SortBy;
import com.cannontech.common.pao.notes.filter.model.PaoNotesFilter;
import com.cannontech.common.pao.notes.model.PaoNote;
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
    public void delete(int noteId, LiteYukonUser user) {
        paoNotesDao.delete(noteId, user);
    }
    
    @Override
    public SearchResults<PaoNote> getAllNotesByFilter(PaoNotesFilter filter, 
                                                      SortBy sortBy, 
                                                      Direction direction, 
                                                      PagingParameters paging) {
        
        if (filter.getDeviceGroup() != null) {
            filter.setPaoIds(deviceGroupService.getDeviceIds(Collections.singleton(filter.getDeviceGroup())));
        }
        return paoNotesDao.findAllNotesByFilter(filter, sortBy, direction, paging);
    }

}
