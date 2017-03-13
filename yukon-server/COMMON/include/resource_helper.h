#pragma once

#include "dlldefs.h"
#include "devicetypes.h"

namespace Cti {

typedef std::vector<unsigned char>  DataBuffer;

IM_EX_CTIBASE DataBuffer loadResourceFromLibrary( const int resourceID, const char * resourceType, const char * libraryName );

IM_EX_CTIBASE DeviceTypes resolvePaoIdXmlType( const std::string & type );

}

