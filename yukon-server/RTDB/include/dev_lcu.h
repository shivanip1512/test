/*-----------------------------------------------------------------------------*
*
* File:   dev_tcu
*
* Class:  CtiDeviceTCU
* Date:   9/5/1999
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_lcu.h-arc  $
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2004/05/12 17:07:55 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LCU_H__
#define __DEV_LCU_H__


#include <windows.h>
#include <vector>
using namespace std;

#include <rw/tpslist.h>
#include <rw/tvordvec.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"

class IM_EX_DEVDB CtiDeviceLCU : public CtiDeviceIDLC
{
private:

    USHORT _lcuStatus;

public:
    typedef enum
    {
        LCU_STANDARD,
        LCU_LANDG,
        LCU_EASTRIVER,
        LCU_T3026

    } CtiLCUType_t;

    typedef enum
    {
        eLCUFastScan = 1,
        eLCUSlowScan,
        eLCULockedOut,
        eLCUNotBusyLockedOut,
        eLCURequeueDeviceControl,
        eLCURequeueGlobalControl,
        eLCUDeviceControlComplete,
        eLCUDeviceControlCompleteAllowTimeout,
        eLCUNotBusyNeverTransmitted,
        eLCULockedOutSpecificControl,
        eLCUAlternateRate,

        eLCUInvalid

    } CtiLCUResult_t;

protected:

    RWTime               _stageTime;
    RWTime               _nextCommandTime;

    UINT                 _lcuFlags;
    UINT                 _numberStarted;

    OUTMESS              *_lastControlMessage;

private:

    BOOL                 _lcuStaged;
    CtiLCUType_t         _lcuType;

    static BOOL          _lcuGlobalsInit;
    static bool          _excludeAllLCUs;
    static bool          _lcuObserveBusyBit;
    static ULONG         _landgMinimumTime;
    static ULONG         _landgTimeout;
    static ULONG         _landgRetries;
    static ULONG         _erepcRetries;

    static ULONG         _lcuStartStopCrossings;
    static ULONG         _lcuBitCrossings;
    static ULONG         _lcuDutyCyclePercent;
    static ULONG         _lcuWithToken;         // This is the most recent LCU to pass through containsExclusionBlock successfully.
    static ULONG         _lcuSlowScanDelay;

    static RWMutexLock   _staticMux;
    static RWMutexLock   _lcuExclusionMux;     // Must be acquired before examining exclusion lists

    RWCString            _lastCommand;

    vector< pair<ULONG, double> >  _honktime;

public:

    typedef CtiDeviceIDLC Inherited;

    CtiDeviceLCU(INT type = TYPE_LCU415);
    CtiDeviceLCU(const CtiDeviceLCU& aRef);
    virtual ~CtiDeviceLCU();
    CtiDeviceLCU& operator=(const CtiDeviceLCU& aRef);

    bool           isLCULockedOut( INMESS *InMessage );
    BOOL           isStagedUp(const RWTime &tRef);
    BOOL           isBusyByCommand(const RWTime &aTime) const;

    UINT           getNumberStarted() const;
    CtiDeviceLCU&  setNumberStarted( const UINT ui );

    UINT           setFlags( const UINT mask = 0 );             // sets none by default
    UINT           resetFlags( const UINT mask = UINT_MAX );    // resets all by default
    UINT           getFlags();
    bool           isFlagSet(UINT flags) const;                 // More than one may be OR'd together.

    RWTime         getStageTime() const;
    CtiDeviceLCU&  setStageTime(const RWTime& aTime);

    RWTime         getNextCommandTime() const;
    CtiDeviceLCU&  setNextCommandTime(const RWTime& aTime);

    OUTMESS*       getLastControlMessage();      // return OUTMESS or NULL
    CtiDeviceLCU&  setLastControlMessage(const OUTMESS *pOutMessage); // delete and set to new val.
    void           deleteLastControlMessage();   // delete and set NULL
    OUTMESS*       releaseLastControlMessage();  // set NULL, we've writen it into a queue

    CtiLCUType_t   getLCUType() const;

    INT            lcuFastScanDecode(OUTMESS *&OutMessage, INMESS *InMessage, CtiLCUResult_t &resultCode, bool globalControlAvailable, RWTPtrSlist< CtiMessage >  &vgList);

    INT            lcuDecode(INMESS*,RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    CtiReturnMsg*  lcuDecodeStatus(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeAnalogs(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeDigitalInputs(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeAccumulators(INMESS *InMessage, RWTPtrSlist< OUTMESS > &outList);


    RWMutexLock&   getExclusionMux();

    void           verifyControlLockoutState(INMESS *InMessage);
    bool           isLCUAlarmed(INMESS *InMessage);
    OUTMESS*       lcuStage(OUTMESS *&OutMessage);
    OUTMESS*       lcuControl(OUTMESS *&OutMessage);
    INT            lcuScanAll(OUTMESS *&OutMessage);
    INT            lcuLoop(OUTMESS *&OutMessage);
    INT lcuScanInternalStatus(OUTMESS *&OutMessage);
    INT lcuScanExternalStatus(OUTMESS *&OutMessage);

    static ULONG   lcuTime(OUTMESS *&OutMessage,  UINT lcuType = 0);
    static void    initLCUGlobals();
    void           dumpStatus(BYTE Byte4, BYTE Byte5);

    static void assignToken(ULONG id);
    static void releaseToken();
    static ULONG whoHasToken();
    static bool tokenIsAvailable(ULONG id_whosasking);
    static bool excludeALL();
    static ULONG getSlowScanDelay();


    static ULONG   lcuRetries();
    void           lcuResetFlagsAndTags();
    INT            lcuReset(OUTMESS *&OutMessage);
    INT            lcuFreeze(OUTMESS *&OutMessage);

    virtual INT    AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    ResultDecode(INMESS*,RWTime&, RWTPtrSlist< CtiMessage >   &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual INT    ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    bool           exceedsDutyCycle(BYTE *bptr);
    bool           watchBusyBit() const;

    virtual bool isExecutionProhibitedByInternalLogic() const;

    virtual INT getProtocolWrap() const;

};
#endif // #ifndef __DEV_LCU_H__
