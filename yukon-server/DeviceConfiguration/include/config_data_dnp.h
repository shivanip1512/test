#pragma once

#include "yukon.h"
#include "dllbase.h"
#include <string>

namespace Cti {
namespace Config {

class IM_EX_CONFIG DNPStrings
{
public:
    static const std::string omitTimeRequest;
    static const std::string internalRetries;
    static const std::string enableUnsolicitedClass1;
    static const std::string enableUnsolicitedClass2;
    static const std::string enableUnsolicitedClass3;
    static const std::string enableNonUpdatedOnFailedScan;
    static const std::string enableDnpTimesyncs;
    static const std::string timeOffset;

    struct IM_EX_CONFIG AttributeMappingConfiguration
    {
        static const std::string AttributeMappings_Prefix;

        struct IM_EX_CONFIG AttributeMappings
        {
            static const std::string Attribute;
            static const std::string PointName;
        };
    };
};

}
}
