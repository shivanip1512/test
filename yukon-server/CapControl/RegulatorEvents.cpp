#include "precompiled.h"

#include "RegulatorEvents.h"
#include "std_helper.h"
#include "queue.h"
#include "database_reader.h"
#include "database_writer.h"
#include "logger.h"
#include "ccid.h"

#include <boost/algorithm/string/join.hpp>

extern unsigned long   _CC_DEBUG;


namespace Cti           {
namespace CapControl    {

namespace
{

CtiValueQueue<RegulatorEvent>   _eventQueue;

std::string desolveEventType( const RegulatorEvent::EventTypes event )
{
    static const std::map<RegulatorEvent::EventTypes, std::string> eventDesolver
    {
        { RegulatorEvent::TapUp,                "TAP_UP"                 },
        { RegulatorEvent::TapDown,              "TAP_DOWN"               },
        { RegulatorEvent::IncreaseSetPoint,     "INCREASE_SETPOINT"      },
        { RegulatorEvent::DecreaseSetPoint,     "DECREASE_SETPOINT"      },
        { RegulatorEvent::IntegrityScan,        "INTEGRITY_SCAN"         },
        { RegulatorEvent::EnableRemoteControl,  "ENABLE_REMOTE_CONTROL"  },
        { RegulatorEvent::DisableRemoteControl, "DISABLE_REMOTE_CONTROL" }
    };

    if ( auto eventStr = mapFind( eventDesolver, event ) )
    {
        return *eventStr;
    }

    return "UNINITIALIZED";
}

boost::optional<char> desolveEventPhase( const Phase phase )
{
    static const std::map<Phase, char> phaseDesolver
    {
        { Phase_A, 'A' },
        { Phase_B, 'B' },
        { Phase_C, 'C' }
    };

    return mapFind( phaseDesolver, phase );
}

long GetEventID()
{
    long nextID;

    static const std::string sql =
        "SELECT "
            "COALESCE(MAX(RegulatorEventID) + 1, 0) AS NextID "
        "FROM "
            "RegulatorEvents";

    Database::DatabaseConnection connection;
    Database::DatabaseReader     reader( connection, sql );

    reader.execute();

    if ( reader() )
    {
        reader[ "NextID" ] >> nextID;
    }

    return nextID;
}

bool WriteEntryToDB( const long ID, const RegulatorEvent & event )
{
    std::vector<std::string> columnNames
    {
        "RegulatorEventID",
        "EventType",
        "RegulatorID",
        "TimeStamp",
        "UserName"
    };

    if ( event.setPointValue )
    {
        columnNames.push_back( "SetPointValue" );
    }

    if ( event.tapPosition )
    {
        columnNames.push_back( "TapPosition" );
    }

    boost::optional<char> phaseEntry = desolveEventPhase( event.phase );

    if ( phaseEntry )
    {
        columnNames.push_back( "Phase" );
    }

    std::vector<std::string>  placeholders( columnNames.size(), "?" );

    const std::string sql =
        "INSERT INTO "
            "RegulatorEvents (" + boost::algorithm::join( columnNames, ", " ) +
        ") VALUES (" + boost::algorithm::join( placeholders, ", " ) + ")";

    Database::DatabaseConnection connection;
    Database::DatabaseWriter     writer( connection, sql );

    writer
        << ID
        << desolveEventType( event.eventType )
        << event.regulatorID
        << CtiTime(event.timeStamp)
        << event.userName
            ;

    if ( event.setPointValue )
    {
        writer
            << *event.setPointValue;
    }

    if ( event.tapPosition )
    {
        writer
            << *event.tapPosition;
    }

    if ( phaseEntry )
    {
        writer
            << *phaseEntry;
    }

    if ( _CC_DEBUG & CC_DEBUG_DATABASE )
    {
        CTILOG_INFO( dout, writer.asString() );
    }

    bool success = writer.execute();

    if ( ! success )
    {
        CTILOG_ERROR( dout, "RegulatorEvents Table insert failed: " << writer.asString() );
    }

    return success;
}

}

RegulatorEvent::RegulatorEvent()
    :   regulatorID( -1 ),
        eventType( Uninitialized ),
        userName( "cap control" ),
        phase( Phase_Unknown )
{
    // empty...
}

RegulatorEvent RegulatorEvent::makeControlEvent( const EventTypes           event,
                                                 const long                 regulatorID,
                                                 const Phase                phase,
                                                 boost::optional<double>  & setPoint,
                                                 boost::optional<long>    & tapPosition )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;
    e.setPointValue = setPoint;
    e.tapPosition   = tapPosition;

    return e;
}

RegulatorEvent RegulatorEvent::makeScanEvent( const EventTypes  event,
                                              const long        regulatorID,
                                              const Phase       phase )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;

    return e;
}

RegulatorEvent RegulatorEvent::makeRemoteControlEvent( const EventTypes  event,
                                                       const long        regulatorID,
                                                       const Phase       phase )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;

    return e;
}

void enqueueRegulatorEvent( const RegulatorEvent & event )
{
    _eventQueue.putQueue( event );
}

void writeRegulatorEventsToDatabase()
{
    for ( long eventID = GetEventID(); ! _eventQueue.empty(); ++eventID )
    {
        WriteEntryToDB( eventID, _eventQueue.getQueue() );
    }
}

namespace Test
{

void exportRegulatorEvents( std::vector<RegulatorEvent> & events )
{
    while ( ! _eventQueue.empty() )
    {
        events.push_back( _eventQueue.getQueue() );
    }
}

}


}
}

