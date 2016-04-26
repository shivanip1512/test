#pragma once

#include "dev_paging.h"
#include "dev_ied.h"
#include "dlldefs.h"
#include "xfer.h"
#include "encryption_oneway_message.h"

class IM_EX_DEVDB CtiDeviceSnppPagingTerminal  : public Cti::Devices::DevicePaging,
                                                 protected OneWayMsgEncryption
{
    typedef Cti::Devices::DevicePaging Inherited;

protected:

   std::queue< CtiVerificationBase * >  _verification_objects;

   BYTE                          _outBuffer[505];
   BYTE                          _inBuffer[100];
   ULONG                         _inCountActual;

public:

   CtiDeviceSnppPagingTerminal();

   YukonError_t decode  (CtiXfer &xfer, YukonError_t commReturnValue);
   YukonError_t generate(CtiXfer  &xfer);

   YukonError_t recvCommRequest( OUTMESS *OutMessage ) override;

   YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

   YukonError_t sendCommResult(INMESS &InMessage) override;

   bool isTransactionComplete();

   void getVerificationObjects(std::queue< CtiVerificationBase * > &work_queue);

   std::string getTransactionReport() override;

   void updateTransactionReport(std::string reportUpdate);

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
    //static const char *_command_message;
    //static const char *_command_reset;
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

    std::string TransactionReport;
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
