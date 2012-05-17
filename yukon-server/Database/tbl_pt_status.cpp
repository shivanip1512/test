#include "precompiled.h"

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


BOOL CtiTablePointStatus::isControlInhibited() const
{
    return getControlInhibit();
}


CtiTablePointStatus& CtiTablePointStatus::setControlType(CtiControlType_t t)
{
    _controlType = t;
}

CtiTablePointStatus& CtiTablePointStatus::setControlOffset(INT i)
{
    _controlOffset = i;
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


