
#pragma warning ( disable : 4786 )
/*-----------------------------------------------------------------------------*
*
* File:   dev_base
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_base.cpp-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:59:58 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


#include <rw/tpslist.h>

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

CtiDeviceBase::~CtiDeviceBase()
{
    LockGuard  guard(monitor());

    if(_pointMgr != NULL)
    {
        delete _pointMgr;
    }
}


CtiRoute* CtiDeviceBase::getRoute(LONG RteId) const
{
    CtiRoute *Rte = NULL;

    LockGuard  guard(monitor());

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


INT CtiDeviceBase::ExecuteRequest(CtiRequestMsg                *pReq,
                                  RWTPtrSlist< CtiMessage >    &vgList,
                                  RWTPtrSlist< CtiMessage >    &retList,
                                  RWTPtrSlist< OUTMESS >       &outList,
                                  const OUTMESS                *OutTemplate)
{
    INT      status = NORMAL;
    LONG     Id;

    LockGuard  guard(monitor());

    OUTMESS  *OutMessageTemplate = NULL;   // This memory MUST be cleaned up after the NEXUS WRITE!

    if(OutTemplate != NULL)
    {
        OutMessageTemplate = new OUTMESS(*OutTemplate);
    }
    else
    {
        OutMessageTemplate = new OUTMESS;
    }

    if(OutMessageTemplate != NULL)
    {
        propagateRequest(OutMessageTemplate, pReq);


        if(isInhibited())
        {
            status = DEVICEINHIBITED;

            CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                                  RWCString(OutMessageTemplate->Request.CommandStr),
                                                  getName() + RWCString(": ") + FormatError(status),
                                                  status,
                                                  OutMessageTemplate->Request.RouteID,
                                                  OutMessageTemplate->Request.MacroOffset,
                                                  OutMessageTemplate->Request.Attempt,
                                                  OutMessageTemplate->Request.TrxID,
                                                  OutMessageTemplate->Request.UserID,
                                                  OutMessageTemplate->Request.SOE,
                                                  RWOrdered());

            retList.insert( pRet );
        }
        else
        {
            CtiCommandParser parse(pReq->CommandString());     // Create a parser reference to be passed through the operation

            /*
             *  Now that the OutMessageTemplate is primed, we should send it out to the specific device..
             *
             *  Call the subclasses' member function.
             *  NOTE:  Observe the conversion of parameters... This will probably bite you later
             */

            if(parse.getCommand() == ControlRequest && getControlInhibit() )
            {
                status = ControlInhibitedOnDevice;

                CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                                      RWCString(OutMessageTemplate->Request.CommandStr),
                                                      getName() + RWCString(": ") + FormatError(status),
                                                      status,
                                                      OutMessageTemplate->Request.RouteID,
                                                      OutMessageTemplate->Request.MacroOffset,
                                                      OutMessageTemplate->Request.Attempt,
                                                      OutMessageTemplate->Request.TrxID,
                                                      OutMessageTemplate->Request.UserID,
                                                      OutMessageTemplate->Request.SOE,
                                                      RWOrdered());

                retList.insert( pRet );
            }
            else
            {
                status = ExecuteRequest(pReq, parse, OutMessageTemplate, vgList, retList, outList );
            }
        }

        if(OutMessageTemplate != NULL)
        {
            delete OutMessageTemplate;
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
    LockGuard  guard(monitor());

    if(pOM != NULL && pReq != NULL)
    {
        /* Copy over some of the standard stuff. */
        pOM->DeviceID              = getID();
        pOM->TargetID              = getID();
        pOM->RouteID               = pReq->RouteId();
        pOM->Priority              = pReq->getMessagePriority();

        /* Fill out the PIL_ECHO structure elements */
        strncpy(pOM->Request.CommandStr, pReq->CommandString(), sizeof(pOM->Request.CommandStr) - 1);

        pOM->Request.Connection    = pReq->getConnectionHandle();
        pOM->Request.RouteID       = pReq->RouteId();               // This is the current route being done.
        pOM->Request.MacroOffset   = pReq->MacroOffset();
        pOM->Request.Attempt       = pReq->AttemptNum();
        pOM->Request.TrxID         = pReq->TransmissionId();
        pOM->Request.UserID        = pReq->UserMessageId();
        pOM->Request.SOE           = pReq->getSOE();

        pOM->Request.CheckSum      = getPhoneNumberCRC();
    }

    return;
}


INT CtiDeviceBase::ResetDevicePoints()
{
    LockGuard guard(monitor());

    if(_pointMgr != NULL)
    {
        delete _pointMgr;
        _pointMgr = NULL;
    }

    return NORMAL;
}

INT CtiDeviceBase::RefreshDevicePoints()
{
   INT status = NORMAL;

   LockGuard guard(monitor());

   if(_pointMgr == NULL)
   {
      _pointMgr = new CtiPointManager();
   }

   if(_pointMgr != NULL)
   {
      _pointMgr->RefreshList( getID() );
   }
   else
   {
      status = MEMORY;
   }

   return status;
}

CtiPointBase* CtiDeviceBase::getDevicePointEqual(INT id)
{
    CtiPoint *pPoint = NULL;

    LockGuard guard(monitor());

    if(_pointMgr == NULL)
    {
        RefreshDevicePoints();
    }

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getEqual( id );
    }

    return pPoint;
}

CtiPointBase* CtiDeviceBase::getDevicePointEqualByName(RWCString pname)
{
    CtiPoint *pPoint = NULL;

    LockGuard guard(monitor());

    if(_pointMgr == NULL)
    {
        RefreshDevicePoints();
    }

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getEqualByName( getID(), pname );
    }

    return pPoint;
}

CtiPointBase* CtiDeviceBase::getDeviceControlPointOffsetEqual(INT offset)
{
    CtiPoint *pPoint = NULL;

    LockGuard guard(monitor());

    if(_pointMgr == NULL)
    {
        RefreshDevicePoints();
    }

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getControlOffsetEqual( getID(), offset );
    }

    return pPoint;
}

CtiPointBase* CtiDeviceBase::getDevicePointOffsetTypeEqual(INT offset, INT type)
{
    CtiPoint *pPoint = NULL;

    LockGuard guard(monitor());

    if(_pointMgr == NULL)
    {
        RefreshDevicePoints();
    }

    if(_pointMgr != NULL)
    {
        pPoint = _pointMgr->getOffsetTypeEqual( getID(), offset, type );
    }

    return pPoint;
}

/*
 *  Loads the points on this device if the device knows how.  returns false if all is good, or the
 *  points are already loaded..
 */
bool CtiDeviceBase::loadDevicePoints()
{
    LockGuard guard(monitor());

    bool bstat = false;

    if(_pointMgr == NULL)
    {
        bstat = RefreshDevicePoints() != NORMAL;
    }

    return bstat;
}


INT CtiDeviceBase::ExecuteRequest(CtiRequestMsg                  *pReq,
                                  CtiCommandParser               &parse,
                                  OUTMESS                        *&tempOut,
                                  RWTPtrSlist< CtiMessage >      &vgList,
                                  RWTPtrSlist< CtiMessage >      &retList,
                                  RWTPtrSlist< OUTMESS >         &outList)
{
    RWCString resultString;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "in dev_base ExecuteRequest" << endl;
    }

    resultString = getName() + " has no type specific ExecuteRequest Method";

    CtiReturnMsg* pRet = new CtiReturnMsg(getID(),
                                          RWCString(tempOut->Request.CommandStr),
                                          resultString,
                                          NoExecuteRequestMethod,
                                          tempOut->Request.RouteID,
                                          tempOut->Request.MacroOffset,
                                          tempOut->Request.Attempt,
                                          tempOut->Request.TrxID,
                                          tempOut->Request.UserID,
                                          tempOut->Request.SOE,
                                          RWOrdered());
    if( pRet != NULL )
    {
        retList.insert( pRet );
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }

    return NoExecuteRequestMethod;
}


// Porter side info to retrieve transmitter device bookkeeping!
CtiTransmitterInfo* CtiDeviceBase::getTrxInfo() const
{
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        dout << "   ERROR!  Device type " << getType() << " " << getName() << " has no TrxInfo object" << endl;
    }

    return NULL;
}

RWCString CtiDeviceBase::getPutConfigAssignment(UINT level)
{
    return  RWCString("config not done ") + getName();
}

INT CtiDeviceBase::executeScan(CtiRequestMsg                  *pReq,
                               CtiCommandParser               &parse,
                               OUTMESS                        *&OutMessage,
                               RWTPtrSlist< CtiMessage >      &vgList,
                               RWTPtrSlist< CtiMessage >      &retList,
                               RWTPtrSlist< OUTMESS >         &outList)
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
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            break;
        }
    }

    return nRet;
}

CtiDeviceBase::CtiDeviceBase() :
_commFailCount(COMM_FAIL_COUNT),
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
{}

CtiDeviceBase::CtiDeviceBase(const CtiDeviceBase& aRef) :
_commFailCount(COMM_FAIL_COUNT),
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

        LockGuard  guard(monitor());

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
    }
    return *this;
}

void CtiDeviceBase::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableDeviceBase::getSQL(db, keyTable, selector);
}
void CtiDeviceBase::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
    _singleDevice = resolveIsDeviceTypeSingle(getType());
    _deviceBase.DecodeDatabaseReader(rdr);
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

INT CtiDeviceBase::ReportError(INT mess) { return 0;}

/* Properly defined by the device types themselves... */
INT CtiDeviceBase::GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)      { return NoGeneralScanMethod; }
INT CtiDeviceBase::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)    { return NoIntegrityScanMethod; }
INT CtiDeviceBase::AccumulatorScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)  { return NoAccumulatorScanMethod; }
INT CtiDeviceBase::LoadProfileScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&pOM, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList, INT ScanPriority)  { return NoLoadProfileScanMethod; }

INT CtiDeviceBase::ResultDecode(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    return NoResultDecodeMethod;
}

INT CtiDeviceBase::ProcessResult(INMESS*, RWTime&, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    return NoProcessResultMethod;
}


INT CtiDeviceBase::ErrorDecode(INMESS*, RWTime&,  RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist<OUTMESS> &outList)
{
    return NoErrorDecodeMethod;
}


BOOL              CtiDeviceBase::getLogOnNeeded() const                          { return _logOnNeeded;}
CtiDeviceBase&    CtiDeviceBase::setLogOnNeeded(BOOL b)
{
    _logOnNeeded = b;
    return *this;
}


CtiDeviceBase& CtiDeviceBase::setCommFailCount(const INT i)
{
    LockGuard guard(monitor());
    if(i < 0)
        _commFailCount = INT_MAX;               // Never ever roll over into negative numbers...
    else
        _commFailCount = i;

    return  *this;
}

CtiDeviceBase& CtiDeviceBase::setAttemptCount(const INT i)
{
    LockGuard guard(monitor());
    _attemptCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptFailCount(const INT i)
{
    LockGuard guard(monitor());
    _attemptFailCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptRetryCount(const INT i)
{
    LockGuard guard(monitor());
    _attemptRetryCount = i;
    return  *this;
}
CtiDeviceBase& CtiDeviceBase::setAttemptSuccessCount(const INT i)
{
    LockGuard guard(monitor());
    _attemptSuccessCount = i;
    return  *this;
}

INT CtiDeviceBase::deviceMaxCommFails() const
{
    return COMM_FAIL_COUNT;
}

#define COMM_FAIL_REPORT_TIME 300

bool CtiDeviceBase::adjustCommCounts( bool &isCommFail, bool retry )
{
    LockGuard  guard(monitor());
    bool bAdjust = false;
    bool bStateChange = false;
    bool success = isCommFail;      // On entry, isCommFail is (CommResult == NORMAL)

    RWTime now;
    INT lastCommCount = _commFailCount;

    ++_attemptCount;

    if(!success)
    {
        ++_attemptFailCount;
        ++_commFailCount;
    }
    else
    {
        _commFailCount = 0;             // reset the consecutive fails.
        ++_attemptSuccessCount;
    }

    if(retry)
        ++_attemptRetryCount;

    bool badtogood = (lastCommCount >= deviceMaxCommFails() && success);
    bool goodtobad = ( (lastCommCount < deviceMaxCommFails()) && (_commFailCount >= deviceMaxCommFails()) && !success );

    if( goodtobad )
    {
        bStateChange = true;
        isCommFail   = true;       // Comm Status is BAD NOW.
    }
    else if( badtogood )
    {
        bStateChange = true;
        isCommFail   = false;       // Comm Status is GOOD NOW.
    }
    else
    {
        isCommFail   = (_commFailCount >= deviceMaxCommFails());       // Comm Status is based upon counts.
    }

    if( bStateChange || now > _lastReport )
    {
        bAdjust = true;
        _lastReport = ((now - (now.seconds() % COMM_FAIL_REPORT_TIME)) + COMM_FAIL_REPORT_TIME);
    }

    return(bAdjust);
}

const CtiTableDeviceBase& CtiDeviceBase::getDeviceBase() const
{
    LockGuard gd(monitor());
    return _deviceBase;
}

CtiDeviceBase& CtiDeviceBase::setDeviceBase(const CtiTableDeviceBase& tbldevbase)
{
    LockGuard gd(monitor());
    _deviceBase = tbldevbase;

    return *this;
}

int CtiDeviceBase::incResponsesOnTrxID(int trxid)
{
    LockGuard gd(monitor());

    if(trxid == getCurrentTrxID())
    {
        _responsesOnTrxID++;
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Response for transmission ID " << trxid << ".  " << getName() << " is expecting " << getCurrentTrxID() << endl;
        }
    }

    return(getResponsesOnTrxID());
}

CtiDeviceBase& CtiDeviceBase::setResponsesOnTrxID(int cnt)
{
    LockGuard gd(monitor());
    _responsesOnTrxID = cnt;
    return *this;
}

CtiDeviceBase& CtiDeviceBase::setTrxID(int trx)
{
    LockGuard gd(monitor());
    _currTrxID = trx;
    return *this;
}


inline INT CtiDeviceBase::processTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList )
{
    return 0;
}

inline INT CtiDeviceBase::initTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList )
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


