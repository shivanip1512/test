
#pragma warning( disable : 4786)
#ifndef __TRANSDATA_DATALINK_H__
#define __TRANSDATA_DATALINK_H__

/*---------------------------------------------------------------------------------*
*
* File:   transdata_datalink
*
* Class:
* Date:   7/22/2003
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2003/08/06 19:51:37 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include "prot_ymodem.h"
#include "xfer.h"

//these are the transdata commands that are defined by the doc 22A204E

#define  IDENTIFY          "ID\r\n"
#define  REVISIOM          "II\r\n"
#define  DEMAND_RESET      "DR\r\n"
#define  SEARCH_SCROLLS    "SC\r\n"
#define  SEARCH_ALTS       "HC\r\n"
#define  SEARCH_COMMS      "CC\r\n"
#define  DUMP_DEMANDS      "RC\r\n"
#define  MODEL_NUMBER      "MI\r\n"
#define  HOLD_DISPLAY      "HD\r\n"
#define  SET_DISPLAY       "SD\r\n"
#define  CHANGE_DISPLAY    "AD\r\n"
#define  SCROLL_DISPLAY    "RS\r\n"
#define  GET_CLOCK         "GT\r\n"
#define  SET_CLOCK         "TI\r\n"
#define  ALT_DISPLAY       "ST\r\n"
#define  TEST_RAM          "RM\r\n"
#define  DIAGNOSE          "DI\r\n"
#define  RESET_FAIL        "RF\r\n"
#define  SEND_COMM_BUFF    "BU\r\n"
#define  CUSTOM_REGS       "SS\r\n"
#define  HANG_UP           "LO\r\n"
#define  INTERVAL          "IS\r\n"
#define  DIAG_STATUS       "SA\r\n"
#define  CHANNELS_ENABLED  "DC\r\n"
#define  SET_MANUAL_MODE   "CM\r\n"
#define  SET_AUTO_MODE     "CA\r\n"
#define  SWITCH_IN         "CI\r\n"
#define  SWITCH_OUT        "CO\r\n"
#define  CONTROL_STATUS    "CS\r\n"
#define  ACTUATE           "SO\r\n"
#define  CONTROL_STATUS_2  "OS\r\n"
#define  CLEAR_EVENTLOG    "RI\r\n"
#define  DUMP_EVENTLOG_REC "RV\r\n"
#define  DIAL_IN_STATUS    "DS\r\n"
#define  SET_DIAL_IN_TIME  "TC\r\n"
#define  GET_DIAL_IN_TIME  "GC\r\n"
#define  GET_PROGRAM_ID    "PI\r\n"

#define  TEST              "\r\n"

#define  START             "C\r\n"//0x43
#define  QUESTION_MARK     0x3f
#define  GOOD_RETURN       "Ok\r\n?"

class IM_EX_PROT CtiTransdataDatalink
{
   enum
   {
      doPassword  = 0,
      doIdentify,
      doScroll,
      doPullBuffer,
      doStartProt,
      doLogoff

   };

   public:

      CtiTransdataDatalink();
      ~CtiTransdataDatalink();

      bool buildMsg( CtiXfer &xfer, RWCString data, int bytesIn, int bytesOut );
      bool logOn( CtiXfer &xfer );
      bool general( CtiXfer &xfer );
      bool logOff( CtiXfer &xfer );
      bool decode( CtiXfer &xfer, int status );
      bool isTransactionComplete( void );

      void injectData( RWCString str );
      void setNextState( void );
      void reset( void );

      BYTE* retreiveData( void );

      CtiProtocolYmodem & getYmodemLayer( void );

   protected:

   private:

      RWCString            _password;
      bool                 _didSomeWork;
      bool                 _waiting;
      bool                 _moveAlong;
      int                  _lastState;
      int                  _bytesReceived;
      int                  _failCount;
      BYTE                 *_storage;
      BYTE                 *_lastCommandSent;
      CtiProtocolYmodem    _ymodem;

};

#endif // #ifndef __TRANSDATA_DATALINK_H__
