
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   transdata_application
*
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2003/08/28 21:25:20 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "transdata_application.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::CtiTransdataApplication()
{
   _lastState     = 0;
   _storage       = new BYTE[1024];
   _dataProcessed = false;
   _finished      = true;
   _weHaveData    = false;
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataApplication::~CtiTransdataApplication()
{

}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::generate( CtiXfer &xfer )
{
   _finished = false;

   switch( _lastState )
   {
   case doLogOn:
      _tracker.logOn( xfer );
      break;

   case doTalk:
      _tracker.general( xfer );    //this should have other *things* it can do...
      break;

   case doLogOff:
      _tracker.logOff( xfer );
      break;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::decode( CtiXfer &xfer, int status )
{
   bool trackerDone;

   _tracker.decode( xfer, status );

   trackerDone = _tracker.isTransactionComplete();

   if( trackerDone )
   {
      if( processData( xfer.getInBuffer() ))  //temp!
         setNextState();
   }

   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::injectData( RWCString str )
{
   _tracker.injectData( str );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataApplication::setNextState( void )
{
   if( _lastState == doLogOff )
      _lastState = doLogOn;
   else
      _lastState++;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataApplication::processData( BYTE *data )
{
//      _finished = true;  //this needs to happen when we really have the big data

   return( true );
}

