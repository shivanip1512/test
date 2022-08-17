#pragma once

#include <vector>
#include <string>

enum DeviceTypes;

namespace Cti {

using DataBuffer = std::vector<unsigned char>;

DataBuffer loadResourceFromLibrary( const int resourceID, const char * resourceType, const char * libraryName );

DeviceTypes resolvePaoIdXmlType( const std::string & type );

}

