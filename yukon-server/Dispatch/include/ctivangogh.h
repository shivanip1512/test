

#ifndef __VANGOGH_H__
    #pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   ctivangogh
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/ctivangogh.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/04/30 16:30:11 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __VANGOGH_H__


#include <functional>
#include <iostream>
#include <set>
using namespace std;


#include <rw\cstring.h>
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
#include "msg_email.h"
#include "msg_commerrorhistory.h"
#include "msg_lmcontrolhistory.h"
#include "guard.h"
#include "mutex.h"
#include "pending_info.h"
#include "tbl_state_grp.h"
#include "tbl_alm_ngroup.h"
#include "tbl_alm_nloc.h"
#include "tbl_lm_controlhist.h"
#include "tbl_commerrhist.h"
#include "tbl_rawpthistory.h"
#include "tbl_signal.h"
#include "tbl_dv_cicust.h"
#include "rtdb.h"



#define MAX_ALARM_TRX 256


class CtiConnectionManager;
class CtiVanGoghConnectionManager;
class CtiPointRegistrationMsg;
class CtiPointBase;
class CtiPointStatus;

class IM_EX_CTIVANGOGH CtiVanGogh : public CtiServer
{
public:

    typedef set< CtiPendingPointOperations >  CtiPendingOpSet_t;
    typedef set< CtiTableNotificationGroup >  CtiNotificationGroupSet_t;
    typedef set< CtiTableGroupRecipient >     CtiRecipientSet_t;
    typedef set< CtiTableStateGroup >         CtiStateGroupSet_t;
    typedef set< CtiDeviceBaseLite >          CtiDeviceLiteSet_t;
    typedef set< CtiTableCICustomerBase >     CtiDeviceCICustSet_t;

    typedef struct
    {
        INT grpid;
        RWCString name;
    } CtiA2DTranslation_t;

private:
    /*
     *  VGMain's Data Stores.
     */

    RWThreadFunction  _rphThread;       // RawPointHistory....
    RWThreadFunction  _archiveThread;
    RWThreadFunction  _timedOpThread;
    RWThreadFunction  _dbThread;

    CtiQueue< CtiSignalMsg, less<CtiSignalMsg> > _signalMsgQueue;
    CtiQueue< CtiTableRawPointHistory, less<CtiTableRawPointHistory> > _archiverQueue;
    CtiQueue< CtiTableLMControlHistory, less<CtiTableLMControlHistory> > _lmControlHistoryQueue;
    CtiQueue< CtiTableCommErrorHistory, less<CtiTableCommErrorHistory> > _commErrorHistoryQueue;

    // These are the signals which have not been cleared by a client app
    CtiRTDB< CtiTableSignal >  _signalsPending;
    CtiA2DTranslation_t        _alarmToDestInfo[256];  // This holds translations from alarm ID to DestinationID.
    CtiPendingOpSet_t          _pendingPointInfo;      // This holds temporal information on a per point basis.
    CtiNotificationGroupSet_t  _notificationGroupSet;  // Notification Groups
    CtiRecipientSet_t          _recipientSet;          // Email/pager targets
    CtiStateGroupSet_t         _stateGroupSet;
    CtiDeviceLiteSet_t         _deviceLiteSet;
    CtiDeviceCICustSet_t       _ciCustSet;             // customer device.

    UINT writeRawPointHistory(bool justdoit, int maxrowstowrite);
    void verifyControlTimesValid( CtiPendingPointOperations &ppc );
    RWCString resolveEmailMsgDescription( const CtiEmailMsg &aMail );

public:

    typedef CtiServer Inherited;

    CtiVanGogh();// :_signalMsgQueue(100),CtiServer(1000);
    CtiVanGogh(int QueueSize);// :_signalMsgQueue(100),CtiServer(QueueSize);
    virtual ~CtiVanGogh();

    virtual void  clientShutdown(CtiConnectionManager *&CM);
    virtual int   commandMsgHandler(const CtiCommandMsg *Cmd);

    virtual void  shutdown();
    int   postDBChange(const CtiDBChangeMsg &Msg);

    int   registration(CtiVanGoghConnectionManager *, const CtiPointRegistrationMsg &aReg);
    int   mail(const CtiEmailMsg &aMail);

    int   execute();
    void  VGMainThread();
    void  VGConnectionHandlerThread();
    void  VGSignalThread();
    void  VGArchiverThread();
    void  VGTimedOperationThread();
    void  VGDBWriterThread();
    void  VGRPHWriterThread();

    INT   archivePointDataMessage(const CtiPointDataMsg &aPD);
    INT   archiveSignalMessage(const CtiSignalMsg& aSig);
    INT   archiveCommErrorHistoryMessage(const CtiCommErrorHistoryMsg& aCEHM);

    CtiMessage* messageToConnectionViaGlobalList(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg);
    CtiMessage* messageToConnectionViaPointList(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg);

    INT postMessageToClients(CtiMessage *pMsg);
    INT analyzeMessageData(CtiMessage *pMsg);
    INT analyzeMultiMessage(CtiMultiMsg *pMulti);

    CtiMultiMsg* generateMultiMessageForConnection(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg);

    INT   assembleMultiForConnection(const CtiVanGoghConnectionManager &Conn, CtiMessage *pMsg, RWOrdered &aOrdered);
    INT   assembleMultiFromMultiForConnection(const CtiVanGoghConnectionManager &Conn,
                                              CtiMessage                        *pMsg,
                                              RWOrdered                         &Ord);
    INT   assembleMultiFromPointDataForConnection(const CtiVanGoghConnectionManager &Conn,
                                                  CtiMessage                        *pMsg,
                                                  RWOrdered                         &Ord);
    INT   assembleMultiFromSignalForConnection(const CtiVanGoghConnectionManager &Conn,
                                               CtiMessage                        *pMsg,
                                               RWOrdered                         &Ord);

    BOOL  isConnectionAttachedToMsgPoint(const CtiVanGoghConnectionManager   &Conn,
                                         const LONG                          pID);
    BOOL  isPointDataForConnection(const CtiVanGoghConnectionManager   &Conn,
                                   const CtiPointDataMsg               &Msg);
    BOOL  isPointDataNewInformation(const CtiPointDataMsg &Msg);
    BOOL  isSignalForConnection(const CtiVanGoghConnectionManager   &Conn,
                                const CtiSignalMsg                  &Msg);

    int   processMessage(CtiMessage *pMsg);
    INT   postMOAUploadToConnection(CtiVanGoghConnectionManager &VGCM, int flags);

    INT   loadPendingSignals();
    void  purifyClientConnectionList();
    void  updateRuntimeDispatchTable();
    void  writeLMControlHistoryToDB(bool justdoit = false);
    void  writeCommErrorHistoryToDB(bool justdoit = false);
    void  writeArchiveDataToDB(bool justdoit = false);
    void  writeSignalsToDB(bool justdoit = false);
    void  refreshCParmGlobals(bool force = false);
    INT   checkDataStateQuality(CtiMessage *pMsg, CtiMultiWrapper &aWrap);
    INT   checkPointDataStateQuality(CtiPointDataMsg *pData, CtiMultiWrapper &aWrap);
    INT   checkMultiDataStateQuality(CtiMultiMsg *pMulti, CtiMultiWrapper &aWrap);
    INT   checkCommandDataStateQuality(CtiCommandMsg *pCmd, CtiMultiWrapper &aWrap);
    INT   checkSignalStateQuality(CtiSignalMsg  *pSig, CtiMultiWrapper &aWrap);
    INT   analyzeForStatusAlarms(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap, CtiPointBase &point);
    INT   analyzeForNumericAlarms(CtiPointDataMsg  *pData, CtiMultiWrapper &aWrap, CtiPointBase &point);
    INT   markPointNonUpdated(CtiPointBase &point, CtiMultiWrapper &aWrap);
    CtiVanGoghConnectionManager* getPILConnection();
    CtiVanGoghConnectionManager* getScannerConnection();
    void  validateConnections();
    void  postSignalAsEmail( const CtiSignalMsg &sig );
    void  loadAlarmToDestinationTranslation();
    INT   sendMail(const CtiSignalMsg &sig, const CtiTableNotificationGroup &grp, const CtiTableGroupRecipient &recip, RWCString subject = RWCString());
    INT   sendMail(const CtiEmailMsg &aMail, const CtiTableGroupRecipient &recip);

    RWCString getAlarmStateName( INT alarm );

    virtual int clientPurgeQuestionables(PULONG pDeadClients);

    virtual int   clientRegistration(CtiConnectionManager *CM);
    virtual int   clientArbitrationWinner(CtiConnectionManager *CM);
    void messageDump(CtiMessage *pMsg);
     void doPendingOperations();
    void loadRTDB(bool force = false, CtiMessage *pMsg = NULL);     // Loads all relevant RTDB elements
    void loadStateNames();
    void loadDeviceNames();
    void loadCICustomers();
    RWCString resolveStateName(const CtiPointBase &aPoint , LONG rawValue);
    RWCString resolveDeviceName(const CtiPointBase &aPoint);
    RWCString resolveDeviceObjectType(const LONG devid);
    RWCString resolveDeviceDescription(LONG PAO);
    CtiTableGroupRecipient* getRecipient( LONG locid );
    CtiTableCICustomerBase* getCustomer( LONG custid );
    void sendSignalToGroup(LONG ngid, CtiSignalMsg sig);
    void  sendEmailToGroup(LONG ngid, CtiEmailMsg email);
    LONG alarmToNotificationGroup(INT signaltrx);

    void displayConnections(void);

    bool ablementDevice(LONG did, UINT setmask, UINT tagmask, RWCString user);
    bool ablementPoint(LONG pid, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList);

    CtiDeviceLiteSet_t::iterator deviceLiteFind(const LONG paoId);
    bool removeLimitFromPending( LONG pID, INT statelimit );
    bool removePointDataFromPending( LONG pID, const CtiPointDataMsg &Data);
    void establishListener();
    void reportOnThreads();
    void bumpDeviceToAlternateRate(CtiPointBase *pPoint);
    void writeMessageToScanner(const CtiCommandMsg *Cmd);
    void writeMessageToPIL(CtiMessage *&pReq);
    void writeControlMessageToPIL(LONG deviceid, LONG rawstate, CtiPointStatus *pPoint, const CtiCommandMsg *&Cmd  );
    int processControlMessage(CtiLMControlHistoryMsg *pMsg);
    void updateControlHistory(  long pendid, int cause, const RWTime &thetime = RWTime(), RWTime &now = RWTime() );
    void dumpPendingOps();

    void insertAndPostControlHistoryPoints( CtiPendingPointOperations &ppc,
                                            const RWTime &now,
                                            bool insertctlhistrow = true,       // A row will be added to table lmcontrolhistory
                                            bool postctlhistaipts = true,       // Analog points for cumulative control times will be generated
                                            bool postctlstopaipnt = false);      // Analog point indicating the control remaining will be posted.
    INT updateDeviceStaticTables(LONG did, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList);
    INT updatePointStaticTables(LONG pid, UINT setmask, UINT tagmask, RWCString user, CtiMultiMsg &sigList);
    void adjustDeviceDisableTags();
    void loadDeviceLites();


};

#endif // #ifndef __VANGOGH_H__
