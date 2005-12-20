
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
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2005/12/20 17:19:58 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "yukon.h"
#include "guard.h"
#include "logger.h"
#include "transdata_datalink.h"
#include "rwutil.h"

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

string CtiTransdataDatalink::buildMsg( string command, string wantToGet )
{
   string cmd;
   memset( _storage, '\0', Storage_size );
   _lookFor = wantToGet;
   _finished = false;

   if( _index < command.length() )
   {
      cmd = char2string( command[_index] );
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
   
      if( _received.find(_lookFor) )
      {
         memcpy( _storage, _received.c_str(), _received.length() );
         _bytesReceived += _received.length();

         //clear what we've got in there
         _received.erase( 0, _received.length() );
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
   else                             // 20051014 CGP Added this else clause to keep us from becoming permanent fixtures in the code.
   {
      setError();
   }


   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::isTransactionComplete( void )
{
   if( getError() == failed )      // 20051014 CGP Added this to keep us from becoming permanent fixtures in the code.
      _finished = true;

   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::retreiveData( BYTE *data, int *bytes )
{
   if( _storage != NULL )
   {
      if( data != NULL )
      {
         memcpy( data, _storage, _bytesReceived );
         *bytes = _bytesReceived;
      }

      memset( _storage, '\0', Storage_size );

      _bytesReceived = 0;
      _finished = false;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::setError( void )
{
   if( ++_failCount > 5 )                       // 20051014 CGP Changed the constant to 5. //   3 )
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


