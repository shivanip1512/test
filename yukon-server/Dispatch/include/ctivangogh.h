/*-----------------------------------------------------------------------------*
*
* File:   ctivangogh
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/ctivangogh.h-arc  $
* REVISION     :  $Revision: 1.55 $
* DATE         :  $Date: 2008/10/02 18:27:29 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __VANGOGH_H__
#define __VANGOGH_H__
#pragma warning( disable : 4786)

#include <functional>
#include <iostream>
#include <set>
#include <map>
using std::set;
using std::map;
using std::iostream;


#include <rw\thr\thrfunc.h>
#include <rw/toolpro/winsock.h>
#include <rw/toolpro/socket.h>
#include <rw/toolpro/neterr.h>
#include <rw\rwerr.h>
#include <rw\thr\mutex.h>
#include <rw\tphasht.h>
#include <rw/tvslist.h>

#include "con_mgr.h"
#include "con_mgr_vg.h"
#include "server_b.h"
#include "dev_base_lite.h"
#include "dlldefs.h"
#include "msg_pdata.h"
#include "msg_dbchg.h"
#include "msg_multiwrap.h"
#include "msg_pcreturn.h"
#include "msg_commerrorhistory.h"
#include "msg_lmcontrolhistory.h"
#include "msg_tag.h"
#include "guard.h"
#include "mutex.h"
#include "pendingopthread.h"
#include "pending_info.h"
#include "pt_status.h"
#include "pttrigger.h"
#include "signalmanager.h"
#include "tagmanager.h"
#include "tbl_state_grp.h"
#include "tbl_alm_ngroup.h"
#include "tbl_lm_controlhist.h"
#include "tbl_commerrhist.h"
#include "tbl_pt_limit.h"
#include "tbl_rawpthistory.h"
#include "tbl_signal.h"
#include "tbl_ci_cust.h"
#include "tbl_contact_notification.h"
#include "rtdb.h"
#include "numstr.h"


#include <string.h>

#define MAX_ALARM_TRX 256


class CtiConnectionManager;
class CtiVanGoghConnectionManager;
class CtiPointRegistrationMsg;
class CtiPointBase;
class CtiPointStatus;
class CtiPointNumeric;
class CtiDynamicPointDispatch;

class IM_EX_CTIVANGOGH CtiVanGogh : public CtiServer
{
public:

    typedef set< CtiTableNotificationGroup >  CtiNotificationGroupSet_t;
    typedef set< CtiTableContactNotification >  CtiContactNotificationSet_t;
    typedef set< CtiDeviceBaseLite >          CtiDeviceLiteSet_t;
    typedef set< CtiTableCICustomerBase >     CtiDeviceCICustSet_t;

    typedef struct
    {
        INT grpid;
        string name;
    } CtiA2DTranslation_t;

private:
    /*
     *  VGMain's Data Stores.
     */

    CtiPendingOpThread _pendingOpThread;

    RWThreadFunction  _rphThread;       // RawPointHistory....
    RWThreadFunction  _archiveThread;
    RWThreadFunction  _timedOpThread;
    RWThreadFunction  _dbThread;
    RWThreadFunction  _dbSigThread;
    RWThreadFunction  _dbSigEmailThread;
    RWThreadFunction  _appMonitorThread;

    CtiFIFOQueue< CtiSignalMsg > _signalMsgPostQueue;   // Messages are processed out of this queue for emailing.

    CtiFIFOQueue< CtiSignalMsg > _signalMsgQueue;
    CtiFIFOQueue< CtiTableRawPointHistory > _archiverQueue;
    CtiFIFOQueue< CtiTableCommErrorHistory > _commErrorHistoryQueue;

    // These are the signals which have not been cleared by a client app
    CtiA2DTranslation_t        _alarmToDestInfo[256];  // This holds translations from alarm ID to DestinationID.
    CtiNotificationGroupSet_t  _notificationGroupSet;  // Notification Groups
    CtiContactNotificationSet_t _contactNotificationSet; // Email/pager targets
    CtiDeviceLiteSet_t         _deviceLiteSet;
    CtiDeviceCICustSet_t       _ciCustSet;             // customer device.

    CtiSignalManager           _signalManager;
    CtiTagManager              _tagManager;

    CtiConnection* _notificationConnection;
    bool ShutdownOnThreadTimeout;

    UINT writeRawPointHistory(bool justdoit, int maxrowstowrite);

    int checkNumericReasonability(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumericSPtr pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void checkNumericRateOfChange(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumericSPtr pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void checkNumericLimits(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointNumericSPtr pointNumeric, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    INT getNumericLimitFromHighLow(int alarmOffset, int alarm);

    void checkStatusUCOS(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void checkStatusCommandFail(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void checkStatusState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void checkChangeOfState(int alarm, CtiPointDataMsg *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *pDyn, CtiSignalMsg *&pSig );
    void tagSignalAsAlarm(CtiPointSPtr point, CtiSignalMsg *&pSig, int alarm, CtiPointDataMsg *pData = 0);
    void updateDynTagsForSignalMsg( CtiPointSPtr point, CtiSignalMsg *&pSig, int alarm_condition, bool condition_active );

    bool ablementDevice(CtiDeviceLiteSet_t::iterator &dliteit, UINT setmask, UINT tagmask);
    bool ablementPoint(CtiPointSPtr &pPoint, bool &devicedifferent, UINT setmask, UINT tagmask, string user, CtiMultiMsg &Multi);

    void bumpDeviceFromAlternateRate(CtiPointSPtr pPoint);
    void bumpDeviceToAlternateRate(CtiPointSPtr pPoint);

    void acknowledgeCommandMsg( CtiPointSPtr &pPt, const CtiCommandMsg *&Cmd, int alarmcondition );
    void acknowledgeAlarmCondition( CtiPointSPtr &pPt, const CtiCommandMsg *&Cmd, int alarmcondition );
    bool processInputFunction(CHAR Char);
    void queueSignalToSystemLog( CtiSignalMsg *&pSig );
    void stopDispatch();
    static void sendbGCtrlC(void *who);
    void sendPointTriggers( const CtiPointDataMsg &aPD , CtiPointSPtr point );
    void sendPendingControlRequest(const CtiPointDataMsg &aPD, CtiPointSPtr point, PtVerifyTriggerSPtr verificationPtr);
    bool limitStateCheck( const int alarm, const CtiTablePointLimit &limit, double val, int &direction);

    CtiPointDataMsg* createPointDataMsg(const CtiDynamicPointDispatch& pDyn);

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
    void processStalePoint(CtiPointSPtr pPoint, CtiDynamicPointDispatch* pDyn, int updateType, const CtiPointDataMsg &aPD, CtiMultiWrapper& wrap );
    void checkForStalePoints(CtiMultiWrapper &aWrap);

public:

    typedef CtiServer Inherited;

    CtiVanGogh();
    virtual ~CtiVanGogh();

    virtual void  clientShutdown(CtiServer::ptr_type CM);
    virtual int   commandMsgHandler(CtiCommandMsg *Cmd);

    virtual void  shutdown();
    int   postDBChange(const CtiDBChangeMsg &Msg);

    int   registration(CtiServer::ptr_type, const CtiPointRegistrationMsg &aReg);

    int   execute();
    void  VGMainThread();
    void  VGConnectionHandlerThread();
    void  VGArchiverThread();
    void  VGTimedOperationThread();
    void  VGDBWriterThread();
    void  VGDBSignalWriterThread();
    void  VGDBSignalEmailThread();
    void  VGRPHWriterThread();
    void  VGAppMonitorThread();

    INT   archivePointDataMessage(const CtiPointDataMsg &aPD);
    INT   archiveSignalMessage(const CtiSignalMsg& aSig);
    INT   archiveCommErrorHistoryMessage(const CtiCommErrorHistoryMsg& aCEHM);

    CtiMessage* messageToConnectionViaGlobalList(const CtiServer::ptr_type &Conn, CtiMessage *pMsg);
    CtiMessage* messageToConnectionViaPointList(const CtiServer::ptr_type &Conn, CtiMessage *pMsg);

    INT postMessageToClients(CtiMessage *pMsg);
    INT processMessageData(CtiMessage *pMsg);
    INT processMultiMessage(CtiMultiMsg *pMulti);

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
    BOOL  isPointDataNewInformation(const CtiPointDataMsg &Msg, const CtiDynamicPointDispatch *pDyn);
    BOOL  isSignalForConnection(const CtiServer::ptr_type &Conn, const CtiSignalMsg &Msg);
    BOOL  isTagForConnection(const CtiServer::ptr_type   &Conn, const CtiTagMsg &Msg);

    int   processMessage(CtiMessage *pMsg);
    INT   postMOAUploadToConnection(CtiServer::ptr_type &VGCM, int flags);

    INT   loadPendingSignals();
    void  purifyClientConnectionList();
    void  updateRuntimeDispatchTable(bool force = false);
    void  writeCommErrorHistoryToDB(bool justdoit = false);
    void  writeArchiveDataToDB(bool justdoit = false);
    void  writeSignalsToDB(bool justdoit = false);
    void  refreshCParmGlobals(bool force = false);
    INT   checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap);
    INT   checkPointDataStateQuality(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap);
    INT   checkMultiDataStateQuality(CtiMultiMsg *pMulti, CtiMultiWrapper &aWrap);
    INT   commandMsgUpdateFailedHandler(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap);
    INT   checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap);
    INT   checkForStatusAlarms(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point);
    INT   checkForNumericAlarms(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap, CtiPointSPtr point);
    INT   markPointNonUpdated(CtiPointSPtr point, CtiMultiWrapper &aWrap);
    CtiServer::ptr_type getPILConnection();
    CtiServer::ptr_type getScannerConnection();
    void  validateConnections();
    void  postSignalAsEmail( CtiSignalMsg &sig );
    void  loadAlarmToDestinationTranslation();

    INT   sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp);
    string getAlarmStateName( INT alarm );

    virtual int clientPurgeQuestionables(PULONG pDeadClients);
    virtual string getMyServerName() const;
    virtual int   clientRegistration(CtiServer::ptr_type CM);
    virtual int   clientArbitrationWinner(CtiServer::ptr_type CM);
    void messageDump(CtiMessage *pMsg);
    void loadRTDB(bool force = false, CtiMessage *pMsg = NULL);     // Loads all relevant RTDB elements
    void loadDeviceNames();
    void loadCICustomers(LONG id = 0);
    string resolveDeviceNameByPaoId(const LONG PAOId);
    string resolveDeviceName(const CtiPointSPtr &aPoint);
    string resolveDeviceObjectType(const LONG devid);
    string resolveDeviceDescription(LONG PAO);
    bool isDeviceIdValid(const LONG devid);
    bool isDeviceGroupType(const LONG devid);
    CtiTableContactNotification* getContactNotification(LONG notifID);
    CtiTableCICustomerBase* getCustomer( LONG custid );
    void sendSignalToGroup(LONG ngid, const CtiSignalMsg& sig);
    LONG alarmToNotificationGroup(INT signaltrx);

    void displayConnections(void);

    CtiDeviceLiteSet_t::iterator deviceLiteFind(const LONG paoId);
    void establishListener();
    void reportOnThreads();
    void writeMessageToScanner(const CtiCommandMsg *Cmd);
    void writeMessageToClient(CtiMessage *&pReq, string clientName);
    bool writeControlMessageToPIL(LONG deviceid, LONG rawstate, CtiPointStatusSPtr pPoint, const CtiCommandMsg *&Cmd  );
    int processControlMessage(CtiLMControlHistoryMsg *pMsg);
    int processCommErrorMessage(CtiCommErrorHistoryMsg *pMsg);

    INT updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, string user, CtiMultiMsg &sigList);
    INT updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, string user, CtiMultiMsg &sigList);
    void adjustDeviceDisableTags(LONG id = 0, bool dbchange = false, string user = string("System"));
    void loadDeviceLites(LONG id = 0);
    void pruneCommErrorHistory();
    void activatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *&pDyn, bool activate);
    void deactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *&pDyn);
    void reactivatePointAlarm(int alarm, CtiMultiWrapper &aWrap, CtiPointSPtr point, CtiDynamicPointDispatch *&pDyn);

    int processTagMessage(CtiTagMsg &tagMsg);
    int loadPendingControls();
    void updateGroupPseduoControlPoint(CtiPointSPtr &pPt, const CtiTime &delaytime);

    void checkForPendingLimitViolation(CtiPointDataMsg *pData, CtiPointNumericSPtr pointNumeric );
    bool addToPendingSet(CtiPendingPointOperations *&pendingOp, CtiTime &updatetime = CtiTime());
    bool removePointDataFromPending(LONG pID);
    bool isPointInPendingControl(LONG pointid);

};

#endif // #ifndef __VANGOGH_H__
