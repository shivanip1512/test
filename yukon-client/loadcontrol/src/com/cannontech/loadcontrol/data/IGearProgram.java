package com.cannontech.loadcontrol.data;

import java.util.List;

public interface IGearProgram
{
	Integer getCurrentGearNumber();
	void setCurrentGearNumber(Integer newCurrentGearNumber);

    LMProgramDirectGear getCurrentGear();

	List<LMProgramDirectGear> getDirectGearVector();
	void setDirectGearVector(List<LMProgramDirectGear> newDirectGearVector);
}
