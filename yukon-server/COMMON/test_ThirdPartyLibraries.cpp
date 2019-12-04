#include <boost/test/unit_test.hpp>

#include "ThirdPartyLibraries.h"

#include "std_helper.h"
#include "string_util.h"

#include <openssl/md5.h>
#include <openssl/sha.h>

#include <filesystem>
#include <fstream>

BOOST_AUTO_TEST_SUITE(test_third_party_libraries)

BOOST_AUTO_TEST_CASE(test_library_environments)
{
    namespace fs = std::filesystem;

    const auto libraries = Cti::ThirdPartyLibraries().getLibraries();
    auto libraryPaths = Cti::ThirdPartyLibraries().getKnownLibraryPaths();

    for( const auto library : libraries )
    {
        BOOST_TEST_CONTEXT(library.project)
        {
            auto libraryPath = libraryPaths.find(library.path);
            
            if( libraryPath == libraryPaths.end() )
            {
                BOOST_ERROR("No entry for " << library.path << " in Cti::ThirdPartyLibraries::getKnownLibraryPaths");
            }
            else
            {
                uint64_t totalSize = 0;
                uint32_t fileCount = 0;

                try
                {
                    for( auto p : fs::recursive_directory_iterator{ libraryPath->second } )
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
                catch( fs::filesystem_error& e )
                {
                    BOOST_ERROR("Filesystem error:" << Cti::FormattedList::of(
                        "Code value", e.code().value(),
                        "Code message", e.code().message(),
                        "Path1", e.path1().string(),
                        "Path2", e.path2().string(),
                        "What", e.what()));
                }
            }

            libraryPaths.erase(libraryPath);
        }
    }

    if( !libraryPaths.empty() )
    {
        const auto path = libraryPaths.begin();

        BOOST_ERROR("Known library path had no entry in thirdPartyLibraries.yaml: " << Cti::FormattedList::of(
            "Library name", path->first, 
            "Library path", path->second));
    }
}

BOOST_AUTO_TEST_SUITE_END()
