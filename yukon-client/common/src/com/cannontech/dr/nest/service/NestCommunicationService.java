package com.cannontech.dr.nest.service;

import java.util.Date;
import java.util.List;

import com.cannontech.dr.nest.model.NestExisting;
import com.cannontech.dr.nest.model.NestPending;

public interface NestCommunicationService{

    List<NestPending> downloadPending(Date date);

    List<NestExisting> downloadExisting(Date date);

    void uploadExisting(List<NestExisting> existing, Date date);
}
