#pragma once

#include "streamBuffer.h"
#include <boost/tuple/tuple_comparison.hpp>

namespace Cti {

struct RfnIdentifier : Loggable
{
    //  All of these should be less than 16 bytes, benefit from Small String Optimization, and incur no additional heap allocation.

    std::string manufacturer;
    std::string model;
    std::string serialNumber;

    RfnIdentifier()
    {}

    RfnIdentifier(std::string manufacturer_, std::string model_, std::string serialNumber_) :
        manufacturer(manufacturer_),
        model(model_),
        serialNumber(serialNumber_)
    {}

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
};

}
