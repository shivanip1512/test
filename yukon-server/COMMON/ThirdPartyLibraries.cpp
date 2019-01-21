#include "precompiled.h"

#include "include\ThirdPartyLibraries.h"

namespace Cti {

std::vector<ThirdPartyLibraries::Library> ThirdPartyLibraries::_libraries;

void ThirdPartyLibraries::addLibrary(
        std::string project,
        std::string path,
        std::string version,
        std::string md5,
        std::string sha1)
{
    _libraries.push_back({ project, path, version, md5, sha1 });
}

auto ThirdPartyLibraries::getLibraries() -> std::vector<Library>
{
    return _libraries;
}

}