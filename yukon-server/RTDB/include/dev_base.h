/*-----------------------------------------------------------------------------*
*
* File:   dev_base
*
* Class:  CtiDeviceBase
* Date:   8/19/1999
*
* Author: Corey Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_base.h-arc  $
* REVISION     :  $Revision: 1.36 $
* DATE         :  $Date: 2005/01/18 19:11:03 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_BASE_H__
#define __DEV_BASE_H__
#pragma warning( disable : 4786)


#include <rw\cstring.h>
#include <rw\thr\mutex.h>
#include <rw/tpslist.h>

#include "boost/shared_ptr.hpp"
using boost::shared_ptr;
using namespace std;

#include "dsm2.h"

#include "cmdparse.h"
#include "counter.h"
#include "dev_exclusion.h"
#include "rte_base.h"
#include "tbl_base.h"
#include "tbl_2way.h"
#include "tbl_stats.h"
#include "tbl_scanrate.h"
#include "tbl_pao.h"
#include "tbl_paoexclusion.h"
#include "yukon.h"
#include "queues.h"
#include "utility.h"

class CtiMessage;
class CtiReturnMsg;
class CtiRequestMsg;
class CtiSignalMsg;
class CtiRouteManager;
class CtiPointBase;
class CtiPointManager;
class CtiTransmitterInfo;
class CtiDeviceBase;

#if VSLICK_TAG_WORKAROUND
typedef CtiDeviceBase * CtiDeviceSPtr;
#else
typedef shared_ptr< CtiDeviceBase > CtiDeviceSPtr;
#endif



/*
 *  This is a class used as a base for all others.... currently he branches
 *  into CtiDeviceSingle and CtiDeviceGroup.. This guy is destinied to have MANY
 *  virtual functions.
 */
class IM_EX_DEVDB CtiDeviceBase : public CtiTblPAO, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

    typedef CtiTblPAO Inherited;
    typedef vector< CtiTablePaoExclusion > exclusions;
    typedef vector< pair< unsigned long, RWTime > > prohibitions;

    CtiDeviceBase();
    CtiDeviceBase(const CtiDeviceBase& aRef);
    virtual ~CtiDeviceBase();

    CtiDeviceBase& operator=(const CtiDeviceBase& aRef);
    bool operator<(const CtiDeviceBase& rhs) const;

    CtiRouteSPtr         getRoute(LONG RteId) const;
    CtiRouteManager*     getRouteManager() const;
    CtiDeviceBase&       setRouteManager(CtiRouteManager* aPtr);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    /*
     *  Virtuals to let my inheritors play ball with me...
     *
     *  These are basically set up to allow this to FAIL if the child class doesn't redefine them.
     */

    virtual RWCString getDescription(const CtiCommandParser & parse) const;

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual INT  getPostDelay() const;

    virtual RWCString getPassword() const;
    virtual RWCString getMeterGroupName() const;
    virtual RWCString getAlternateMeterGroupName() const;
    virtual RWCString getPhoneNumber() const;
    virtual LONG getMinConnectTime() const;
    virtual LONG getMaxConnectTime() const;

    virtual LONG getRouteID() const;

    virtual LONG getDemandInterval() const;


    virtual ULONG getUniqueIdentifier() const;
    virtual bool hasLongScanRate(const RWCString &cmd) const;


    /*
     *  This method is called to mark any and all tables which may need removal based upon a DBChange.
     *  For example, Scan rate pointers must be invalidated if the editor deselects a scan type.
     */
    virtual void invalidateScanRates();
    virtual void deleteNonUpdatedScanRates();


    virtual void DumpData();

    INT         ReportError(INT mess);

    /* Properly defined by the device types themselves... */
    virtual INT ResetDevicePoints();
    virtual INT RefreshDevicePoints();
    virtual bool orphanDevicePoint(LONG id);
    virtual INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 11);
    virtual INT IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 11);
    virtual INT AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 12);
    virtual INT LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority = 6);
    virtual INT ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);
    virtual INT ProcessResult(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);

    // This one is a preprocessing method which calls the other ExecuteRequest method.
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, const OUTMESS *OutTemplate = NULL);

    // This one is implemented in the child classes
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    virtual INT processTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, RWTPtrSlist< CtiMessage >  &vgList );

    void propagateRequest(OUTMESS *pOM, CtiRequestMsg *pReq );
    virtual INT ErrorDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList);

    BOOL              getLogOnNeeded() const;
    CtiDeviceBase&    setLogOnNeeded(BOOL b = TRUE);


    virtual CtiPointBase* getDevicePointEqual(INT id);
    virtual CtiPointBase* getDevicePointEqualByName(RWCString pname);
    virtual CtiPointBase* getDevicePointOffsetTypeEqual(INT offset, INT type);
    virtual CtiPointBase* getDeviceControlPointOffsetEqual(INT offset);

    virtual CtiTransmitterInfo* getTrxInfo(); // Porter side info to retrieve transmitter device bookkeeping!
    virtual bool hasTrxInfo() const;
    virtual CtiTransmitterInfo* initTrxInfo(); // Porter side info to setup transmitter device bookkeeping!
    virtual RWCString getPutConfigAssignment(UINT level = UINT_MAX);
    virtual bool isMeter() const;
    virtual INT deviceMaxCommFails() const;

    INT checkForInhibitedDevice(RWTPtrSlist< CtiMessage > &retList, const OUTMESS *&OutMessage);

    INT getCommFailCount() const;
    CtiDeviceBase& setCommFailCount(const INT i);
    INT getAttemptCount() const;
    CtiDeviceBase& setAttemptCount(const INT i);
    INT getAttemptFailCount() const;
    CtiDeviceBase& setAttemptFailCount(const INT i);
    INT getAttemptRetryCount() const;
    CtiDeviceBase& setAttemptRetryCount(const INT i);
    INT getAttemptSuccessCount() const;
    CtiDeviceBase& setAttemptSuccessCount(const INT i);

    INT executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList);
    bool loadDevicePoints();
    bool adjustCommCounts( bool &isCommFail, bool retry );

    const CtiTableDeviceBase& getDeviceBase() const;
    CtiDeviceBase& setDeviceBase(const CtiTableDeviceBase& tbldevbase);

    bool getControlInhibit() const;
    CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b);

    bool isSingle() const;
    bool isGroup() const;

    int getCurrentTrxID() const;
    int getResponsesOnTrxID() const;
    int incResponsesOnTrxID(int trxid);
    CtiDeviceBase& setResponsesOnTrxID(int cnt);
    CtiDeviceBase& setTrxID(int trx);
    void setOutMessageTrxID( UINT &omtrxid );
    void setOutMessageLMGID( LONG &omlmgid );
    void setOutMessageTargetID( LONG &omtid );

    MutexType& getMux()  { return mutex(); }

    virtual ULONG selectInitialMacroRouteOffset(LONG routeid) const;

    bool isTAP() const;
    virtual bool isDialup() const;
    virtual INT getBaudRate() const;
    virtual INT getBits() const;
    virtual INT getStopBits() const;
    virtual INT getParity() const;

    virtual INT getProtocolWrap() const;


    // The methods below are in place to support exclusion logic and will typ. be overridden in load group objects only.
    CtiMutex& getExclusionMux();
    bool hasExclusions() const;
    void addExclusion(CtiTablePaoExclusion &paox);
    void clearExclusions();
    CtiDeviceExclusion& getExclusion();
    CtiDeviceExclusion exclusion() const; // New copy.
    exclusions getExclusions() const;
    RWTime selectCompletionTime() const;
    bool isDeviceExcluded(long id) const;
    bool isExecuting() const;
    void setExecuting(bool set = true);
    bool isExecutionProhibited(const RWTime &now = RWTime(), LONG did = 0);
    size_t setExecutionProhibited(unsigned long id, RWTime& releaseTime = RWTime(YUKONEOT));
    bool removeInfiniteProhibit(unsigned long id);

    virtual bool hasQueuedWork() const;
    virtual bool isExecutionProhibitedByInternalLogic() const;
    virtual INT queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt = 0);
    virtual LONG deviceQueueCommunicationTime() const;          // how many millis of comm time do we have?
    virtual LONG deviceMaxCommunicationTime() const;            // maximum transmitter transmit time that this device is permitted to grab.  Assigned by db or CPARM "PORTER_MAX_TRANSMITTER_TIME"
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual INT queuedWorkCount() const;                        // Number of queued commnads on the device.

    INT incQueueSubmittal(int bumpcnt, RWTime &rwt);    // Bumps the count of submitted deviceQ entries for this 5 minute window.
    INT incQueueProcessed(int bumpCnt, RWTime & rwt);   // Bumps the count of processed deviceQ entries for this 5 minute window.
    INT setQueueOrphans(int num, RWTime &rwt);          // Number of queue entries remaining on device following this pass.
    void getQueueMetrics(int index, int &submit, int &processed, int &orphan); // Return the metrics above.

    /*
     *  The rsvpToDispatch method allows the device object to produce a message to dispatch.
     *  This message may be the result of any number of events.  The callee has no option other than to assume the messages s
     *  hould be relayed to dispatch
     */
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev)  { return false; }
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev)  { return false; }

protected:

    CtiCounter          _submittal;
    CtiCounter          _processed;
    CtiCounter          _orphaned;

    INT                  _commFailCount;                        // Consecutive failures to this device.
    INT                  _attemptCount;                         // Cumulative. Attempts to communicate with the device
    INT                  _attemptFailCount;                     // Cumulative. Failed with no retries
    INT                  _attemptRetryCount;                    // Cumulative. Failed, but retries remain
    INT                  _attemptSuccessCount;                  // Cumulative. Comms successful.


    CtiPointManager      *_pointMgr;                            // Manages points associated with this Device (Device owned memory)
    CtiRouteManager      *_routeMgr;                            // Helps me find my Route.  (Memory managed elsewhere)

    union
    {
        UINT     _clear;
        struct
        {
            UINT  _logOnNeeded : 1;
        };
    };

    bool _singleDevice;                                         // This should be one for any device not a group.
    CtiTableDeviceBase _deviceBase;                             // This guy used to give us a LOT of members by being our parent!

private:

    int _currTrxID;
    int _responsesOnTrxID;
    RWTime _lastReport;

    CtiDeviceExclusion  _exclusion;
};

typedef CtiDeviceBase CtiDevice;

inline bool CtiDeviceBase::isDialup() const { return false; }
inline RWCString CtiDeviceBase::getDescription(const CtiCommandParser & parse) const    { return getName();}
inline bool CtiDeviceBase::isMeter() const               { return false;}
inline LONG CtiDeviceBase::getPortID() const             { return -1;}
inline LONG CtiDeviceBase::getAddress() const            { return -1;}
inline INT  CtiDeviceBase::getPostDelay() const          { return 0;}
inline RWCString CtiDeviceBase::getPassword() const      { return RWCString();}
inline RWCString CtiDeviceBase::getPhoneNumber() const   { return RWCString();}
inline LONG CtiDeviceBase::getRouteID() const            { return -1;}
inline LONG CtiDeviceBase::getDemandInterval() const     { return LONG_MAX;}
inline void CtiDeviceBase::invalidateScanRates()         { return;}
inline void CtiDeviceBase::deleteNonUpdatedScanRates()   { return;}
inline RWCString CtiDeviceBase::getMeterGroupName() const   { return RWCString();}
inline RWCString CtiDeviceBase::getAlternateMeterGroupName() const    { return RWCString();}

inline INT CtiDeviceBase::getCommFailCount() const       { LockGuard guard(monitor()); return _commFailCount;}
inline INT CtiDeviceBase::getAttemptCount() const        { LockGuard guard(monitor()); return _attemptCount;}
inline INT CtiDeviceBase::getAttemptFailCount() const    { LockGuard guard(monitor()); return _attemptFailCount;}
inline INT CtiDeviceBase::getAttemptRetryCount() const   { LockGuard guard(monitor()); return _attemptRetryCount;}
inline INT CtiDeviceBase::getAttemptSuccessCount() const { LockGuard guard(monitor()); return _attemptSuccessCount;}
inline bool CtiDeviceBase::getControlInhibit() const     { LockGuard guard(monitor()); return _deviceBase.getControlInhibit(); }
inline CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b) { LockGuard guard(monitor()); _deviceBase.setControlInhibit(b); return *this; }
inline bool CtiDeviceBase::isSingle() const              { LockGuard guard(monitor()); return _singleDevice; }

inline int CtiDeviceBase::getCurrentTrxID() const        { LockGuard gd(monitor()); return(_currTrxID);}
inline int CtiDeviceBase::getResponsesOnTrxID() const    { LockGuard gd(monitor()); return(_responsesOnTrxID);}
inline ULONG CtiDeviceBase::selectInitialMacroRouteOffset(LONG routeid = 0) const   { return 0; }
inline INT CtiDeviceBase::getBaudRate() const { return 0; }
inline INT CtiDeviceBase::getBits() const { return 8; }
inline INT CtiDeviceBase::getStopBits() const { return ONESTOPBIT; }
inline INT CtiDeviceBase::getParity() const { return NOPARITY; }
inline INT CtiDeviceBase::getProtocolWrap() const { return ProtocolWrapNone; }
inline bool CtiDeviceBase::isExecutionProhibitedByInternalLogic() const { return false;}
inline INT CtiDeviceBase::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt) { return NORMAL; }
inline bool CtiDeviceBase::hasQueuedWork() const { return false; }
inline INT CtiDeviceBase::queuedWorkCount() const { return 0; }
inline bool CtiDeviceBase::operator<(const CtiDeviceBase& rhs) const { return getID() < rhs.getID(); }


#endif // #ifndef __DEV_BASE_H__
