
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
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2003/10/30 15:02:50 $
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
   _good_return( "Ok\r\n?" ),
   _prot_message( "ol \r\n" ),
   _retry( "Retry\r\n" )

{
   reinitalize();

   reset();

}

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataTracker::~CtiTransdataTracker()
{
   destroy();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::destroy( void )
{
   _ymodem.destroy();
   _datalink.destroy();

   if( _storage )
   {
      delete [] _storage;
      _storage = NULL;
   }

   if( _meterData )
   {
      delete [] _meterData;
      _meterData = NULL;
   }

   if( _lastCommandSent )
   {
      delete [] _lastCommandSent;
      _lastCommandSent = NULL;
   }
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::reinitalize( void )
{
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track reinit" << endl;
   }
   
   _ymodem.reinitalize();
   _datalink.reinitalize();


   _lastState        = 0;
   _meterBytes       = 0;

   _finished         = true;
   _moveAlong        = false;
   _goodCRC          = false;
   _ymodemsTurn      = false;

   _storage          = new BYTE[4000];    //supposedly, we'd only need 1k, but...
   _meterData        = new BYTE[4000];
   _lastCommandSent  = new BYTE[30];

   _password         = "22222222\r\n";       //silly hard-codedness for now
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{
   BYTE  temp[500];
   int   bytes = 0;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track decode" << endl;
   }

   if( _ymodemsTurn )
   {
      _ymodem.decode( xfer, status );

      if( _ymodem.isTransactionComplete() )
      {
         _goodCRC = _ymodem.isCrcValid();

         if( _goodCRC )
         {
            _ymodem.retreiveData( _meterData, &_meterBytes );
            setNextState();
            _ymodemsTurn = false;//this gets reset!
            _finished = true;///////////////////////////////
         }
      }
   }
   else
   {
      _datalink.readMsg( xfer, status );

      if( _datalink.isTransactionComplete() )
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
      }
   }

   if( _datalink.getError() == failed )
   {
      setError();
      _waiting = false;
   }

   return( _finished );
}

//=====================================================================================================================
//search for one of our magic conditions that says 'success' of this layer (not ymodem)
//=====================================================================================================================

bool CtiTransdataTracker::processData( BYTE *_storage )
{
   int   index;
   char  temp[7];

   //copy the last little chunk of the data
   memset( temp, '\0', sizeof( temp ));
   index = _bytesReceived - 5;
   memcpy( temp, _storage + index, 5 );

   if(( strstr( temp, _good_return ) != NULL ) ||
      ( strstr( temp, _prot_message ) != NULL ))
   {
      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         _finished = true;
      }

      return( true );
   }

   if( strstr( ( char *)_storage, _retry ) != NULL )
   {
      reset();
   }

   return( false );
}


//=====================================================================================================================
//sequence for the login process
//=====================================================================================================================

bool CtiTransdataTracker::logOn( CtiXfer &xfer )
{
   _finished = false;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track logon" << endl;
   }

   if( _waiting )
   {
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " track logon wait" << endl;
      }
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
         }
         break;

      case doTest2:
         {
            setXfer( xfer, _test, 0, false, 0 );
         }
         break;

      case doPassword:
         {
            setXfer( xfer, "22222222\r\n", strlen( _good_return ), false, 1 );
         }
         break;

      case doIdentify:
         {
            setXfer( xfer, _identify, 50, true, 1 );
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

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track general" << endl;
   }

   if( _waiting )
   {
      if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
      {
         CtiLockGuard<CtiLogger> doubt_guard(dout);
         dout << RWTime() << " track general wait" << endl;
      }
      xfer.setOutCount( 0 );
      xfer.setInCountExpected( 1 );
      xfer.setMessageStart( false );
   }
   else
   {
      switch( _lastState )
      {
/*
      case doEnabledChannels:
         {
            setXfer( xfer, _channels_enabled, 9, false, 0 );
            _waiting = true;
            _datalink.buildMsg( xfer );
         }
         break;
*/
      case doScroll:
         {
            setXfer( xfer, _search_scrolls, 9, true, 1 );
            _datalink.buildMsg( xfer );
            _waiting = true;
         }
         break;

      case doPullBuffer:
         {
            setXfer( xfer, _send_comm_buff, 9, true, 1 );
            _datalink.buildMsg( xfer );
            _waiting = true;
         }
         break;

      case doStartProt:
         {
            if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
            {
               CtiLockGuard<CtiLogger> doubt_guard(dout);
               dout << RWTime() << " track general doStartProt" << endl;
            }
            _ymodem.generate( xfer );
            _ymodemsTurn = true;
            _moveAlong = true;
         }
      }
   }

   return( true );
}

//=====================================================================================================================
//last step, but we'll get no response back
//=====================================================================================================================

bool CtiTransdataTracker::logOff( CtiXfer &xfer )
{
   _finished = false;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track logoff" << endl;
   }

   setXfer( xfer, _hang_up, 0, true, 2 );
   
   _datalink.buildMsg( xfer );
   _finished = true;

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

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track state " << _lastState << endl;
   }

}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataTracker::retreiveData( BYTE *data )
{
   int temp = _meterBytes;

   memcpy( ( void *)data, ( void *)(_meterData + 3 ), _meterBytes /*1024*/ );

   _meterBytes = 0;
   
   _goodCRC = false;
   _finished = false;

   return( temp );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::reset( void )
{
   _failCount = 0;
   _waiting = false;
   _ymodemsTurn = false;
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

void CtiTransdataTracker::setError( void )
{
   if( ++_failCount > 3 )
      _error = failed;
   else
      _error = working;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::goodCRC( void )
{
   return( _goodCRC );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::setXfer( CtiXfer &xfer, RWCString dataOut, int bytesIn, bool block, ULONG time )
{
   memcpy( xfer.getOutBuffer(), dataOut, strlen( dataOut ) );

   _bytesReceived = 0;

   memset( _storage, '\0', 4000 );

   xfer.setMessageStart( true );
   xfer.setOutCount( strlen( dataOut ) );     //there will be a problem with this using RWCStrings
   xfer.setInCountExpected( bytesIn );
   xfer.setInTimeout( time );
   xfer.setNonBlockingReads( block );
}



































































