#include "ThirdPartyLibraries.h"

#include "resource_ids.h"
#include "resource_helper.h"
#include "preprocessor_helper.h"

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

#define PATH_TO(x) PATH_TO_##x
#define LIBRARY_ENTRY(x) { STRINGIZE(x), RAW_STRINGIZE(PATH_TO(x)) }

std::map<std::string, std::string> ThirdPartyLibraries::getKnownLibraryPaths()
{
    //  List of PATH_TO... library paths provided in the Properties -> C/C++ -> Preprocessor -> Preprocessor Definitions
    return {
        LIBRARY_ENTRY(ACTIVEMQ),
        LIBRARY_ENTRY(APR),
        LIBRARY_ENTRY(APR_ICONV),
        LIBRARY_ENTRY(APR_UTIL),
        LIBRARY_ENTRY(BOOST),
        LIBRARY_ENTRY(DBGHELP),
        LIBRARY_ENTRY(JSON),
        LIBRARY_ENTRY(LIBCOAP),
        LIBRARY_ENTRY(LOG4CXX),
        LIBRARY_ENTRY(MICROSOFT),
        LIBRARY_ENTRY(MICROSOFT_SQL),
        LIBRARY_ENTRY(OPENSSL),
        LIBRARY_ENTRY(SQLAPI),
        LIBRARY_ENTRY(TCL),
        LIBRARY_ENTRY(THRIFT),
        LIBRARY_ENTRY(XERCES),
        LIBRARY_ENTRY(YAML_CPP),
    };
}

}