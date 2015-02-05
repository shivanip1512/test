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

    INT CCUDecode(const INMESS &InMessage, CtiTime &ScanTime, CtiMessageList &retList);
    CtiReturnMsg*  CCUDecodeStatus(INMESS &InMessage);

    INT CCULoop(OUTMESS*);
    INT CCU711Reset(OUTMESS*);

    bool checkForTimeSyncLoop(int status);
    bool checkAlgorithmReset(int alg);
    unsigned queuedWorkCount() const;
    Cti::DeviceQueueInterface* getDeviceQueueHandler();

    YukonError_t IntegrityScan (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t GeneralScan   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    YukonError_t ResultDecode(const INMESS&, const CtiTime, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

};
