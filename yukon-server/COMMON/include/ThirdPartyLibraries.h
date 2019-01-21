#pragma once

#include "dlldefs.h"

#include <vector>

namespace Cti {

class IM_EX_CTIBASE ThirdPartyLibraries
{
public:

    struct Library
    {
        std::string project;
        std::string path;
        std::string version;
        std::string md5;
        std::string sha1;
    };

    static void addLibrary(
            std::string project,
            std::string path,
            std::string version,
            std::string md5,
            std::string sha1);

    static std::vector<Library> ThirdPartyLibraries::getLibraries();

private:

    static std::vector<Library> _libraries;
};

}

