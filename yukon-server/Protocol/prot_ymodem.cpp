
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_ymodem
*
* Date:   8/4/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/12/18 15:57:18 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*
*  NOTE: this is just a shell of the ymodem protocol and only incorporates what
         the transdata mk-V meter needs.  if you need more, you'll have to fill in
         some more
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_ymodem.h"

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::CtiProtocolYmodem()
{
   reinitalize();
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::~CtiProtocolYmodem()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::destroy( void )
{
   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::reinitalize( void )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ymodem reinit" << endl;
   }

   _lastState     = doStart;
   _bytesReceived = 0;
   _finished      = false;
   _storage       = new BYTE[Storage_size];
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::generate( CtiXfer &xfer, int bytesWanted, int timeToWait )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ymodem generate" << endl;
   }

   if( _lastState == doAck )
   {
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ymodem set ACK" << endl;
      }
      
      _bytesExpected = bytesWanted;
      _lastState = doStart;
      setXfer( xfer, Ack, _bytesExpected, false, timeToWait ); //false, 0?
   }
   else
   {
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " ymodem set NAK" << endl;
      }
      
      _bytesExpected = bytesWanted;
      _lastState = doAck;
      setXfer( xfer, Crcnak, _bytesExpected, false, timeToWait );
   }

   return( false );
}

//=====================================================================================================================
//real temp quick hack
//=====================================================================================================================

bool CtiProtocolYmodem::stopAck( CtiXfer &xfer, int bytesWanted, int timeToWait )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ymodem stopack" << endl;
   }

   _bytesExpected = bytesWanted;
   _lastState = doStart;
   setXfer( xfer, Ack, _bytesExpected, false, timeToWait ); 
      
   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::decode( CtiXfer &xfer, int status )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ymodem decode" << endl;
   }
   
   if( xfer.getInCountActual() >= _bytesExpected )
   {
      if( _bytesExpected )
      {
         memcpy( _storage, xfer.getInBuffer(), xfer.getInCountActual() );
         _bytesReceived = xfer.getInCountActual();
      }
      
      _finished = true;
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::retreiveData( BYTE *data, int *bytes )
{
   if( _storage != NULL )
   {
      memcpy( data, _storage, _bytesReceived );
      *bytes = _bytesReceived;

      memset( _storage, '\0', Storage_size );

      _bytesReceived = 0;
      _finished = false;
   }
}

//=====================================================================================================================
// from the XMODEM/YMODEM Protocol Reference
// www.techfest.com
//=====================================================================================================================

unsigned short CtiProtocolYmodem::updateCRC( BYTE c, unsigned short crc )
{
   int            count;
   unsigned short data = c;

   for( count = 8; --count >= 0; )
   {
      if( crc & 0x8000 )
      {
         crc <<= 1;
         crc += (( ( data <<= 1 ) & 0400 ) != 0 );
         crc ^= 0x1021;
      }
      else
      {
         crc <<= 1;
         crc += (( ( data <<= 1 ) & 0400 ) != 0 );
      }
   }

   return crc;
}

//=====================================================================================================================
//=====================================================================================================================

unsigned short CtiProtocolYmodem::calcCRC( BYTE *ptr, int count )
{
   int            index = 0;
   unsigned short crc   = 0;

   for( index; index < count; index++, ptr++ )
   {
      crc = updateCRC( *ptr, crc );
   }

   return( crc );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::isCrcValid( void )
{
   BYTEUSHORT  crc;
   bool        isOk = false;
   BYTE        temp[3000];

   if( _bytesReceived > 1020 )
   {
      memset( temp, '\0', sizeof( temp ) );
      memcpy( temp, ( void *)_storage, _bytesReceived - 2 );

      crc.ch[0] = _storage[_bytesReceived - 1];
      crc.ch[1] = _storage[_bytesReceived - 2];
      
      if( crc.sh == calcCRC( temp + 3, _bytesReceived - 3 ))    //fixme.. should not use hardcoded stuff if pos.
      {
         isOk = true;
      }
   }

   return( isOk );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setXfer( CtiXfer &xfer, BYTE dataOut, int bytesIn, bool block, ULONG time )
{
   xfer.getOutBuffer()[0] = dataOut;
   xfer.setMessageStart( true );
   xfer.setOutCount( sizeof( dataOut ) );
   xfer.setInCountExpected( bytesIn );
   xfer.setInTimeout( time );
   xfer.setNonBlockingReads( block );
}

