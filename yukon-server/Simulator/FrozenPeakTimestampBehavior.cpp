#include "precompiled.h"

#include "FrozenPeakTimestampBehavior.h"

namespace Cti {
namespace Simulator{

FrozenPeakTimestampBehavior::FrozenPeakTimestampBehavior()
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
        double dist = rand() / double(RAND_MAX+1);
        double chance = dist * 100;
        if(chance < _chance)
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

/**
 * Sets the FrozenPeakTimestampBehavior's chance to be applied 
 * to a given message. 
 *  
 * @param chance The percent chance for the 
 * FrozenPeakTimestampBehavior to be applied to a message as 
 * defined in the master.cfg file under the CPARM value 
 * SIMULATOR_INVALID_FROZEN_PEAK_TIMESTAMP_PROBABILITY 
 */
void FrozenPeakTimestampBehavior::setChance(double chance)
{
    _chance = chance;
}

}
}
