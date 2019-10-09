#include <boost/test/unit_test.hpp>

#include "ThirdPartyLibraries.h"

#include "std_helper.h"

#include <openssl/md5.h>
#include <openssl/sha.h>

#include <filesystem>
#include <fstream>

BOOST_AUTO_TEST_SUITE(test_third_party_libraries)

BOOST_AUTO_TEST_CASE(test_library_environments)
{
    namespace fs = std::filesystem;

    const auto libraries = Cti::ThirdPartyLibraries().getLibraries();
    const auto libraryPaths = Cti::ThirdPartyLibraries().getKnownLibraryPaths();

    for( const auto library : libraries )
    {
        BOOST_TEST_CONTEXT(library.project)
        {
            auto libraryPath = Cti::mapFind(libraryPaths, library.path);
            
            if( ! libraryPath )
            {
                BOOST_ERROR("No entry for " << library.path << " in Cti::ThirdPartyLibraries::getKnownLibraryPaths");

                continue;
            }

            uint64_t totalSize = 0;
            uint32_t fileCount = 0;

            for( auto p : fs::recursive_directory_iterator{ *libraryPath } )
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
