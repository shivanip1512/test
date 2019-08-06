#pragma once

#include "dlldefs.h"
#include "dbmemobject.h"
#include "CtiTime.h"

class IM_EX_CTIYUKONDB CtiTableLMProgramHistory : public CtiMemDBObject
{
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

    void validateEntry( std::string & entry, std::size_t maxLength );
    void validateData();

    std::string getStrFromAction(long action);

    static long getNextGearHistId();

public:

    enum LMHistoryActions
    {
        Start,
        Update,         // Re-Start
        GearChange,
        Stop
    };

    CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                             std::string programName, std::string reason, std::string user, std::string gearName,
                             CtiTime time, std::string origin);

    static CtiTableLMProgramHistory createStartHistory( long progHistID,
                                                        long program,
                                                        long gear,
                                                        LMHistoryActions action,
                                                        const std::string & programName,
                                                        const std::string & reason,
                                                        const std::string & user,
                                                        const std::string & gearName,
                                                        const CtiTime time,
                                                        const std::string & origin );
    
    static CtiTableLMProgramHistory createGenericHistory( long progHistID,
                                                          long program,
                                                          long gear,
                                                          LMHistoryActions action,
                                                          const std::string & programName,
                                                          const std::string & reason,
                                                          const std::string & user,
                                                          const std::string & gearName,
                                                          const CtiTime time );
    
    CtiTableLMProgramHistory(const CtiTableLMProgramHistory &aRef) = default;
    CtiTableLMProgramHistory& operator=(const CtiTableLMProgramHistory &aRef) = default;

    bool Insert();

    static long getNextProgramHistId();
};
