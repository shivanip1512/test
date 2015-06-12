#include "precompiled.h"

#include "BchBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator{

BchBehavior::BchBehavior(double probability)
    :   PlcBehavior(probability)
{
}

/**
 * Applies the BchBehavior to the last byte of the D-word by
 * using the XOR operator. Since the last four bits of a D-word
 * aren't used in a 7-byte message because D-words are only 52
 * bits long, the last bit of the second-to-last nibble of data
 * will be XOR'ed to produce a BCH error.
 *
 * @param message The message received from PLC transmission.
 */
void BchBehavior::apply(target_type &message, Logger &logger)
{
    if( behaviorOccurs() )
    {
        logger.breadcrumbLog("***** MESSAGE BCH MUTILATED *****");
        message.back() ^= 0x10;
    }
}

}
}
