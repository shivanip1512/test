#define BOOST_TEST_MAIN

#include <boost/test/unit_test.hpp>

#include "ThirdPartyLibraries.h"

#include "std_helper.h"

#include <openssl/md5.h>
#include <openssl/sha.h>

#include <windows.h>

#include <filesystem>
#include <fstream>
#include <iostream>
#include <array>

BOOST_AUTO_TEST_SUITE(test_third_party_libraries)

template <size_t Size>
std::string arrayToHexString(std::array<unsigned char, Size>& buf)
{
    std::string hex;

    for( unsigned char c : buf )
    {
        auto nibble = c / 16;

        hex += (nibble > 9)
            ? nibble + 'a' - 10 
            : nibble + '0';

        nibble = c % 16;

        hex += (nibble > 9)
            ? nibble + 'a' - 10
            : nibble + '0';
    }

    return hex;
}

BOOST_AUTO_TEST_CASE(test_library_environments)
{
    namespace fs = std::filesystem;

    int libraryIndex = 0;

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
                BOOST_TEST_CONTEXT(libraryPath->second)
                {
                    BOOST_TEST_MESSAGE("Generating hashes for " + library.project);

                    try
                    {
                        MD5_CTX md5Context;
                        MD5_Init(&md5Context);

                        SHA_CTX sha1Context;
                        SHA1_Init(&sha1Context);

                        size_t files = 0;
                        uint64_t fileSizes = 0;

                        std::vector<fs::directory_entry> fileList;

                        std::copy(fs::recursive_directory_iterator{ libraryPath->second }, {}, std::back_inserter(fileList));

                        //  Print status so users know why they're waiting
                        std::cout << "Generating hashes for " << ++libraryIndex << "/" << libraries.size() << ": " << library.project << std::endl;

                        for( auto p : fileList )
                        {
                            if( p.is_regular_file() )
                            {
                                ++files;

                                std::ifstream fileContents{ p.path(), std::ios::binary };

                                while( ! fileContents.eof() )
                                {
                                    std::array<char, 4096> block;

                                    fileContents.read(block.data(), block.size());

                                    const auto blockSize = fileContents.gcount();

                                    fileSizes += blockSize;

                                    MD5_Update (&md5Context,  block.data(), blockSize);
                                    SHA1_Update(&sha1Context, block.data(), blockSize);
                                }

                                //  Print updates if there are more than 1000 files to process (Boost has 14,000+)
                                if( ! (files % 1000) )
                                {
                                    std::cout << "\t" << files << "/" << fileList.size() << std::endl;
                                }
                            }
                        }

                        BOOST_TEST_CONTEXT("Files: " + std::to_string(files) + " File sizes: " + std::to_string(fileSizes))
                        {
                            std::array<unsigned char, MD5_DIGEST_LENGTH> md5Digest;
                            std::array<unsigned char, SHA_DIGEST_LENGTH> sha1Digest;

                            MD5_Final (md5Digest.data(),  &md5Context);
                            SHA1_Final(sha1Digest.data(), &sha1Context);

                            const auto md5Actual = arrayToHexString(md5Digest);
                            const auto sha1Actual = arrayToHexString(sha1Digest);

                            BOOST_CHECK_EQUAL(md5Actual,  library.md5);
                            BOOST_CHECK_EQUAL(sha1Actual, library.sha1);
                        }
                    }
                    catch( fs::filesystem_error& e )
                    {
                        BOOST_ERROR("Filesystem error:" << e.what());
                    }
                }
            }

            libraryPaths.erase(libraryPath);
        }
    }

    if( ! libraryPaths.empty() )
    {
        const auto path = libraryPaths.begin();

        BOOST_ERROR("Known library path had no entry in thirdPartyLibraries.yaml: "
            << "\nLibrary name: " << path->first
            << "\nLibrary path: " << path->second);
    }
}

BOOST_AUTO_TEST_SUITE_END()
