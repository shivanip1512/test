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
* REVISION     :  $Revision: 1.69 $
* DATE         :  $Date: 2008/09/15 17:59:18 $
*
* Copyright (c) 1999 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_BASE_H__
#define __DEV_BASE_H__

#include <rw\thr\mutex.h>

#include <boost/shared_ptr.hpp>
#include "boostutil.h"
using boost::shared_ptr;
#include <set>

#include "dsm2.h"

#include "cmdparse.h"
#include "counter.h"
#include "dev_exclusion.h"
#include "config_device.h"
#include "rte_base.h"
#include "tbl_base.h"
#include "tbl_2way.h"
#include "tbl_stats.h"
#include "tbl_scanrate.h"
#include "tbl_pao.h"
#include "tbl_paoexclusion.h"
#include "tbl_dyn_paoinfo.h"
#include "pt_base.h"
#include "queues.h"
#include "utility.h"

using std::list;

class CtiMessage;
class CtiReturnMsg;
class CtiRequestMsg;
class CtiSignalMsg;
class CtiRouteManager;
class CtiPointBase;
class CtiPointManager;
class CtiTransmitterInfo;

namespace Cti       {
class DeviceQueueInterface;
namespace Protocol  {
class Interface;
}
}

using CtiTableDynamicPaoInfo::Keys;  //  Allows us to refer to the keys by Key::keyname


/*
 *  This is a class used as a base for all others.... currently he branches
 *  into CtiDeviceSingle and CtiDeviceGroup.. This guy is destinied to have MANY
 *  virtual functions.
 */
class IM_EX_DEVDB CtiDeviceBase : public CtiTblPAO, public RWMonitor< RWRecursiveLock< RWMutexLock > >
{
public:

    enum PutConfigModifiers
    {
        PutConfigAssignForce = 0x00000001
    };

private:

    typedef CtiTblPAO Inherited;

    int _currTrxID;
    int _responsesOnTrxID;
    CtiTime _lastReport;
    mutable CtiMutex _configMux;
    Cti::Config::CtiConfigDeviceSPtr _deviceConfig;

protected:

    CtiDeviceExclusion  _exclusion;

    CtiCounter          _submittal;
    CtiCounter          _processed;
    CtiCounter          _orphaned;

    INT                  _commFailCount;          //  Consecutive failures to this device.
    INT                  _attemptCount;           //  Cumulative. Attempts to communicate with the device
    INT                  _attemptFailCount;       //  Cumulative. Failed with no retries
    INT                  _attemptRetryCount;      //  Cumulative. Failed, but retries remain
    INT                  _attemptSuccessCount;    //  Cumulative. Comms successful.


    CtiPointManager *_pointMgr;    //  Porter or Scanner's point manager, assigned by attachPointManagerToDevices()
    CtiRouteManager *_routeMgr;    //  Porter's route manager, assigned by attachRouteManagerToDevices()

    union
    {
        UINT     _clear;
        struct
        {
            UINT  _logOnNeeded : 1;
        };
    };

    bool _singleDevice;                           //  This should be one for any device not a group.
    CtiTableDeviceBase _deviceBase;               //  This guy used to give us a LOT of members by being our parent!

    set<CtiTableDynamicPaoInfo> _paoInfo;         //  This is a list of miscellaneous data that is dynamically generated
                                                  //    by Porter, Scanner, or whomever

public:

    typedef vector< CtiTablePaoExclusion > exclusions;
    typedef vector< pair< unsigned long, CtiTime > > prohibitions;

    CtiDeviceBase();
    CtiDeviceBase(const CtiDeviceBase& aRef);
    virtual ~CtiDeviceBase();

    CtiDeviceBase& operator=(const CtiDeviceBase& aRef);
    bool operator<(const CtiDeviceBase& rhs) const;

    CtiRouteSPtr         getRoute(LONG RteId) const;
    CtiRouteManager*     getRouteManager() const;
    CtiDeviceBase&       setRouteManager(CtiRouteManager* aPtr);

    CtiDeviceBase&       setPointManager(CtiPointManager* aPtr);

    virtual void getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector);
    virtual void DecodeDatabaseReader(RWDBReader &rdr);

    /*
     *  Virtuals to let my inheritors play ball with me...
     *
     *  These are basically set up to allow this to FAIL if the child class doesn't redefine them.
     */

    virtual string getDescription(const CtiCommandParser & parse) const;

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
    virtual INT  getPostDelay() const;

    virtual string getPassword() const;
    virtual string getPhoneNumber() const;
    virtual LONG getMinConnectTime() const;
    virtual LONG getMaxConnectTime() const;

    virtual LONG getRouteID() const;

    virtual LONG getDemandInterval() const;

    virtual Cti::Protocol::Interface *getProtocol();


    virtual ULONG getUniqueIdentifier() const;
    virtual bool hasLongScanRate(const string &cmd) const;


    /*
     *  This method is called to mark any and all tables which may need removal based upon a DBChange.
     *  For example, Scan rate pointers must be invalidated if the editor deselects a scan type.
     */
    virtual void invalidateScanRates();
    virtual void deleteNonUpdatedScanRates();


    virtual void DumpData();

    INT         ReportError(INT mess);

    /* Properly defined by the device types themselves... */
    virtual void deviceInitialization(list< CtiRequestMsg * > &request_list);
    virtual INT  GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = 11);
    virtual INT  IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = 11);
    virtual INT  AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = 12);
    virtual INT  LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, INT ScanPriority = 6);
    virtual INT  ResultDecode (INMESS*, CtiTime&, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT  ProcessResult(INMESS*, CtiTime&, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);

    // This one is a preprocessing method which calls the other ExecuteRequest method.
    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, const OUTMESS *OutTemplate = NULL);

    // This one is implemented in the child classes
    virtual INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    virtual INT processTrxID( int trx, list< CtiMessage* >  &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, list< CtiMessage* >  &vgList );

    void propagateRequest(OUTMESS *pOM, CtiRequestMsg *pReq );
    virtual INT ErrorDecode(INMESS*, CtiTime&, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore);

    BOOL              getLogOnNeeded() const;
    CtiDeviceBase&    setLogOnNeeded(BOOL b = TRUE);

    virtual void getDevicePoints(vector<CtiPointSPtr> &points) const;
    virtual CtiPointSPtr getDevicePointEqual(INT id);
    virtual CtiPointSPtr getDevicePointEqualByName(string pname);
    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(INT offset, CtiPointType_t type);
    virtual CtiPointSPtr getDeviceControlPointOffsetEqual(INT offset);

    virtual CtiTransmitterInfo* getTrxInfo(); // Porter side info to retrieve transmitter device bookkeeping!
    virtual bool hasTrxInfo() const;
    virtual CtiTransmitterInfo* initTrxInfo(); // Porter side info to setup transmitter device bookkeeping!
    virtual string getPutConfigAssignment(UINT modifier = 0);
    virtual bool isMeter() const;
    virtual INT deviceMaxCommFails() const;

    INT checkForInhibitedDevice(list< CtiMessage* > &retList, const OUTMESS *&OutMessage);

    INT             getCommFailCount() const;
    CtiDeviceBase&  setCommFailCount(const INT i);
    INT             getAttemptCount() const;
    CtiDeviceBase&  setAttemptCount(const INT i);
    INT             getAttemptFailCount() const;
    CtiDeviceBase&  setAttemptFailCount(const INT i);
    INT             getAttemptRetryCount() const;
    CtiDeviceBase&  setAttemptRetryCount(const INT i);
    INT             getAttemptSuccessCount() const;
    CtiDeviceBase&  setAttemptSuccessCount(const INT i);

    INT executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList);
    bool adjustCommCounts( bool &isCommFail, bool retry );
    bool isCommFailed() const;

    const CtiTableDeviceBase& getDeviceBase() const;
    CtiDeviceBase& setDeviceBase(const CtiTableDeviceBase& tbldevbase);

    bool getControlInhibit() const;
    CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b);

    bool isSingle() const;
    bool isGroup() const;

    bool hasDynamicInfo(Keys k) const;
    bool setDynamicInfo(const CtiTableDynamicPaoInfo &paoinfo);
    bool setDynamicInfo(Keys k, const string        &value);
    bool setDynamicInfo(Keys k, const int           &value);
    bool setDynamicInfo(Keys k, const unsigned int  &value);
    bool setDynamicInfo(Keys k, const long          &value);
    bool setDynamicInfo(Keys k, const unsigned long &value);
    bool setDynamicInfo(Keys k, const double        &value);
    bool setDynamicInfo(Keys k, const CtiTime       &value);
    bool getDynamicInfo(Keys k, string        &destination) const;
    bool getDynamicInfo(Keys k, int           &destination) const;
    bool getDynamicInfo(Keys k, unsigned int  &destination) const;
    bool getDynamicInfo(Keys k, long          &destination) const;
    bool getDynamicInfo(Keys k, unsigned long &destination) const;
    bool getDynamicInfo(Keys k, double        &destination) const;
    bool getDynamicInfo(Keys k, CtiTime       &destination) const;
    //  note - this returns the value as a long for convenience - the name may need to be changed to prevent confusion if it arises
    long getDynamicInfo(Keys k) const;

    bool getDirtyInfo(vector<CtiTableDynamicPaoInfo *> &dirty_info);

    int getCurrentTrxID() const;
    int getResponsesOnTrxID() const;
    int incResponsesOnTrxID(int trxid);
    CtiDeviceBase& setResponsesOnTrxID(int cnt);
    CtiDeviceBase& setTrxID(int trx);
    void setOutMessageTrxID( UINT &omtrxid );
    void setOutMessageLMGID( LONG &omlmgid );
    void setOutMessageTargetID( LONG &omtid );

    MutexType& getMux()  { return mutex(); }

    void setDeviceConfig(Cti::Config::CtiConfigDeviceSPtr config);
    Cti::Config::CtiConfigDeviceSPtr getDeviceConfig();//Configs are now thread safe!
    virtual Cti::DeviceQueueInterface* getDeviceQueueHandler();

    virtual ULONG selectInitialMacroRouteOffset(LONG routeid) const;

    bool isTAP() const;
    virtual bool isDialup()   const;
    virtual INT getBaudRate() const;
    virtual INT getBits()     const;
    virtual INT getStopBits() const;
    virtual INT getParity()   const;

    virtual INT getProtocolWrap() const;


    // The methods below are in place to support exclusion logic and will typ. be overridden in load group objects only.
    CtiMutex& getExclusionMux();  //  this isn't implemented anywhere... ?
    virtual bool hasExclusions() const;
    virtual void addExclusion(CtiTablePaoExclusion &paox);
    virtual void clearExclusions();
    virtual CtiDeviceExclusion& getExclusion();
    virtual CtiDeviceExclusion exclusion() const; // New copy.
    virtual exclusions getExclusions() const;
    virtual CtiTime selectCompletionTime() const;
    virtual bool isDeviceExcluded(long id) const;
    virtual bool isExecuting() const;
    virtual void setExecuting(bool set = true, CtiTime when = CtiTime(YUKONEOT));
    virtual bool isExecutionProhibited(const CtiTime &now = CtiTime(), LONG did = 0);
    virtual size_t setExecutionProhibited(unsigned long id, CtiTime& releaseTime = CtiTime(YUKONEOT));
    virtual bool removeInfiniteProhibit(unsigned long id);
    virtual bool removeProhibit(unsigned long id);
    virtual void dumpProhibits(unsigned long id = 0);

    virtual bool hasQueuedWork() const;
    virtual bool hasPreloadWork() const;
    virtual CtiTime getPreloadEndTime() const;
    virtual LONG getPreloadBytes() const;
    virtual LONG getCycleTime() const;
    virtual LONG getCycleOffset() const;

    virtual bool isExecutionProhibitedByInternalLogic() const;
    virtual INT queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt = 0);
    virtual LONG deviceQueueCommunicationTime() const;          // how many millis of comm time do we have?
    virtual LONG deviceMaxCommunicationTime() const;            // maximum transmitter transmit time that this device is permitted to grab.  Assigned by db or CPARM "PORTER_MAX_TRANSMITTER_TIME"
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual INT queuedWorkCount() const;                        // Number of queued commnads on the device.

    virtual void setExpectedFreeze(int freeze);  //  for frozen reads

    INT incQueueSubmittal(int bumpcnt, CtiTime &rwt);    // Bumps the count of submitted deviceQ entries for this 5 minute window.
    INT incQueueProcessed(int bumpCnt, CtiTime & rwt);   // Bumps the count of processed deviceQ entries for this 5 minute window.
    INT setQueueOrphans(int num, CtiTime &rwt);          // Number of queue entries remaining on device following this pass.
    void getQueueMetrics(int index, int &submit, int &processed, int &orphan); // Return the metrics above.

    /*
     *  The rsvpToDispatch method allows the device object to produce a message to dispatch.
     *  This message may be the result of any number of events.  The callee has no option other than to assume the messages s
     *  hould be relayed to dispatch
     */
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev)  { return false; }
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev)  { return false; }


};

typedef CtiDeviceBase CtiDevice;

inline bool   CtiDeviceBase::isDialup() const                   { return false; }
inline string CtiDeviceBase::getDescription(const CtiCommandParser & parse) const    { return getName();}
inline bool   CtiDeviceBase::isMeter() const                    { return false;}
inline LONG   CtiDeviceBase::getPortID() const                  { return -1;}
inline LONG   CtiDeviceBase::getAddress() const                 { return -1;}
inline LONG   CtiDeviceBase::getMasterAddress() const           { return -1;}
inline INT    CtiDeviceBase::getPostDelay() const               { return 0;}
inline string CtiDeviceBase::getPassword() const                { return string();}
inline string CtiDeviceBase::getPhoneNumber() const             { return string();}
inline LONG   CtiDeviceBase::getRouteID() const                 { return -1;}
inline LONG   CtiDeviceBase::getDemandInterval() const          { return LONG_MAX;}
inline Cti::Protocol::Interface *CtiDeviceBase::getProtocol()   { return NULL;}
inline void   CtiDeviceBase::invalidateScanRates()              { }
inline void   CtiDeviceBase::deleteNonUpdatedScanRates()        { }

inline INT  CtiDeviceBase::getCommFailCount() const         { LockGuard guard(monitor()); return _commFailCount;}
inline INT  CtiDeviceBase::getAttemptCount() const          { LockGuard guard(monitor()); return _attemptCount;}
inline INT  CtiDeviceBase::getAttemptFailCount() const      { LockGuard guard(monitor()); return _attemptFailCount;}
inline INT  CtiDeviceBase::getAttemptRetryCount() const     { LockGuard guard(monitor()); return _attemptRetryCount;}
inline INT  CtiDeviceBase::getAttemptSuccessCount() const   { LockGuard guard(monitor()); return _attemptSuccessCount;}
inline bool CtiDeviceBase::getControlInhibit() const        { LockGuard guard(monitor()); return _deviceBase.getControlInhibit(); }
inline CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b) { LockGuard guard(monitor()); _deviceBase.setControlInhibit(b); return *this; }
inline bool CtiDeviceBase::isSingle() const                 { LockGuard guard(monitor()); return _singleDevice; }

inline int     CtiDeviceBase::getCurrentTrxID() const         { LockGuard gd(monitor()); return(_currTrxID);}
inline int     CtiDeviceBase::getResponsesOnTrxID() const     { LockGuard gd(monitor()); return(_responsesOnTrxID);}
inline ULONG   CtiDeviceBase::selectInitialMacroRouteOffset(LONG routeid = 0) const   { return 0; }
inline INT     CtiDeviceBase::getBaudRate() const             { return 0; }
inline INT     CtiDeviceBase::getBits() const                 { return 8; }
inline INT     CtiDeviceBase::getStopBits() const             { return ONESTOPBIT; }
inline INT     CtiDeviceBase::getParity() const               { return NOPARITY; }
inline INT     CtiDeviceBase::getProtocolWrap() const         { return ProtocolWrapNone; }
inline bool    CtiDeviceBase::isExecutionProhibitedByInternalLogic() const { return false;}
inline INT     CtiDeviceBase::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt) { return NORMAL; }
inline bool    CtiDeviceBase::hasQueuedWork() const           { return false; }
inline INT     CtiDeviceBase::queuedWorkCount() const         { return 0; }
inline bool    CtiDeviceBase::hasPreloadWork() const          { return false; }
inline CtiTime CtiDeviceBase::getPreloadEndTime() const     { return CtiTime(); }
inline LONG    CtiDeviceBase::getPreloadBytes() const         { return 0; }
inline LONG    CtiDeviceBase::getCycleTime() const            { return 0; }
inline LONG    CtiDeviceBase::getCycleOffset() const          { return 0; }
inline bool    CtiDeviceBase::operator<(const CtiDeviceBase& rhs) const { return getID() < rhs.getID(); }


typedef shared_ptr< CtiDeviceBase > CtiDeviceSPtr;

#endif // #ifndef __DEV_BASE_H__
