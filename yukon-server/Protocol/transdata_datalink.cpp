
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
* REVISION     :  $Revision: 1.14 $
* DATE         :  $Date: 2004/01/16 22:44:29 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "guard.h"
#include "logger.h"
#include "transdata_datalink.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataDatalink::CtiTransdataDatalink():
   _storage( NULL )
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
   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::reinitalize( void )
{
   _failCount           = 0;
   _error               = 0;
   _bytesExpected       = 0;
   _bytesReceived       = 0;

   _finished            = false;

   if( _storage != NULL )
   {
      delete [] _storage;
   }
   _storage             = CTIDBG_new BYTE[Storage_size];
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::buildMsg( CtiXfer &xfer )
{
   memset( _storage, '\0', Storage_size );
   _finished = false;
   _bytesExpected = xfer.getInCountExpected();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::readMsg( CtiXfer &xfer, int status )
{
   if(( xfer.getInCountActual() + _bytesReceived ) >= _bytesExpected )
   {
      _finished = true;
      _bytesExpected = 333;
      _failCount = 0;
   }
   else
   {
      setError();
   }

   if( xfer.getInCountActual() )
   {
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


