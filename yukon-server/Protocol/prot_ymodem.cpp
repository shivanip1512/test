
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
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/10/06 15:18:59 $
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
   _lastState = doStart;
   _bytesReceived = 0;
   _finished = false;
   _storage = new BYTE[3000];
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem::~CtiProtocolYmodem()
{
   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::destroyMe( void )
{
   delete [] _storage;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::reinitalize( void )
{
   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::generate( CtiXfer &xfer )
{
   if( _lastState == doAck )
   {
      setXfer( xfer, Ack, 0, false, 0 );
      _lastState = doStart;
      _finished = true;
   }
   else
   {
      setXfer( xfer, Crcnak, 1029, false, 5 );
      _lastState = doAck;
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::decode( CtiXfer &xfer, int status )
{
   if( xfer.getInCountActual() >= 1000 )
   {
      memcpy( _storage, xfer.getInBuffer(), xfer.getInCountActual() );

      _bytesReceived = xfer.getInCountActual();
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

      memset( _storage, '\0', sizeof( _storage ));

      _bytesReceived = 0;
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
// possible crap
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
   BYTEUSHORT  crc2;
   bool        isOk = false;
   BYTE        temp[3000];

   if( _bytesReceived > 1020 )
   {
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
//   memcpy( xfer.getOutBuffer(), dataOut, sizeof( dataOut ) );
   xfer.getOutBuffer()[0] = dataOut;
   xfer.setMessageStart( true );
   xfer.setOutCount( sizeof( dataOut ) );
   xfer.setInCountExpected( bytesIn );
   xfer.setInTimeout( time );
   xfer.setNonBlockingReads( block );
}


