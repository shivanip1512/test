#pragma once

#include "streamBuffer.h"
#include <boost/tuple/tuple_comparison.hpp>

namespace Cti {

struct RfnIdentifier : public Loggable
{
    std::string manufacturer;
    std::string model;
    std::string serialNumber;

    virtual std::string toString() const override
    {
        //  matches RfnIdentifier.java toString()
        /*
        StreamBuffer() << "RfnIdentifier ["
            "sensorManufacturer=" << rfnId.manufacturer << ", "
            "sensorModel="        << rfnId.model << ", "
            "sensorSerialNumber=" << rfnId.serialNumber << "]";
        */

        //  matches RfnIdentifier.java getCombinedIdentifier()
        return StreamBuffer() << manufacturer << "_" << model << "_" << serialNumber;
    }

    bool operator<(const RfnIdentifier &rhs) const
    {
        return boost::tie(manufacturer, model, serialNumber)
             < boost::tie(rhs.manufacturer, rhs.model, rhs.serialNumber);
    }
};

}
