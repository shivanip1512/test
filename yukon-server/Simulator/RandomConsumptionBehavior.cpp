#include "precompiled.h"

#include "RandomConsumptionBehavior.h"

#include "SimulatorLogger.h"

namespace Cti {
namespace Simulator {

RandomConsumptionBehavior::RandomConsumptionBehavior(double probability)
    :   MctBehavior(probability)
{
}

/**
 * Applies the RandomConsumptionBehavior to the message by, on
 * reads that contain a current meter read, replacing the kWh
 * value in the message with a random value instead.
 */
void RandomConsumptionBehavior::apply(target_type &message, Logger &logger)
{
    if( message.function_read && message.function == 0x90 )
    {
        if( behaviorOccurs() )
        {
            logger.breadcrumbLog("***** RANDOM CONSUMPTION VALUE GENERATED *****");

            unsigned kwh = _consumptionValueGenerator;

            // Remove the first three elements.
            message.data.erase(message.data.begin(), message.data.begin() + 3);

            bytes kwhData;

            kwhData.push_back(kwh >> 16);
            kwhData.push_back(kwh >> 8);
            kwhData.push_back(kwh);

            message.data.insert(message.data.begin(), kwhData.begin(), kwhData.end());
        }
    }
}

}
}
