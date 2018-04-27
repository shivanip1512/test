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
#include "database_util.h"
#include "mgr_config.h"
#include "mgr_dyn_paoinfo.h"

using namespace std;

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
        Rte = _routeMgr->getRouteById(RteId);
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

YukonError_t CtiDeviceBase::invokeDeviceHandler(Cti::Devices::DeviceHandler &handler)
{
    return handler.execute(*this);
}


YukonError_t CtiDeviceBase::beginExecuteRequest(CtiRequestMsg *pReq,
                                                CtiCommandParser &parse,
                                                CtiMessageList &vgList,
                                                CtiMessageList &retList,
                                                OutMessageList &outList)
{
    return beginExecuteRequestFromTemplate(pReq, parse, vgList, retList, outList, 0);
}

bool CtiDeviceBase::executeBackgroundRequest(const std::string &commandString, const OUTMESS &OutMessageTemplate, OutMessageList &outList)
{
    CtiMessageList unused;

    CtiRequestMsg req(getID(), commandString);

    req.setMessagePriority(OutMessageTemplate.Priority);

    CtiCommandParser parse(req.CommandString());

    return beginExecuteRequestFromTemplate(&req, parse, unused, unused, outList, &OutMessageTemplate) == ClientErrors::None;
}

bool CtiDeviceBase::executeAuxiliaryRequest(const std::string &commandString, const CtiRequestMsg &originalRequest, OutMessageList &outList, CtiMessageList &retList)
{
    CtiMessageList unused;
    OutMessageList tmpOutList;

    CtiRequestMsg req(originalRequest);

    //  ConnectionHandle isn't copied by the copy constructor
    req.setConnectionHandle(originalRequest.getConnectionHandle());
    req.setCommandString(commandString);

    CtiCommandParser parse(req.CommandString());

    if( beginExecuteRequest(&req, parse, unused, retList, tmpOutList) != ClientErrors::None )
    {
        return false;
    }

    if( tmpOutList.size() != 1 )
    {
        Cti::FormattedList l;

        l.add("Message count") << tmpOutList.size();
        l.add("Command string") << commandString;

        CTILOG_WARN(dout, "Auxiliary request failed" << l);

        return false;
    }

    //  Ideally, this would set a flag in PIL_ECHO that could be checked by resumeAfterAuxiliaryRequest(), but I'm hesitant to add things to PIL_ECHO.

    //  Overwrite the command string so the request can be resumed by resumeAfterAuxiliaryRequest
    strcpy_s(tmpOutList.front()->Request.CommandStr, originalRequest.CommandString().c_str());

    outList.push_back(tmpOutList.front());

    return true;
}

void CtiDeviceBase::resumeAfterAuxiliaryRequest(const INMESS &InMessage, OutMessageList &outList, CtiMessageList &retList, CtiMessageList &vgList)
{
    CtiRequestMsg newReq(getID(),
        InMessage.Return.CommandStr,
        InMessage.Return.UserID,
        InMessage.Return.GrpMsgID,
        getRouteID(),
        selectInitialMacroRouteOffset(getRouteID()),  //  this bypasses PIL, so we need to calculate this
        0,
        0,
        InMessage.Priority);

    newReq.setConnectionHandle(InMessage.Return.Connection);

    beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
}

YukonError_t CtiDeviceBase::beginExecuteRequestFromTemplate(CtiRequestMsg *pReq,
                                                            CtiCommandParser &parse,
                                                            CtiMessageList &vgList,
                                                            CtiMessageList &retList,
                                                            OutMessageList &outList,
                                                            const OUTMESS *OutTemplate)
{
    YukonError_t status = ClientErrors::None;

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

        if((status = checkForInhibitedDevice(retList, OutMessage)) != ClientErrors::DeviceInhibited)
        {
            /*
             *  Now that the OutMessageTemplate is primed, we should send it out to the specific device..
             *
             *  Call the subclasses' member function.
             *  NOTE:  Observe the conversion of parameters... This will probably bite you later
             */

            if(parse.getCommand() == ControlRequest && getControlInhibit() )
            {
                status = ClientErrors::ControlInhibitedOnDevice;

                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                             string(OutMessage->Request.CommandStr),
                                                             getName() + string(": ") + CtiError::GetErrorString(status),
                                                             status,
                                                             OutMessage->Request.RouteID,
                                                             OutMessage->Request.RetryMacroOffset,
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
        status = ClientErrors::Memory;
    }

    return( status );
}

void CtiDeviceBase::propagateRequest(OUTMESS *pOM, CtiRequestMsg *pReq )
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(pOM != NULL && pReq != NULL)
    {
        /* Copy over some of the standard stuff. */
        pOM->DeviceID                   = getID();
        pOM->TargetID                   = getID();
        EstablishOutMessagePriority( pOM, pReq->getMessagePriority() );

        /* Fill out the PIL_ECHO structure elements */
        strncpy(pOM->Request.CommandStr, pReq->CommandString().c_str(), sizeof(pOM->Request.CommandStr) - 1);

        pOM->Request.Connection         = pReq->getConnectionHandle();
        pOM->Request.RouteID            = pReq->RouteId();               // This is the current route being done.
        pOM->Request.RetryMacroOffset   = pReq->MacroOffset();
        pOM->Request.Attempt            = pReq->AttemptNum();
        pOM->Request.GrpMsgID           = pReq->GroupMessageId();
        pOM->Request.UserID             = pReq->UserMessageId();
        pOM->Request.SOE                = pReq->getSOE();

        pOM->Request.CheckSum           = getUniqueIdentifier();
    }

    return;
}


void CtiDeviceBase::getDevicePoints(vector<CtiPointSPtr> &points) const
{
    if( _pointMgr )
    {
        _pointMgr->getEqualByPAO(getID(), points);
    }
}


CtiPointSPtr CtiDeviceBase::getDevicePointByID(INT pointID)
{
    CtiPointSPtr pPoint;

    if(_pointMgr != NULL)
    {
        CtiPointSPtr point = _pointMgr->getPoint(pointID, getID());

        if (point && point->getDeviceID() == getID())
        {
            pPoint = point;
        }
    }

    return pPoint;
}

CtiPointSPtr CtiDeviceBase::getDevicePointByName(const string& pname)
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

CtiPointSPtr CtiDeviceBase::getDeviceAnalogOutputPoint(INT offset)
{
    if( _pointMgr )
    {
        return _pointMgr->getAnalogOutput( getID(), offset );
    }

    return nullptr;
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

boost::optional<long> CtiDeviceBase::getPointIdForOffsetAndType(int offset, CtiPointType_t type)
{
    if( _pointMgr )
    {
        return _pointMgr->getIdForOffsetAndType( getID(), offset, type );
    }

    return boost::none;
}


YukonError_t CtiDeviceBase::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&tempOut, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    CTILOG_TRACE(dout, "in dev_base ExecuteRequest");
    string resultString = getName() + " has no type specific ExecuteRequest Method";

    CtiReturnMsg* pRet =
        new CtiReturnMsg(
                getID(),
                string(tempOut->Request.CommandStr),
                resultString,
                ClientErrors::NoMethodForExecuteRequest,
                tempOut->Request.RouteID,
                tempOut->Request.RetryMacroOffset,
                tempOut->Request.Attempt,
                tempOut->Request.GrpMsgID,
                tempOut->Request.UserID,
                tempOut->Request.SOE,
                CtiMultiMsg_vec());

    retList.push_back( pRet );

    return ClientErrors::NoMethodForExecuteRequest;
}


// Porter side info to retrieve transmitter device bookkeeping!
CtiTransmitterInfo* CtiDeviceBase::getTrxInfo()
{
    CTILOG_ERROR(dout, "Device type "<< getType() <<" "<< getName() <<" has no TrxInfo object");

    return NULL;
}

bool CtiDeviceBase::hasTrxInfo() const
{
    return false;
}

CtiTransmitterInfo* CtiDeviceBase::initTrxInfo() // Porter side info to setup transmitter device bookkeeping!
{
    CTILOG_ERROR(dout, "Device type "<< getType() <<" "<< getName() <<" has no TrxInfo object");

    return NULL;
}

string CtiDeviceBase::getPutConfigAssignment(UINT modifier)
{
    return  string("config not done ") + getName();
}

YukonError_t CtiDeviceBase::executeScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    switch(parse.getiValue("scantype"))
    {
    case ScanRateStatus:
    case ScanRateGeneral:
        {
            return GeneralScan(pReq,parse,OutMessage,vgList,retList,outList);
        }
    case ScanRateAccum:
        {
            return AccumulatorScan(pReq,parse,OutMessage,vgList,retList,outList);
        }
    case ScanRateIntegrity:
        {
            return IntegrityScan(pReq,parse,OutMessage,vgList,retList,outList);
        }
    default:
        {
            CTILOG_ERROR(dout, "Invalid scan type \""<< parse.getiValue("scantype") <<"\" for device \""<< getName() <<"\"");

            return ClientErrors::NoMethod;
        }
    }
}


CtiDeviceBase::CtiDeviceBase() :
_commFailCount(gDefaultCommFailCount),
_attemptCount(0),
_attemptFailCount(0),
_attemptRetryCount(0),
_attemptSuccessCount(0),
_pointMgr(NULL),
_logOnNeeded(true),
_singleDevice(false),
_routeMgr(NULL),
_responsesOnTrxID(0),
_currTrxID(0)
{
}

void CtiDeviceBase::populateOutMessage(OUTMESS &OutMessage)
{
    OutMessage.DeviceID = getID();
    OutMessage.Port     = getPortID();
    OutMessage.Remote   = getAddress();

    OutMessage.TimeOut   = 2;
}

YukonError_t CtiDeviceBase::insertReturnMsg(YukonError_t retval, OUTMESS *&om, CtiMessageList &retList, const string &error) const
{
    retList.push_back(
        new CtiReturnMsg(
            getID(),
            om->Request,
            getName() + " / " + error,
            retval));

    delete om;
    om = NULL;

    return retval;
}

void CtiDeviceBase::purgeStaticPaoInfo()
{
    // We dont own this data, but we can clear our internal stores.
    _staticPaoInfo.clear();
}

void CtiDeviceBase::purgeDynamicPaoInfo()
{
    Cti::DynamicPaoInfoManager::purgeInfo(getID());
}

void CtiDeviceBase::purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::PaoInfoKeys key)
{
    Cti::DynamicPaoInfoManager::purgeInfo(getID(), key);
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

void CtiDeviceBase::setPaoType(const std::string& category, const std::string& type)
{
    const auto deviceType = resolveDeviceType(type);

    setDeviceType(deviceType);
}

void CtiDeviceBase::setDeviceType(const DeviceTypes type)
{
    _deviceType = type;

    setType(_deviceType);
}

DeviceTypes CtiDeviceBase::getDeviceType() const
{
    return _deviceType;
}

/*
 *  Virtuals to let my inheritors play ball with me...
 *
 *  These are basically set up to allow this to FAIL if the child class doesn't redefine them.
 */


std::string CtiDeviceBase::toString() const
{
    Cti::FormattedList itemList;

    itemList<<"CtiDeviceBase";
    itemList<< _deviceBase; // is a CtiTableDeviceBase

    return (Inherited::toString() += itemList.toString());
}

INT CtiDeviceBase::ReportError(INT mess)
{
    return 0;
}

/* Properly defined by the device types themselves... */
YukonError_t CtiDeviceBase::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return ClientErrors::NoMethodForGeneralScan;
}
YukonError_t CtiDeviceBase::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return ClientErrors::NoMethodForIntegrityScan;
}
YukonError_t CtiDeviceBase::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return ClientErrors::NoMethodForAccumulatorScan;
}
YukonError_t CtiDeviceBase::LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    return ClientErrors::NoMethodForLoadProfileScan;
}

YukonError_t CtiDeviceBase::ResultDecode(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return ClientErrors::NoMethodForResultDecode;
}

YukonError_t CtiDeviceBase::ProcessResult(const INMESS&, const CtiTime, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return ClientErrors::NoMethodForProcessResult;
}


YukonError_t CtiDeviceBase::ErrorDecode(const INMESS & InMessage, const CtiTime TimeNow,  CtiMessageList &retList)
{
    return ClientErrors::NoMethodForErrorDecode;
}


bool CtiDeviceBase::getLogOnNeeded() const
{
    return _logOnNeeded;
}
void CtiDeviceBase::setLogOnNeeded(bool b)
{
    _logOnNeeded = b;
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

int CtiDeviceBase::incResponsesOnTrxID(int trxid)
{
    CtiLockGuard<CtiMutex> guard(_classMutex);

    if(trxid == getCurrentTrxID())
    {
        _responsesOnTrxID++;
    }
    else
    {
        CTILOG_ERROR(dout, "Response for transmission ID "<< trxid <<".  "<< getName() <<" is expecting "<< getCurrentTrxID());
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
    return ClientErrors::None;
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


YukonError_t CtiDeviceBase::checkForInhibitedDevice(CtiMessageList &retList, const OUTMESS *OutMessage)
{
    YukonError_t status = ClientErrors::None;

    if(isInhibited())
    {
        status = ClientErrors::DeviceInhibited;

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(OutMessage->TargetID,          // 20050922 CGP.  TargetId should be used in case the target is an MCT, not the CCU. // getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     getName() + string(": ") + CtiError::GetErrorString(status),
                                                     status,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
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
    return getClass() == PAOClassGroup;
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


bool CtiDeviceBase::hasDynamicInfo(PaoInfoKeys k) const
{
    return Cti::DynamicPaoInfoManager::hasInfo(getID(), k);
}

bool CtiDeviceBase::hasStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
{
    return _staticPaoInfo.count(k);
}

bool CtiDeviceBase::setStaticInfo(const CtiTableStaticPaoInfo &info)
{
    bool new_record = false;

    auto itr = _staticPaoInfo.find(info.getKey());

    if( itr != _staticPaoInfo.end() )
    {
        itr->second = info;
    }
    else
    {
        _staticPaoInfo.emplace(info.getKey(), info);
        new_record = true;
    }

    return new_record;
}

bool CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, double &destination) const
{
    bool success = false;

    auto itr = _staticPaoInfo.find(k);

    if( itr != _staticPaoInfo.end() )
    {
        itr->second.getValue(destination);
        success = true;
    }

    return success;
}

bool CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k, string &destination) const
{
    bool success = false;

    auto itr = _staticPaoInfo.find(k);

    if( itr != _staticPaoInfo.end() )
    {
        itr->second.getValue(destination);
        success = true;
    }

    return success;
}

long CtiDeviceBase::getStaticInfo(CtiTableStaticPaoInfo::PaoInfoKeys k) const
{
    long value = 0;

    auto itr = _staticPaoInfo.find(k);

    if( itr != _staticPaoInfo.end() )
    {
        itr->second.getValue(value);
    }

    return value;
}



void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const string &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const int &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const unsigned int &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const long &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const unsigned long &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const double &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, value);
}
void CtiDeviceBase::setDynamicInfo(PaoInfoKeys k, const CtiTime &value)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, (unsigned long) value.seconds());
}

bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,        string &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,           int &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,          long &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k, unsigned long &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,        double &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,  unsigned int &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }
bool CtiDeviceBase::getDynamicInfo(PaoInfoKeys k,       CtiTime &destination) const    {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k, destination);  }

long CtiDeviceBase::getDynamicInfo(PaoInfoKeys k) const  {  return Cti::DynamicPaoInfoManager::getInfo(getID(), k);  }


template <>
boost::optional<unsigned char> CtiDeviceBase::findDynamicInfo<unsigned char>(PaoInfoKeys k) const
{
    long val;

    if( ! getDynamicInfo(k, val) || val < 0 || val > std::numeric_limits<unsigned char>::max() )
    {
        return boost::none;
    }

    return static_cast<unsigned char>(val);
}

template <>
boost::optional<bool> CtiDeviceBase::findDynamicInfo<bool>(PaoInfoKeys k) const
{
    long val;

    if( ! getDynamicInfo(k, val) )
    {
        return boost::none;
    }

    return static_cast<bool>(val);
}

template <>
boost::optional<unsigned> CtiDeviceBase::findDynamicInfo<unsigned>(PaoInfoKeys k) const
{
    long val;

    if( ! getDynamicInfo(k, val) || val < 0 )
    {
        return boost::none;
    }

    return static_cast<unsigned>(val);
}


void CtiDeviceBase::setDynamicInfo(PaoInfoKeysIndexed k, const std::vector<unsigned long> &values)
{
    Cti::DynamicPaoInfoManager::setInfo(getID(), k, values);
}

template <typename T>
boost::optional<std::vector<T>> CtiDeviceBase::findDynamicInfo(PaoInfoKeysIndexed k) const
{
    return Cti::DynamicPaoInfoManager::getInfo<T> (getID(), k);
}

void CtiDeviceBase::setExpectedFreeze(int freeze)
{
    return;
}


bool CtiDeviceBase::hasExclusions() const
{
    return _exclusion.hasExclusions();
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
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
        CTILOG_DEBUG(dout, getName() <<" "<< id <<" removed.");
    }

    return r;
}

bool CtiDeviceBase::removeProhibit(unsigned long id)
{
    if(gConfigParms.isTrue("DEBUG_EXCLUSION_PROHIBIT"))
    {
        CTILOG_DEBUG(dout, getName() <<" removing "<< id);
    }

    return _exclusion.removeProhibit(id);
}

void CtiDeviceBase::dumpProhibits(unsigned long id)
{
    if(gConfigParms.isTrue("DEBUG_EXCLUSION_PROHIBIT"))
    {
        CTILOG_DEBUG(dout, getName() <<" / "<< getID() <<" prohibited by:");

        _exclusion.dumpProhibits(id);
    }
    return;
}

bool CtiDeviceBase::getOutMessage(CtiOutMessage *&OutMessage)
{
    if(OutMessage)
    {
        CTILOG_ERROR(dout, "function not implemented");

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

Cti::Config::DeviceConfigSPtr CtiDeviceBase::getDeviceConfig()
{
    return Cti::ConfigManager::getConfigForIdAndType( getID(), getDeviceType() );
}

Cti::DeviceQueueInterface* CtiDeviceBase::getDeviceQueueHandler()
{
    return NULL;
}
