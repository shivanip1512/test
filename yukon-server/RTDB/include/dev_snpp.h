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
* REVISION     :  $Revision: 1.8 $
* DATE         :  $Date: 2008/10/28 19:21:44 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_SNPP_H__
#define __DEV_SNPP_H__
#pragma warning( disable : 4786)


#include <rw\thr\mutex.h>

#include "tbl_dv_tappaging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"

class IM_EX_DEVDB CtiDeviceSnppPagingTerminal  : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

protected:

   queue< CtiVerificationBase * >  _verification_objects;
   CtiTableDeviceTapPaging       _table;

   BYTE                          _outBuffer[505];
   BYTE                          _inBuffer[100];
   ULONG                         _inCountActual;

public:

   CtiDeviceSnppPagingTerminal();
   CtiDeviceSnppPagingTerminal(const CtiDeviceSnppPagingTerminal& aRef);
   virtual ~CtiDeviceSnppPagingTerminal();

   CtiDeviceSnppPagingTerminal& operator=(const CtiDeviceSnppPagingTerminal& aRef);

   INT decode(CtiXfer &xfer,INT commReturnValue);
   INT generate(CtiXfer  &xfer);

   int recvCommRequest( OUTMESS *OutMessage );

   virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                          CtiCommandParser               &parse,
                          OUTMESS                        *&OutMessage,
                          list< CtiMessage* >      &vgList,
                          list< CtiMessage* >      &retList,
                          list< OUTMESS* >         &outList);

   bool isTransactionComplete();

   void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);

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

   OUTMESS _outMessage;

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

    string getLoginName();
    string getLoginPass();
    string getLevelNumber();
    string getAlertNumber();
    string getCoverageNumber();
    string getCallerID();
    string getHoldTime();
    string getSubject();
    string getPagePass();
    string getPageNumber();
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
