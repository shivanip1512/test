#pragma once

#include <set>

#include "con_mgr.h"
#include "con_mgr_vg.h"
#include "server_b.h"
#include "dev_base_lite.h"
#include "dlldefs.h"
#include "msg_pdata.h"
#include "msg_dbchg.h"
#include "msg_multiwrap.h"
#include "msg_pcreturn.h"
#include "msg_lmcontrolhistory.h"
#include "msg_tag.h"
#include "guard.h"
#include "mutex.h"
#include "pendingopthread.h"
#include "pending_info.h"
#include "pt_dyn_dispatch.h"
#include "pt_status.h"
#include "signalmanager.h"
#include "tagmanager.h"
#include "tbl_state_grp.h"
#include "tbl_alm_ngroup.h"
#include "tbl_lm_controlhist.h"
#include "tbl_pt_limit.h"
#include "tbl_rawpthistory.h"
#include "tbl_signal.h"
#include "tbl_ci_cust.h"
#include "tbl_contact_notification.h"
#include "rtdb.h"
#include "connection_client.h"
#include "rph_archiver.h"

class CtiPointRegistrationMsg;
class CtiPointBase;
class CtiPointStatus;
class CtiPointNumeric;
class CtiDynamicPointDispatch;
class CtiPointClientManager;

class CtiVanGogh : public CtiServer
{
public:

    typedef std::set< CtiTableCICustomerBase >       CtiDeviceCICustSet_t;

    typedef struct
    {
        INT grpid;
        std::string name;
    } CtiA2DTranslation_t;

private:
    /*
     *  VGMain's Data Stores.
     */

    CtiVanGogh(CtiPointClientManager* externalMgrPtr);  //  Delegating constructor for unit test support

    std::unique_ptr<CtiPointClientManager> _localPointClientMgr;  
    CtiPointClientManager     &PointMgr;

    CtiPendingOpThread _pendingOpThread;

    Cti::Dispatch::RawPointHistoryArchiver _rphArchiver;

    boost::thread  _archiveThread;
    boost::thread  _timedOpThread;
    boost::thread  _dbSigThread;
    boost::thread  _dbSigEmailThread;
    boost::thread  _appMonitorThread;
    boost::thread  _cacheHandlerThread1, _cacheHandlerThread2, _cacheHandlerThread3;

    CtiFIFOQueue< CtiSignalMsg > _signalMsgPostQueue;   // Messages are processed out of this queue for emailing.

    CtiFIFOQueue< CtiSignalMsg > _signalMsgQueue;

    // These are the signals which have not been cleared by a client app
    CtiA2DTranslation_t        _alarmToDestInfo[256];  // This holds translations from alarm ID to DestinationID.
    std::map< long, CtiTableNotificationGroup >  _notificationGroups;
    std::map< long, Cti::DeviceBaseLite > _deviceLites;

    CtiSignalManager           _signalManager;
    CtiTagManager              _tagManager;

    CtiClientConnection*       _notificationConnection;
    bool ShutdownOnThreadTimeout;

    void checkNumericReasonability(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig );
    void checkNumericLimits(int alarm, CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointNumeric &pointNumeric, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig );
    INT getNumericLimitFromHighLow(int alarmOffset, int alarm);

    void checkStatusUCOS(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig );
    void checkStatusCommandFail(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatchSPtr &pDyn, CtiSignalMsg *&pSig );
    void checkStatusState(int alarm, const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatch &dpd, CtiSignalMsg *&pSig );
    void tagSignalAsAlarm(const CtiPointBase &point, CtiSignalMsg *&pSig, int alarm, const CtiPointDataMsg *pData);
    void updateDynTagsForSignalMsg( const CtiPointBase &point, CtiSignalMsg *&pSig );

    bool ablementDevice(Cti::DeviceBaseLite &dLite, UINT setmask, UINT tagmask);
    bool ablementPoint(const CtiPointBase &point, bool &devicedifferent, UINT setmask, UINT tagmask, std::string user, CtiMultiMsg &Multi);

    void bumpDeviceFromAlternateRate(const CtiPointBase &point);
    void bumpDeviceToAlternateRate(const CtiPointBase &point);

    void acknowledgeCommandMsg( const CtiPointBase &point, const CtiCommandMsg *Cmd, int alarmcondition );
    void acknowledgeAlarmCondition( const CtiPointBase &point, const CtiCommandMsg *Cmd, int alarmcondition );
    bool processInputFunction(CHAR Char);
    void queueSignalToSystemLog( CtiSignalMsg *&pSig );
    void stopDispatch();
    static void sendbGCtrlC( const std::string & who );
    bool limitStateCheck( const int alarm, const CtiTablePointLimit &limit, double val, int &direction);
    bool checkMessageForPreLoad(CtiMessage *MsgPtr);
    void findPreLoadPointId(CtiMessage *MsgPtr, std::set<long> &ptIdList);
    void groupControlStatusVerification(unsigned long pointID);

    void sendTagUpdate(const CtiTablePointDispatch &dispatch, const std::string &addnl, const std::string &user);

    CtiConnection* getNotificationConnection();
    CtiMultiMsg* resetControlHours();

    struct StalePointTimeData
    {
        CtiTime time;
        long pointID;
        bool operator<(const StalePointTimeData &rhs) const
        {
            return time < rhs.time;
        }
    };
    std::multiset<StalePointTimeData> _expirationSet;//This is yucky. Oh well.
    std::map<long, CtiTime> _pointUpdatedTime;//The whole point of this is to give me a time associated with these points.
    void loadStalePointMaps(int pointID = 0);
    void processStalePoint(const CtiPointBase &point, CtiDynamicPointDispatch &dpd, int updateType, const CtiPointDataMsg &aPD, CtiMultiWrapper& wrap );
    void checkForStalePoints(CtiMultiWrapper &aWrap);


    // handles dispatcher activemq static queue to receive client request to connect
    CtiListenerConnection _listenerConnection;

protected:

    CtiVanGogh(CtiPointClientManager& externalMgr, Cti::Test::use_in_unit_tests_only &);  //  For unit tests
    
public:

    typedef CtiServer Inherited;

    CtiVanGogh();
    virtual ~CtiVanGogh();

    CtiVanGogh(const CtiVanGogh &) = delete;
    CtiVanGogh &operator=(const CtiVanGogh &) = delete;

    void  clientShutdown(CtiServer::ptr_type &CM) override;
    void  commandMsgHandler(CtiCommandMsg *Cmd) override;

    void  shutdown() override;
    void  postDBChange(const CtiDBChangeMsg &Msg);

    void  shutdownAllClients();

    void  registration(CtiServer::ptr_type& CM, const CtiPointRegistrationMsg &aReg);

    int   execute();
    void  VGMainThread();
    void  VGConnectionHandlerThread();
    void  VGArchiverThread();
    void  VGTimedOperationThread();
    void  VGDBSignalWriterThread();
    void  VGDBSignalEmailThread();
    void  VGAppMonitorThread();
    void  VGCacheHandlerThread(int threadNumber);

    void  archivePointDataMessage(const CtiPointDataMsg &aPD);
    INT   archiveSignalMessage(const CtiSignalMsg& aSig);

    void postMessageToClients(CtiMessage *pMsg);
    void processMessageData(CtiMessage *pMsg);
    void processMultiMessage(CtiMultiMsg *pMulti);

    CtiMultiMsg* generateMultiMessageForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg);

    INT   assembleMultiForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg, CtiMultiMsg_vec &aOrdered);
    INT   assembleMultiFromMultiForConnection(const CtiServer::ptr_type &Conn,
                                              CtiMessage                        *pMsg,
                                              CtiMultiMsg_vec                         &Ord);
    INT   assembleMultiFromPointDataForConnection(const CtiServer::ptr_type &Conn,
                                                  CtiMessage                        *pMsg,
                                                  CtiMultiMsg_vec                         &Ord);
    INT   assembleMultiFromSignalForConnection(const CtiServer::ptr_type &Conn,
                                               CtiMessage                        *pMsg,
                                               CtiMultiMsg_vec                         &Ord);
    INT assembleMultiFromTagForConnection(const CtiServer::ptr_type &Conn, CtiMessage *pMsg, CtiMultiMsg_vec &Ord);

    BOOL  isConnectionAttachedToMsgPoint(const CtiServer::ptr_type &Conn,
                                         const LONG                          pID);
    BOOL  isPointDataForConnection(const CtiServer::ptr_type &Conn, const CtiPointDataMsg &Msg);
    static bool hasPointDataChanged(const CtiPointDataMsg &Msg, const CtiDynamicPointDispatch &Dyn);
    static bool isDuplicatePointData(const CtiPointDataMsg &Msg, const CtiDynamicPointDispatch &Dyn);
    BOOL  isSignalForConnection(const CtiServer::ptr_type &Conn, const CtiSignalMsg &Msg);
    BOOL  isTagForConnection(const CtiServer::ptr_type   &Conn, const CtiTagMsg &Msg);

    YukonError_t processMessage(CtiMessage *pMsg);
    void postRegistrationUpload(CtiVanGoghConnectionManager& VGCM, std::set<long> &ptIds, const bool tag_as_moa);

    void  loadPendingSignals();
    void  purifyClientConnectionList();
    void  updateRuntimeDispatchTable(bool force = false);
    void  writeSignalsToDB(bool justdoit = false);
    void  refreshCParmGlobals(bool force = false);
    YukonError_t checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap);
    YukonError_t checkPointDataStateQuality(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap);
    YukonError_t checkMultiDataStateQuality(CtiMultiMsg *pMulti, CtiMultiWrapper &aWrap);
    YukonError_t commandMsgUpdateFailedHandler(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap);
    YukonError_t checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap);
    void  checkForStatusAlarms (const CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point);
    void  checkForNumericAlarms(CtiPointDataMsg &pData, CtiMultiWrapper &aWrap, const CtiPointBase &point);
    YukonError_t markPointNonUpdated(const CtiPointBase &point, CtiMultiWrapper &aWrap);
    CtiServer::ptr_type getPorterConnection();
    CtiServer::ptr_type getScannerConnection();
    void  validateConnections();
    void  postSignalAsEmail( CtiSignalMsg &sig );
    void  loadAlarmToDestinationTranslation();

    INT   sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp);
    std::string getAlarmStateName( INT alarm );

    int clientPurgeQuestionables(PULONG pDeadClients) override;
    std::string getMyServerName() const override;
    YukonError_t clientRegistration(CtiServer::ptr_type &CM) override;
    int   clientArbitrationWinner(CtiServer::ptr_type &CM) override;
    void messageDump(CtiMessage *pMsg);
    void loadRTDB(bool force = false, CtiMessage *pMsg = NULL);     // Loads all relevant RTDB elements
    std::string resolveDeviceNameByPaoId(const LONG PAOId);
    std::string resolveDeviceName(const CtiPointBase &aPoint);
    std::string resolveDeviceObjectType(const LONG devid);
    bool isDeviceGroupType(const LONG devid);
    void sendSignalToGroup(LONG ngid, const CtiSignalMsg& sig);
    LONG alarmToNotificationGroup(INT signaltrx);

    std::string displayConnections();

    boost::optional<Cti::DeviceBaseLite &> findDeviceLite(const LONG paoId);
    void reportOnThreads();
    void writeMessageToScanner(const CtiCommandMsg *Cmd);
    void writeMessageToClient(const CtiMessage *pReq, std::string clientName);
    bool writeControlMessageToPIL(LONG deviceid, LONG rawstate, const CtiPointStatus &point, const CtiCommandMsg *Cmd );
    void writeAnalogOutputMessageToPIL(long deviceid, long pointid, long value, const CtiCommandMsg *Cmd);
    int processControlMessage(CtiLMControlHistoryMsg *pMsg);

    bool updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, std::string user, CtiMultiMsg &sigList);
    bool updatePointStaticTables (LONG pid, UINT setmask, UINT tagmask, std::string user, CtiMultiMsg &sigList);
    void adjustDeviceDisableTags(LONG id = 0, bool dbchange = false, std::string user = std::string("System"));
    void loadDeviceLites(LONG id);
    void activatePointAlarm(int alarm, CtiMultiWrapper &aWrap, const CtiPointBase &point, CtiDynamicPointDispatch &dpd, bool activate);

    void processTagMessage(CtiTagMsg &tagMsg);
    void updateGroupPseduoControlPoint(const CtiPointBase &point, const CtiTime &delaytime);

    bool addToPendingSet(std::unique_ptr<CtiPendingPointOperations>&& pendingOp, CtiTime &updatetime = CtiTime());
    bool removePointDataFromPending(LONG pID);
};

