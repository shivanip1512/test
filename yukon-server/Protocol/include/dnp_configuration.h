#pragma once

namespace Cti {
namespace Protocol {
namespace DNP {

struct config_data
{
    config_data(unsigned internalRetries, bool useLocalTime, bool enableDnpTimesyncs, 
                bool omitTimeRequest, bool enableUnsolicited) :
        internalRetries(internalRetries),
        useLocalTime(useLocalTime),
        enableDnpTimesyncs(enableDnpTimesyncs),
        omitTimeRequest(omitTimeRequest),
        enableUnsolicited(enableUnsolicited)
    {
    }

    const unsigned internalRetries;
    const bool useLocalTime;
    const bool enableDnpTimesyncs;
    const bool omitTimeRequest;
    const bool enableUnsolicited;

private:
    config_data();
};

}
}
}
