
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2003/08/28 21:25:19 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "xfer.h"
#include "transdata_datalink.h"
#include "prot_ymodem.h"

class IM_EX_PROT CtiTransdataTracker
{
   enum
   {
      doTest      = 0,
      doPassword,
      doIdentify,
      doScroll,
      doPullBuffer,
      doStartProt,
      doLogoff

   };

   enum
   {
      working     = 0,
      failed
   };

   public:

      CtiTransdataTracker();
      ~CtiTransdataTracker();

      void setXfer( CtiXfer &xfer, RWCString dataOut, int bytesIn, bool block, ULONG time );

      bool logOn( CtiXfer &xfer );
      bool general( CtiXfer &xfer );
      bool logOff( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool processData( BYTE *data );
      bool isTransactionComplete( void );
      bool isCrcValid( void );

      void injectData( RWCString str );
      void setNextState( void );
      void reset( void );

      void retreiveData( BYTE *data );
      int getError( void );
      void setError( void );

   protected:

   private:

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
      const char *const    _prot_start;
      const char *const    _good_return;
      const char *const    _prot_message;
      const char *const    _retry;

      RWCString            _password;

      bool                 _didSomeWork;
      bool                 _waiting;
      bool                 _moveAlong;
      bool                 _ignore;
      bool                 _finished;
      bool                 _weHaveData;

      int                  _lastState;
      int                  _bytesReceived;
      int                  _failCount;
      int                  _error;

      BYTE                 *_storage;
      BYTE                 *_lastCommandSent;

      CtiTransdataDatalink _datalink;
      CtiProtocolYmodem    _ymodem;

};

#endif // #ifndef __TRANSDATA_TRACKER_H__
