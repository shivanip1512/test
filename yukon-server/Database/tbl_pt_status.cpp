/*-----------------------------------------------------------------------------*
*
* File:   tbl_pt_status
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DATABASE/tbl_pt_status.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2007/09/28 15:43:05 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "pointdefs.h"
#include "tbl_pt_status.h"
#include "resolvers.h"
#include "logger.h"

using std::string;
using std::endl;

static const string zeroControl = "control open";
static const string oneControl = "control close";

void CtiTablePointStatus::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    string rwsTemp;
    if(getDebugLevel() & DEBUGLEVEL_DATABASE) 
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    rdr >> _initialState;
    rdr >> rwsTemp;
    std::transform(rwsTemp.begin(), rwsTemp.end(), rwsTemp.begin(), tolower);
    _controlInhibit = ((rwsTemp == "y") ? TRUE : FALSE);

    rdr >> rwsTemp;
    _controlType = resolveControlType(rwsTemp);

    rdr >> _controlOffset;
    rdr >> _closeTime1;
    rdr >> _closeTime2;
    rdr >> rwsTemp;
    setStateZeroControl(rwsTemp);
    rdr >> rwsTemp;
    setStateOneControl(rwsTemp);
    rdr >> _commandTimeout;
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
        setStateZeroControl(aRef.getStateZeroControl());
        setStateOneControl(aRef.getStateOneControl());
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

const string& CtiTablePointStatus::getStateZeroControl() const
{
    if( _stateZeroControl != NULL )
    {
        return *_stateZeroControl;
    }
    else
    {
        return zeroControl;
    }
}

const string& CtiTablePointStatus::getStateOneControl() const
{
    if( _stateOneControl != NULL )
    {
        return *_stateOneControl;
    }
    else
    {
        return oneControl;
    }
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

CtiTablePointStatus& CtiTablePointStatus::setStateZeroControl(const string& zero)
{
    if( zero == zeroControl )
    {
        if( _stateZeroControl != NULL )
        {
            delete _stateZeroControl;
            _stateZeroControl = NULL;
        }
    }
    else if( _stateZeroControl == NULL || *_stateZeroControl != zero  )
    {
        if( _stateZeroControl != NULL )
        {
            *_stateZeroControl = zero;;
        }
        else
        {
            _stateZeroControl = CTIDBG_new string(zero);
        }
    }

    return *this;
}

CtiTablePointStatus& CtiTablePointStatus::setStateOneControl(const string& one)
{
    if( one == oneControl )
    {
        if( _stateOneControl != NULL )
        {
            delete _stateOneControl;
            _stateOneControl = NULL;
        }
    }
    else if( _stateOneControl == NULL || *_stateOneControl != one  )
    {
        if( _stateOneControl != NULL )
        {
            *_stateOneControl = one;
        }
        else 
        {
            _stateOneControl = CTIDBG_new string(one);
        }
    }

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
_controlInhibit(TRUE),
_stateZeroControl(NULL),
_stateOneControl(NULL),
_controlType(InvalidControlType),
_pointID       (0),
_initialState  (0),
_controlOffset (0),
_closeTime1    (0),
_closeTime2    (0),
_commandTimeout(0)
{}

CtiTablePointStatus::CtiTablePointStatus(const CtiTablePointStatus& aRef)
{
    *this = aRef;
}

CtiTablePointStatus::~CtiTablePointStatus()
{
    if( _stateZeroControl != NULL )
    {
        delete _stateZeroControl;
        _stateZeroControl = NULL;
    }
    if( _stateOneControl != NULL )
    {
        delete _stateOneControl;
        _stateOneControl = NULL;
    }
}

string CtiTablePointStatus::getTableName()
{
    return "PointStatus";
}

INT CtiTablePointStatus::getCommandTimeout() const
{
    return _commandTimeout;
}

CtiTablePointStatus& CtiTablePointStatus::setCommandTimeout(INT i)
{

    _commandTimeout = i;
    return *this;
}


