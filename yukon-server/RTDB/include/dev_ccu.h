/* REVISION     :  $Revision: 1.16.10.2 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CCU_H__
#define __DEV_CCU_H__

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "device_queue_interface.h"

class IM_EX_DEVDB CtiDeviceCCU : public CtiDeviceIDLC
{
private:

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

    Cti::DeviceQueueInterface _queueHandler;

protected:

    CtiTime              LastPointRefresh;

public:

    CtiDeviceCCU();

    CtiDeviceCCU(const CtiDeviceCCU& aRef);

    virtual ~CtiDeviceCCU();

    CtiDeviceCCU& operator=(const CtiDeviceCCU& aRef);

    INT CCUDecode(INMESS *InMessage, CtiTime &ScanTime, list< CtiMessage* > &retList);
    CtiReturnMsg*  CCUDecodeStatus(INMESS *InMessage);

    INT CCULoop(OUTMESS*);
    INT CCU711Reset(OUTMESS*);

    bool checkForTimeSyncLoop(int status);
    bool checkAlgorithmReset(int alg);
    INT queuedWorkCount() const;
    Cti::DeviceQueueInterface* getDeviceQueueHandler();

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(INMESS*, CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

};
#endif // #ifndef __DEV_CCU_H__
