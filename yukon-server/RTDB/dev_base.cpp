#include "precompiled.h"

#include <limits>

#include "cparms.h"
#include "dev_base.h"
#include "guard.h"
#include "logger.h"
#include "mgr_route.h"
#include "mgr_point.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "porter.h"
#include "utility.h"
#include "database_connection.h"
#include "database_writer.h"

using namespace std;

CtiMutex CtiDeviceBase::_configMux;

CtiDeviceBase::~CtiDeviceBase()
{
    _pointMgr = NULL;  //  We don't own these - null them out to make
    _routeMgr = NULL;  //    PC-Lint happy
}


CtiRouteSPtr CtiDeviceBase::getRoute(LONG RteId) const
{
    CtiRouteSPtr Rte;

    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(_routeMgr != NULL)
    {
        Rte = _routeMgr->getEqual(RteId);
    }

    return Rte;
}

CtiRouteManager* CtiDeviceBase::getRouteManager() const
{
    return _routeMgr;
}

CtiDeviceBase& CtiDeviceBase::setRouteManager(CtiRouteManager* aPtr)
{
    _routeMgr = aPtr;
    return *this;
}

CtiDeviceBase& CtiDeviceBase::setPointManager(CtiPointManager* aPtr)
{
    _pointMgr = aPtr;
    return *this;
}

void IM_EX_DEVDB attachPointManagerToDevice(const long id, CtiDeviceSPtr device, void *pointManager)
{
    if( device )
    {
        device->setPointManager(static_cast<CtiPointManager *>(pointManager));
    }
}

void IM_EX_DEVDB attachRouteManagerToDevice(const long id, CtiDeviceSPtr device, void *routeManager)
{
    if( device )
    {
        device->setRouteManager(static_cast<CtiRouteManager *>(routeManager));
    }
}


INT CtiDeviceBase::beginExecuteRequest(CtiRequestMsg *pReq,
                                       CtiCommandParser &parse,
                                       CtiMessageList &vgList,
                                       CtiMessageList &retList,
                                       OutMessageList &outList)
{
    return beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, 0);
}


INT CtiDeviceBase::beginExecuteRequestFromTemplate(CtiRequestMsg *pReq,
                                                   CtiCommandParser &parse,
                                                   CtiMessageList &vgList,
                                                   CtiMessageList &retList,
                                                   OutMessageList &outList,
                                                   const OUTMESS *OutTemplate)
{
    INT      status = NORMAL;
    LONG     Id;

    CtiLockGuard<CtiMutex> guard(_classMutex);

    OUTMESS  *OutMessage = NULL;   // This memory MUST be cleaned up after the NEXUS WRITE!

    if(OutTemplate != NULL)
    {
        OutMessage = CTIDBG_new OUTMESS(*OutTemplate);
    }
    else
    {
        OutMessage = CTIDBG_new OUTMESS;
    }

    if(OutMessage != NULL)
    {
        propagateRequest(OutMessage, pReq);

        if((status = checkForInhibitedDevice(retList, OutMessage)) != DEVICEINHIBITED)
        {
            /*
             *  Now that the OutMessageTemplate is primed, we should send it out to the specific device..
             *
             *  Call the subclasses' member function.
             *  NOTE:  Observe the conversion of parameters... This will probably bite you later
             */

            if(parse.getCommand() == ControlRequest && getControlInhibit() )
            {
                status = ControlInhibitedOnDevice;

                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                             string(OutMessage->Request.CommandStr),
                                                             getName() + string(": ") + FormatError(status),
                                                             status,
                                                             OutMessage->Request.RouteID,
                                                             OutMessage->Request.MacroOffset,
                                                             OutMessage->Request.Attempt,
                                                             OutMessage->Request.GrpMsgID,
                                                             OutMessage->Request.UserID,
                                                             OutMessage->Request.SOE,
                                                             CtiMultiMsg_vec());

                retList.push_back( pRet );
            }
            else
            {
                status = ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList );
            }
        }

        if(OutMessage != NULL)
        {
            delete OutMessage;
        }
    }
    else
    {
        status = MemoryError;
    }

    return( status );
}

void CtiDeviceBase::propagateRequest(OUTMESS *pOM, CtiRequestMsg *pReq )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(pOM != NULL && pReq != NULL)
    {
        /* Copy over some of the standard stuff. */
        pOM->DeviceID              = getID();
        pOM->TargetID              = getID();
        EstablishOutMessagePriority( pOM, pReq->getMessagePriority() );

        /* Fill out the PIL_ECHO structure elements */
        strncpy(pOM->Request.CommandStr, pReq->CommandString().c_str(), sizeof(pOM->Request.CommandStr) - 1);

        pOM->Request.Connection    = pReq->getConnectionHandle();
        pOM->Request.RouteID       = pReq->RouteId();               // This is the current route being done.
        pOM->Request.MacroOffset   = pReq->MacroOffset();
        pOM->Request.Attempt       = pReq->AttemptNum();
        pOM->Request.GrpMsgID         = pReq->GroupMessageId();
        pOM->Request.UserID        = pReq->UserMessageId();
        pOM->Request.SOE           = pReq->getSOE();

        pOM->Request.CheckSum      = getUniqueIdentifier();
    }

    return;
}


//  override if the device needs to do anything immediately on Porter startup
void CtiDeviceBase::deviceInitialization(list< CtiRequestMsg * > &request_list)
{
}


void CtiDeviceBase::getDevicePoints(vector<CtiPointSPtr> &points) const
{
    if( _pointMgr )
    {
        _pointMgr->getEqualByPAO(getID(), points);
    }
}


CtiPointSPtr CtiDeviceBase::getDevicePointEqual(INT pointID)
{
    CtiPointSPtr pPoint;

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getPoint(pointID, getID());
    }

    return pPoint;
}

CtiPointSPtr CtiDeviceBase::getDevicePointEqualByName(string pname)
{
    CtiPointSPtr pPoint;

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getEqualByName( getID(), pname );
    }

    return pPoint;
}

CtiPointSPtr CtiDeviceBase::getDeviceControlPointOffsetEqual(INT offset)
{
    CtiPointSPtr pPoint;

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getControlOffsetEqual( getID(), offset );
    }

    return pPoint;
}

CtiPointSPtr CtiDeviceBase::getDevicePointOffsetTypeEqual(INT offset, CtiPointType_t type)
{
    CtiPointSPtr pPoint;

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getOffsetTypeEqual( getID(), offset, type );
    }

    return pPoint;
}


INT CtiDeviceBase::ExecuteRequest(CtiRequestMsg *pReq,
                                  CtiCommandParser &parse,
                                  OUTMESS *&tempOut,
                                  CtiMessageList &vgList,
                                  CtiMessageList &retList,
                                  OutMessageList &outList)
{
    string resultString;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "in dev_base ExecuteRequest" << endl;
    }

    resultString = getName() + " has no type specific ExecuteRequest Method";

    CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                 string(tempOut->Request.CommandStr),
                                                 resultString,
                                                 NoExecuteRequestMethod,
                                                 tempOut->Request.RouteID,
                                                 tempOut->Request.MacroOffset,
                                                 tempOut->Request.Attempt,
                                                 tempOut->Request.GrpMsgID,
                                                 tempOut->Request.UserID,
                                                 tempOut->Request.SOE,
                                                 CtiMultiMsg_vec());
    if( pRet != NULL )
    {
        retList.push_back( pRet );
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return NoExecuteRequestMethod;
}


// Porter side info to retrieve transmitter device bookkeeping!
CtiTransmitterInfo* CtiDeviceBase::getTrxInfo()
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "   ERROR!  Device type " << getType() << " " << getName() << " has no TrxInfo object" << endl;
    }

    return NULL;
}

bool CtiDeviceBase::hasTrxInfo() const
{
    return false;
}

CtiTransmitterInfo* CtiDeviceBase::initTrxInfo() // Porter side info to setup transmitter device bookkeeping!
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "   ERROR!  Device type " << getType() << " " << getName() << " has no TrxInfo object" << endl;
    }

    return NULL;
}

string CtiDeviceBase::getPutConfigAssignment(UINT modifier)
{
    return  string("config not done ") + getName();
}

INT CtiDeviceBase::executeScan(CtiRequestMsg *pReq,
                               CtiCommandParser &parse,
                               OUTMESS *&OutMessage,
                               CtiMessageList &vgList,
                               CtiMessageList &retList,
                               OutMessageList &outList)
{
    INT   nRet = NoError;

    INT function;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
    case ScanRateStatus:
    case ScanRateGeneral:
        {
            nRet = GeneralScan(pReq,parse,OutMessage,vgList,retList,outList);
            break;
        }
    case ScanRateAccum:
        {
            nRet = AccumulatorScan(pReq,parse,OutMessage,vgList,retList,outList);
            break;
        }
    case ScanRateIntegrity:
        {
            nRet = IntegrityScan(pReq,parse,OutMessage,vgList,retList,outList);
            break;
        }
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    return nRet;
}


CtiDeviceBase::CtiDeviceBase() :
_commFailCount(gDefaultCommFailCount),
_attemptCount(0),
_attemptFailCount(0),
_attemptRetryCount(0),
_attemptSuccessCount(0),
_pointMgr(NULL),
_logOnNeeded(TRUE),
_singleDevice(false),
_routeMgr(NULL),
_responsesOnTrxID(0),
_currTrxID(0)
{
}

CtiDeviceBase::CtiDeviceBase(const CtiDeviceBase& aRef) :
_commFailCount(gDefaultCommFailCount),
_attemptCount(0),
_attemptFailCount(0),
_attemptRetryCount(0),
_attemptSuccessCount(0),
_pointMgr(NULL),
_logOnNeeded(TRUE),
_singleDevice(false),
_routeMgr(NULL),
_responsesOnTrxID(0),
_currTrxID(0)
{
    *this = aRef;
}

CtiDeviceBase& CtiDeviceBase::operator=(const CtiDeviceBase& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        CtiLockGuard<CtiMutex> guard(_classMutex);

        if(_pointMgr != NULL)
        {
            delete _pointMgr;
            _pointMgr   = NULL;
        }
        _routeMgr      = aRef.getRouteManager();
        _logOnNeeded   = aRef.getLogOnNeeded();

        setCommFailCount(aRef.getCommFailCount());
        setAttemptCount(aRef.getAttemptCount());
        setAttemptFailCount(aRef.getAttemptFailCount());
        setAttemptRetryCount(aRef.getAttemptRetryCount());
        setAttemptSuccessCount(aRef.getAttemptSuccessCount());
        setResponsesOnTrxID( aRef.getResponsesOnTrxID());
        setTrxID(aRef.getCurrentTrxID());


        setDeviceBase( aRef.getDeviceBase() );
        _exclusion = aRef.exclusion();
    }
    return *this;
}

void CtiDeviceBase::purgeStaticPaoInfo()
{
    // We dont own this data, but we can clear our internal stores.
    _staticPaoInfo.clear();
}

void CtiDeviceBase::purgeDynamicPaoInfo()
{
    set<CtiTableDynamicPaoInfo>::iterator itr = _paoInfo.begin();

    if(itr == _paoInfo.end()) // Nothing to purge, let's get out of here!
        return;

    const string owner = itr->getOwnerString();

    // Purge the dynamic info from memory.
    _paoInfo.clear();

    if( owner.empty() )
    {
        return;
    }

    // Purge the dynamic info from the database.
    static const string sqlPurge = "DELETE "
                                   "FROM dynamicpaoinfo "
                                   "WHERE paobjectid = ? AND owner = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       deleter(connection, sqlPurge);

    deleter << getID()
            << owner;

    deleter.execute();
}

void CtiDeviceBase::purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::PaoInfoKeys key)
{
    std::set<CtiTableDynamicPaoInfo>::iterator itr = _paoInfo.find(CtiTableDynamicPaoInfo(getID(), key));

    if(itr == _paoInfo.end()) // Nothing to purge, let's get out of here!
        return;

    const string owner   = itr->getOwnerString();
    const string keyname = itr->getKeyString();

    // Purge the dynamic info from memory.
    _paoInfo.erase(itr);

    if( owner.empty() || keyname.empty() )
    {
        return;
    }

    // Purge the dynamic info from the database.
    static const string sqlPurge = "DELETE "
                                   "FROM dynamicpaoinfo "
                                   "WHERE paobjectid = ? AND infokey = ? AND owner = ?";

    Cti::Database::DatabaseConnection   connection;
    Cti::Database::DatabaseWriter       deleter(connection, sqlPurge);

    deleter << getID()
            << keyname
            << owner;

    deleter.execute();
}

string CtiDeviceBase::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit "
                                   "FROM YukonPAObject YP, Device DV "
                                   "WHERE YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceBase::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
    _singleDevice = resolveIsDeviceTypeSingle(getType());
    _deviceBase.DecodeDatabaseReader(rdr);

    //  Not sure if this is the proper place to do this - perhaps it should be done in the device manager
    resetDirty();
    purgeStaticPaoInfo();
}

/**
 * Process parameters from the database
 *
 * @param rdr
 */
void CtiDeviceBase::decodeParameters(Cti::RowReader &rdr)
{

}

/**
 * Any cleanup needed to prepare for new parameters.
 *
 */
void CtiDeviceBase::clearParameters()
{

}

/*
 *  Virtuals to let my inheritors play ball with me...
 *
 *  These are basically set up to allow this to FAIL if the child class doesn't redefine them.
 */


void CtiDeviceBase::DumpData()
{
    Inherited::DumpData();
    _deviceBase.DumpData();
}

INT CtiDeviceBase::ReportError(INT mess)
{
    return 0;
}

/* Properly defined by the device types themselves... */
INT CtiDeviceBase::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return NoGeneralScanMethod;
}
INT CtiDeviceBase::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return NoIntegrityScanMethod;
}
INT CtiDeviceBase::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return NoAccumulatorScanMethod;
}
INT CtiDeviceBase::LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return NoLoadProfileScanMethod;
}

INT CtiDeviceBase::ResultDecode(INMESS*, CtiTime&, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return NoResultDecodeMethod;
}

INT CtiDeviceBase::ProcessResult(INMESS*, CtiTime&, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return NoProcessResultMethod;
}


INT CtiDeviceBase::ErrorDecode(const INMESS & InMessage, const CtiTime TimeNow,  CtiMessageList &retList)
{
    return NoErrorDecodeMethod;
}


BOOL              CtiDeviceBase::getLogOnNeeded() const
{
    return _logOnNeeded;
}
CtiDeviceBase&    CtiDeviceBase::setLogOnNeeded(BOOL b)
{
    _logOnNeeded = b;
    return *this;
}


CtiDeviceBase& CtiDeviceBase::setCommFailCount(const INT i)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    if(i < 0)
        _commFailCount = INT_MAX;               // Never ever roll over into negative numbers...
    else
        _commFailCount = i;

    return  *this;
}

CtiDeviceBase& CtiDeviceBase::setAttemptCount(const INT i)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _attemptCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptFailCount(const INT i)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _attemptFailCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptRetryCount(const INT i)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _attemptRetryCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptSuccessCount(const INT i)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _attemptSuccessCount = i;
    return  *this;
}

INT CtiDeviceBase::deviceMaxCommFails() const
{
    return gDefaultCommFailCount;
}

bool CtiDeviceBase::isCommFailed() const
{
    return _commFailCount >= deviceMaxCommFails();
}

bool CtiDeviceBase::adjustCommCounts( bool &isCommFail, bool retry )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    bool bAdjust = false;
    bool bStateChange = false;
    bool success = isCommFail;      // On entry, isCommFail is (CommResult == NORMAL)

    CtiTime now;
    INT lastCommCount = _commFailCount;

    ++_attemptCount;

    if(!success)
    {
        ++_attemptFailCount;            // This attempt failed
        ++_commFailCount;               // We failed to talk to the device.
    }
    else
    {
        _commFailCount = 0;             // reset the consecutive fails, we succeeded in talking to the device...
        ++_attemptSuccessCount;
    }

    if(retry)
        ++_attemptRetryCount;

    bool badtogood = (lastCommCount >= deviceMaxCommFails() && success);                          // Just went good.
    bool goodtobad = ( (lastCommCount < deviceMaxCommFails()) && isCommFailed() && !success );    // Just went bad.

    if( goodtobad || badtogood )
    {
        bStateChange = true;        // We need to report the state change. Comm Status changed from goodtobad or badtogood.
    }

    isCommFail   = isCommFailed();  // Comm Status is _always_ based upon counts.

    if( bStateChange || now > _lastReport )
    {
        bAdjust = true;
        _lastReport = nextScheduledTimeAlignedOnRate(now, gConfigParms.getValueAsULong("COMM_FAIL_REPORT_TIME", 300));
    }

    return(bAdjust);
}

const CtiTableDeviceBase& CtiDeviceBase::getDeviceBase() const
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return _deviceBase;
}

CtiDeviceBase& CtiDeviceBase::setDeviceBase(const CtiTableDeviceBase& tbldevbase)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _deviceBase = tbldevbase;

    return *this;
}

int CtiDeviceBase::incResponsesOnTrxID(int trxid)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(trxid == getCurrentTrxID())
    {
        _responsesOnTrxID++;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Response for transmission ID " << trxid << ".  " << getName() << " is expecting " << getCurrentTrxID() << endl;
        }
    }

    return(getResponsesOnTrxID());
}

CtiDeviceBase& CtiDeviceBase::setResponsesOnTrxID(int cnt)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _responsesOnTrxID = cnt;
    return *this;
}

CtiDeviceBase& CtiDeviceBase::setTrxID(int trx)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    _currTrxID = trx;
    return *this;
}


inline INT CtiDeviceBase::processTrxID( int trx, CtiMessageList &vgList )
{
    return 0;
}

inline INT CtiDeviceBase::initTrxID( int trx, CtiCommandParser &parse, CtiMessageList &vgList )
{
    setResponsesOnTrxID(0);
    setTrxID(trx);
    return NORMAL;
}


void CtiDeviceBase::setOutMessageTrxID( UINT &omtrxid )
{
    if(!omtrxid)
        omtrxid = generateTransmissionID();             // Mark all such messages sourced by this Group Device.
    else
        setTrxID( omtrxid );

    return;
}

/* Establishes the OutMessages "owning" Load management group */
void CtiDeviceBase::setOutMessageLMGID( LONG &omlmgid )
{
    if(!omlmgid)
        omlmgid = getID();             // Mark all such messages sourced by this Group Device.

    return;
}

/* Establishes the OutMessages "owning" Target Device */
void CtiDeviceBase::setOutMessageTargetID( LONG &omtid )
{
    if(!omtid)
        omtid = getID();             // Mark all such messages sourced by this Group Device.

    return;
}


INT CtiDeviceBase::checkForInhibitedDevice(CtiMessageList &retList, const OUTMESS *OutMessage)
{
    int status = NORMAL;

    if(isInhibited())
    {
        status = DEVICEINHIBITED;

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,          // 20050922 CGP.  TargetId should be used in case the target is an MCT, not the CCU. // getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     getName() + string(": ") + FormatError(status),
                                                     status,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.MacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());

        retList.push_back( pRet );
    }

    return status;
}


bool CtiDeviceBase::isGroup() const
{
    bool bstatus = false;

    if(getClass() == PAOClassGroup)
    {
        bstatus = true;
    }

    return bstatus;
}

bool CtiDeviceBase::isTAP() const
{
    bool bret = false;

    if(getType() == TYPE_TAPTERM ||
       getType() == TYPE_WCTP ||
       getType() == TYPE_SNPP )
    {
        bret = true;
    }

    return bret;
}


//  this dynamic stuff might need to move to tbl_pao - it is dynamicpaoinfo, after all
bool CtiDeviceBase::hasDynamicInfo(PaoInfoKeys k) const
{
    return (_paoInfo.find(CtiTableDynamicPaoInfo(getID(), k)) != _paoInfo.end());
}

bool CtiDeviceBase::hasStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
{
    return (_staticPaoInfo.find(CtiTableStaticPaoInfo(getID(), k)) != _staticPaoInfo.end());
}

bool CtiDeviceBase::setStaticInfo(const CtiTableStaticPaoInfo &info)
{
    bool new_record = false;
    std::set<CtiTableStaticPaoInfo>::iterator itr;

    itr = _staticPaoInfo.find(info);

    if( itr != _staticPaoInfo.end() )
    {
        *itr = info;
    }
    else
    {
        _staticPaoInfo.insert(info);
        new_record = true;
    }

    return new_record;
}

bool CtiDeviceBase::setDynamicInfo(const CtiTableDynamicPaoInfo &info)
{
    bool new_record = false;
    std::set<CtiTableDynamicPaoInfo>::iterator itr;

    itr = _paoInfo.find(info);

    if( itr != _paoInfo.end() )
    {
        *itr = info;
    }
    else
    {
        _paoInfo.insert(info);
        new_record = true;
    }

    return new_record;
}

bool CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, double &destination) const
{
    bool success = false;

    std::set<CtiTableStaticPaoInfo>::const_iterator itr;

    if( (itr = _staticPaoInfo.find(CtiTableStaticPaoInfo(getID(), k))) != _staticPaoInfo.end() )
    {
        itr->getValue(destination);
        success = true;
    }

    return success;
}

bool CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, string &destination) const
{
    bool success = false;

    std::set<CtiTableStaticPaoInfo>::const_iterator itr;

    if( (itr = _staticPaoInfo.find(CtiTableStaticPaoInfo(getID(), k))) != _staticPaoInfo.end() )
    {
        itr->getValue(destination);
        success = true;
    }

    return success;
}

long CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
{
    std::set<CtiTableStaticPaoInfo>::const_iterator itr;

    long value = 0;
    if( (itr = _staticPaoInfo.find(CtiTableStaticPaoInfo(getID(), k))) != _staticPaoInfo.end() )
    {
        itr->getValue(value);
    }

    return value;
}



//  helper function for overloads
template <class T>
bool setInfo(set<CtiTableDynamicPaoInfo> &s, long paoid, CtiTableDynamicPaoInfo::PaoInfoKeys k, const T &value)
{
    std::pair<std::set<CtiTableDynamicPaoInfo>::iterator, bool> set_result;
    bool record_added = false;

    set_result = s.insert(CtiTableDynamicPaoInfo(paoid, k));

    set_result.first->setValue(value);

    return set_result.second;
}

bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const string &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const int &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const unsigned int &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const long &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const unsigned long &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const double &value)
{
    return setInfo(_paoInfo, getID(), k, value);
}
bool CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const CtiTime &value)
{
    return setInfo(_paoInfo, getID(), k, (unsigned long) value.seconds());
}

//  helper function for overloads
template <class T>
bool getInfo(const set<CtiTableDynamicPaoInfo> &s, long paoid, CtiTableDynamicPaoInfo::PaoInfoKeys k, T &destination)
{
    bool success = false;

    std::set<CtiTableDynamicPaoInfo>::const_iterator itr;

    if( (itr = s.find(CtiTableDynamicPaoInfo(paoid, k))) != s.end() )
    {
        itr->getValue(destination);
        success = true;
    }

    return success;
}

bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,        string &destination) const    {   return getInfo(_paoInfo, getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,           int &destination) const    {   return getInfo(_paoInfo, getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,          long &destination) const    {   return getInfo(_paoInfo, getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k, unsigned long &destination) const    {   return getInfo(_paoInfo, getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,        double &destination) const    {   return getInfo(_paoInfo, getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,       CtiTime &destination) const
{
    ctitime_t t;

    bool retval = getInfo(_paoInfo, getID(), k, (unsigned long&)t);

    destination = t;

    return retval;
}

long CtiDeviceBase::getDynamicInfo(PaoInfoKeys k) const
{
    long l = std::numeric_limits<long>::min();

    getInfo(_paoInfo, getID(), k, l);

    return l;
}

bool CtiDeviceBase::getDirtyInfo(std::vector<CtiTableDynamicPaoInfo *> &dirty_info, CtiApplication_t app_id)
{
    bool retval = false;

    if( !_paoInfo.empty() )
    {
        std::set<CtiTableDynamicPaoInfo>::iterator itr, itr_end = _paoInfo.end();

        for( itr = _paoInfo.begin(); itr != itr_end; itr++ )
        {
            if( itr->getOwnerID() == Application_Invalid )
            {
                itr->setOwner(app_id);
            }

            if( itr->isDirty() )
            {
                dirty_info.push_back(CTIDBG_new CtiTableDynamicPaoInfo(*itr));
                itr->setDirty(false);

                retval = true;
            }
        }
    }

    return retval;
}


void CtiDeviceBase::setExpectedFreeze(int freeze)
{
    return;
}


bool CtiDeviceBase::hasExclusions() const
{
    return _exclusion.hasExclusions();
}

CtiDeviceExclusion CtiDeviceBase::exclusion() const
{
    return _exclusion;
}

CtiDeviceExclusion& CtiDeviceBase::getExclusion()
{
    return _exclusion;
}

CtiDeviceBase::exclusions CtiDeviceBase::getExclusions() const
{
    return _exclusion.getExclusions();
}
void CtiDeviceBase::addExclusion(CtiTablePaoExclusion &paox)
{
    if( ciStringEqual(paox.getFunctionName(), "TimeInfo") &&
        ! paox.getCycleTime() &&
        ! paox.getCycleOffset() &&
        ! paox.getTransmitTime() &&
        ! paox.getMaxTransmitTime() )
    {
        //  empty placeholder entry left by DB editor for convenience in case you want to reenable timing
        return;
    }

    _exclusion.setId(getID());
    try
    {
        _exclusion.addExclusion(paox);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}

void CtiDeviceBase::clearExclusions()
{
    try
    {
        _exclusion.clearExclusions();
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCEPTION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return;
}


bool CtiDeviceBase::hasLongScanRate(const string &cmd) const
{
    return false;
}


/*
 *  Check if the passed id is in the exclusion list?
 */
bool CtiDeviceBase::isDeviceExcluded(long id) const
{
    bool bstatus = false;

    try
    {
        bstatus = _exclusion.isDeviceExcluded(id);
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** EXCLUSION Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return bstatus;
}

bool CtiDeviceBase::isExecuting() const
{
    return _exclusion.isExecuting();
}

void CtiDeviceBase::setExecuting(bool executing, CtiTime when)
{
    _exclusion.setExecuting(executing, when);
    return;
}

bool CtiDeviceBase::isExecutionProhibited(const CtiTime &now, LONG did)
{
    return _exclusion.isExecutionProhibited(now, did);
}

size_t CtiDeviceBase::setExecutionProhibited(unsigned long id, CtiTime& releaseTime)
{
    return _exclusion.setExecutionProhibited(id,releaseTime);
}

bool CtiDeviceBase::removeInfiniteProhibit(unsigned long id)
{
    bool r = _exclusion.removeInfiniteProhibit(id);

    if(gConfigParms.isTrue("DEBUG_EXCLUSION_PROHIBIT"))
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << getName() << " " << id << " removed. **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return r;
}

bool CtiDeviceBase::removeProhibit(unsigned long id)
{
    if(gConfigParms.isTrue("DEBUG_EXCLUSION_PROHIBIT"))
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << getName() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    return _exclusion.removeProhibit(id);
}

void CtiDeviceBase::dumpProhibits(unsigned long id)
{
    if(gConfigParms.isTrue("DEBUG_EXCLUSION_PROHIBIT"))
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << getName() << " / " << getID() << " prohibited by: " << endl;
        }
        _exclusion.dumpProhibits(id);
    }
    return;
}

bool CtiDeviceBase::getOutMessage(CtiOutMessage *&OutMessage)
{
    if(OutMessage)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        delete OutMessage;
        OutMessage = 0;
    }

    return false;
}

CtiMessage* CtiDeviceBase::rsvpToDispatch(bool clearMessage)
{
    return 0;
}

/*
 *  This method should return the expected completion time for this device.  If the device has no special behavior this
 *  method may return YUKONEOT.  This is equivalent to saying that it is executing until we say it is not executing.
 */
CtiTime CtiDeviceBase::selectCompletionTime() const
{
    CtiTime now;
    CtiTime bestTime(YUKONEOT);

    if( now < _exclusion.getExecutionGrantExpires() && bestTime > _exclusion.getExecutionGrantExpires() )
    {
        // Must Complete is in the future and less than bestTime.
        bestTime = _exclusion.getExecutionGrantExpires();
    }

    if( now < _exclusion.getTimeSlotClose() && _exclusion.getTimeSlotClose() < bestTime )
    {
        bestTime = _exclusion.getTimeSlotClose();
    }

    LONG queueTime = deviceQueueCommunicationTime();
    if(queueTime > 0 && now + (queueTime / 1000) + 1 < bestTime)
    {
        bestTime = now + ((queueTime / 1000) + 1);
    }

    // Lastly, make certain we do not allocate more than PORTER_MAX_TRANSMITTER_TIME in any one allocation
    LONG maxtime =  deviceMaxCommunicationTime();
    if(maxtime && now + maxtime < bestTime)
    {
        bestTime = now + maxtime;
    }

    return bestTime;
}

LONG CtiDeviceBase::deviceQueueCommunicationTime() const
{
    return -1;
}

LONG CtiDeviceBase::deviceMaxCommunicationTime() const
{
    return 0L;
}

inline LONG CtiDeviceBase::getMinConnectTime() const
{
    return gConfigParms.getValueAsULong("DEFAULT_MIN_CONNECT",0);
}
inline LONG CtiDeviceBase::getMaxConnectTime() const
{
    return gConfigParms.getValueAsULong("DEFAULT_MAX_CONNECT",10);
}

inline ULONG CtiDeviceBase::getUniqueIdentifier() const
{
    return getPortID();
}

void CtiDeviceBase::setDeviceConfig(Cti::Config::DeviceConfigSPtr config)
{
    CtiLockGuard<CtiMutex> guard(_configMux);
    _deviceConfig = config;
}

Cti::Config::DeviceConfigSPtr CtiDeviceBase::getDeviceConfig()
{
    CtiLockGuard<CtiMutex> guard(_configMux);
    return _deviceConfig;
}

void CtiDeviceBase::changeDeviceConfig(Cti::Config::DeviceConfigSPtr config)
{
    setDeviceConfig(config);
}

Cti::DeviceQueueInterface* CtiDeviceBase::getDeviceQueueHandler()
{
    return NULL;
}
