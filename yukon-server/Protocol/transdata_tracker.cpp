
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   transdata_tracker
*
* Date:   8/14/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 21:25:20 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "transdata_tracker.h"
#include "guard.h"
#include "logger.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataTracker::CtiTransdataTracker():
   _identify( "ID\r\n" ),
   _revision( "II\r\n" ),
   _demand_reset( "DR\r\n" ),
   _search_scrolls( "SC\r\n" ),
   _search_alts( "HC\r\n" ),
   _search_comms( "CC\r\n" ),
   _dump_demands( "RC\r\n" ),
   _model_number( "MI\r\n" ),
   _hold_display( "HD\r\n" ),
   _set_display( "SD\r\n" ),
   _change_display( "AD\r\n" ),
   _scroll_display( "RS\r\n" ),
   _get_clock( "GT\r\n" ),
   _set_clock( "TI\r\n" ),
   _alt_display( "ST\r\n" ),
   _test_ram( "RM\r\n" ),
   _diagnose( "DI\r\n" ),
   _reset_fail( "RF\r\n" ),
   _send_comm_buff( "BU\r\n" ),
   _custom_regs( "SS\r\n" ),
   _hang_up( "LO\r\n" ),
   _interval( "IS\r\n" ),
   _diag_status( "SA\r\n" ),
   _channels_enabled( "DC\r\n" ),
   _set_manual_mode( "CM\r\n" ),
   _set_auto_mode( "CA\r\n" ),
   _switch_in( "CI\r\n" ),
   _switch_out( "CO\r\n" ),
   _control_status( "CS\r\n" ),
   _actuate( "SO\r\n" ),
   _control_status_2( "OS\r\n" ),
   _clear_eventlog( "RI\r\n" ),
   _dump_eventlog_rec( "RV\r\n" ),
   _dial_in_status( "DS\r\n" ),
   _set_dial_in_time( "TC\r\n" ),
   _get_dial_in_time( "GC\r\n" ),
   _get_program_id( "PI\r\n" ),
   _test( "\r\n" ),
   _prot_start( "C" ),
   _good_return( "Ok\r\n?" ),
   _prot_message( "ol \r\n" ),
   _retry( "Retry\r\n" )

{
   _lastState        = 0;

   _finished         = true;
   _didSomeWork      = false;
   _moveAlong        = false;
   _weHaveData       = false;

   _storage          = new BYTE[1500];    //supposedly, we'd only need 1k, but...
   _lastCommandSent  = new BYTE[30];

   _password         = "22222222\r\n";       //silly hard-codedness for now

   //init everything
   reset();

}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataTracker::~CtiTransdataTracker()
{

}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{
   bool  datalinkDone;
   BYTE  temp[2000];
   int   bytes = 0;

   _datalink.readMsg( xfer, status );

   datalinkDone = _datalink.isTransactionComplete();

   if( datalinkDone )
   {
      _datalink.retreiveData( temp, &bytes );

      if( bytes != 0 )
      {
         memcpy( _storage + _bytesReceived, temp, bytes );
         _bytesReceived += bytes;

         processData( _storage );
      }
      else
      {
         setNextState();
      }

      datalinkDone = false;
   }
   else
   {
      if( _datalink.getError() == failed )
      {
         setError();
         _waiting = false;
      }
   }

   return( _finished );
}

//=====================================================================================================================
//search for one of our magic conditions that says 'success'
//=====================================================================================================================

bool CtiTransdataTracker::processData( BYTE *_storage )
{
   bool  result = false;
   int   index;
   char  temp[7];

   //copy the last little chunk of the data
   memset( temp, '\0', sizeof( temp ));
   index = _bytesReceived - 5;
   memcpy( temp, _storage + index, 5 );

   if(( strstr( temp, _good_return ) != NULL ) ||
      ( strstr( temp, _prot_message ) != NULL )
      )
   {
      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         _didSomeWork = true;
         _finished = true;
      }

      return( true );
   }

   //this will tell the layer above that we're ready with his data
   if( isCrcValid() )
   {
      setNextState();
      _finished = true;
   }

   if( strstr( ( char *)_storage, _retry ) != NULL )
   {
      reset();
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::setXfer( CtiXfer &xfer, RWCString dataOut, int bytesIn, bool block, ULONG time )
{
   memcpy( xfer.getOutBuffer(), dataOut, strlen( dataOut ) );

   xfer.setMessageStart( true );
   xfer.setOutCount( strlen( dataOut ) );     //there will be a problem with this using RWCStrings
   xfer.setInCountExpected( bytesIn );
   xfer.setInTimeout( time );
   xfer.setNonBlockingReads( block );
}

//=====================================================================================================================
//sequence for the login process
//=====================================================================================================================

bool CtiTransdataTracker::logOn( CtiXfer &xfer )
{
   _finished = false;

   if( _waiting )
   {
      xfer.setOutCount( 0 );
      xfer.setInCountExpected( 1 );
      xfer.setMessageStart( false );
   }
   else
   {
      _waiting = true;

      switch( _lastState )
      {
      case doTest:
         {
            setXfer( xfer, _test, 0, false, 0 );
            _ignore = true;
         }
         break;

      case doPassword:
         {
            setXfer( xfer, "22222222\r\n", strlen( _good_return ), false, 0 );
         }
         break;

      case doIdentify:
         {
            setXfer( xfer, _identify, 10, false, 0 );
            _moveAlong = true;
         }
         break;
      }
   }

   _datalink.buildMsg( xfer );

   return( true );
}

//=====================================================================================================================
//sequence for the scan
//=====================================================================================================================

bool CtiTransdataTracker::general( CtiXfer &xfer )
{
   _finished = false;

   if( _waiting )
   {
      xfer.setOutCount( 0 );
      xfer.setInCountExpected( 1 );
      xfer.setMessageStart( false );
   }
   else
   {
      _waiting = true;

      switch( _lastState )
      {
      case doScroll:
         {
            setXfer( xfer, _search_scrolls, strlen( _search_scrolls ) +  strlen( _good_return ), false, 0 );
         }
         break;

      case doPullBuffer:
         {
            setXfer( xfer, _send_comm_buff, strlen( _send_comm_buff ) + strlen( _good_return ), false, 0 );
         }
         break;

      case doStartProt:
         {
            setXfer( xfer, _prot_start, 1000, true, 2 );
            _moveAlong = true;
         }
      }
   }

   _datalink.buildMsg( xfer );

   return( true );
}

//=====================================================================================================================
//last step, but we'll get no response back
//=====================================================================================================================

bool CtiTransdataTracker::logOff( CtiXfer &xfer )
{
   _finished = false;

   if( _datalink.isTransactionComplete() )
   {
      setXfer( xfer, _hang_up, 0, true, 2 );
      _datalink.buildMsg( xfer );

      _didSomeWork = true;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::isTransactionComplete( void )
{
   return( _finished );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::setNextState( void )
{
   reset();

   if( _lastState == doLogoff )
      _lastState = 0;
   else
      _lastState++;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::retreiveData( BYTE *data )
{
   memcpy( ( void *)data, ( void *)(_storage + 3 ), 1024 );

   _bytesReceived = 0;
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::reset( void )
{
   _failCount = 0;
   _waiting = false;
   _ignore = false;
   _bytesReceived = 0;

   memset( _storage, '\0', 1500 );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataTracker::getError( void )
{
   return( _error );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::injectData( RWCString str )
{
//   _password = str;//we'll come back to this when we figure out the db stuff
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::isCrcValid( void )
{
   BYTEUSHORT  crc;
   BYTEUSHORT  crc2;
   bool        isOk = false;
   BYTE        temp[1024];


   if( _bytesReceived > 1020 )
   {
      memcpy( temp, ( void *)_storage, _bytesReceived - 2 );

      crc.ch[0] = _storage[_bytesReceived - 1];
      crc.ch[1] = _storage[_bytesReceived - 2];

      if( crc.sh == _ymodem.addCRC( temp, _bytesReceived - 2, false ) )
      {
         isOk = true;
      }

      _didSomeWork = true;
   }

   return false;         //just for now
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::setError( void )
{
   if( ++_failCount > 3 )
      _error = failed;
   else
      _error = working;
}




























































/*                     8/18
bool CtiTransdataTracker::processData( BYTE *_storage )
{
   bool  result = false;
   int   bytes = xfer.getInCountActual();
   int   index;
   char  temp[7];

   if( bytes )
   {
      //copy the data into our main container
      memcpy( ( _storage + _bytesReceived ), xfer.getInBuffer(), bytes );

      _bytesReceived += bytes;

      if( _bytesReceived >= 5 )
      {
         //copy the last little chunk of the data
         memset( temp, '\0', sizeof( temp ));
         index = _bytesReceived - 5;
         memcpy( temp, _storage + index, 5 );
      }

      _failCount = 0;
   }
   else
   {
      _failCount++;

      //we've not heard any reply, let's resend
      if( _failCount >= 4 )
      {
         reset();
      }
   }

   //search for one of our magic conditions that says 'success'
   if(( strstr( temp, _good_return ) != NULL ) ||
      ( strstr( temp, _prot_message ) != NULL ) ||
//      ( isCrcValid() ) ||
      ( _ignore ))
   {
      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         _didSomeWork = true;

         result = true; //remove?
      }
   }
   else if( strstr( temp, _retry ) != NULL )
   {
      reset();
   }

   return( false );
}
*/




/*
bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{
   bool  result = false;
   int   bytes = xfer.getInCountActual();
   char  *ptr;

   if( bytes )
   {
      //look for nulls and replace them with spaces
      for( int index = 0; index < 5; index++ )
      {
         ptr = strchr( (const char *)xfer.getInBuffer(), '\0' );

         if( ptr != NULL )
            *ptr = ' ';
         else
            break;
      }

      memcpy( ( _storage + _bytesReceived ), xfer.getInBuffer(), bytes );

      _bytesReceived += bytes;
      _failCount = 0;
   }
   else
   {
      _failCount++;

      //we've not heard any reply, let's resend
      if( _failCount >= 4 )
      {
         reset();
      }
   }

   if(( strstr( ( char *)_storage, _good_return ) != NULL ) ||
      ( strcmp( ( char *)_storage, _prot_message ) == 0 ) ||
      ( _ignore ))
   {
      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         result = true;
      }
   }
   else if( strstr(( char *)_storage, _retry ) != NULL )
   {
      reset();
   }

   return( result );
}
*/
