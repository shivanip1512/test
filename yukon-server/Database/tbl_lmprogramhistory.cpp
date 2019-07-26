#include "precompiled.h"

#include "row_reader.h"
#include "tbl_lmprogramhistory.h"
#include "dbmemobject.h"
#include "dbaccess.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::transform;
using std::string;
using std::endl;

static long CurrentLMProgramHistoryId = 0;
static long CurrentLMGearHistoryId    = 0;

CtiTableLMProgramHistory::CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                                                   string programName, string reason, string user, string gearName,
                                                   CtiTime time, std::string origin) :
_lmProgramHistID(progHistID),
_programID(program),
_gearID(gear),
_action(action),
_programName(programName),
_reason(reason),
_user(user),
_gearName(gearName),
_time(time),
_origin(origin)
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
        _origin           = aRef._origin;
    }

    return *this;
}

bool CtiTableLMProgramHistory::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    validateData();

    std::string origin = "(none)";  // we only care about this for Start actions

    if( _action == Start )
    {
        origin = _origin;   // we care about this for Start actions

        static const std::string sql_program_history = "insert into LMProgramHistory values (?, ?, ?)";

        Cti::Database::DatabaseWriter   inserter(conn, sql_program_history);

        inserter
            << _lmProgramHistID
            << _programName
            << _programID;

        if( ! Cti::Database::executeCommand( inserter, CALLSITE ))
        {
            //We had an error, force a reload
            CurrentLMProgramHistoryId = 0;
            CurrentLMGearHistoryId    = 0;
        }
    }

    long gearHistId = getNextGearHistId();

    static const std::string sql_program_gear_history = "insert into LMProgramGearHistory values (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseWriter   inserter(conn, sql_program_gear_history);

    inserter
        << gearHistId
        << _lmProgramHistID
        << _time
        << getStrFromAction(_action)
        << _user
        << _gearName
        << _gearID
        << _reason
        << origin;

    if( ! Cti::Database::executeCommand( inserter, CALLSITE ))
    {
        //We had an error, force a reload
        CurrentLMProgramHistoryId = 0;
        CurrentLMGearHistoryId    = 0;
        return false;
    }

    return true; // No error occurred!
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
        static const string sql = "SELECT MAX (PH.LMProgramHistoryId) "
                                  "FROM LMProgramHistory PH";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

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

        static const string sql = "SELECT MAX (PGH.LMProgramGearHistoryId) "
                                  "FROM LMProgramGearHistory PGH";

        Cti::Database::DatabaseConnection connection;
        Cti::Database::DatabaseReader rdr(connection, sql);
        rdr.execute();

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
    if( _origin.size() == 0 )
    {
        _origin = "(none)";
    }
}
