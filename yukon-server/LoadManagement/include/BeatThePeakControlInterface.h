#pragma once
#include <boost/shared_ptr.hpp>
#include "logger.h"

namespace Cti {
namespace LoadManagement {

class BeatThePeakControlInterface
{
public:
    enum Tier{
        Green = 0,
        Yellow = 2,
        Red =4
    };

    static std::string tierAsString(const Tier tier)
    {
        switch(tier)
        {
        case Green:
            return std::string("green");
        case Yellow:
            return std::string("yellow");
        case Red:
            return std::string("red");
        default:
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << " **** EXCEPTION Checkpoint **** Invalid tier: " << tier << ". Setting to Green." << std::endl;
            }
            return std::string("green");
        }
    }

    virtual bool sendBeatThePeakControl(Tier tier, int timeout) = 0;
    virtual bool sendBeatThePeakRestore() = 0;

};

typedef boost::shared_ptr<BeatThePeakControlInterface> BeatThePeakControlInterfacePtr;

}
}
