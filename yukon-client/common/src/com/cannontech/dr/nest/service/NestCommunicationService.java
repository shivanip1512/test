package com.cannontech.dr.nest.service;

import java.util.Date;
import java.util.List;

import com.cannontech.dr.nest.model.CriticalEvent;
import com.cannontech.dr.nest.model.NestEventId;
import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.StandardEvent;

public interface NestCommunicationService{

    List<NestExisting> downloadExisting(Date date);

    void uploadExisting(List<NestExisting> existing, Date date);

    NestEventId createStandardEvent(StandardEvent event);

    NestEventId createCriticalEvent(CriticalEvent event);

    boolean cancelEvent(String nestEventId);
}
