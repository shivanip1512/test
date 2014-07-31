#pragma once

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "dev_ccu_queue_interface.h"

class IM_EX_DEVDB CtiDeviceCCU : public CtiDeviceIDLC
{
    typedef CtiDeviceIDLC Inherited;

    enum Commands
    {
        Command_Loop = 456,  //  something non-zero
        Command_Reset
    };

    enum
    {
        TimeSyncToggles = 3,

        AlgorithmCount          =  8,
        AlgorithmRepeatInterval = 60
    };

    int     _tsAlgStatus[TimeSyncToggles * 2];
    int     _tsPos;
    CtiTime _tsLastCheck;

    unsigned long _algorithmCommandTime[AlgorithmCount];

    Cti::CCU711DeviceQueueInterface _queueHandler;

protected:

    CtiTime              LastPointRefresh;

public:

    CtiDeviceCCU();
    virtual ~CtiDeviceCCU();

    INT CCUDecode(const INMESS *InMessage, CtiTime &ScanTime, std::list< CtiMessage* > &retList);
    CtiReturnMsg*  CCUDecodeStatus(INMESS *InMessage);

    INT CCULoop(OUTMESS*);
    INT CCU711Reset(OUTMESS*);

    bool checkForTimeSyncLoop(int status);
    bool checkAlgorithmReset(int alg);
    unsigned queuedWorkCount() const;
    Cti::DeviceQueueInterface* getDeviceQueueHandler();

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(const INMESS*, CtiTime&, std::list< CtiMessage* >   &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);

};
