#include "yukon.h"



/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_status
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_status.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:48 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "pointdefs.h"
#include "tbl_pt_status.h"
#include "resolvers.h"
#include "logger.h"


void CtiTablePointStatus::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    RWDBTable tbl = db.table(getTableName() );

    selector <<
    tbl["initialstate"] <<
    tbl["controlinhibit"] <<
    tbl["controltype"] <<
    tbl["controloffset"] <<
    tbl["closetime1"] <<
    tbl["closetime2"] <<
    tbl["statezerocontrol"] <<
    tbl["stateonecontrol"] <<
    tbl["commandtimeout"];

    selector.from(tbl);

    selector.where( selector.where() && keyTable["pointid"] == tbl["pointid"]);

}

void CtiTablePointStatus::DecodeDatabaseReader(RWDBReader &rdr)
{
    RWCString rwsTemp;
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        if(getDebugLevel() & 0x0800) dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr["initialstate"]  >> _initialState;

    rdr["controlinhibit"]   >> rwsTemp;
    rwsTemp.toLower();
    _controlInhibit = ((rwsTemp == 'y') ? TRUE : FALSE);

    rdr["controltype"]   >> rwsTemp;
    _controlType = resolveControlType(rwsTemp);

    rdr["controloffset"] >> _controlOffset;
    rdr["closetime1"] >> _closeTime1;
    rdr["closetime2"] >> _closeTime2;
    rdr["statezerocontrol"] >> _stateZeroControl;
    rdr["stateonecontrol"] >> _stateOneControl;
    rdr["commandtimeout"] >> _commandTimeout;
}


CtiTablePointStatus& CtiTablePointStatus::operator=(const CtiTablePointStatus& aRef)
{
    if(this != &aRef)
    {
        _initialState     = aRef.getInitialState();
        _controlInhibit   = aRef.getControlInhibit();
        _controlType      = aRef.getControlType();
        _controlOffset    = aRef.getControlOffset();
        _closeTime1       = aRef.getCloseTime1();
        _closeTime2       = aRef.getCloseTime2();
        _stateZeroControl = aRef.getStateZeroControl();
        _stateOneControl  = aRef.getStateOneControl();
        _commandTimeout   = aRef.getCommandTimeout();
    }
    return *this;
}


LONG CtiTablePointStatus::getPointID()
{
    return _pointID;
}

INT CtiTablePointStatus::getInitialState() const
{


    return _initialState;
}

BOOL CtiTablePointStatus::getControlInhibit() const
{


    return _controlInhibit;
}

CtiControlType_t CtiTablePointStatus::getControlType()  const
{


    return _controlType;
}

INT CtiTablePointStatus::getControlOffset() const
{


    return _controlOffset;
}

INT CtiTablePointStatus::getCloseTime1() const
{


    return _closeTime1;
}

INT CtiTablePointStatus::getCloseTime2() const
{


    return _closeTime2;
}

const RWCString& CtiTablePointStatus::getStateZeroControl() const
{


    return _stateZeroControl;
}

const RWCString& CtiTablePointStatus::getStateOneControl() const
{


    return _stateOneControl;
}


//********************************************************
BOOL CtiTablePointStatus::isControlInhibited() const
{


    return getControlInhibit();
}
//********************************************************


CtiTablePointStatus& CtiTablePointStatus::setPointID( const LONG pointID )
{
    _pointID = pointID;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setInitialState(INT i)
{


    _initialState = i;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setControlInhibit(const BOOL b)
{


    _controlInhibit = b;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setControlType(CtiControlType_t t)
{


    _controlType = t;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setControlOffset(INT i)
{


    _controlOffset = i;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setCloseTime1(INT i)
{


    _closeTime1 = i;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setCloseTime2(INT i)
{


    _closeTime2 = i;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setStateZeroControl(const RWCString& zero)
{


    _stateZeroControl = zero;
    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setStateOneControl(const RWCString& one)
{


    _stateOneControl = one;
    return *this;
}


void CtiTablePointStatus::dump() const
{
    CtiLockGuard<CtiLogger> logger_guard(dout);

    dout << " Initial State                            : " << _initialState << endl;
    dout << " Control Inhibit                          : " << _controlInhibit << endl;
    dout << " Control Type                             : " << _controlType << endl;
    dout << " Control Offset                           : " << _controlOffset << endl;
    dout << " Close Time 1                             : " << _closeTime1 << endl;
    dout << " Close Time 2                             : " << _closeTime2 << endl;
    dout << " State Zero Command String                : " << _stateZeroControl << endl;
    dout << " State One Command String                 : " << _stateOneControl << endl;
    dout << " Command Timeout on Control               : " << _commandTimeout << endl;
}

UINT CtiTablePointStatus::adjustStaticTags(UINT &tag) const
{


    if(NoneControlType < getControlType()  && getControlType() < InvalidControlType)
    {
        tag |= TAG_ATTRIB_CONTROL_AVAILABLE;
    }
    else
    {
        tag &= ~TAG_ATTRIB_CONTROL_AVAILABLE;
    }

    if(isControlInhibited())
        tag |= TAG_DISABLE_CONTROL_BY_POINT;
    else
        tag &= ~TAG_DISABLE_CONTROL_BY_POINT;


    return tag;
}

UINT CtiTablePointStatus::getStaticTags() const
{
    UINT tag = 0;



    if(NoneControlType < getControlType()  && getControlType() < InvalidControlType)
    {
        tag |= TAG_ATTRIB_CONTROL_AVAILABLE;
    }

    if(isControlInhibited()) tag |= TAG_DISABLE_CONTROL_BY_POINT;

    return tag;
}

CtiTablePointStatus::CtiTablePointStatus() :
_controlInhibit(TRUE)
{}

CtiTablePointStatus::CtiTablePointStatus(const CtiTablePointStatus& aRef)
{
    *this = aRef;
}

CtiTablePointStatus::~CtiTablePointStatus()
{
}

RWCString CtiTablePointStatus::getTableName()
{
    return "PointStatus";
}

#if 0
void CtiTablePointStatus::Update()
{


    if(tag & TAG_ATTRIB_CONTROL_AVAILABLE)
    {
        CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
        RWDBConnection conn = getConnection();

        RWDBTable table = getDatabase().table( getTableName() );
        RWDBUpdater updater = table.updater();

        updater.where( table["pointid"] == getPointID() );
        updater << table["controlinhibit"].assign( (tag & TAG_DISABLE_CONTROL_BY_POINT) ? RWCString("Y") : RWCString("N") );

        if( updater.execute( conn ).status().errorCode() == RWDBStatus::ok)
        {
            setDirty(false);
        }
    }

    return;
}
#endif

INT CtiTablePointStatus::getCommandTimeout() const
{
    return _commandTimeout;
}

CtiTablePointStatus& CtiTablePointStatus::setCommandTimeout(INT i)
{

    _commandTimeout = i;
    return *this;
}


