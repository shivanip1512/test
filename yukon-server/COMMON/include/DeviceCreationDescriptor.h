#pragma once

#include "streamBuffer.h"
#include <boost/tuple/tuple_comparison.hpp>

namespace Cti {

struct DeviceCreationDescriptor : Loggable
{

    int paoId;
    std::string category,
                deviceType;

    DeviceCreationDescriptor()
    {}

    DeviceCreationDescriptor(int paoId_, std::string category_, std::string deviceType_) :
        paoId(paoId_),
        category(category_),
        deviceType(deviceType_)
    {}

    virtual std::string toString() const override
    {
        //  matches DeviceCreationDescriptor.java toString()
        return StreamBuffer() << "DeviceCreationDescriptor ["
            "paoId="      << paoId << ", "
            "category="   << category << ", "
            "deviceType=" << deviceType << "]";
    }

    bool operator<(const DeviceCreationDescriptor &rhs) const
    {
        return boost::tie(paoId, category, deviceType)
             < boost::tie(rhs.paoId, rhs.category, rhs.deviceType);
    }

    bool operator==(const DeviceCreationDescriptor &rhs) const
    {
        return boost::tie(paoId, category, deviceType)
            == boost::tie(rhs.paoId, rhs.category, rhs.deviceType);
    }
};

}
