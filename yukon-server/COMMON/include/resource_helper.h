#pragma once

#include "dlldefs.h"

#include <vector>

namespace Cti {

typedef std::vector<unsigned char>  DataBuffer;

enum ResourceIds
{
    Resource_PaoDefinitionXml = 101,
    Resource_PaoDefinitionXsd = 102,

    Resource_ConfigCategoryDefinitionXml = 103,
    Resource_ConfigCategoryDefinitionXsd = 104,
    Resource_DeviceConfigCategoryXsd     = 105,

    Resource_MetricIdToAttributeMapping  = 106
};

IM_EX_CTIBASE DataBuffer loadResourceFromLibrary( const ResourceIds resourceID, const char * resourceType, const char * libraryName );

}

