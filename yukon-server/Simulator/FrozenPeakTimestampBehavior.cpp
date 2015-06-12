#include "precompiled.h"

#include "FrozenPeakTimestampBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

FrozenPeakTimestampBehavior::FrozenPeakTimestampBehavior(double probability)
    :   MctBehavior(probability)
{
}

/**
 * Applies the FrozenPeakTimestampBehavior to the message by
 * adding a day's worth of seconds to the frozen peak timestamp.
 */
void FrozenPeakTimestampBehavior::apply(target_type &message, Logger &logger)
{
    if( message.function_read && message.function == 0x94)
    {
        if( behaviorOccurs() )
        {
            logger.breadcrumbLog("***** FROZEN PEAK TIMESTAMP MODIFIED *****");
            unsigned timestamp = 0;

            for( int i = 0; i < 4; i++ )
            {
                timestamp <<= 8;
                timestamp |= message.data[2 + i]; // Bytes 2-5 hold the timestamp
            }

            timestamp += 86400; // Seconds per day.

            for( int i = 0; i < 4; i++)
            {
                message.data[2 + i] = timestamp >> ((3-i)*8);
            }
        }
    }
}

}
}
