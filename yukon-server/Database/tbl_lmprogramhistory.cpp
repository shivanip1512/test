/*-----------------------------------------------------------------------------*
*
* File:   tbl_lmprogramhistory
*
* Date:   12/8/2008
* 
* Copyright (c) 2008 Cooper Industries, All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/reader.h>
#include <rw/db/db.h>
#include <rw/db/dbase.h>
#include <rw/db/table.h>
#include <rw/db/datetime.h>


#include "tbl_lmprogramhistory.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "logger.h"

#include "rwutil.h"

using std::transform;


CtiTableLMProgramHistory::CtiTableLMProgramHistory(long progHistID, long gearHistID, long program, long gear, LMHistoryActions action,
                                                   string programName, string reason, string user, string gearName,
                                                   CtiTime time) :
_lmProgramHistID(progHistID),
_lmGearHistID(gearHistID),
_programID(program),
_gearID(gear),
_action(action),
_programName(programName),
_reason(reason),
_user(user),
_gearName(gearName),
_time(time)
{
}

CtiTableLMProgramHistory::CtiTableLMProgramHistory(const CtiTableLMProgramHistory &aRef)
{
    *this = aRef;
}

CtiTableLMProgramHistory& CtiTableLMProgramHistory::operator=(const CtiTableLMProgramHistory &aRef)
{
    if(this != &aRef)
    {
        _lmProgramHistID  = aRef._lmProgramHistID;
        _lmGearHistID     = aRef._lmGearHistID;
        _programID        = aRef._programID;
        _gearID           = aRef._gearID;
        _action           = aRef._action;
        _programName      = aRef._programName;
        _reason           = aRef._reason;
        _user             = aRef._user;
        _gearName         = aRef._gearName;
        _time             = aRef._time;
    }

    return *this;
}

RWDBStatus CtiTableLMProgramHistory::Insert()
{
    CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
    RWDBConnection conn = getConnection();

    if( _action == Start )
    {
        RWDBTable table = getDatabase().table( "LMProgramHistory" );
        RWDBInserter inserter = table.inserter();

        inserter << _lmProgramHistID << _programName << _programID;
        
        if( RWDBStatus::ErrorCode err = ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - error \"" << err << "\" while inserting in LMProgramHistory **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    RWDBTable table = getDatabase().table( "LMProgramGearHistory" );
    RWDBInserter inserter = table.inserter();

    inserter <<
    _lmGearHistID <<
    _lmProgramHistID <<
    _time <<
    getStrFromAction(_action) <<
    _user <<
    _gearName <<
    _gearID <<
    _reason;

    if( RWDBStatus::ErrorCode err = ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint - error \"" << err << "\" while inserting in LMProgramGearHistory **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return inserter.status();
}

string CtiTableLMProgramHistory::getStrFromAction(long action)
{
    switch( action )
    {
        case Start:
            return "Start";
        case Stop:
            return "Stop";
        case GearChange:
            return "Gear Change";
        default:
            return "Unknown";
    }
}
