package com.cannontech.dr.nest.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.service.NestCommunicationService;
import com.cannontech.dr.nest.service.NestSyncService;

public class NestSyncServiceImpl implements NestSyncService{
    
    @Autowired private NestCommunicationService nestCommunicationService;
    
    //called by scheduled job or UI "force" button
    @Override
    public void sync() {
        Date date = new Date();
        List<NestExisting> existing = nestCommunicationService.downloadExisting(date);
        List<NestExisting> modified = new ArrayList<>();
        nestCommunicationService.uploadExisting(existing, date);
    }
}
