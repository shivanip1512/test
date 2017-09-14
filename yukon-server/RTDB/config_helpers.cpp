#include "precompiled.h"

#include "config_helpers.h"
#include "config_data_dnp.h"

namespace Cti {
namespace Devices {

AttributeAndPointName::AttributeAndPointName(const Config::DeviceConfig::ItemsByName &src) :
    attributeName(getConfigData(src, Config::DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute)),
    pointName    (getConfigData(src, Config::DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName))
{
}

}
}

