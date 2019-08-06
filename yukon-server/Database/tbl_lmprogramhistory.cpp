#include "precompiled.h"

#include "tbl_lmprogramhistory.h"
#include "logger.h"
#include "database_connection.h"
#include "database_reader.h"
#include "database_util.h"

static long CurrentLMProgramHistoryId = 0;
static long CurrentLMGearHistoryId    = 0;

CtiTableLMProgramHistory::CtiTableLMProgramHistory(long progHistID, long program, long gear, LMHistoryActions action,
                                                   std::string programName, std::string reason, std::string user, std::string gearName,
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

CtiTableLMProgramHistory CtiTableLMProgramHistory::createStartHistory(
    long progHistID,
    long program,
    long gear,
    LMHistoryActions action,
    const std::string & programName,
    const std::string & reason,
    const std::string & user,
    const std::string & gearName,
    const CtiTime time,
    const std::string & origin )
{
    return
    { 
        progHistID, program, gear, action, programName, reason, user, gearName, time, origin
    };
}

CtiTableLMProgramHistory CtiTableLMProgramHistory::createGenericHistory(
    long progHistID,
    long program,
    long gear,
    LMHistoryActions action,
    const std::string & programName,
    const std::string & reason,
    const std::string & user,
    const std::string & gearName,
    const CtiTime time )
{
    return
    { 
        progHistID, program, gear, action, programName, reason, user, gearName, time, "(none)"
    };
}

bool CtiTableLMProgramHistory::Insert()
{
    Cti::Database::DatabaseConnection   conn;

    validateData();

    if( _action == Start )
    {
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
        << _origin;

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
        static const std::string sql = "SELECT MAX (PH.LMProgramHistoryId) "
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

        static const std::string sql = "SELECT MAX (PGH.LMProgramGearHistoryId) "
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
std::string CtiTableLMProgramHistory::getStrFromAction(long action)
{
    switch( action )
    {
        case Start:
            return "Start";
        case Update:
            return "Update";
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

    validateEntry( _programName, 60 );
    validateEntry( _reason,      50 );
    validateEntry( _user,        64 );
    validateEntry( _gearName,    30 );
    validateEntry( _origin,      30 );
}

void CtiTableLMProgramHistory::validateEntry( std::string & entry, std::size_t maxLength )
{
    if ( entry.empty() )
    {
        entry = "(none)";
    }
    else if ( entry.length() > maxLength )
    {
        entry.resize( maxLength );
    }
}

