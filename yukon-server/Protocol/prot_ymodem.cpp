
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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2004/02/16 20:53:07 $
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

CtiProtocolYmodem::CtiProtocolYmodem():
   _storage( NULL )
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
   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::reinitalize( void )
{
   _lastState     = doStart;

   _failCount     = 0;
   _bytesReceived = 0;
   _acks          = 0;
   _reqAcks       = 0;

   _finished      = false;
   _start         = true;

   if( _storage != NULL )
   {
      delete [] _storage;
   }

   _storage = CTIDBG_new BYTE[Storage_size];
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::generate( CtiXfer &xfer, int reqAcks )
{
   _bytesExpected = Packet_size;
   
   if( _start )
   {
      setXfer( xfer, Crcnak, _bytesExpected, false, 0 );
      setAcks( reqAcks );     //when we start the protocol, set how many times we think we'll need to ack
      _acks = 0;
      _start = false;
   }
   else
   {
      setXfer( xfer, Ack, _bytesExpected, false, 0 );
      _acks++; 
   }
   
   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolYmodem::decode( CtiXfer &xfer, int status )
{
   if( xfer.getInCountActual() >= _bytesExpected )
   {
      if( _bytesExpected < Storage_size )
      {
         memcpy( _storage, xfer.getInBuffer(), xfer.getInCountActual() );
         _bytesReceived = xfer.getInCountActual();
      }
      else
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
             CtiLockGuard<CtiLogger> doubt_guard(dout);
             dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
         }
      }
      
      _finished = true;
   }
   else
   {
      setError();
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
      //do the 'front & end shaving' here instead of in tracker....
      memcpy( data, _storage + 3, _bytesReceived - 5 );
      *bytes = _bytesReceived - 5;

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

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setError( void )
{
   if( ++_failCount > 1 )
      _error = Failed;
   else
      _error = Working;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolYmodem::getError( void )
{
   return( _error );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setStart( bool doSet )
{
   _start = doSet;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolYmodem::getAcks( void )
{
   return( _acks );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolYmodem::setAcks( int acks )
{
   _reqAcks = acks;
}
