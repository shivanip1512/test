package com.cannontech.common.device.streaming.service;

import java.util.List;

import com.cannontech.common.device.streaming.model.BehaviorType;

public interface DeviceBehaviorService {

    void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);

    void unassignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);
}
