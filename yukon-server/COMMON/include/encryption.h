#pragma once

#include "dlldefs.h"

#include <string>
#include <vector>
#include <stdexcept>



namespace Cti
{
namespace Encryption
{
    IM_EX_CTIBASE enum EncryptionType
    {
        MasterCfg,
        OneWayMsgEncryptionKey
    };

    IM_EX_CTIBASE typedef std::vector< unsigned char > Buffer;


    struct IM_EX_CTIBASE Error : public std::runtime_error
    {
        Error( const std::string & message )
           :   std::runtime_error( message )
        {
            // empty
        }
    };


    IM_EX_CTIBASE void initialize( const std::string & yukonBase );

    IM_EX_CTIBASE Buffer encrypt( const EncryptionType type, const Buffer & plainText );

    IM_EX_CTIBASE Buffer decrypt( const EncryptionType type, const Buffer & cipherText );

    /**
     *  Unit Testing
     *  ---
     *  Used to push in mock data for the encrypted xml file where
     *  the decryption key is stored and to signal the reloading of
     *  keys.
     */
    IM_EX_CTIBASE void seedFileData( const EncryptionType type, const Buffer & fileData );
    IM_EX_CTIBASE void unseedFileData( const EncryptionType type );
}
}

