#pragma once

#include "xfer.h"
#include "numstr.h"
#include "transdata_datalink.h"
#include "prot_ymodem.h"
#include "dllbase.h"
#include "ctidate.h"
#include "ctitime.h"

class IM_EX_PROT CtiTransdataTracker
{
   public:

      #pragma pack( push, 1 )

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
         lpRecord lpData[10240];
      };

      #pragma pack( pop )

      CtiTransdataTracker();
      ~CtiTransdataTracker();

      void setXfer( CtiXfer &xfer, std::string dataOut, int bytesIn, bool block, ULONG time );
      bool logOn( CtiXfer &xfer );
      bool billing( CtiXfer &xfer );
      bool loadProfile( CtiXfer &xfer );
      bool logOff( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool decodeYModem( CtiXfer &xfer, int status );
      bool decodeLink( CtiXfer &xfer, int status );
      bool decodeLogoff( CtiXfer &xfer, int status );
      bool processComms( BYTE *data, int bytes );
      bool processData( BYTE *data, int bytes );
      bool isTransactionComplete( void );
      bool gotValidResponse( const BYTE *data, int length );
      bool gotRetry( const BYTE *data, int length );
      bool grabChannels( BYTE *data, int bytes );
      bool grabFormat( BYTE *data, int bytes );
      bool grabTime( BYTE *data, int bytes );
      void injectData( std::string str );
      void setNextState( void );
      void reset( void );
      void setLastLPTime( ULONG lpTime );
      int retrieveData( BYTE *data );
      int getError( void );
      void setError( void );
      bool haveData( void );
      void reinitalize( void );
      void destroy( void );
      int calcLPRecs( void );
      int countChannels( void );
      int calcPackets( int recs );
      std::string formatRecNums( int recs );
      CtiTime timeAdjust( CtiTime meter );

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
         doReadPrompt,
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
         Storage_size   = 4500,
         Meter_size     = 50000,
         Record_size    = 2,
         Max_lp_recs    = 9999
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

      std::string            _password;
      std::string            _tempSent;

      bool                 _waiting;
      bool                 _moveAlong;
      bool                 _finished;
      bool                 _goodCRC;
      bool                 _didRecordCheck;
      bool                 _didLoadProfile;
      bool                 _didBilling;
      bool                 _haveData;

      int                  _lastState;
      int                  _bytesReceived;
      int                  _meterBytes;
      int                  _failCount;
      int                  _error;
      int                  _packetsExpected;
      int                  _dataBytes;

      BYTE                 *_storage;
      BYTE                 *_meterData;
      BYTE                 *_lastCommandSent;

      ULONG                _lastLPTime;

      mark_v_lp            *_lp;

      CtiTransdataDatalink _datalink;
      CtiProtocolYmodem    _ymodem;

};
