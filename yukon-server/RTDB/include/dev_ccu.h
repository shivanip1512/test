/*-----------------------------------------------------------------------------*
*
* File:   dev_ccu
*
* Class:  CtiDeviceCCU
* Date:   6/07/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_ccu.h-arc  $
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2006/01/16 16:57:18 $
*
* Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_CCU_H__
#define __DEV_CCU_H__


#include <windows.h>

#include <rw/tpslist.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"

class IM_EX_DEVDB CtiDeviceCCU : public CtiDeviceIDLC
{
private:

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

protected:

    CtiTime              LastPointRefresh;

public:

    typedef CtiDeviceIDLC Inherited;

    CtiDeviceCCU();

    CtiDeviceCCU(const CtiDeviceCCU& aRef);

    virtual ~CtiDeviceCCU();

    CtiDeviceCCU& operator=(const CtiDeviceCCU& aRef);

    /*
     *  These guys initiate a scan based upon the type requested.
     */

    INT CCUDecode(INMESS *InMessage, CtiTime &ScanTime, RWTPtrSlist< CtiMessage > &retList);
    CtiReturnMsg*  CCUDecodeStatus(INMESS *InMessage);

    INT CCULoop(OUTMESS*);
    INT CCU711Reset(OUTMESS*);

    bool checkForTimeSyncLoop(int status);
    bool checkAlgorithmReset(int alg);

    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT ResultDecode(INMESS*, CtiTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage,  RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

};
#endif // #ifndef __DEV_CCU_H__
