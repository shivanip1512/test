
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_TRACKER_H__
#define __TRANSDATA_TRACKER_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_tracker
*
* Class:
* Date:   8/14/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.19 $
* DATE         :  $Date: 2004/02/09 16:48:42 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <rw/db/datetime.h>

#include "xfer.h"
#include "numstr.h"
#include "transdata_datalink.h"
#include "prot_ymodem.h"

class IM_EX_PROT CtiTransdataTracker
{
   public:
      
      struct lpRecord
      {
         BYTE     rec[2];
      };

      struct mark_v_lp
      {
         bool     enabledChannels[8];
         int      lpFormat[3];
         int      numLpRecs;
         ULONG    meterTime;
         lpRecord lpData[9999];
      };


      CtiTransdataTracker();
      ~CtiTransdataTracker();

      void setXfer( CtiXfer &xfer, RWCString dataOut, int bytesIn, bool block, ULONG time );
      bool logOn( CtiXfer &xfer );
      bool billing( CtiXfer &xfer );
      bool loadProfile( CtiXfer &xfer );
      bool logOff( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool decodeYModem( CtiXfer &xfer, int status );
      bool decodeLink( CtiXfer &xfer, int status );
      bool processComms( BYTE *data, int bytes );
      bool processData( BYTE *data, int bytes );
      bool isTransactionComplete( void );
      bool gotValidResponse( const BYTE *data, int length );
      bool gotRetry( const BYTE *data, int length );
      bool grabChannels( BYTE *data, int bytes );
      bool grabFormat( BYTE *data, int bytes );
      bool grabTime( BYTE *data, int bytes );
      bool grabReturnedChannels( BYTE *data, int bytes );
      void injectData( RWCString str );
      void setNextState( void );
      void reset( void );
      void setLastLPTime( ULONG lpTime );
      int retreiveData( BYTE *data );
      int getError( void );
      void setError( void );
      bool haveData( void );
      void reinitalize( void );
      void destroy( void );
      int calcLPRecs( void );
      int countChannels( void );
      int calcAcks( int recs );
      RWCString formatRecNums( int recs );
      RWTime timeAdjust( RWTime meter );

   protected:

   private:

      enum
      {
         //connect
         doType         = 0,
         doPassword,
         doEnabledChannels,
         doTime,
         doIntervalSize,

         //billing
         doScroll,
         doPullBuffer,
         doProt1,

         //loadprofile
         doRecordDump,
         doRecordNumber,
         doProt2,

         //disconnect
         doLogoff
      };

      enum Errors
      {
         Working     = 0,
         Failed
      };

      enum Sizes
      {
         Command_size   = 30,
         Recs_Fitable   = 512,
         Storage_size   = 4500,
         Meter_size     = 50000,
         Max_lp_recs    = 5000//9999
      };

      //these are the transdata commands that are defined by the doc 22A204E
      const char *const    _identify;
      const char *const    _revision;
      const char *const    _demand_reset;
      const char *const    _search_scrolls;
      const char *const    _search_alts;
      const char *const    _search_comms;
      const char *const    _dump_demands;
      const char *const    _model_number;
      const char *const    _hold_display;
      const char *const    _set_display;
      const char *const    _change_display;
      const char *const    _scroll_display;
      const char *const    _get_clock;
      const char *const    _set_clock;
      const char *const    _alt_display;
      const char *const    _test_ram;
      const char *const    _diagnose;
      const char *const    _reset_fail;
      const char *const    _send_comm_buff;
      const char *const    _custom_regs;
      const char *const    _hang_up;
      const char *const    _interval;
      const char *const    _diag_status;
      const char *const    _channels_enabled;
      const char *const    _set_manual_mode;
      const char *const    _set_auto_mode;
      const char *const    _switch_in;
      const char *const    _switch_out;
      const char *const    _control_status;
      const char *const    _actuate;
      const char *const    _control_status_2;
      const char *const    _clear_eventlog;
      const char *const    _dump_eventlog_rec;
      const char *const    _dial_in_status;
      const char *const    _set_dial_in_time;
      const char *const    _get_dial_in_time;
      const char *const    _get_program_id;
      const char *const    _test;
      const char *const    _good_return;
      const char *const    _prot_message;
      const char *const    _retry;
      const char *const    _dump;
      const char *const    _fail;
      const char *const    _enter;
      const char *const    _ems;

      RWCString            _password;
      RWCString            _tempSent;

      bool                 _waiting;
      bool                 _moveAlong;
      bool                 _finished;
      bool                 _goodCRC;
      bool                 _ymodemsTurn;
      bool                 _first;
      bool                 _sec;
      bool                 _dataIsExpected;
      bool                 _didRecordCheck;
      bool                 _didLoadProfile;
      bool                 _didBilling;
      bool                 _haveData;

      int                  _lastState;
      int                  _bytesReceived;
      int                  _meterBytes;
      int                  _failCount;
      int                  _error;
      int                  _neededAcks;
      int                  _dataBytes;

      BYTE                 *_storage;
      BYTE                 *_meterData;
      BYTE                 *_lastCommandSent;

      ULONG                _lastLPTime;
      
      mark_v_lp            *_lp;

      CtiTransdataDatalink _datalink;
      CtiProtocolYmodem    _ymodem;

};

#endif // #ifndef __TRANSDATA_TRACKER_H__
