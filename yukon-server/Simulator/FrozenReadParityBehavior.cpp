#include "precompiled.h"

#include "FrozenReadParityBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

FrozenReadParityBehavior::FrozenReadParityBehavior(double probability)
    :   MctBehavior(probability)
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
    if( behaviorOccurs() )
    {
        logger.breadcrumbLog("***** FROZEN READ PARITY INVERTED *****");
        byte ^= 0x01;
    }
}

}
}
