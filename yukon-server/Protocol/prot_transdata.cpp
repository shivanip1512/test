
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   prot_transdata
*
* Date:   7/16/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/10/06 15:18:59 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <rw/cstring.h>

#include "guard.h"
#include "logger.h"
#include "prot_transdata.h"

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata::CtiProtocolTransdata()
{
   _finished = false;
   _weHaveData = false;

   _dataSize = 0;
   _storage = new BYTE[4000];
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolTransdata::~CtiProtocolTransdata()
{
   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::generate( CtiXfer &xfer )
{
   _application.generate( xfer );

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::decode( CtiXfer &xfer, int status )
{
   _application.decode( xfer, status );

   if( _application.isTransactionComplete() )
   {
      //
      //get our buffer filled from below
      //
      _numBytes = _application.retreiveData( _storage );
      _finished = true;


/*
      if( !_application.getConverted().empty() )
      {
         memcpy( _storage, &_application.getConverted()[0], _application.getConverted().size() );
         _dataSize = _application.getConverted().size();
         _finished = true;
      }
*/
   }

   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::recvOutbound( OUTMESS *OutMessage )
{
   return( 1 );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiProtocolTransdata::sendCommResult( INMESS *InMessage )
{
   BYTE *ptr = InMessage->Buffer.InMessage;
/*
   if( !_application.getConverted().empty() )
      memcpy( ptr, &_application.getConverted()[0], _application.getConverted().size() );
*/

   //
   //put the data we got from below into the inmess
   //
   memcpy( ptr, _storage, _numBytes );
   InMessage->InLength = _numBytes;

//   _finished = true;

   return( NORMAL );
}

//=====================================================================================================================
//we're going to take the raw data that we got back from porter and bust it up into messages and stick them into
//a vector, pass them back up to the device, and we're done from here down...
//=====================================================================================================================

vector<CtiTransdataData *> CtiProtocolTransdata::resultDecode( INMESS *InMessage )
{
   BYTE  *ptr;

   while( *InMessage->Buffer.InMessage )
   {
      ptr = ( unsigned char*)( strchr( (const char*)InMessage->Buffer.InMessage, '\n' ));

      _converted = new CtiTransdataData( InMessage->Buffer.InMessage );

      _transVector.push_back( _converted );

      if( ptr != NULL )
         ++ptr;
   }

   return( _transVector );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiProtocolTransdata::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::injectData( RWCString str )
{
   _application.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::reinitalize( void )
{
   _application.reinitalize();

   destroyMe();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiProtocolTransdata::destroyMe( void )
{
   delete [] _storage;
}
