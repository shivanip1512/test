
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
* REVISION     :  $Revision: 1.15 $
* DATE         :  $Date: 2004/01/07 16:47:06 $
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
   _ymodem.reinitalize();
   _datalink.reinitalize();
      
   _lastState        = doPassword;
   _error            = Working;
   _meterBytes       = 0;
   _neededAcks       = 1;  //smarter, later
   _failCount        = 0;
   _dataBytes        = 0;

   _finished         = true;
   _moveAlong        = false;
   _goodCRC          = false;
   _ymodemsTurn      = false;
   _dataIsExpected   = false;
//   _hold             = false;
   _didRecordCheck   = false;
   _didLoadProfile   = false;
   _didBilling       = false;
   _haveData         = false;

   _lp               = new mark_v_lp;

   _storage          = new BYTE[Storage_size];    
   _meterData        = new BYTE[Meter_size];
   _lastCommandSent  = new BYTE[Command_size];
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{    
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

   case doProt1:
   case doProt2:
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

bool CtiTransdataTracker::decodeYModem( CtiXfer &xfer, int status )
{
   int bytes;

   _ymodem.decode( xfer, status );

   if( _ymodem.isTransactionComplete() )
   {
      _goodCRC = _ymodem.isCrcValid();

      if( _goodCRC )
      {
         _ymodem.retreiveData( _meterData, &bytes );
         processData( _meterData, bytes );
      }
   }
   
   if( _ymodem.getAcks() >= _neededAcks )
   {
      _finished = true;
      //
      //
      _ymodem.setStart( true );
      setNextState();
   }

   return( false );
}
   
//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decodeLink( CtiXfer &xfer, int status )
{
   _datalink.readMsg( xfer, status );

   if( _datalink.isTransactionComplete() )
   {
      memset( _storage, '\0', Storage_size );

      _datalink.retreiveData( _storage, &_bytesReceived );
      processComms( _storage, _bytesReceived );
   }
   else
   {
      setError(); //we can get rid of failcounts below
   }
   
   return( false );
}

//=====================================================================================================================
//search for one of our magic conditions that says 'success' of this layer (not ymodem)
//=====================================================================================================================

bool CtiTransdataTracker::processComms( BYTE *data, int bytes )
{
   bool  valid = false;

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

      setNextState();

      _failCount = 0;

      if( _moveAlong )
      {
         _moveAlong = false;
         _finished = true;
      }

      valid = true;
   }
   else
   {
      setError(); //we're getting crap
   }

   return( valid );
}

//=====================================================================================================================
//if we collected loadprofile data, we need to add our raw data to our lp struct, then copy it back into the main
// meter buffer so it gets passed up the chain correctly...
//=====================================================================================================================

bool CtiTransdataTracker::processData( BYTE *data, int bytes )
{
   if( _didBilling )
   {
      if( _didRecordCheck )
      {
         //do loadprofile
         if( bytes != 0 )
         {
            //copy the packet data we just got 
            memcpy( _lp->lpData + _dataBytes, data, bytes );
            _dataBytes += bytes;

            //
            //copy over any previous data
            //
            memcpy( _meterData, _lp, sizeof( *_lp ) );   

            _meterBytes = sizeof( *_lp );
            _haveData = true;
         }
      }
      else
      {
         //do record check
         int returningRecs = atoi( ( const char *)data );

         if( returningRecs < _lp->numLpRecs )
         {
            _lastState = doRecordDump;
         }
         _didRecordCheck = true;
      }
   }
   else
   {
      //do billing stuff
      _didBilling = true;
      _haveData = true;
      _meterBytes += bytes;
   }

   return( true );
}

//=====================================================================================================================
//sequence for the login process
//=====================================================================================================================

bool CtiTransdataTracker::logOn( CtiXfer &xfer )
{
   _finished = false;

   if( _waiting )
   {
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
            setXfer( xfer, _password, strlen( _good_return ), false, 1 );
            _datalink.buildMsg( xfer );
            _first = true;
         }
         break;

      case doEnabledChannels:
         {
            setXfer( xfer, _channels_enabled, 20, true, 1 );
            _datalink.buildMsg( xfer );
         }
         break;

      case doTime:
         {
            setXfer( xfer, _get_clock, 50, true, 1 );
            _datalink.buildMsg( xfer );
         }
         break;

      case doIntervalSize:
         {
            setXfer( xfer, _interval, 24, true, 1 );
            _datalink.buildMsg( xfer );
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

   case doProt1:
      {
         _neededAcks = 1;
         _ymodem.generate( xfer, _neededAcks );
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

   switch( _lastState )
   {
   
   case doRecordDump:
      {
         setXfer( xfer, _dump_demands, 47, true, 1 );
         _datalink.buildMsg( xfer );
      }
      break;
     
   case doRecordNumber:
      {
         //make this crap...
         _lp->numLpRecs = calcLPRecs();
         RWCString recNum( CtiNumStr( _lp->numLpRecs ).zpad( 4 ) );
         recNum.append( "\r\n" );
         // ...into a function, it's ugly

         setXfer( xfer, recNum, 25, true, 1 );  
         _datalink.buildMsg( xfer );
      }
      break;
   
   case doProt2:
      {
         _neededAcks = calcAcks( _lp->numLpRecs );
         _ymodem.generate( xfer, _neededAcks );
      }
      break;
   }

   return( true );
}

//=====================================================================================================================
//we take the number of records that we've decided that we need and figure out how many ymodem-chunks it'll take to
//get that many load-profile records
//=====================================================================================================================

int CtiTransdataTracker::calcAcks( int recs )
{
   int need = 0;

   need = ( recs / Recs_Fitable ) + 2;
   
   return( need );
}

//=====================================================================================================================
//last step, but we'll get no response back
//=====================================================================================================================

bool CtiTransdataTracker::logOff( CtiXfer &xfer )
{
   _finished = false;

   setXfer( xfer, _hang_up, 0, true, 2 );
   
   _datalink.buildMsg( xfer );
   _finished = true;

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

   for( int x = 0; x < 8; x++ )
   {
      _lp->enabledChannels[x] = false;
   }

   for( int index = 0; index < 8; index++ )
   {
      ptr = strstr( ( const char*)ptr, " " );

      if( ptr != NULL )
      {
         ptr++;
         
         if( *ptr == index + '0' )
            _lp->enabledChannels[index] = true;
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
   
   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ meterTime " << t << endl;
   }

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
//=====================================================================================================================
 
void CtiTransdataTracker::setLastLPTime( ULONG lpTime )
{
   _lastLPTime = ( ULONG)lpTime;
}

//=====================================================================================================================
//one record = one interval for one channel
//=====================================================================================================================
 
int CtiTransdataTracker::calcLPRecs( void )
{
   int channels = countChannels();
   int numberLPRecs = (( _lp->meterTime - _lastLPTime ) / ( _lp->lpFormat[0] * 60 )) * channels;

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ numberLPRecs " << numberLPRecs << endl;
   }

   if( numberLPRecs > Max_lp_recs )     
      numberLPRecs = Max_lp_recs;

   return( numberLPRecs );
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataTracker::countChannels( void )
{
   int ch = 0;

   for( int index = 0; index < 8; index++ )
   {
      if( _lp->enabledChannels[index] )
         ch++;
   }

   return( ch );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::isTransactionComplete( void )
{
   if( getError() == Failed )
      _finished = true;

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
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataTracker::retreiveData( BYTE *data )
{
   int temp = _meterBytes;

   memcpy( ( void *)data, ( void *)(_meterData ), _meterBytes );
   
   _meterBytes = 0;
   
   _haveData = false;
   _goodCRC = false;
   _finished = false;
   
   return( temp );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::reset( void )
{
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
   if( ++_failCount > 10 )
   {
      _error = Failed;
      _finished = true;
   }
   else
   {
      _error = Working;
   }
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::haveData( void )
{
   return( _haveData );
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
   xfer.setOutCount( strlen( dataOut ) );    
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
