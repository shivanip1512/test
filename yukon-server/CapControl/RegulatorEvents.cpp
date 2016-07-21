#include "precompiled.h"

#include "RegulatorEvents.h"
#include "std_helper.h"
#include "queue.h"
#include "database_reader.h"
#include "database_writer.h"
#include "logger.h"
#include "ccid.h"

#include <mutex>

extern unsigned long   _CC_DEBUG;


namespace Cti           {
namespace CapControl    {

namespace
{

using EventQueueLock = std::lock_guard<std::mutex>;

std::vector<RegulatorEvent> eventQueue;
std::mutex                  eventQueueMux;

void exchangeRegulatorEvents( std::vector<RegulatorEvent> & events )
{
    EventQueueLock  lock( eventQueueMux );

    events.swap( eventQueue );
}

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

    return mapFindOrDefault( eventDesolver, event, "UNINITIALIZED" );
}

boost::optional<std::string> desolveEventPhase( const Phase phase )
{
    static const std::map<Phase, std::string> phaseDesolver
    {
        { Phase_A, "A" },
        { Phase_B, "B" },
        { Phase_C, "C" }
    };

    return mapFind( phaseDesolver, phase );
}

long GetEventID( Database::DatabaseConnection & connection, const std::size_t reserveIdCount )
{
    static long nextID = -1;

    if ( nextID < 0 )
    {
        static const std::string sql =
            "SELECT "
                "COALESCE(MAX(RegulatorEventID) + 1, 0) AS NextID "
            "FROM "
                "RegulatorEvents";

        Database::DatabaseReader    reader( connection, sql );

        reader.execute();

        if ( reader() )
        {
            reader[ "NextID" ] >> nextID;
        }
    }

    const long currentID = nextID;

    nextID += reserveIdCount;

    return currentID;
}

bool WriteEntryToDB( Database::DatabaseConnection & connection, const long ID, const RegulatorEvent & event )
{
    static const std::string sql =
        "INSERT INTO "
            "RegulatorEvents "
        "VALUES (?,?,?,?,?,?,?,?)";

    Database::DatabaseWriter     writer( connection, sql );

    writer
        << ID
        << desolveEventType( event.eventType )
        << event.regulatorID
        << CtiTime(event.timeStamp)
        << event.userName
        << event.setPointValue
        << event.tapPosition
        << desolveEventPhase( event.phase )
            ;

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
                                                 boost::optional<long>    & tapPosition,
                                                 const std::string        & user )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;
    e.setPointValue = setPoint;
    e.tapPosition   = tapPosition;
    e.userName      = user;

    return e;
}

RegulatorEvent RegulatorEvent::makeScanEvent( const EventTypes    event,
                                              const long          regulatorID,
                                              const Phase         phase,
                                              const std::string & user )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;
    e.userName      = user;

    return e;
}

RegulatorEvent RegulatorEvent::makeRemoteControlEvent( const EventTypes    event,
                                                       const long          regulatorID,
                                                       const Phase         phase,
                                                       const std::string & user )
{
    RegulatorEvent  e;

    e.eventType     = event;
    e.regulatorID   = regulatorID;
    e.phase         = phase;
    e.userName      = user;

    return e;
}

void enqueueRegulatorEvent( const RegulatorEvent & event )
{
    EventQueueLock  lock( eventQueueMux );

    eventQueue.push_back( event );
}

void writeRegulatorEventsToDatabase()
{
    if ( ! eventQueue.empty() )
    {
        std::vector<RegulatorEvent> writeEvents;

        exchangeRegulatorEvents( writeEvents );

        Database::DatabaseConnection connection;

        long eventID = GetEventID( connection, writeEvents.size() );

        for ( const RegulatorEvent & event : writeEvents )
        {
            WriteEntryToDB( connection, eventID++, event );
        }
    }
}

namespace Test
{

void exportRegulatorEvents( std::vector<RegulatorEvent> & events, const Cti::Test::use_in_unit_tests_only & )
{
    exchangeRegulatorEvents( events );
}

}


}
}

