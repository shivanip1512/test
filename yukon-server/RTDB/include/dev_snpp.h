/*-----------------------------------------------------------------------------*
*
* File:   dev_snpp
*
* Class:  CtiDeviceSnppPagingTerminal
* Date:   5/9/2000
*
* Author: Corey G. Plender
*
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_tap.h-arc  $
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2005/06/13 14:04:48 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SNPP_H__
#define __DEV_SNPP_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>
#include <rw\thr\mutex.h>

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

class IM_EX_DEVDB CtiDeviceSnppPagingTerminal  : public CtiDeviceRemote
{
protected:

   CtiTableDeviceTapPaging       _table;

   BYTE                          _outBuffer[505];
   BYTE                          _inBuffer[100];
   BYTE                          _messageBuffer[500];
   ULONG                         _inCountActual;

public:

   typedef CtiDeviceRemote Inherited;

   CtiDeviceSnppPagingTerminal();
   CtiDeviceSnppPagingTerminal(const CtiDeviceSnppPagingTerminal& aRef);
   virtual ~CtiDeviceSnppPagingTerminal();

   CtiDeviceSnppPagingTerminal& operator=(const CtiDeviceSnppPagingTerminal& aRef);

   INT decode(CtiXfer &xfer,INT commReturnValue);
   INT generate(CtiXfer  &xfer);

   int recvCommRequest( OUTMESS *OutMessage );

   virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
   virtual void DecodeDatabaseReader(RWDBReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                          CtiCommandParser               &parse,
                          OUTMESS                        *&OutMessage,
                          RWTPtrSlist< CtiMessage >      &vgList,
                          RWTPtrSlist< CtiMessage >      &retList,
                          RWTPtrSlist< OUTMESS >         &outList);

   bool isTransactionComplete();

   enum CommandState
   {
       Normal=0,
       Complete
   };

   enum StateMachine
   {
       StateHandshakeInitialize = 0,
       StateGenerateReadTextString,
       StateGenerateSetupReadResponse,
       StateSendLoginInformation,
       StateSendLevelNumber,
       StateSendAlertNumber,
       StateSendCoverageNumber,
       StateSendHoldTime,
       StateSendPageWithPass,
       StateSendPageWithoutPass,
       StateSendCallerID,
       StateSendSubject,
       StateSendDataCommand,
       StateSendData,
       StateSendSend,
       StateSendQuit,
       StateEnd,
       StateDecodeReadTextString,
       StateDecodeResponse,
       StateDecodeSetupReadResponse,

   };

   //below enums???
protected:
   CommandState                  _command;

private:
   StateMachine          _previousState;
   StateMachine          _currentState;
   StateMachine getCurrentState();
   StateMachine getPreviousState();
   void resetStates();
   void setCurrentState(StateMachine newCurrentState);
   void setPreviousState(StateMachine newPreviousState);

    static const char *_command_login;
    static const char *_command_page;
    static const char *_command_level;
    static const char *_command_alert;
    static const char *_command_coverage;
    static const char *_command_hold;
    static const char *_command_caller_id;
    static const char *_command_subject;
    static const char *_command_message;
    static const char *_command_reset;
    static const char *_command_data;
    static const char *_command_send;
    static const char *_command_quit;
    static const char *_char_cr_lf;

    char* getLoginName();
    char* getLoginPass();
    char* getLevelNumber();
    char* getAlertNumber();
    char* getCoverageNumber();
    char* getCallerID();
    char* getHoldTime();
    char* getSubject();
    char* getPagePass();
    char* getPageNumber();
};
/*
#define CHAR_CR         0x0D
#define CHAR_LF         0x0A
#define CHAR_ESC        0x1B
#define CHAR_STX        0x02
#define CHAR_ETX        0x03
#define CHAR_US         0x1F
#define CHAR_ETB        0x17
#define CHAR_EOT        0x04
#define CHAR_SUB        0x1A
#define CHAR_ACK        0x06
#define CHAR_NAK        0x15
#define CHAR_RS         0x1e
*/

#endif // #ifndef __DEV_SNPP_H__
