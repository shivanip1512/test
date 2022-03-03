#pragma once

#include <vector>
#include <string>
#include <optional>

enum DeviceTypes;

namespace Cti {

using DataBuffer = std::vector<unsigned char>;

DataBuffer loadResourceFromLibrary( const int resourceID, const char * resourceType, const char * libraryName );

std::optional<DeviceTypes> resolvePaoIdXmlType( const std::string & type );

}

