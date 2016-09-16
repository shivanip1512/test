#pragma once

#include <boost/shared_ptr.hpp>


namespace Cti {
    namespace LoadManagement {

        struct HoneywellControlInterface
        {
            virtual bool sendCycleControl(long dutyCycle,
                                          long controlDurationSeconds,
                                          bool rampInOption,
                                          bool rampOutOption) = 0;
        };

        typedef boost::shared_ptr<HoneywellControlInterface> HoneywellControlInterfacePtr;

    }
}