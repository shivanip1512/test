
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
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2004/01/28 16:50:13 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "transdata_tracker.h"
#include "guard.h"
#include "logger.h"

//=====================================================================================================================
//=====================================================================================================================

CtiTransdataTracker::CtiTransdataTracker():
   _identify( "ID\r" ),
   _revision( "II\r" ),
   _demand_reset( "DR\r" ),
   _search_scrolls( "SC\r" ),
   _search_alts( "HC\r" ),
   _search_comms( "CC\r" ),
   _dump_demands( "RC\r" ),
   _model_number( "MI\r" ),
   _hold_display( "HD\r" ),
   _set_display( "SD\r" ),
   _change_display( "AD\r" ),
   _scroll_display( "RS\r" ),
   _get_clock( "GT\r" ),
   _set_clock( "TI\r" ),
   _alt_display( "ST\r" ),
   _test_ram( "RM\r" ),
   _diagnose( "DI\r" ),
   _reset_fail( "RF\r" ),
   _send_comm_buff( "BU\r" ),
   _custom_regs( "SS\r" ),
   _hang_up( "LO\r" ),
   _interval( "IS\r" ),
   _diag_status( "SA\r" ),
   _channels_enabled( "DC\r" ),
   _set_manual_mode( "CM\r" ),
   _set_auto_mode( "CA\r" ),
   _switch_in( "CI\r" ),
   _switch_out( "CO\r" ),
   _control_status( "CS\r" ),
   _actuate( "SO\r" ),
   _control_status_2( "OS\r" ),
   _clear_eventlog( "RI\r" ),
   _dump_eventlog_rec( "RV\r" ),
   _dial_in_status( "DS\r" ),
   _set_dial_in_time( "TC\r" ),
   _get_dial_in_time( "GC\r" ),
   _get_program_id( "PI\r" ),
   _test( "\r\r\r" ),
   _good_return( "Ok\r\n?" ),
   _prot_message( "protocol" ),
   _retry( "Retry" ),
   _dump( "XXXX\r" ),
   _fail( "failed\r\n" ),
   _enter( "Enter" ),
   _ems( "EMS" ),
   _lp( NULL ),
   _storage( NULL ),
   _meterData( NULL ),
   _lastCommandSent( NULL )
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

   if( _storage != NULL )
   {
      delete [] _storage;
      _storage = NULL;
   }

   if( _meterData != NULL )
   {
      delete [] _meterData;
      _meterData = NULL;
   }

   if( _lastCommandSent != NULL )
   {
      delete [] _lastCommandSent;
      _lastCommandSent = NULL;
   }

   if( _lp != NULL )
   {
      delete _lp;
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
   _didRecordCheck   = false;
   _didLoadProfile   = false;
   _didBilling       = false;
   _haveData         = false;

   if( _lp != NULL )
   {
      delete _lp;
   }

   if( _storage != NULL )
   {
      delete [] _storage;
   }

   if( _meterData != NULL )
   {
      delete [] _meterData;
   }

   if( _lastCommandSent != NULL )
   {
      delete [] _lastCommandSent;
   }

   _lp               = CTIDBG_new mark_v_lp;
   _storage          = CTIDBG_new BYTE[Storage_size];    
   _meterData        = CTIDBG_new BYTE[Meter_size];
   _lastCommandSent  = CTIDBG_new BYTE[Command_size];
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decode( CtiXfer &xfer, int status )
{    
   switch( _lastState )
   {
   case doPassword:
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
   bool  allGood = true;

   if( gotRetry( data, bytes ) )
   {
      reset();
   }
   else if( gotValidResponse( data, bytes ) )
   {
      if( _lastState == doEnabledChannels )
         allGood = grabChannels( data, bytes );

      if( _lastState == doIntervalSize )
      {
         allGood = grabFormat( data, bytes );
         RWTime temp = timeAdjust( _lp->meterTime );  //we do this here because we have to have the format first!
         _lp->meterTime = temp.seconds();
      }

      if( _lastState == doTime )
         allGood = grabTime( data, bytes );

      if( allGood )
      {
         setNextState();

         _failCount = 0;

         if( _moveAlong )
         {
            _moveAlong = false;
            _finished = true;
         }

         valid = true;
      }
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
         _lp->numLpRecs = calcLPRecs();
         setXfer( xfer, formatRecNums( _lp->numLpRecs ), 25, true, 1 );
         
         if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
         {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ recs " << formatRecNums( _lp->numLpRecs ) << endl;
         }
         
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
//=====================================================================================================================

RWCString CtiTransdataTracker::formatRecNums( int recs )
{
   RWCString temp( CtiNumStr( recs ).zpad( 4 ) );

   temp.append( "\r\n" );

   return( temp );
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
   char  fluff[400];
   bool  foundCorrectCommand = false;

   memcpy( fluff, data, bytes );
   char *ptr = fluff;

   while( !foundCorrectCommand )
   {
      char *temp = strstr( ( const char*)ptr, "DC" );

      if( temp != NULL )
      {
         ptr = temp + 2;
         foundCorrectCommand = true;
      }
   }

   if( foundCorrectCommand )
   {
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
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================
 
bool CtiTransdataTracker::grabFormat( BYTE *data, int bytes )
{
   char  fluff[400];
   bool  foundCorrectCommand = false;   

   memcpy( fluff, data, bytes );
   char *ptr = fluff;
   
   for( ;; )
   {
      char *temp = strstr( ( const char*)ptr, "IS" );

      if( temp != NULL )
      {
         ptr = temp + 2;
         foundCorrectCommand = true;
      }
      else
      {
         break;
      }
   }

   if( foundCorrectCommand )
   {
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
   }
   
   return( true );
}

//=====================================================================================================================
//there may be a better way to get the time of the last complete load profile interval, but I don't know what it is 
//right now, so we'll just ask the meter what time it is and send that up with the other LP data
//=====================================================================================================================

bool CtiTransdataTracker::grabTime( BYTE *data, int bytes )
{
   char           fluff[400];
   unsigned       timeBits[6];
   bool           foundCorrectCommand = false;

   memcpy( fluff, data, bytes );
   char *ptr = fluff;

   for( int i = 0; i < 6; i++ )
   {
      timeBits[i] = 0;
   }

   //shoud trim off any excess crap in the front....
   //make this a general thing and run everybody through it...!
   for( ;; )
   {
      char *temp = strstr( ( const char*)ptr, "GT" );

      if( temp != NULL )
      {
         ptr = temp + 2;
         foundCorrectCommand = true;
      }
      else
      {
         break;
      }
   }
   
   if( foundCorrectCommand )
   {
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
         dout << RWTime() << " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ meter time " << t << endl;
      }

      _lp->meterTime = t.seconds();
   }

   return( true );
}
//=====================================================================================================================
//the point of this nonsense is to correct for the fact that the meter doesn't include a timestamp with loadprofile
// data.  This means that we have to figure out how far off (forward) we are from the last interval we've passed and 
// correct it...
//=====================================================================================================================

RWTime CtiTransdataTracker::timeAdjust( RWTime meterTime )
{
   int interval = _lp->lpFormat[0] * 60;
   int pullOff = _lp->meterTime % interval;

   RWTime corrected( _lp->meterTime - pullOff );

   return( corrected );
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

   if( getDebugLevel() & DEBUGLEVEL_LUDICROUS )
   {
      CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << RWTime() << " ~~~~~~~~~~~~~~~~~~~~~~~~~~~~ tracker thinks lastlptime is " << RWTime( lpTime ) << endl;
   }
}

//=====================================================================================================================
//one record = one interval for one channel
//=====================================================================================================================
 
int CtiTransdataTracker::calcLPRecs( void )
{
   int channels = countChannels();
   int numberLPRecs = 0;
   
   if( _lastLPTime < _lp->meterTime )
   {
      numberLPRecs = (( _lp->meterTime - _lastLPTime ) / ( _lp->lpFormat[0] * 60 )) * channels;
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
