package com.cannontech.common.pao.notes.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.notes.dao.PaoNotesDao;
import com.cannontech.common.pao.notes.model.PaoNote;
import com.cannontech.common.pao.notes.service.PaoNotesService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class PaoNotesServiceImpl implements PaoNotesService {
    
    @Autowired private PaoNotesDao paoNotesDao;

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

}
