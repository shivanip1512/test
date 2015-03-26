#pragma once

#include "yukon.h"
#include "ccutil.h"

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

    RegulatorEvent();

    static RegulatorEvent makeControlEvent( const EventTypes           event,       
                                            const long                 regulatorID, 
                                            const Phase                phase,       
                                            boost::optional<double>  & setPoint,    
                                            boost::optional<long>    & tapPosition );

    static RegulatorEvent makeScanEvent( const EventTypes  event,
                                         const long        regulatorID,
                                         const Phase       phase );


    static RegulatorEvent makeRemoteControlEvent( const EventTypes  event,
                                                  const long        regulatorID,
                                                  const Phase       phase );

    long                    regulatorID;
    EventTypes              eventType;
    std::string             userName;
    CtiTime                 timeStamp;
    Phase                   phase;
    boost::optional<double> setPointValue;
    boost::optional<long>   tapPosition;
};




void writeRegulatorEventsToDatabase();

void enqueueRegulatorEvent( const RegulatorEvent & event );

namespace Test
{

void exportRegulatorEvents( std::vector<RegulatorEvent> & events );

}



}
}

