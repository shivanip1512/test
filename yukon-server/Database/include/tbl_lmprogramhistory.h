/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmprogramhistory
*
* Date:   12/8/2008
* 
* Copyright (c) 2008 Cooper Industries, All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning( disable : 4786)
#ifndef __TBL_LMPROGHIST_H__
#define __TBL_LMPROGHIST_H__

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
    string  _programName;
    string  _reason;
    string  _user;
    string  _gearName;
    CtiTime _time;

    CtiTableLMProgramHistory() {};

    void validateData();

    string getStrFromAction(long action);

    static long getNextGearHistId();
public:

    enum LMHistoryActions
    {
        Start,
        GearChange,
        Stop
    };

    CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                             string programName, string reason, string user, string gearName,
                             CtiTime time);
    
    CtiTableLMProgramHistory(const CtiTableLMProgramHistory &aRef);

    CtiTableLMProgramHistory& operator=(const CtiTableLMProgramHistory &aRef);

    bool Insert();

    static long getNextProgramHistId();
};

#endif
