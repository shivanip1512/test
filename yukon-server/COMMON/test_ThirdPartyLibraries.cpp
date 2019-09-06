#include <boost/test/unit_test.hpp>

#include "ThirdPartyLibraries.h"

#include <openssl/md5.h>
#include <openssl/sha.h>

#include <filesystem>
#include <fstream>

BOOST_AUTO_TEST_SUITE(test_third_party_libraries)

BOOST_AUTO_TEST_CASE(test_library_environments)
{
    namespace fs = std::filesystem;

    const auto libraries = Cti::ThirdPartyLibraries().getLibraries();

    for( const auto library : libraries )
    {
        BOOST_TEST_CONTEXT(library.project)
        {
            std::array<char, MAX_PATH> libraryPath;

            auto pathLen = GetEnvironmentVariable(library.path.c_str(), libraryPath.data(), libraryPath.size());
            
            if( ! pathLen )
            {
                BOOST_ERROR("No environment variable for " << library.path);

                continue;
            }

            uint64_t totalSize = 0;
            uint32_t fileCount = 0;

            for( auto p : fs::recursive_directory_iterator{ libraryPath.data() } )
            {
                if( p.is_regular_file() )
                {
                    ++fileCount;

                    totalSize += p.file_size();
                }
            }

            BOOST_CHECK_EQUAL(fileCount, library.fileCount);
            BOOST_CHECK_EQUAL(totalSize, library.totalSize);
        }
    }
}

BOOST_AUTO_TEST_SUITE_END()
