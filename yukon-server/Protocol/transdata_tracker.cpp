
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
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2003/12/18 15:57:18 $
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
   _prot_message( "protocol" ),
   _retry( "Retry" ),
   _dump( "XXXX\r\n" ),
   _fail( "failed\r\n" ),
   _enter( "Enter" )
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

   if( _lp )
   {
      _lp = NULL;
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
      
   _lastState        = doPassword;
   _error            = working;
   _meterBytes       = 0;

   _finished         = true;
   _moveAlong        = false;
   _goodCRC          = false;
   _ymodemsTurn      = false;

   _dataIsExpected   = false;;
   _hold             = false;

   _lp               = new mark_v_lp;

   _storage          = new BYTE[Storage_size];    //supposedly, we'd only need 1k, but...
   _meterData        = new BYTE[Meter_size];
   _lastCommandSent  = new BYTE[Command_size];
   
//   _password         = "22222222\r\n";       //silly hard-codedness for now
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decodeYModem( CtiXfer &xfer, int status )
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
         if( _dataIsExpected )
         {
            _goodCRC = _ymodem.isCrcValid();

            if( _goodCRC )
            {
               _ymodem.retreiveData( _meterData, &_meterBytes );
               processData( _meterData, _meterBytes );
               setNextState();
               _ymodemsTurn = false;
            }
         }
         else
         {
            setNextState();
         }

         if( _moveAlong )
         {
            _moveAlong = false;
            _finished = true;
         }
      }
   }
   
   return( false );
}
   
//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decodeLink( CtiXfer &xfer, int status )
{
   BYTE  temp[500];
   int   bytes = 0;
   
   _datalink.readMsg( xfer, status );

   if( _datalink.isTransactionComplete() )
   {
      memset( _storage, '\0', Storage_size );

      _datalink.retreiveData( _storage, &_bytesReceived );
      processComms( _storage, _bytesReceived );
   }
   
   if( _datalink.getError() == failed )
   {
      setError();
      _waiting = false;
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{
   if( _error == failed )
      reset();

   switch( _lastState )
   {
   case doPassword:
   case doIdentify:
   case doScroll:
   case doPullBuffer:
   case doEnabledChannels:
   case doIntervalSize:
   case doRecordDump:
   case doRecordNumber:
   case doTime:
      {
         decodeLink( xfer, status );
      }
      break;

   case doStartProt1:
   case doEndProt1:
   case doProt1:
   case doProt2:
   case doProt3:
      {
         decodeYModem( xfer, status );
      }
      break;
   
      //we don't expect to get anything back here
   case doLogoff:
   default:
      break;
   }
   
   return( true );
}

//=====================================================================================================================
//=====================================================================================================================
 
bool CtiTransdataTracker::grabChannels( BYTE *data, int bytes )
{
   char  *ptr = NULL;
   char  *temp = NULL;
   char  fluff[400];
   
   memcpy( fluff, data, bytes );
   ptr = fluff;

   for( ;; )
   {
      temp = strstr( ( const char*)ptr, "DC" );

      if( temp != NULL )
      {
         ptr = temp + 2;
      }
      else
      {
         break;
      }
   }

   for( int index = 0; index < 8; index++ )
   {
      ptr = strstr( (const char*)ptr, " " );

      if( ptr != NULL )
      {
         ptr++;
         
         if( *ptr == index + '0' )
            _lp->enabledChannels[index] = true;
         else
            _lp->enabledChannels[index] = false;
      }
      else
      {
         break;
      }
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================
 
bool CtiTransdataTracker::grabFormat( BYTE *data, int bytes )
{
   char  *ptr = NULL;
   char  *temp = NULL;
   char  fluff[400];
   
   memcpy( fluff, data, bytes );
   ptr = fluff;
   
   for( ;; )
   {
      temp = strstr( ( const char*)ptr, "IS" );

      if( temp != NULL )
      {
         ptr = temp + 2;
      }
      else
      {
         break;
      }
   }

   for( int index = 0; index < 3; index++ )
   {
      ptr = strstr( ( const char*)ptr, "\n" );

      if( ptr != NULL )
      {
         ptr++;
         _lp->lpFormat[index] = atoi( ptr );
         ptr += 2;
      }
      else
      {
         break;
      }
   }
   
   return( true );
}

//=====================================================================================================================
//there may be a better way to get the time of the last complete load profile interval, but I don't know what it is 
//right now, so we'll just ask the meter what time it is and send that up with the other LP data
//=====================================================================================================================

bool CtiTransdataTracker::grabTime( BYTE *data, int bytes )
{
   char           *ptr = NULL;
   char           *temp = NULL;
   char           fluff[400];
   unsigned       timeBits[6];

   memcpy( fluff, data, bytes );
   ptr = fluff;

   //shoud trim off any excess crap in the front....
   //make this a general thing and run everybody through it...!
   for( ;; )
   {
      temp = strstr( ( const char*)ptr, "GT" );

      if( temp != NULL )
      {
         ptr = temp + 2;
      }
      else
      {
         break;
      }
   }
   
   for( int index = 0; index < 6; index++ )
   {
      ptr = strstr( ( const char*)ptr, "\n" );

      if( ptr != NULL )
      {
         ptr++;
         timeBits[index] = atoi( ptr );
         ptr += 2;
      }
   }

   RWTime t( RWDate( timeBits[3], timeBits[4], timeBits[5] + 2000 ), timeBits[2], timeBits[1], timeBits[0] );
   _lp->meterTime = t.seconds();

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::grabReturnedChannels( BYTE *data, int bytes )
{
   char           *ptr = NULL;
   char           *temp = NULL;
   char           fluff[400];

   memcpy( fluff, data, bytes );
   ptr = fluff;

   for( int index = 0; index < 6; index++ )
   {
      ptr = strstr( ( const char*)ptr, "\n" );

      if( ptr != NULL )
      {
         ptr++;
         //
         //this needs work. If the meter doesn't return 4 digits, we'll screw ourselves up by copying a null or
         //something in...
         _lp->numLpRecs = atoi( ptr );
         ptr += 2;
      }
   }


   return( true );
}

//=====================================================================================================================
//search for one of our magic conditions that says 'success' of this layer (not ymodem)
//=====================================================================================================================

bool CtiTransdataTracker::processComms( BYTE *data, int bytes )
{
   bool  valid = false;
   char  *ptr = NULL;
   char  fluff[400];

   if( gotRetry( data, bytes ) )
   {
      reset();
   }
   else if( gotValidResponse( data, bytes ) )
   {
      if( _lastState == doEnabledChannels )
         grabChannels( data, bytes );

      if( _lastState == doIntervalSize )
         grabFormat( data, bytes );

      if( _lastState == doTime )
         grabTime( data, bytes );
      
//      if( _lastState == doRecordNumber )
//         grabReturnedChannels( data, bytes );

      setNextState();

      if( _moveAlong )
      {
         _moveAlong = false;
         _finished = true;
      }

      valid = true;
   }

   return( valid );
}
//=====================================================================================================================
//if we collected billing data, we don't get here as we can work with the raw data later
//if we collected loadprofile data, we need to add our raw data to our lp struct, then copy it back into the main
// meter buffer so it gets passed up the chain correctly...
//=====================================================================================================================

bool CtiTransdataTracker::processData( BYTE *data, int bytes )
{
   bool result = false;

   if( _lastState == doProt2 )
   {
      memset( _meterData, '\0', Meter_size );
      memcpy( _lp->lpData, data + 3, bytes );
      memcpy( _meterData, _lp, sizeof( *_lp ) );   //not completely convinced that this sizeof(*ptr) works....
      _meterBytes = sizeof( *_lp );
      result = true;
   }

   return( result );
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

      setXfer(xfer, "", 1, true, 1 );
      _datalink.buildMsg( xfer );
   }
   else
   {
      _waiting = true;

      switch( _lastState )
      {
         case doPassword:
         {
//            setXfer( xfer, "22222222\r\n", strlen( _good_return ), false, 1 );
            setXfer( xfer, _password, strlen( _good_return ), false, 1 );
            _datalink.buildMsg( xfer );
            _first = true;
         }
         break;

      case doIdentify:
         {
            setXfer( xfer, _identify, 41, true, 1 );
            _datalink.buildMsg( xfer );
            _moveAlong = true;
         }
         break;
      }
   }

   return( true );
}

//=====================================================================================================================
//sequence for the scan for billing
//=====================================================================================================================

bool CtiTransdataTracker::billing( CtiXfer &xfer )
{
   _finished = false;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track billing" << endl;
   }
   
   switch( _lastState )
   {
   case doScroll:
      {
         setXfer( xfer, _search_scrolls, 11, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;

   case doPullBuffer:
      {
         setXfer( xfer, _send_comm_buff, 25, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;

   case doStartProt1:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " track billing doStartProt1" << endl;
         }
         
         _ymodem.generate( xfer, 1029, 5 );
         _dataIsExpected = true;
         _ymodemsTurn = true;
      }
      break;

   case doEndProt1:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " track billing doEndProt1" << endl;
         }
         _ymodem.generate( xfer, 0, 1 );
         _dataIsExpected = false;
         _ymodemsTurn = true;
         _moveAlong = true;
      }
      break;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::loadProfile( CtiXfer &xfer )
{
   _finished = false;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " track lp" << endl;
   }
   
   switch( _lastState )
   {

   case doEnabledChannels:
      {
         setXfer( xfer, _channels_enabled, 20, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;

   case doIntervalSize:
      {
         setXfer( xfer, _interval, 24, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;

   case doTime:
      {
         setXfer( xfer, _get_clock, 50, true, 1 );//24
         _datalink.buildMsg( xfer );
      }
      break;

   case doRecordDump:
      {
         setXfer( xfer, _dump_demands, 47, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;
     
   case doRecordNumber:
      {
         _lp->numLpRecs = calcLPRecs();
         RWCString recNum( CtiNumStr( _lp->numLpRecs ).zpad( 4 ) );
         recNum.append( "\r\n" );
         setXfer( xfer, recNum, 25, true, 1 );  //get this number from layers above
         _datalink.buildMsg( xfer );
      }
      break;

   case doProt1:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " track lp doProt1" << endl;
         }

         _ymodem.generate( xfer, 1029, 5 );
         _dataIsExpected = true;
         _ymodemsTurn = true;
      }
      break;
   
   case doProt2:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " track lp doProt2" << endl;
         }
         _ymodem.generate( xfer, 1029, 5 );
         _dataIsExpected = true;
         _ymodemsTurn = true;
      }
      break;

   case doProt3:
      {
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " track lp doProt3" << endl;
         }
         _ymodem.stopAck( xfer, 0, 0 );
         _dataIsExpected = false;
         _ymodemsTurn = true;
         _moveAlong = true;
      }
      break;
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

void CtiTransdataTracker::setLastLPTime( ULONG lpTime )
{
   _lastLPTime = ( ULONG)lpTime;
}

//=====================================================================================================================
//=====================================================================================================================
 
int CtiTransdataTracker::calcLPRecs( void )
{
   int numberLPRecs = 0;
      
   numberLPRecs = ( _lp->meterTime - _lastLPTime ) / ( _lp->lpFormat[0] * 60 );

   if( numberLPRecs > 9999 )     //the max records that a markV can return
      numberLPRecs = 9999;

   return( numberLPRecs );
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
      _lastState = doPassword;
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

   //this is just the result of a stupid decision... we'll fix it later
   if( _lastState == doStartProt1 )
      memcpy( ( void *)data, ( void *)(_meterData + 3 ), _meterBytes );
   else
      memcpy( ( void *)data, ( void *)(_meterData ), _meterBytes );
   
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

   memset( _storage, '\0', Storage_size );
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
   _password = str;
   _password.append( "\r\n" );
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
   reset();
   memcpy( xfer.getOutBuffer(), dataOut, strlen( dataOut ) );

   _bytesReceived = 0;

   memset( _storage, '\0', Storage_size );

   xfer.setMessageStart( true );
   xfer.setOutCount( strlen( dataOut ) );     //there will be a problem with this using RWCStrings
   xfer.setInCountExpected( bytesIn );
   xfer.setInTimeout( time );
   xfer.setNonBlockingReads( block );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::gotValidResponse( const BYTE *data, int length )
{
   char        temp[100];
   UINT        totalLen = 0;
   UINT        offset = 0;
   bool        success = false;

   const char *ptr = ( const char *)data;

   while( offset < length )
   {
      if(( strstr( ptr + offset, _good_return ) != NULL ) ||
         ( strstr( ptr + offset, _prot_message ) != NULL ) ||
         ( strstr( ptr + offset, _enter ) != NULL ) ||
         ( strstr( ptr + offset, _dump ) != NULL ))
      {
         success = true;
         break;
      }
      else
      {
         offset += strlen( ptr + offset ) + 1;
      }
   }

   return( success );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::gotRetry( const BYTE *data, int length )
{
   char        temp[100];
   UINT        totalLen = 0;
   UINT        offset = 0;
   bool        success = false;

   const char *ptr = ( const char *)data;

   while( offset < length )
   {
      if(( strstr( ptr + offset, _retry ) != NULL ) ||
         ( strstr( ptr + offset, _fail ) != NULL ))
      {
         success = true;
         break;
      }
      else
      {
         offset += strlen( ptr + offset ) + 1;
      }
   }

   return( success );
}


















































/*bool CtiTransdataTracker::decodeLink( CtiXfer &xfer, int status )
{
   BYTE  temp[500];
   int   bytes = 0;
   
   _datalink.readMsg( xfer, status );

   if( _datalink.isTransactionComplete() )
   {
      _datalink.retreiveData( temp, &bytes );

      if( bytes != 0 )
      {
         memcpy( _storage + _bytesReceived, temp, bytes );
         _bytesReceived += bytes;

         processComms( _storage, _bytesReceived );
      }
   }
   
   if( _datalink.getError() == failed )
   {
      setError();
      _waiting = false;
   }

   return( false );
}
*/
                                                        
/*bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
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
         if( _dataIsExpected )
         {
            _goodCRC = _ymodem.isCrcValid();

            if( _goodCRC )
            {
               _ymodem.retreiveData( _meterData, &_meterBytes );
               processData( _meterData );
               setNextState();
               _ymodemsTurn = false;
            }
         }
         else
         {
            setNextState();
         }

         if( _moveAlong )
         {
            _moveAlong = false;
            _finished = true;
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

            processComms( _storage, _bytesReceived );
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
*/





