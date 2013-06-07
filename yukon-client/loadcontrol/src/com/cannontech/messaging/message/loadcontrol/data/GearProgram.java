package com.cannontech.messaging.message.loadcontrol.data;

import java.util.List;


public interface GearProgram {
    Integer getCurrentGearNumber();

    void setCurrentGearNumber(Integer newCurrentGearNumber);

    ProgramDirectGear getCurrentGear();

    List<ProgramDirectGear> getDirectGearVector();

    void setDirectGearVector(List<ProgramDirectGear> newDirectGearVector);
}
