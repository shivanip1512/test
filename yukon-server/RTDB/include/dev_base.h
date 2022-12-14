#pragma once

#include "dsm2.h"
#include "cmdparse.h"
#include "dev_exclusion.h"
#include "config_device.h"
#include "rte_base.h"
#include "tbl_base.h"
#include "tbl_scanrate.h"
#include "tbl_pao_lite.h"
#include "tbl_paoexclusion.h"
#include "tbl_dyn_paoinfo.h"
#include "tbl_static_paoinfo.h"
#include "pt_base.h"
#include "utility.h"
#include "loggable.h"

#include <boost/shared_ptr.hpp>
#include <set>

class CtiMessage;
class CtiRequestMsg;
class CtiRouteManager;
class CtiPointManager;
class CtiTransmitterInfo;

namespace Cti {
    class DeviceQueueInterface;
namespace Protocols {
    class Interface;
}
namespace Devices {
    struct DeviceHandler;
    class RfnDevice;
}
}



/*
 *  This is a class used as a base for all others.... currently he branches
 *  into CtiDeviceSingle and CtiDeviceGroup.. This guy is destinied to have MANY
 *  virtual functions.
 */
class IM_EX_DEVDB CtiDeviceBase : public CtiTblPAOLite
{
public:
    typedef CtiTableDynamicPaoInfo::PaoInfoKeys               PaoInfoKeys;
    typedef CtiTableDynamicPaoInfoIndexed::PaoInfoKeysIndexed PaoInfoKeysIndexed;

    enum PutConfigModifiers
    {
        PutConfigAssignForce = 0x00000001
    };

    typedef std::list<OUTMESS *> OutMessageList;
    typedef std::list<CtiMessage *> CtiMessageList;

private:

    void setPaoType( const std::string& category, const std::string& type ) override;
    DeviceTypes _deviceType;

    typedef CtiTblPAOLite Inherited;

    int _currTrxID;
    int _responsesOnTrxID;
    CtiTime _lastReport;

protected:

    mutable CtiMutex _classMutex;
    CtiDeviceExclusion  _exclusion;

    INT  _commFailCount;          //  Consecutive failures to this device.
    INT  _attemptCount;           //  Cumulative. Attempts to communicate with the device
    INT  _attemptFailCount;       //  Cumulative. Failed with no retries
    INT  _attemptRetryCount;      //  Cumulative. Failed, but retries remain
    INT  _attemptSuccessCount;    //  Cumulative. Comms successful.


    CtiPointManager *_pointMgr;    //  Porter or Scanner's point manager, assigned by attachPointManagerToDevices()
    CtiRouteManager *_routeMgr;    //  Porter's route manager, assigned by attachRouteManagerToDevices()

    bool _logOnNeeded;

    bool _singleDevice;                           //  This should be one for any device not a group.
    CtiTableDeviceBase _deviceBase;               //  This guy used to give us a LOT of members by being our parent!

    void setDeviceType( const DeviceTypes type );

    std::map<CtiTableStaticPaoInfo::PaoInfoKeys, CtiTableStaticPaoInfo> _staticPaoInfo;  //  This is the misc data that is generated by the dbeditor/clients

    void populateOutMessage( OUTMESS &om );

    // Places error onto the retlist, DELETES OUTMESSAGE
    YukonError_t insertReturnMsg(YukonError_t retval, OUTMESS *&om, CtiMessageList &retList, const std::string &error) const;

public:

    void purgeDynamicPaoInfo();
    void purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::PaoInfoKeys key);
    void purgeStaticPaoInfo();

    typedef std::vector< CtiTablePaoExclusion > exclusions;
    typedef std::vector< std::pair< unsigned long, CtiTime > > prohibitions;

    CtiDeviceBase();
    virtual ~CtiDeviceBase();

    bool operator<(const CtiDeviceBase& rhs) const;

    virtual CtiRouteSPtr getRoute(LONG RteId) const;  //  virtual for unit tests
    CtiRouteManager*     getRouteManager() const;
    CtiDeviceBase&       setRouteManager(CtiRouteManager* aPtr);

    CtiDeviceBase&       setPointManager(CtiPointManager* aPtr);

    //  name hiding allows us to take this over from tbl_pao_lite
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);

    virtual std::string getSQLCoreStatement() const;

    /*
     *  Virtuals to let my inheritors play ball with me...
     *
     *  These are basically set up to allow this to FAIL if the child class doesn't redefine them.
     */

    virtual std::string getDescription(const CtiCommandParser & parse) const;

    virtual LONG getPortID() const;
    virtual LONG getAddress() const;
    virtual LONG getMasterAddress() const;
    virtual INT  getPostDelay() const;

    virtual std::string getPassword() const;
    virtual std::string getPhoneNumber() const;
    virtual LONG getMinConnectTime() const;
    virtual LONG getMaxConnectTime() const;

    virtual LONG getRouteID() const;

    virtual LONG getDemandInterval();

    virtual ULONG getUniqueIdentifier() const;
    virtual bool hasLongScanRate(const std::string &cmd) const;


    /*
     *  This method is called to mark any and all tables which may need removal based upon a DBChange.
     *  For example, Scan rate pointers must be invalidated if the editor deselects a scan type.
     */
    virtual void invalidateScanRates();
    virtual void deleteNonUpdatedScanRates();


    virtual std::string toString() const override;

    INT         ReportError(INT mess);

    /* Properly defined by the device types themselves... */
    virtual YukonError_t GeneralScan    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 11);
    virtual YukonError_t IntegrityScan  (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 11);
    virtual YukonError_t AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 12);
    virtual YukonError_t LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority = 6);
    virtual YukonError_t ResultDecode (const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual YukonError_t ProcessInMessageResult(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);

    bool executeBackgroundRequest(const std::string &commandString, const OUTMESS &OutMessageTemplate, OutMessageList &outList);
    bool executeAuxiliaryRequest(const std::string &commandString, const CtiRequestMsg &originalRequest, OutMessageList &outList, CtiMessageList &retList);
    void resumeAfterAuxiliaryRequest(const INMESS &InMessage, OutMessageList &outList, CtiMessageList &retList, CtiMessageList &vgList);

    virtual YukonError_t invokeDeviceHandler(Cti::Devices::DeviceHandler &handler);

    // This is a preprocessing method which calls ExecuteRequest.
    YukonError_t beginExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    YukonError_t beginExecuteRequestFromTemplate(CtiRequestMsg *pReq, CtiCommandParser &parse, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, const OUTMESS *OutTemplate);

    // This one is implemented in the child classes
    virtual YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    virtual INT processTrxID( int trx, CtiMessageList &vgList );
    virtual INT initTrxID( int trx, CtiCommandParser &parse, CtiMessageList &vgList );

    void propagateRequest(OUTMESS *pOM, CtiRequestMsg *pReq );
    virtual YukonError_t ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList& retList);

    bool getLogOnNeeded() const;
    void setLogOnNeeded(bool b);

    virtual void getDevicePoints(std::vector<CtiPointSPtr> &points) const;
    virtual CtiPointSPtr getDevicePointByID(INT id);
    virtual CtiPointSPtr getDevicePointByName(const std::string& pname);
    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(INT offset, CtiPointType_t type);
    virtual CtiPointSPtr getDeviceControlPointOffsetEqual(INT offset);
    virtual CtiPointSPtr getDeviceAnalogOutputPoint(INT offset);
    virtual boost::optional<long> getPointIdForOffsetAndType(int offset, CtiPointType_t type);

    virtual CtiTransmitterInfo* getTrxInfo(); // Porter side info to retrieve transmitter device bookkeeping!
    virtual bool hasTrxInfo() const;
    virtual CtiTransmitterInfo* initTrxInfo(); // Porter side info to setup transmitter device bookkeeping!
    virtual std::string getPutConfigAssignment(UINT modifier = 0);
    virtual bool isMeter() const;
    virtual INT deviceMaxCommFails() const;

    YukonError_t checkForInhibitedDevice(CtiMessageList &retList, const OUTMESS *OutMessage);

    INT             getAttemptCount() const;
    CtiDeviceBase&  setAttemptCount(const INT i);
    INT             getAttemptFailCount() const;
    CtiDeviceBase&  setAttemptFailCount(const INT i);
    INT             getAttemptRetryCount() const;
    CtiDeviceBase&  setAttemptRetryCount(const INT i);
    INT             getAttemptSuccessCount() const;
    CtiDeviceBase&  setAttemptSuccessCount(const INT i);

    virtual YukonError_t executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList);
    bool adjustCommCounts( bool &isCommFail, bool retry );
    bool isCommFailed() const;

    bool getControlInhibit() const;
    CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b);

    DeviceTypes getDeviceType() const;

    bool isSingle() const;
    bool isGroup() const;

    bool hasDynamicInfo(PaoInfoKeys k) const;
    void setDynamicInfo(PaoInfoKeys k, const std::string   &value);
    void setDynamicInfo(PaoInfoKeys k, const int           &value);
    void setDynamicInfo(PaoInfoKeys k, const unsigned int  &value);
    void setDynamicInfo(PaoInfoKeys k, const long          &value);
    void setDynamicInfo(PaoInfoKeys k, const unsigned long &value);
    void setDynamicInfo(PaoInfoKeys k, const double        &value);
    void setDynamicInfo(PaoInfoKeys k, const CtiTime       &value);

    bool getDynamicInfo(PaoInfoKeys k, std::string   &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, int           &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, unsigned int  &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, long          &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, unsigned long &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, double        &destination) const;
    bool getDynamicInfo(PaoInfoKeys k, CtiTime       &destination) const;
    //  note - this returns the value as a long for convenience - the name may need to be changed to prevent confusion if it arises
    long getDynamicInfo(PaoInfoKeys k) const;

    template <typename T>
    boost::optional<T> findDynamicInfo(PaoInfoKeys k) const
    {
        T val;

        if( ! getDynamicInfo(k, val) )
        {
            return boost::none;
        }

        return val;
    }

    // set indexed dynamic info
    void setDynamicInfo(PaoInfoKeysIndexed k, const std::vector<unsigned long> &values);

    // get indexed dynamic info
    template <typename T>
    boost::optional<std::vector<T>> findDynamicInfo(PaoInfoKeysIndexed k) const;

    bool hasStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const;
    bool setStaticInfo(const CtiTableStaticPaoInfo &info);
    bool getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, std::string &destination) const;
    bool getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, double &destination) const;
    long getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const;

    int getCurrentTrxID() const;
    int getResponsesOnTrxID() const;
    int incResponsesOnTrxID(int trxid);
    CtiDeviceBase& setResponsesOnTrxID(int cnt);
    CtiDeviceBase& setTrxID(int trx);
    void setOutMessageTrxID( UINT &omtrxid );
    void setOutMessageLMGID( LONG &omlmgid );
    void setOutMessageTargetID( LONG &omtid );

    CtiMutex& getMux() const  { return _classMutex; }

    Cti::Config::DeviceConfigSPtr getDeviceConfig();//Configs are now thread safe!
    virtual Cti::DeviceQueueInterface* getDeviceQueueHandler();

    virtual Cti::MacroOffset selectInitialMacroRouteOffset(LONG routeid) const;

    bool isTAP() const;
    virtual bool isDialup()   const;
    virtual INT getBaudRate() const;
    virtual INT getBits()     const;
    virtual INT getStopBits() const;
    virtual INT getParity()   const;

    virtual INT getProtocolWrap() const;


    // The methods below are in place to support exclusion logic and will typ. be overridden in load group objects only.
    virtual bool hasExclusions() const;
    virtual void addExclusion(CtiTablePaoExclusion &paox);
    virtual void clearExclusions();
    virtual CtiDeviceExclusion& getExclusion();
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

    virtual YukonError_t queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt = 0);
    virtual LONG deviceQueueCommunicationTime() const;          // how many millis of comm time do we have?
    virtual LONG deviceMaxCommunicationTime() const;            // maximum transmitter transmit time that this device is permitted to grab.  Assigned by db or CPARM "PORTER_MAX_TRANSMITTER_TIME"
    virtual bool getOutMessage(CtiOutMessage *&OutMessage);
    virtual unsigned queuedWorkCount() const;                   // Number of queued commnads on the device.

    virtual void setExpectedFreeze(int freeze);  //  for frozen reads

    /*
     *  The rsvpToDispatch method allows the device object to produce a message to dispatch.
     *  This message may be the result of any number of events.  The callee has no option other than to assume the messages s
     *  hould be relayed to dispatch
     */
    virtual CtiMessage* rsvpToDispatch(bool clearMessage = true);

    virtual bool isShedProtocolParent(CtiDeviceBase *otherdev)  { return false; }
    virtual bool isRestoreProtocolParent(CtiDeviceBase *otherdev)  { return false; }

    virtual bool timeToPerformPeriodicAction(const CtiTime & currentTime)   { return false; }

};

template<> IM_EX_DEVDB boost::optional<unsigned char> CtiDeviceBase::findDynamicInfo<unsigned char> (PaoInfoKeys k) const;
template<> IM_EX_DEVDB boost::optional<bool>          CtiDeviceBase::findDynamicInfo<bool>          (PaoInfoKeys k) const;
template<> IM_EX_DEVDB boost::optional<unsigned>      CtiDeviceBase::findDynamicInfo<unsigned>      (PaoInfoKeys k) const;

// explicit instantiation
template IM_EX_DEVDB boost::optional<std::vector<unsigned long>> CtiDeviceBase::findDynamicInfo<unsigned long> (PaoInfoKeysIndexed k) const;

namespace Cti {
namespace Devices {

struct DeviceHandler
{
    virtual YukonError_t execute(CtiDeviceBase &) = 0;
    virtual YukonError_t execute(RfnDevice &) = 0;
};

}
}


inline bool   CtiDeviceBase::isDialup() const                   { return false; }
inline std::string CtiDeviceBase::getDescription(const CtiCommandParser & parse) const    { return getName();}
inline bool   CtiDeviceBase::isMeter() const                    { return false;}
inline LONG   CtiDeviceBase::getPortID() const                  { return -1;}
inline LONG   CtiDeviceBase::getAddress() const                 { return -1;}
inline LONG   CtiDeviceBase::getMasterAddress() const           { return -1;}
inline INT    CtiDeviceBase::getPostDelay() const               { return 0;}
inline std::string CtiDeviceBase::getPassword() const           { return std::string();}
inline std::string CtiDeviceBase::getPhoneNumber() const        { return std::string();}
inline LONG   CtiDeviceBase::getRouteID() const                 { return -1;}
inline LONG   CtiDeviceBase::getDemandInterval()                { return LONG_MAX;}
inline void   CtiDeviceBase::invalidateScanRates()              { }
inline void   CtiDeviceBase::deleteNonUpdatedScanRates()        { }

inline INT  CtiDeviceBase::getAttemptCount() const          { CtiLockGuard<CtiMutex> guard(_classMutex); return _attemptCount;}
inline INT  CtiDeviceBase::getAttemptFailCount() const      { CtiLockGuard<CtiMutex> guard(_classMutex); return _attemptFailCount;}
inline INT  CtiDeviceBase::getAttemptRetryCount() const     { CtiLockGuard<CtiMutex> guard(_classMutex); return _attemptRetryCount;}
inline INT  CtiDeviceBase::getAttemptSuccessCount() const   { CtiLockGuard<CtiMutex> guard(_classMutex); return _attemptSuccessCount;}
inline bool CtiDeviceBase::getControlInhibit() const        { CtiLockGuard<CtiMutex> guard(_classMutex); return _deviceBase.getControlInhibit();}
inline CtiDeviceBase& CtiDeviceBase::setControlInhibit(const bool b) { CtiLockGuard<CtiMutex> guard(_classMutex); _deviceBase.setControlInhibit(b); return *this;}
inline bool CtiDeviceBase::isSingle() const                 { CtiLockGuard<CtiMutex> guard(_classMutex); return _singleDevice;}

inline int     CtiDeviceBase::getCurrentTrxID() const         { CtiLockGuard<CtiMutex> guard(_classMutex); return _currTrxID;}
inline int     CtiDeviceBase::getResponsesOnTrxID() const     { CtiLockGuard<CtiMutex> guard(_classMutex); return _responsesOnTrxID;}
inline Cti::MacroOffset  CtiDeviceBase::selectInitialMacroRouteOffset(LONG routeid = 0) const   { return Cti::MacroOffset::none; }
inline INT     CtiDeviceBase::getBaudRate() const             { return 0; }
inline INT     CtiDeviceBase::getBits() const                 { return 8; }
inline INT     CtiDeviceBase::getStopBits() const             { return ONESTOPBIT; }
inline INT     CtiDeviceBase::getParity() const               { return NOPARITY; }
inline INT     CtiDeviceBase::getProtocolWrap() const         { return ProtocolWrapNone; }
inline YukonError_t CtiDeviceBase::queueOutMessageToDevice(OUTMESS *&OutMessage, UINT *dqcnt) { return ClientErrors::None; }
inline bool    CtiDeviceBase::hasQueuedWork() const           { return false; }
inline unsigned CtiDeviceBase::queuedWorkCount() const         { return 0; }
inline bool    CtiDeviceBase::hasPreloadWork() const          { return false; }
inline CtiTime CtiDeviceBase::getPreloadEndTime() const     { return CtiTime(); }
inline LONG    CtiDeviceBase::getPreloadBytes() const         { return 0; }
inline LONG    CtiDeviceBase::getCycleTime() const            { return 0; }
inline LONG    CtiDeviceBase::getCycleOffset() const          { return 0; }
inline bool    CtiDeviceBase::operator<(const CtiDeviceBase& rhs) const { return getID() < rhs.getID(); }


typedef boost::shared_ptr< CtiDeviceBase > CtiDeviceSPtr;

//  helper functions for Porter and Scanner
void IM_EX_DEVDB attachPointManagerToDevice(const long id, CtiDeviceSPtr device, void *pointManager);
void IM_EX_DEVDB attachRouteManagerToDevice(const long id, CtiDeviceSPtr device, void *routeManager);
void IM_EX_DEVDB attachConfigManagerToDevice(const long id, CtiDeviceSPtr device, void *configManager);

