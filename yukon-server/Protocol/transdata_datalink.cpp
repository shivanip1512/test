
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   transdata_datalink
*
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2003/12/16 17:23:04 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "guard.h"
#include "logger.h"
#include "transdata_datalink.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataDatalink::CtiTransdataDatalink()
{
   reinitalize();
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataDatalink::~CtiTransdataDatalink()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::destroy( void )
{
   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::reinitalize( void )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " link reinit" << endl;
   }
   
   _failCount           = 0;
   _error               = 0;
   _bytesExpected       = 0;
   _bytesReceived       = 0;

   _finished            = true;

   _storage             = new BYTE[Storage_size];
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::buildMsg( CtiXfer &xfer )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " link build" << endl;
   }

   memset( _storage, '\0', Storage_size );
   _finished = false;
   _bytesExpected = xfer.getInCountExpected();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::readMsg( CtiXfer &xfer, int status )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " link read" << endl;
   }

   if(( xfer.getInCountActual() + _bytesReceived ) >= _bytesExpected )
   {
      _finished = true;
      _bytesExpected = 333;
   }
   else
   {
      setError();
   }

   if( xfer.getInCountActual() )
   {
//      memcpy( ( _storage + _bytesReceived ), xfer.getInBuffer(), xfer.getInCountActual() );
      memcpy( _storage, xfer.getInBuffer(), xfer.getInCountActual() );
      _bytesReceived += xfer.getInCountActual();
   }

   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::retreiveData( BYTE *data, int *bytes )
{
   if( _storage != NULL )
   {
      memcpy( data, _storage, _bytesReceived );
      *bytes = _bytesReceived;

      memset( _storage, '\0', Storage_size );

      _bytesExpected = 0;
      _bytesReceived = 0;
      _finished = false;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::setError( void )
{
   if( ++_failCount > 3 )
      _error = failed;
   else
      _error = working;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataDatalink::getError( void )
{
   return( _error );
}


