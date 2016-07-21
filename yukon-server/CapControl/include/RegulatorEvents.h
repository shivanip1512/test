#pragma once

#include "yukon.h"
#include "ccutil.h"
#include "utility.h"

#include <boost/optional.hpp>


namespace Cti           {
namespace CapControl    {

struct RegulatorEvent
{
    enum EventTypes
    {
        Uninitialized,
        TapUp,
        TapDown,
        IncreaseSetPoint,
        DecreaseSetPoint,
        IntegrityScan,
        EnableRemoteControl,
        DisableRemoteControl
    };

    static RegulatorEvent makeControlEvent( const EventTypes           event,       
                                            const long                 regulatorID, 
                                            const Phase                phase,       
                                            boost::optional<double>  & setPoint,    
                                            boost::optional<long>    & tapPosition,
                                            const std::string        & user );

    static RegulatorEvent makeScanEvent( const EventTypes    event,
                                         const long          regulatorID,
                                         const Phase         phase,
                                         const std::string & user );


    static RegulatorEvent makeRemoteControlEvent( const EventTypes    event,
                                                  const long          regulatorID,
                                                  const Phase         phase,
                                                  const std::string & user );

    long                    regulatorID;
    EventTypes              eventType;
    std::string             userName;
    CtiTime                 timeStamp;
    Phase                   phase;
    boost::optional<double> setPointValue;
    boost::optional<long>   tapPosition;

private:

    RegulatorEvent();
};




void writeRegulatorEventsToDatabase();

void enqueueRegulatorEvent( const RegulatorEvent & event );

namespace Test
{

void exportRegulatorEvents( std::vector<RegulatorEvent> &events, const Cti::Test::use_in_unit_tests_only & );

}



}
}

