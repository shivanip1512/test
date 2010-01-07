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

long CurrentLMProgramHistoryId = 0;
long CurrentLMGearHistoryId    = 0;

CtiTableLMProgramHistory::CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                                                   string programName, string reason, string user, string gearName,
                                                   CtiTime time) :
_lmProgramHistID(progHistID),
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

    validateData();

    if( _action == Start )
    {
        RWDBTable table = getDatabase().table( "LMProgramHistory" );
        RWDBInserter inserter = table.inserter();

        inserter << _lmProgramHistID << _programName << _programID;
        
        if( RWDBStatus::ErrorCode err = ExecuteInserter(conn,inserter,__FILE__,__LINE__).errorCode() )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - error \"" << err << "\" while inserting in LMProgramHistory **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            //We had an error, force a reload
            CurrentLMProgramHistoryId = 0;
            CurrentLMGearHistoryId    = 0;
        }
    }

    long gearHistId = getNextGearHistId();

    RWDBTable table = getDatabase().table( "LMProgramGearHistory" );
    RWDBInserter inserter = table.inserter();

    inserter <<
    gearHistId <<
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

        //We had an error, force a reload
        CurrentLMProgramHistoryId = 0;
        CurrentLMGearHistoryId    = 0;
    }

    return inserter.status();
}

// Get the next program id from local store or the database
// Not thread safe.
long CtiTableLMProgramHistory::getNextProgramHistId()
{
    if( CurrentLMProgramHistoryId != 0 )
    {
        CurrentLMProgramHistoryId++;
        return CurrentLMProgramHistoryId;
    }
    else
    {
        // Not yet initialized. Default to 1 and try to load from database.
        CurrentLMProgramHistoryId = 1;
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable programTable = getDatabase().table( "LMProgramHistory" );
        RWDBSelector selector = conn.database().selector();

        selector << rwdbMax(programTable["LMProgramHistoryId"]);
        selector.from(programTable);

        RWDBReader rdr = selector.reader(conn);

        if( rdr() )
        {
            rdr >> CurrentLMProgramHistoryId;
            CurrentLMProgramHistoryId++;
        }
        return CurrentLMProgramHistoryId;
    }
}

// Get the next gear id from local store or the database
// Not thread safe.
long CtiTableLMProgramHistory::getNextGearHistId()
{
    if( CurrentLMGearHistoryId != 0 )
    {
        CurrentLMGearHistoryId++;
        return CurrentLMGearHistoryId;
    }
    else
    {
        // Not yet initialized. Default to 1 and try to load from database.
        CurrentLMGearHistoryId = 1;

        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable gearTable = getDatabase().table( "LMProgramGearHistory" );
        RWDBSelector selector = conn.database().selector();

        selector << rwdbMax(gearTable["LMProgramGearHistoryId"]);
        selector.from(gearTable);
        RWDBReader rdr = selector.reader(conn);

        if( rdr() )
        {
            rdr >> CurrentLMGearHistoryId;
            CurrentLMGearHistoryId++;
        }

        return CurrentLMGearHistoryId;
    }
}

// Note the action field is a varchar(50)
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

// This ensures that all strings have something in them 
// and that their sizes are not larger than the database can handle.
void CtiTableLMProgramHistory::validateData()
{
    //Inserting a blank name causes problems in oracle.
    if( _programName.size() == 0 )
    {
        _programName = "(none)";
    }
    if( _reason.size() == 0 )
    {
        _reason = "(none)";
    }
    if( _user.size() == 0 )
    {
        _user = "(none)";
    }
    if( _gearName.size() == 0 )
    {
        _gearName = "(none)";
    }

    if( _programName.size() > 60 )
    {
        _programName.resize(60);
    }
    if( _reason.size() > 50 )
    {
        _reason.resize(50);
    }
    if( _user.size() > 64 )
    {
        _user.resize(64);
    }
    if( _gearName.size() > 30 )
    {
        _gearName.resize(30);
    }
}
