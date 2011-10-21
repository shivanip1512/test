#include "precompiled.h"

#include "encryption_cbcrbt.h"
#include "encryption_cmac.h"
#include "encryption_oneway.h"
#include "encryption_oneway_message.h"
#include "CtiTime.h"
#include "mutex.h"
#include "logger.h"
#include "guard.h"
#include "CParms.h"

#include <openssl/md5.h>

#include <cstddef>
#include <algorithm>
#include <ostream>



void GetNextSequenceValues( const CtiTime & timeNow, CtiTime * seqTime, unsigned * seqCounter )
{
    static CtiTime  _lastXmitTime = CtiTime::neg_infin;
    static unsigned _utcCounter   = 0;
    static CtiMutex _mutex;

    CtiLockGuard<CtiMutex> mutexGuard(_mutex);

    _utcCounter     = ( _lastXmitTime == timeNow ) ? _utcCounter + 1 : 0 ;
    _lastXmitTime   = timeNow;

    *seqTime    = _lastXmitTime;
    *seqCounter = _utcCounter;
}


std::size_t OneWayMsgEncryption::encryptMessage( const CtiTime      & timeNow,
                                                 char               * inMessage,
                                                 const std::size_t    msgLength,
                                                 char               * outMessage,
                                                 const OutputFormat   format )
{
    // update timestamp components
    CtiTime  lastXmitTime;
    unsigned utcCounter;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    // parse incoming message
    std::size_t keyStart        = msgLength - 20;
    std::size_t cmacStart       = msgLength - 4;
    std::size_t passwordLength  = inMessage[ keyStart - 1 ];
    std::size_t passwordStart   = keyStart - 1 - passwordLength;

    CbcRbtEncryption::Key   parentKey;

    const unsigned char * cuc_inMessage = reinterpret_cast<const unsigned char *>( inMessage );
    unsigned char       * uc_outMessage = reinterpret_cast<unsigned char *>( outMessage );

    {
        // get the IV from the supplied password
        CbcRbtEncryption::Key   iv;

        MD5( cuc_inMessage + passwordStart, passwordLength, iv );

        // check the CMAC supplied with the key

        CmacAuthentication::Key     cmac;
        CmacAuthentication          cmacCalculator( OneWayEncryption::_yukonEncryptionKey );

        cmacCalculator.calculate( cuc_inMessage + keyStart, 16, cmac );

        if ( ! std::equal( cuc_inMessage + cmacStart, cuc_inMessage + cmacStart + 4, cmac ) )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);

            dout << CtiTime() << " - *** One-Way Encryption Key - Authentication Failed ***" << std::endl;
        }

        // decrypt the supplied key with the IV and the Yukon Encryption key

        CbcRbtEncryption    decryptor( OneWayEncryption::_yukonEncryptionKey , iv );

        decryptor.decrypt( cuc_inMessage + keyStart, 16, parentKey );
    }

    // Encrypt the message

    unsigned long counter = gConfigParms.getValueAsULong("ONE_WAY_ENCRYPT_KEY_ROLL");   // defaults to zero

    OneWayEncryption    oneWay( counter, parentKey );                
    unsigned char       messageBuffer[300];
    std::size_t         encryptedLength = passwordStart + 10;
    
    oneWay.encrypt( lastXmitTime, utcCounter, cuc_inMessage, passwordStart, messageBuffer );

    if ( format == Ascii )
    {
        static const char convert[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

        for ( std::size_t i = 0; i < encryptedLength; ++i )
        {
            unsigned char high4bits = ( messageBuffer[i] >> 4 ) & 0x0f;
            unsigned char low4bits  =   messageBuffer[i]        & 0x0f;

            *uc_outMessage++ = convert[ high4bits ];
            *uc_outMessage++ = convert[ low4bits  ];
        }
        *uc_outMessage++ = 0; // NULL Terminate
        encryptedLength *= 2;
    }
    else    // format == Binary
    {
        std::copy( messageBuffer, messageBuffer + encryptedLength, uc_outMessage );
    }

    return encryptedLength;
}

