
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 14:22:57 $
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
   _failCount           = 0;
   _error               = 0;
   _ioState             = 0;
   _totalBytesExpected  = 0;
   _totalBytesReceived  = 0;
   _transBytesExpected  = 0;

   _finished            = true;

   _storage = new BYTE[1500];
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataDatalink::~CtiTransdataDatalink()
{
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::buildMsg( CtiXfer &xfer )
{
   _finished = false;
   _transBytesExpected = xfer.getInCountExpected();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::readMsg( CtiXfer &xfer, int status )
{
   _totalBytesReceived += xfer.getInCountActual();

   if( _totalBytesReceived >= _totalBytesExpected )
   {
      _finished = true;
   }

   setError();

   //copy the data into our main container
   memcpy( ( _storage + _totalBytesReceived ), xfer.getInBuffer(), xfer.getInCountActual() );

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
      memcpy( data, _storage, _totalBytesReceived );
      *bytes = _totalBytesReceived;

      memset( _storage, '\0', sizeof( _storage ));

      _transBytesExpected = 0;
      _totalBytesReceived = 0;
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


