
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
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2004/02/02 16:59:29 $
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
   _bytesReceived       = 0;
   _index               = 0;
   _offset              = 0;

   _finished            = false;
   _firstTime           = true;

   if( _storage != NULL )
   {
      delete [] _storage;
   }
   _storage             = CTIDBG_new BYTE[Storage_size];
}

//=====================================================================================================================
//gotta send the commands one char at a time so the meter hears us
//=====================================================================================================================

RWCString CtiTransdataDatalink::buildMsg( RWCString command, RWCString wantToGet )
{
   RWCString cmd;
   memset( _storage, '\0', Storage_size );
   _lookFor = wantToGet;
   _finished = false;

   if( _index < command.length() )
   {
      cmd = RWCString(command.data()[_index]);
      _index++;
   }
   else
   {
      _index = 0;
   }
    
   return( cmd );
}

//=====================================================================================================================
//read in one char at a time, tack 'em all together, see if they match what we're looking for
//=====================================================================================================================

bool CtiTransdataDatalink::readMsg( CtiXfer &xfer, int status )
{
   if( xfer.getInCountActual() > 0 )
   {
      //tack on the new byte(s) we got from the meter
      _received.insert( _offset, ( const char*)xfer.getInBuffer(), xfer.getInCountActual() );
      _offset += xfer.getInCountActual();
   
      if( _received.contains( ( const char*)_lookFor, RWCString::exact ) )
      {
         memcpy( _storage, _received, _received.length() );
         _bytesReceived += _received.length();

         //clear what we've got in there
         _received.remove( 0, _received.length() );
         _offset = 0;
         _index = 0;

         _finished = true;
         _failCount = 0;
      }
      else
      {
         setError();
      }
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


