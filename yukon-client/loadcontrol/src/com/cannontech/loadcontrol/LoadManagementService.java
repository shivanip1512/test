package com.cannontech.loadcontrol;

import java.util.Date;

public interface LoadManagementService {

    void startProgram(int programId, Date startTime, Date stopTime);

    void stopProgram(int programId);

    void changeProgramStop(int programId, Date stopTime);

}
