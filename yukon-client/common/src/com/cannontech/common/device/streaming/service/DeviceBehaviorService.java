package com.cannontech.common.device.streaming.service;

import java.util.List;

import com.cannontech.common.device.streaming.model.Behavior;
import com.cannontech.common.device.streaming.model.BehaviorType;
import com.cannontech.common.device.streaming.model.VerificationInfo;

public interface DeviceBehaviorService {

    void assignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);

    void unassignBehavior(int behaviorId, BehaviorType type, List<Integer> deviceIds);

    VerificationInfo verify(Behavior behavior, List<Integer> deviceIds);

    VerificationInfo verify(int behaviorId, List<Integer> deviceIds);
}
