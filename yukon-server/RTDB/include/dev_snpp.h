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

#include "dev_paging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"
#include "encryption_oneway_message.h"

class IM_EX_DEVDB CtiDeviceSnppPagingTerminal  : public Cti::Devices::DevicePaging,
                                                 protected OneWayMsgEncryption
{
private:

    typedef Cti::Devices::DevicePaging Inherited;

protected:

   std::queue< CtiVerificationBase * >  _verification_objects;

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

   virtual INT ExecuteRequest(CtiRequestMsg               *pReq,
                          CtiCommandParser               &parse,
                          OUTMESS                        *&OutMessage,
                          std::list< CtiMessage* >      &vgList,
                          std::list< CtiMessage* >      &retList,
                          std::list< OUTMESS* >         &outList);
   virtual int sendCommResult(INMESS *InMessage);

   bool isTransactionComplete();

   void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

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

    std::string getLoginName();
    std::string getLoginPass();
    std::string getLevelNumber();
    std::string getAlertNumber();
    std::string getCoverageNumber();
    std::string getCallerID();
    std::string getHoldTime();
    std::string getSubject();
    std::string getPagePass();
    std::string getPageNumber();
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
