#pragma once

#include "logger.h"

#include <boost/tuple/tuple_comparison.hpp>

namespace Cti {
namespace Devices {

struct RfnIdentifier
{
    std::string manufacturer;
    std::string model;
    std::string serialNumber;

    bool operator<(const RfnIdentifier &rhs) const
    {
        return boost::tie(manufacturer, model, serialNumber)
             < boost::tie(rhs.manufacturer, rhs.model, rhs.serialNumber);
    }
};

inline std::ostream &operator<<(std::ostream &os, const RfnIdentifier &rfnId)
{
    //  matches RfnIdentifier.java toString()
    /*
    return l << "RfnIdentifier ["
        "sensorManufacturer=" << rfnId.manufacturer << ", "
        "sensorModel="        << rfnId.model << ", "
        "sensorSerialNumber=" << rfnId.serialNumber << "]";
    */

    //  matches RfnIdentifier.java getCombinedIdentifier()
    return os << rfnId.manufacturer << "_" << rfnId.model << "_" << rfnId.serialNumber;
}

}
}
