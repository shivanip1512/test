#pragma once

#include "row_reader.h"


#include "dbmemobject.h"
#include "dbaccess.h"
#include "yukon.h"
#include "CtiTime.h"

class IM_EX_CTIYUKONDB CtiTableLMProgramHistory : public CtiMemDBObject
{
private:
    long    _lmProgramHistID;
    long    _programID;
    long    _gearID;
    long    _action; // Stored in database as a string
    std::string  _programName;
    std::string  _reason;
    std::string  _user;
    std::string  _gearName;
    CtiTime _time;
    std::string  _origin;

    CtiTableLMProgramHistory() {};

    void validateData();

    std::string getStrFromAction(long action);

    static long getNextGearHistId();
public:

    enum LMHistoryActions
    {
        Start,
        GearChange,
        Stop
    };

    CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                             std::string programName, std::string reason, std::string user, std::string gearName,
                             CtiTime time, std::string origin);
    
    CtiTableLMProgramHistory(const CtiTableLMProgramHistory &aRef);

    CtiTableLMProgramHistory& operator=(const CtiTableLMProgramHistory &aRef);

    bool Insert();

    static long getNextProgramHistId();
};
