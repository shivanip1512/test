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
* REVISION     :  $Revision: 1.21 $
* DATE         :  $Date: 2006/09/21 21:31:38 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_LCU_H__
#define __DEV_LCU_H__


#include <windows.h>
#include <vector>
using std::vector;

#include <rw/tpslist.h>
#include <rw/tvordvec.h>

#include "ctitypes.h"
#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_idlc.h"
#include "mgr_point.h"
#include "mutex.h"

#define SEQUENCE_ACTIVE    1000
#define MISSED      1001

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

    enum
    {
        LCU_POINTOFFSET_NEVERRETRY = 101
    };

protected:

    CtiTime               _stageTime;
    CtiTime               _nextCommandTime;

    UINT                 _lcuFlags;
    UINT                 _numberStarted;

    OUTMESS              *_lastControlMessage;
    bool                 _lockedOut;

private:

    typedef CtiDeviceIDLC Inherited;

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

    static CtiMutex      _staticMux;
    static CtiMutex      _lcuExclusionMux;     // Must be acquired before examining exclusion lists

    string            _lastCommand;

    vector< pair<ULONG, double> >  _honktime;

    vector< pair<LONG, UINT> > _controlledGroupVector; // Vector of groupids, and TrxId for that group.  Used initially for MPC bit mashing 12/31/04 CGP

public:

    CtiDeviceLCU(INT type = TYPE_LCU415);
    CtiDeviceLCU(const CtiDeviceLCU& aRef);
    virtual ~CtiDeviceLCU();
    CtiDeviceLCU& operator=(const CtiDeviceLCU& aRef);

    bool           isLCULockedOut( ) const;
    bool           isLCULockedOut( INMESS *InMessage );
    BOOL           isStagedUp(const CtiTime &tRef);
    BOOL           isBusyByCommand(const CtiTime &aTime) const;

    UINT           getNumberStarted() const;
    CtiDeviceLCU&  setNumberStarted( const UINT ui );

    UINT           setFlags( const UINT mask = 0 );             // sets none by default
    UINT           resetFlags( const UINT mask = UINT_MAX );    // resets all by default
    UINT           getFlags();
    bool           isFlagSet(UINT flags) const;                 // More than one may be OR'd together.

    CtiTime         getStageTime() const;
    CtiDeviceLCU&  setStageTime(const CtiTime& aTime);

    CtiTime         getNextCommandTime() const;
    CtiDeviceLCU&  setNextCommandTime(const CtiTime& aTime);

    OUTMESS*       getLastControlMessage();      // return OUTMESS or NULL
    CtiDeviceLCU&  setLastControlMessage(const OUTMESS *pOutMessage); // delete and set to new val.
    void           deleteLastControlMessage();   // delete and set NULL
    OUTMESS*       releaseLastControlMessage();  // set NULL, we've writen it into a queue

    CtiLCUType_t   getLCUType() const;

    INT            lcuFastScanDecode(OUTMESS *&OutMessage, INMESS *InMessage, CtiLCUResult_t &resultCode, bool globalControlAvailable, list< CtiMessage* >  &vgList);

    INT            lcuDecode(INMESS*,CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    CtiReturnMsg*  lcuDecodeStatus(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeAnalogs(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeDigitalInputs(INMESS *InMessage);
    CtiReturnMsg*  lcuDecodeAccumulators(INMESS *InMessage, list< OUTMESS* > &outList);


    CtiMutex& getLCUExclusionMux();

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

    static bool excludeALL();
    static ULONG getSlowScanDelay();


    static ULONG   lcuRetries();
    void           lcuResetFlagsAndTags();
    INT            lcuReset(OUTMESS *&OutMessage);
    INT            lcuFreeze(OUTMESS *&OutMessage);

    virtual INT    AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);
    virtual INT    ResultDecode(INMESS*,CtiTime&, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT    ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT    ErrorDecode(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* >   &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual CtiTime selectCompletionTime() const;

    bool           exceedsDutyCycle(BYTE *bptr);
    bool           watchBusyBit() const;

    virtual INT getProtocolWrap() const;
    INT lcuLockout(OUTMESS *&OutMessage, bool set);

    CtiPointDataMsg* getPointSet( int status );   //ecs 12/10/2004
    CtiPointDataMsg* getPointClear( int status );   //ecs 12/20/2004

    void pushControlledGroupInfo(LONG LMGIDControl, UINT TrxID);
    bool popControlledGroupInfo(LONG &LMGIDControl, UINT &TrxID);
    virtual void resetForScan(int scantype);

};
#endif // #ifndef __DEV_LCU_H__
