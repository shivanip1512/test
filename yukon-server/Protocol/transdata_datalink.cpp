
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
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/06 19:50:51 $
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
   _bytesReceived    = 0;
   _lastState        = 0;
   _failCount        = 0;

   _didSomeWork      = false;
   _waiting          = false;
   _moveAlong        = false;

   _storage          = new BYTE[500];
   _lastCommandSent  = new BYTE[30];

   _password         = "22222222\r\n";       //silly hard-codedness for now
}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataDatalink::~CtiTransdataDatalink()
{
/*
   if( _storage != NULL )
      delete _storage;
*/
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::buildMsg( CtiXfer &xfer, RWCString data, int bytesIn, int bytesOut )
{
   memcpy( xfer.getOutBuffer(), data, bytesOut );

   xfer.setOutCount( bytesOut );
   xfer.setInCountExpected( bytesIn );

   return( true );
}

//=====================================================================================================================
//
//FIXME: we need to give up gracefully at somepoint....
//
//=====================================================================================================================

bool CtiTransdataDatalink::decode( CtiXfer &xfer, int status )
{
   bool result = false;

   if( xfer.getInCountActual() )
   {
      _storage[_bytesReceived] = xfer.getInBuffer()[0];
      _bytesReceived++;
      _failCount = 0;
   }
   else
   {
      _failCount++;
   }

   //we've not heard any reply, let's resend
   if( _failCount >= 3 )
   {
      _failCount = 0;
      _waiting = false;
   }

   if( strchr( ( char *)_storage, '?' ) )
   {
      CtiLockGuard< CtiLogger > doubt_guard( dout );
      dout << endl;
      dout << RWTime::now() << "                Setting next state..." << endl;

      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         result = true;
      }

      _waiting = false;    //no bytes came back, let's resend
   }

   return( result );
}

//=====================================================================================================================
//sequence for the login process
//=====================================================================================================================

bool CtiTransdataDatalink::logOn( CtiXfer &xfer )
{
   if( _waiting )
   {
      xfer.setOutCount( 0 );
      xfer.setInCountExpected( 1 );
   }
   else
   {
      switch( _lastState )
      {
      case doPassword:
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
            dout << RWTime::now() << " Sending password" << endl;

            _waiting = buildMsg( xfer, _password, 1, _password.length());
         }
         break;

      case doIdentify:
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
            dout << RWTime::now() << " Sending identify" << endl;

            _waiting = buildMsg( xfer, IDENTIFY, 14, 4 );
            _moveAlong = true;
         }
         break;
      }
   }

   return( true );
}

//=====================================================================================================================
//sequence for the scan
//=====================================================================================================================

bool CtiTransdataDatalink::general( CtiXfer &xfer )
{
   if( _waiting )
   {
      xfer.setOutCount( 0 );
      xfer.setInCountExpected( 1 );
   }
   else
   {
      switch( _lastState )
      {
      case doScroll:
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
            dout << RWTime::now() << " Sending scroll search" << endl;

            _waiting = buildMsg( xfer, SEARCH_SCROLLS, 4, 4 );
         }
         break;

      case doPullBuffer:
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
            dout << RWTime::now() << " Sending buffer request" << endl;

            _waiting = buildMsg( xfer, SEND_COMM_BUFF, 4, 4 );
         }
         break;

      case doStartProt:
         {
            CtiLockGuard< CtiLogger > doubt_guard( dout );
            dout << endl;
            dout << RWTime::now() << " Sending YMODEM start code" << endl;

            _waiting = buildMsg( xfer, START, 20, 3 );   //guess on the inbound expected
            _moveAlong = true;
         }
      }
   }

   return( true );
}

//=====================================================================================================================
//last step, but we'll get no response back
//=====================================================================================================================

bool CtiTransdataDatalink::logOff( CtiXfer &xfer )
{
   memcpy( xfer.getOutBuffer(), HANG_UP, sizeof( HANG_UP ));

   xfer.setOutCount( 4 );
   xfer.setInCountExpected( 0 );

   _didSomeWork = true;

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::isTransactionComplete( void )
{
   if( _didSomeWork )
   {
      _didSomeWork = false;
      return( true );
   }
   else
   {
      return( false );
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::injectData( RWCString str )
{
//   _password = str;//we'll come back to this when we figure out the db stuff
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::setNextState( void )
{
   reset();

   if( _lastState == doLogoff )
      _lastState = doIdentify;
   else
      _lastState++;
}

//=====================================================================================================================
//=====================================================================================================================

BYTE* CtiTransdataDatalink::retreiveData( void )
{
   _bytesReceived = 0;

   return( _storage );
}

//=====================================================================================================================
//=====================================================================================================================

CtiProtocolYmodem & CtiTransdataDatalink::getYmodemLayer( void )
{
   return _ymodem;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataDatalink::reset( void )
{
   _bytesReceived = 0;
   memset( _storage, NULL, sizeof( _storage ));
}





































































/*
//=====================================================================================================================
//step 1 of logOn
//=====================================================================================================================

bool CtiTransdataDatalink::password( CtiXfer &xfer )
{
   memcpy( xfer.getOutBuffer(), _password, _password.length() );

   xfer.setOutCount( _password.length() );
   xfer.setInCountExpected( 1 );

   return( true );
}

//=====================================================================================================================
//step 2 of logOn
//=====================================================================================================================

bool CtiTransdataDatalink::identify( CtiXfer &xfer )
{
   memset( _storage, NULL, sizeof( _storage ));

   memcpy( xfer.getOutBuffer(), ( BYTE *)IDENTIFY, sizeof( IDENTIFY ));

   xfer.setOutCount( 4 );
   xfer.setInCountExpected( 14 );   //min would be 8 for sn and 2 <cr>'s if the ids are blank

   return( true );
}

//=====================================================================================================================
//last step, but we'll get no response back
//=====================================================================================================================

bool CtiTransdataDatalink::logOff( CtiXfer &xfer )
{
   memcpy( xfer.getOutBuffer(), HANG_UP, sizeof( HANG_UP ));

   xfer.setOutCount( 4 );
   xfer.setInCountExpected( 0 );

   _didSomeWork = true;

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::getScrollData( CtiXfer &xfer )
{
   memcpy( xfer.getOutBuffer(), SEARCH_SCROLLS, sizeof( SEARCH_SCROLLS ));

   xfer.setOutCount( 4 );
   xfer.setInCountExpected( 1 );

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataDatalink::getBuffer( CtiXfer &xfer )
{
   memcpy( xfer.getOutBuffer(), SEND_COMM_BUFF, sizeof( SEND_COMM_BUFF ));

   xfer.setOutCount( 4 );
   xfer.setInCountExpected( 1 );

   return( true );
}
*/
/*
   bool result = false;

   if( xfer.getInCountActual() )
   {
      if( strcmp(( char *)_lastCommandSent, ( char *)_storage ) == 0 )
      {
         setNextState();
         _waiting = false;

         if( _moveAlong )
         {
            _moveAlong = false;
            result = true;
         }
      }
      else
      {
         _storage[_bytesReceived] = xfer.getInBuffer()[0];
         _bytesReceived++;
         _waiting = true;
      }
   }
   else
   {
      _waiting = false;    //no bytes came back, let's resend
   }

   return( result );
}
*/

/*
bool CtiTransdataDatalink::decode( CtiXfer &xfer, int status )
{
   bool result = false;

   if( xfer.getInCountActual() )
   {
      if( xfer.getInBuffer()[0] == QUESTION_MARK )
      {
         setNextState();
         _waiting = false;

         if( _moveAlong )
         {
            _moveAlong = false;
            result = true;
         }
      }
      else if(( xfer.getInBuffer()[0] == 0x0a ) && ( _bytesReceived >= 18 )) //0x0a is a '\n'
      {
         if( strcmp(( char *)_storage, "Start the protocol\r" ) == 0 )
         {
            getYmodemLayer();
            result = true;
         }
         else
         {
            _storage[_bytesReceived] = xfer.getInBuffer()[0];
            _bytesReceived++;
            _waiting = true;
         }
      }
      else
      {
         _storage[_bytesReceived] = xfer.getInBuffer()[0];
         _bytesReceived++;
         _waiting = true;
      }
   }
   else
   {
      _waiting = false;    //no bytes came back, let's resend
   }

   return( result );
}
*/

/*
bool CtiTransdataDatalink::decode( CtiXfer &xfer, int status )
{
   bool result = false;

   if( xfer.getInCountActual() )
   {
      _storage[_bytesReceived] = xfer.getInBuffer()[0];
      _bytesReceived++;
      _waiting = true;
   }
   else
   {
      if( strcmp(( char *)GOOD_RETURN, ( char *)_storage ) == 0 )
      {
         setNextState();

         if( _moveAlong )
         {
            _moveAlong = false;
            result = true;
         }
      }
      _waiting = false;    //no bytes came back, let's resend
   }

   return( result );
}
*/
