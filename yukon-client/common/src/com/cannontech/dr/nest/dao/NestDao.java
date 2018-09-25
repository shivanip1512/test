package com.cannontech.dr.nest.dao;

import java.util.List;

import com.cannontech.dr.nest.model.NestSync;
import com.cannontech.dr.nest.model.NestSyncDetail;

public interface NestDao {

    void saveSyncDetails(List<NestSyncDetail> details);

    void saveSyncInfo(NestSync sync);
}
