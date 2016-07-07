package com.cannontech.common.device.streaming.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.streaming.dao.DeviceBehaviorDao;
import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.model.VerificationInfo;
import com.cannontech.common.device.streaming.service.DeviceBehaviorService;

public class DeviceBehaviorServiceImpl implements DeviceBehaviorService {

    @Autowired private DeviceBehaviorDao deviceBehaviorDao;
    
    @Override
    public void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds) {
        deviceBehaviorDao.assignBehavior(behaviorId, type, deviceIds);
        deviceBehaviorDao.deleteUnusedBehaviors();
        // send message to porter
    }

    @Override
    public void unassignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds) {
        deviceBehaviorDao.unassignBehavior(behaviorId, type, deviceIds);
        deviceBehaviorDao.deleteUnusedBehaviors();
        // send message to porter
    }

    // configuration type = new, click "next" button
    @Override
    public VerificationInfo verify(Behavior behavior, List<Integer> deviceIds) {
        VerificationInfo info = new VerificationInfo();
        return info;
    }

    // configuration type = existing, click "next" button
    @Override
    public VerificationInfo verify(int behaviorId, List<Integer> deviceIds) {
        VerificationInfo info = new VerificationInfo();
        Behavior behavior = deviceBehaviorDao.getBehaviorById(behaviorId);
        return info;
    }
}
