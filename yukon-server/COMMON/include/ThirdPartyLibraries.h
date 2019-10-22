#pragma once

#include <vector>
#include <map>
#include <string>

namespace Cti {

class ThirdPartyLibraries
{
public:

    struct Library
    {
        std::string project;
        std::string path;
        std::string version;
        uint32_t fileCount;
        uint64_t totalSize;
        std::string md5;
        std::string sha1;
    };

    ThirdPartyLibraries();

    std::vector<Library> getLibraries();

    std::map<std::string, std::string> getKnownLibraryPaths();

private:

    std::vector<Library> _libraries;
};

}
