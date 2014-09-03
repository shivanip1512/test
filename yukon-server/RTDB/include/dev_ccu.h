#pragma once

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "dev_ccu_queue_interface.h"

class IM_EX_DEVDB CtiDeviceCCU : public CtiDeviceIDLC
{
private:
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceCCU(const CtiDeviceCCU&);
    CtiDeviceCCU& operator=(const CtiDeviceCCU&);

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

    INT IntegrityScan (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    INT GeneralScan   (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = MAXPRIORITY - 4) override;
    INT ResultDecode(const INMESS&, const CtiTime, CtiMessageList   &vgList, CtiMessageList &retList, OutMessageList &outList) override;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;

};
