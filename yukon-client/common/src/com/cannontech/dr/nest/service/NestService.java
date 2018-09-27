package com.cannontech.dr.nest.service;

import com.cannontech.dr.nest.model.NestUploadInfo;

public interface NestService {

    void control();

    NestUploadInfo updateGroup(String accountNumber, String newGroup);

    NestUploadInfo dissolveAccountWithNest(String accountNumber);
}
