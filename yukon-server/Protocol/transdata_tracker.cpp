#include "precompiled.h"
#include "transdata_tracker.h"
#include "guard.h"
#include "logger.h"

using std::string;
using std::endl;

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

   _lastState   = doType;
   _error       = Working;
   _meterBytes  = 0;
   _failCount   = 0;
   _dataBytes   = 0;
   _packetsExpected = 1;  //smarter, later

   _finished         = true;
   _moveAlong        = false;
   _goodCRC          = false;
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
   case doType:
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

   case doReadPrompt:
   case doLogoff:
      {
         decodeLogoff( xfer, status );
      }
      break;

   default:
      break;
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decodeYModem( CtiXfer &xfer, int status )
{
   int   bytes = 0;
   BYTE  data[2000]; //02.12.04

   _ymodem.decode( xfer, status );

   if( _ymodem.isTransactionComplete() )
   {
      _goodCRC = _ymodem.isCrcValid();

      if( _goodCRC )
      {
         _ymodem.retrieveData( data, &bytes );

         if( bytes < 2000 )
         {
            processData( data, bytes );
         }
         else
         {
            if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
            {
                CTILOG_DEBUG(dout, "bytes >= 2000");
            }
         }
      }

      if( _ymodem.packetsReceived() >= _packetsExpected )
      {
         _finished = true;
         _ymodem.setStart();
         setNextState();
      }
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
      memset( _storage, 0, Storage_size );

      _datalink.retrieveData( _storage, &_bytesReceived );
      processComms( _storage, _bytesReceived );
   }
   else
   {
      setError(); //we can get rid of failcounts below
   }

   return( false );
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::decodeLogoff( CtiXfer &xfer, int status )
{
   _datalink.readMsg( xfer, status );

   if( _datalink.isTransactionComplete() )
   {
      if( _lastState == doLogoff )
      {
          _finished = true;
      }

      setNextState();
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

   if( gotValidResponse( data, bytes ) )
   {
      switch( _lastState )
      {
      case doEnabledChannels:
         {
            allGood = grabChannels( data, bytes );
         }
         break;

      case doIntervalSize:
         {
            allGood = grabFormat( data, bytes );
            CtiTime temp = timeAdjust( _lp->meterTime );  //we do this here because we have to have the format first!
            _lp->meterTime = temp.seconds();
         }
         break;

      case doTime:
         {
            allGood = grabTime( data, bytes );
         }
         break;
      }

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
            if( bytes < 0 )
            {
               if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
               {
                   CTILOG_DEBUG(dout, "Our world is ending!");
               }
            }

            //copy the packet data we just got
            memcpy( ((BYTE*)(_lp->lpData)) + _dataBytes, data, bytes );

            if( !_CrtCheckMemory( ) )
            {
               if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
               {
                   CTILOG_DEBUG(dout, "!_CrtCheckMemory()");
               }
            }

            _dataBytes += bytes;

            //copy over any previous data
            memset( _meterData, 0, Meter_size );      //02.10.04
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
      memcpy( _meterData, data, bytes );
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
      setXfer( xfer, _datalink.buildMsg(), 1, true, 1 );
   }
   else
   {
      _waiting = true;

      switch( _lastState )
      {
      case doType:    //this is just to get us going
         {
            setXfer( xfer, _datalink.buildMsg( _revision, _ems ), 20, true, 1 );
         }
         break;

      case doPassword:
         {
            setXfer( xfer, _datalink.buildMsg( _password, _good_return ), 8, true, 1 );
         }
         break;

      case doEnabledChannels:
         {
            setXfer( xfer, _datalink.buildMsg( _channels_enabled, _good_return ), 25, true, 1 );
         }
         break;

      case doTime:
         {
            setXfer( xfer, _datalink.buildMsg( _get_clock, _good_return ), 60, true, 1 );
         }
         break;

      case doIntervalSize:
         {
            setXfer( xfer, _datalink.buildMsg( _interval, _good_return ), 50, true, 1 );
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
         setXfer( xfer, _datalink.buildMsg( _search_comms, _good_return ), 10, true, 1 );
      }
      break;

   case doPullBuffer:
      {
         setXfer( xfer, _datalink.buildMsg( _send_comm_buff, _prot_message ), 50, true, 1 );
      }
      break;

   case doProt1:
      {
         _packetsExpected = 1;
         _ymodem.generate( xfer, _packetsExpected );
      }
      break;
   }

   return( true );
}

//=====================================================================================================================
//sequence for the scan for load profile
//=====================================================================================================================

bool CtiTransdataTracker::loadProfile( CtiXfer &xfer )
{
   _finished = false;

   switch( _lastState )
   {
   case doRecordDump:
      {
         setXfer( xfer, _datalink.buildMsg( _dump_demands, _dump ), 45, true, 1 );
      }
      break;

   case doRecordNumber:
      {
         _lp->numLpRecs = calcLPRecs();
         setXfer( xfer, _datalink.buildMsg( formatRecNums( _lp->numLpRecs ), _prot_message ), 50, true, 1 );

         if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
         {
            CTILOG_DEBUG(dout, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~ recs "<< formatRecNums( _lp->numLpRecs ));
         }
      }
      break;

   case doProt2:
      {
         _packetsExpected = calcPackets( _lp->numLpRecs );
         _ymodem.generate( xfer, _packetsExpected );
      }
      break;
   }

   return( true );
}

//=====================================================================================================================
//last step
//=====================================================================================================================

bool CtiTransdataTracker::logOff( CtiXfer &xfer )
{
   switch( _lastState )
   {
      default:
      {
         _lastState = doReadPrompt;
         //  fall through
      }
      case doReadPrompt:
      {
         setXfer( xfer, _datalink.buildMsg( "", _good_return ), 7, true, 1 );
         break;
      }
      case doLogoff:
      {
         setXfer( xfer, _datalink.buildMsg( _hang_up, _hang_up ), 4, true, 0 );
         break;
      }
   }

   return( true );
}

//=====================================================================================================================
//=====================================================================================================================

string CtiTransdataTracker::formatRecNums( int recs )
{
   string temp( CtiNumStr( recs ).zpad( 4 ) );

   temp.append( "\r" );

   return( temp );
}

//=====================================================================================================================
//we take the number of records that we've decided that we need and figure out how many ymodem-chunks it'll take to
//get that many load-profile records
//=====================================================================================================================

int CtiTransdataTracker::calcPackets( int recs )
{
   int packets = 0;

   //  figure out how many YMODEM packets it'll take to get back all of the LP data
   packets = ((recs * Record_size) + CtiProtocolYmodem::Packet_data_length - 1) / CtiProtocolYmodem::Packet_data_length;
   //  add on a packet for the header, which contains the record count
   packets++;

   return packets;
}

//=====================================================================================================================
//=====================================================================================================================

bool CtiTransdataTracker::grabChannels( BYTE *data, int bytes )
{
   char  fluff[400];
   bool  foundCorrectCommand = false;

   if( bytes < 400 )
   {
      memcpy( fluff, data, bytes );
      const char *ptr = fluff;

      while( !foundCorrectCommand )
      {
         const char *temp = strstr( ptr, "DC" );

         if( temp != NULL )
         {
            ptr = temp + 2;
            foundCorrectCommand = true;
         }
      }

      if( foundCorrectCommand )
      {
            //
            // clear our enabled channels
            //
         for( int x = 0; x < 8; x++ )
         {
            _lp->enabledChannels[x] = false;
         }

            //
            // parse the string for enabled channels
            //
         for( int index = 0; index < 8; index++ )
         {
            ptr = strstr( ptr, " " );

            if( ptr != NULL )
            {
               ptr++;
               _lp->enabledChannels[atoi( ptr )] = true;
            }
            else
            {
               break;
            }
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

   if( bytes < 400 )
   {
      memcpy( fluff, data, bytes );
      const char *ptr = fluff;

      for( ;; )
      {
         const char *temp = strstr( ptr, "IS" );

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
            ptr = strstr( ptr, "\n" );

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

   if( bytes < 400 )
   {
      memcpy( fluff, data, bytes );
      const char *ptr = fluff;

      for( int i = 0; i < 6; i++ )
      {
         timeBits[i] = 0;
      }

      //should trim off any excess crap in the front....
      //make this a general thing and run everybody through it...!
      for( ;; )
      {
         const char *temp = strstr( ptr, "GT" );

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
            ptr = strstr( ptr, "\n" );

            if( ptr != NULL )
            {
               ptr++;
               timeBits[index] = atoi( ptr );
               ptr += 2;
            }
         }

         CtiTime t( CtiDate( timeBits[3], timeBits[4], timeBits[5] + 2000 ), timeBits[2], timeBits[1], timeBits[0] );

         if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
         {
            CTILOG_DEBUG(dout, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~ meter time "<< t);
         }

         _lp->meterTime = t.seconds();
      }
   }

   return( true );
}
//=====================================================================================================================
//the point of this nonsense is to correct for the fact that the meter doesn't include a timestamp with loadprofile
// data.  This means that we have to figure out how far off (forward) we are from the last interval we've passed and
// correct it...
//=====================================================================================================================

CtiTime CtiTransdataTracker::timeAdjust( CtiTime meterTime )
{
   int interval = _lp->lpFormat[0] * 60;
   int pullOff = _lp->meterTime % interval;

   CtiTime corrected( _lp->meterTime - pullOff );

   return( corrected );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiTransdataTracker::setLastLPTime( ULONG lpTime )
{
   _lastLPTime = ( ULONG)lpTime;

   if( getDebugLevel() & DEBUGLEVEL_ACTIVITY_INFO )
   {
      CTILOG_DEBUG(dout, "~~~~~~~~~~~~~~~~~~~~~~~~~~~~ tracker thinks lastlptime is "<< CtiTime( lpTime ));
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
      _lastState = doType;
   else
      _lastState++;
}

//=====================================================================================================================
//=====================================================================================================================

int CtiTransdataTracker::retrieveData( BYTE *data )
{
   int temp = _meterBytes;

   if( data != NULL )
   {
      memcpy( ( void *)data, ( void *)(_meterData ), _meterBytes );
   }

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
   _bytesReceived = 0;

   if( _storage != NULL )
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

void CtiTransdataTracker::injectData( string str )
{
   _password = str;
   _password.append( "\r" );
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

void CtiTransdataTracker::setXfer( CtiXfer &xfer, string dataOut, int bytesIn, bool block, ULONG time )
{
   reset();

   memcpy( xfer.getOutBuffer(), dataOut.c_str(), strlen( dataOut.c_str() ) );

   _bytesReceived = 0;

   memset( _storage, '\0', Storage_size );

   xfer.setMessageStart( true );
   xfer.setOutCount( strlen( dataOut.c_str() ) );
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
         ( strstr( ptr + offset, _dump ) != NULL ) ||
         ( strstr( ptr + offset, _hang_up ) != NULL ) || //02.12.04
         ( strstr( ptr + offset, _ems ) != NULL ))
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
