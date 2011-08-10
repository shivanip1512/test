#include "precompiled.h"

#include "encryption_cbcrbt.h"
#include "encryption_oneway.h"
#include "encryption_oneway_message.h"
#include "CtiTime.h"
#include "mutex.h"
#include "guard.h"

#include <openssl/md5.h>

#include <cstddef>



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
                                                 char               * outMessage )
{
    // update timestamp components
    CtiTime  lastXmitTime;
    unsigned utcCounter;

    GetNextSequenceValues( timeNow, &lastXmitTime, &utcCounter );

    // parse incoming message
    std::size_t keyStart        = msgLength - 16;
    std::size_t passwordLength  = inMessage[ keyStart - 1 ];
    std::size_t passwordStart   = keyStart - 1 - passwordLength;

    CbcRbtEncryption::Key   parentKey;

    const unsigned char * cuc_inMessage = reinterpret_cast<const unsigned char *>( inMessage );
    unsigned char       * uc_outMessage = reinterpret_cast<unsigned char *>( outMessage );

    {
        // get the IV from the supplied password
        CbcRbtEncryption::Key   iv;

        MD5( cuc_inMessage + passwordStart, passwordLength, iv );

        // decrypt the supplied key with the IV and the Yukon Encryption key

        CbcRbtEncryption    decryptor( OneWayEncryption::_yukonEncryptionKey , iv );

        decryptor.decrypt( cuc_inMessage + keyStart, 16, parentKey );
    }

    // Encrypt the message

    OneWayEncryption    oneWay( parentKey );                

    oneWay.encrypt( lastXmitTime, utcCounter, cuc_inMessage, passwordStart, uc_outMessage );

    return passwordStart + 10;
}

