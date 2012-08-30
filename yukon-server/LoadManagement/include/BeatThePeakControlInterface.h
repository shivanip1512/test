#pragma once
#include <boost/shared_ptr.hpp>
#include "logger.h"
#include "BeatThePeakAlertLevel.h"

namespace Cti {
namespace LoadManagement {

class BeatThePeakControlInterface
{
public:

    virtual bool sendBeatThePeakControl(BeatThePeak::AlertLevel level, int timeout) = 0;
    virtual bool sendBeatThePeakRestore() = 0;
};

typedef boost::shared_ptr<BeatThePeakControlInterface> BeatThePeakControlInterfacePtr;

}
}
