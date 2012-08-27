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

    unsigned char   * uc_outMessage = reinterpret_cast<unsigned char *>( outMessage );
    unsigned char   * uc_inMessage  = reinterpret_cast<unsigned char *>( inMessage  );

/*
    +-----------+---------+-------------+---------+-------+---------+--------------+ 
    | serial    | start   | message     | stop    | start |  msg    |  key         |
    | patch     | wrapper |             | wrapper | index |  length |  (16 bytes)  |
    +-----------+---------+-------------+---------+-------+---------+--------------+ 
 
    serial patch    : optional prefix string                   -- variable length
    start wrapper   : expresscom message wrapper byte          -- 1 byte (only present in non-RDS messages)
    message         : the message to encrypt                   -- variable length
    stop wrapper    : expresscom message wrapper byte          -- 1 byte (only present in non-RDS messages)
    start index     : index of the first byte of the message   -- 1 byte
    msg length      : length of the message to encrypt         -- 1 byte
    key             : the encryption key                       -- 16 bytes
 
*/ 
    // parse incoming message

    std::size_t keyStart        = msgLength - 16;
    std::size_t startIndex      = inMessage[keyStart - 2];
    std::size_t xcomMsgLength   = inMessage[keyStart - 1];

    CbcRbtEncryption::Key   encryptionKey;
    std::copy( inMessage + keyStart, inMessage + msgLength, encryptionKey );

    // Wrapper bytes
    //  ( startIndex + xcomMsgLength ) is the index of the byte after the end of the message
    //  ( keyStart - 2 ) is the index of the storage of the startIndex byte
    // 
    // if they are not equal then there is an extra byte between them (the stop wrapper)
    //    and we have a paging message.
    // if they are equal then there is no extra byte and we have a RDS message

    const bool noWrapper = ( ( startIndex + xcomMsgLength ) == ( keyStart - 2 ) );   // RDS message

    // copy the serial patch (and start wrapper if it exists) to outmessage

    std::copy( inMessage, inMessage + startIndex, uc_outMessage );

    std::size_t     outLength   = startIndex;
    unsigned char * encryptMe   = uc_inMessage + startIndex;

    // Encrypt the message
    unsigned long       counter = gConfigParms.getValueAsULong( "ONE_WAY_ENCRYPT_KEY_ROLL" );   // defaults to zero

    OneWayEncryption    oneWay( counter, encryptionKey );
    unsigned char       messageBuffer[300];

    oneWay.encrypt( lastXmitTime, utcCounter, encryptMe, xcomMsgLength, messageBuffer );

    std::size_t encryptedLength = xcomMsgLength + 10;

    if ( format == Ascii )
    {
        encryptedLength = convertToAscii( messageBuffer, encryptedLength, uc_outMessage + outLength );
    }
    else    // format == Binary
    {
        std::copy( messageBuffer, messageBuffer + encryptedLength, uc_outMessage + outLength );
    }
    outLength += encryptedLength;

    if ( !noWrapper )
    {
        uc_outMessage[ outLength ] = inMessage[ startIndex + xcomMsgLength ];
        outLength++;
    }

    return outLength;
}


std::size_t OneWayMsgEncryption::convertToAscii( const unsigned char  * fromBuffer,
                                                 const std::size_t      byteCount,
                                                 unsigned char        * toBuffer )
{
    static const char convert[16] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    for ( std::size_t i = 0; i < byteCount; ++i )
    {
        unsigned char high4bits = ( fromBuffer[i] >> 4 ) & 0x0f;
        unsigned char low4bits  =   fromBuffer[i]        & 0x0f;

        *toBuffer++ = convert[ high4bits ];
        *toBuffer++ = convert[ low4bits  ];
    }
    *toBuffer++ = 0;    // NULL Terminate

    return byteCount * 2;
}

