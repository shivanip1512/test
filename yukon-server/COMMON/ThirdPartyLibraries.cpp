#include "ThirdPartyLibraries.h"

#include "resource_ids.h"
#include "resource_helper.h"

#include <yaml-cpp/yaml.h>

namespace Cti {

ThirdPartyLibraries::ThirdPartyLibraries()
{
    DataBuffer raw = loadResourceFromLibrary(THIRD_PARTY_LIBRARIES_ID, "YAML", "yukon-resource.dll");

    std::string yaml_as_string{ raw.begin(), raw.end() };

    const auto libraryTypes = YAML::Load(yaml_as_string);

    const auto cppLibraries = libraryTypes["C++"];

    if( !cppLibraries.IsSequence() )
    {
        throw std::runtime_error{ "C++ library entry not a sequence" };
    }

    for( const auto library : cppLibraries )
    {
        _libraries.push_back({
            library["project"].as<std::string>(),
            library["path"].as<std::string>(),
            library["version"].as<std::string>(),
            library["fileCount"].as<uint32_t>(),
            library["totalSize"].as<uint64_t>(),
            library["md5"].as<std::string>(),
            library["sha1"].as<std::string>()});
    }
}

auto ThirdPartyLibraries::getLibraries() -> std::vector<Library>
{
    return _libraries;
}

}