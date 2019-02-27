#pragma once

#include <vector>

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

private:

    std::vector<Library> _libraries;
};

}

