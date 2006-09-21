/*-----------------------------------------------------------------------------*
*
* File:   dev_pagerreceive
*
* Date:   5/22/2002
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_pagerreceive.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2006/09/21 21:31:39 $
*
* Copyright (c) 2005 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_PAGERRECEIVE_H__
#define __DEV_PAGERRECEIVE_H__
#pragma warning( disable : 4786)

#include <map>
#include <string>
#include <vector>

#include "dev_single.h"
#include "dlldefs.h"
#include "xfer.h"
#include "tbl_dv_pagingreceiver.h"

#include "boost_time.h"

#define DEV_PAGERRECEIVE_IN_BUFFER_SIZE 500
class CtiDevicePagingReceiver : public CtiDeviceRemote
{
private:

    typedef CtiDeviceRemote Inherited;

    CtiTableDevicePagingReceiver                        _tbl;
    queue< CtiVerificationBase * >                      _verification_objects;
    static const std::vector<const char*>                    _commandVector;
    std::vector<const char*>::const_iterator                 _cmdVectorIterator;

    bool                _hadHeader;//set to true if last loop we had a header and no footer.
    int                 _retryCount;
    ptime               _retryTime;

protected:

    BYTE                          _outBuffer[100];
    BYTE                          _inBuffer[DEV_PAGERRECEIVE_IN_BUFFER_SIZE];
    string                        _messageString;//needs to be initialized
    ULONG                         _inCountActual;
    int                           _capcodeCount;
public:

    static vector<const char*> initCommandVector();
    CtiDevicePagingReceiver();
    CtiDevicePagingReceiver(const CtiDevicePagingReceiver& aRef);
    virtual ~CtiDevicePagingReceiver();
    int recvCommRequest(OUTMESS *OutMessage);
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual int sendCommResult(INMESS *InMessage);
    bool isTransactionComplete();

    void DecodeDatabaseReader(RWDBReader &rdr);
    void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);

   int decode(CtiXfer &xfer, int commReturnValue);
   int generate(CtiXfer &xfer);
   void getVerificationObjects(queue< CtiVerificationBase * > &work_queue);

   enum CommandState
   {
       Normal=0,
       Complete
   };

   enum StateMachine
   {
       DoInit=0,
       SendWhatStringPointerPointsTo,
       InitializeAndRead,
       SendCapcodeConfig,
       SendCapcodeNumber,
       SendFrequency,
       ReadResult,
       ReadAndCheck,
       SendSave,
       SendQuit,
       Done,
       Wait
   };

   //below enums???
protected:
   CommandState                  _command;

private:
    StateMachine          _previousState;
    StateMachine          _currentState;
    StateMachine getCurrentState();
    StateMachine getPreviousState();
    void resetStates(bool initial = false);
    void setCurrentState(StateMachine newCurrentState);
    void setPreviousState(StateMachine newPreviousState);
    string getFormattedHexCapcodeNumber(int number);
    string getFormattedFrequency();

    static const char *_change_mode;
    static const char *_read;
    static const char *_save;
    static const char *_stop;
    static const char *_char_cr_lf;
    static const char *_capcode_number;
    static const char *_capcode_config;
    static const char *_header;
    static const char *_footer;
};
#endif // #ifndef __DEV_CBC_H__

