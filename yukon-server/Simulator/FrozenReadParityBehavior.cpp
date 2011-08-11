#include "precompiled.h"

#include "FrozenReadParityBehavior.h"

namespace Cti {
namespace Simulator{

FrozenReadParityBehavior::FrozenReadParityBehavior()
{
}

/**
 * Applies the FrozenReadParityBehavior to the message by, on 
 * reads that contain a frozen meter read, inverting the LSB of 
 * the data to incorrectly state which freeze command was 
 * responsible for this meter read. 
 */
void FrozenReadParityBehavior::apply(target_type &message, Logger &logger)
{
    if( message.function_read )
    {
        if( message.function == 0x91 && (message.data.size() > 2) )
        {
            invertFrozenParityBit(message.data[2], logger);
        }
        else if( message.function == 0x94 && (message.data.size() > 8) )
        {
            invertFrozenParityBit(message.data[8], logger);
        }
    }
}

void FrozenReadParityBehavior::invertFrozenParityBit(unsigned char &byte, Logger &logger)
{
    double dist = rand() / double(RAND_MAX+1);
    double chance = dist * 100;
    if(chance < _chance)
    {
        logger.breadcrumbLog("***** FROZEN READ PARITY INVERTED *****");
        byte ^= 0x01;
    }
}

/**
 * Sets the FrozenReadParityBehavior's chance to be applied to a 
 * given message. 
 *  
 * @param chance The percent chance for the 
 * FrozenReadParityBehavior to be applied to a message as 
 * defined in the master.cfg file under the CPARM value 
 * SIMULATOR_INVALID_FROZEN_READ_PARITY_PROBABILITY 
 */
void FrozenReadParityBehavior::setChance(double chance)
{
    _chance = chance;
}

}
}
